(function ($) {
    // This is strictly coupled to the homepage design!
    const numOfStoriesHomePage = 4;
    const numOfCartGene = 25;

    // These seperators are for replacing items within the observation summary
    const leftSep = "<";
    const rightSep = ">";

    // To make URL constructing more configurable
    const CORE_API_URL = "./";

    // This is for the moustache-like templates
    // prevents collisions with JSP tags <%...%>
    _.templateSettings = {
        interpolate: /\{\{(.+?)\}\}/g
    };

    // Get these options from the page
    const maxNumberOfEntities = $("#maxNumberOfEntites").html() * 1;

    // Datatables fix
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    });

    $.extend(true, $.fn.dataTable.defaults, {
        "oLanguage": { // "search" -> "filter"
            "sSearch": "Filter Table:"
        },
        "search": { // simple searching
            "smart": false
        },
        // These are for bootstrap-styled datatables
        "sDom": "<ifrtlp>",
        "sPaginationType": "bootstrap"
    });

    // Let datatables know about our date format
    $.extend($.fn.dataTable.ext.order, {
        "dashboard-date": function (settings, col) {
            return this.api().column(col, {
                order: 'index'
            }).nodes().map(
                function (td, i) {
                    return (new Date($('a', td).html())).getTime();
                }
            );
        },
        /* this sorting order is special and only for the date column of the center page */
        "text-date-order": function (settings, col) {
            return this.api().column(col, {
                order: 'index'
            }).nodes().map(
                function (td, i) {
                    return (new Date($('small', td).html())).getTime();
                }
            );
        }
    });

    // Let datatables know about dashboard count (for sorting)
    $.extend($.fn.dataTable.ext.order, {
        "dashboard-rank": function (settings, col) {
            return this.api().column(col, {
                order: 'index'
            }).nodes().map(
                function (td, i) {
                    return $('a', td).attr("count");
                }
            );
        },
        'submission-count': function (settings, col) {
            return this.api().column(col, {
                order: 'index'
            }).nodes().map(
                function (td, i) {
                    const t = $('a', td).text().trim();
                    return parseInt(t.split(' ')[0]);
                }
            );
        }
    });

    // Let datatables know about observation count (for sorting explore-table)
    $.extend($.fn.dataTable.ext.type.order, {
        "observation-count-pre": function (d) {
            if (d == null || d == "") return 0;
            const start = d.indexOf(">");
            const end = d.indexOf("<", start);
            if (end <= start) return 0;
            const count_text = d.substring(start + 1, end);
            let count = 0;
            if (count_text != undefined) count = parseInt(count_text);
            return count;
        }
    });

    $.fn.dataTable.Api.register('order.neutral()', function () {
        return this.iterator('table', function (s) {
            s.aaSorting.length = 0;
            s.aiDisplay.sort(function (a, b) {
                return a - b;
            });
            s.aiDisplayMaster.sort(function (a, b) {
                return a - b;
            });
        });
    });

    /* Models */
    const HomepageText = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/homepage-text"
    });

    const SubmissionCenter = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/center"
    });
    const SubmissionCenters = Backbone.Collection.extend({
        url: CORE_API_URL + "list/center/?filterBy=",
        model: SubmissionCenter
    });

    const Submission = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/submission"
    });

    const CenterSubmissions = Backbone.Collection.extend({
        url: CORE_API_URL + "list/submission/?filterBy=",
        model: Submission,

        initialize: function (attributes) {
            this.url += attributes.centerId;
        }
    });

    const StorySubmissions = Backbone.Collection.extend({
        url: CORE_API_URL + "stories/?limit=",
        model: Submission,

        initialize: function (attributes) {
            if (attributes != undefined && attributes.limit != undefined) {
                this.url += attributes.limit;
            } else {
                this.url += numOfStoriesHomePage;
            }
        }

    });

    const Observation = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observation"
    });

    const ObservationsBySubmission = Backbone.Collection.extend({
        url: CORE_API_URL + "observations/bySubmission/?submissionId=",
        model: Observation,

        initialize: function (attributes) {
            this.url += attributes.submissionId;

            if (attributes.getAll != undefined) {
                this.url += "&getAll=" + attributes.getAll;
            }
        }
    });

    const OneObservationsPerSubmissionBySubject = Backbone.Collection.extend({
        url: CORE_API_URL + "observations/onePerSubmissionBySubject/?subjectId=",
        model: Observation,

        initialize: function (attributes) {
            this.url += attributes.subjectId;
            if (attributes.role != undefined) {
                this.url += "&role=" + attributes.role;
            }
            if (attributes.tier != undefined) {
                this.url += "&tier=" + attributes.tier;
            }
        }
    });

    const OneObservationsPerSubmissionByECOTerm = Backbone.Collection.extend({
        url: CORE_API_URL + "observations/onePerSubmissionByEcoTerm/?ecocode=",
        model: Observation,

        initialize: function (attributes) {
            this.url += attributes.ecocode;
            if (attributes.tier != undefined) {
                this.url += "&tier=" + attributes.tier;
            }
        }
    });

    const ObservationsBySubmissionAndSubject = Backbone.Collection.extend({
        url: CORE_API_URL + "observations/bySubmissionAndSubject/?",
        model: Observation, // in fact observation with summary

        initialize: function (attributes) {
            this.url += "submissionId=" + attributes.submissionId + "&subjectId=" + attributes.subjectId;
            if (attributes.role != undefined) {
                this.url += "&role=" + attributes.role;
            }
        }
    });

    const ObservationsBySubmissionAndEcoTerm = Backbone.Collection.extend({
        url: CORE_API_URL + "observations/bySubmissionAndEcoTerm/?",
        model: Observation, // in fact observation with summary

        initialize: function (attributes) {
            this.url += "submissionId=" + attributes.submissionId + "&ecocode=" + attributes.ecocode;
        }
    });

    const SubjectRole = Backbone.Model.extend({});
    const SubjectRoles = Backbone.Collection.extend({
        url: CORE_API_URL + "list/role?filterBy=",
        model: SubjectRole
    });

    const ObservedEvidence = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observedevidence"
    });

    const ObservedEvidences = Backbone.Collection.extend({
        url: CORE_API_URL + "list/observedevidence/?filterBy=",
        model: ObservedEvidence,

        initialize: function (attributes) {
            this.url += attributes.observationId;
        }
    });

    const ObservedSubject = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observedsubject"
    });

    const ObservedSubjects = Backbone.Collection.extend({
        url: CORE_API_URL + "list/observedsubject/?filterBy=",
        model: ObservedSubject,

        initialize: function (attributes) {
            if (attributes.subjectId != undefined) {
                this.url += attributes.subjectId;
            } else {
                this.url += attributes.observationId;
            }
        }
    });

    const SearchResults = Backbone.Model.extend({
        url: CORE_API_URL + "search/",

        initialize: function (attributes) {
            this.url += encodeURIComponent(attributes.term.toLowerCase());
        }
    });

    const ECOTerm = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "eco/term"
    });

    const AnimalModel = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/animal-model"
    });

    const Gene = Backbone.Model.extend({
        urlRoot: 'get/gene',

        initialize: function (attributes) {
            this.url = this.urlRoot + "/" + attributes.species + "/" + attributes.symbol;
        }
    });

    const CellSample = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/cell-sample",
    });

    const Compound = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/compound",
    });

    const Protein = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/protein",
    });

    const ShRna = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/rna",
    });

    const TissueSample = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/tissue",
    });

    const Transcript = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/transcript",
    });

    const Subject = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/subject"
    });

    const SubjectWithSummaryCollection = Backbone.Collection.extend({
        url: CORE_API_URL + "explore/",

        initialize: function (attributes) {
            this.url += attributes.roles;
        }
    });

    const EcoBrowse = Backbone.Collection.extend({
        url: CORE_API_URL + "eco/browse",
    });

    const Summary = Backbone.Collection.extend({
        url: CORE_API_URL + "api/summary",
    });

    /* Views */
    const HomeView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#home-tmpl").html()),
        render: function () {
            // Load the template
            $(this.el).html(this.template(this.model));
            $("#tierTooltip").popover({
                placement: "bottom",
                trigger: 'hover',
                content: "A CTD<sup>2</sup> Network-defined ranking system for evidence that is based on the extent of characterization associated with a particular study." +
                    "<ul><li><b><i>Tier 1</i></b>: Preliminary results of screening campaigns." +
                    "<li><b><i>Tier 2</i></b>: Confirmation of primary results <i>in vitro</i>." +
                    "<li><b><i>Tier 3</i></b>: Validation of results in a cancer relevant <i>in vivo</i> model.</ul>",
                html: true,
            });

            // and load the stories
            const storySubmissions = new StorySubmissions();
            storySubmissions.fetch({
                success: function () {
                    let counter = 1;
                    _.each(storySubmissions.models, function (aStory) {
                        new StorySubmissionView({
                            el: $("#story-" + counter),
                            model: aStory.toJSON()
                        }).render();
                        counter++;
                    });

                    const allinks = $('.stories-pagination a.story-link');
                    allinks.click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    let next = 1;
                    const turn_carousel = function () {
                        if (!allinks.is(":visible")) return;
                        allinks[next].click();
                        next++;
                        next = next % 4;
                        setTimeout(turn_carousel, 12000);
                    };
                    setTimeout(turn_carousel, 12000);
                }
            });

            const summary = new Summary();
            summary.fetch({
                success: function () {
                    const x = {};
                    _.each(summary.models, function (summaryItem) {
                        const label = summaryItem.get('label');
                        x[label] = summaryItem;
                    });
                    ['Compounds', 'Genes', 'shRNA'].forEach(function (s) {
                        new SummaryItemView({
                            el: $("#summary-table-body"),
                            model: x[s].toJSON(),
                        }).render();
                    });
                    $("#summary-table-body").append('<tr><td colspan=6></td></tr>');
                    ['Animal Models', 'Cell Lines', 'Disease Contexts (Tissues)'].forEach(function (s) {
                        new SummaryItemView({
                            el: $("#summary-table-body"),
                            model: x[s].toJSON(),
                        }).render();
                    });
                    $("#summary-table-body").append('<tr><td colspan=6></td></tr>');
                    ['Evidence Types', 'Stories'].forEach(function (s) {
                        new SummaryItemView({
                            el: $("#summary-table-body"),
                            model: x[s].toJSON(),
                        }).render();
                    });
                    const summaryTable = new SummaryItemView({
                        el: $("#summary-table-body"),
                        model: x[''].toJSON(),
                    });
                    summaryTable.render();
                    summaryTable.$('tr').last().css('background-color', 'white');
                }
            });
            $("#summary-table").hide();
            $("#summary-table-label").click(function (e) {
                e.preventDefault();
                $("#summary-table").toggle();
                $("#toggle-word").text(function (index, content) {
                    if (content == "Show") return "Hide";
                    else return "Show";
                });
            });
            $('#summary-table thead th').popover({
                placement: "top",
                trigger: 'hover',
            });

            $("#omni-search-form").submit(function () {
                window.location.hash = "search/" + $("#omni-search").val();
                return false;
            });

            $("#homepage-help-navigate").click(function (e) {
                e.preventDefault();
                (new HelpNavigateView()).render();
            });

            $("#video-link1").click(function (e) {
                e.preventDefault();
                (new VideoPopupView({
                    model: {
                        videoid: "UD40bbg2ISU",
                        description: this.title
                    }
                })).render();
            }).popover({
                placement: "bottom",
                trigger: 'hover',
                content: "This video introduces and defines common terminology used throughout the Dashboard.",
            });
            $("#video-link2").click(function (e) {
                e.preventDefault();
                (new VideoPopupView({
                    model: {
                        videoid: "_hpDlXMAYMs",
                        description: this.title
                    }
                })).render();
            }).popover({
                placement: "bottom",
                trigger: 'hover',
                content: "This video explains how users can search and browse the Dashboard through gene-centric, compound/perturbation-centric, or disease-relevant keywords.",
            });
            $("#video-link3").click(function (e) {
                e.preventDefault();
                (new VideoPopupView({
                    model: {
                        videoid: "RsHTBX_CeNw",
                        description: this.title
                    }
                })).render();
            }).popover({
                placement: "bottom",
                trigger: 'hover',
                content: "This video shows how the Dashboard Gene Cart can predict or verify molecular interactions using a subset of publicly available tissue- and disease-specific interactomes.",
            });

            $('#explore-gene-button').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.BROWSE_GENES,
            }).click(function () {
                $(this).popover('hide');
            });
            $('#explore-compound-button').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.BROWSE_COMPOUNDS,
            }).click(function () {
                $(this).popover('hide');
            });
            $('#explore-disease-button').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.BROWSE_DISEASE,
            }).click(function () {
                $(this).popover('hide');
            });
            $('#explore-stories-button').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.BROWSE_STORIES,
            }).click(function () {
                $(this).popover('hide');
            });
            $('#explore-celllines-button').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.BROWSE_CELLLINES,
            }).click(function () {
                $(this).popover('hide');
            });
            $('#explore-eco-button').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.BROWSE_ECO,
            }).click(function () {
                $(this).popover('hide');
            });

            $('#navlink-genecart').popover({
                placement: "bottom",
                trigger: 'hover',
                content: __ctd2_hovertext.GENE_CART,
            }).click(function () {
                $(this).popover('hide');
            });
            return this;
        }
    });

    const VideoPopupView = Backbone.View.extend({
        template: _.template($("#video-popup-tmpl").html()),
        render: function () {

            $.fancybox.open(
                this.template(this.model), {
                'autoDimensions': false,
                'centerOnScroll': true,
                'transitionIn': 'none',
                'transitionOut': 'none'
            }
            );

            return this;
        }
    });

    const HowToCiteView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#how-to-cite-tmpl").html()),
        render: function () {
            $(this.el).html(this.template({}));
            return this;
        }
    });

    const HelpNavigateView = Backbone.View.extend({
        template: _.template($("#help-navigate-tmpl").html()),

        render: function () {

            $.fancybox.open(
                this.template({}), {
                'autoDimensions': false,
                'centerOnScroll': true,
                'transitionIn': 'none',
                'transitionOut': 'none'
            }
            );

            return this;
        }
    });

    const HtmlStoryView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#html-story-container-tmpl").html()),
        render: function () {
            const storyView = this;
            const thatEl = $(this.el);
            const url = this.model.url;
            const observation = this.model.observation;

            $.post("html", {
                url: url
            }).done(function (summary) {
                summary = summary.replace(
                    new RegExp("#submission_center", "g"),
                    "#" + observation.submission.observationTemplate.submissionCenter.stableURL
                );

                const observedSubjects = new ObservedSubjects({
                    observationId: observation.id
                });
                observedSubjects.fetch({
                    success: function () {
                        _.each(observedSubjects.models, function (observedSubject) {
                            observedSubject = observedSubject.toJSON();

                            if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                                return;

                            summary = summary.replace(
                                new RegExp("#" + observedSubject.observedSubjectRole.columnName + "\"", "g"),
                                "#" + observedSubject.subject.stableURL + "\""
                            );
                        });

                        const observedEvidences = new ObservedEvidences({
                            observationId: observation.id
                        });
                        observedEvidences.fetch({
                            success: function () {
                                _.each(observedEvidences.models, function (observedEvidence) {
                                    observedEvidence = observedEvidence.toJSON();

                                    if (observedEvidence.observedEvidenceRole == null ||
                                        observedEvidence.evidence == null ||
                                        observedEvidence.evidence.class != "UrlEvidence") {
                                        return;
                                    }

                                    summary = summary.replace(
                                        new RegExp("#" + observedEvidence.observedEvidenceRole.columnName, "g"),
                                        observedEvidence.evidence.url.replace(/^\//, '')
                                    );
                                });

                                // clean up outer html tags
                                summary = summary.replace(new RegExp("<!DOCTYPE\\b[^>]*>\\n"), "");
                                summary = summary.replace(new RegExp("<html>\\n"), "");
                                summary = summary.replace(new RegExp("<head\\b[^>]*>\\n"), "");
                                summary = summary.replace(new RegExp("<title\\b[^>]*>(.*?)</title>\\n"), ""); // remove title element
                                summary = summary.replace(new RegExp("<meta\\b[^>]*>\\n"), ""); // remove meta tag
                                summary = summary.replace(new RegExp("</head>\\n"), "");
                                summary = summary.replace(new RegExp("<body>\\n"), "");
                                summary = summary.replace(new RegExp("</body>\\n"), "");
                                summary = summary.replace(new RegExp("</html>\\n"), "");

                                $(thatEl).html(storyView.template({
                                    story: summary,
                                    centerName: observation.submission.observationTemplate.submissionCenter.displayName
                                }));
                                $('.noclick-popover').popover({
                                    placement: "bottom",
                                    trigger: 'hover',
                                });
                            }
                        });
                    }
                });
            });

            return this;

        }
    });

    const StoryListItemView = Backbone.View.extend({
        template: _.template($("#stories-tbl-row-tmpl").html()),

        render: function () {
            $(this.el).append(this.template(this.model));

            let summary = this.model.submission.observationTemplate.observationSummary;
            const thatModel = this.model;
            const thatEl = $("#story-list-summary-" + this.model.id);
            const observedSubjects = new ObservedSubjects({
                observationId: this.model.id
            });
            observedSubjects.fetch({
                success: function () {
                    _.each(observedSubjects.models, function (observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html())(observedSubject.subject)
                        );
                    });

                    const observedEvidences = new ObservedEvidences({
                        observationId: thatModel.id
                    });
                    observedEvidences.fetch({
                        success: function () {
                            _.each(observedEvidences.models, function (observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if (observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                // If there are more than one file evidences, then we might have a problem here
                                if (observedEvidence.evidence.class == "FileEvidence" &&
                                    (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1 || observedEvidence.evidence.mimeType.toLowerCase().search("pdf") > -1)) {
                                    // If this is a summary, then it should be a pdf/html file evidence
                                    if (observedEvidence.evidence.mimeType.toLowerCase().search("html") < 0) {
                                        console.log(observedEvidence.evidence.mimeType + ": pdf case, no handled. ");
                                    }
                                }

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html())(observedEvidence.evidence)
                                );
                            });

                            $(thatEl).html(summary);
                        }
                    });
                }
            });
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });

            return this;
        }
    });

    const StorySubmissionView = Backbone.View.extend({
        template: _.template($("#story-homepage-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));

            let summary = this.model.submission.observationTemplate.observationSummary;
            const thatModel = this.model;
            const thatEl = $("#story-summary-" + this.model.id);
            const observedSubjects = new ObservedSubjects({
                observationId: this.model.id
            });
            observedSubjects.fetch({
                success: function () {
                    _.each(observedSubjects.models, function (observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html())(observedSubject.subject)
                        );
                    });

                    const observedEvidences = new ObservedEvidences({
                        observationId: thatModel.id
                    });
                    observedEvidences.fetch({
                        success: function () {
                            _.each(observedEvidences.models, function (observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if (observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                // If there are more than one file evidences, then we might have a problem here
                                if (observedEvidence.evidence.class == "FileEvidence" &&
                                    (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1 || observedEvidence.evidence.mimeType.toLowerCase().search("pdf") > -1)) {
                                    // If this is a summary, then it should be a pdf/html file evidence
                                    if (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1) {
                                        // console.log("html case. expected");
                                    } else {
                                        console.log(observedEvidence.evidence.mimeType + ": pdf case, no handled. ");
                                    }
                                }

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html())(observedEvidence.evidence)
                                );
                            });

                            $(thatEl).html(summary);
                        }
                    });
                }
            });

            return this;
        }
    });

    /// TODO: encode these into the DB instead
    const ecoMappings = [{
        "evidence": "file",
        "role": "background",
        "eco_term": "inference from background scientific knowledge",
        "eco_id": "ECO:0000001"
    },
    {
        "evidence": "file",
        "role": "computed",
        "eco_term": "computational combinatorial evidence",
        "eco_id": "ECO:0000053"
    },
    {
        "evidence": "file",
        "role": "literature",
        "eco_term": "traceable author statement",
        "eco_id": "ECO:0000033"
    },
    {
        "evidence": "file",
        "role": "measured",
        "eco_term": "direct assay evidence",
        "eco_id": "ECO:0000002"
    },
    {
        "evidence": "file",
        "role": "observed",
        "eco_term": "experimental phenotypic evidence",
        "eco_id": "ECO:0000059"
    },
    {
        "evidence": "file",
        "role": "written",
        "eco_term": "author statement",
        "eco_id": "ECO:0000204"
    },
    {
        "evidence": "label",
        "role": "background",
        "eco_term": "inference from background scientific knowledge",
        "eco_id": "ECO:0000001"
    },
    {
        "evidence": "label",
        "role": "computed",
        "eco_term": "computational combinatorial evidence",
        "eco_id": "ECO:0000053"
    },
    {
        "evidence": "label",
        "role": "observed",
        "eco_term": "ad-hoc qualitative phenotype observation evidence",
        "eco_id": "ECO:0005673"
    },
    {
        "evidence": "label",
        "role": "species",
        "eco_term": "biological system reconstruction evidence by experimental evidence from single species",
        "eco_id": "ECO:0005553"
    },
    {
        "evidence": "numeric",
        "role": "background",
        "eco_term": "inference from background scientific knowledge",
        "eco_id": "ECO:0000001"
    },
    {
        "evidence": "numeric",
        "role": "computed",
        "eco_term": "computational combinatorial evidence",
        "eco_id": "ECO:0000053"
    },
    {
        "evidence": "numeric",
        "role": "measured",
        "eco_term": "experimental phenotypic evidence",
        "eco_id": "ECO:0000059"
    },
    {
        "evidence": "numeric",
        "role": "observed",
        "eco_term": "ad-hoc quantitative phenotype observation evidence",
        "eco_id": "ECO:0005675"
    },
    {
        "evidence": "url",
        "role": "computed",
        "eco_term": "computational combinatorial evidence",
        "eco_id": "ECO:0000053"
    },
    {
        "evidence": "url",
        "role": "link",
        "eco_term": "combinatorial evidence",
        "eco_id": "ECO:0000212"
    },
    {
        "evidence": "url",
        "role": "measured",
        "eco_term": "experimental phenotypic evidence",
        "eco_id": "ECO:0000059"
    },
    {
        "evidence": "url",
        "role": "reference",
        "eco_term": "traceable author statement",
        "eco_id": "ECO:0000033"
    },
    {
        "evidence": "url",
        "role": "resource",
        "eco_term": "imported information",
        "eco_id": "ECO:0000311"
    }
    ];

    const class2imageData = {
        AnimalModel: {
            image: 'img/animalmodel.png',
            label: 'Animal model'
        },
        CellSample: {
            image: 'img/cellsample.png',
            label: 'Cell sample'
        },
        Compound: {
            image: 'img/unknown.png',
            label: 'compound'
        },
        Gene: {
            image: 'img/gene.png',
            label: 'Gene'
        },
        Protein: {
            image: 'img/protein.png',
            label: 'Protein'
        },
        ShRna: {
            image: 'img/shrna.png',
            label: 'shRNA'
        },
        TissueSample: {
            image: 'img/tissuesample.png',
            label: 'Tissue sample'
        },
        ECOTerm: {
            image: 'img/eco_logo.png',
            label: "ECO Term"
        }
    };

    const ObservationView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#observation-tmpl").html()),
        render: function () {
            const result = this.model.toJSON();
            $(this.el).html(this.template(result));

            $(this.el).find("h2 > small").popover({
                placement: "top",
                trigger: 'hover',
                content: function () {
                    const hovertext_id = 'EXPLORE_' + $(this).text().toUpperCase().replace(' ', '_').replace(/\(|\)/g, '');
                    return __ctd2_hovertext[hovertext_id];
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
                success: function () {
                    _.each(observedSubjects.models, function (observedSubject) {
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
                                success: function () {
                                    _.each(compound.toJSON().xrefs, function (xref) {
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
                        content: function () {
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
                ecos.forEach(function (ecocode) {
                    if (ecocode == '') return;
                    const ecourl = ecocode.replace(':', '-').toLowerCase();
                    const eco_model = new ECOTerm({
                        id: ecourl,
                    });
                    eco_model.fetch({
                        async: false,
                        success: function () { // no-op 
                        },
                        error: function () {
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
                success: function () {
                    let storyFilePath = "";
                    _.each(observedEvidences.models, function (observedEvidence) {
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

                    $(".numeric-value").each(function (idx) {
                        const val = $(this).html();
                        const vals = val.split("e"); // capture scientific notation
                        if (vals.length > 1) {
                            $(this).html(_.template($("#observeddatanumericevidence-val-tmpl").html())({
                                firstPart: vals[0],
                                secondPart: vals[1].replace("+", "")
                            }));
                        }
                    });
                    $(".cytoscape-view").click(function (event) {
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
                            success: function (data) {
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

                                    layout: {
                                        name: 'cola',
                                        liveUpdate: false,
                                        maxSimulationTime: 1000,
                                        stop: function () {
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

                                    ready: function () {
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

            $("#small-show-sub-details").click(function (event) {
                event.preventDefault();
                $("#obs-submission-details").slideDown();
                $("#small-show-sub-details").hide();
                $("#small-hide-sub-details").show();
            });

            const hide_submission_detail = function () {
                $("#obs-submission-details").slideUp();
                $("#small-hide-sub-details").hide();
                $("#small-show-sub-details").show();
            };
            $("#small-hide-sub-details").click(function (event) {
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
        render: function () {
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
                    .filter(function (m) {
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

    const CenterListRowView = Backbone.View.extend({
        template: _.template($("#centers-tbl-row-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });
            return this;
        }

    });

    const CenterListView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#centers-tmpl").html()),
        render: function () {
            $(this.el).html(this.template({}));

            const centers = new SubmissionCenters();
            const thatEl = this.el;
            centers.fetch({
                success: function () {

                    _.each(centers.toJSON(), function (aCenter) {
                        aCenter.pinned = 0;
                        if ('CTD² Network Collaboration' == aCenter.displayName) aCenter.pinned = 2;
                        if ('Other NCI Programs' == aCenter.displayName) aCenter.pinned = -1;
                        new CenterListRowView({
                            el: $(thatEl).find("#centers-tbody"),
                            model: aCenter
                        }).render();

                        $.ajax("count/submission/?filterBy=" + aCenter.id).done(function (count) {
                            const cntContent = _.template(
                                $("#count-submission-tmpl").html())({
                                    count: count
                                });

                            const countCellId = "#submission-count-" + aCenter.id;
                            $(countCellId).html(cntContent);
                            $("#centers-list-table").DataTable().cells(countCellId).invalidate();
                        });

                        $.ajax("list/observationtemplate/?filterBy=" + aCenter.id).done(function (templates) {
                            const piCellId = "#center-pi-" + aCenter.id;
                            if ('CTD² Network Collaboration' == aCenter.displayName || 'Other NCI Programs' == aCenter.displayName) {
                                $(piCellId).html('');
                            } else {
                                const pis = [];
                                _.each(templates, function (template) {
                                    pis.push(template.principalInvestigator);
                                });
                                $(piCellId).html(_.uniq(pis).join(", "));
                            }
                            $("#centers-list-table").DataTable().cells(piCellId).invalidate();
                        });
                    });

                    $("#centers-list-table").dataTable({
                        // might want to increase this number if we have incredible number of centers
                        "iDisplayLength": 25,
                        "orderFixed": [4, "desc"],
                        columnDefs: [{
                            targets: [0, 2],
                            orderable: false,
                        }, {
                            targets: 3,
                            orderDataType: "submission-count",
                            type: "numeric"
                        },
                        {
                            "visible": false,
                            "sortable": false,
                            "searchable": false,
                            "targets": 4
                        },
                        ],
                    }).fnSort([
                        [1, 'asc']
                    ]);
                    $("#centers-list-table").parent().find('input[type=search]').popover(table_filter_popover);
                    $('th.submission-count').popover({
                        placement: "top",
                        trigger: 'hover',
                        content: __ctd2_hovertext.CENTER_LIST,
                    });
                }
            });
            return this;
        }
    });

    const StoriesListView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#stories-tmpl").html()),

        render: function () {
            $(this.el).html(this.template({}));

            // and load the stories
            const storySubmissions = new StorySubmissions({
                limit: -1
            });
            storySubmissions.fetch({
                success: function () {
                    _.each(storySubmissions.models, function (aStory) {
                        const storyView = new StoryListItemView({
                            el: $("#stories-list #stories-tbody"),
                            model: aStory.toJSON()
                        });
                        storyView.render();
                    });
                    $('#stories-list').dataTable({
                        columns: [{
                            type: "string"
                        },
                            null,
                        {
                            orderDataType: "text-date-order"
                        },
                        {
                            "orderable": false
                        },
                        ],
                        order: [
                            [2, 'desc']
                        ],
                    });
                }
            });

            return this;
        }
    });

    const EcoBrowseView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#eco-browse-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            $(this.el).html(this.template(thatModel));
            const ecoBrowse = new EcoBrowse(thatModel);
            ecoBrowse.fetch({
                success: function () {
                    $("#eco-browse-items").html("");

                    const table_data = [];
                    _.each(ecoBrowse.models, function (ecoBrowseRow) {
                        const sModel = ecoBrowseRow.toJSON();
                        const sbumission_count = sModel.numberOfSubmissions;
                        const nameLink = "<a href='#" + sModel.ecoTermURL + "'>" + sModel.displayName + "</a>";
                        const n3obv = sModel.numberOfTier3Observations;
                        const n3ctr = sModel.numberOfTier3SubmissionCenters;
                        const n3link = (n3obv == 0 ? "" : "<a href='#" + sModel.ecoTermURL + "/3'>" + n3obv + "</a>") +
                            (n3obv > 0 ? " (" + n3ctr + " center" + (n3ctr > 1 ? "s" : "") + ")" : "");
                        const n2obv = sModel.numberOfTier2Observations;
                        const n2ctr = sModel.numberOfTier2SubmissionCenters;
                        const n2link = (n2obv == 0 ? "" : "<a href='#" + sModel.ecoTermURL + "/2'>" + n2obv + "</a>") +
                            (n2obv > 0 ? " (" + n2ctr + " center" + (n2ctr > 1 ? "s" : "") + ")" : "");
                        const n1obv = sModel.numberOfTier1Observations;
                        const n1ctr = sModel.numberOfTier1SubmissionCenters;
                        const n1link = (n1obv == 0 ? "" : "<a href='#" + sModel.ecoTermURL + "/1'>" + n1obv + "</a>") +
                            (n1obv > 0 ? " (" + n1ctr + " center" + (n1ctr > 1 ? "s" : "") + ")" : "");
                        table_data.push([nameLink, sbumission_count, n3link, n2link, n1link]);
                    });
                    $("#eco-browse-table").dataTable({
                        'dom': '<iBfrtlp>',
                        'data': table_data,
                        "deferRender": true,
                        "columns": [{
                            class: "wrapok"
                        },
                            null,
                        {
                            "type": "observation-count"
                        },
                        {
                            "type": "observation-count"
                        },
                        {
                            "type": "observation-count"
                        }
                        ],
                        'buttons': [{
                            extend: 'excelHtml5',
                            text: 'Export as Spreadsheet',
                            className: "extra-margin",
                        }],
                    });
                    $("#eco-browse-table").parent().width("100%");
                    $("#eco-browse-table").width("100%");
                    $("#eco-browse-table").parent().find('input[type=search]').popover(table_filter_popover);

                    $('#eco-browse-table thead th').popover({
                        placement: "top",
                        trigger: 'hover',
                        content: function () {
                            const hovertext_id = 'EXPLORE_' + $(this).text().toUpperCase().replace(' ', '_');
                            return __ctd2_hovertext[hovertext_id];
                        },
                    });

                    $("#reset-ordering").popover({
                        placement: "top",
                        trigger: 'hover',
                        content: __ctd2_hovertext.EXPLORE_RESET_ORDER,
                    });
                    $("#reset-ordering").click(function () {
                        $("#explore-table").DataTable().order.neutral().draw();
                    });
                }
            });

            return this;
        },
    });

    const CenterSubmissionRowView = Backbone.View.extend({
        template: _.template($("#center-submission-tbl-row-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    const SearchSubmissionRowView = Backbone.View.extend({
        el: "#searched-submissions tbody",
        template: _.template($("#search-submission-tbl-row-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    const SubmissionDescriptionView = Backbone.View.extend({
        el: "#optional-submission-description",
        template: _.template($("#submission-description-tmpl").html()),
        render: function () {
            $(this.el).html(this.template(this.model));
            return this;
        }
    });

    const CompoundView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#compound-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();

            result.drugbank = result.pubchem = result.cas = false;
            result.ctrpID = result.ctrpName = result.depmap = false;

            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "IMAGE") {
                    result.imageFile = xref.databaseId;
                } else if (xref.databaseName == "PUBCHEM") {
                    result.pubchem = xref.databaseId;
                } else if (xref.databaseName == "DRUG BANK") {
                    result.drugbank = xref.databaseId;
                } else if (xref.databaseName == "CTRP ID") {
                    result.ctrpID = xref.databaseId;
                } else if (xref.databaseName == "CTRP NAME") {
                    result.ctrpName = xref.databaseId;
                } else if (xref.databaseName == "DepMap compound") {
                    result.depmap = xref.databaseId;
                } else if (xref.databaseName == "CAS") {
                    result.cas = xref.databaseId;
                }

            });
            result.type = result.class;

            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            let count = 0;
            _.each(result.synonyms, function (aSynonym) {
                if (aSynonym.displayName == result.displayName) return;
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("ul.synonyms")
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch").text(SEE_ALL);
                $(".toomany").hide();
                $("#see-all-switch").click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch").show();
            } else {
                $("#see-all-switch").hide();
            }

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#compound-observation-grid"
            }).render();

            $("a.compound-image").fancybox({
                titlePosition: 'inside'
            });
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });
            return this;
        }
    });

    const observationTableOptions = {
        'dom': '<iBfrtlp>',
        "sPaginationType": "bootstrap",
        "columns": [{
            "orderDataType": "dashboard-date"
        },
            null,
            null,
            null
        ],
        'buttons': [{
            extend: 'excelHtml5',
            text: 'Export as Spreadsheet',
            className: "extra-margin",
            customizeData: function (data) {
                const body = data.body;
                for (let i = 0; i < body.length; i++) {
                    const raw_content = body[i][1].split(/ +/);
                    raw_content.pop();
                    raw_content.pop();
                    body[i][1] = raw_content.join(' ');
                }
            },
        }],
        order: [
            [2, 'desc'],
            [0, 'desc'],
        ],
    };

    const SubjectObservationsView = Backbone.View.extend({
        render: function () {
            const thatEl = $(this.el);
            const thatModel = this.model;

            const observations = new OneObservationsPerSubmissionBySubject({
                subjectId: thatModel.subjectId,
                role: thatModel.role, // possibly undefined
                tier: thatModel.tier, // possibly undefined
            });
            observations.fetch({
                success: function () {
                    $(".subject-observations-loading", thatEl).remove();
                    _.each(observations.models, function (observationWithCount) {
                        observationWithCount = observationWithCount.toJSON();
                        observation = observationWithCount.observation;
                        observation.count = observationWithCount.count;
                        observation.contextSubject = thatModel.subjectId;
                        observation.role = thatModel.role;
                        new ObservationRowView({
                            el: $(thatEl).find("tbody"),
                            model: observation,
                        }).render();
                    });
                    $(thatEl).find('thead th:contains("Tier")').popover({
                        placement: "top",
                        trigger: 'hover',
                        html: true, // because we need multiple lines
                        content: function () {
                            return __ctd2_hovertext.ALL_TIERS;
                        },
                    });

                    $(thatEl).dataTable(observationTableOptions);
                    $(thatEl).width("100%");
                }
            });

            return this;
        }
    });

    const ECOTermObservationsView = Backbone.View.extend({
        render: function () {
            const thatEl = $(this.el);
            const thatModel = this.model;

            const observations = new OneObservationsPerSubmissionByECOTerm({
                ecocode: thatModel.ecocode,
                tier: thatModel.tier, // possibly undefined
            });
            observations.fetch({
                success: function () {
                    $(".subject-observations-loading", thatEl).remove();
                    _.each(observations.models, function (observationWithCount) {
                        observationWithCount = observationWithCount.toJSON();
                        observation = observationWithCount.observation;
                        observation.count = observationWithCount.count;
                        observation.contextSubject = thatModel.subjectId;
                        observation.role = thatModel.role;
                        observation.ecocode = thatModel.ecocode;
                        new ObservationRowView({
                            el: $(thatEl).find("tbody"),
                            model: observation,
                        }).render();
                    });
                    $(thatEl).find('thead th:contains("Tier")').popover({
                        placement: "top",
                        trigger: 'hover',
                        html: true, // because we need multiple lines
                        content: function () {
                            return __ctd2_hovertext.ALL_TIERS;
                        },
                    });

                    $(thatEl).dataTable(observationTableOptions);
                    $(thatEl).width("100%");
                }
            });

            return this;
        }
    });

    const GeneView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#gene-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();
            // Find out the UniProt ID

            result.genecard = false;
            result.dave = false;
            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "GeneCards") {
                    result.genecard = xref.databaseId;
                }
                if (xref.databaseName == "Ensembl") {
                    result.dave = xref.databaseId;
                }
            });

            result.type = result.class;
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            let count = 0;
            _.each(result.synonyms, function (aSynonym) {
                if (aSynonym.displayName == result.displayName) return;
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("ul.synonyms")
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch").text(SEE_ALL);
                $(".toomany").hide();
                $("#see-all-switch").click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch").show();
            } else {
                $("#see-all-switch").hide();
            }

            $.getJSON("findProteinFromGene/" + result.id, function (proteins) {
                _.each(proteins, function (protein) {
                    $("ul.refs").append(_.template($("#gene-uniprot-tmpl").html())({
                        uniprotId: protein.uniprotId
                    }));
                });
            });

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#gene-observation-grid"
            }).render();

            const currentGene = result.displayName;
            $(".addGene-" + currentGene).click(function (e) {
                e.preventDefault();
                updateGeneList(currentGene);
                return this;
            }); //end addGene
            $('.cartAddPlus').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });

            return this;
        }
    });

    const ProteinView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#protein-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();
            result.type = result.class;
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            let count = 0;
            _.each(result.synonyms, function (aSynonym) {
                if (aSynonym.displayName == result.displayName) return;
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("ul.synonyms")
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch").text(SEE_ALL);
                $(".toomany").hide();
                $("#see-all-switch").click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch").show();
            } else {
                $("#see-all-switch").hide();
            }

            _.each(result.transcripts, function (aTranscript) {
                new TranscriptItemView({
                    model: aTranscript,
                    el: $("ul.transcripts")
                }).render();
            });


            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#protein-observation-grid"
            }).render();

            return this;
        }
    });

    const RnaView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#rna-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#shrna-observation-grid"
            }).render();

            return this;
        }
    });

    const TranscriptView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#transcript-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();
            result.type = result.class;
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#transcript-observation-grid"
            }).render();

            return this;
        }
    });

    const TissueSampleView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#tissuesample-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();

            result.diseaseOntology = false;
            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "disease-ontology") {
                    result.diseaseOntology = xref.databaseId;
                }
            });

            result.malacards = false;
            result.depmap = false;
            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "MalaCards") {
                    result.malacards = xref.databaseId;
                } else if (xref.databaseName == "DepMap lineage") {
                    result.depmap = xref.databaseId;
                }
            });

            result.type = result.class;
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            const thatEl = this.el;
            if (result.xrefs.length == 0) {
                $(thatEl).find("#tissue-refs").hide();
            }
            _.each(result.xrefs, function (xref) {
                //if(xref.databaseName == "NCI_PARENT_THESAURUS" || xref.databaseName == "NCI_THESAURUS") {
                if (xref.databaseName == "NCI_THESAURUS") {
                    const ids = xref.databaseId.split(";");
                    _.each(ids, function (xrefid) {
                        $(thatEl).find("ul.xrefs").append(
                            _.template($("#ncithesaurus-tmpl").html())({
                                nciId: xrefid
                            })
                        );
                    });
                }
            });

            if (result.synonyms.length == 0) {
                $(thatEl).find("#tissue-synonyms").hide();
            }
            let count = 0;
            _.each(result.synonyms, function (aSynonym) {
                if (aSynonym.displayName == result.displayName) return;
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("ul.synonyms")
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch").text(SEE_ALL);
                $(".toomany").hide();
                $("#see-all-switch").click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch").show();
            } else {
                $("#see-all-switch").hide();
            }

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#tissuesample-observation-grid"
            }).render();

            return this;
        }
    });

    const ECOTermView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#ecoterm-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.ecoterm.toJSON();
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
            })));

            if (result.synonyms != null && result.synonyms.length > 0) {
                _.each(result.synonyms.split("|"), function (synonym) {
                    if (synonym.displayName == result.displayName) return;

                    new SynonymView({
                        model: {
                            displayName: synonym,
                            sid: result.id,
                        },
                        el: $("ul.synonyms")
                    }).render();
                });
            }

            _.each(result.annotations, function (annotation) {
                annotation.displayName = annotation.displayName.replace(/_/g, " ");
                new AnnotationView({
                    model: annotation,
                    el: $("#annotations ul")
                }).render();
            });

            new ECOTermObservationsView({
                model: {
                    ecocode: result.code,
                    tier: thatModel.tier,
                },
                el: "#ecoterm-observation-grid"
            }).render();

            return this;
        },
    });

    const AnimalModelView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#animalmodel-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();
            result.type = result.class;
            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            let count = 0;
            _.each(result.synonyms, function (aSynonym) {
                if (aSynonym.displayName == result.displayName) return;
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("ul.synonyms")
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch").text(SEE_ALL);
                $(".toomany").hide();
                $("#see-all-switch").click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch").show();
            } else {
                $("#see-all-switch").hide();
            }

            _.each(result.annotations, function (annotation) {
                annotation.displayName = annotation.displayName.replace(/_/g, " ");
                new AnnotationView({
                    model: annotation,
                    el: $("#annotations ul")
                }).render();
            });

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#animalmodel-observation-grid"
            }).render();

            return this;
        }
    });

    const AnnotationView = Backbone.View.extend({
        template: _.template($("#annotation-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
        }
    });

    const CellSampleView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#cellsample-tmpl").html()),
        render: function () {
            const thatModel = this.model;
            const result = thatModel.subject.toJSON();

            result.cosmic = false;
            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "COSMIC SAMPLE") {
                    result.cosmic = xref.databaseId;
                }
            });

            result.cellosaurus = false;
            result.depmap = false;
            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "CELLOSAURUS_ACCESSION") {
                    result.cellosaurus = xref.databaseId;
                } else if (xref.databaseName == "DepMap cell_line") {
                    result.depmap = xref.databaseId;
                }
            });

            result.type = result.class;

            // Look for cbioPortal Id
            let cbioPortalId = null;
            _.each(result.xrefs, function (xref) {
                if (xref.databaseName == "CBIO_PORTAL") {
                    cbioPortalId = xref.databaseId;
                }
            });

            result.cbioPortalId = cbioPortalId;
            result.type = result.class;

            $(this.el).html(this.template($.extend(result, {
                tier: thatModel.tier ? thatModel.tier : null,
                role: thatModel.role ? thatModel.role : null
            })));

            if (!cbioPortalId) {
                $("#cbiolink").css("display", "none");
            }

            let count = 0;
            _.each(result.synonyms, function (aSynonym) {
                if (aSynonym.displayName == result.displayName) return;
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("ul.synonyms")
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch").text(SEE_ALL);
                $(".toomany").hide();
                $("#see-all-switch").click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch").show();
            } else {
                $("#see-all-switch").hide();
            }

            _.each(result.annotations, function (annotation) {
                annotation.displayName = annotation.displayName.replace(/_/g, " ");
                new AnnotationView({
                    model: annotation,
                    el: $("#annotations ul")
                }).render();
            });

            new SubjectObservationsView({
                model: {
                    subjectId: result.id,
                    tier: thatModel.tier,
                    role: thatModel.role
                },
                el: "#cellsample-observation-grid"
            }).render();

            return this;
        }
    });

    const ObservationRowView = Backbone.View.extend({
        template: _.template($("#observation-row-tmpl").html()),
        render: function () {
            const tableEl = this.el;
            const thatModel = this.model; // observation
            const ecocode = thatModel.ecocode;

            if (thatModel.extra === undefined) {
                thatModel.extra = null;
                $(tableEl).append(this.template(thatModel));
            } else {
                thatModel.parentRow.after(this.template(thatModel));
            }
            let summary = this.model.submission.observationTemplate.observationSummary;

            const cellId = "#observation-summary-" + this.model.id;
            const thatEl = $(cellId);
            const parentRow = $(thatEl).parent("tr");

            const observedSubjects = new ObservedSubjects({
                observationId: this.model.id
            });
            observedSubjects.fetch({
                success: function () {
                    _.each(observedSubjects.models, function (observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html())(observedSubject.subject)
                        );
                    });

                    const observedEvidences = new ObservedEvidences({
                        observationId: thatModel.id
                    });
                    observedEvidences.fetch({
                        success: function () {
                            _.each(observedEvidences.models, function (observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if (observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html())(observedEvidence.evidence)
                                );
                            });

                            summary += _.template($("#submission-obs-tbl-row-tmpl").html())(thatModel);
                            $(thatEl).html(summary);
                            const dataTable = $(tableEl).parent().DataTable();
                            dataTable.cells(cellId).invalidate();
                            dataTable.order([
                                [2, 'desc'],
                                [0, 'desc'],
                                [1, 'asc']
                            ]).draw();

                            if (thatModel.extra != null) {
                                return;
                            }

                            if (thatModel.count == undefined) { // the case of search result
                                return;
                            }

                            // following is only for the 'leading' observation
                            $(thatEl).append("<br>");
                            if (thatModel.count == 1) {
                                $(thatEl).append("(There is only one observation in this submission.)");
                                return;
                            }
                            const buttonText = "Show all " + thatModel.count + " observations";
                            const btn = $("<button>" + buttonText + "</button>");
                            $(thatEl).append(btn);
                            thatEl.css("border", "1px solid black");
                            const expandHandler = (function () {
                                $(btn).prop('disabled', true);
                                const submissionId = thatModel.submission.id;

                                let observations = null;
                                if (ecocode != undefined && ecocode.length > 0) {
                                    /* ECO term case */
                                    observations = new ObservationsBySubmissionAndEcoTerm({
                                        submissionId: submissionId,
                                        ecocode: ecocode,
                                    });
                                } else {
                                    /* subject case */
                                    observations = new ObservationsBySubmissionAndSubject({
                                        submissionId: submissionId,
                                        subjectId: thatModel.contextSubject,
                                        role: thatModel.role,
                                    });
                                }
                                const page_before_expanding = $(tableEl).parent().dataTable().api().page();
                                observations.fetch({
                                    success: function () {
                                        $(tableEl).parent().dataTable().fnDestroy();
                                        _.each(observations.models, function (observation_with_summary) {
                                            observation_with_summary = observation_with_summary.toJSON();
                                            if (observation_with_summary.observation.id == thatModel.id) return;
                                            observation_with_summary.parentRow = parentRow;
                                            const extraObservationRowView = new FastObservationRowView({
                                                el: $(thatEl).find("tbody"),
                                                model: observation_with_summary,
                                            });
                                            extraObservationRowView.render();
                                        });
                                        const dataTable = $(tableEl).parent().dataTable(observationTableOptions);
                                        dataTable.api().page(page_before_expanding).draw(false);
                                        $(btn).text("Hide additional observations from the same submission");
                                        $(btn).off("click");
                                        $(btn).click(function () {
                                            const page_before_expanding = dataTable.api().page();
                                            dataTable.fnDestroy();
                                            $(tableEl).find("tr[submission_id=" + submissionId + "][extra]").remove();
                                            $(tableEl).parent().dataTable(observationTableOptions);
                                            dataTable.api().page(page_before_expanding).draw(false);
                                            $(btn).text(buttonText);
                                            $(btn).off("click");
                                            $(btn).click(expandHandler);
                                        });
                                        $(btn).prop('disabled', false);
                                    },
                                    error: function () {
                                        $(btn).prop('disabled', false);
                                        console.log('ObservationsBySubmissionAndSubject fetch failed');
                                    },
                                });
                            });
                            btn.click(expandHandler);
                        }
                    });
                }
            });
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });

            return this;
        }
    });

    // similar to ObservationRowView, but summary readily expanded
    const FastObservationRowView = Backbone.View.extend({
        template: _.template($("#observation-row-tmpl").html()),
        render: function () {
            const tableEl = this.el;
            const thatModel = this.model; // observation with summary
            const observation = thatModel.observation;

            observation.extra = "extra";
            thatModel.parentRow.after(this.template(observation));

            const cellId = "#observation-summary-" + observation.id;
            const thatEl = $(cellId);
            $(thatEl).html(thatModel.summary);

            const dataTable = $(tableEl).parent().DataTable();
            dataTable.cells(cellId).invalidate();
            dataTable.order([
                [2, 'desc'],
                [0, 'desc'],
                [1, 'asc']
            ]).draw();
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });

            return this;
        }
    });

    const ObservedSubjectSummaryRowView = Backbone.View.extend({
        template: _.template($("#observedsubject-summary-row-tmpl").html()),
        render: function () {
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

                $(".addGene-" + currentGene).click(function (e) {
                    e.preventDefault();
                    updateGeneList(currentGene);
                    return this;
                }); //end addGene
                $('.cartAddPlus').popover({
                    placement: "bottom",
                    trigger: 'hover',
                }).click(function () {
                    $(this).popover('hide');
                });
            }

            return this;
        }
    });

    const CenterView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#center-tmpl").html()),
        render: function (filterProject) {
            const urlMap = {};
            const centerModel = this.model.toJSON();
            centerModel.ocg_dash = ctd2_ocg_dash[centerModel.displayName];
            $(this.el).html(this.template(centerModel));

            const thatEl = this.el;
            const centerSubmissions = new CenterSubmissions({
                centerId: centerModel.id
            });
            centerSubmissions.fetch({
                success: function () {
                    _.each(centerSubmissions.toJSON(), function (submission) {
                        const centerSubmissionRowView = new CenterSubmissionRowView({
                            el: $(thatEl).find("tbody"),
                            model: submission
                        });

                        $.ajax("observations/countBySubmission/?submissionId=" + submission.id, {
                            "async": false
                        }).done(function (count) {
                            const tmplName = submission.observationTemplate.isSubmissionStory ?
                                "#count-story-tmpl" :
                                "#count-observations-tmpl";
                            submission.details = _.template(
                                $(tmplName).html())({
                                    count: count
                                });
                        });

                        centerSubmissionRowView.render();
                    });

                    $('#center-submission-grid').dataTable({
                        "columns": [
                            null,
                            {
                                "visible": false
                            },
                            null,
                            {
                                "orderDataType": "text-date-order"
                            },
                            null
                        ],
                        "drawCallback": function (settings) {
                            const api = this.api();
                            api.column(1, {
                                page: 'all'
                            })
                                .data()
                                .each(function (group, i) {
                                    const project_url = group.toLowerCase().replace(/[^a-zA-Z0-9]/g, "-");
                                    urlMap[project_url] = group;
                                });

                            const rows = api.rows({
                                page: 'current'
                            }).nodes();
                            let last = null;
                            api.column(1, {
                                page: 'current'
                            })
                                .data()
                                .each(function (group, i) {
                                    if (last != group) {
                                        $(rows)
                                            .eq(i)
                                            .before(
                                                _.template($("#tbl-project-title-tmpl").html())({
                                                    project: group,
                                                    project_url: group.toLowerCase().replace(/[^a-zA-Z0-9]/g, "-"),
                                                    centerStableURL: centerModel.stableURL
                                                })
                                            );

                                        last = group;
                                    }
                                });
                        }
                    }).fnSort([
                        [0, 'desc']
                    ]);
                    $("#center-submission-grid").parent().find('input[type=search]').popover(table_filter_popover);
                    $("#center-submission-grid").find('thead th:contains("Tier")').popover({
                        placement: "top",
                        trigger: 'hover',
                        html: true,
                        content: function () {
                            return __ctd2_hovertext.ALL_TIERS;
                        },
                    });

                    if (filterProject != null) {
                        $('#center-submission-grid').DataTable().search(urlMap[filterProject]).draw();
                        const mpModel = {
                            filterProject: urlMap[filterProject],
                            centerStableURL: centerModel.stableURL
                        };
                        new MoreProjectsView({
                            model: mpModel
                        }).render();
                    }
                }
            });
            $('.noclick-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            });

            return this;
        }

    });

    const MoreProjectsView = Backbone.View.extend({
        template: _.template($("#more-projects-tmpl").html()),
        el: "#more-project-container",

        render: function () {
            $(this.el).append(this.template(this.model));
        }
    });

    const SubmissionRowView = Backbone.View.extend({
        template: _.template($("#submission-tbl-row-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
            const sTable = $(this.el).parent();

            let summary = this.model.submission.observationTemplate.observationSummary;

            const thatModel = this.model;
            const cellId = "#submission-observation-summary-" + this.model.id;
            const thatEl = $(cellId);
            const observedSubjects = new ObservedSubjects({
                observationId: this.model.id
            });
            observedSubjects.fetch({
                success: function () {
                    _.each(observedSubjects.models, function (observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html())(observedSubject.subject)
                        );
                    });

                    const observedEvidences = new ObservedEvidences({
                        observationId: thatModel.id
                    });
                    observedEvidences.fetch({
                        success: function () {
                            _.each(observedEvidences.models, function (observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if (observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html())(observedEvidence.evidence)
                                );
                            });

                            summary += _.template($("#submission-obs-tbl-row-tmpl").html())(thatModel);
                            $(thatEl).html(summary);

                            // let the datatable know about the update
                            $(sTable).DataTable().cells(cellId).invalidate();
                        }
                    });

                }
            });

            return this;
        }
    });

    const SubmissionView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#submission-tmpl").html()),
        render: function () {
            const submission = this.model.toJSON();
            $(this.el).html(this.template(submission));
            const centerName = submission.observationTemplate.submissionCenter.displayName;
            if (centerName == 'CTD² Network Collaboration') {
                $('#center-abbreviations').show();
            } else {
                $('#center-abbreviations').hide();
            }
            $(this.el).find("span.badge-tier").popover({
                placement: "top",
                trigger: 'hover',
                content: function () {
                    const hovertext_id = 'EXPLORE_' + $(this).text().toUpperCase().replace(' ', '_');
                    return __ctd2_hovertext[hovertext_id];
                },
            });

            if (submission.observationTemplate.submissionDescription.length > 0) {
                const submissionDescriptionView = new SubmissionDescriptionView({
                    model: submission
                });
                submissionDescriptionView.render();
            }

            const thatEl = this.el;
            const submissionId = this.model.get("id");
            const sTable = '#submission-observation-grid';

            $.ajax("list/similar/" + submissionId).done(function (similarSubmissions) {
                if (similarSubmissions.length < 1) {
                    $("#similar-submission-info").hide();
                } else {
                    _.each(similarSubmissions, function (simSub) {
                        $(thatEl)
                            .find("ul.similar-submission-list")
                            .append(_.template($("#similar-submission-item-tmpl").html())(simSub));
                    });
                }
            });

            const ecocodes = submission.observationTemplate.ECOCode;
            if (ecocodes.length == 0) {
                $("#eco-row").hide();
            } else {
                const ecos = ecocodes.split('|');
                ecos.forEach(function (ecocode) {
                    if (ecocode == '') return;
                    const ecourl = ecocode.replace(':', '-').toLowerCase();
                    const eco_model = new ECOTerm({
                        id: ecourl,
                    });
                    eco_model.fetch({
                        success: function () {
                            const econame = eco_model.toJSON().displayName;
                            $("#eco-list").append('<li>' + econame + " (<a href='#eco/" + ecourl + "'>" + ecocode + '</a>)</li>');
                        },
                        error: function () {
                            console.log('ECO term not found for' + ecocode);
                        },
                    });
                });
            }

            $.ajax("observations/countBySubmission/?submissionId=" + submissionId).done(function (count) {
                const observations = new ObservationsBySubmission({
                    submissionId: submissionId
                });
                observations.fetch({
                    success: function () {
                        $(".submission-observations-loading").hide();

                        _.each(observations.models, function (observation) {
                            observation = observation.toJSON();

                            const submissionRowView = new SubmissionRowView({
                                el: $(thatEl).find(".observations tbody"),
                                model: observation,
                                attributes: {
                                    table: sTable
                                }
                            });
                            submissionRowView.render();
                        });

                        $(sTable).dataTable({
                            dom: "<'fullwidth'ifrtlp>",
                        });

                    }
                });

                if (count > maxNumberOfEntities) {
                    const moreObservationView = new MoreObservationView({
                        model: {
                            numOfObservations: maxNumberOfEntities,
                            numOfAllObservations: count,
                            submissionId: submissionId,
                            tableEl: sTable,
                            rowView: SubmissionRowView,
                            columns: [null],
                            submissionDisplayName: submission.displayName,
                        }
                    });
                    moreObservationView.render();
                }

            });
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });
            $('.noclick-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            });

            return this;
        }
    });

    const MoreObservationView = Backbone.View.extend({
        el: ".more-observations-message",
        template: _.template($("#more-observations-tmpl").html()),
        render: function () {
            const model = this.model;
            const thatEl = this.el;
            $(thatEl).html(this.template(model));
            $(thatEl).find("a.load-more-observations").click(function (e) {
                e.preventDefault();
                $(thatEl).slideUp();

                $(".submission-observations-loading").show();
                const sTableId = model.tableEl;

                if (model.submissionId === undefined) {
                    console.log("something is wrong here!");
                    return;
                }
                const observations = new ObservationsBySubmission({
                    submissionId: model.submissionId,
                    getAll: true
                });
                observations.fetch({
                    success: function () {
                        $(sTableId).DataTable().rows().remove().draw().destroy();

                        _.each(observations.models, function (observation, i) {
                            observation = observation.toJSON();

                            new model.rowView({
                                el: $(model.tableEl).find("tbody"),
                                model: observation
                            }).render();
                        });

                        $(sTableId).dataTable({
                            "columns": model.columns
                        });

                    }
                });
            });
        }
    });

    const TranscriptItemView = Backbone.View.extend({
        template: _.template($("#transcript-item-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    const SynonymView = Backbone.View.extend({
        template: _.template($("#synonym-item-tmpl").html()),
        render: function () {
            if (this.model.toomany == undefined) {
                this.model.toomany = '';
            }
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    const RoleView = Backbone.View.extend({
        template: _.template($("#role-item-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    const EmptyResultsView = Backbone.View.extend({
        template: _.template($("#search-empty-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));

            return this;
        }
    });

    const SearchResultsRowView = Backbone.View.extend({
        template: _.template($("#search-result-row-tmpl").html()),
        render: function () {
            const model = this.model;
            const result = model.dashboardEntity;

            if (result.class != "Gene") {
                this.template = _.template($("#search-result-row-tmpl").html());
                $(this.el).append(this.template(model));
            } else {
                this.template = _.template($("#search-result-gene-row-tmpl").html());
                $(this.el).append(this.template(model));
                const currentGene = result.displayName;

                $(".addGene-" + currentGene).click(function (e) {
                    e.preventDefault();
                    updateGeneList(currentGene);
                    return this;
                }); //end addGene
                $('.cartAddPlus').popover({
                    placement: "bottom",
                    trigger: 'hover',
                }).click(function () {
                    $(this).popover('hide');
                });
            }

            let synonyms = result.synonyms;
            if (result.class == "ECOTerm") {
                synonyms = [];
                if (result.synonyms != null && result.synonyms.length > 0) {
                    _.each(result.synonyms.split("|"), function (aSynonym) {
                        if (aSynonym.displayName == result.displayName) return;
                        synonyms.push({
                            displayName: aSynonym
                        });
                    });
                }
            }
            let count = 0;
            _.each(synonyms, function (aSynonym) {
                if (count >= 3) aSynonym.toomany = 'toomany';
                aSynonym.sid = result.id;
                new SynonymView({
                    model: aSynonym,
                    el: $("#synonyms-" + result.id)
                }).render();
                count++;
            });
            if (count > 3) {
                const SEE_ALL = "see all";
                $("#see-all-switch" + result.id).text(SEE_ALL);
                $(".synonym-of-" + result.id + ".toomany").hide();
                $("#see-all-switch" + result.id).click(function () {
                    if ($(this).text() == SEE_ALL) {
                        $(".synonym-of-" + result.id + ".toomany").show();
                        $(this).text("hide");
                    } else {
                        $(".synonym-of-" + result.id + ".toomany").hide();
                        $(this).text(SEE_ALL);
                    }
                });
                $("#see-all-switch" + result.id).show();
            } else {
                $("#see-all-switch" + result.id).hide();
            }

            _.each(model.roles, function (aRole) {
                new RoleView({
                    model: {
                        role: aRole
                    },
                    el: $("#roles-" + result.id)
                }).render();
            });

            const imageData = class2imageData[result.class];
            imageData.stableURL = result.stableURL;
            const imgTemplate = $("#search-results-image-tmpl");
            if (result.class == "Compound") {
                _.each(result.xrefs, function (xref) {
                    if (xref.databaseName == "IMAGE") {
                        imageData.image = $("#explore-tmpl").attr("data-url") + "compounds/" + xref.databaseId;
                    }
                });
            } else if (result.class == "ShRna" && result.type.toLowerCase() == "sirna") {
                imageData.image = "img/sirna.png";
                imageData.label = "siRNA";
            }
            $("#search-image-" + result.id).append(_.template(imgTemplate.html())(imageData));

            // some of the elements will be hidden in the pagination. Use magic-scoping!
            const cntContent = _.template(
                $("#count-observations-tmpl").html())({
                    count: model.observationCount
                });
            $("#subject-observation-count-" + result.id).html(cntContent);

            return this;
        }
    });

    const tabulate_matching_observations = function (m_observations) {
        $("#observation-search-results").hide();
        if (m_observations.length <= 0) return;

        $("#observation-search-results").fadeIn();
        const thatEl = $("#searched-observation-grid");

        $(".subject-observations-loading", thatEl).remove();
        _.each(m_observations, function (observation) {
            new ObservationRowView({
                el: $(thatEl).find("tbody"),
                model: observation
            }).render();
        });
        $(thatEl).find('thead th:contains("Tier")').popover({
            placement: "top",
            trigger: 'hover',
            html: true,
            content: function () {
                return __ctd2_hovertext.ALL_TIERS;
            },
        });
    };

    const SearchView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#search-tmpl").html()),
        render: function () {
            $(this.el).html(this.template(this.model));

            // update the search box accordingly
            $("#omni-input").val(decodeURIComponent(this.model.term));

            const thatEl = this.el;
            const thatModel = this.model;
            const searchResults = new SearchResults({
                term: this.model.term
            });

            searchResults.fetch({
                success: function () {
                    $("#loading-row").remove();
                    const results = searchResults.toJSON();
                    const subject_result = results.subject_result;
                    const submission_result = results.submission_result;
                    const observation_result = results.observation_result;
                    if (subject_result.length + submission_result.length == 0) {
                        (new EmptyResultsView({
                            el: $(thatEl).find("tbody"),
                            model: thatModel
                        })).render();
                        $('#submission-search-results').hide();
                        $('#observation-search-results').hide();
                    } else {
                        const submissions = [];
                        const matching_observations = [];
                        _.each(subject_result, function (aResult) {
                            if (aResult.dashboardEntity.organism == undefined) {
                                aResult.dashboardEntity.organism = {
                                    displayName: "-"
                                };
                            }
                            const searchResultsRowView = new SearchResultsRowView({
                                model: aResult,
                                el: $(thatEl).find("tbody")
                            });
                            searchResultsRowView.render();
                        });
                        _.each(submission_result, function (aResult) {
                            submissions.push(aResult);
                        });
                        _.each(observation_result, function (aResult) {
                            matching_observations.push(aResult.dashboardEntity);
                        });

                        $(".search-info").popover({
                            placement: "left",
                            trigger: "hover",
                        });
                        $(".obs-tooltip").popover({
                            placement: "bottom",
                            trigger: "hover",
                        });

                        const oTable = $("#search-results-grid").dataTable({
                            "columns": [
                                null,
                                null,
                                null,
                                null,
                                null,
                                {
                                    "orderDataType": "dashboard-rank",
                                    "type": 'num',
                                },
                            ]

                        });
                        oTable.fnSort([
                            [5, 'desc'],
                            [1, 'asc']
                        ]);
                        $("#search-results-grid").parent().width("100%");
                        $("#search-results-grid").parent().find('input[type=search]').popover(table_filter_popover);
                        $('#search-results-grid thead th').popover({
                            placement: "top",
                            trigger: 'hover',
                            content: function () {
                                const hovertext_id = 'SEARCH_' + $(this).text().toUpperCase();
                                const t = __ctd2_hovertext[hovertext_id];
                                if (!t) return null; // only null is automatically hidden
                                return t;
                            },
                        });

                        // OK done with the subjects; let's build the submissions table
                        $("#submission-search-results").hide();
                        if (submissions.length > 0) {
                            $("#submission-search-results").fadeIn();

                            const centerCounter = new Set();
                            _.each(submissions, function (submission) {
                                const searchSubmissionRowView = new SearchSubmissionRowView({
                                    model: submission
                                });
                                searchSubmissionRowView.render();

                                if (submission.observationTemplate === undefined) { // TODO why does this happen?
                                    submission.observationTemplate = {};
                                }
                                const tmplName = submission.observationTemplate.isSubmissionStory ?
                                    "#count-story-tmpl" :
                                    "#count-observations-tmpl";
                                const cntContent = _.template(
                                    $(tmplName).html())({
                                        count: submission.observationCount
                                    });
                                $("#search-observation-count-" + submission.dashboardEntity.id).html(cntContent);
                                const centerId = submission.dashboardEntity.observationTemplate.submissionCenter.id;
                                centerCounter.add(centerId);
                            });

                            const sTable = $("#searched-submissions").dataTable({
                                "columns": [
                                    null,
                                    {
                                        "orderDataType": "dashboard-date"
                                    },
                                    null,
                                    null,
                                    null,
                                    null
                                ]
                            });
                            sTable.fnSort([
                                [4, 'desc'],
                                [2, 'desc']
                            ]);
                            $("#searched-submissions").parent().find('input[type=search]').popover(table_filter_popover);
                            $("#searched-submissions").find('thead th:contains("Tier")').popover({
                                placement: "top",
                                trigger: 'hover',
                                html: true,
                                content: function () {
                                    return __ctd2_hovertext.ALL_TIERS;
                                },
                            });
                            if (submissions.length == 1) {
                                $('#submission-summary').text('one matched submission');
                            } else {
                                $('#submission-summary').text(submissions.length + ' matched submissions');

                            }
                            if (centerCounter.size == 1) {
                                $('#center-summary').text('one center');
                            } else {
                                $('#center-summary').text(centerCounter.size + ' centers');
                            }
                        } else {
                            $('#submission-summary-link').hide();
                        }

                        tabulate_matching_observations(matching_observations);
                        if (matching_observations.length == 0) {
                            $('#observation-summary-link').hide();
                        } else {
                            $('#observation-count').text(matching_observations.length);
                        }
                    }
                    $('.clickable-popover').popover({
                        placement: "bottom",
                        trigger: 'hover',
                    }).click(function () {
                        $(this).popover('hide');
                    });
                }
            });

            return this;
        }
    });

    const SummaryItemView = Backbone.View.extend({
        template: _.template($("#summary-item-tmpl").html()),
        render: function () {
            $(this.el).append(this.template(this.model));
        },
    });

    //MRA View
    const MraView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#mra-view-tmpl").html()),
        render: function () {
            const result = this.model.toJSON();
            const mra_data_url = $("#mra-view-tmpl").attr("mra-data-url") + result.evidence.filePath.replace(/\\/g, '/');
            $(this.el).html(this.template(result));
            $.ajax({
                url: "mra-data/",
                data: {
                    url: mra_data_url,
                    dataType: "mra",
                    filterBy: "none",
                    nodeNumLimit: 0,
                    throttle: ""
                },
                dataType: "json",
                contentType: "json",

                success: function (data) {
                    const thatEl = $("#master-regulator-grid");
                    const thatE2 = $("#mra-barcode-grid");
                    _.each(data, function (aData) {
                        new MraViewRowView({
                            el: $(thatEl).find("tbody"),
                            model: aData
                        }).render();

                        new MraBarcodeRowView({
                            el: $(thatE2).find("tbody"),
                            model: aData
                        }).render();

                    });

                    $(thatEl).dataTable({
                        "sDom": "<'fullwidth'ifrtlp>",
                        "sScrollY": "200px",
                        "bPaginate": false
                    });

                    $(thatE2).dataTable();
                }
            }); //ajax 

            $(".mra-cytoscape-view").click(function (event) {
                event.preventDefault();
                const mraDesc = $(this).attr("data-description");
                const layoutName = $("#cytoscape-layouts").val();
                const nodeLimit = $("#cytoscape-node-limit").val();

                let filters = "";
                $('input[type="checkbox"]:checked').each(function () {
                    filters = filters + ($(this).val() + ',');
                });

                if (filters.length == 0) {
                    showAlertMessage("Please select at least one master regulator.");
                    return;
                }

                $.ajax({
                    url: "mra-data/",
                    data: {
                        url: mra_data_url,
                        dataType: "cytoscape",
                        filterBy: filters,
                        nodeNumLimit: nodeLimit,
                        throttle: ""
                    },
                    dataType: "json",
                    contentType: "json",
                    success: function (data) {

                        if (data == null) {
                            showAlertMessage("The network is empty.");
                            return;
                        }

                        $.fancybox.open(
                            _.template($("#mra-cytoscape-tmpl").html())({
                                description: mraDesc
                            }), {
                            touch: false,
                            'autoDimensions': false,
                            'transitionIn': 'none',
                            'transitionOut': 'none'
                        }
                        );

                        window.cy = this;
                        cytoscape({
                            container: $('#mra-cytoscape'),

                            layout: {
                                name: layoutName,
                                fit: true,
                                liveUpdate: false,
                                maxSimulationTime: 8000, // max length in ms to run the layout
                                stop: function () {
                                    $("#mra_progress_indicator").hide();
                                    this.stop();
                                } // callback on layoutstop 

                            },
                            elements: data,
                            style: cytoscape.stylesheet()
                                .selector("node")
                                .css({
                                    "content": "data(id)",
                                    "shape": "data(shape)",
                                    "border-width": 2,
                                    "labelValign": "middle",
                                    "font-size": 10,
                                    "width": "25px",
                                    "height": "25px",
                                    "background-color": "data(color)",
                                    "border-color": "#555"
                                })
                                .selector("edge")
                                .css({
                                    "width": "mapData(weight, 0, 100, 1, 3)",
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
                                    "border-width": 2
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

                            ready: function () {
                                // for debugging
                            }
                        });

                    }
                }); //end ajax

            }); //end .cytoscape-view

            $("#master-regulator-grid").on("change", ":checkbox", function () {
                let filters = "";
                $('input[type="checkbox"]:checked').each(function () {
                    filters = filters + ($(this).val() + ',');
                });

                $.ajax({
                    url: "mra-data/",
                    data: {
                        url: mra_data_url,
                        dataType: "throttle",
                        filterBy: filters,
                        nodeNumLimit: $("#cytoscape-node-limit").val(),
                        throttle: ""
                    },
                    dataType: "json",
                    contentType: "json",
                    success: function (data) {
                        if (data != null)
                            $("#throttle-input").text(data);
                        else
                            $("#throttle-input").text("e.g. 0.01");
                        $("#throttle-input").css('color', 'grey');
                    }
                });


            }); //end mra-checked  

            $("#cytoscape-node-limit").change(function (evt) {
                //the following block code is same as above, shall make it as function,
                //but for somehow the function call does not work here for me. 
                let filters = "";
                $('input[type="checkbox"]:checked').each(function () {
                    filters = filters + ($(this).val() + ',');
                });

                $.ajax({
                    url: "mra-data/",
                    data: {
                        url: mra_data_url,
                        dataType: "throttle",
                        filterBy: filters,
                        nodeNumLimit: $("#cytoscape-node-limit").val(),
                        throttle: ""
                    },
                    dataType: "json",
                    contentType: "json",
                    success: function (data) {
                        if (data != null)
                            $("#throttle-input").text(data);
                        else
                            $("#throttle-input").text("e.g. 0.01");
                        $("#throttle-input").css('color', 'grey');
                    }
                });

            });
            $('.clickable-popover').popover({
                placement: "bottom",
                trigger: 'hover',
            }).click(function () {
                $(this).popover('hide');
            });

            return this;
        }
    });

    const MraViewRowView = Backbone.View.extend({
        render: function () {
            this.template = _.template($("#mra-view-row-tmpl").html());
            $(this.el).append(this.template(this.model));

            return this;
        }
    });

    const MraBarcodeRowView = Backbone.View.extend({
        render: function () {
            const result = this.model;

            this.template = _.template($("#mra-barcode-view-row-tmpl").html());
            $(this.el).append(this.template(result));

            if (result.daColor != null)
                $(".da-color-" + result.entrezId).css({
                    "background-color": result.daColor
                });

            if (result.deColor != null)
                $(".de-color-" + result.entrezId).css({
                    "background-color": result.deColor
                });

            const ctx = document.getElementById("draw-" + result.entrezId).getContext("2d");

            _.each(result.mraTargets, function (mraTarget) {

                const colorIndex = 255 - mraTarget.colorIndex;
                if (mraTarget.arrayIndex == 0) {
                    ctx.fillStyle = 'rgb(255,' + colorIndex + ',' + colorIndex + ')';
                    ctx.fillRect(mraTarget.position, 0, 1, 15);
                } else {
                    ctx.fillStyle = 'rgb(' + colorIndex + ', ' + colorIndex + ', 255)';
                    ctx.fillRect(mraTarget.position, 15, 1, 15);
                }

            });

            return this;
        }
    });

    const reformattedClassName = {
        "Gene": "gene",
        "AnimalModel": "animal model",
        "Compound": "compound",
        "CellSample": "cell sample",
        "TissueSample": "tissue sample",
        "ShRna": "shRNA",
        "Transcript": "transcript",
        "Protein": "protein",
    };

    const table_filter_popover = {
        placement: "top",
        trigger: 'hover',
        content: __ctd2_hovertext.TABLE_FILTER,
    };

    const ExploreView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#explore-tmpl").html()),

        render: function () {
            const thatModel = this.model;
            thatModel.rolesLabel = _.map(decodeURIComponent(thatModel.roles).split(","), uppercase_roles, []).join(", ");
            $(this.el).html(this.template(thatModel));
            const data_url = $("#explore-tmpl").attr("data-url");
            const subjectWithSummaryCollection = new SubjectWithSummaryCollection(thatModel);
            subjectWithSummaryCollection.fetch({
                success: function () {
                    $("#explore-items").html("");

                    const table_data = [];
                    _.each(subjectWithSummaryCollection.models, function (subjectWithSummary) {
                        const sModel = subjectWithSummary.toJSON();
                        const subject = sModel.subject;
                        if (subject.class == "Compound") {
                            _.each(subject.xrefs, function (xref) {
                                if (xref.databaseName == "IMAGE") {
                                    subject.imageFile = xref.databaseId;
                                }
                            });
                        }
                        const role = sModel.role;
                        let reformatted = reformattedClassName[subject.class];
                        if (subject.class == 'Compound') {
                            reformatted += " <span style='display:inline-block;width:100px'><a href='" + data_url + "compounds/" +
                                subject.imageFile + "' target='_blank' class='compound-image clickable-popover' data-content='Compound: " +
                                subject.displayName + "'><img class='img-polaroid' style='height:25px' src='" + data_url + "compounds/" +
                                subject.imageFile + "' alt='Compound: " + subject.displayName + "'></a></span>";
                        } else {
                            reformatted += " <img src='img/" + subject.class.toLowerCase() + ".png' style='height:25px' alt=''>";
                        }
                        const nameLink = "<a href='#" + subject.stableURL + "/" + role + "'>" + subject.displayName + "</a>";
                        const n3obv = sModel.numberOfTier3Observations;
                        const n3ctr = sModel.numberOfTier3SubmissionCenters;
                        const n3link = (n3obv == 0 ? "" : "<a href='#" + subject.stableURL + "/" + role + "/3'>" + n3obv + "</a>") +
                            (n3obv > 0 ? " (" + n3ctr + " center" + (n3ctr > 1 ? "s" : "") + ")" : "");
                        const n2obv = sModel.numberOfTier2Observations;
                        const n2ctr = sModel.numberOfTier2SubmissionCenters;
                        const n2link = (n2obv == 0 ? "" : "<a href='#" + subject.stableURL + "/" + role + "/2'>" + n2obv + "</a>") +
                            (n2obv > 0 ? " (" + n2ctr + " center" + (n2ctr > 1 ? "s" : "") + ")" : "");
                        const n1obv = sModel.numberOfTier1Observations;
                        const n1ctr = sModel.numberOfTier1SubmissionCenters;
                        const n1link = (n1obv == 0 ? "" : "<a href='#" + subject.stableURL + "/" + role + "/1'>" + n1obv + "</a>") +
                            (n1obv > 0 ? " (" + n1ctr + " center" + (n1ctr > 1 ? "s" : "") + ")" : "");
                        table_data.push([reformatted, nameLink, "<span class=subject_role>" + role + "</span>", n3link, n2link, n1link]);
                    });
                    $("#explore-table").dataTable({
                        'dom': '<iBfrtlp>',
                        'data': table_data,
                        "deferRender": true,
                        "columns": [
                            null,
                            null,
                            null,
                            {
                                "type": "observation-count"
                            },
                            {
                                "type": "observation-count"
                            },
                            {
                                "type": "observation-count"
                            }
                        ],
                        "drawCallback": function (settings) {
                            $("a.compound-image").fancybox({
                                titlePosition: 'inside'
                            });
                        },
                        'buttons': [{
                            extend: 'excelHtml5',
                            text: 'Export as Spreadsheet',
                            className: "extra-margin",
                        }],
                    }).on('draw.dt', function () {
                        $(".subject_role").popover({
                            placement: "top",
                            trigger: 'hover',
                            content: function () {
                                return ctd2_role_definition[$(this).text()];
                            },
                        });
                    });
                    $("#explore-table").parent().width("100%");
                    $("#explore-table").width("100%");
                    $("#explore-table").parent().find('input[type=search]').popover(table_filter_popover);
                    $(".subject_role").popover({
                        placement: "top",
                        trigger: 'hover',
                        content: function () {
                            return ctd2_role_definition[$(this).text()];
                        },
                    });

                    $('#explore-table thead th').popover({
                        placement: "top",
                        trigger: 'hover',
                        content: function () {
                            const hovertext_id = 'EXPLORE_' + $(this).text().toUpperCase().replace(' ', '_');
                            return __ctd2_hovertext[hovertext_id];
                        },
                    });

                    const blurb = $("#text-blurb");
                    if (blurb.length > 0) {
                        $("#explore-blurb").append(_.template(blurb.html())({
                            subject_type: subjectType[thatModel.type],
                            roles: thatModel.roles
                        }));
                        $("#explore-blurb .blurb-help").click(function (e) {
                            e.preventDefault();
                            (new HelpNavigateView()).render();
                        });
                    }
                    $("#reset-ordering").popover({
                        placement: "top",
                        trigger: 'hover',
                        content: __ctd2_hovertext.EXPLORE_RESET_ORDER,
                    });
                    $("#reset-ordering").click(function () {
                        $("#explore-table").DataTable().order.neutral().draw();
                    });
                    $('.clickable-popover').popover({
                        placement: "bottom",
                        trigger: 'hover',
                    }).click(function () {
                        $(this).popover('hide');
                    });
                }
            });

            $("#customize-roles").popover({
                placement: "top",
                trigger: 'hover',
                content: __ctd2_hovertext["EXPLORE_SELECT_ROLES_" + thatModel.type.toUpperCase()],
            });
            $("#customize-roles").click(function (e) {
                e.preventDefault();

                const subjectRoles = new SubjectRoles();
                subjectRoles.fetch({
                    success: function () {
                        $("#customized-roles-tbl tbody").html("");

                        const currentRoles = decodeURIComponent(thatModel.roles.toLowerCase());
                        _.each(subjectRoles.models, function (role) {
                            role = role.toJSON();
                            if (browseRole[thatModel.type].indexOf(role.displayName) == -1) return;
                            const checked = currentRoles.search(role.displayName.toLowerCase()) > -1;
                            role.checked = checked;
                            const roleName = role.displayName;
                            role.displayName = roleName.charAt(0).toUpperCase() + roleName.slice(1);
                            const customRoleItemView = new CustomRoleItemView({
                                model: role
                            });
                            customRoleItemView.render();
                        });

                        $("#role-modal").modal('show');

                        $("#role-modal").on('hidden.bs.modal', function (e) {
                            const newRoles = [];
                            $("#role-modal input").each(function () {
                                const aRole = $(this).attr("data-role");
                                if ($(this).prop("checked")) {
                                    newRoles.push(aRole);
                                }
                            });
                            window.location.hash = "/explore/" + thatModel.type + "/" + newRoles.join(",");
                        });
                    }
                });
            });

            return this;
        }
    });

    const browseRole = {
        target: ["background", "biomarker", "candidate master regulator", "interactor", "master regulator", "oncogene", "target"],
        compound: ["candidate drug", "control compound", "perturbagen"],
        context: ["disease", "metastasis", "tissue"],
        cellline: ["cell line",],
    };

    const subjectType = {
        target: "Biomarkers, Targets, Genes & Proteins (genes)",
        compound: "Compounds and Perturbagens (compounds, shRNA, genes)",
        context: "Disease Context (tissues)",
        cellline: "Cell Lines",
    };

    //customize-roles-item-tmpl
    const CustomRoleItemView = Backbone.View.extend({
        el: "#customized-roles-tbl tbody",
        template: _.template($("#customize-roles-item-tmpl").html()),

        render: function () {
            if (this.model.checked) {
                $(this.el).prepend(this.template(this.model));
            } else {
                $(this.el).append(this.template(this.model));
            }
            return this;
        }
    });

    const uppercase_roles = function (role) {
        if (role == 'Metastasis') return 'Metastases';
        return role + "s";
    };

    const updateGeneList = function (addedGene) {
        let geneNames = JSON.parse(localStorage.getItem("genelist"));
        if (geneNames == null)
            geneNames = [];

        if (geneNames.length >= numOfCartGene) {
            showAlertMessage("Gene Cart can only contains " + numOfCartGene + " genes.");
            return;
        }

        if (geneNames.indexOf(addedGene) > -1) {
            showAlertMessage(addedGene + " is already in the Gene Cart.");
        } else {
            //Not in the array
            geneNames.push(addedGene);
            localStorage.genelist = JSON.stringify(geneNames);
            showAlertMessage(addedGene + " added to the Gene Cart.");
        }
    };

    const showAlertMessage = function (message) {
        $("#alertMessage").text(message);
        $("#alertMessage").css('color', '#5a5a5a');
        $("#alert-message-modal").modal('show');
    };

    const showInvalidMessage = function (message) {
        $("#alertMessage").text(message);
        $("#alertMessage").css('color', 'red');
        $("#alert-message-modal").modal('show');
    };

    const viewOnlyRouter = function (View) {
        return function () {
            new View().render();
        };
    };

    const idBasedRouter = function (Model, View) {
        return function (id) {
            const model = new Model({
                id: id
            });
            model.fetch({
                success: function () {
                    new View({
                        model: model,
                    }).render();
                }
            });
        };
    };

    const subjectRouter = function (SubjectModel, SubjectView) {
        return function (name, role, tier) {
            const model = new SubjectModel({
                id: name
            });
            model.fetch({
                success: function () {
                    new SubjectView({
                        model: {
                            subject: model,
                            tier: tier,
                            role: role
                        }
                    }).render();
                }
            });
        };
    };

    /* Routers */
    const AppRouter = Backbone.Router.extend({
        routes: {
            "centers": viewOnlyRouter(CenterListView),
            "stories": viewOnlyRouter(StoriesListView),
            "eco_browse": viewOnlyRouter(EcoBrowseView),
            "explore/:type/:roles": "explore",
            "center/:name(/:project)": "showCenter",
            "submission/:id": idBasedRouter(Submission, SubmissionView),
            "observation/:id": idBasedRouter(Observation, ObservationView),
            "search/:term": "search",
            "story/:submission_name": "showStory",
            "animal-model/:name(/:role)(/:tier)": subjectRouter(AnimalModel, AnimalModelView),
            "cell-sample/:name(/:role)(/:tier)": subjectRouter(CellSample, CellSampleView),
            "compound/:name(/:role)(/:tier)": subjectRouter(Compound, CompoundView),
            "protein/:name(/:role)(/:tier)": subjectRouter(Protein, ProteinView),
            "tissue/:name(/:role)(/:tier)": subjectRouter(TissueSample, TissueSampleView),
            "transcript/:name(/:role)(/:tier)": subjectRouter(Transcript, TranscriptView),
            "rna/:name(/:role)(/:tier)": subjectRouter(ShRna, RnaView),
            "gene/:species/:symbol(/:role)(/:tier)": "showGene",
            "eco/:code(/:tier)": function (code, tier) {
                const eco_model = new ECOTerm({
                    id: code,
                });
                eco_model.fetch({
                    success: function () {
                        new ECOTermView({
                            model: {
                                ecoterm: eco_model,
                                tier: tier,
                            }
                        }).render();
                    },
                    error: function () {
                        console.log('ECO term ' + code + " (tier=" + tier + ") not returned");
                    },
                });
            },
            "mra/:filename": idBasedRouter(ObservedEvidence, MraView),
            "genes": viewOnlyRouter(GeneListView),
            "cnkb-query": viewOnlyRouter(CnkbQueryView),
            "cnkb-result": viewOnlyRouter(CnkbResultView),
            "gene-cart-help": viewOnlyRouter(GeneCartHelpView),
            "cite": viewOnlyRouter(HowToCiteView),
            "*actions": "home",
        },

        home: function (actions) {
            const homepageText = new HomepageText();
            homepageText.fetch({
                success: function () {
                    new HomeView({
                        model: homepageText.toJSON(),
                    }).render();
                },
            });
        },

        search: function (term) {
            new SearchView({
                model: {
                    term: decodeURI(term)
                        .replace(new RegExp("<", "g"), "")
                        .replace(new RegExp(">", "g"), "")
                }
            }).render();
        },

        explore: function (type, roles) {
            new ExploreView({
                model: {
                    roles: roles.replace(new RegExp("<", "g"), "").replace(new RegExp(">", "g"), ""),
                    type: type.replace(new RegExp("<", "g"), "").replace(new RegExp(">", "g"), ""),
                    customized: false
                }
            }).render();
        },

        showStory: function (submission_name) {
            const storySubmissions = new StorySubmissions({
                limit: -1
            });
            storySubmissions.fetch({
                success: function () {
                    // only one match is expected
                    _.each(storySubmissions.models, function (aStory) {
                        const observation = aStory.toJSON();
                        const id = observation.submission.stableURL.replace("submission/", "");
                        if (id !== submission_name) {
                            //console.log("NOT MATCH"); // no-op
                            return;
                        }
                        // if it is the queried submission, ...
                        const observedEvidences = new ObservedEvidences({
                            observationId: observation.id
                        });
                        observedEvidences.fetch({
                            success: function () {
                                let url = "";

                                _.each(observedEvidences.models, function (observedEvidence) {
                                    observedEvidence = observedEvidence.toJSON();

                                    if (observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                        return;

                                    if (observedEvidence.evidence.class == "FileEvidence" &&
                                        (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1 || observedEvidence.evidence.mimeType.toLowerCase().search("pdf") > -1)) {
                                        // If this is a summary, then it should be a pdf/html file evidence
                                        if (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1) {
                                            url = $("#explore-tmpl").attr("data-url") + observedEvidence.evidence.filePath.replace(/\\/g, '/');
                                        } else {
                                            console.log(observedEvidence.evidence.mimeType + ": pdf case, no handled. ");
                                        }
                                    }
                                });

                                new HtmlStoryView({
                                    model: {
                                        observation: observation,
                                        url: url,
                                    }
                                }).render();
                            }
                        });
                    });
                }
            });
        },

        showGene: function (species, symbol, role, tier) {
            const gmodel = new Gene({
                species: species,
                symbol: symbol
            });
            gmodel.fetch({
                success: function () {
                    new GeneView({
                        model: {
                            subject: gmodel,
                            tier: tier,
                            role: role
                        }
                    }).render();
                }
            });
        },

        showCenter: function (name, project) {
            const center = new SubmissionCenter({
                id: name
            });
            center.fetch({
                success: function () {
                    if (project != null) {
                        project = decodeURI(project)
                            .replace(new RegExp("<", "g"), "")
                            .replace(new RegExp(">", "g"), "");
                    }
                    new CenterView({
                        model: center
                    }).render(project);
                }
            });
        },
    });

    $(function () {
        new AppRouter();
        Backbone.history.start();

        $("#omnisearch").submit(function () {
            window.location.hash = "search/" + encodeURI(encodeURIComponent($("#omni-input").val()));
            return false;
        });

        $("#omni-input").popover({
            placement: "bottom",
            trigger: "manual",
            html: true,
            title: function () {
                $(this).attr("title");
            },
            content: function () {
                return $("#search-help-content").html();
            },
        }).on("mouseenter", function () {
            const _this = this;
            $(this).popover("show");
            $(".popover").on("mouseleave", function () {
                $(_this).popover('hide');
            });
        }).on("mouseleave", function () {
            const _this = this;
            setTimeout(function () {
                if (!$(".popover:hover").length) {
                    $(_this).popover("hide");
                }
            }, 300);
        });

        $("a.help-navigate").click(function (e) {
            e.preventDefault();
            (new HelpNavigateView()).render();
        });
    });

})(window.jQuery);