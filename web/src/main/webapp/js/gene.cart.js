import { supported_interactomes, supported_confidence_types } from './supported.interactomes.js'

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
            const selected_genes = $('#geneNames :selected').toArray().map(x => x.value).join(",") || geneList.join(",")
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
                        list: $('#geneNames :selected').toArray().map(x => x.value).join("\n") || geneList.join("\n"),
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
        const names = supported_interactomes.map(x => x.name)
        $.ajax({
            url: "cnkb/interactome-list",
            dataType: "json",
            contentType: "json",
            success: function (interactomeList) {
                _.each(interactomeList, function ({ name, count }) {
                    if (!names.includes(name.toLowerCase())) {
                        return;
                    }
                    const option = `<option value="${name}">${name} (${count} interactions)</option>`;
                    if (name.toLowerCase() === "preppi") {
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
                url: "cnkb/interactome-descriptions",
                data: {
                    interactome: $('#interactomeList option:selected').val(),
                },
                dataType: "json",
                contentType: "json",
                success: function (descriptions) {
                    const URL_pattern = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&/=]*[a-zA-Z0-9/])?/g;
                    // convert URL to an actual link
                    $('#interactomeDescription').html(descriptions[0].replaceAll(URL_pattern, "<a href='$&' target='_blank'>$&</a>"));
                    $('#versionDescription').html(descriptions[1]);
                }
            }); //ajax

        }); //end $('#interactomeList').change()

        $("#cnkb-result").click(function (e) {
            const selectedInteractome = $('#interactomeList option:selected').val();

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
        if (selectedgenes.length > numOfCartGene) {
            selectedgenes.slice(numOfCartGene, selectedgenes.length - 1);
            sessionStorage.selectedGenes = JSON.stringify(selectedgenes);
        }

        $(this.el).html(this.template({}));
        supported_interactomes.find(x => x.name == selectedInteractome.toLowerCase())
            .confidence_types
            .forEach(x => {
                $("#supported-confidence-types").append(`<option>${x}</option>`)
            })
        const code = { "p-value": 7, "likelihood ratio": 1, "mutual information": 3, "mode of action": 6, "probability": 4 }
        const network_data = {}
        $("#supported-confidence-types").change(function (event) {
            const confidence_type = $(event.target).val()
            const confidence_type_code = code[confidence_type]
            console.debug(`selected confidence type code ${confidence_type_code}`)
            drawCNKBCytoscape(select_data(confidence_type_code), confidence_type_code)
        })
        $.ajax({
            url: "cnkb/interaction-total-number",
            data: {
                interactome: selectedInteractome,
                selectedGenes: JSON.stringify(selectedgenes),
            },
            dataType: "json",
            contentType: "json",
            success: function (totalNumber) {
                $("#cnkb_data_progress").hide();
                $("#total-interaction-number").text(totalNumber)
                $("#interaction-limit").attr("max", totalNumber)
                const geneNames = selectedgenes.toString()
                $("#genecart-genes").text(geneNames);
                $("#interactome-selected").text(selectedInteractome)
                createNetwork()
                function download(filename) {
                    return function (data, textStatus, jqXHR) {
                        const url = window.URL.createObjectURL(new Blob([data]));
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        a.href = url;
                        a.download = filename;
                        document.body.appendChild(a);
                        a.click();
                        window.URL.revokeObjectURL(url);
                    }
                }
                $('#cnkb-export-all').click(function (e) {
                    $(this).prop("disabled", true)
                    $("#export-spinner").show()
                    $.post("cnkb/download", {
                        interactome: selectedInteractome,
                        selectedGenes: geneNames,
                        all: true,
                    },
                    ).done(download("all-interactions.sif")).always(function () {
                        $("#export-spinner").hide()
                        $("#cnkb-export-all").prop("disabled", false)
                    })
                })
                $('#cnkb-export-displayed').click(function (e) {
                    const confidence_type_code = code[$("#supported-confidence-types").val()]
                    $(this).prop("disabled", true)
                    $("#export-spinner").show()
                    $.post("cnkb/download", {
                        interactome: selectedInteractome,
                        selectedGenes: geneNames,
                        all: false,
                        confidenceType: confidence_type_code,
                        interactionLimit: network_limit
                    },
                    ).done(download("displayed-interactions.sif")).always(function () {
                        $("#export-spinner").hide()
                        $("#cnkb-export-displayed").prop("disabled", false)
                    })
                })
            }
        });

        let network_limit = 200;

        const select_data = function (confidence_type) {
            const types = { 7: "p-value", 1: "likelihood ratio", 3: "mutual information", 6: "mode of action", 4: "probability" }
            const direction = supported_confidence_types.find(x => x.name == types[confidence_type]).directionality
            const compare_plus_name_ordering = (a, b) => {
                const x = a.data.confidences[confidence_type] - b.data.confidences[confidence_type]
                if (x < 0) return -1
                else if (x > 0) return 1
                else return a.data.id.localeCompare(b.data.id)
            }
            const compare = direction == "increasing" ? (a, b) => (b.data.confidences[confidence_type] - a.data.confidences[confidence_type]) : compare_plus_name_ordering
            const data = {}
            data.edges = network_data.edges.sort(compare).slice(0, network_limit)
            const node_set = data.edges.reduce((prev, curr) => {
                prev.add(curr.data.source)
                prev.add(curr.data.target)
                return prev
            }, new Set())
            data.nodes = [...node_set].map(x => { return { data: { id: x, color: selectedgenes.includes(x) ? "yellow" : "#DDD" } } })
            return data
        }

        const createNetwork = function () {
            $('#createnw_progress_indicator').show();
            $.ajax({
                url: "cnkb/network",
                data: {
                    interactome: selectedInteractome,
                    selectedGenes: selectedgenes.toString(),
                    //interactionLimit: default_limit,
                },
                dataType: "json",
                contentType: "json",
                success: function (data) {
                    $('#createnw_progress_indicator').hide();
                    if (data == null) {
                        showAlertMessage("The network is empty.");
                        return;
                    }
                    make_legend(data.interactionTypes, Encoder.htmlEncode(selectedInteractome))
                    Object.assign(network_data, data)
                    const confidence_type = code[$("#supported-confidence-types").val()]
                    $("#displayed-interaction-number").text(network_limit)
                    drawCNKBCytoscape(select_data(confidence_type), confidence_type)
                    $("#interaction-limit").val(network_limit).on("input", function () {
                        $("#displayed-interaction-number").text(this.value)
                    }).on("change", function () {
                        network_limit = parseInt(this.value)
                        const confidence_type = code[$("#supported-confidence-types").val()]
                        drawCNKBCytoscape(select_data(confidence_type), confidence_type)
                    })
                    function stepwise(change) {
                        const max_number = parseInt($("#total-interaction-number").text())
                        return function () {
                            network_limit += change
                            if (network_limit < 10) network_limit = 10
                            if (network_limit > max_number) network_limit = max_number
                            $("#displayed-interaction-number").text(network_limit)
                            $("#interaction-limit").val(network_limit)
                            const confidence_type = code[$("#supported-confidence-types").val()]
                            drawCNKBCytoscape(select_data(confidence_type), confidence_type)
                        }
                    }
                    $("#incre25").click(stepwise(25))
                    $("#decre25").click(stepwise(-25))
                    $("#incre1").click(stepwise(1))
                    $("#decre1").click(stepwise(-1))
                } //end success
            }); //end ajax
        } //end createnetwork
        $('.clickable-popover').popover({
            placement: "bottom",
            trigger: 'hover',
        }).click(function () {
            $(this).popover('hide');
        });

        return this;
    }
});

