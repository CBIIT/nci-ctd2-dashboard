export default Backbone.View.extend({
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
                filterBy: "none",
                nodeNumLimit: 0,
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
                url: "mra-data/cytoscape",
                data: {
                    url: mra_data_url,
                    filterBy: filters,
                    nodeNumLimit: nodeLimit,
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

                    const node_shape = {}
                    const node_color = {}
                    const node_set = data.edges.reduce((prev, curr) => {
                        const source = curr.data.source
                        const target = curr.data.target
                        prev.add(source)
                        prev.add(target)
                        node_shape[source] = curr.data.source_shape
                        node_color[source] = curr.data.source_color
                        node_shape[target] = curr.data.target_shape
                        node_color[target] = curr.data.target_color
                        return prev
                    }, new Set())
                    data.nodes = [...node_set].map(x => { return { data: { id: x, shape: node_shape[x], color: node_color[x] } } })
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

        const update_throttle = function () {
            let filters = "";
            $('input[type="checkbox"]:checked').each(function () {
                filters = filters + ($(this).val() + ',');
            });

            $.ajax({
                url: "mra-data/throttle",
                data: {
                    url: mra_data_url,
                    filterBy: filters,
                    nodeNumLimit: $("#cytoscape-node-limit").val(),
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
        }
        $("#master-regulator-grid").on("change", ":checkbox", update_throttle);
        $("#cytoscape-node-limit").change(update_throttle);

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
