function create_wordcloud(dom_id, w_cloud = 700, h_cloud = 400) { /* totally 7 parameters to control the picture */
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

    /* The code here is largely based on https://github.com/jasondavies/d3-cloud
    The following part is mostly a copy of the source code index.js */
    const dispatch = d3.dispatch;

    const cloudRadians = Math.PI / 180,
        cw = 1 << 11 >> 5,
        ch = 1 << 11;

    d3.layout.cloud = function() {
        let size = [256, 256],
            text = cloudText,
            font = cloudFont,
            fontSize = cloudFontSize,
            fontStyle = cloudFontNormal,
            fontWeight = cloudFontNormal,
            rotate = cloudRotate,
            padding = cloudPadding,
            spiral = archimedeanSpiral,
            words = [],
            timeInterval = Infinity,
            event = dispatch("word", "end"),
            timer = null,
            random = Math.random,
            cloud = {},
            canvas = cloudCanvas;

        cloud.canvas = function(_) {
            return arguments.length ? (canvas = functor(_), cloud) : canvas;
        };

        cloud.start = function() {
            let contextAndRatio = getContext(canvas()),
                board = zeroArray((size[0] >> 5) * size[1]),
                bounds = null,
                n = words.length,
                i = -1,
                tags = [],
                data = words.map(function(d, i) {
                    d.text = text.call(this, d, i);
                    d.font = font.call(this, d, i);
                    d.style = fontStyle.call(this, d, i);
                    d.weight = fontWeight.call(this, d, i);
                    d.rotate = rotate.call(this, d, i);
                    d.size = ~~fontSize.call(this, d, i);
                    d.padding = padding.call(this, d, i);
                    return d;
                }).sort(function(a, b) {
                    return b.size - a.size;
                });

            if (timer) clearInterval(timer);
            timer = setInterval(step, 0);
            step();

            return cloud;

            function step() {
                const start = Date.now();
                while (Date.now() - start < timeInterval && ++i < n && timer) {
                    const d = data[i];
                    d.x = (size[0] * (random() + .5)) >> 1;
                    d.y = (size[1] * (random() + .5)) >> 1;
                    cloudSprite(contextAndRatio, d, data, i);
                    if (d.hasText && place(board, d, bounds)) {
                        tags.push(d);
                        event.word(d);
                        if (bounds) cloudBounds(bounds, d);
                        else bounds = [{
                            x: d.x + d.x0,
                            y: d.y + d.y0
                        }, {
                            x: d.x + d.x1,
                            y: d.y + d.y1
                        }];
                        // Temporary hack
                        d.x -= size[0] >> 1;
                        d.y -= size[1] >> 1;
                    }
                }
                if (i >= n) {
                    cloud.stop();
                    event.end(tags, bounds);
                }
            }
        }

        cloud.stop = function() {
            if (timer) {
                clearInterval(timer);
                timer = null;
            }
            return cloud;
        };

        function getContext(canvas) {
            canvas.width = canvas.height = 1;
            const ratio = Math.sqrt(canvas.getContext("2d").getImageData(0, 0, 1, 1).data.length >> 2);
            canvas.width = (cw << 5) / ratio;
            canvas.height = ch / ratio;

            const context = canvas.getContext("2d");
            context.fillStyle = context.strokeStyle = "red";
            context.textAlign = "center";

            return {
                context: context,
                ratio: ratio
            };
        }

        function place(board, tag, bounds) {
            let
                startX = tag.x,
                startY = tag.y,
                maxDelta = Math.sqrt(size[0] * size[0] + size[1] * size[1]),
                s = spiral(size),
                dt = random() < .5 ? 1 : -1,
                t = -dt,
                dxdy,
                dx,
                dy;

            while (dxdy = s(t += dt)) {
                dx = ~~dxdy[0];
                dy = ~~dxdy[1];

                if (Math.min(Math.abs(dx), Math.abs(dy)) >= maxDelta) break;

                tag.x = startX + dx;
                tag.y = startY + dy;

                if (tag.x + tag.x0 < 0 || tag.y + tag.y0 < 0 ||
                    tag.x + tag.x1 > size[0] || tag.y + tag.y1 > size[1]) continue;
                // TODO only check for collisions within current bounds.
                if (!bounds || !cloudCollide(tag, board, size[0])) {
                    if (!bounds || collideRects(tag, bounds)) {
                        const sprite = tag.sprite,
                            w = tag.width >> 5,
                            sw = size[0] >> 5,
                            lx = tag.x - (w << 4),
                            sx = lx & 0x7f,
                            msx = 32 - sx,
                            h = tag.y1 - tag.y0;
                        let x = (tag.y + tag.y0) * sw + (lx >> 5),
                            last;
                        for (let j = 0; j < h; j++) {
                            last = 0;
                            for (let i = 0; i <= w; i++) {
                                board[x + i] |= (last << msx) | (i < w ? (last = sprite[j * w + i]) >>> sx : 0);
                            }
                            x += sw;
                        }
                        delete tag.sprite;
                        return true;
                    }
                }
            }
            return false;
        }

        cloud.timeInterval = function(_) {
            return arguments.length ? (timeInterval = _ == null ? Infinity : _, cloud) : timeInterval;
        };

        cloud.words = function(_) {
            return arguments.length ? (words = _, cloud) : words;
        };

        cloud.size = function(_) {
            return arguments.length ? (size = [+_[0], +_[1]], cloud) : size;
        };

        cloud.font = function(_) {
            return arguments.length ? (font = functor(_), cloud) : font;
        };

        cloud.fontStyle = function(_) {
            return arguments.length ? (fontStyle = functor(_), cloud) : fontStyle;
        };

        cloud.fontWeight = function(_) {
            return arguments.length ? (fontWeight = functor(_), cloud) : fontWeight;
        };

        cloud.rotate = function(_) {
            return arguments.length ? (rotate = functor(_), cloud) : rotate;
        };

        cloud.text = function(_) {
            return arguments.length ? (text = functor(_), cloud) : text;
        };

        cloud.spiral = function(_) {
            return arguments.length ? (spiral = spirals[_] || _, cloud) : spiral;
        };

        cloud.fontSize = function(_) {
            return arguments.length ? (fontSize = functor(_), cloud) : fontSize;
        };

        cloud.padding = function(_) {
            return arguments.length ? (padding = functor(_), cloud) : padding;
        };

        cloud.random = function(_) {
            return arguments.length ? (random = _, cloud) : random;
        };

        cloud.on = function() {
            const value = event.on.apply(event, arguments);
            return value === event ? cloud : value;
        };

        return cloud;
    };

    function cloudText(d) {
        return d.text;
    }

    function cloudFont() {
        return "serif";
    }

    function cloudFontNormal() {
        return "normal";
    }

    function cloudFontSize(d) {
        return Math.sqrt(d.value);
    }

    function cloudRotate() {
        return (~~(Math.random() * 6) - 3) * 30;
    }

    function cloudPadding() {
        return 2;
    }

    // Fetches a monochrome sprite bitmap for the specified text.
    // Load in batches for speed.
    function cloudSprite(contextAndRatio, d, data, di) {
        if (d.sprite) return;
        const c = contextAndRatio.context,
            ratio = contextAndRatio.ratio;

        c.clearRect(0, 0, (cw << 5) / ratio, ch / ratio);
        let x = 0,
            y = 0,
            maxh = 0,
            n = data.length;
        --di;
        while (++di < n) {
            d = data[di];
            c.save();
            c.font = d.style + " " + d.weight + " " + ~~((d.size + 1) / ratio) + "px " + d.font;
            let w = c.measureText(d.text + "m").width * ratio,
                h = d.size << 1;
            if (d.rotate) {
                const sr = Math.sin(d.rotate * cloudRadians),
                    cr = Math.cos(d.rotate * cloudRadians),
                    wcr = w * cr,
                    wsr = w * sr,
                    hcr = h * cr,
                    hsr = h * sr;
                w = (Math.max(Math.abs(wcr + hsr), Math.abs(wcr - hsr)) + 0x1f) >> 5 << 5;
                h = ~~Math.max(Math.abs(wsr + hcr), Math.abs(wsr - hcr));
            } else {
                w = (w + 0x1f) >> 5 << 5;
            }
            if (h > maxh) maxh = h;
            if (x + w >= (cw << 5)) {
                x = 0;
                y += maxh;
                maxh = 0;
            }
            if (y + h >= ch) break;
            c.translate((x + (w >> 1)) / ratio, (y + (h >> 1)) / ratio);
            if (d.rotate) c.rotate(d.rotate * cloudRadians);
            c.fillText(d.text, 0, 0);
            if (d.padding) c.lineWidth = 3 * d.padding, c.strokeText(d.text, 0, 0);
            c.restore();
            d.width = w;
            d.height = h;
            d.xoff = x;
            d.yoff = y;
            d.x1 = w >> 1;
            d.y1 = h >> 1;
            d.x0 = -d.x1;
            d.y0 = -d.y1;
            d.hasText = true;
            x += w;
        }
        const pixels = c.getImageData(0, 0, (cw << 5) / ratio, ch / ratio).data,
            sprite = [];
        while (--di >= 0) {
            d = data[di];
            if (!d.hasText) continue;
            let w = d.width,
                w32 = w >> 5,
                h = d.y1 - d.y0;
            // Zero the buffer
            for (let i = 0; i < h * w32; i++) sprite[i] = 0;
            x = d.xoff;
            if (x == null) return;
            y = d.yoff;
            let seen = 0,
                seenRow = -1;
            for (let j = 0; j < h; j++) {
                for (let i = 0; i < w; i++) {
                    const k = w32 * j + (i >> 5),
                        m = pixels[((y + j) * (cw << 5) + (x + i)) << 2] ? 1 << (31 - (i % 32)) : 0;
                    sprite[k] |= m;
                    seen |= m;
                }
                if (seen) seenRow = j;
                else {
                    d.y0++;
                    h--;
                    j--;
                    y++;
                }
            }
            d.y1 = d.y0 + seenRow;
            d.sprite = sprite.slice(0, (d.y1 - d.y0) * w32);
        }
    }

    // Use mask-based collision detection.
    function cloudCollide(tag, board, sw) {
        sw >>= 5;
        const sprite = tag.sprite,
            w = tag.width >> 5,
            lx = tag.x - (w << 4),
            sx = lx & 0x7f,
            msx = 32 - sx,
            h = tag.y1 - tag.y0;
        let x = (tag.y + tag.y0) * sw + (lx >> 5),
            last;
        for (let j = 0; j < h; j++) {
            last = 0;
            for (let i = 0; i <= w; i++) {
                if (((last << msx) | (i < w ? (last = sprite[j * w + i]) >>> sx : 0)) &
                    board[x + i]) return true;
            }
            x += sw;
        }
        return false;
    }

    function cloudBounds(bounds, d) {
        const b0 = bounds[0],
            b1 = bounds[1];
        if (d.x + d.x0 < b0.x) b0.x = d.x + d.x0;
        if (d.y + d.y0 < b0.y) b0.y = d.y + d.y0;
        if (d.x + d.x1 > b1.x) b1.x = d.x + d.x1;
        if (d.y + d.y1 > b1.y) b1.y = d.y + d.y1;
    }

    function collideRects(a, b) {
        return a.x + a.x1 > b[0].x && a.x + a.x0 < b[1].x && a.y + a.y1 > b[0].y && a.y + a.y0 < b[1].y;
    }

    function archimedeanSpiral(size) {
        const e = size[0] / size[1];
        return function(t) {
            return [e * (t *= .1) * Math.cos(t), t * Math.sin(t)];
        };
    }

    function rectangularSpiral(size) {
        const dy = 4,
            dx = dy * size[0] / size[1],
            x = 0,
            y = 0;
        return function(t) {
            const sign = t < 0 ? -1 : 1;
            // See triangular numbers: T_n = n * (n + 1) / 2.
            switch ((Math.sqrt(1 + 4 * sign * t) - sign) & 3) {
                case 0:
                    x += dx;
                    break;
                case 1:
                    y += dy;
                    break;
                case 2:
                    x -= dx;
                    break;
                default:
                    y -= dy;
                    break;
            }
            return [x, y];
        };
    }

    // TODO reuse arrays?
    function zeroArray(n) {
        let a = [],
            i = -1;
        while (++i < n) a[i] = 0;
        return a;
    }

    function cloudCanvas() {
        return document.createElement("canvas");
    }

    function functor(d) {
        return typeof d === "function" ? d : function() {
            return d;
        };
    }

    const spirals = {
        archimedean: archimedeanSpiral,
        rectangular: rectangularSpiral
    };

    // following part is outside the original source code index.js
    const fontSize = d3.scale[scale_type]().range([10, 70]);
    const liner_scale = d3.scale.linear().domain([0, angle_count - 1]).range([angle_from, angle_to]);
    const layout = d3.layout.cloud().timeInterval(10).size([w_cloud, h_cloud]).fontSize(function(t) {
        return fontSize(+t.value)
    }).text(function(t) {
        return t.key
    }).on("end", draw).rotate(function() {
        return liner_scale(~~(Math.random() * angle_count))
    }).font(font_name).spiral(spiral_type);

    const svg = d3.select(dom_id).append("svg").attr("width", w_cloud).attr("height", h_cloud);
    const background = svg.append("g");
    const vis = svg.append("g").attr("transform", "translate(" + [w_cloud >> 1, h_cloud >> 1] + ")");

    return generate;
}