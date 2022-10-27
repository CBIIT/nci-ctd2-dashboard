const express = require('express');
const path = require('path');

const app = module.exports = express();

// Register ejs as .html. If we did
// not call this, we would need to
// name our views foo.ejs instead
// of foo.html. The __express method
// is simply a function that engines
// use to hook into the Express view
// system by default, so if we want
// to change "foo.ejs" to "foo.html"
// we simply pass _any_ function, in this
// case `ejs.__express`.

app.engine('.html', require('ejs').__express);

// Optional since express defaults to CWD/views

app.set('views', path.join(__dirname, 'views'));

// Path to our public directory

app.use(express.static(path.join(__dirname, 'static')));

// Without this you would need to
// supply the extension to res.render()
// ex: res.render('users.html').
app.set('view engine', 'html');

app.get('/', function (request, response) {
    response.render('index');
});

function get_names(url, response, ctd2type) {
    const https = require('https')
    const options = {
        hostname: "translator.broadinstitute.org",
        port: 443,
        path: url,
        method: 'GET',
    }

    const req = https.request(options, res => {
        console.log(`statusCode: ${res.statusCode}`);
        if (res.statusCode != 200) {
            console.warn(`not OK for {url}`)
            ctd2(ctd2type, [], response)
            return
        }
        let data = '';
        res.on('data', chunk => {
            data += chunk;
        });
        res.on('end', () => {
            const x = JSON.parse(data);
            const a = Array(x.elements.length);
            for (let i = 0; i < x.elements.length; i++) {
                const n = x.elements[i].names_synonyms;
                // the following implementation of extracting name is kind of arbitrary. names_synonyms is a mess and/or mystery.
                if (n.length > 0) {
                    a[i] = n[0].name;
                } else {
                    a[i] = "NO NAME";
                }
            }
            ctd2(ctd2type, a, response)
        })
    });

    req.on('error', error => {
        console.error(error)
    });
    req.end();
}

function aggregate(list_ids, response, ctd2type) {
    const https = require('https');
    const data =
        JSON.stringify({
            'operation': 'union',
            'collection_ids': list_ids
        });
    const options = {
        hostname: "translator.broadinstitute.org",
        port: 443,
        path: '/molecular_data_provider/aggregate',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': data.length
        }
    };
    const req = https.request(options, res => {
        console.log(`statusCode: ${res.statusCode}`);
        if (res.statusCode != 200) {
            console.warn(`not OK for {options} {data}`)
            ctd2(ctd2type, [], response)
            return
        }
        res.on('data', d => {
            const x = JSON.parse(d);
            get_names(x.url, response, ctd2type);
        })
    });

    req.on('error', error => {
        console.error(error)
    });
    req.write(data);
    req.end();
}

function transform(id, transformers, response, ctd2type) {
    const https = require('https');
    const list_ids = [];
    transformers.forEach(transformer => {
        const data =
            JSON.stringify({
                'name': transformer,
                'collection_id': id,
                'controls': []
            });
        const options = {
            hostname: "translator.broadinstitute.org",
            port: 443,
            path: '/molecular_data_provider/transform',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        };

        const req = https.request(options, res => {
            console.log(`statusCode: ${res.statusCode}`);
            if (res.statusCode != 200) {
                console.warn(`not OK for {options} {data}`)
                ctd2(ctd2type, [], response)
                return
            }
            let result_data = '';
            res.on('data', chunk => {
                result_data += chunk;
            });
            res.on('end', () => {
                const x = JSON.parse(result_data);
                list_ids.push(x.id);
                if (list_ids.length == transformers.length) {
                    aggregate(list_ids, response, ctd2type);
                }
            });
        }).on('error', error => {
            console.error(error)
        });
        req.write(data);
        req.end();
    });
}

app.get('/gene/:name', function (req1, res1) {
    const compound_transformers = [
        'DrugBank inhibitors transformer',
        'DrugBank substrates transformer',
        'DrugBank transporter substrates transformer',
        'DrugBank carrier substrates transformer',
        'DGIdb inhibitor transformer',
        'GtoPdb inhibitors transformer'
    ];
    const gene = req1.params.name;

    const https = require('https')
    const data =
        JSON.stringify({
            'name': 'HGNC gene-list producer',
            'controls': [{
                'name': 'genes',
                'value': gene
            }]
        });

    const options = {
        hostname: "translator.broadinstitute.org",
        port: 443,
        path: '/molecular_data_provider/transform',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': data.length
        }
    }

    const req = https.request(options, res => {
        console.log(`statusCode: ${res.statusCode}`);
        if (res.statusCode != 200) {
            console.warn(`not OK for {options} {data}`)
            ctd2(ctd2type, [], response)
            return
        }
        let data = '';
        res.on('data', chunk => {
            data += chunk;
        });
        res.on('end', () => {
            const x = JSON.parse(data);
            transform(x.id, compound_transformers, res1, "compound");
        })
    });

    req.on('error', error => {
        console.error(error)
    });

    req.write(data);
    req.end();
});

