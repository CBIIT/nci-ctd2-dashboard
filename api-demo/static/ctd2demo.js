(function() {
    function start_query() {
        document.getElementById('enter_button').disabled = true;
        document.getElementById('search_terms').disabled = true;

        const search_terms = document.getElementById('search_terms').value.trim();
        const query_type = document.querySelector('input[name="queryType"]:checked').value;

        const terms = query_type == 'gene' ? search_terms.split(/[,;]/) : search_terms.split(";");
        document.getElementById('debug').innerHTML = "";
        const result = document.getElementById('result');
        while (result.firstChild) {
            result.removeChild(result.firstChild);
        }
        const xterms = [...terms];
        terms.forEach(term => {
            fetch(`${query_type}/${term}`).then(response => {
                if (!response.ok) {
                    throw response.json();
                }
                return response.json()
            }).then(data => {
                show_result(query_type, term, data)
            }).catch(error => {
                no_result(query_type, term)
                if (error instanceof Promise)
                    error.then(x => {
                        console.log(x)
                    })
                else
                    console.log(error)
            }).finally(function() {
                const index = xterms.indexOf(term);
                if (index > -1) {
                    xterms.splice(index, 1);
                    document.getElementById('progressbar').innerHTML = `querying for term ${xterms} ...`;
                }
                if (xterms.length == 0) {
                    clearInterval(timer);
                    document.getElementById('progressbarcontainer').style.visibility = "hidden";
                    document.getElementById('enter_button').disabled = false;
                    document.getElementById('search_terms').disabled = false;
                }
            });
        });
        document.getElementById('progressbar').innerHTML = `querying for term ${terms} ...`;
        document.getElementById('progressbarcontainer').style.visibility = "visible";
        let seconds = 0;
        const timer = setInterval(function() {
            seconds += 1;
            document.getElementById('progressbar').innerHTML = `querying for term ${xterms} ... ${seconds} seconds`;
        }, 1000);
    }

    function no_result(query_type, starting_term) {
        const x = document.createElement("p");
        x.innerHTML = `<h4><span class="badge bg-info">${query_type} ${starting_term}</span></h4>`;
        document.getElementById('result').appendChild(x);
        const t = document.createElement("div");
        t.innerHTML = `No result found for ${starting_term}.`;
        t.className = "alert alert-warning";
        t.style.width = "90%";
        document.getElementById('result').appendChild(t);
    }

    const ctd2type = {
        gene: "Compound",
        compound: "Gene"
    }

    const ctd2url = {
        gene: "https://ctd2-dashboard.nci.nih.gov/dashboard/#compound/",
        compound: "https://ctd2-dashboard.nci.nih.gov/dashboard/#gene/h/"
    }

    function show_result(query_type, starting_term, result) {
        console.log(result);
        const x = document.createElement("p");
        x.innerHTML = `<h4><span class="badge bg-info">${query_type} ${starting_term}</span></h4>`;
        document.getElementById('result').appendChild(x);
        const t = document.createElement("table");
        t.innerHTML = `<thead><tr><th>${ctd2type[query_type]} name</th><th>Roles</th><th>Tier 3</th><th>Tier 2</th><th>Tier 1</th></tr></thead>`;
        t.id = "result-" + starting_term;
        t.className = "table table-striped table-sm";
        t.style.width = "90%";
        document.getElementById('result').appendChild(t);
        const tbody = document.createElement("tbody");
        t.appendChild(tbody);
        result.map(x => {
            const subject_role_url = `${ctd2url[query_type]}${x.name}/${x.roles[0]}`
            let tier1 = x.observation_count.tier1;
            if (tier1 > 0) {
                tier1 = `<a href="${subject_role_url}/1" target=_blank>${tier1}</a>`;
            }
            let tier2 = x.observation_count.tier2;
            if (tier2 > 0) {
                tier2 = `<a href="${subject_role_url}/2" target=_blank>${tier2}</a>`;
            }
            let tier3 = x.observation_count.tier3;
            if (tier3 > 0) {
                tier3 = `<a href="${subject_role_url}/3" target=_blank>${tier3}</a>`;
            }

            const row = tbody.insertRow();
            const cell = row.insertCell();
            cell.innerHTML = `<a href="${subject_role_url}" target=_blank>${x.name}</a>`
            const r = row.insertCell();
            r.innerHTML = x.roles;
            const t3 = row.insertCell();
            t3.innerHTML = tier3;
            const t2 = row.insertCell();
            t2.innerHTML = tier2;
            const t1 = row.insertCell();
            t1.innerHTML = tier1;
        });
    }

    if (document.getElementById('search_terms').value.trim().length == 0) {
        document.getElementById('enter_button').disabled = true;
    }

    document.getElementById('enter_button').onclick = start_query;

    document.getElementById('search_terms').onchange = function() {
        if (this.value.trim().length == 0) {
            document.getElementById('enter_button').disabled = true;
        } else {
            document.getElementById('enter_button').disabled = false;
        }
    };
    document.getElementById('search_terms').onkeydown = function(event) {
        if (event.key === 'Enter') {
            start_query();
        }
    };
})();