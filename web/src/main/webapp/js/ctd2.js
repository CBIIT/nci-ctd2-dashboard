!function ($) {
    // This is strictly coupled to the homepage design!
    var numOfStoriesHomePage = 4;

    // These seperators are for replacing items within the observation summary
    var leftSep = "<";
    var rightSep = ">";

    // To make URL constructing more configurable
    var CORE_API_URL = "./";

    // This is for the moustache-like templates
    // prevents collisions with JSP tags <%...%>
    _.templateSettings = {
        interpolate : /\{\{(.+?)\}\}/g
    };

    // Get these options from the page
    var maxNumberOfEntities = $("#maxNumberOfEntites").html() * 1;

    // Datatables fix
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    });

    $.extend( true, $.fn.dataTable.defaults, {
        "oLanguage": { // "search" -> "filter"
            "sSearch": "Filter Table:"
        },
        "search": { // simple searching
            "smart": false
        },
        // These are for bootstrap-styled datatables
        "sDom": "<'row'<'span6'i><'span6'f>r>t<'row'<'span6'l><'span6'p>>",
        "sPaginationType": "bootstrap"
    });

    // Let datatables know about our date format
    $.extend($.fn.dataTable.ext.order, {
        "dashboard-date": function(settings, col) {
            return this.api().column( col, {order:'index'} ).nodes().map(
                function(td, i) {
                    return (new Date($('a', td).html())).getTime();
                }
            );
        }
    });

    // Let datatables know about our date format
    $.extend($.fn.dataTable.ext.order, {
        "dashboard-rank": function(settings, col) {
            return this.api().column( col, {order:'index'} ).nodes().map(
                function(td, i) {
                    return $('ul', td).attr("data-score");
                }
            );
        }
    });


    /* Models */
    var SubmissionCenter = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/center"
    });
    var SubmissionCenters = Backbone.Collection.extend({
        url: CORE_API_URL + "list/center/?filterBy=",
        model: SubmissionCenter
    });

    var Submission = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/submission"
    });

    var CenterSubmissions = Backbone.Collection.extend({
        url: CORE_API_URL + "list/submission/?filterBy=",
        model: Submission,

        initialize: function(attributes) {
            this.url += attributes.centerId;
        }
    });

    var StorySubmissions = Backbone.Collection.extend({
        url: CORE_API_URL + "stories/?limit=",
        model: Submission,

        initialize: function(attributes) {
            if(attributes != undefined && attributes.limit != undefined) {
                this.url += attributes.limit;
            } else {
                this.url += numOfStoriesHomePage;
            }
        }

    });

    var Observation = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observation"
    });

    var Observations = Backbone.Collection.extend({
        url: CORE_API_URL + "list/observation/?filterBy=",
        model: Observation,

        initialize: function(attributes) {
            if(attributes.subjectId != undefined) {
                this.url += attributes.subjectId;
            } else {
                this.url += attributes.submissionId;
            }

            if(attributes.getAll != undefined) {
                this.url += "&getAll=" + attributes.getAll;
            }
        }
    });

    var BrowsedItems = Backbone.Collection.extend({
        url: CORE_API_URL + "browse/",
        defaults: {
            type: "target",
            character: "A"
        },

        initialize: function(attributes) {
            var attr = _.extend(this.defaults, attributes);
            this.url += attr.type + "/" + attr.character;
        }
    });

    var ObservedEvidence = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observedevidence"
    });

    var ObservedEvidences = Backbone.Collection.extend({
        url: CORE_API_URL + "list/observedevidence/?filterBy=",
        model: ObservedEvidence,

        initialize: function(attributes) {
            this.url += attributes.observationId;
        }
    });

    var ObservedSubject = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observedsubject"
    });

    var ObservedSubjects = Backbone.Collection.extend({
        url: CORE_API_URL + "list/observedsubject/?filterBy=",
        model: ObservedSubject,

        initialize: function(attributes) {
            if(attributes.subjectId != undefined) {
                this.url += attributes.subjectId;
            } else {
                this.url += attributes.observationId;
            }
        }
    });

    var SearchResult = Backbone.Model.extend({});

    var SearchResults = Backbone.Collection.extend({
        url: CORE_API_URL + "search/",
        model: SearchResult,

        initialize: function(attributes) {
            this.url += encodeURIComponent(attributes.term.toLowerCase())
        }
    });

    var Subject = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/subject"
    });

    var SubjectWithSummary = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/subject"
    });

    var SubjectWithSummaryCollection = Backbone.Collection.extend({
        url: CORE_API_URL + "explore/",
        model: SubjectWithSummary,

        initialize: function(attributes) {
            this.url += attributes.roles;
        }
    });

    /* Views */
    var HomeView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#home-tmpl").html()),
        render: function() {
            // Load the template
            $(this.el).html(this.template({}));

            // and load the stories
            var storySubmissions = new StorySubmissions();
            storySubmissions.fetch({
                success: function() {
                    var counter = 1;
                    _.each(storySubmissions.models, function(aStory) {
                        var storyView = new StorySubmissionView({
                            el: $("#story-" + counter),
                            model: aStory.toJSON()
                        });
                        storyView.render();
                        counter++;
                    });

                    Holder.run();

                    $('.stories-pagination a.story-link').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    })
                }
            });

            $('#myCarousel').carousel('pause');
            $("#omni-search-form").submit(function() {
                var searchTerm = $("#omni-search").val();
                window.location.hash = "search/" + searchTerm;
                return false;
            });

            $("a.show-more").click(function(e) {
                e.preventDefault();
                $("#overview-hidden-part").slideDown();
                $(this).hide();
                $("a.show-less").show();
            });
            $("a.show-less").click(function(e) {
                e.preventDefault();
                $("#overview-hidden-part").slideUp();
                $(this).hide();
                $("a.show-more").show();
            });


            return this;
        }
    });

    var HtmlStoryView = Backbone.View.extend({
        render: function() {
            var url = this.model.url;
            var observation = this.model.observation;

            $.post("html", {url: url}).done(function(summary) {
                summary = summary.replace(
                    new RegExp("#submission_center", "g"),
                    "#center/" + observation.submission.observationTemplate.submissionCenter.id
                );

                var observedSubjects = new ObservedSubjects({ observationId: observation.id });
                observedSubjects.fetch({
                    success: function() {
                        _.each(observedSubjects.models, function(observedSubject) {
                            observedSubject = observedSubject.toJSON();

                            if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                                return;

                            summary = summary.replace(
                                new RegExp("#" + observedSubject.observedSubjectRole.columnName, "g"),
                                "#subject/" + observedSubject.subject.id
                            );
                        });

                        var observedEvidences = new ObservedEvidences({ observationId: observation.id });
                        observedEvidences.fetch({
                            success: function() {
                                _.each(observedEvidences.models, function(observedEvidence) {
                                    observedEvidence = observedEvidence.toJSON();

                                    if(observedEvidence.observedEvidenceRole == null
                                        || observedEvidence.evidence == null
                                        || observedEvidence.evidence.class != "UrlEvidence")
                                    {
                                        return;
                                    }

                                    summary = summary.replace(
                                        new RegExp("#" + observedEvidence.observedEvidenceRole.columnName, "g"),
                                        observedEvidence.evidence.url.replace(/^\//, '')
                                    );
                                });

                                $.fancybox(
                                    _.template(
                                        $("#html-story-container-tmpl").html(),
                                        {
                                            story: summary,
                                            centerName: observation.submission.observationTemplate.submissionCenter.displayName
                                        }
                                    ),
                                    {
                                        'autoDimensions' : false,
                                        'width': '95%',
                                        'height': '95%',
                                        'centerOnScroll': true,
                                        'transitionIn' : 'none',
                                        'transitionOut' : 'none'
                                    }
                                );
                            }
                        });
                    }
                });
            });

            return this;

        }
    });

    var StoryListItemView = Backbone.View.extend({
        template:_.template($("#stories-tbl-row-tmpl").html()),

        render: function() {
            var mainContainer = $(this.el);
            mainContainer.append(this.template(this.model));

            var summary = this.model.submission.observationTemplate.observationSummary;
            var thatModel = this.model;
            var thatEl = $("#story-list-summary-" + this.model.id);
            var observedSubjects = new ObservedSubjects({ observationId: this.model.id });
            observedSubjects.fetch({
                success: function() {
                    _.each(observedSubjects.models, function(observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                        );
                    });

                    var observedEvidences = new ObservedEvidences({ observationId: thatModel.id });
                    observedEvidences.fetch({
                        success: function() {
                            _.each(observedEvidences.models, function(observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if(observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                // If there are more than one file evidences, then we might have a problem here
                                if(observedEvidence.evidence.class == "FileEvidence"
                                    && (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1 || observedEvidence.evidence.mimeType.toLowerCase().search("pdf") > -1)) {
                                    // If this is a summary, then it should be a pdf/html file evidence
                                    var elId = "#file-link2-" + thatModel.id;
                                    var url = $(elId).attr("href") + observedEvidence.evidence.filePath;
                                    $(elId).attr("href", url);

                                    if(observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1) {
                                        $(elId).on("click", function(e) {
                                            e.preventDefault();
                                            (new HtmlStoryView({ model: {observation: thatModel, url: url }})).render();
                                        });
                                    }
                                }

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                                );
                            });

                            $(thatEl).html(summary);
                        }
                    })
                }
            });

            $("#story-observation-link-" + thatModel.id).click(function(e) {
                e.preventDefault();

                var back = flippant.flip(
                    this,
                    $("#back-of-story-" + thatModel.id).show()[0],
                    'card'
                );

                $(back).click(function(e) {
                    back.close();
                });
            });

            return this;
        }
    });

    var StorySubmissionView = Backbone.View.extend({
        template:_.template($("#story-homepage-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));

            var summary = this.model.submission.observationTemplate.observationSummary;
            var thatModel = this.model;
            var thatEl = $("#story-summary-" + this.model.id);
            var observedSubjects = new ObservedSubjects({ observationId: this.model.id });
            observedSubjects.fetch({
                success: function() {
                    _.each(observedSubjects.models, function(observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                        );
                    });

                    var observedEvidences = new ObservedEvidences({ observationId: thatModel.id });
                    observedEvidences.fetch({
                        success: function() {
                            _.each(observedEvidences.models, function(observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if(observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                // If there are more than one file evidences, then we might have a problem here
                                if(observedEvidence.evidence.class == "FileEvidence"
                                    && (observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1 || observedEvidence.evidence.mimeType.toLowerCase().search("pdf") > -1)) {
                                    // If this is a summary, then it should be a pdf/html file evidence
                                    var elId = "#file-link-" + thatModel.id;
                                    var url = $(elId).attr("href") + observedEvidence.evidence.filePath;
                                    $(elId).attr("href", url);

                                    if(observedEvidence.evidence.mimeType.toLowerCase().search("html") > -1) {
                                        $(elId).on("click", function(e) {
                                            e.preventDefault();
                                            (new HtmlStoryView({ model: {observation: thatModel, url: url }})).render();
                                        });
                                    }
                                }

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                                );
                            });

                            $(thatEl).html(summary);
                        }
                    })
                }
            });

            return this;
        }
    });

    var ObservationView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#observation-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            $(this.el).html(this.template(result));

            // We will replace the values in this summary
            var summary = result.submission.observationTemplate.observationSummary;

            // Load Subjects
            var observedSubjects = new ObservedSubjects({ observationId: result.id });
            var thatEl = $("#observed-subjects-grid");
            observedSubjects.fetch({
                success: function() {
                    _.each(observedSubjects.models, function(observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        var observedSubjectRowView
                            = new ObservedSubjectSummaryRowView({
                            el: $(thatEl).find("tbody"),
                            model: observedSubject
                        });
                        observedSubjectRowView.render();


                        var subject = observedSubject.subject;
                        var thatEl2 = $("#subject-image-" + observedSubject.id);
                        var imgTemplate = $("#search-results-unknown-image-tmpl");
                        if(subject.class == "Compound") {
                            var compound = new Subject({id: subject.id });
                            compound.fetch({
                               success: function() {
                                   compound = compound.toJSON();
                                   _.each(compound.xrefs, function(xref) {
                                       if(xref.databaseName == "IMAGE") {
                                           compound["imageFile"] = xref.databaseId;
                                       }
                                   });

                                   imgTemplate = $("#search-results-compund-image-tmpl");
                                   thatEl2.append(_.template(imgTemplate.html(), compound));
                               }
                            });
                        } else if( subject.class == "AnimalModel" ) {
                            imgTemplate = $("#search-results-animalmodel-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "CellSample" ) {
                            imgTemplate = $("#search-results-cellsample-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "TissueSample" ) {
                            imgTemplate = $("#search-results-tissuesample-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "Gene" ) {
                            imgTemplate = $("#search-results-gene-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "ShRna" && subject.type.toLowerCase() == "sirna") {
                            subject.class == "SiRNA";
                            imgTemplate = $("#search-results-sirna-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "ShRna" ) {
                            imgTemplate = $("#search-results-shrna-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "Protein" ) {
                                imgTemplate = $("#search-results-protein-image-tmpl");
                                thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else {
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        }

                        if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                        );

                        $("#observation-summary").html(summary);
                    });
                }
            });

            // Load evidences
            var observedEvidences = new ObservedEvidences({ observationId: result.id });
            var thatEl2 = $("#observed-evidences-grid");
            observedEvidences.fetch({
                success: function() {
                    _.each(observedEvidences.models, function(observedEvidence) {
                        observedEvidence = observedEvidence.toJSON();

                        var observedEvidenceRowView = new ObservedEvidenceRowView({
                            el: $(thatEl2).find("tbody"),
                            model: observedEvidence
                        });

                        observedEvidenceRowView.render();
                        summary = summary.replace(
                            new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                            _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                        );

                        $("#observation-summary").html(summary);
                    });

                    var tableLength = (observedEvidences.models.length > 25 ? 10 : 25);
                    var oTable = $('#observed-evidences-grid').dataTable({
                        "iDisplayLength": tableLength
                    });

                    oTable.fnSort( [ [1, 'asc'], [2, 'asc'] ] );

                    $('.desc-tooltip').tooltip({ placement: "left" });

                    $("a.evidence-images").fancybox({titlePosition: 'inside'});
                    $("div.expandable").expander({
                        slicePoint: 50,
                        expandText:       '[...]',
                        expandPrefix:     ' ',
                        userCollapseText: '[^]'
                    });

                    $(".numeric-value").each(function(idx) {
                        var val = $(this).html();
                        var vals = val.split("e"); // capture scientific notation
                        if(vals.length > 1) {
                            $(this).html(_.template($("#observeddatanumericevidence-val-tmpl").html(), {
                                firstPart: vals[0],
                                secondPart: vals[1].replace("+", "")
                            }));
                        }
                    });
                    $(".cytoscape-view").click(function(event) {
                        event.preventDefault();

                        var sifUrl = $(this).attr("data-sif-url");
                        var sifDesc = $(this).attr("data-description");
                        $.ajax({
                            url: "sif/",
                            data: { url: sifUrl },
                            dataType: "json",
                            contentType: "json",
                            success: function(data) {
                                $.fancybox(
                                    _.template($("#cytoscape-tmpl").html(), { description: sifDesc }),
                                    {
                                        'autoDimensions' : false,
                                        'width' : '100%',
                                        'height' : '100%',
                                        'transitionIn' : 'none',
                                        'transitionOut' : 'none'
                                    }
                                );

                                // load cytoscape
                                //var div_id = "cytoscape-sif";

                                var container = $('#cytoscape-sif');
                                var cyOptions = {
                                    layout: {
                                        name: 'arbor',
                                        liveUpdate: false,
                                        maxSimulationTime: 1000
                                    },
                                    elements: data,
                                    style: cytoscape.stylesheet()
                                        .selector("node")
                                        .css({
                                            "content": "data(id)",
                                            "shape": "data(shape)",
                                            "border-width": 3,
                                            "background-color": "#DDD",
                                            "border-color": "#555"
                                        })
                                        .selector("edge")
                                        .css({
                                            "width": "mapData(weight, 0, 100, 1, 4)",
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
                                        })
                                    ,

                                    ready: function(){
                                        window.cy = this; // for debugging
                                    }
                                };

                                container.cy(cyOptions);
                                // end load cytoscape
                            }
                        });

                    });
                }
            });

            $("#small-show-sub-details").click(function(event) {
                event.preventDefault();
                $("#obs-submission-details").slideDown();
                $("#small-show-sub-details").hide();
                $("#small-hide-sub-details").show();
            });

            $("#small-hide-sub-details").click(function(event) {
                event.preventDefault();
                $("#obs-submission-details").slideUp();
                $("#small-hide-sub-details").hide();
                $("#small-show-sub-details").show();
            });


            if(result.submission.observationTemplate.submissionDescription == "") {
                $("#obs-submission-summary").hide();
            }

            return this;
        }
    });


    var ObservationPreviewView = Backbone.View.extend({
        template: _.template($("#observation-tmpl").html()),
        render: function() {
            var result = this.model.observation;
            $(this.el).html(this.template(result));

            // We will replace the values in this summary
            var summary = result.submission.observationTemplate.observationSummary;

            // Load Subjects
            var thatEl = $("#observed-subjects-grid");
            _.each(this.model.observedSubjects, function(observedSubject) {
                var observedSubjectRowView
                    = new ObservedSubjectSummaryRowView({
                    el: $(thatEl).find("tbody"),
                    model: observedSubject
                });
                observedSubjectRowView.render();

                var subject = observedSubject.subject;
                var thatEl2 = $("#subject-image-" + observedSubject.id);
                var imgTemplate = $("#search-results-unknown-image-tmpl");
                thatEl2.append(_.template(imgTemplate.html(), subject));

                if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                    return;

                summary = summary.replace(
                    new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                    _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                );

                $("#observation-summary").html(summary);
            });

            // Load evidences
            var thatEl2 = $("#observed-evidences-grid");
            _.each(this.model.observedEvidences, function(observedEvidence) {
                var observedEvidenceRowView = new ObservedEvidenceRowView({
                    el: $(thatEl2).find("tbody"),
                    model: observedEvidence
                });

                observedEvidenceRowView.render();
                summary = summary.replace(
                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                    _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                );

                $("#observation-summary").html(summary);
            });

            var tableLength = (this.model.observedEvidences.length > 25 ? 10 : 25);
            var oTable = $('#observed-evidences-grid').dataTable({
                "iDisplayLength": tableLength
            });

            oTable.fnSort( [ [1, 'asc'], [2, 'asc'] ] );

            $('.desc-tooltip').tooltip({ placement: "left" });
            $("div.expandable").expander({
                slicePoint: 50,
                expandText:       '[...]',
                expandPrefix:     ' ',
                userCollapseText: '[^]'
            });

            $(".numeric-value").each(function(idx) {
                var val = $(this).html();
                var vals = val.split("e"); // capture scientific notation
                if(vals.length > 1) {
                    $(this).html(_.template($("#observeddatanumericevidence-val-tmpl").html(), {
                        firstPart: vals[0],
                        secondPart: vals[1].replace("+", "")
                    }));
                }
            });

            $("#small-show-sub-details").click(function(event) {
                event.preventDefault();
                $("#obs-submission-details").slideDown();
                $("#small-show-sub-details").hide();
                $("#small-hide-sub-details").show();
            });

            $("#small-hide-sub-details").click(function(event) {
                event.preventDefault();
                $("#obs-submission-details").slideUp();
                $("#small-hide-sub-details").hide();
                $("#small-show-sub-details").show();
            });

            if(result.submission.observationTemplate.submissionDescription == "") {
                $("#obs-submission-summary").hide();
            }

            return this;
        }
    });


    var ObservedEvidenceRowView = Backbone.View.extend({
        render: function() {
            var result = this.model;
            var type = result.evidence.class;
            result.evidence["type"] = type;

            if(result.observedEvidenceRole == null) {
                result.observedEvidenceRole = {
                    displayText: "-",
                    evidenceRole: { displayName: "unknown" }
                };
            }

            var templateId = "#observedevidence-row-tmpl";
            var isHtmlStory = false;
            if(type == "FileEvidence") {
                result.evidence.filePath = result.evidence.filePath.replace(/\\/g, "/");
                if(result.evidence.mimeType.toLowerCase().search("image") > -1) {
                    templateId = "#observedimageevidence-row-tmpl";
                } else if(result.evidence.mimeType.toLowerCase().search("gct") > -1) {
                    templateId = "#observedgctfileevidence-row-tmpl";
                } else if(result.evidence.mimeType.toLowerCase().search("pdf") > -1) {
                    templateId = "#observedpdffileevidence-row-tmpl";
                } else if(result.evidence.mimeType.toLowerCase().search("sif") > -1) {
                    templateId = "#observedsiffileevidence-row-tmpl";
                } else if(result.evidence.mimeType.toLowerCase().search("mra") > -1) {
                    templateId = "#observedmrafileevidence-row-tmpl";
                } else if(result.evidence.mimeType.toLowerCase().search("html") > -1) {
                    templateId = "#observedhtmlfileevidence-row-tmpl";
                    isHtmlStory = true;
                } else {
                    templateId = "#observedfileevidence-row-tmpl";
                }
            } else if(type == "UrlEvidence") {
                templateId = "#observedurlevidence-row-tmpl";
            } else if(type == "LabelEvidence") {
                templateId = "#observedlabelevidence-row-tmpl";
            } else if(type == "DataNumericValue") {
                templateId = "#observeddatanumericevidence-row-tmpl";
            }

            this.template = _.template($(templateId).html());
            var thatEl = $(this.el);
            $(this.el).append(this.template(result));

            if(isHtmlStory) {
                thatEl.find(".html-story-link").on("click", function(e) {
                    e.preventDefault();
                    var url = $(this).attr("href");
                    (new HtmlStoryView({ model: {observation: result.observation, url: url }})).render();
                });
            }

            $(".img-rounded").tooltip({ placement: "left" });
            return this;
        }
    });

    var CenterListRowView = Backbone.View.extend({
        template:  _.template($("#centers-tbl-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }

    });

    var CenterListView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#centers-tmpl").html()),
        render: function() {
            $(this.el).html(this.template({}));

            var centers = new SubmissionCenters();
            var thatEl = this.el;
            var cTable = null;
            centers.fetch({
                success: function() {
                    var tableEl = $(thatEl).find("table");

                    _.each(centers.toJSON(), function(aCenter) {
                       var centerListRowView
                           = new CenterListRowView({ el: $(thatEl).find("#centers-tbody"), model: aCenter });
                        centerListRowView.render();

                        $.ajax("count/submission/?filterBy=" + aCenter.id).done(function(count) {
                            var cntContent = _.template(
                                $("#count-submission-tmpl").html(),
                                { count: count }
                            );

                            var countCellId = "#submission-count-" + aCenter.id;
                            $(countCellId).html(cntContent);
                            tableEl.DataTable().cells(countCellId).invalidate();
                        });

                        $.ajax("list/observationtemplate/?filterBy=" + aCenter.id).done(function(templates) {
                            var pis = [];
                            _.each(templates, function(template) {
                                pis.push(template.principalInvestigator);
                            });
                            var piCellId = "#center-pi-" + aCenter.id;
                            $(piCellId).html(_.uniq(pis).join(", "));
                            tableEl.DataTable().cells(piCellId).invalidate();
                        });
                    });

                    cTable = tableEl.dataTable({
                        // might want to increase this number if we have incredible number of centers
                        "iDisplayLength": 25
                    });

                    cTable.fnSort( [ [1, 'asc'] ] );
                }
            });
            return this;
        }
    });

    var StoriesListView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#stories-tmpl").html()),

        render: function() {
            $(this.el).html(this.template({}));

            // and load the stories
            var storySubmissions = new StorySubmissions({ limit: -1 });
            storySubmissions.fetch({
                success: function() {
                    var counter = 1;
                    _.each(storySubmissions.models, function(aStory) {
                        var storyView = new StoryListItemView({
                            el: $("#stories-container .stories-list"),
                            model: aStory.toJSON()
                        });
                        storyView.render();
                        counter++;
                    });

                    Holder.run();
                }
            });

            return this;
        }
    });

    var CenterSubmissionRowView = Backbone.View.extend({
        template:  _.template($("#center-submission-tbl-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    var SearchSubmissionRowView = Backbone.View.extend({
        el: "#searched-submissions tbody",
        template:  _.template($("#search-submission-tbl-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });


    var SubmissionDescriptionView = Backbone.View.extend({
        el: "#optional-submission-description",
        template:  _.template($("#submission-description-tmpl").html()),
        render: function() {
            $(this.el).html(this.template(this.model));
            return this;
        }
    });

    var CompoundView = Backbone.View.extend({
         el: $("#main-container"),
         template:  _.template($("#compound-tmpl").html()),
         render: function() {
             var result = this.model.toJSON();

             _.each(result.xrefs, function(xref) {
                 if(xref.databaseName == "IMAGE") {
                     result["imageFile"] = xref.databaseId;
                 }

             });
             result["type"] = result.class;

             $(this.el).html(this.template(result));

             var thatEl = $("ul.synonyms");
             _.each(result.synonyms, function(aSynonym) {
                 if(aSynonym.displayName == result.displayName ) return;

                 var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                 synonymView.render();
             });

             var subjectObservationView = new SubjectObservationsView({
                 model: result.id,
                 el: "#compound-observation-grid"
             });
             subjectObservationView.render();

             $("a.compound-image").fancybox({titlePosition: 'inside'});
             return this;
         }
     });

    var SubjectObservationsView = Backbone.View.extend({
        render: function() {
            var thatEl = $(this.el);
            var subjectId = this.model;

            $.ajax("count/observation/?filterBy=" + subjectId).done(function(count) {
                var observations = new Observations({ subjectId: subjectId });
                observations.fetch({
                    success: function () {
                        $(".subject-observations-loading", thatEl).remove();
                        _.each(observations.models, function (observation) {
                            observation = observation.toJSON();

                            var observationRowView
                                = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                            observationRowView.render();
                        });

                        var oTable = $(thatEl).dataTable({
                            "sPaginationType": "bootstrap"
                        });

                        oTable.fnSort([
                            [2, 'desc']
                        ]);

                    }
                });

                if(count > maxNumberOfEntities) {
                    var moreObservationView = new MoreObservationView({
                        model: {
                            numOfObservations: maxNumberOfEntities,
                            numOfAllObservations: count,
                            subjectId: subjectId,
                            tableEl: thatEl,
                            rowView: ObservationRowView,
                            columns: [
                                { "orderDataType": "dashboard-date" },
                                null,
                                null,
                                null
                            ]
                        }
                    });
                    moreObservationView.render();
                }
            });

            return this;
        }
    });

    var GeneView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#gene-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;
            $(this.el).html(this.template(result));

            var thatEl = $("ul.synonyms");
            _.each(result.synonyms, function(aSynonym) {
                if(aSynonym.displayName == result.displayName ) return;

                var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                synonymView.render();
            });

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#gene-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });

    var ProteinView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#protein-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;
            $(this.el).html(this.template(result));

            var thatEl = $("ul.synonyms");
            _.each(result.synonyms, function(aSynonym) {
                if(aSynonym.displayName == result.displayName ) return;

                var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                synonymView.render();
            });

            thatEl = $("ul.transcripts");
            _.each(result.transcripts, function(aTranscript) {
                var transcriptItemView = new TranscriptItemView({ model: aTranscript, el: thatEl });
                transcriptItemView.render();
            });


            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#protein-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });

    var ShrnaView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#shrna-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;
            $(this.el).html(this.template(result));

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#shrna-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });

    var SirnaView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#sirna-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = "sirna";
            $(this.el).html(this.template(result));

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#sirna-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });

    var TranscriptView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#transcript-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;
            $(this.el).html(this.template(result));

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#transcript-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });

    var TissueSampleView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#tissuesample-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;
            $(this.el).html(this.template(result));

            var thatEl = this.el;
            if(result.xrefs.length == 0) { $(thatEl).find("#tissue-refs").hide(); }
            _.each(result.xrefs, function(xref) {
                //if(xref.databaseName == "NCI_PARENT_THESAURUS" || xref.databaseName == "NCI_THESAURUS") {
                if(xref.databaseName == "NCI_THESAURUS") {
                    var ids = xref.databaseId.split(";");
                    _.each(ids, function(xrefid) {
                        $(thatEl).find("ul.xrefs").append(
                            _.template($("#ncithesaurus-tmpl").html(), { nciId: xrefid })
                        );
                    });
                }
            });

            if(result.synonyms.length == 0) { $(thatEl).find("#tissue-synonyms").hide(); }
            var thatEl = $("ul.synonyms");
            _.each(result.synonyms, function(aSynonym) {
                if(aSynonym.displayName == result.displayName ) return;

                var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                synonymView.render();
            });

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#tissuesample-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });


    var AnimalModelView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#animalmodel-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;
            $(this.el).html(this.template(result));

            var thatEl = $("ul.synonyms");
            _.each(result.synonyms, function(aSynonym) {
                if(aSynonym.displayName == result.displayName ) return;

                var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                synonymView.render();
            });

            var thatEl2 = $("#annotations ul");
            _.each(result.annotations, function(annotation) {
                annotation.displayName = annotation.displayName.replace(/_/g, " ");
                var annotationView = new AnnotationView({ model: annotation, el: thatEl2 });
                annotationView.render();
            });

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#animalmodel-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });


    var AnnotationView = Backbone.View.extend({
        template: _.template($("#annotation-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
        }
    });

    var CellSampleView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#cellsample-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();
            result["type"] = result.class;

            // Look for cbioPortal Id
            var cbioPortalId = null;
            _.each(result.xrefs, function(xref) {
                if(xref.databaseName == "CBIO_PORTAL") {
                    cbioPortalId = xref.databaseId;
                }
            });

            result["cbioPortalId"] = cbioPortalId;
            result["type"] = result.class;

            $(this.el).html(this.template(result));

            var thatEl = $("ul.synonyms");
            _.each(result.synonyms, function(aSynonym) {
                if(aSynonym.displayName == result.displayName ) return;

                var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                synonymView.render();
            });

            var thatEl2 = $("#annotations ul");
            _.each(result.annotations, function(annotation) {
                annotation.displayName = annotation.displayName.replace(/_/g, " ");
                var annotationView = new AnnotationView({ model: annotation, el: thatEl2 });
                annotationView.render();
            });

            var subjectObservationView = new SubjectObservationsView({
                model: result.id,
                el: "#cellsample-observation-grid"
            });
            subjectObservationView.render();

            return this;
        }
    });

    var ObservationRowView = Backbone.View.extend({
        template: _.template($("#observation-row-tmpl").html()),
        render: function() {
            var tableEl = this.el;
            $(tableEl).append(this.template(this.model));
            var summary = this.model.submission.observationTemplate.observationSummary;

            var thatModel = this.model;
            var cellId = "#observation-summary-" + this.model.id;
            var thatEl = $(cellId);
            var observedSubjects = new ObservedSubjects({ observationId: this.model.id });
            observedSubjects.fetch({
                success: function() {
                    _.each(observedSubjects.models, function(observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                        );
                    });

                    var observedEvidences = new ObservedEvidences({ observationId: thatModel.id });
                    observedEvidences.fetch({
                        success: function() {
                            _.each(observedEvidences.models, function(observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if(observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                                );
                            });

                            summary += _.template($("#submission-obs-tbl-row-tmpl").html(), thatModel);
                            $(thatEl).html(summary);
                            var dataTable = $(tableEl).parent().DataTable();
                            dataTable.cells(cellId).invalidate();
                            dataTable.order([
                                [2, 'desc'],
                                [0, 'desc'],
                                [1, 'asc']
                            ]).draw();
                        }
                    })
                }
            });

            return this;
        }
    });

    var ObservedSubjectRowView = Backbone.View.extend({
        template:  _.template($("#observedsubject-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    var ObservedSubjectSummaryRowView = Backbone.View.extend({
        template:  _.template($("#observedsubject-summary-row-tmpl").html()),
        render: function() {
            var result = this.model;
            if(result.subject == null) return;
            if(result.subject.type == undefined) {
                result.subject["type"] = result.subject.class;
            }
            $(this.el).append(this.template(result));
            return this;
        }
    });

    var CenterView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#center-tmpl").html()),
        render: function() {
            $(this.el).html(this.template(this.model.toJSON()));

            var thatEl = this.el;
            var centerSubmissions = new CenterSubmissions({ centerId: this.model.get("id") });
            centerSubmissions.fetch({
                success: function() {
                    var tableElId = '#center-submission-grid';
                    _.each(centerSubmissions.toJSON(), function(submission) {
                        var centerSubmissionRowView
                            = new CenterSubmissionRowView({ el: $(thatEl).find("tbody"), model: submission });
                        centerSubmissionRowView.render();

                        $.ajax("count/observation/?filterBy=" + submission.id).done(function(count) {
                            var cntContent = _.template(
                                $("#count-observations-tmpl").html(),
                                { count: count }
                            );

                            var countCellId = "#observation-count-" + submission.id;
                            $(countCellId).html(cntContent);
                            var dataTable = $(tableElId).DataTable();
                            dataTable.cells(countCellId).invalidate();
                            dataTable.order(
                                [
                                    [2, 'desc'],
                                    [0, 'desc'],
                                    [3, 'asc']
                                ]
                            ).draw();
                        });
                    });

                    $(".template-description").tooltip();
                    $(tableElId).dataTable({
                       "columns": [
                           { "orderDataType": "dashboard-date" },
                           null,
                           null,
                           null
                       ]
                    }).fnSort( [[2, 'desc']] );
                }
            });

            return this;
        }
    });

    var SubmissionRowView = Backbone.View.extend({
        template:  _.template($("#submission-tbl-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            var sTable = $(this.el).parent();

            var summary = this.model.submission.observationTemplate.observationSummary;

            var thatModel = this.model;
            var cellId = "#submission-observation-summary-" + this.model.id;
            var thatEl = $(cellId);
            var observedSubjects = new ObservedSubjects({ observationId: this.model.id });
            observedSubjects.fetch({
                success: function() {
                    _.each(observedSubjects.models, function(observedSubject) {
                        observedSubject = observedSubject.toJSON();

                        if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                            _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                        );
                    });

                    var observedEvidences = new ObservedEvidences({ observationId: thatModel.id });
                    observedEvidences.fetch({
                        success: function() {
                            _.each(observedEvidences.models, function(observedEvidence) {
                                observedEvidence = observedEvidence.toJSON();

                                if(observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                                    return;

                                summary = summary.replace(
                                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                                    _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                                );
                            });

                            summary += _.template($("#submission-obs-tbl-row-tmpl").html(), thatModel);
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

    var SubmissionView =  Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#submission-tmpl").html()),
        render: function() {
            var submission = this.model.toJSON();
            $(this.el).html(this.template(submission));

            if(submission.observationTemplate.submissionDescription.length > 0) {
                var submissionDescriptionView = new SubmissionDescriptionView({ model: submission });
                submissionDescriptionView.render();
            }

            var thatEl = this.el;
            var submissionId = this.model.get("id");
            var sTable = '#submission-observation-grid';

            $.ajax("count/observation/?filterBy=" + submissionId).done(function(count) {
                var observations = new Observations({ submissionId: submissionId });
                observations.fetch({
                    success: function() {
                        $(".submission-observations-loading").hide();

                        // If there is only one observation, directly go there
                        if(observations.models.length == 1) {
                            $("#redirect-message").slideDown();

                            var countBack = 5;
                            var clickedCancel = false;
                            $("#cancel-redirect").click(function(e) {
                                e.preventDefault();
                                clickedCancel = true;
                                $("#redirect-message").slideUp();
                            });

                            var countBackFunc = function() {
                                if(clickedCancel) return;

                                $("#seconds-left").text(countBack);
                                if(countBack-- < 1) {
                                    var observation =  observations.models[0].toJSON().id;
                                    window.location.hash = "observation/" + observation;
                                } else {
                                    window.setTimeout(countBackFunc, 1000);
                                }
                            };

                            countBackFunc();
                        }


                        _.each(observations.models, function(observation) {
                            observation = observation.toJSON();

                            var submissionRowView = new SubmissionRowView({
                                el: $(thatEl).find(".observations tbody"),
                                model: observation,
                                attributes: { table: sTable }
                            });
                            submissionRowView.render();
                        });

                        $(sTable).dataTable();

                    }
                });

                if(count > maxNumberOfEntities) {
                    var moreObservationView = new MoreObservationView({
                        model: {
                            numOfObservations: maxNumberOfEntities,
                            numOfAllObservations: count,
                            submissionId: submissionId,
                            tableEl: sTable,
                            rowView: SubmissionRowView,
                            columns: [ null ]
                        }
                    });
                    moreObservationView.render();
                }
            });

            return this;
        }
    });

    var MoreObservationView = Backbone.View.extend({
        el: ".more-observations-message",
        template: _.template($("#more-observations-tmpl").html()),
        render: function() {
            var model = this.model;
            var thatEl = this.el;
            $(thatEl).html(this.template(model));
            $(thatEl).find("a.load-more-observations").click(function(e) {
                e.preventDefault();
                $(thatEl).slideUp();

                $(".submission-observations-loading").show();
                var sTableId = model.tableEl;

                var observations;
                if(model.submissionId != undefined) {
                    observations = new Observations({ submissionId: model.submissionId, getAll: true });
                } else if(model.subjectId != undefined) {
                    observations = new Observations({ subjectId: model.subjectId, getAll: true });
                } else {
                    console.log("something is wrong here!");
                }
                observations.fetch({
                    success: function() {
                        $(sTableId).DataTable().rows().remove().draw().destroy();

                        _.each(observations.models, function(observation, i) {
                            observation = observation.toJSON();

                            var submissionRowView = new model.rowView({
                                el: $(model.tableEl).find("tbody"),
                                model: observation
                            });
                            submissionRowView.render();
                        });

                        $(sTableId).dataTable({
                            "columns": model.columns
                        });

                    }
                })
            });
        }
    });

    var SubmissionPreviewView =  Backbone.View.extend({
        el: "#submission-preview",
        template: _.template($("#submission-tmpl").html()),
        render: function() {
            var submission = this.model.submission;
            $(this.el).html(this.template(submission));
            $(".submission-observations-loading").remove();

            if(submission.observationTemplate.submissionDescription.length > 0) {
                var submissionDescriptionView = new SubmissionDescriptionView({ model: submission });
                submissionDescriptionView.render();
            }
            var thatEl = this.el;
            _.each(this.model.observations, function(observation) {
                var submissionRowView = new SubmissionRowPreviewView({
                    el: $(thatEl).find(".observations tbody"),
                    model: observation
                });
                submissionRowView.render();
            });

            $('#submission-observation-grid').dataTable();

            return this;
        }
    });

    var SubmissionRowPreviewView = Backbone.View.extend({
        template:  _.template($("#submission-tbl-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model.observation));

            var summary = this.model.observation.submission.observationTemplate.observationSummary;

            var thatModel = this.model.observation;
            var thatEl = $("#submission-observation-summary-" + this.model.observation.id);
            _.each(this.model.observedSubjects, function(observedSubject) {
                if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                    return;

                summary = summary.replace(
                    new RegExp(leftSep + observedSubject.observedSubjectRole.columnName + rightSep, "g"),
                    _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                );
            });

            _.each(this.model.observedEvidences, function(observedEvidence) {
                if(observedEvidence.observedEvidenceRole == null || observedEvidence.evidence == null)
                    return;

                summary = summary.replace(
                    new RegExp(leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep, "g"),
                    _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                );
            });

            summary += _.template($("#submission-obs-tbl-row-tmpl").html(), thatModel);
            $(thatEl).html(summary);

            return this;
        }
    });

    var TranscriptItemView = Backbone.View.extend({
        template: _.template($("#transcript-item-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });


    var SynonymView = Backbone.View.extend({
        template: _.template($("#synonym-item-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    var RoleView = Backbone.View.extend({
        template: _.template($("#role-item-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    var EmptyResultsView = Backbone.View.extend({
        template: _.template($("#search-empty-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));

            return this;
        }
    });

    var SearchResultsRowView = Backbone.View.extend({
        template: _.template($("#search-result-row-tmpl").html()),
        render: function() {
            var model = this.model;
            var result = model.dashboardEntity;
            result["type"] = result.class;

            var tableElId = this.el;
            $(tableElId).append(this.template(model));

            var thatEl = $("#synonyms-" + result.id);
            _.each(result.synonyms, function(aSynonym) {
                var synonymView = new SynonymView({model: aSynonym, el: thatEl});
                synonymView.render();
            });

            var thatEl = $("#roles-" + result.id);
            _.each(model.roles, function(aRole) {
                var roleView = new RoleView({model: {role: aRole}, el: thatEl});
                roleView.render();
            });

            thatEl = $("#search-image-" + result.id);
            var imgTemplate = $("#search-results-unknown-image-tmpl");
            if(result.class == "Compound") {
                _.each(result.xrefs, function(xref) {
                    if(xref.databaseName == "IMAGE") {
                        result["imageFile"] = xref.databaseId;
                    }
                });
                imgTemplate = $("#search-results-compund-image-tmpl");
            } else if( result.class == "CellSample" ) {
                imgTemplate = $("#search-results-cellsample-image-tmpl");
            } else if( result.class == "TissueSample" ) {
                imgTemplate = $("#search-results-tissuesample-image-tmpl");
            } else if( result.class == "Gene" ) {
                imgTemplate = $("#search-results-gene-image-tmpl");
            } else if( result.class == "ShRNA" && result.type.toLowerCase() == "sirna" ) {
                imgTemplate = $("#search-results-sirna-image-tmpl");
            } else if( result.class == "ShRNA" ) {
                imgTemplate = $("#search-results-shrna-image-tmpl");
            } else if( result.class == "Protein" ) {
                imgTemplate = $("#search-results-protein-image-tmpl");
            }
            thatEl.append(_.template(imgTemplate.html(), result));

            // some of the elements will be hidden in the pagination. Use magic-scoping!
            var updateElId = "#subject-observation-count-" + result.id;
            var updateEl = $(updateElId);
            var cntContent = _.template(
                $("#count-observations-tmpl").html(),
                { count: model.observationCount }
            );
            updateEl.html(cntContent);

            return this;
        }
    });

    var SearchView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#search-tmpl").html()),
        render: function() {
            $(this.el).html(this.template(this.model));

            // update the search box accordingly
            $("#omni-input").val(decodeURIComponent(this.model.term));

            var thatEl = this.el;
            var thatModel = this.model;
            var searchResults = new SearchResults({ term: this.model.term });

            searchResults.fetch({
                success: function() {
                    $("#loading-row").remove();
                    if(searchResults.models.length == 0) {
                        (new EmptyResultsView({ el: $(thatEl).find("tbody"), model: thatModel})).render();
                    } else {
                        var submissions = [];
                        _.each(searchResults.models, function(aResult) {
                            aResult = aResult.toJSON();
                            if(aResult.dashboardEntity.organism == undefined) {
                                aResult.dashboardEntity.organism = { displayName: "-" };
                            }

                            if(aResult.dashboardEntity.class == "Submission") {
                                submissions.push(aResult);
                                return;
                            }

                            var searchResultsRowView = new SearchResultsRowView({
                                model: aResult,
                                el: $(thatEl).find("tbody")
                            });
                            searchResultsRowView.render();
                        });

                        $(".search-info").tooltip({ placement: "left" });
                        $(".obs-tooltip").tooltip();

                        var oTable = $("#search-results-grid").dataTable({
                            "columns": [
                                null,
                                null,
                                null,
                                null,
                                { "orderDataType": "dashboard-rank" },
                                null
                            ]

                        });
                        oTable.fnSort( [[4, 'desc'], [5, 'desc'], [1, 'asc']] );

                        // OK done with the subjects; let's build the submissions table
                        if(submissions.length > 0) {
                            $("#submission-search-results").fadeIn();

                            _.each(submissions, function(submission) {
                                var searchSubmissionRowView = new SearchSubmissionRowView({ model: submission });
                                searchSubmissionRowView.render();

                                var cntContent = _.template(
                                    $("#count-observations-tmpl").html(),
                                    { count: submission.observationCount }
                                );
                                $("#search-observation-count-" + submission.dashboardEntity.id).html(cntContent);
                            });

                            var sTable = $("#searched-submissions").dataTable({
                                "columns": [
                                    null,
                                    { "orderDataType": "dashboard-date" },
                                    null,
                                    null,
                                    null,
                                    null
                                ]
                            });
                            sTable.fnSort( [ [4, 'desc'], [2, 'desc'] ] );
                        }
                    }
                }
            });

            return this;
        }
    });

    var BrowseView = Backbone.View.extend({
        template: _.template($("#browse-tmpl").html()),
        el: $("#main-container"),

        render: function() {
            $(this.el).html(this.template(this.model));

            var str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for(var i = 0; i < str.length; i++) {
                var nextChar = str.charAt(i);

                $("#browse-pagination ul.nav").append(
                    _.template(
                        $("#browse-pagination-template").html(),
                        {
                            className: this.model.character == nextChar ? "active" : "",
                            character: nextChar,
                            type: this.model.type
                        }
                    )
                );
            }

            var browsedItems = new BrowsedItems(this.model);
            browsedItems.fetch({
                success: function() {
                    if(browsedItems.models.length < 1) {
                        $("#noitems-to-browse").slideDown();
                    }

                    _.each(browsedItems.models, function(browseItem) {
                        (new BrowsedItemView({
                            el: $("#browsed-items-list"),
                            model: browseItem.toJSON()
                        })).render();

                    });

                    $(".loading").hide();
                }
            });

            return this;
        }
    });

    var BrowsedItemView = Backbone.View.extend({
        template: _.template($("#browsed-item-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));

            var observedSubjects = new ObservedSubjects({ subjectId: this.model.id });
            var thatId = this.model.id;

            var actions = {
                success: function() {
                    $("#browsed-item-count-" + thatId).text(observedSubjects.models.length);
                },

                error: function() {
                    observedSubjects.fetch(actions);
                }
            };

            observedSubjects.fetch(actions);
        }
    });
    
    //MRA View
    var MraView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#mra-view-tmpl").html()),
        render: function() { 
        	var result = this.model.toJSON();         
        	var mra_data_url = $("#mra-view-tmpl").attr("mra-data-url")  + result.evidence.filePath;
            $(this.el).html(this.template(result));            
            $.ajax({
               url: "mra/",
               data: {url : mra_data_url, dataType : "mra", filterBy: "none", nodeNumLimit: 0, throttle : ""},
               dataType: "json",
               contentType: "json",
               
               success: function(data) {            	      
            	   var thatEl = $("#master-regulator-grid");   
            	   var thatE2 = $("#mra-barcode-grid");   
                   _.each(data, function(aData){                   	 
                   	    var mraViewRowView = new MraViewRowView({
                           el: $(thatEl).find("tbody"),
                            model: aData
                        });
                        mraViewRowView.render();
                      
                        var mraBarcodeRowView = new MraBarcodeRowView({
                            el: $(thatE2).find("tbody"),
                            model: aData
                        });
                        mraBarcodeRowView.render();                   
                      
                   });            
                 
                   var oTable1 = $('#master-regulator-grid').dataTable({
                	 "sScrollY": "200px",
                     "bPaginate": false           		 
             	   });
                 
                 
                   var oTable2 = $('#mra-barcode-grid').dataTable();
              }
           });  //ajax 
       
         
           $(".mra-cytoscape-view").click(function(event) {            	
                event.preventDefault();               
                var mraDesc = $(this).attr("data-description");
                var throttle = $("#throttle-input").text();               
                var layoutName = $("#cytoscape-layouts").val();
                var nodeLimit = $("#cytoscape-node-limit").val();
              
                var filters = "";              
                $('input[type="checkbox"]:checked').each(function() {                 
                	    filters = filters + ($(this).val() + ',');                 	   
                });   
                
               
                if (filters.length == 0) {
                	  alert("Please select at least one master regulator.");
                      return;
                }
               
                $.ajax({
                	url: "mra/",
                    data: {url : mra_data_url, dataType : "cytoscape", filterBy: filters, nodeNumLimit: nodeLimit, throttle : ""},
                    dataType: "json",
                    contentType: "json",
                    success: function(data) {   
                    	
                    	if (data == null)
                        {
                    		alert("The network is empty.");
                    		return;
                        }
                    	
                        $.fancybox(
                            _.template($("#mra-cytoscape-tmpl").html(), { description: mraDesc }),
                            {
                                'autoDimensions' : false,
                                'width' : '100%',
                                'height' : '85%',
                                'transitionIn' : 'none',
                                'transitionOut' : 'none'
                            }
                        );
 
                        var container = $('#cytoscape-mra');                        
                        
                        var cyOptions = {                        	             	 
                            layout: {
                            	 name: layoutName,
                            	 fit: true,                                                  	 
                            	 liveUpdate: false,                       
                            	 maxSimulationTime: 8000, // max length in ms to run the layout                        
                            	 stop: function(){
                            		 $("#mra_progress_indicator").hide();
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
                                })
                            ,

                            ready: function(){
                                window.cy = this; // for debugging
                            }
                        };  

                        container.cy(cyOptions); 
                  
                    }
                });  //end ajax              
              

            });  //end .cytoscape-view         
           
            $("#master-regulator-grid").on("change", ":checkbox", function() {           	 
            	 var nodeLimit = $("#cytoscape-node-limit").val();
            	 var filters = "";
                 $('input[type="checkbox"]:checked').each(function() {                 
             	    filters = filters + ($(this).val() + ',');             	    
                 });             
             
                 $.ajax({
                 	url: "mra/",
                     data: {url : mra_data_url, dataType : "throttle", filterBy: filters, nodeNumLimit: nodeLimit, throttle : ""},
                     dataType: "json",
                     contentType: "json",
                     success: function(data) {                     	 
                         if (data != null)                                	 
                            $("#throttle-input").text(data);                      
                         else
                            $("#throttle-input").text("e.g. 0.01");
                         $("#throttle-input").css('color', 'grey');                          
                     }
                 });
                

            });  //end mra-checked  
            
            $("#cytoscape-node-limit").change(function(evt) {            	 
            	//the following block code is same as above, shall make it as function,
            	//but for somehow the function call does not work here for me. 
            	 var nodeLimit = $("#cytoscape-node-limit").val();
            	 var filters = "";
                 $('input[type="checkbox"]:checked').each(function() {                 
             	    filters = filters + ($(this).val() + ',');             	    
                 });             
             
                 $.ajax({
                 	url: "mra/",
                     data: {url : mra_data_url, dataType : "throttle", filterBy: filters, nodeNumLimit: nodeLimit, throttle : ""},
                     dataType: "json",
                     contentType: "json",
                     success: function(data) {                     	 
                         if (data != null)                                	 
                            $("#throttle-input").text(data);                      
                         else
                            $("#throttle-input").text("e.g. 0.01");
                         $("#throttle-input").css('color', 'grey');                          
                     }
                 });
                
            });
            
 
            return this;
        }
    });      
   
    var MraViewRowView = Backbone.View.extend({
        render: function() {
            var result = this.model;
            
            var templateId = "#mra-view-row-tmpl";     

            this.template = _.template($(templateId).html());
            $(this.el).append(this.template(result));

            
            return this;
        }
    });
    
    
    var MraBarcodeRowView = Backbone.View.extend({
        render: function() {
            var result = this.model;
            
            var templateId = "#mra-barcode-view-row-tmpl";     

            this.template = _.template($(templateId).html());
            $(this.el).append(this.template(result));            
          
            if (result.daColor != null)
                $(".da-color-" + result.entrezId).css({"background-color": result.daColor});           
            
            if (result.deColor != null)
                $(".de-color-" + result.entrezId).css({"background-color": result.deColor});           
           
            
            var canvasId = "draw-" + result.entrezId;            
            var ctx = document.getElementById(canvasId).getContext("2d");
            
            _.each(result.mraTargets, function(mraTarget){
            	
            	var colorIndex = 255 - mraTarget.colorIndex;             
            	if (mraTarget.arrayIndex == 0)
            	{            		
            		ctx.fillStyle = 'rgb(255,'+colorIndex+','+colorIndex+')';
            		ctx.fillRect(mraTarget.position, 0, 1, 15);
            	}
            	else
            	{
            		ctx.fillStyle = 'rgb('+colorIndex+', '+colorIndex+', 255)';
            		ctx.fillRect(mraTarget.position, 15, 1, 15);
            	}
             
            	
            });
            
            return this;
        }
    });
    
    
    var TemplateHelperView = Backbone.View.extend({
        template: _.template($("#template-helper-tmpl").html()),
        el: $("#main-container"),
        table: "#template-table",
        metaTable: "#template-meta-table",
        preview: "#template-preview",

        addColumn: function(identifier, displayTextEditable, columnType) {
            $(this.table).find("tr")
                .append(_.template($("#template-header-col-tmpl").html(), {id: identifier, columnType: columnType}));
            $(this.table).find("#template-header td").last().text(identifier);
            var inputTemplate = _.template($("#template-sample-data-tmpl").html(), {});
            $(this.table).find("tr.sample-data td." + identifier).append(inputTemplate);

            if(displayTextEditable)
                $(this.table).find("#template-display_text td." + identifier).append(inputTemplate);

            return this;
        },

        addMetaColumn: function(identifier, displayText) {
            $(this.metaTable).find("#meta-" + identifier).text(displayText);

            return this;
        },

        render: function() {
            $(this.el).html(this.template(this.model));

            var submissionCenters = new SubmissionCenters();
            submissionCenters.fetch({
                success: function() {
                    _.each(submissionCenters.models, function(aCenter) {
                        (new TemplateHelperCenterView({
                            model: aCenter.toJSON(),
                            el: $("#template-submission-centers")
                        })).render();
                    });
                }
            });

            var self = this;
            $("#apply-submission-center").click(function() {
                var centerName = $("#template-submission-centers").val();
                if(centerName.length == 0) {
                    centerName = $("#template-submission-centers-custom").val();
                }

                if(centerName.length == 0) {
                    return; // error control
                }

                $("#step1").fadeOut();
                $("#step2").slideDown();
                self.addColumn("submission_center", false, "meta");
                $(self.table).find("tr.sample-data td.submission_center").each(function() {
                    $(this).find("input").val(centerName);
                });
            });

            $("#apply-template-name").click(function() {
                var tmplName = $("#template-name").val();
                if(tmplName.length == 0) return;

                $("#step2").fadeOut();
                $("#step3").slideDown();
                self.addColumn("template_name", false, "meta");
                $(self.table).find("tr.sample-data td.template_name").each(function() {
                    $(this).find("input").val(tmplName);
                });
                var dateString = (function(date) {
                    var d = date.getDate();
                    var m = date.getMonth() + 1;
                    var y = date.getFullYear();
                    return '' + y +  (m<=9 ? '0' + m : m) + (d <= 9 ? '0' + d : d);
                })(new Date());
                self.addMetaColumn("template_name", tmplName);
                self.addMetaColumn("submission_name", dateString + "-" + tmplName);
            });

            $("#apply-template-desc").click(function() {
                var templDesc = $("#template-desc").val();
                var templSubmissionDesc = $("#template-submission-desc").val();

                if(templDesc.length == 0 || templSubmissionDesc.length == 0) {
                    return; // error control
                }

                $("#step3").fadeOut();
                $("#step4").slideDown();
                self.addMetaColumn("template_description", templDesc);
                self.addMetaColumn("submission_description", templSubmissionDesc);
            });

            $("#apply-template-tier").click(function() {
                var tmplTier = $("#template-tier").val();
                self.addMetaColumn("observation_tier", tmplTier);

                $("#step4").fadeOut();
                $("#step5").slideDown();
                $(self.preview).slideDown();
            });


            $("#add-evidence").click(function() {
                $("#evidence-modal").modal('show');
            });

            // The following variables are shared across evidence/subject add buttons
            var stype, cname, role, mime, unit;

            $("#apply-evidence-type").click(function() {
                stype = $("#evidence-type").val();
                if(stype.length == 0) return;

                $("#evidence-step1").fadeOut();
                if(stype == "File") {
                    $("#evidence-step1-mime").slideDown();
                } else if(stype == "Numeric") {
                    $("#evidence-step1-unit").slideDown();
                } else {
                    $("#evidence-step2").slideDown();
                }
            });

            $("#apply-evidence-mime-type").click(function() {
                mime = $("#evidence-mime-type").val();
                if(mime.length == 0) {
                    mime = $("#evidence-mime-type-custom").val();
                }

                if(mime.length == 0) {
                    return; // error control
                }

                $("#evidence-step1-mime").fadeOut();
                $("#evidence-step2").slideDown();
            });

            $("#apply-evidence-numeric-unit").click(function() {
                unit = $("#evidence-numeric-unit").val();
                $("#evidence-step1-unit").fadeOut();
                $("#evidence-step2").slideDown();
            });

            $("#apply-evidence-cname").click(function() {
                cname = $("#evidence-cname").val();
                if(cname.length == 0) {
                    cname = $("#evidence-cname-custom").val();
                }

                if(cname.length == 0) {
                    return; // error control
                }

                $("#evidence-step2").fadeOut();
                $("#evidence-step3").slideDown();
            });

            $("#apply-evidence-role").click(function() {
                role = $("#evidence-role").val();
                if(role.length == 0) {
                    role = $("#evidence-role-custom").val();
                }

                if(role.length == 0) {
                    return; // error control
                }

                $("#evidence-step3").fadeOut();
                $("#evidence-step4").slideDown();
            });

            $("#apply-evidence-desc").click(function() {
                var desc = $("#evidence-desc").val();
                if(desc.length == 0) return;

                self.addColumn(cname, true, "evidence");

                $(self.table).find("#template-evidence td." + cname).text(stype);
                $(self.table).find("#template-role td." + cname).text(role);
                $(self.table).find("#template-display_text td." + cname + " input").val(desc);
                $(self.table).find("tr.sample-data td." + cname + " input").val(stype + " value");
                $(self.table).find("#template-mime_type td." + cname).text(mime);
                $(self.table).find("#template-numeric_units td." + cname).text(unit);

                $("#evidence-step4").hide();
                $("#evidence-step1").show();

                $("#evidence-modal").modal('hide');

            });

            $("#add-subject").click(function() {
                $("#subject-modal").modal('show');
            });

            $("#apply-subject-type").click(function() {
                stype = $("#subject-type").val();
                if(stype.length == 0) return;

                $("#subject-step1").fadeOut();
                $("#subject-step2").slideDown();
            });

            $("#apply-subject-cname").click(function() {
                cname = $("#subject-cname").val();
                if(cname.length == 0) {
                    cname = $("#subject-cname-custom").val();
                }

                if(cname.length == 0) {
                    return; // error control
                }

                $("#subject-step2").fadeOut();
                $("#subject-step3").slideDown();
            });

            $("#apply-subject-role").click(function() {
                role = $("#subject-role").val();
                if(role.length == 0) {
                    role = $("#subject-role-custom").val();
                }

                if(role.length == 0) {
                    return; // error control
                }

                $("#subject-step3").fadeOut();
                $("#subject-step4").slideDown();
            });

            $("#apply-subject-desc").click(function() {
                var desc = $("#subject-desc").val();
                if(desc.length == 0) return;

                self.addColumn(cname, true, "subject");
                $(self.table).find("#template-subject td." + cname).text(stype);
                $(self.table).find("#template-role td." + cname).text(role);
                $(self.table).find("#template-display_text td." + cname + " input").val(desc);
                $(self.table).find("tr.sample-data td." + cname + " input").val(stype + " ID");

                $("#subject-step4").hide();
                $("#subject-step1").show();

                $("#subject-modal").modal('hide');

            });

            $("#download-template").click(function() {
                self.addMetaColumn("observation_summary", $("#template-obs-summary").val());
                return this;
            });

            $("#preview-template").click(function() {
                self.addMetaColumn("observation_summary", $("#template-obs-summary").val());

                $.fancybox(
                    _.template($("#preview-tmpl").html()),
                    {
                        'autoDimensions' : false,
                        'width' : '100%',
                        'height' : '100%',
                        'transitionIn' : 'none',
                        'transitionOut' : 'none'
                    }
                );

                var mockId = 1;

                // create submission center
                var submissionCenter = {
                    'class': "SubmissionCenter",
                    displayName: $(self.table).find("tr.sample-data td.submission_center input").val(),
                    id: mockId++
                };
    
                // create template
                var observationTemplate = {
                    'class': "observationTemplate",
                    description: $("#meta-template_description").text(),
                    displayName: $("#meta-submission_name").text(),
                    id: mockId++,
                    isSubmissionStory: false,
                    observationSummary: $("#meta-observation_summary").text(),
                    submissionDescription: $("#meta-submission_description").text(),
                    submissionName: $("#meta-submission_name").text(),
                    submissionStoryRank: 0,
                    tier: $("#meta-observation_tier").text()
                };

                // create submission
                var submission = {
                    'class': "Submission",
                    displayName: $("#meta-submission_name").text(),
                    id: mockId++,
                    submissionDate: (new Date()).toDateString(),
                    observationTemplate: observationTemplate,
                    submissionCenter: submissionCenter
                };

                // create observations
                var createObservations = function(id, preview) {
                    var rowId = "#" + "template-sample-data" + id;

                    var observation = {
                        'class': "Observation",
                        id: mockId++,
                        displayName: "",
                        submission: submission
                    };

                    var observedSubjects = [];
                    var observedEvidences = [];

                    $("#template-header td").each(function(i, aCell) {
                        var cellType = $(aCell).data("type");
                        var className = $(aCell).text();

                        if( i < 1 || cellType == "meta" )
                            return; // this will get rid of non-essential cells

                        var displayText = $("#template-display_text").find("td." + className + " input").val();
                        var displayName = $(rowId).find("td." + className + " input").val();
                        switch(cellType) {
                            case "subject":
                                var subject = {
                                    'class': $("#template-subject").find("td." + className).text(),
                                    id: mockId++,
                                    displayName: displayName
                                };

                                var subjectRole = {
                                    'class': "SubjectRole",
                                    id: mockId++,
                                    displayName: $("#template-role").find("td." + className).text()
                                };

                                var observedSubjectRole = {
                                    subjectRole: subjectRole,
                                    observationTemplate: observationTemplate,
                                    id: mockId++,
                                    'class': "ObservedSubjectRole",
                                    columnName: className,
                                    displayName: displayText,
                                    displayText: displayText
                                };

                                var observedSubject = {
                                    'class': "ObservedSubject",
                                    displayName: subject.displayName,
                                    id: mockId++,
                                    observation: observation,
                                    observedSubjectRole: observedSubjectRole,
                                    subject: subject
                                };

                                observedSubjects.push(observedSubject);

                                break;
                            case "evidence":
                                var evidence = {
                                    'class': $("#template-evidence").find("td." + className).text(),
                                    id: mockId++,
                                    displayName: displayName,
                                    mimeType: $("#template-evidence").find("td." + className).text(),
                                    filePath: displayName,
                                    url: displayName
                                };

                                var evidenceRole = {
                                    'class': "EvidenceRole",
                                    id: mockId++,
                                    displayName: $("#template-role").find("td." + className).text()
                                };

                                var observedEvidenceRole = {
                                    evidenceRole: evidenceRole,
                                    observationTemplate: observationTemplate,
                                    id: mockId++,
                                    'class': "ObservedEvidenceRole",
                                    columnName: className,
                                    displayName: displayText,
                                    displayText: displayText
                                };

                                var observedEvidence = {
                                    'class': "ObservedEvidence",
                                    displayName: evidence.displayName,
                                    id: mockId++,
                                    observation: observation,
                                    observedEvidenceRole: observedEvidenceRole,
                                    evidence: evidence
                                };

                                observedEvidences.push(observedEvidence);
                                break;
                        }
                    });

                    var returnObject = {
                        observation: observation,
                        observedEvidences: observedEvidences,
                        observedSubjects: observedSubjects
                    };

                    if(preview) {
                        (new ObservationPreviewView({
                            model: returnObject,
                            el: $("#obs" + id + "-preview")
                        })).render();
                    }

                    return returnObject;
                };

                var obs1 = createObservations(1, true);
                var obs2 = createObservations(2, false);

                // Create the submission preview
                (new SubmissionPreviewView({
                    model: {
                        submission: submission,
                        observations: [obs1, obs2]
                    }
                })).render();

                $("#preview-container div.common-container").removeClass("common-container");
                $('#preview-tabs a').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });

                return this;
            });

            $("#download-form").submit(function() {
                var table2TSV = function(id) {
                    var text = "";

                    $(id).find("tr").each(function(i, aRow) {
                        var cells = $(aRow).children();
                        cells.each(function(j, aCell) {
                            var input = $(aCell).find("input");
                            if($(aCell).find("i").length > 0) {
                                text += "";
                            } else if(input.length == 0) {
                                text += $(aCell).text();
                            } else {
                                text += $(input).val();
                            }

                            if((j+1) < cells.length) {
                                text += "\t";
                            }
                        });

                        text += "\n";
                    });

                    return text;
                };

                $("#template-input").val(table2TSV("#template-table"));
                $("#template-meta-input").val(table2TSV("#template-meta-table"));
                $("#filename-input").val($("#meta-submission_name").text());

                return true;
            });


            return this;
        }
    });

    var TemplateHelperCenterView = Backbone.View.extend({
        template: _.template($("#template-helper-center-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    var ExploreView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#explore-tmpl").html()),

        render: function() {
            var exploreLimit = 36;

            var thatModel = this.model;
            $(this.el).html(this.template(thatModel));
            var subjectWithSummaryCollection = new SubjectWithSummaryCollection(thatModel);
            subjectWithSummaryCollection.fetch({
                success: function() {
                    $("#explore-items").html("");

                    var numberOfEls = subjectWithSummaryCollection.models.length;
                    var spanSize = 2;
                    for(order=1; order <= numberOfEls; order++) {
                        var subjectWithSummary = subjectWithSummaryCollection.models[order];
                        if(order < exploreLimit) {
                            var sModel = subjectWithSummary.toJSON();
                            sModel["spanSize"] = spanSize;
                            sModel["type"] = thatModel.type;
                            sModel["order"] = order;
                            if(sModel.subject.class == "Compound") {
                                _.each(sModel.subject.xrefs, function(xref) {
                                    if(xref.databaseName == "IMAGE") {
                                        sModel.subject["imageFile"] = xref.databaseId;
                                    }
                                });
                            }
                            var exploreItemView = new ExploreItemView({ model: sModel });
                            exploreItemView.render();
                        } else {
                            var exploreMoreItemView = new ExploreMoreItemView({
                                model: {
                                    shown: order-1,
                                    known: numberOfEls,
                                    type: thatModel.type
                                }
                            });
                            exploreMoreItemView.render();

                            $("#show-more-" + thatModel.type).click(function(e) {
                                e.preventDefault();
                                $(this).fadeOut();

                                for(var j=order; j <  numberOfEls; j++) {
                                    var subjectWithSummary = subjectWithSummaryCollection.models[j];
                                    var sModel = subjectWithSummary.toJSON();
                                    sModel["spanSize"] = spanSize;
                                    sModel["type"] = thatModel.type;
                                    sModel["order"] = j;
                                    if(sModel.subject.class == "Compound") {
                                        _.each(sModel.subject.xrefs, function(xref) {
                                            if(xref.databaseName == "IMAGE") {
                                                sModel.subject["imageFile"] = xref.databaseId;
                                            }
                                        });
                                    }
                                    var exploreItemView = new ExploreItemView({ model: sModel });
                                    exploreItemView.render();
                                }
                            });

                            break;
                        }
                    }

                    $(".explore-thumbnail h4").tooltip();
                }
            });


            return this;
        }
    });

    var ExploreItemView = Backbone.View.extend({
        el: "#explore-items",
        template: _.template($("#explore-item-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });

    var ExploreMoreItemView = Backbone.View.extend({
        el: "#explore-items",
        template: _.template($("#explore-more-item-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
    });


    /* Routers */
    AppRouter = Backbone.Router.extend({
        routes: {
            "centers": "listCenters",
            "stories": "listStories",
            "browse/:type/:character": "browse",
            "explore": "scrollToExplore",
            "explore/:type/:roles": "explore",
            "center/:id": "showCenter",
            "submission/:id": "showSubmission",
            "observation/:id": "showObservation",
            "search/:term": "search",
            "subject/:id": "showSubject",
            "evidence/:id": "showMraView",
            "template-helper": "showTemplateHelper",
            "about": "about",
            "*actions": "home"
        },

        home: function(actions) {
            var homeView = new HomeView();
            homeView.render();
        },

        about: function() {
            var homeView = new HomeView();
            homeView.render();
            $("a.show-more").trigger('click');
        },

        scrollToExplore: function() {
            var homeView = new HomeView();
            homeView.render();

            var whereTo = $(".ctd2-boxes").offset().top - 5;
            $('html, body').animate({
                scrollTop: whereTo
            }, 500);
        },

        search: function(term) {
            var searchView = new SearchView({
                model: {
                    term: decodeURI(term)
                        .replace(new RegExp("<", "g"), "")
                        .replace(new RegExp(">", "g"), "")
                }
            });
            searchView.render();
        },

        browse: function(type, character) {
            var browseView = new BrowseView({
                model: {
                    type: type.replace(new RegExp("<", "g"), "").replace(new RegExp(">", "g"), ""),
                    character: character.replace(new RegExp("<", "g"), "").replace(new RegExp(">", "g"), "")
                }
            });
            browseView.render();
        },

        explore: function(type, roles) {
            var exploreView = new ExploreView({
                model: {
                    roles: roles,
                    type: type
                }
            });
            exploreView.render();
        },

        showSubject: function(id) {
            var subject = new Subject({ id: id });
            subject.fetch({
                success: function() {
                    var type = subject.get("class");
                    var subjectView;
                    if(type == "Gene") {
                        subjectView = new GeneView({ model: subject });
                    } else if(type == "AnimalModel") {
                        subjectView = new AnimalModelView({ model: subject });
                    } else if(type == "Compound") {
                        subjectView = new CompoundView({ model: subject });
                    } else if(type == "CellSample") {
                        subjectView = new CellSampleView({ model: subject });
                    } else if(type == "TissueSample") {
                        subjectView = new TissueSampleView({ model: subject });
                    } else if(type == "ShRna") {
                        // shRna covers both siRNA and shRNA
                        if(subject.get("type").toLowerCase() == "sirna") {
                            subjectView = new SirnaView({ model: subject });
                        } else {
                            subjectView = new ShrnaView({ model: subject });
                        }
                    } else if(type == "Transcript") {
                        subjectView = new TranscriptView({ model: subject });
                    } else if(type == "Protein") {
                        subjectView = new ProteinView({model: subject });
                    } else {
                        subjectView = new GeneView({ model: subject });
                    }
                    subjectView.render();
                }
            });
        },

        showCenter: function(id) {
            var center = new SubmissionCenter({id: id});
            center.fetch({
                success: function() {
                    var centerView = new CenterView({model: center});
                    centerView.render();
                }
            });
        },

        showSubmission: function(id) {
            var submission = new Submission({id: id});
            submission.fetch({
                success: function() {
                    var submissionView = new SubmissionView({model: submission});
                    submissionView.render();
                }
            });
        },

        showObservation: function(id) {
            var observation = new Observation({id: id});
            observation.fetch({
                success: function() {
                    var observationView = new ObservationView({model: observation});
                    observationView.render();
                }
            });
        },

        listCenters: function() {
            var centerListView = new CenterListView();
            centerListView.render();
        },

        listStories: function() {
            var storiesListView = new StoriesListView();
            storiesListView.render();
        },
        
        showMraView: function(id) {
        	  var observedEvidence = new ObservedEvidence({id: id});
        	  observedEvidence.fetch({
                  success: function() {
                     var mraView = new MraView({model: observedEvidence});
                     mraView.render();
                  }        
              });
        },

        showTemplateHelper: function() {
            var templateHelperView = new TemplateHelperView();
            templateHelperView.render();
        }
    });

    $(function(){
        new AppRouter();
        Backbone.history.start();

        $("#omnisearch").submit(function() {
            var searchTerm = $("#omni-input").val();
            window.location.hash = "search/" + encodeURI(encodeURIComponent(searchTerm));
            return false;
        });

        $("#omni-input").popover({
           placement: "bottom",
           trigger: "hover",
           html: true,
           title: function() {
                $(this).attr("title");
           },
           content: function() {
               return $("#search-help-content").html();
           },
           delay: {hide: 2000}
        });

    });

}(window.jQuery);