app.get('/compound/:name', function (request, response) {
    const gene_transformers = [
        'DrugBank target genes transformer',
        'DrugBank enzyme genes transformer',
        'DrugBank transporter genes transformer',
        'DrugBank carrier genes transformer',
        'Pharos target genes transformer',
        'ChEMBL gene target transformer',
        'HMDB target genes transformer',
        'Repurposing Hub target transformer',
        'DGIdb target transformer',
        'GtoPdb target transformer'
    ];
    const compound = request.params.name
    const https = require('https')
    const options = {
        hostname: "translator.broadinstitute.org",
        port: 443,
        path: '/molecular_data_provider/compound/by_name/' + compound,
        method: 'get',
    }
    https.request(options, res => {
        console.log(`statusCode: ${res.statusCode}`);
        if (res.statusCode != 200) {
            console.warn(`not OK for {options}`)
            ctd2(ctd2type, [], response)
            return
        }
        let data = '';
        res.on('data', chunk => {
            data += chunk;
        });
        res.on('end', () => {
            const x = JSON.parse(data);
            transform(x.id, gene_transformers, response, "gene");
        });
    }).on('error', error => {
        console.error(error)
    }).end();
});

// query CTD2 API - here is the main point of this demo app
function ctd2(ctd2type, terms, res1) {
    if (terms.length == 0) { // this is necessary to prevent time-out for empty query
        res1.status(404).json({
            error: 'no MolePro result'
        });
        return;
    }
    const https = require('https');
    let x = [];
    let total = terms.length;
    terms.forEach(term => {
        const options = {
            hostname: process.env.CTD2_API_HOST,
            port: process.env.CTD2_API_PORT,
            path: `/dashboard/api/browse/${ctd2type}/${encodeURIComponent(term)}`,
            method: 'GET',
        }

        const req = https.request(options, res => {
            let data = '';
            res.on('data', chunk => {
                data += chunk;
            });
            res.on('end', () => {
                if (res.statusCode != 200) {
                    console.log(`ERROR statusCode: ${res.statusCode} for ${ctd2type} ${term}`);
                    total--;
                    return;
                }
                const d = JSON.parse(data);
                if (d.class == null) {
                    console.log("empty result ");
                    total--;
                    return;
                }
                const roles = d.roles;
                if (roles.length <= 1) {
                    x.push(d);
                } else {
                    total += (roles.length - 1);
                    /* this is complicated because of multiple requests being asynchromous */
                    roles.forEach(role => {
                        const options = {
                            hostname: process.env.CTD2_API_HOST,
                            port: process.env.CTD2_API_PORT,
                            path: `/dashboard/api/browse/${ctd2type}/${encodeURIComponent(term)}?role=${encodeURIComponent(role)}`,
                            method: 'GET',
                        }
                        const req = https.request(options, res => {
                            let data = '';
                            res.on('data', chunk => {
                                data += chunk;
                            });
                            res.on('end', () => {
                                if (res.statusCode != 200) {
                                    console.log(`ERROR statusCode: ${res.statusCode} for ${ctd2type} ${term}`);
                                    total--;
                                    return;
                                }
                                const d = JSON.parse(data);
                                if (d.class == null) {
                                    console.log("empty result ");
                                    total--;
                                    return;
                                }
                                x.push(d);
                                if (x.length == total) {
                                    res1.send(x);
                                }
                            })
                        }).on('error', error => {
                            console.error(error)
                        });
                        req.end();
                    });
                }
                if (x.length == total) {
                    //console.log(x);
                    res1.send(x);
                }
            })
        })

        req.on('error', error => {
            console.error(error)
        })

        req.end()
    });
}

app.use(express.json()) // for parsing application/json
app.use(express.urlencoded({
    extended: true
})) // for parsing application/x-www-form-urlencoded

if (!module.parent) {
    app.listen(3000);
    console.log('Express started on port 3000');
}