import {ctd2_hovertext} from './ctd2.constants.js'

const action_explanatory = [
    "Retrieve molecular interactions involving the selected genes from the Cellular Networks Knowledge Base (CNKB). The CNKB is a database of gene and protein interaction networks maintained at Columbia University.  It includes PREPPI, a large database of predicted and experimentally confirmed protein-protein interactions.", // CNKB
    "Send the contents of the gene cart to the external Enrichr web service for gene set enrichment analysis.  Enrichment analysis is a computational method for inferring knowledge about an input gene set by comparing it to annotated gene sets representing prior biological knowledge. Enrichment analysis checks whether an input set of genes significantly overlaps with annotated gene sets.", // Enrichr
    "STRING is a database of known and predicted protein-protein interactions. The interactions include direct (physical) and indirect (functional) associations; they stem from computational prediction, from knowledge transfer between organisms, and from interactions aggregated from other (primary) databases.", // STRING
    "Pathway Commons collects and integrates data from public pathway and interactions databases. Pathway Commons is a collaboration between the Bader Lab at the University of Toronto, the Sander Lab at the Dana-Farber Cancer Institute and Harvard Medical School and the Demir Lab at Oregon Health & Science University.", // text only option "Pathway Commons"
    "Search Pathway Commons for pathways involving genes in the gene cart.", // Pathway search
    "Display and explore gene interaction network involving genes in the gene cart.", // Interaction viewer text
];

// common utility
export const showAlertMessage = function (message) {
    $("#alertMessage").text(message);
    $("#alertMessage").css('color', '#5a5a5a');
    $("#alert-message-modal").modal('show');
};

const showInvalidMessage = function (message) {
    $("#alertMessage").text(message);
    $("#alertMessage").css('color', 'red');
    $("#alert-message-modal").modal('show');
};

