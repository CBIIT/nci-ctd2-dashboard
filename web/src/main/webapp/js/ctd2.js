!function ($) {
    var onWhichSlide = 0;
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
        model: Submission,

        initialize: function(attributes) {
            this.url += attributes.submissionId;
        }
    });

    var ObservedSubject = Backbone.Model.extend({
        urlRoot: CORE_API_URL + "get/observedsubject"
    });

    var ObservedSubjects = Backbone.Collection.extend({
        url: CORE_API_URL + "list/observedsubject/?filterBy=",
        model: Submission,

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

            var ctd2Boxes = $('.ctd2-boxes .span3');
            ctd2Boxes.hover( function() {
              $('.ctd2-boxes .span3').removeClass('active-box');
              var val = parseInt($(this).attr("data-order"));
              $(this).addClass('active-box');
              if(onWhichSlide != val) {
                  $('#myCarousel').carousel(val);
              }
              onWhichSlide = val;
              $('#myCarousel').carousel('pause');
            });

            ctd2Boxes.first().addClass('active-box');

            $('#prevSlideControl').bind("click", function() {
              changeBoxFocus($('#myCarousel .active').index()-1);
            });

            $('#nextSlideControl').bind("click", function() {
              changeBoxFocus($('#myCarousel .active').index()+1);
            });


            var changeBoxFocus = function(idx) {
              // Do the modulo
              if(idx < 0) {
                  idx += 4;
              } else {
                  idx = idx % 4;
              }

              $('.ctd2-boxes .span3').removeClass('active-box');
              $('.ctd2-boxes .span3[data-order="' + idx + '"]').addClass('active-box');
            };

            $(".target-link").tooltip();
            $(".drug-link").tooltip();
            $(".genomics-link").tooltip();
            $(".story-link").tooltip();

            $("#target-search").typeahead({ source: targets, items: 3 });
            $("#drug-search").typeahead({ source: drugs, items: 3 });
            $("#alteration-search").typeahead({ source: targets, items: 3 });

            Holder.run();
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
                           = new CenterListRowView({ el: $(thatEl).find("tbody"), model: aCenter });
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

    var GeneView = Backbone.View.extend({
        el: $("#main-container"),
        template:  _.template($("#gene-tmpl").html()),
        render: function() {
            var result = this.model.toJSON();

            var synonymsStr = "";
            _.each(result.synonyms, function(aSynonym) {
                synonymsStr += aSynonym.displayName + " ";
            });
            result["synonymsStr"] = synonymsStr;

            var xrefStr = "";
            _.each(result.xrefs, function(xref) {
                xrefStr += xref.databaseName + ":" + xref.databaseId + " ";
            });
            result["xrefStr"] = xrefStr;

            result["type"] = result.class;

            $(this.el).html(this.template(result));

            var observedSubjects = new ObservedSubjects({ subjectId: result.id });
            var thatEl = $("#gene-observation-grid");
            observedSubjects.fetch({
                success: function() {
                    _.each(observedSubjects.models, function(observedSubject) {
                        observedSubject = observedSubject.toJSON();
                        var observedSubjectRowView
                            = new ObservedSubjectRowView({ el: $(thatEl).find("tbody"), model: observedSubject });
                        observedSubjectRowView.render();
                    });

                    $('#gene-observation-grid').dataTable({
                           "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                           "sPaginationType": "bootstrap"
                    });
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
                        if(observation.subject == null) return;
                        var submissionRowView = new SubmissionRowView({
                            el: $(thatEl).find(".observations tbody"),
                            model: observation
                        });
                        submissionRowView.render();
                    });

                    $(".template-description").tooltip();

                    $('#submission-observation-grid').dataTable({
                           "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                           "sPaginationType": "bootstrap"
                    });
                }
            });


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
            var synonymsStr = "";
            _.each(result.synonyms, function(aSynonym) {
                synonymsStr += aSynonym.displayName + " ";
            });
            result["synonymsStr"] = synonymsStr;
            result["type"] = result.class;

            $(this.el).append(this.template(result));

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
                            var searchResultsRowView = new SearchResultsRowView({
                                model: aResult,
                                el: $(thatEl).find("tbody")
                            });
                            searchResultsRowView.render();
                        });

                        $("#search-results-grid").dataTable({
                            "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                            "sPaginationType": "bootstrap"
                        });
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

        listCenters: function() {
            var centerListView = new CenterListView();
            centerListView.render();
        }
    });

    $(function(){
        new AppRouter();
        Backbone.history.start();
    });

}(window.jQuery);
