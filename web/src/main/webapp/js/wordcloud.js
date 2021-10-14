function create_wordcloud(dom_id, w_cloud = 960, h_cloud = 600) { /* totally 7 parameters to control the picture */
    const max_word_number = 250;
    const angle_count = 5; // default 5
    const angle_from = -60; // default -60
    const angle_to = 60; // default 60
    const font_name = "sans-serif"; // default Impact
    const scale_type = "sqrt"; // three options: log, sqrt, linear. default log. see https://i.stack.imgur.com/0oZZQ.png
    const spiral_type = "archimedean"; // two options: archimedean, rectangular. default archimedean. see https://en.wikipedia.org/wiki/Archimedean_spiral

    function generate(word_counts) {
        const tags = JSON.parse(JSON.stringify(word_counts)); // deep copy
        tags.sort(function(t, e) {
            return e.value - t.value
        });

        tags.length && fontSize.domain([+tags[tags.length - 1].value || 1, +tags[0].value]);
        layout.stop().words(tags.slice(0, max = Math.min(tags.length, max_word_number))).start();
    }

    function draw(tags, bounds) {
        const scale = bounds ? Math.min(w_cloud / Math.abs(bounds[1].x - w_cloud / 2), w_cloud / Math.abs(bounds[0].x - w_cloud / 2), h_cloud / Math.abs(bounds[1].y - h_cloud / 2), h_cloud / Math.abs(bounds[0].y - h_cloud / 2)) / 2 : 1;
        const color = ["#FF7F0E", "#D12FC2", "#0066FF", "#4ECB35", ];
        const n = vis.selectAll("text").data(tags, function(t) {
            return t.text.toLowerCase()
        });
        n.transition().duration(1e3).attr("transform", function(t) {
            return "translate(" + [t.x, t.y] + ")rotate(" + t.rotate + ")"
        }).style("font-size", function(t) {
            return t.size + "px"
        });
        n.enter().append("text").attr("text-anchor", "middle").attr("transform", function(t) {
            return "translate(" + [t.x, t.y] + ")rotate(" + t.rotate + ")"
        }).style("font-size", "1px").transition().duration(1e3).style("font-size", function(t) {
            return t.size + "px"
        }).style("font-family", function(t) {
            return t.font
        }).style("fill", function(t) {
            return color[(Math.floor(Math.random() * color.length))];
        });
        n.style("cursor", "pointer").on("click", function(t) {
            d3.event.stopPropagation();
            window.location = "#" + t.url;
        }).text(function(t) {
            return t.text
        }).append("title").text(function(t) {
            return t.fullname;
        });
        const a = background.append("g").attr("transform", vis.attr("transform")),
            r = a.node();
        n.exit().each(function() {
                r.appendChild(this)
            }),
            a.transition().duration(1e3).style("opacity", 1e-6).remove(),
            vis.transition().delay(1e3).duration(750).attr("transform", "translate(" + [w_cloud >> 1, h_cloud >> 1] + ")scale(" + scale + ")")
    }

    const fontSize = d3.scale[scale_type]().range([10, 70]);
    const liner_scale = d3.scale.linear().domain([0, angle_count - 1]).range([angle_from, angle_to]);
    const layout = d3.layout.cloud().timeInterval(10).size([w_cloud, h_cloud]).fontSize(function(t) {
        return fontSize(+t.value)
    }).text(function(t) {
        return t.key
    }).on("end", draw).rotate(function() {
        return liner_scale(~~(Math.random() * angle_count))
    }).font(font_name).spiral(spiral_type).padding(3);

    const svg = d3.select(dom_id).append("svg").attr("width", w_cloud).attr("height", h_cloud);
    const background = svg.append("g");
    const vis = svg.append("g").attr("transform", "translate(" + [w_cloud >> 1, h_cloud >> 1] + ")");

    return generate;
}