//Gene List View
export const GeneListView = Backbone.View.extend({
    el: $("#main-container"),
    template: _.template($("#genelist-view-tmpl").html()),
    render: function () {
        const numOfCartGene = 25; // this should match the numOfCartGene in ctd2.js

        let geneList = JSON.parse(localStorage.getItem("genelist"));
        if (geneList == null)
            geneList = [];
        else if (geneList.length > numOfCartGene) {
            geneList.slice(numOfCartGene, geneList.length - 1);
            localStorage.genelist = JSON.stringify(geneList);
        }

        $(this.el).html(this.template({}));
        $.each(geneList, function (aData) {
            $("#geneNames").append(_.template($("#gene-cart-option-tmpl").html())({
                displayItem: Encoder.htmlEncode(this.toString())
            }));
        });

        $("#addGene").click(function (e) {
            e.preventDefault();

            $("#gene-symbols").val("");
            $("#addgene-modal").modal('show');
        });

        $("#add-gene-symbols").click(function () {
            processInputGenes(Encoder.htmlEncode($("#gene-symbols").val()).split(/[\s,]+/));
        });

        $("#deleteGene").click(function (e) {
            e.preventDefault();
            const selectedGenes = [];
            $('#geneNames :selected').each(function (i, selected) {
                selectedGenes[i] = $(selected).text();
            });

            if (selectedGenes == null || selectedGenes.length == 0) {
                showAlertMessage("You haven't select any gene!");
                return;
            }

            $.each(selectedGenes, function () {
                const index = $.inArray($.trim(this.toString()).toUpperCase(), geneList);
                if (index >= 0) geneList.splice(index, 1);

            });
            localStorage.genelist = JSON.stringify(geneList);
            sessionStorage.selectedGenes = JSON.stringify(geneList);
            $("#geneNames option:selected").remove();

        });


        $("#clearList").click(function (e) {
            e.preventDefault();
            $('#geneNames').html('');
            localStorage.removeItem("genelist");
            sessionStorage.removeItem("selectedGenes");

            geneList = [];
        });

        $("#loadGenes").click(function (e) {
            e.preventDefault();
            $('#geneFileInput').click();

        });

        if (window.FileReader) {
            $('#geneFileInput').on('change', function (e) {
                const file = e.target.files[0];
                if (file.size > 1000) {
                    showAlertMessage("Gene Cart can only contains " + numOfCartGene + " genes.");
                    return;
                }
                const reader = new FileReader();
                reader.onload = function (e) {
                    processInputGenes(reader.result.split(/[\s,]+/));
                };
                reader.readAsText(file);
                $('#geneFileInput').each(function () {
                    $(this).after($(this).clone(true)).remove();
                });
            });
        } else {
            showAlertMessage("Load Genes from file is not supported.");
        }

        function enrich(options) {
            if (typeof options.list === 'undefined') {
                alert('No genes defined.');
            }

            const description = options.description || "",
                popup = options.popup || false,
                form = document.createElement('form'),
                listField = document.createElement('input'),
                descField = document.createElement('input');

            form.setAttribute('method', 'post');
            form.setAttribute('action', 'http://amp.pharm.mssm.edu/Enrichr/enrich');
            if (popup) {
                form.setAttribute('target', '_blank');
            }
            form.setAttribute('enctype', 'multipart/form-data');

            listField.setAttribute('type', 'hidden');
            listField.setAttribute('name', 'list');
            listField.setAttribute('value', options.list);
            form.appendChild(listField);

            descField.setAttribute('type', 'hidden');
            descField.setAttribute('name', 'description');
            descField.setAttribute('value', description);
            form.appendChild(descField);

            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        }
        $("#gene-cart-action").click(function (e) {
            const selected = $('#gene-cart-action-list :selected');
            const action_index = selected[0].index;
            const selected_genes = $('#geneNames :selected').toArray().map(x=>x.value).join(",") || geneList.join(",")
            switch (action_index) {
                case 0:
                    const selectedGenes = [];
                    $('#geneNames :selected').each(function (i, selected) {
                        selectedGenes[i] = $(selected).text();
                    });

                    if (selectedGenes == null || selectedGenes.length == 0) {
                        sessionStorage.selectedGenes = JSON.stringify(geneList);
                    } else {
                        sessionStorage.selectedGenes = JSON.stringify(selectedGenes);
                    }
                    document.location.href = '#cnkb-query';
                    break;
                case 1:
                    e.preventDefault();
                    enrich({
                        list: $('#geneNames :selected').toArray().map(x=>x.value).join("\n") || geneList.join("\n"),
                        description: "CTD2 Dashboard Query",
                        popup: true,
                    });
                    break;
                case 2:
                    const count = $('#geneNames :selected').length || geneList.length;
                    if (count == 1) {
                        window.open(`https://version-11-0.string-db.org/network/homo_sapiens/${selected_genes}`, "_blank");
                        break;
                    }
                    $.ajax({
                        url: "string/identifier",
                        method: 'GET',
                        data: {
                            genes: selected_genes,
                        },
                        dataType: "text",
                        success: function (identifiers) {
                            console.log(identifiers);
                            if (identifiers.length == 0) {
                                showAlertMessage('no match identifer found in STRING DB');
                            } else {
                                window.open(`https://version-11-0.string-db.org/cgi/network.pl?identifiers=${identifiers}`, "_blank");
                            }
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.log(textStatus);
                        },
                    });
                    break;
                case 3:
                    console.log("do nothing")
                    break
                case 4:
                    window.open(`https://apps.pathwaycommons.org/search?type=Pathway&q=${selected_genes}`, "_blank");
                    break
                case 5:
                    window.open(`https://www.pathwaycommons.org/pcviz/#pathsbetween/${selected_genes}`, "_blank");
                    break
            }
        });
        $("#gene-cart-action").attr("disabled", true);
        $('#gene-cart-action-list').change(function () {
            const selected = $('#gene-cart-action-list :selected');
            if (selected.length == 1) {
                $("#gene-cart-action").attr("disabled", false);
                $("#gene-cart-action-detail").text(action_explanatory[selected[0].index]);
                if (selected[0].index == 3) { // text only option "Pathway Commons"
                    $("#gene-cart-action").attr("disabled", true);
                }
            } else {
                $("#gene-cart-action").attr("disabled", true);
                $("#gene-cart-action-detail").empty();
            }
        });
        $('#gene-cart-action-list option:contains("CNKB")').prop('selected', true);
        $('#gene-cart-action-list').change();

        const processInputGenes = function (genes) {
            let geneNames = JSON.parse(localStorage.getItem("genelist"));
            if (geneNames == null)
                geneNames = [];

            if (genes.length + geneNames.length > numOfCartGene) {
                showAlertMessage("Gene Cart can only contains " + numOfCartGene + " genes.");
                return;
            }

            $.ajax({
                url: "cnkb/validation",
                data: {
                    geneSymbols: JSON.stringify(genes)
                },
                dataType: "json",
                contentType: "json",
                success: function (data) {
                    let invalidGenes = "";
                    _.each(data, function (aData) {
                        if (invalidGenes.length > 0)
                            invalidGenes = aData;
                        else
                            invalidGenes = invalidGenes + ", " + aData;
                        genes.splice(genes.indexOf(aData), 1);
                    });
                    if (data.length > 1) {
                        showInvalidMessage("\"" + data + "\" are invalid and not added to the cart.");
                    } else if (data.length == 1) {
                        showInvalidMessage("\"" + data + "\" is invalid and not added to the cart.");
                    } else {
                        $("#addgene-modal").modal('hide');
                    }

                    addGenes(genes);

                }
            }); //ajax   
        };

        const addGenes = function (genes) {
            const newGenes = [];
            $.each(genes, function () {
                const eachGene = Encoder.htmlEncode($.trim(this.toString())).toUpperCase();
                if (geneList.indexOf(eachGene) == -1 &&
                    newGenes.indexOf(eachGene.toUpperCase()) == -1 && eachGene != "") {
                    newGenes.push(eachGene);
                    geneList.push(eachGene);
                }
            });

            if (newGenes.length > 0) {
                localStorage.genelist = JSON.stringify(geneList);
                $.each(newGenes, function () {
                    $("#geneNames").append(_.template($("#gene-cart-option-tmpl").html())({
                        displayItem: this.toString()
                    }));
                });

            }
        };

        return this;
    }

});

