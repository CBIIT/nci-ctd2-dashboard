import {
    BASE_URL,
    ctd2_hovertext,
    ctd2_role_definition,
    wildcard_evidence_codes,
    class2imageData
} from './ctd2.constants.js'
import {
    leftSep,
    rightSep
} from './ctd2.constants.js'
import {
    ObservedSubjects,
    ObservedEvidences
} from './observed.js'
import {
    ECOTerm
} from './ecoterm.js'

// This is for the moustache-like templates
// prevents collisions with JSP tags <%...%>
_.templateSettings = {
    interpolate: /\{\{(.+?)\}\}/g
};

/* ObservationView */
export default Backbone.View.extend({
    el: $("#main-container"),
    template: _.template($("#observation-tmpl").html()),
    render: function() {
        const result = this.model.toJSON();
        $(this.el).html(this.template(result));

        $(this.el).find("h2 > small").popover({
            placement: "top",
            trigger: 'hover',
            content: function() {
                const hovertext_id = 'EXPLORE_' + $(this).text().toUpperCase().replace(' ', '_').replace(/\(|\)/g, '');
                return ctd2_hovertext[hovertext_id];
            },
        });

        // We will replace the values in this summary
        let summary = result.submission.observationTemplate.observationSummary;

        // Load Subjects
        const observedSubjects = new ObservedSubjects({
            observationId: result.id
        });
        const thatEl = $("#observed-subjects-grid");
        observedSubjects.fetch({
            success: function() {
                _.each(observedSubjects.models, function(observedSubject) {
                    observedSubject = observedSubject.toJSON();

                    const observedSubjectRowView = new ObservedSubjectSummaryRowView({
                        el: $(thatEl).find("tbody"),
                        model: observedSubject
                    });
                    observedSubjectRowView.render();

                    const subject = observedSubject.subject;
                    const imageData = class2imageData[subject.class];
                    imageData.stableURL = subject.stableURL;
                    const thatEl2 = $("#subject-image-" + observedSubject.id);
                    const imgTemplate = $("#search-results-image-tmpl");
                    if (subject.class == "Compound") {
                        const compound = new Subject({
                            id: subject.id
                        });
                        compound.fetch({
                            success: function() {
                                _.each(compound.toJSON().xrefs, function(xref) {
                                    if (xref.databaseName == "IMAGE") {
                                        imageData.image = $("#explore-tmpl").attr("data-url") + "compounds/" + xref.databaseId;
                                    }
                                });
                                thatEl2.append(_.template(imgTemplate.html())(imageData));
                            }
                        });
                    } else {
                        if (subject.type.toLowerCase() == "sirna") {
                            imageData.image = 'img/sirna.png';
                            imageData.label = "siRNA";
                        }
                        thatEl2.append(_.template(imgTemplate.html())(imageData));
                    }

                    if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                        return;

                    summary = summary.replace(
                        new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                        _.template($("#summary-subject-replacement-tmpl").html())(observedSubject.subject)
                    );

                    $("#observation-summary").html(summary);
                });
                $(".subject_role").popover({
                    placement: "top",
                    trigger: 'hover',
                    content: function() {
                        return ctd2_role_definition[$(this).text()];
                    },
                });
            }
        });

        const ecoTable = $("#eco-grid");
        const ecocodes = result.submission.observationTemplate.ECOCode;
        if (ecocodes.length == 0) {
            ecoTable.hide();
        } else {
            const ecos = ecocodes.split('|');
            const ecodata = [];
            ecos.forEach(function(ecocode) {
                if (ecocode == '') return;
                const ecourl = ecocode.replace(':', '-').toLowerCase();
                const eco_model = new ECOTerm({
                    id: ecourl,
                });
                eco_model.fetch({
                    async: false,
                    success: function() { // no-op 
                    },
                    error: function() {
                        console.log('ECO term not found for' + ecocode);
                    },
                });
                const econame = eco_model.toJSON().displayName;
                ecodata.push(['<a href="#eco/' + ecourl + '">' + ecocode + '</a>', econame]);
            });

            ecoTable.DataTable({
                data: ecodata,
                paging: false,
                ordering: false,
                info: false,
                searching: false,
            });
        }

        // Load evidences
        const observedEvidences = new ObservedEvidences({
            observationId: result.id
        });
        const thatEl2 = $("#observed-evidences-grid");
        observedEvidences.fetch({
            success: function() {
                let storyFilePath = "";
                _.each(observedEvidences.models, function(observedEvidence) {
                    observedEvidence = observedEvidence.toJSON();

                    const observedEvidenceRowView = new ObservedEvidenceRowView({
                        el: $(thatEl2).find("tbody"),
                        model: observedEvidence
                    });

                    observedEvidenceRowView.render();
                    summary = summary.replace(
                        new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                        _.template($("#summary-evidence-replacement-tmpl").html())(observedEvidence.evidence)
                    );

                    $("#observation-summary").html(summary);
                    if (observedEvidence.evidence.class == "FileEvidence") {
                        // observedEvidence.observedEvidenceRole.attribute is always text/html in known cases
                        if (observedEvidence.observedEvidenceRole.columnName == "story_location") {
                            storyFilePath = observedEvidence.evidence.filePath;
                        }
                    }
                });

                const tableLength = (observedEvidences.models.length > 25 ? 10 : 25);
                const oTable = $('#observed-evidences-grid').dataTable({
                    "iDisplayLength": tableLength
                });

                oTable.fnSort([
                    [1, 'asc'],
                    [2, 'asc']
                ]);

                $('.desc-tooltip').popover({
                    placement: "left",
                    trigger: "hover",
                });

                $("a.evidence-images").fancybox({
                    titlePosition: 'inside'
                });
                $("div.expandable").expander({
                    slicePoint: 50,
                    expandText: '[...]',
                    expandPrefix: ' ',
                    userCollapseText: '[^]'
                });

                $(".numeric-value").each(function(idx) {
                    const val = $(this).html();
                    const vals = val.split("e"); // capture scientific notation
                    if (vals.length > 1) {
                        $(this).html(_.template($("#observeddatanumericevidence-val-tmpl").html())({
                            firstPart: vals[0],
                            secondPart: vals[1].replace("+", "")
                        }));
                    }
                });
                $(".cytoscape-view").click(function(event) {
                    event.preventDefault();

                    const sifUrl = $(this).attr("data-sif-url");
                    const sifDesc = $(this).attr("data-description");
                    $.ajax({
                        url: "sif/",
                        data: {
                            url: sifUrl
                        },
                        dataType: "json",
                        contentType: "json",
                        success: function(data) {
                            $.fancybox.open(
                                _.template($("#cytoscape-tmpl").html())({
                                    description: sifDesc
                                }), {
                                    touch: false,
                                    'autoDimensions': false,
                                    'transitionIn': 'none',
                                    'transitionOut': 'none'
                                }
                            );

                            // load cytoscape
                            cytoscape({
                                container: $('#cytoscape-sif'),
                                wheelSensitivity: 0.4,
                                layout: {
                                    name: 'cola',
                                    liveUpdate: false,
                                    maxSimulationTime: 1000,
                                    stop: function() {
                                            this.stop();
                                        } // callback on layoutstop 
                                },
                                elements: data,
                                style: cytoscape.stylesheet()
                                    .selector("node")
                                    .css({
                                        "content": "data(id)",
                                        "border-width": 3,
                                        "background-color": "#DDD",
                                        "border-color": "#555"
                                    })
                                    .selector("edge")
                                    .css({
                                        "width": 1,
                                        "target-arrow-shape": "triangle",
                                        "source-arrow-shape": "circle",
                                        "line-color": "#444"
                                    })
                                    .selector(":selected")
                                    .css({
                                        "background-color": "#000",
                                        "line-color": "#000",
                                        "source-arrow-color": "#000",
                                        "target-arrow-color": "#000"
                                    })
                                    .selector(".ui-cytoscape-edgehandles-source")
                                    .css({
                                        "border-color": "#5CC2ED",
                                        "border-width": 3
                                    })
                                    .selector(".ui-cytoscape-edgehandles-target, node.ui-cytoscape-edgehandles-preview")
                                    .css({
                                        "background-color": "#5CC2ED"
                                    })
                                    .selector("edge.ui-cytoscape-edgehandles-preview")
                                    .css({
                                        "line-color": "#5CC2ED"
                                    })
                                    .selector("node.ui-cytoscape-edgehandles-preview, node.intermediate")
                                    .css({
                                        "shape": "rectangle",
                                        "width": 15,
                                        "height": 15
                                    }),

                                ready: function() {
                                    // for debugging
                                }
                            });
                            // end load cytoscape
                        }
                    });

                }); // END OF .cytoscape-view").click

                if (result.submission.observationTemplate.isSubmissionStory && storyFilePath.length > 0) {
                    // show the link
                } else {
                    $("#view-full-story").hide();
                }
            }
        });

        $("#small-show-sub-details").click(function(event) {
            event.preventDefault();
            $("#obs-submission-details").slideDown();
            $("#small-show-sub-details").hide();
            $("#small-hide-sub-details").show();
        });

        const hide_submission_detail = function() {
            $("#obs-submission-details").slideUp();
            $("#small-hide-sub-details").hide();
            $("#small-show-sub-details").show();
        };
        $("#small-hide-sub-details").click(function(event) {
            event.preventDefault();
            hide_submission_detail();
        });
        hide_submission_detail();

        if (result.submission.observationTemplate.submissionDescription == "") {
            $("#obs-submission-summary").hide();
        }

        return this;
    }
});

