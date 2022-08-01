export default function create_wordcloud(dom_id, words, w_cloud = 960, h_cloud = 600,
    color_scheme = "default", font_name = "Arial", max_font = 70, scale_type = "sqrt", spiral_type = "archimedean") {
    if (words.length == 0) return

    const max_word_number = 250;
    const angle_count = 5, angle_from = -60, angle_to = 60;

    const scheme_options = {
        "default": ["#FF7F0E", "#D12FC2", "#0066FF", "#4ECB35",],
        "category10": d3.schemeCategory10,
        "accent": d3.schemeAccent,
        "dark2": d3.schemeDark2,
        "paired": d3.schemePaired,
    }
    const color = scheme_options[color_scheme]
    const scale_options = {
        "sqrt": d3.scaleSqrt(),
        "linear": d3.scaleLinear(),
        "logarithm": d3.scaleLog(),
    }
    const scale = scale_options[scale_type]

    function draw(tags, bounds) {
        cloud_area.selectAll("text").data(tags)
            .enter().append("text")
            .attr("text-anchor", "middle")
            .attr("transform", t => "translate(" + [t.x, t.y] + ")rotate(" + t.rotate + ")")
            .style("font-size", t => t.size + "px")
            .style("font-family", t => t.font)
            .style("fill", t => color[(Math.floor(Math.random() * color.length))])
            .style("cursor", "pointer")
            .text(t => t.text)
            .on("click", t => {
                d3.event.stopPropagation()
                window.location = "#" + t.url
            })
            .append("title").text(t => t.fullname)

        /* if the entire region containing words is small, we will scale it up to fit the area */
        const scale = bounds ? Math.min(w_cloud / Math.abs(bounds[1].x - w_cloud / 2), w_cloud / Math.abs(bounds[0].x - w_cloud / 2), h_cloud / Math.abs(bounds[1].y - h_cloud / 2), h_cloud / Math.abs(bounds[0].y - h_cloud / 2)) / 2 : 1
        cloud_area.transition().delay(1e3).duration(750).attr("transform", "translate(" + [w_cloud >> 1, h_cloud >> 1] + ")scale(" + scale + ")")
    }

    words.sort((t, e) => e.value - t.value)

    const font_size_scale = scale.range([10, max_font]).domain([+words[words.length - 1].value || 1, +words[0].value])
    const rotation_scale = d3.scaleLinear().domain([0, angle_count - 1]).range([angle_from, angle_to])
    const layout = d3.layout.cloud().timeInterval(10)
        .size([w_cloud, h_cloud])
        .words(words.slice(0, Math.min(words.length, max_word_number)))
        .text(t => t.key)
        .padding(3)
        .rotate(x => rotation_scale(~~(Math.random() * angle_count)))
        .font(font_name)
        .fontSize(t => font_size_scale(+t.value))
        .spiral(spiral_type)
        .on("end", draw)
    d3.select(dom_id).select("svg").remove()
    const cloud_area = d3.select(dom_id).append("svg").attr("width", w_cloud).attr("height", h_cloud).append("g")
        .attr("transform", "translate(" + [w_cloud >> 1, h_cloud >> 1] + ")")

    layout.start()
}