export const CnkbQueryView = Backbone.View.extend({
    el: $("#main-container"),
    template: _.template($("#cnkb-query-tmpl").html()),
    render: function () {
        const selectedGenes = JSON.parse(sessionStorage.getItem("selectedGenes"));
        let count = 0;
        if (selectedGenes != null)
            count = selectedGenes.length;

        $(this.el).html(this.template({}));
        $('#queryDescription').html("");
        $('#queryDescription').html("Query with " + count + " genes from cart");
        $.ajax({
            url: "cnkb/query",
            data: {
                dataType: "interactome-context",
                interactome: "",
                selectedGenes: "",
                interactionLimit: 0,
                throttle: ""
            },
            dataType: "json",
            contentType: "json",
            success: function (data) {
                _.each(data.interactomeList, function (aData) {
                    const option = '<option value="' + aData + '">' + aData + '</option>';
                    if (aData.toLowerCase().startsWith("preppi")) {
                        $("#interactomeList").prepend(option);
                    } else {
                        $("#interactomeList").append(option);
                    }
                });
                $('#interactomeList option:contains("Preppi")').prop('selected', true);
                $('#interactomeList').change();
                $('#interactomeVersionList').disabled = true;
            }
        });

        $('#interactomeList').change(function () {
            $.ajax({
                url: "cnkb/query",
                data: {
                    dataType: "interactome-version",
                    interactome: $('#interactomeList option:selected').text().split("(")[0].trim(),
                    selectedGenes: "",
                    interactionLimit: 0,
                    throttle: ""
                },
                dataType: "json",
                contentType: "json",
                success: function (data) {
                    const URL_pattern = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&/=]*[a-zA-Z0-9/])?/g;
                    // convert URL to an actual link
                    $('#interactomeDescription').html(data.description.replaceAll(URL_pattern, "<a href='$&' target='_blank'>$&</a>"));
                    $('#versionDescription').html(data.versionDescriptor);

                }
            }); //ajax

        }); //end $('#interactomeList').change()

        $("#cnkb-result").click(function (e) {

            const selectedInteractome = $('#interactomeList option:selected').text().split("(")[0].trim();

            if (selectedInteractome == null || $.trim(selectedInteractome).length == 0) {
                e.preventDefault();
                showAlertMessage("Please select an interactome name");
            } else {
                sessionStorage.selectedInteractome = JSON.stringify(selectedInteractome);
            }

        });

        return this;
    }

});