const ObservedEvidenceRowView = Backbone.View.extend({
    render: function() {
        const result = this.model;
        const type = result.evidence.class;
        result.evidence.type = type;

        if (result.observedEvidenceRole == null) {
            result.observedEvidenceRole = {
                displayText: "-",
                evidenceRole: {
                    displayName: "unknown"
                }
            };
        }

        let templateId = "#observedevidence-row-tmpl";
        let isHtmlStory = false;
        let mEvidence = "";

        if (type == "FileEvidence") {
            result.evidence.filePath = result.evidence.filePath.replace(/\\/g, "/");
            if (result.evidence.mimeType.toLowerCase().search("image") > -1) {
                templateId = "#observedimageevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("gct") > -1) {
                templateId = "#observedgctfileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("pdf") > -1) {
                templateId = "#observedpdffileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("sif") > -1) {
                templateId = "#observedsiffileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("mra") > -1) {
                templateId = "#observedmrafileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("html") > -1) {
                templateId = "#observedhtmlfileevidence-row-tmpl";
                isHtmlStory = true;
            } else {
                templateId = "#observedfileevidence-row-tmpl";
            }

            mEvidence = "file";
        } else if (type == "UrlEvidence") {
            templateId = "#observedurlevidence-row-tmpl";
            mEvidence = "url";
        } else if (type == "LabelEvidence") {
            templateId = "#observedlabelevidence-row-tmpl";
            mEvidence = "label";
        } else if (type == "DataNumericValue") {
            templateId = "#observeddatanumericevidence-row-tmpl";
            mEvidence = "numeric";
        }

        result.eco =
            _.chain(ecoMappings)
            .filter(function(m) {
                return m.evidence == mEvidence && m.role == result.observedEvidenceRole.evidenceRole.displayName;
            })
            .first()
            .value();
        if (result.eco == undefined) {
            result.eco = wildcard_evidence_codes[result.observedEvidenceRole.evidenceRole.displayName];
        }
        if (result.eco == undefined) {
            result.eco = {
                eco_term: '',
                eco_id: ''
            };
        }

        this.template = _.template($(templateId).html());
        $(this.el).append(this.template(result));

        if (isHtmlStory) {
            $(this.el).find(".html-story-link").attr("href", "#" + result.observation.submission.stableURL.replace("submission", "story"));
        }

        $(".img-rounded").popover({
            placement: "left",
            trigger: "hover",
        });
        return this;
    }
});

