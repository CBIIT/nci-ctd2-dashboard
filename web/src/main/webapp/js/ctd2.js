require(
    ["jquery", "underscore", "json2", "holder", "backbone-min", "bootstrap.min"],
    function($, StaticDataSource) {
        $(function() {
            var onWhichSlide = 0;
            var CORE_API_URL = "./";

            // This is for the moustache-like templates
            // prevents collisions with JSP tags <%...%>
            _.templateSettings = {
                interpolate : /\{\{(.+?)\}\}/g
            };

            /* Models */
            var SubmissionCenter = Backbone.Model.extend({
                urlRoot: CORE_API_URL + "get/center"
            });
            var SubmissionCenters = Backbone.Collection.extend({
                url: CORE_API_URL + "list/center",
                model: SubmissionCenter
            });

            var Submission = Backbone.Model.extend({
                urlRoot: CORE_API_URL + "get/submission"
            });

            var CenterSubmissions = Backbone.Collection.extend({
                url: CORE_API_URL + "list/submission",
                model: Submission
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

            var CenterView = Backbone.View.extend({
                el: $("#main-container"),
                template: _.template($("#centers-tmpl").html()),
                render: function() {
                    $(this.el).html(this.template({}));

                    var thatEl = this.el;
                    var centerSubmissions = new CenterSubmissions({ centerId: this.model.get("id") });
                    centerSubmissions.fetch({
                        success: function() {
                            _.each(centerSubmissions.toJSON(), function(submission) {
                                var centerSubmissionRowView
                                    = new CenterSubmissionRowView({ el: $(thatEl).find("tbody"), model: submission });
                                centerSubmissionRowView.render();
                            });
                        }
                    });
                }
            });

            /* Routers */
            AppRouter = Backbone.Router.extend({
                routes: {
                    "centers": "listCenters",
                    "center/:id": "showCenter",
                    "*actions": "home"
                },

                home: function(actions) {
                    var homeView = new HomeView();
                    homeView.render();
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

                listCenters: function() {
                    var centerListView = new CenterListView();
                    centerListView.render();
                }
            });


            $(function(){
                new AppRouter();
                Backbone.history.start();
            });

        });
});
