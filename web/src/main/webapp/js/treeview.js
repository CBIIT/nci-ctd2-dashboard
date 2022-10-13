export default function () {
    $.ajax("tree/disease-context").done(function (result) {
        console.debug(result)
        const { tree, collapse, expand } = create_tree(result, {
            label: d => d.label.substring(0, 10) + (d.label.length > 10 ? "..." : ""),
            title: d => `${d.label}\nobservations of this node: ${d.direct}\nobservations of all descendants: ${d.observations}`, // hover text
            link: (d, n) => `#tissue/c${d.name}`,
            width: 1152,
            port_width: 1152,
            r: 4,
        })
        $("#treeview").html(tree)
        $("#treeview-collapse").click(collapse)
        $("#treeview-expand").click(expand)
        $("#tv-disease-context").click(function (e) {
            $("#tv-evidence-type").prop('disabled', false)
            $("#tv-disease-context").prop('disabled', true)
            $("#treeview").html(tree)
            $("#treeview-collapse").off('click').click(collapse)
            $("#treeview-expand").off('click').click(expand)
        })
    }).fail(function (err) {
        console.log(err);
    })
    $.ajax("tree/evidence-type").done(function (result) {
        console.debug(result)
        const { tree, collapse, expand } = create_tree(result, {
            label: d => d.label.substring(0, 10) + (d.label.length > 10 ? "..." : ""),
            title: d => `${d.label}\nobservations of this node: ${d.direct}\nobservations of all descendants: ${d.observations}`, // hover text
            link: (d, n) => `#eco/eco-${d.name.padStart(7, "0")}`,
            width: 1152,
            port_width: 1152,
            r: 4,
        })
        $("#tv-evidence-type").click(function (e) {
            $("#tv-disease-context").prop('disabled', false)
            $("#tv-evidence-type").prop('disabled', true)
            $("#treeview").html(tree)
            $("#treeview-collapse").off('click').click(collapse)
            $("#treeview-expand").off('click').click(expand)
        })
    }).fail(function (err) {
        console.log(err);
    })
}