const ObservedSubjectSummaryRowView = Backbone.View.extend({
    template: _.template($("#observedsubject-summary-row-tmpl").html()),
    render: function() {
        const result = this.model;
        if (result.subject == null) return;
        if (result.subject.type == undefined) {
            result.subject.type = result.subject.class;
        }

        if (result.subject.class != "Gene") {
            this.template = _.template($("#observedsubject-summary-row-tmpl").html());
            $(this.el).append(this.template(result));
        } else {
            this.template = _.template($("#observedsubject-gene-summary-row-tmpl").html());
            $(this.el).append(this.template(result));
            const currentGene = result.subject.displayName;

            $(".addGene-" + currentGene).click(function(e) {
                e.preventDefault();
                updateGeneList(currentGene);
                return this;
            }); //end addGene
            $('.cartAddPlus').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function() {
                $(this).popover('hide');
            });
        }

        return this;
    }
});

const Subject = Backbone.Model.extend({
    urlRoot: BASE_URL + "get/subject"
});

/// TODO: encode these into the DB instead
const ecoMappings = [{
    "evidence": "file",
    "role": "background",
    "eco_term": "inference from background scientific knowledge",
    "eco_id": "ECO:0000001"
}, {
    "evidence": "file",
    "role": "computed",
    "eco_term": "computational combinatorial evidence",
    "eco_id": "ECO:0000053"
}, {
    "evidence": "file",
    "role": "literature",
    "eco_term": "traceable author statement",
    "eco_id": "ECO:0000033"
}, {
    "evidence": "file",
    "role": "measured",
    "eco_term": "direct assay evidence",
    "eco_id": "ECO:0000002"
}, {
    "evidence": "file",
    "role": "observed",
    "eco_term": "experimental phenotypic evidence",
    "eco_id": "ECO:0000059"
}, {
    "evidence": "file",
    "role": "written",
    "eco_term": "author statement",
    "eco_id": "ECO:0000204"
}, {
    "evidence": "label",
    "role": "background",
    "eco_term": "inference from background scientific knowledge",
    "eco_id": "ECO:0000001"
}, {
    "evidence": "label",
    "role": "computed",
    "eco_term": "computational combinatorial evidence",
    "eco_id": "ECO:0000053"
}, {
    "evidence": "label",
    "role": "observed",
    "eco_term": "ad-hoc qualitative phenotype observation evidence",
    "eco_id": "ECO:0005673"
}, {
    "evidence": "label",
    "role": "species",
    "eco_term": "biological system reconstruction evidence by experimental evidence from single species",
    "eco_id": "ECO:0005553"
}, {
    "evidence": "numeric",
    "role": "background",
    "eco_term": "inference from background scientific knowledge",
    "eco_id": "ECO:0000001"
}, {
    "evidence": "numeric",
    "role": "computed",
    "eco_term": "computational combinatorial evidence",
    "eco_id": "ECO:0000053"
}, {
    "evidence": "numeric",
    "role": "measured",
    "eco_term": "experimental phenotypic evidence",
    "eco_id": "ECO:0000059"
}, {
    "evidence": "numeric",
    "role": "observed",
    "eco_term": "ad-hoc quantitative phenotype observation evidence",
    "eco_id": "ECO:0005675"
}, {
    "evidence": "url",
    "role": "computed",
    "eco_term": "computational combinatorial evidence",
    "eco_id": "ECO:0000053"
}, {
    "evidence": "url",
    "role": "link",
    "eco_term": "combinatorial evidence",
    "eco_id": "ECO:0000212"
}, {
    "evidence": "url",
    "role": "measured",
    "eco_term": "experimental phenotypic evidence",
    "eco_id": "ECO:0000059"
}, {
    "evidence": "url",
    "role": "reference",
    "eco_term": "traceable author statement",
    "eco_id": "ECO:0000033"
}, {
    "evidence": "url",
    "role": "resource",
    "eco_term": "imported information",
    "eco_id": "ECO:0000311"
}];