const cytoscape_context_menu = {
    linkout: { name: 'LinkOut' },
    sep1: "---------",
    entrez: {
        name: "Entrez",
        items: {
            gene: { name: "Gene" },
            protein: { name: "Protein" },
            pubmed: { name: "PubMed" },
            nucleotide: { name: "Nucleotide" },
            alldatabases: { name: "All Databases" },
            structure: { name: "Structure" },
            omim: { name: "OMIM" }
        }
    },
    genecards: { name: "GeneCards" },
    ctd2_dashboard: { name: "CTD2-Dashboard" }
}

const context_menu_callback = sym => function (key, options) {
    if (!key || 0 === key.length) {
        $.contextMenu('destroy', '#cytoscape');
        return;
    }

    const links = {
        gene: "http://www.ncbi.nlm.nih.gov/gene?cmd=Search&term=" + sym,
        protein: "http://www.ncbi.nlm.nih.gov/protein?cmd=Search&term=" + sym + "&doptcmdl=GenPept",
        pubmed: "http://www.ncbi.nlm.nih.gov/pubmed?cmd=Search&term=" + sym + "&doptcmdl=Abstract",
        nucleotide: "http://www.ncbi.nlm.nih.gov/nucleotide?cmd=Search&term=" + sym + "&doptcmdl=GenBank",
        alldatabases: "http://www.ncbi.nlm.nih.gov/gquery/?term=" + sym,
        structure: "http://www.ncbi.nlm.nih.gov/structure?cmd=Search&term=" + sym + "&doptcmdl=Brief",
        omim: "http://www.ncbi.nlm.nih.gov/omim?cmd=Search&term=" + sym + "&doptcmdl=Synopsis",
        genecards: "http://www.genecards.org/cgi-bin/carddisp.pl?gene=" + sym + "&alias=yes",
        ctd2_dashboard: "#gene/h/" + sym,
    }
    const linkUrl = links[key]
    if (linkUrl !== undefined) {
        window.open(links[key])
    }
    $.contextMenu('destroy', '#cytoscape');
}