// based on https://observablehq.com/@d3/collapsible-tree and https://observablehq.com/@d3/tree
function create_tree(data, { // data is hierarchy (nested objects)
    sort, // how to sort nodes prior to layout (e.g., (a, b) => d3.descending(a.height, b.height))
    label, // given a node d, returns the display name
    title, // given a node d, returns its hover text
    link, // given a node d, its link (if any)
    linkTarget = "_blank", // the target attribute for links (if any)
    width = 640, // outer width, in pixels
    r = 3, // radius of nodes
    padding = 1, // horizontal padding for first and last column
    fill = "#999", // fill for nodes
    stroke = "#555", // stroke for links
    strokeWidth = 1.5, // stroke width for links
    strokeOpacity = 0.4, // stroke opacity for links
    halo = "#fff", // color of label halo 
    haloWidth = 3, // padding around the labels
    port_width, // width of viewport
    internal_node_fill = "brown"
} = {}) {
    const xlink = link
    const diagonal = d3.linkHorizontal().x(d => d.y).y(d => d.x)
    const margin = ({ top: 10, right: 120, bottom: 10, left: 40 })

    const root = d3.hierarchy(data)

    // Sort the nodes.
    if (sort != null) root.sort(sort);

    // Compute the layout.
    const dx = 10;
    const dy = width / (root.height + padding);
    const tree = d3.tree().nodeSize([dx, dy])

    root.x0 = dy / 2;
    root.y0 = 0;
    root.descendants().forEach((d, i) => {
        d.id = i;
        d._children = d.children;
        if (d.depth > 5) d.children = null;
    });

    const svg = d3.create("svg")
        .attr("viewBox", [-margin.left, -margin.top, width, dx])
        .style("font", "10px sans-serif")
        .style("user-select", "none")
        .attr("width", port_width)
    const gLink = svg.append("g")
        .attr("fill", "none")
        .attr("stroke", stroke)
        .attr("stroke-opacity", strokeOpacity)
        .attr("stroke-width", strokeWidth)
    const gNode = svg.append("g")
        .attr("cursor", "pointer")
        .attr("pointer-events", "all")

    function update(source) {
        const duration = d3.event && d3.event.altKey ? 2500 : 250;
        const nodes = root.descendants().reverse();
        const links = root.links();

        // Compute the new tree layout.
        tree(root);

        let left = root;
        let right = root;
        root.eachBefore(node => {
            if (node.x < left.x) left = node;
            if (node.x > right.x) right = node;
        });

        const height = right.x - left.x + margin.top + margin.bottom;

        const transition = svg.transition()
            .duration(duration)
            .attr("viewBox", [-margin.left, left.x - margin.top, width, height])
            .tween("resize", window.ResizeObserver ? null : () => () => svg.dispatch("toggle"));

        // Update the nodes ...
        const node = gNode.selectAll("g")
            .data(nodes, d => d.id);

        // Enter any new nodes at the parent's previous position.
        const nodeEnter = node.enter().append("g")
            .attr("transform", d => `translate(${source.y0},${source.x0})`)
            .attr("fill-opacity", 0)
            .attr("stroke-opacity", 0)

        nodeEnter.append("circle")
            .attr("r", r)
            .attr("fill", d => d._children ? internal_node_fill : fill)
            .attr("stroke-width", 10)
            .on("click", (event, d) => {
                d.children = d.children ? null : d._children;
                update(d);
            })

        nodeEnter.append("a")
            .attr("xlink:href", xlink == null ? null : d => xlink(d.data, d))
            .attr("target", xlink == null ? null : linkTarget)
            .append("text")
            .attr("dy", "0.31em")
            .attr("x", d => d._children ? -6 : 6)
            .attr("text-anchor", d => d._children ? "end" : "start")
            .text(d => label(d.data, d))
            .clone(true).lower()
            .attr("stroke-linejoin", "round")
            .attr("stroke-width", haloWidth)
            .attr("stroke", halo);

        if (title != null) nodeEnter.append("title")
            .text(d => title(d.data, d));

        // Transition nodes to their new position.
        const nodeUpdate = node.merge(nodeEnter).transition(transition)
            .attr("transform", d => `translate(${d.y},${d.x})`)
            .attr("fill-opacity", 1)
            .attr("stroke-opacity", 1);

        // Transition exiting nodes to the parent's new position.
        const nodeExit = node.exit().transition(transition).remove()
            .attr("transform", d => `translate(${source.y},${source.x})`)
            .attr("fill-opacity", 0)
            .attr("stroke-opacity", 0);

        // Update the links ...
        const link = gLink.selectAll("path")
            .data(links, d => d.target.id);

        // Enter any new links at the parent's previous position.
        const linkEnter = link.enter().append("path")
            .attr("d", d => {
                const o = { x: source.x0, y: source.y0 };
                return diagonal({ source: o, target: o });
            });

        // Transition links to their new position.
        link.merge(linkEnter).transition(transition)
            .attr("d", diagonal);

        // Transition exiting nodes to the parent's new position.
        link.exit().transition(transition).remove()
            .attr("d", d => {
                const o = { x: source.x, y: source.y };
                return diagonal({ source: o, target: o });
            });

        // Stash the old positions for transition.
        root.eachBefore(d => {
            d.x0 = d.x;
            d.y0 = d.y;
        });
    }

    update(root);

    function collapse() {
        root.descendants().forEach((d, i) => {
            if (d.depth) d.children = null;
        });
        update(root)
    }

    function _expand(x) {
        if (x._children === undefined || x._children === null) return
        x.children = x._children
        x.children.forEach(d => {
            _expand(d)
        })
    }
    function expand() {
        _expand(root)
        update(root)
    }

    return { tree: svg.node(), collapse: collapse, expand: expand };
}