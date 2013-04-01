!function ($) {
    var onWhichSlide = 0;
    var leftSep = "<";
    var rightSep = ">";
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

    var FuzzySearchResults = Backbone.Collection.extend({
        url: CORE_API_URL + "search/fuzzy/",
        model: SearchResult,

        initialize: function(attributes) {
            this.url += attributes.term;
        }
    });

    var ExactSearchResults = Backbone.Collection.extend({
        url: CORE_API_URL + "search/exact/",
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
            $(this.el).html(this.template({}));

            $('#myCarousel').carousel('pause');
            $(".target-link").tooltip();
            $(".drug-link").tooltip();
            $(".genomics-link").tooltip();
            $(".story-link").tooltip();

            $("#target-search").typeahead({ source: targets, items: 3 });
            $("#drug-search").typeahead({ source: drugs, items: 3 });
            $("#alteration-search").typeahead({ source: targets, items: 3 });

            $("#omni-search-form").submit(function() {
                var searchTerm = $("#omni-search").val();
                window.location.hash = "search/exact/" + searchTerm;
                return false;
            });

            Holder.run();
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

                        if(observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                            return;

                        summary = summary.replace(
                            leftSep + observedSubject.observedSubjectRole.columnName + rightSep,
                            _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
                        );

                        $("#observation-summary").html(summary);
                    });

                    $('#observed-subjects-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
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
                            leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep,
                            _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
                        );

                        $("#observation-summary").html(summary);
                    });

                    var oTable = $('#observed-evidences-grid').dataTable({
                        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                        "sPaginationType": "bootstrap"
                    });

                    oTable.fnSort( [ [1, 'asc'] ] );

                    $('.desc-tooltip').tooltip({ placement: "bottom" });

                }
            });


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
                if(result.evidence.mimeType.toLowerCase().search("image") > -1) {
                    templateId = "#observedimageevidence-row-tmpl";
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
                    });
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

             Holder.run();

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
                            leftSep + observedSubject.observedSubjectRole.columnName + rightSep,
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
                                    leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep,
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
                    });

                    $(".template-description").tooltip();
                    $('#center-submission-grid').dataTable({
                           "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                           "sPaginationType": "bootstrap"
                    });
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
                            leftSep + observedSubject.observedSubjectRole.columnName + rightSep,
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
                                    leftSep + observedEvidence.observedEvidenceRole.columnName + rightSep,
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

    var SubmissionView =  Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#submission-tmpl").html()),
        render: function() {
            $(this.el).html(this.template(this.model.toJSON()));

            var thatEl = this.el;
            var observations = new Observations({ submissionId: this.model.get("id") });
            observations.fetch({
                success: function() {
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
            $(this.el).append(this.template({}));

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

            return this;
        }
    });

    var SearchView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#search-tmpl").html()),
        exact: true,
        setExact: function(isExact) {
            this.exact = isExact;
        },

        render: function() {
            $(this.el).html(this.template(this.model));

            // update the search box accordingly
            $("#omni-input").val(this.model.term);

            var thatEl = this.el;
            var searchResults = this.exact
                ? new ExactSearchResults({ term: this.model.term })
                : new FuzzySearchResults({ term: this.model.term });

            searchResults.fetch({
                success: function() {
                    $("#loading-row").remove();
                    if(searchResults.models.length == 0) {
                        (new EmptyResultsView({ el: $(thatEl).find("tbody")})).render();
                    } else {
                        _.each(searchResults.models, function(aResult) {
                            aResult = aResult.toJSON();
                            if(aResult.organism == undefined) {
                                aResult.organism = { displayName: "N/A" };
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
                    }
                }
            });

            return this;
        }
    });

    /* Routers */
    AppRouter = Backbone.Router.extend({
        routes: {
            "centers": "listCenters",
            "center/:id": "showCenter",
            "submission/:id": "showSubmission",
            "observation/:id": "showObservation",
            "search/:type/:term": "search",
            "subject/:id": "showSubject",
            "*actions": "home"
        },

        home: function(actions) {
            var homeView = new HomeView();
            homeView.render();
        },

        search: function(type, term) {
            var searchView = new SearchView({ model: { term: term } });
            var isExact = true;
            if(type.toLowerCase() == "fuzzy") {
                isExact = false;
            }
            searchView.setExact(isExact);
            searchView.render();
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
        }
    });

    $(function(){
        new AppRouter();
        Backbone.history.start();

        $("#omnisearch").submit(function() {
            var searchTerm = $("#omni-input").val();
            window.location.hash = "search/exact/" + searchTerm;
            return false;
        });

        $("#omni-input").focus(function() {
            $(this).removeClass("span2").addClass("span3").attr("placeholder", "e.g. CTNNB1 or aspirin");
        }).blur(function() {
            $(this).removeClass("span3").addClass("span2").attr("placeholder", "e.g. CTNNB1");
        }).popover({
           placement: "bottom",
           trigger: "hover",
           html: true,
           title: function() {
                $(this).attr("title");
           },
           content: function() {
               return $("#search-help-content").html();
           },
           delay: {hide: 2500}
        });

    });

}(window.jQuery);
