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

    // Datatables fix
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
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
            this.url += attributes.term;
        }
    });

    var Subject = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/subject"
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

            return this;
        }
    });

    var StoryListItemView = Backbone.View.extend({
        template:_.template($("#stories-tbl-row-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));

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

                                // If this is a summary, then it should be a pdf file evidence
                                $("#file-link2-" + thatModel.id).attr(
                                    "href",
                                    $("#file-link2-" + thatModel.id).attr("href") + observedEvidence.evidence.filePath
                                );

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
                    $("#back-of-story-" + thatModel.id).html(),
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

                                // If this is a summary, then it should be a pdf file evidence
                                $("#file-link-" + thatModel.id).attr(
                                    "href",
                                    $("#file-link-" + thatModel.id).attr("href") + observedEvidence.evidence.filePath
                                );

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

                        } else if( subject.class == "CellSample" ) {
                            imgTemplate = $("#search-results-cellsample-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "TissueSample" ) {
                            imgTemplate = $("#search-results-tissuesample-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "Gene" ) {
                            imgTemplate = $("#search-results-gene-image-tmpl");
                            thatEl2.append(_.template(imgTemplate.html(), subject));
                        } else if( subject.class == "ShRna" ) {
                            imgTemplate = $("#search-results-shrna-image-tmpl");
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
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap",
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

    var ObservedEvidenceRowView = Backbone.View.extend({
        render: function() {
            var result = this.model;
            var type = result.evidence.class;
            result.evidence["type"] = type;

            if(result.observedEvidenceRole == null) {
                result.observedEvidenceRole = {
                    displayText: "N/A",
                    evidenceRole: { displayName: "unknown" }
                };
            }

            var templateId = "#observedevidence-row-tmpl";
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
            $(this.el).append(this.template(result));

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
            centers.fetch({
                success: function() {
                    _.each(centers.toJSON(), function(aCenter) {
                       var centerListRowView
                           = new CenterListRowView({ el: $(thatEl).find(".thumbnails"), model: aCenter });
                        centerListRowView.render();

                        $.ajax("count/submission/?filterBy=" + aCenter.id).done(function(count) {
                            $("#submission-count-" + aCenter.id).html(count);
                        });
                    });
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

             var observations = new Observations({ subjectId: result.id });
             thatEl = $("#compound-observation-grid");
             observations.fetch({
                 success: function() {
                     $(".subject-observations-loading").remove();
                     _.each(observations.models, function(observation) {
                         observation = observation.toJSON();

                         var observationRowView
                             = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                         observationRowView.render();
                     });

                     var oTable = $('#compound-observation-grid').dataTable({
                         "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                         "sPaginationType": "bootstrap"
                     });

                     oTable.fnSort( [ [2, 'desc'] ] );
                 }
             });

             $("a.compound-image").fancybox({titlePosition: 'inside'});
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

            var observations = new Observations({ subjectId: result.id });
            thatEl = $("#gene-observation-grid");
            observations.fetch({
                success: function() {
                    $(".subject-observations-loading").remove();
                    _.each(observations.models, function(observation) {
                        observation = observation.toJSON();

                        var observationRowView
                            = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                        observationRowView.render();
                    });

                    var oTable = $('#gene-observation-grid').dataTable({
                           "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                           "sPaginationType": "bootstrap"
                    });

                    oTable.fnSort( [ [2, 'desc'] ] );

                }
            });

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

            var observations = new Observations({ subjectId: result.id });
            thatEl = $("#shrna-observation-grid");
            observations.fetch({
                success: function() {
                    $(".subject-observations-loading").remove();
                    _.each(observations.models, function(observation) {
                        observation = observation.toJSON();

                        var observationRowView
                            = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                        observationRowView.render();
                    });

                    var oTable = $('#shrna-observation-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
                    });

                    oTable.fnSort( [ [2, 'desc'] ] );

                }
            });

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

            var observations = new Observations({ subjectId: result.id });
            thatEl = $("#transcript-observation-grid");
            observations.fetch({
                success: function() {
                    $(".subject-observations-loading").remove();
                    _.each(observations.models, function(observation) {
                        observation = observation.toJSON();

                        var observationRowView
                            = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                        observationRowView.render();
                    });

                    var oTable = $('#transcript-observation-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
                    });

                    oTable.fnSort( [ [2, 'desc'] ] );

                }
            });

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

            var thatEl = $("ul.synonyms");
            _.each(result.synonyms, function(aSynonym) {
                if(aSynonym.displayName == result.displayName ) return;

                var synonymView = new SynonymView({ model: aSynonym, el: thatEl });
                synonymView.render();
            });

            var observations = new Observations({ subjectId: result.id });
            thatEl = $("#tissuesample-observation-grid");
            observations.fetch({
                success: function() {
                    $(".subject-observations-loading").remove();
                    _.each(observations.models, function(observation) {
                        observation = observation.toJSON();

                        var observationRowView
                            = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                        observationRowView.render();
                    });

                    var oTable = $('#tissuesample-observation-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
                    });

                    oTable.fnSort( [ [2, 'desc'] ] );

                }
            });

            return this;
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

            var observations = new Observations({ subjectId: result.id });
            thatEl = $("#cellsample-observation-grid");
            observations.fetch({
                success: function() {
                    $(".subject-observations-loading").remove();
                    _.each(observations.models, function(observation) {
                        observation = observation.toJSON();

                        var observationRowView
                            = new ObservationRowView({ el: $(thatEl).find("tbody"), model: observation });
                        observationRowView.render();
                    });

                    var oTable = $('#cellsample-observation-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
                    });

                    oTable.fnSort( [ [2, 'desc'] ] );
                }
            });

            return this;
        }
    });

    var ObservationRowView = Backbone.View.extend({
        template: _.template($("#observation-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            var summary = this.model.submission.observationTemplate.observationSummary;

            var thatModel = this.model;
            var thatEl = $("#observation-summary-" + this.model.id);
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

                            $(thatEl).html(summary);
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
            result.subject["type"] = result.subject.class;
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
                    _.each(centerSubmissions.toJSON(), function(submission) {
                        var centerSubmissionRowView
                            = new CenterSubmissionRowView({ el: $(thatEl).find("tbody"), model: submission });
                        centerSubmissionRowView.render();

                        $.ajax("count/observation/?filterBy=" + submission.id).done(function(count) {
                            $("#observation-count-" + submission.id).html(count);
                        });
                    });

                    $(".template-description").tooltip();
                    var oTable = $('#center-submission-grid').dataTable({
                           "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                           "sPaginationType": "bootstrap"
                    });
                    oTable.fnSort( [ [2, 'desc'] ] );

                }
            });

            return this;
        }
    });

    var SubmissionRowView = Backbone.View.extend({
        template:  _.template($("#submission-tbl-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));

            var summary = this.model.submission.observationTemplate.observationSummary;

            var thatModel = this.model;
            var thatEl = $("#submission-observation-summary-" + this.model.id);
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
            var observations = new Observations({ submissionId: this.model.get("id") });
            observations.fetch({
                success: function() {
                    $(".submission-observations-loading").remove();

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
                            model: observation
                        });
                        submissionRowView.render();
                    });

                    $('#submission-observation-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
                    });
                }
            });

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
            var result = this.model;
            result["type"] = result.class;

            $(this.el).append(this.template(result));

            var thatEl = $("#synonyms-" + result.id);
            _.each(result.synonyms, function(aSynonym) {
                var synonymView = new SynonymView({model: aSynonym, el: thatEl});
                synonymView.render();
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
            } else if( result.class == "Gene" ) {
                imgTemplate = $("#search-results-gene-image-tmpl");
            }
            thatEl.append(_.template(imgTemplate.html(), result));

            $.ajax("count/observation/?filterBy=" + result.id).done(function(count) {
               $("#subject-observation-count-" + result.id).html(count);
            });

            return this;
        }
    });

    var SearchView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#search-tmpl").html()),
        render: function() {
            $(this.el).html(this.template(this.model));

            // update the search box accordingly
            $("#omni-input").val(this.model.term);

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
                            if(aResult.organism == undefined) {
                                aResult.organism = { displayName: "N/A" };
                            }

                            if(aResult.class == "Submission") {
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

                        var oTable = $("#search-results-grid").dataTable({
                            "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                            "sPaginationType": "bootstrap"
                        });
                        oTable.fnSort( [ [1, 'desc'] ] );

                        // OK done with the subjects; let's build the submissions table
                        if(submissions.length > 0) {
                            $("#submission-search-results").fadeIn();

                            _.each(submissions, function(submission) {
                                var searchSubmissionRowView = new SearchSubmissionRowView({ model: submission });
                                searchSubmissionRowView.render();

                                $.ajax("count/observation/?filterBy=" + submission.id).done(function(count) {
                                    $("#search-observation-count-" + submission.id).html(count);
                                });
                            });

                            var sTable = $("#searched-submissions").dataTable({
                                "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                                "sPaginationType": "bootstrap"
                            });
                            sTable.fnSort( [ [1, 'desc'] ] );
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

            observedSubjects.fetch({
                success: function() {
                    $("#browsed-item-count-" + thatId).text(observedSubjects.models.length);
                }
            });
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
                 
                 
                   var oTable2 = $('#mra-barcode-grid').dataTable({
                     "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                     "sPaginationType": "bootstrap"
                   });
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
                            	 ready: undefined, // callback on layoutready
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
                                    "width": "20px",
                                    "height": "20px",                                   
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
    
    
    
    
    

    /* Routers */
    AppRouter = Backbone.Router.extend({
        routes: {
            "centers": "listCenters",
            "stories": "listStories",
            "browse/:type/:character": "browse",
            "center/:id": "showCenter",
            "submission/:id": "showSubmission",
            "observation/:id": "showObservation",
            "search/:term": "search",
            "subject/:id": "showSubject",
            "evidence/:id": "showMraView",
            "*actions": "home"
        },

        home: function(actions) {
            var homeView = new HomeView();
            homeView.render();
        },

        search: function(term) {
            var searchView = new SearchView({
                model: {
                    term: term.replace(new RegExp("<", "g"), "").replace(new RegExp(">", "g"), "")
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

        showSubject: function(id) {
            var subject = new Subject({ id: id });
            subject.fetch({
                success: function() {
                    var type = subject.get("class");
                    var subjectView;
                    if(type == "Gene") {
                        subjectView = new GeneView({ model: subject });
                    } else if(type == "Compound") {
                        subjectView = new CompoundView({ model: subject });
                    } else if(type == "CellSample") {
                        subjectView = new CellSampleView({ model: subject });
                    } else if(type == "TissueSample") {
                        subjectView = new TissueSampleView({ model: subject });
                    } else if(type == "ShRna") {
                        subjectView = new ShrnaView({ model: subject });
                    } else if(type == "Transcript") {
                        subjectView = new TranscriptView({ model: subject });
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
            
        }

    });

    $(function(){
        new AppRouter();
        Backbone.history.start();

        $("#omnisearch").submit(function() {
            var searchTerm = $("#omni-input").val();
            window.location.hash = "search/" + searchTerm;
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