export const GeneCartHelpView = Backbone.View.extend({
    el: $("#main-container"),
    template: _.template($("#gene-cart-help-tmpl").html()),
    render: function () {
        $(this.el).html(this.template({}));
        return this;
    }
});

export const CnkbResultView = Backbone.View.extend({
        el: $("#main-container"),
        template: _.template($("#cnkb-result-tmpl").html()),
        render: function () {
            const selectedgenes = JSON.parse(sessionStorage.getItem("selectedGenes"));
            const selectedInteractome = JSON.parse(sessionStorage.getItem("selectedInteractome"));

            const numOfCartGene = 25; // this should match the numOfCartGene in ctd2.js
            const table_filter_popover = {
                placement: "top",
                trigger: 'hover',
                content: ctd2_hovertext.TABLE_FILTER,
            };
            if (selectedgenes.length > numOfCartGene) {
                selectedgenes.slice(numOfCartGene, selectedgenes.length - 1);
                sessionStorage.selectedGenes = JSON.stringify(selectedgenes);
            }

            $(this.el).html(this.template({}));
            $.ajax({
                url: "cnkb/query",
                data: {
                    dataType: "interaction-result",
                    interactome: selectedInteractome,
                    selectedGenes: JSON.stringify(selectedgenes),
                    interactionLimit: 0,
                    throttle: ""
                },
                dataType: "json",
                contentType: "json",
                success: function (data) {
                    $("#cnkb_data_progress").hide();
                    _.each(data.interactionTypeList, function (aData) {
                        $('#cnkb-result-grid thead tr').append('<th>' + aData.toUpperCase() + '</th>');
                    });

                    const thatEl = $("#cnkb-result-grid");
                    _.each(data.cnkbElementList, function (aData) {
                        new CnkbResultRowView({
                            el: $(thatEl).find("tbody"),
                            model: aData
                        }).render();

                    });

                    $(thatEl).dataTable({
                        "dom": "<'fullwidth'ifrtlp>",
                        "paging": false
                    }); // return value ignored
                    $(thatEl).parents("#cnkb-result-grid_wrapper").find('input[type=search]').popover(table_filter_popover);
                    $(thatEl).find('thead th').popover({
                        placement: "top",
                        trigger: 'hover',
                        content: function () {
                            const hovertext_id = 'CNKB_' + $(this).text().toUpperCase().replace('-', '_');
                            const t = ctd2_hovertext[hovertext_id];
                            if (!t) return null; // only null is automatically hidden
                            return t;
                        },
                    });
                }

            }); //ajax  

            $('#cnkbExport').click(function (e) {
                e.preventDefault();
                let filters = "";
                $('input[type="checkbox"]:checked').each(function () {
                    filters = filters + ($(this).val() + ',');
                });
                if (filters.length == 0 || $.trim(filters) === 'on,') {
                    showAlertMessage("Please select at least one row to export to a SIF file.");
                    return;
                }

                $("#interactome").val(selectedInteractome);
                $("#selectedGenes").val(filters);
                $("#interactionLimit").val("0");
                $("#throttle").val("");
                $('#cnkbExport-form').submit();

            }); //end $('#interactomeList').change()

            const getThrottleValue = function () {

                let filters = "";
                $('input[type="checkbox"]:checked').each(function () {
                    filters = filters + ($(this).val() + ',');
                });

                $.ajax({
                    url: "cnkb/query",
                    data: {
                        dataType: "interaction-throttle",
                        interactome: selectedInteractome,
                        selectedGenes: filters,
                        interactionLimit: $("#cytoscape-node-limit").val(),
                        throttle: ""
                    },
                    dataType: "json",
                    contentType: "json",
                    success: function (data) {
                        if (data != null && data.threshold != -1) {
                            if (data.threshold == 0)
                                $("#throttle-input").text("0.0");
                            else
                                $("#throttle-input").text(data.threshold);
                        } else
                            $("#throttle-input").text("e.g. 0.01");
                        $("#throttle-input").css('color', 'grey');
                    }
                });

            };

            $("#cnkb-result-grid").on("change", ":checkbox", function () {
                getThrottleValue();
            }); //end cnkb-checked

            $("#cytoscape-node-limit").change(function (evt) {
                getThrottleValue();
            }).popover({
                placement: "top",
                trigger: 'hover',
                content: ctd2_hovertext.CNKB_LIMIT,
            });
            $("#cytoscape-layouts").popover({
                placement: "top",
                trigger: 'hover',
                content: ctd2_hovertext.CNKB_LAYOUT,
            });

            $('#checkbox_selectall').click(function (event) { //on click
                if (this.checked) { // check select status
                    $('.cnkb_checkbox').each(function () { //loop through each checkbox
                        this.checked = true; //select all checkboxes with class "checkbox1"
                    });
                    getThrottleValue();
                } else {
                    $('.cnkb_checkbox').each(function () { //loop through each checkbox
                        this.checked = false; //deselect all checkboxes with class "checkbox1"
                    });
                    $("#throttle-input").text("e.g. 0.01");
                    $("#throttle-input").css('color', 'grey');
                }
            });

            $('#createnetwork').click(function (event) {
                event.preventDefault();
                const throttle = $("#throttle-input").text();
                const interactionLimit = $("#cytoscape-node-limit").val();

                let filters = "";
                $('input[type="checkbox"]:checked').each(function () {
                    filters = filters + ($(this).val() + ',');

                });

                if (filters.length == 0 || $.trim(filters) === 'on,') {
                    showAlertMessage("Please select at least one row to create a network.");
                    return;
                }
                $('#createnw_progress_indicator').show();
                $.ajax({
                    url: "cnkb/network",
                    data: {
                        interactome: selectedInteractome,
                        selectedGenes: filters,
                        interactionLimit: interactionLimit,
                        throttle: throttle
                    },
                    dataType: "json",
                    contentType: "json",
                    success: function (data) {
                        $('#createnw_progress_indicator').hide();
                        if (data == null) {
                            showAlertMessage("The network is empty.");
                            return;
                        }
                        drawCNKBCytoscape(data, Encoder.htmlEncode(selectedInteractome));

                    } //end success
                }); //end ajax

            }); //end createnetwork.click
            $('#createnetwork').popover({
                placement: 'top',
                trigger: 'hover',
                content: ctd2_hovertext.CNKB_CREATE_NETWORK,
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

const CnkbResultRowView = Backbone.View.extend({
        render: function () {
            const result = this.model;

            this.template = _.template($("#cnkb-result-row-tmpl").html());
            $(this.el).append(this.template(result));

            _.each(result.interactionNumlist, function (aData) {
                $("#tr_" + Encoder.htmlEncode(result.geneName)).append('<td>' + aData + '</td>');
            });


            return this;
        }
});

const drawCNKBCytoscape = function (data, description) {
        let svgHtml = "";
        const interactions = data.interactions;
        let x1 = 20 + 90 * (3 - interactions.length),
            x2 = 53 + 90 * (3 - interactions.length);
        _.each(interactions, function (aData) {
            svgHtml = svgHtml + '<rect x="' + x1 + '" y="15" width="30" height="2" fill="' + aData.color + '" stroke="grey" stroke-width="0"/><text x="' + x2 + '" y="20" fill="grey">' + aData.type + '</text>';
            x1 = x1 + aData.type.length * 11;
            x2 = x2 + aData.type.length * 11;
        });

        $.fancybox.open(
            _.template($("#cnkb-cytoscape-tmpl").html())({
                description: description,
                svgHtml: svgHtml
            }), {
            touch: false,
            'autoDimensions': false,
            'transitionIn': 'none',
            'transitionOut': 'none'
        }
        );

        cytoscape({
            container: $('#cytoscape'),
            wheelSensitivity: 0.4,
            layout: {
                name: $("#cytoscape-layouts").val(),
                fit: true,
                liveUpdate: false,
                maxSimulationTime: 4000, // max length in ms to run the layout
                stop: function () {
                    $("#cnkb_cytoscape_progress").remove();
                    this.stop();

                } // callback on layoutstop 

            },
            elements: data,
            style: cytoscape.stylesheet()
                .selector("node")
                .css({
                    "content": "data(id)",
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
                    "target-arrow-shape": "circle",
                    "source-arrow-shape": "circle",
                    "line-color": "data(color)"
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
                window.cy = this; // for debugging
            }
        }).on('cxttap', 'node', function () {

            $.contextMenu('destroy', '#cytoscape');
            const sym = this.data('id');
            $.contextMenu({
                selector: '#cytoscape',

                callback: function (key, options) {
                    if (!key || 0 === key.length) {
                        $.contextMenu('destroy', '#cytoscape');
                        return;
                    }

                    let linkUrl = "";
                    switch (key) {
                        case 'linkout':
                            return;
                        case 'gene':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/gene?cmd=Search&term=" + sym;
                            break;
                        case 'protein':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/protein?cmd=Search&term=" + sym + "&doptcmdl=GenPept";
                            break;
                        case 'pubmed':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/pubmed?cmd=Search&term=" + sym + "&doptcmdl=Abstract";
                            break;
                        case 'nucleotide':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/nucleotide?cmd=Search&term=" + sym + "&doptcmdl=GenBank";
                            break;
                        case 'alldatabases':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/gquery/?term=" + sym;
                            break;
                        case 'structure':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/structure?cmd=Search&term=" + sym + "&doptcmdl=Brief";
                            break;
                        case 'omim':
                            linkUrl = "http://www.ncbi.nlm.nih.gov/omim?cmd=Search&term=" + sym + "&doptcmdl=Synopsis";
                            break;
                        case 'genecards':
                            linkUrl = "http://www.genecards.org/cgi-bin/carddisp.pl?gene=" + sym + "&alias=yes";
                            break;
                        case 'ctd2-dashboard':
                            linkUrl = "#search/" + sym;
                    }
                    window.open(linkUrl);
                    $.contextMenu('destroy', '#cytoscape');
                },
                items: {
                    "linkout": {
                        "name": 'LinkOut'
                    },
                    "sep1": "---------",
                    "entrez": {
                        "name": "Entrez",
                        "items": {
                            "gene": {
                                "name": "Gene"
                            },
                            "protein": {
                                "name": "Protein"
                            },
                            "pubmed": {
                                "name": "PubMed"
                            },
                            "nucleotide": {
                                "name": "Nucleotide"
                            },
                            "alldatabases": {
                                "name": "All Databases"
                            },
                            "structure": {
                                "name": "Structure"
                            },
                            "omim": {
                                "name": "OMIM"
                            }
                        }
                    },
                    "genecards": {
                        "name": "GeneCards"
                    },
                    "ctd2-dashboard": {
                        "name": "CTD2-Dashboard"
                    }

                }

            });

        });

};