const make_legend = function (interaction_types, description) {
    const edge_type_color = {
        "protein-dna": "cyan",
        "protein-protein": "orange",
    }
    let svgHtml = "";
    let x1 = 20 + 90 * (3 - interaction_types.length),
        x2 = 53 + 90 * (3 - interaction_types.length);
    _.each(interaction_types, function (type) {
        svgHtml = svgHtml + '<rect x="' + x1 + '" y="15" width="30" height="2" fill="' + edge_type_color[type] + '" stroke="grey" stroke-width="0"/><text x="' + x2 + '" y="20" fill="grey">' + type + '</text>';
        x1 = x1 + type.length * 11;
        x2 = x2 + type.length * 11;
    });

    $("#network-description").text(description)
    $("#legend-svg").html(svgHtml)
}

const drawCNKBCytoscape = function (data, confidence_type) {
    $("#gene-detail").hide()
    $("#interaction-detail").hide()
    $("#network-detail-viewer > #initial-text").show()
    let min_w = 1
    let max_w = -1
    data.edges.forEach(x => {
        x.data.weight = x.data.confidences[confidence_type]
        if (x.data.weight > max_w) max_w = x.data.weight
        if (x.data.weight < min_w) min_w = x.data.weight
    })
    let range_w = max_w - min_w
    if (range_w <= 0) range_w = 1
    data.edges.forEach(x => x.data.weight = (x.data.weight - min_w) / range_w)

    const CYTOSCAPE_TIMER = "cytoscape_timer"
    console.time(CYTOSCAPE_TIMER)
    cytoscape({
        container: $('#cytoscape'),
        wheelSensitivity: 0.4,
        layout: {
            name: 'cola',
            fit: true,
            liveUpdate: false,
            maxSimulationTime: 4000, // max length in ms to run the layout
            stop: function () {
                $("#cnkb_cytoscape_progress").remove();
                this.stop();
                console.timeEnd(CYTOSCAPE_TIMER)
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
                "width": "mapData(weight, 0, 1, 1, 3)",
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
            callback: context_menu_callback(sym),
            items: cytoscape_context_menu,
        });

    }).on('tap', 'node', function (event) {
        const gene_symbol = event.target.data("id")
        $.ajax({
            url: "cnkb/gene-detail",
            data: {
                gene_symbol: gene_symbol,
            },
            dataType: "json",
            contentType: "json",
            success: function (gene_detail) {
                $("#gene-name").text(gene_detail.geneName)
                const template = _.template($("#gene-detail-references-tmpl").html())
                $("#references").html(template({ ...gene_detail.references, gene_symbol: gene_symbol }))
                $("#gene-symbol").html(gene_symbol + " (<a href='https://ctd2-dashboard.nci.nih.gov/dashboard/#gene/h/" + gene_symbol + "'>dashboard entry</a>)")
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.debug(jqXHR)
                console.debug(textStatus)
                console.debug(errorThrown)
                $("#gene-name").text("")
                $("#references").html("")
                $("#gene-symbol").text(gene_symbol)
            }
        });
        $("#network-detail-viewer > #initial-text").hide()
        $("#interaction-detail").hide()
        $("#gene-detail").show()
    }).on('tap', 'edge', function (event) {
        const edge_data = event.target;
        gene_and_link(edge_data.data("source"), $("#interaction-source"))
        gene_and_link(edge_data.data("target"), $("#interaction-target"))
        const confidences = edge_data.data("confidences")
        const types = { 7: "p-value", 1: "likelihood ratio", 3: "mutual information", 6: "mode of action", 4: "probability" }
        $("#interaction-values").empty()
        for (const type in confidences) {
            const type_name = types[type]
            const tooltip = supported_confidence_types.find(x => x.name == type_name)["description"]
            $("#interaction-values").append(`<li title="${tooltip}">${type_name}: ${confidences[type]}</li>`)
        }
        $("#network-detail-viewer > #initial-text").hide()
        $("#gene-detail").hide()
        $("#interaction-detail").show()
    });
    console.timeLog(CYTOSCAPE_TIMER)
};

const gene_and_link = function (gene_symbol, html_element) {
    $.ajax({
        url: "cnkb/gene-detail",
        data: {
            gene_symbol: gene_symbol,
        },
        dataType: "json",
        contentType: "json",
        success: function (gene_detail) {
            html_element.html(gene_symbol + " (<a href='https://ctd2-dashboard.nci.nih.gov/dashboard/#gene/h/" + gene_symbol + "'>dashboard entry</a>)")
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.debug(jqXHR)
            console.debug(textStatus)
            console.debug(errorThrown)
            html_element.text(gene_symbol)
        }
    })
}