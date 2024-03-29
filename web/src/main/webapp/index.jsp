<!DOCTYPE html>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%
    WebApplicationContext context = WebApplicationContextUtils
            .getWebApplicationContext(application);
    String dataURL = (String) context.getBean("dataURL");
    Integer maxNumOfObservations = (Integer) context.getBean("maxNumberOfEntities");
    String dashboardReleaseVersion = (String) context.getBean("dashboardReleaseVersion");
%>
<html lang="en" xmlns="http://www.w3.org/1999/html">

<head>
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-173263585-1"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'UA-173263585-1');
</script>

    <!-- X-UA-Compatible meta tag to disable IE compatibility view must always be first -->
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>CTD² Dashboard</title>
    <meta name="description" content="" />
    <meta name="author" content="" />

    <link rel="shortcut icon" href="img/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.2.1/dist/css/bootstrap.min.css" integrity="sha256-azvvU9xKluwHFJ0Cpgtf0CYzK7zgtOznnzxV4924X1w=" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.11.3/b-2.0.1/b-html5-2.0.1/datatables.min.css"/>
    <link rel="stylesheet" href="css/jquery.fancybox.min.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-contextmenu/2.9.2/jquery.contextMenu.min.css" integrity="sha512-SWjZLElR5l3FxoO9Bt9Dy3plCWlBi1Mc9/OlojDPwryZxO0ydpZgvXMLhV6jdEyULGNWjKgZWiX/AMzIvZ4JuA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="css/ctd2.css?ts=202110" type="text/css" />

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <script src="//assets.adobedtm.com/6a4249cd0a2c/0890c2def00a/launch-dbd676d3af6b.min.js" async></script>
    <!-- Fav and touch icons -->
    <link rel="shortcut icon" href="img/favicon.png" />
    <script src="https://kit.fontawesome.com/95ddca2a38.js" crossorigin="anonymous"></script>
</head>

<body>
    <!-- NAVBAR
    ================================================== -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
    <script>
        $(function () {
            // Bind an event to window.onhashchange that, when the hash changes, 
            // gets the hash and alters class of desired navlinks
            window.onhashchange = function () {
                var hash = location.hash || '#';
                $('[id^="navlink-"]').each(function () {
                    // navbar regular items
                    if (
                        $(this).attr('id') == 'navlink-dashboard' ||
                        $(this).attr('id') == 'navlink-centers'
                    ) {
                        if ($(this).attr('href') === decodeURIComponent(hash)) {
                            $(this).removeClass('navlink');
                            $(this).addClass('navlink-current');
                        }
                        else {
                            $(this).removeClass('navlink-current');
                            $(this).addClass('navlink');
                        }
                    }
                    // navbar dropdown menu items
                    else if (
                        $(this).attr('id') == 'navlink-browse' ||
                        $(this).attr('id') == 'navlink-genecart'
                    ) {
                        var id = $(this).attr('id') == 'navlink-browse'
                            ? 'dropdown-menu-browse'
                            : 'dropdown-menu-genecart';
                        var dropdownLink = $(this);
                        $('#' + id + ' li a').each(function () {
                            if ($(this).attr('href') === decodeURIComponent(hash)) {
                                dropdownLink.removeClass('navlink');
                                dropdownLink.addClass('navlink-current');
                                return false;
                            }
                            else {
                                dropdownLink.removeClass('navlink-current');
                                dropdownLink.addClass('navlink');
                            }
                        });
                    }
                });
            };
            // Since the event is only triggered when the hash changes, we need to trigger
            // the event now, to handle the hash the page may have been loaded with.
            window.onhashchange();
        });
    </script>
    <div class="navbar-wrapper">
        <!-- Wrap the .navbar in .container to center it within the absolutely positioned parent. -->
        <div class="container">
            <a href="https://ocg.cancer.gov/" target="_blank"><img src="img/logos/ccg.png"></a>
            <div class="navbar">
                <div class="navbar-inner">
                    <div class="nav-collapse collapse show">
                        <ul id="nav" class="nav">
                            <li><a id="navlink-dashboard" class="navlink" href="#">CTD<sup>2</sup> Dashboard</a></li>
                            <li><a id="navlink-centers" class="navlink" href="#centers">Centers</a></li>
                            <li class="dropdown">
                                <a class="dropdown-toggle navlink" href="#" data-toggle="dropdown">Resources <b
                                        class="caret"></b></a>
                                <ul class="dropdown-menu">
                                    <li><a href="#master-export">Master Observation Export</a></li>
                                    <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2">OCG/CTD² Home Page</a></li>
                                    <li><a href="#cite">How to Cite</a></li>
                                    <li><a href="#api-documentation">API Documentation</a></li>
                                    <li><a target="_blank"
                                            href="https://ocg.cancer.gov/programs/ctd2/publications">Publications</a>
                                    </li>
                                    <li><a href="" class="help-navigate">Glossary</a></li>
                                    <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/data-portal">Data
                                            Portal - Downloads</a></li>
                                    <li><a target="_blank"
                                            href="https://ocg.cancer.gov/programs/ctd2/analytical-tools">Analytical
                                            Tools</a></li>
                                    <li><a target="_blank"
                                            href="https://ocg.cancer.gov/programs/ctd2/supported-reagents">Supported
                                            Reagents</a></li>
                                    <li class="dropdown-submenu"><a tabindex="-1" href="#">Outside Resources</a>
                                        <ul class="dropdown-menu">
                                            <li><a target="_blank" href="http://www.lincsproject.org/">LINCS</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="navlink-browse" class="dropdown-toggle navlink" href="#"
                                    data-toggle="dropdown">Browse <b class="caret"></b></a>
                                <ul id="dropdown-menu-browse" class="dropdown-menu">
                                    <li><a href="#stories">Stories</a></li>
                                    <li><a href="#explore/target/Biomarker,Target">Genes (Biomarkers, Targets, etc.)</a>
                                    </li>
                                    <li><a href="#explore/compound/Perturbagen,Candidate Drug">Compounds and
                                            Perturbagens</a></li>
                                    <li><a href="#explore/context/Disease">Disease Context</a></li>
                                    <li><a href="#explore/cellline/Cell Line">Cell Lines</a></li>
                                    <li><a href="#eco_browse">Experimental Evidence</a></li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="navlink-genecart" class="dropdown-toggle navlink" href="#"
                                    data-toggle="dropdown">Gene Cart <b class="caret"></b></a>
                                <ul id="dropdown-menu-genecart" class="dropdown-menu">
                                    <li><a href="#genes">Go To Cart</a></li>
                                    <li><a href="#gene-cart-help">Help</a></li>
                                </ul>
                            </li>
                        </ul>
                        <ul class="nav pull-right">
                            <form class="form-search" id="omnisearch">
                                <div class="input-append">
                                    <input type="text" id="omni-input" class="search-query"
                                        placeholder="e.g. CTNNB1 or dasatinib" aria-label="search">
                                    <button type="submit" class="btn search-button">Search</button>
                                    <span class="d-none" id="search-help-content">
                                        <p>Please enter the keyword(s) you would like to search on the website. Use of quotes to surround multi-word search terms is advised to avoid a large number of hits. You may
                                            enter multiple search terms, but do not use "AND" or "OR".</p>
                                        <strong>Examples:</strong>
                                        <ul>
                                            <li><em>Gene: </em> <a href="#search/CTNNB1">CTNNB1</a></li>
                                            <li><em>Gene: </em> <a href="#search/YAP*">YAP*</a></li>
                                            <li><em>Compound: </em> <a href="#search/dasatinib">dasatinib</a></li>
                                            <li><em>Cell Sample: </em> <a href="#search/OVCAR8">OVCAR8</a></li>
                                            <li><em>Multiple: </em> <a href="#search/dexamethasone AKT1">dexamethasone
                                                    AKT1</a></li>
                                            <li><em>Expt. Evidence Code: </em> <a
                                                    href="#search/ECO_0006053">ECO_0006053</a></li>
                                            <li><em>Expt. Evidence term: </em> <a href="#search/PCR">PCR</a></li>
                                        </ul>
                                    </span>
                                </div>
                            </form>
                        </ul>
                    </div>
                    <!--/.nav-collapse -->
                </div><!-- /.navbar-inner -->
            </div><!-- /.navbar -->

        </div> <!-- /.container -->
    </div><!-- /.navbar-wrapper -->

    <!-- all the backbone magic will happen here, right in this div -->
    <div id="main-container"></div>

    <div class="container footer-container">
        <!-- FOOTER -->
        <footer>
            <div style="font-size:14px; font-weight:bold; margin-bottom:10px;">
                Dashboard Release <%=dashboardReleaseVersion%>
                <a href="#attribution" data-toggle="collapse">attributions</a>
            </div>
            <div id="attribution" class="collapse">
                <div style="font-size:14px; margin-bottom:10px;">
                    Data users must acknowledge and cite the manuscript <a
                        href="https://www.ncbi.nlm.nih.gov/pubmed/29220450" target="_blank">Aksoy, Dančík, Smith et
                        al.</a>, Database 2017;1-10 and provide the URL <a
                        href="https://ctd2-dashboard.nci.nih.gov/dashboard/">https://ctd2-dashboard.nci.nih.gov/dashboard/</a>.
                </div>
                <div style="font-size:14px; margin-bottom:10px;">
                    As the CTD<sup>2</sup> Network continues to refine the Dashboard, input from the research community
                    is highly valued to help improve usability.
                    Please send your feedback and comments to <a
                        href="mailto:ocg@mail.nih.gov?subject=CTD2 Dashboard Feedback">ocg@mail.nih.gov</a>.
                </div>
                <div style="font-size:14px; margin-bottom:10px;">
                    <a href='' class="help-navigate">Glossary</a> &middot;
                    <a href="http://www.cancer.gov/global/web/policies" target="_blank">Policies</a> &middot;
                    <a href="http://www.cancer.gov/global/web/policies/accessibility" target="_blank">Accessibility</a>
                    &middot;
                    <a href="http://www.cancer.gov/global/web/policies/foia" target="_blank">FOIA</a>
                </div>
            </div>
            <div style="font-size:14px; margin-bottom:10px;">
                <a href="http://www.hhs.gov/" target="_blank">U.S. Department of Health and Human Services</a> / <a href="http://www.nih.gov" target="_blank">National Institutes of Health</a> / <a href="http://www.cancer.gov/" target="_blank">National Cancer Institute</a> / <a href="http://usa.gov" target="_blank">USA.gov</a>
                / <a href="https://www.hhs.gov/vulnerability-disclosure-policy/index.html" target="_blank">HHS Vulnerability Disclosure</a>
            </div>
        </footer>
    </div>

    <div class="modal hide fade" id="alert-message-modal">
        <!-- a hidden div for showing alert message -->
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-body">
                    <br>
                    <medium id="alertMessage"></medium>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal hide fade" id="wordcloud-modal">
        <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Configure Word Cloud</h3>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            </div>
            <div class="modal-body">
                Color palette
                <select class="form-control border selectpicker" id="wordcloud-color">
                    <option data-content='<img src="img/color-scheme/default.png"> default' value="default"></option>
                    <option data-content='<img src="img/color-scheme/category10.png"> category10' value="category10"></option>
                    <option data-content='<img src="img/color-scheme/Accent.png"> accent' value="accent"></option>
                    <option data-content='<img src="img/color-scheme/Dark2.png"> dark2' value="dark2"></option>
                    <option data-content='<img src="img/color-scheme/Paired.png"> paired' value="paired"></option>
                </select>
                Font style 
                <select class="form-control border selectpicker" id="wordcloud-font">
                    <option>Arial</option>
                    <option>Times New Roman</option>
                    <option>Impact</option>
                </select>
                Max font size
                <select class="form-control border selectpicker" id="wordcloud-max-font">
                    <option>40</option>
                    <option>70</option>
                    <option>100</option>
                </select>
                Scaling
                <select class="form-control border selectpicker" id="wordcloud-scaling">
                    <option value="sqrt">Square root</option>
                    <option value="linear">Linear</option>
                    <option value="logarithm">Logarithm</option>
                </select>
                Layout
                <select class="form-control border selectpicker" id="wordcloud-spiral">
                    <option value="archimedean">Archimedean</option>
                    <option value="rectangular">Rectangular</option>
                </select>
                Max word number <select class="form-control border selectpicker" id="wordcloud-max-words"></select>
                Download format <select class="form-control border selectpicker" id="wordcloud-download-format"><option>png</option><option>jpg</option><option>svg</option></select>

                <div style="float: right;padding: 10px;"><a id="reset-link">Reset</a></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal" id="apply-button">Apply</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal" id="cancel-button">Cancel</button>
            </div>
        </div>
        </div>
    </div>

    <!-- these are the templates -->
    <script type="text/template" id="home-tmpl">
        <div class="overview-container">
            <div class="container overview-box">
                <div class="row" id="overview-text">
                    <div class="col-3">
                        <a href="https://ocg.cancer.gov/programs/ctd2">
                            <img class="img-polaroid pull-left" src="img/logos/ctd2_overall.png" alt="CTD2 general image" style="margin:0px">
                        </a>
                    </div>
                    <div class="col-9">
                        {{description}}
                        <ul id='video-list' class="list-inline">
                            <li class="list-inline-item small">
                                <a href='' id="video-link1">Understanding the Dashboard</a>
                            </li>
                            <li class="list-inline-item small">
                                <a href='' id="video-link2">Searching and Browsing</a>
                            </li>
                            <li class="list-inline-item small">
                                <a href='' id="video-link3">Identifying Molecular Interactions</a>
                            </li>
                        </ul>

                    </div> <!-- col-9 -->
                </div><!-- row -->

        <div class="dark-separator"></div>
        <div style="text-align:center;display:block;padding:10px">
        <button type="button" class="btn btn-secondary" id=summary-table-label><span id=toggle-word>Show</span> Dashboard Content Summary</button>
        <button type="button" class="btn btn-secondary" id=wordcloud-button><span id=wordcloud-toggle-word>Hide</span> Word Cloud</button>
        <button type="button" class="btn btn-secondary" id=treeview-button><span id=treeview-toggle-word>Show</span> Tree View</button>
        </div>
        <div class=container id='treeview-container'>
            <div style="text-align:center;display:block;padding:10px">
                <button type="button" class="btn btn-light" id=tv-disease-context style='background:rgb(253, 255, 201);border-color:#6c757d'>Disease context</button>
                <button type="button" class="btn btn-light" id=tv-evidence-type style='background:rgb(253, 255, 201);border-color:#6c757d'>Evidence type</button>
                <i class="fas fa-expand-arrows-alt fa-lg" style="color:SteelBlue" id="treeview-expand"></i>
                <i class="fas fa-compress-arrows-alt fa-lg" style="color:SteelBlue" id='treeview-collapse'></i>
            </div>
            <div id=treeview style="height:600px;overflow-y:scroll;overflow-x:scroll"></div>
        </div>
        <div class=container id='wordcloud-container'>
            <div style="text-align:center;display:block;padding:10px">
                <button type="button" class="btn btn-light" id=wordcloud-all style='background:rgb(253, 255, 201);border-color:#6c757d'>All</button>
                <button type="button" class="btn btn-light" id=wordcloud-genes style='background:rgb(253, 255, 201);border-color:#6c757d'>Genes and Proteins</button>
                <button type="button" class="btn btn-light" id=wordcloud-compounds style='background:rgb(253, 255, 201);border-color:#6c757d'>Compounds and Perturbagens</button>
                <button type="button" class="btn btn-light" id=wordcloud-disease style='background:rgb(253, 255, 201);border-color:#6c757d'>Disease context</button>
                <button type="button" class="btn btn-light" id=wordcloud-cell style='background:rgb(253, 255, 201);border-color:#6c757d'>Cell lines</button>
                <i class="fas fa-cog fa-lg" style="color:SteelBlue;" id="config-wordcloud"></i>
                <i class="fas fa-download fa-lg" style="color:SteelBlue;" id='download-wordcloud'></i>
            </div>
            <div id="vis" class=wordcloud></div>
            <div id="vis-genes" class=wordcloud></div>
            <div id="vis-compounds" class=wordcloud></div>
            <div id="vis-disease" class=wordcloud></div>
            <div id="vis-cell" class=wordcloud></div>
        </div>
        <table id=summary-table class="table table-bordered table-sm text-center">
            <thead>
                <tr><th rowspan=2>Category</th><th rowspan=2>Submissions</th>
                    <th colspan=4 data-content='A finding or conclusion about one or more subjects made by a Center based on submitted evidence'>Observations</th></tr>
                <tr><th data-content='Preliminary results of a screening campaign or large-scale computational analysis'>Tier 1</th>
                    <th data-content='Confirmation of primary results in a cancer-relevant in vitro model'>Tier 2</th>
                    <th data-content='Validation of results in a cancer-relevant in vivo model'>Tier 3</th>
                    <th>Total</th></tr>
            </thead>
            <tbody id='summary-table-body'>
            </tbody>
        </table>
        <div class="dark-separator"></div>

        <div class="container ctd2-boxes">
            <div class=row style='margin-bottom:-20px'>
                <div class="col-4">
                    <a class="btn btn-danger btn-block ctd2bigbutton" href="#explore/target/Biomarker,Target" role="button" id="explore-gene-button">genes and proteins</a>
                </div>
                <div class="col-4">
                    <a class="btn btn-danger btn-block ctd2bigbutton" href="#explore/compound/Perturbagen,Candidate Drug" role="button" id="explore-compound-button">compounds and perturbagens</a>
                </div>
                <div class="col-4">
                    <a class="btn btn-danger btn-block ctd2bigbutton" href="#explore/context/Disease" role="button" id="explore-disease-button">disease context</a>
                </div>
            </div>
            <div class=row>
                <div class="col-4">
                    <a class="btn btn-danger btn-block ctd2bigbutton" href="#explore/cellline/Cell Line" role="button" id="explore-celllines-button">cell lines</a>
                </div>
                <div class="col-4">
                    <a class="btn btn-danger btn-block ctd2bigbutton" href="#eco_browse" role="button" id="explore-eco-button">experimental evidence</a>
                </div>
                <div class="col-4">
                    <a class="btn btn-danger btn-block ctd2bigbutton" href="#stories" role="button" id="explore-stories-button">stories</a>
                </div>
            </div>
        </div>

        <div class="dark-separator"></div>
        <!-- Carousel
        ================================================== -->
        <div class="carousel slide">
          <div class="carousel-inner">
            <div class="item active">
              <div class="container">
                  <div class="carousel-caption">
                        <div class="well carousel-well">
                            <div class="row one-story">
                            <div class="col-3" style="text-align:center">
                                <h4>Recent Stories</h4>
                                <div class="pagination pagination-centered stories-pagination">
                                    <ul class="nav nav-tabs">
                                        <li class="nav-item"><a href="#story-1" class="nav-link story-link active">&bull;</a></li>
                                        <li class="nav-item"><a href="#story-2" class="nav-link story-link">&bull;</a></li>
                                        <li class="nav-item"><a href="#story-3" class="nav-link story-link">&bull;</a></li>
                                        <li class="nav-item"><a href="#story-4" class="nav-link story-link">&bull;</a></li>
                                    </ul>
                                </div>
                            </div>
                            <div class="tab-content stories-tabs col-9">
                                <div class="tab-pane active fade in show" id="story-1"></div>
                                <div class="tab-pane fade" id="story-2"></div>
                                <div class="tab-pane fade" id="story-3"></div>
                                <div class="tab-pane fade" id="story-4"></div>
                            </div>
                            </div>
                        </div>
                  </div>
              </div>
            </div>
          </div>

        </div><!-- /.carousel -->

        </div><!-- container overview-box -->
        </div><!-- overview-container -->

    </script>

    <script type="text/template" id="centers-tmpl">
        <div class="container common-container" id="centers-container">
            <h2>Centers</h2>
            <table class="table table-bordered table-striped table-compact nowrap" id="centers-list-table">
                <thead>
                    <tr>
                        <th class="center-image-column"></th>
                        <th>Center Name</th>
                        <th>Principal Investigator</th>
                        <th class="submission-count">Submissions</th>
                    </tr>
                </thead>
                <tbody id="centers-tbody">
                <!-- here will come the centers... -->
                </tbody>
            </table>

            <div class="rss-feed">
                Subscribe to the
                <a href="feed/submissions">
                    Submissions RSS feed
                </a>
                to receive alerts when new content is posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="stories-tmpl">
        <div class="container common-container" id="stories-container">
            <h2>Stories</h2>
            <table class="table table-bordered table-striped table-compact" id="stories-list">
                <thead>
                <tr>
                    <th class="center-image-column">Center</th>
                    <th>Description</th>
                    <th>Date</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody id="stories-tbody">
                <!-- here will come the stories... -->
                </tbody>
            </table>


            <div class="rss-feed">
                Subscribe to the
                <a href="feed/stories">
                    Stories RSS feed
                </a>
                to receive alerts when new content is posted to the site.
            </div>

        </div>
    </script>


    <script type="text/template" id="stories-tbl-row-tmpl">
        <tr>
            <td class="center-image-column">
                <a href="#{{submission.observationTemplate.submissionCenter.stableURL}}">
                    <img src="img/slogos/{{submission.observationTemplate.submissionCenter.displayName}}.png" alt="{{submission.observationTemplate.submissionCenter.displayName}}" data-content="{{submission.observationTemplate.submissionCenter.displayName}}" class="img-circle clickable-popover">
                </a>
            </td>
            <td>
                <b>{{submission.observationTemplate.description}}</b><br>
                <p id="story-list-summary-{{id}}" class="stories-text"></p>
            </td>
            <td class="story-date">
                <small>{{submission.submissionDate}}</small>
            </td>
            <td class="story-details">
                <a href="#{{submission.stableURL.replace("submission", "story")}}">view full story</a>
                <br>or<br>
                <a href="#{{stableURL}}">see observation</a>
            </td>
        </tr>
    </script>

    <script type="text/template" id="centers-tbl-row-tmpl">
        <tr>
            <td class="center-image-column">
                <a href="#{{stableURL}}">
                    <img src="img/{{displayName}}.png" alt="{{displayName}}" data-content="{{displayName}}" class="img-polaroid clickable-popover">
                </a>
            </td>
            <td class="center-name">
                <a href="#{{stableURL}}">
                    {{displayName}}
                </a>
            </td>
            <td class="center-pi">
                <span id="center-pi-{{id}}">loading...</span>
            </td>
            <td>
                <a href="#{{stableURL}}" id="submission-count-{{id}}">
                    loading...
                </a>
            </td>
            <td>{{pinned}}</td>
        </tr>
    </script>

    <script type="text/template" id="center-tmpl">
        <div class="container common-container" id="center-submission-container">
            <div class="row">
                <div class="col-9">
                    <h2 class="center-title">{{displayName}} <small>submissions</small></h2>
                    <div class="center-link-container">(<span class="center-link"><a href="https://ocg.cancer.gov/programs/ctd2/centers#{{ocg_dash}}" target="_blank">view center description</a></span>)</div>
                </div>
                <div class="col-3">
                    <img src="img/{{displayName}}.png" data-content="{{displayName}}" alt="{{displayName}}" class="img-polaroid noclick-popover" style="max-width:200px;max-height:50px">
                </div>
            </div>

            <div id="more-project-container"></div>

            <table id="center-submission-grid" class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Tier</th>
                        <th>Project</th>
                        <th>Description</th>
                        <th width="90">Date</th>
                        <th>Details</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- here will go the rows -->
                </tbody>
            </table>
        </div>
    </script>

    <script type="text/template" id="center-submission-tbl-row-tmpl">
        <tr>
            <td><span class="badge tier-badge">Tier {{observationTemplate.tier}}</span></td>
            <td>{{observationTemplate.project}}</td>
            <td>
                {{(observationTemplate.submissionDescription != "") ? observationTemplate.submissionDescription : observationTemplate.description}}
            </td>
            <td><small>{{submissionDate}}</small></td>
            <td width=150>
                <a href="#{{stableURL}}" class="obs-count" id="observation-count-{{id}}">{{details}}</a>
                <div style="font-size:70%">[<a href="<%=dataURL%>submissions/{{displayName}}.zip">Download</a>]</div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="submission-tmpl">
        <div class="container common-container" id="submission-container">
            <div class="row">
                <div class="col-10">
                    <h2>
                        Submission
                        <span class="badge-tier-container">
                            <span class="badge badge-tier">Tier {{observationTemplate.tier}}</span>
                        </span>
                    </h2>


                    <table id="submission-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Project</th>
                            <td><a href="#{{observationTemplate.submissionCenter.stableURL}}/{{observationTemplate.project.toLowerCase().replace(/[^a-zA-Z0-9]/g, '-')}}">{{observationTemplate.project}}</a></td>
                        </tr>
                        <tr>
                            <th>Description</th>
                            <td>{{observationTemplate.description}}</td>
                        </tr>
                        <tr id="similar-submission-info">
                            <th>Similar Submissions</th>
                            <td>
                                <ul class="similar-submission-list"></ul>
                            </td>
                        </tr>
                        <tr id="eco-row">
                            <th>Evidence Ontology Terms</th>
                            <td>
                                <ul id="eco-list"></ul>
                            </td>
                        </tr>
                        <tr>
                            <th width="175">Submission Date</th>
                            <td>{{submissionDate}}</td>
                        </tr>
                        <tr>
                            <th>Source Data</th>
                            <td><a href="<%=dataURL%>submissions/{{displayName}}.zip">download</a></td>
                        </tr>
                    </table>
                </div>
                <div class="col-2">
                    <a href="#{{observationTemplate.submissionCenter.stableURL}}">
                        <img src="img/{{observationTemplate.submissionCenter.displayName}}.png" class="img-polaroid clickable-popover" height=30 alt="{{observationTemplate.submissionCenter.displayName}}" data-content="{{observationTemplate.submissionCenter.displayName}}">
                    </a>

                    <br>
                    <br>

                    <img src="img/submission.png" class="img-polaroid noclick-popover" width=150 height=150 alt="Submission" data-content="Submission"><br>
                </div>
            </div>

            <div id="optional-submission-description">

            </div>

            <h3>Observations within this submission</h3>

            <div class="more-observations-message"></div>

            <table id="submission-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Observation Summary</th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                </tbody>
            </table>
            <p id=center-abbreviations >Center Abbreviations: Broad, BR; Cold Spring Harbor Laboratory, CSHL; Columbia University, CU; Dana-Farber Cancer Institute, DFCI; Emory, EM</p>
        </div>
    </script>

    <script type="text/template" id="submission-obs-tbl-row-tmpl">
        (<a class="button-link" href="#{{stableURL}}">details &raquo;</a>)
    </script>

    <script type="text/template" id="submission-tbl-row-tmpl">
        <tr>
            <td id="submission-observation-summary-{{id}}">
                Loading...
            </td>
         /tr>
    </script>

    <script type="text/template" id="observation-tmpl">
        <div class="container common-container" id="observation-container">

            <div class="row">
                <div class="col-10">
                    <h2>Observation <small>(Tier {{submission.observationTemplate.tier}})</small></h2>
                    <blockquote>
                        <p id="observation-summary"></p>
                    </blockquote>
                    <span id="view-full-story">(<a href="#{{submission.stableURL.replace("submission", "story")}}">view full story</a>)</span>

                    <table id="observed-subjects-grid" class="table table-bordered table-striped subjects">
                        <thead>
                        <tr>
                            <th width="60">&nbsp;&nbsp;&nbsp;&nbsp;</th>
                            <th>Name</th>
                            <th>Class</th>
                            <th>Role</th>
                            <th>Description</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!-- here will go the rows -->
                        </tbody>
                    </table>

                </div>
                <div class="col-2">
                    <a href="#{{submission.observationTemplate.submissionCenter.stableURL}}"><img src="img/{{submission.observationTemplate.submissionCenter.displayName}}.png" class="img-polaroid" width="120" alt="{{submission.observationTemplate.submissionCenter.displayName}}"></a>
                    <br><br>
                    <img src="img/observation.png" alt="Observation" class="img-polaroid" width=120 height=120><br>
                </div>
            </div>


            <h3>Submission <small>(<a href="#" id="small-show-sub-details">show details</a><a href="#" id="small-hide-sub-details">hide details</a>)</small></h3>
            <div>
                <table id="eco-grid" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>Evidence Ontology Code</th>
                        <th>Term Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <!-- ECO term content goes here -->
                    </tbody>
                </table>
            </div>
            <div id="obs-submission-details">
                <table class="table table-bordered table-striped">
                    <tr>
                        <th>Project</th>
                        <td><a href="#{{submission.observationTemplate.submissionCenter.stableURL}}/{{submission.observationTemplate.project.toLowerCase().replace(/[^a-zA-Z0-9]/g, '-')}}">{{submission.observationTemplate.project}}</a></td>
                    </tr>
                    <tr>
                        <th>Description</th>
                        <td>
                            <a href="#{{submission.stableURL}}">{{submission.observationTemplate.description}}</a>
                        </td>
                    </tr>
                    <tr id="obs-submission-summary">
                        <th>Summary</th>
                        <td>{{submission.observationTemplate.submissionDescription}}</td>
                    </tr>
                    <tr>
                        <th>Date</th>
                        <td>{{submission.submissionDate}}</td>
                    </tr>
                    <tr>
                        <th>Source Data</th>
                        <td><a href="<%=dataURL%>submissions/{{submission.displayName}}.zip">download</a></td>
                    </tr>


                </table>
            </div>


            <h3>Evidence</h3>
            <table id="observed-evidences-grid" class="table table-bordered table-striped evidences">
                <thead>
                <tr>
                    <th>&nbsp;&nbsp;</th>
                    <th class="nonewline">Type</th>
                    <th>Description</th>
                    <th width="150">Details</th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                </tbody>
            </table>

        </div>
    </script>

    <script type="text/template" id="similar-submission-item-tmpl">
        <li>
            <small><a href="#{{stableURL}}">{{observationTemplate.description}}</a></small>
        </li>
    </script>

    <script type="text/template" id="submission-description-tmpl">
        <h3>Submission summary</h3>
        <blockquote>
            <p>{{observationTemplate.submissionDescription}}</p>
        </blockquote>
    </script>

    <script type="text/template" id="summary-subject-replacement-tmpl"><a class="summary-replacement" href="#{{stableURL}}">{{displayName}}</a></script>

    <script type="text/template" id="summary-evidence-replacement-tmpl"><strong class="summary-replacement">{{displayName}}</strong></script>

    <script type="text/template" id="observedevidence-row-tmpl">
        <tr>
            <td>&nbsp;&nbsp;</td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>
                {{observedEvidenceRole.displayText}}</td>
            <td>{{displayName}}</td>
        </tr>
    </script>

    <script type="text/template" id="observedfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" data-content="Download file ({{evidence.mimeType}})" class="desc-tooltip">
                    download file
                </a>
            )</td>
        </tr>
    </script>

    <script type="text/template" id="observedhtmlfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" data-content="View file ({{evidence.mimeType}})" class="desc-tooltip html-story-link">
                    view
                </a>
                )</td>
        </tr>
    </script>


    <script type="text/template" id="observedpdffileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" data-content="{{observedEvidenceRole.displayText}}" class="desc-tooltip pdf-file-link">
                    view PDF
                </a>
                )</td>
        </tr>
    </script>

    <script type="text/template" id="observedgctfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view file <b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="http://www.broadinstitute.org/cancer/software/GENE-E/dynamic.php?data=<%=dataURL%>{{evidence.filePath}}" target="_blank" data-content="open in GENE-E (Java Web-start)" class="desc-tooltip">
                                open with GENE-E
                            </a>
                        </li>
                        <li>
                            <a href="<%=dataURL%>{{evidence.filePath}}" class="desc-tooltip" target="_blank" data-content="type: ({{evidence.mimeType}})">view in browser</a>
                        </li>

                    </ul>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="observedsiffileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view file <b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="#" data-description="{{observedEvidenceRole.displayText}}" data-sif-url="<%=dataURL%>{{evidence.filePath}}" target="_blank" data-content="open in Cytoscape.js" class="desc-tooltip cytoscape-view">
                                interactive network view
                            </a>
                        </li>
                        <li>
                            <a href="<%=dataURL%>{{evidence.filePath}}" class="desc-tooltip" target="_blank" data-content="type: ({{evidence.mimeType}})">view in browser</a>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="observedimageevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="image-evidence-wrapper">
                    <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" data-caption="{{observedEvidenceRole.displayText}}" rel="evidence-images" class="evidence-images">
                        <img src="<%=dataURL%>{{evidence.filePath}}" class="img-polaroid img-evidence desc-tooltip" height="140" data-content="File" alt="File">
                    </a>
                </div>
            </td>
        </tr>
    </script>


    <script type="text/template" id="observedlabelevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><div class="labelevidence expandable">{{displayName}}</div></td>
        </tr>
    </script>

    <script type="text/template" id="observedurlevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                (<a href="{{evidence.url.replace(/^\//, '')}}" target="_blank" class="desc-tooltip" data-content="Open link in a new window">
                    open link
                </a>)
            </td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>
                {{observedEvidenceRole.evidenceRole.displayName}}
                <a target="_blank" href="http://www.evidenceontology.org/term/{{eco.eco_id}}/" class=desc-tooltip data-content="ECO Term: {{eco.eco_term}} ({{eco.eco_id}})"><i class="icon-question-sign"></i></a>
            </td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><span class="numeric-value">{{evidence.numericValue}}</span> <em>{{evidence.unit}}</em></td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-val-tmpl">
        {{firstPart}} &times; 10<sup>{{secondPart}}</sup>
    </script>

    <script type="text/template" id="gene-uniprot-tmpl">
        <li id="gene-uniprot-link">
            UniProt: <a href="https://www.uniprot.org/uniprot/{{uniprotId}}" target="_blank">{{uniprotId}} <i class="icon-share"></i></a>
        </li>
    </script>

    <script type="text/template" id="gene-detail-references-tmpl">
        <ul style="padding-left:20px">
        <li>Entrez: <a href="http://www.ncbi.nlm.nih.gov/gene/{{entrez}}" target="_blank">{{entrez}} <i class="icon-share"></i></a></li>
        {{genecards ? "<li>GeneCards: <a target='_blank' href='https://www.genecards.org/cgi-bin/carddisp.pl?gene=" + genecards + "'>" + genecards + " <i class='icon-share'></i></a></li>" : "" }}
        {{dave ? "<li>Data Analysis, Visualization, and Exploration (DAVE): <a target='_blank' href='https://portal.gdc.cancer.gov/genes/" + dave + "'>" + dave + " <i class='icon-share'></i></a></li>" : "" }}
        <li>Harmonizome: <a href="https://maayanlab.cloud/Harmonizome/gene/{{gene_symbol}}" target="_blank">{{gene_symbol}}</a></li>
        <li>UniProt: <a href="https://www.uniprot.org/uniprot/{{uniprot}}" target="_blank">{{uniprot}} <i class="icon-share"></i></a></li>
        </ul>
    </script>

    <script type="text/template" id="gene-tmpl">
         <div class="container common-container" id="gene-container">
             <h2>{{displayName}}</h2>

             <div class="row">
                 <div class="col-9">
                     <table id="gene-details-grid" class="table table-bordered table-striped">
                         <tr>
                             <th>Gene symbol<div style="font-size:10px; font-style:italic">(from HGNC)</div></th>
                             <td>{{displayName}}&nbsp;&nbsp;
                                  <a href="#" class="addGene-{{displayName}} cartAddPlus" data-content="Add gene to cart">+</a>
                             </td>
                         </tr>
                         <tr><th>Gene Name</th><td>{{fullName}}</td></tr>
                         <tr><th>Gene Type</th><td>{{geneType.displayName}}</td></tr>
                         <tr><th>Map Location</th><td>{{mapLocation}}</td></tr>
                         <tr>
                             <th>Synonyms/Related terms<div style="font-size:10px; font-style:italic">(from Entrez)</div></th>
                             <td>
                                 <ul class="synonyms"></ul>
                                 <button type="button" class="btn btn-outline-dark" id=see-all-switch></button>
                             </td>
                         </tr>
                         <tr>
                             <th>Organism</th>
                             <td>{{organism.displayName}}</td>
                         </tr>
                         <tr>
                             <th>References</th>
                             <td>
                                 <ul class="refs">
                                     <li>
                                         Entrez: <a href="http://www.ncbi.nlm.nih.gov/gene/{{entrezGeneId}}" target="_blank">{{entrezGeneId}} <i class="icon-share"></i></a>
                                     </li>
                                     {{genecard ? "<li>GeneCards: <a target='_blank' href='https://www.genecards.org/cgi-bin/carddisp.pl?gene=" + genecard + "'>" + genecard + " <i class='icon-share'></i></a></li>" : "" }}
                                     {{dave ? "<li>Data Analysis, Visualization, and Exploration (DAVE): <a target='_blank' href='https://portal.gdc.cancer.gov/genes/" + dave + "'>" + dave + " <i class='icon-share'></i></a></li>" : "" }}
                                     <li>Harmonizome: <a href="https://maayanlab.cloud/Harmonizome/gene/{{displayName}}" target="_blank">{{displayName}}</a></li>
                                 </ul>
                             </td>
                         </tr>
                         <tr>
                             <th>Genomic alterations</th>
                             <td>
                                 <a class="btn btn-small" href="http://cbioportal.org/ln?q={{displayName}}" target="_blank">view in cBioPortal <i class="icon-share"></i></a>
                             </td>
                         </tr>
                     </table>
                 </div>
                 <div class="col-3">
                     <h4>Gene</h4>
                     <img src="img/gene.png" class="img-polaroid" width=175 height=175 alt="Gene">
                 </div>
             </div>

             <h3>Related observations <small id=observation-filter></small></h3>
             <div>
                <button type="button" class="btn btn-secondary" id=subject-wordcloud-button><span id=subject-wordcloud-toggle-word>Show</span> Connected Subjects Word Cloud</button>
                <i class="fas fa-cog fa-lg" style="color:SteelBlue;" id="config-wordcloud"></i>
                <i class="fas fa-download fa-lg" style="color:SteelBlue;" id='download-wordcloud'></i>
                <span id="subject-id" style="display:none"></span>
                <div id=subject-wordcloud class=wordcloud></div>
             </div>

             <table id="gene-observation-grid" class="table table-bordered table-striped observations">
                 <thead>
                 <tr>
                     <th>Date</th>
                     <th width=500>Observation Summary</th>
                     <th>Tier</th>
                     <th>Center</th>
                     <th></th><!--hidden-->
                 </tr>
                 </thead>
                 <tbody>
                 <!-- here will go the rows -->
                 <tr class="subject-observations-loading">
                     <td colspan="5">
                         <h3>Loading observations...</h3>
                         <div class="progress progress-striped active">
                             <div class="bar" style="width: 100%;"></div>
                         </div>
                     </td>
                 </tr>
                 </tbody>
             </table>

             <div class="rss-feed">
                 Subscribe to the
                 <a href="feed/search/{{displayName}}">
                     '{{displayName}}' RSS feed
                 </a>
                 to receive alerts when new observations are posted to the site.
             </div>
         </div>
    </script>

    <script type="text/template" id="protein-tmpl">
        <div class="container common-container" id="protein-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="col-9">
                    <table id="protein-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Gene symbol<div style="font-size:10px; font-style:italic">(from HGNC)</div></th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms/Related terms<div style="font-size:10px; font-style:italic">(from Entrez)</div></th>
                            <td>
                                <ul class="synonyms"></ul>
                                <button type="button" class="btn btn-outline-dark" id=see-all-switch></button>
                            </td>
                        </tr>
                        <tr>
                            <th>Transcripts</th>
                            <td>
                                <ul class="transcripts"></ul>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                        <tr>
                            <th>References</th>
                            <td>
			        UniProt ID: <a href="http://www.uniprot.org/uniprot/{{uniprotId}}" target="_blank">{{uniprotId}} <i class="icon-share"></i></a> <br>
                    <!--
                    Harmonizome: <a href="https://maayanlab.cloud/Harmonizome/gene/{{uniprotId}}" target="_blank">{{uniprotId}}</a>
                    -->
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="col-3">
                    <h4>Protein</h4>
                    <img src="img/protein.png" class="img-polaroid" width=175 height=175 alt="Protein">
                </div>
            </div>

            <h3>Related observations <small id=observation-filter></small></h3>

            <table id="protein-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="rss-feed">
                Subscribe to the
                <a href="feed/search/{{displayName}}">
                    '{{displayName}}' RSS feed
                </a>
                to receive alerts when new observations are posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="rna-tmpl">
        <div class="container common-container" id="shrna-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="col-9">
                    <table id="shrna-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Target Sequence</th>
                            <td>{{targetSequence}}</td>
                        </tr>
                        <tr>
                            <th>Target Transcript</th>
                            <td>
                                <a
                                    href="#{{transcript?transcript.stableURL:''}}">{{transcript?transcript.refseqId:''}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Target Gene</th>
                            <td>
                                <a
                                    href="#{{transcript?transcript.gene.stableURL:''}}">{{transcript?transcript.gene.displayName:''}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-3">
                    <h4>{{type=='sirna'?'siRNA':'shRNA'}}</h4>
                    <img src="img/{{type}}.png" class="img-polaroid" width=175 height=175 alt="{{type=='sirna'?'siRNA':'shRNA'}}">
                </div>
            </div>

            <h3>Related observations <small id=observation-filter></small></h3>

            <table id="shrna-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="rss-feed">
                Subscribe to the
                <a href="feed/search/{{displayName}}">
                    '{{displayName}}' RSS feed
                </a>
                to receive alerts when new observations are posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="transcript-tmpl">
        <div class="container common-container" id="transcript-container">
            <h2>{{refseqId}}</h2>

            <div class="row">
                <div class="col-9">
                    <table id="transcript-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Gene</th>
                            <td>
                                <a href="#{{gene.stableURL}}">{{gene.displayName}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                        <tr>
                            <th>References</th>
                            <td>
                                Entrez: <a href="http://www.ncbi.nlm.nih.gov/nuccore/{{refseqId}}" target="blank">{{refseqId}} <i class="icon-share"></i></a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="col-3">
                    <h4>Transcript</h4>
                    <img src="img/transcript.png" class="img-polaroid" width=175 height=175 alt="Transcript">
                </div>
            </div>

            <h3>Related observations <small id=observation-filter></small></h3>

            <table id="transcript-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="rss-feed">
                Subscribe to the
                <a href="feed/search/{{displayName}}">
                    '{{displayName}}' RSS feed
                </a>
                to receive alerts when new observations are posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="tissuesample-tmpl">
        <div class="container common-container" id="tissuesample-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="col-9">
                    <table id="tissuesample-details-grid" class="table table-bordered table-striped">
                        <tr id="tissue-synonyms">
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
                                <button type="button" class="btn btn-outline-dark" id=see-all-switch></button>
                            </td>
                        </tr>
                        <tr id="tissue-refs">
                            <th>References</th>
                            <td>
                                <ul class="xrefs">
                                      {{diseaseOntology ? "<li>Disease ontology: <a target='_blank' href='http://disease-ontology.org/term/" + diseaseOntology + "'>" + diseaseOntology + " <i class='icon-share'></i></a></li>" : ""}}
                                      {{malacards ? "<li>MalaCards: <a target='_blank' href='https://www.malacards.org" + malacards + "'>" + malacards + " <i class='icon-share'></i></a></li>" : ""}}
                                      {{depmap ? "<li>DepMap: <a target='_blank' href='https://depmap.org/portal/context/" + depmap + "'>" + depmap + "</a> <i class='icon-share'></i></li>" : ""}}
                                </ul>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="col-3">
                    <h4>Tissue Sample</h4>
                    <img src="img/tissuesample.png" class="img-polaroid" width=175 height=175 alt="Tissue sample">
                </div>
            </div>

            <h3>Related observations <small id=observation-filter></small></h3>
            <div>
                <button type="button" class="btn btn-secondary" id=subject-wordcloud-button><span id=subject-wordcloud-toggle-word>Show</span> Connected Subjects Word Cloud</button>
                <i class="fas fa-cog fa-lg" style="color:SteelBlue;" id="config-wordcloud"></i>
                <i class="fas fa-download fa-lg" style="color:SteelBlue;" id='download-wordcloud'></i>
                <span id="subject-id" style="display:none"></span>
                <div id=subject-wordcloud class=wordcloud></div>
            </div>

            <table id="tissuesample-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>


        </div>
    </script>

    <script type="text/template" id="cellsample-tmpl">
        <div class="container common-container" id="cellsample-container">
            <h2>{{displayName}}</h2>
            <div class="row">
                <div class="col-9">
                    <table id="cellsample-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Name</th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
                                <button type="button" class="btn btn-outline-dark" id=see-all-switch></button>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                        <tr>
                            <th>Annotations</th>
                            <td id="annotations">
                                <ul></ul>
                            </td>
                        </tr>
                        <tr>
                            <th>References</th>
                            <td>
                                <ul class="refs">
                                    {{cosmic ? "<li>COSMIC cell line: <a target='_blank' href='https://cancer.sanger.ac.uk/cell_lines/sample/overview?id=" + cosmic + "'>" + cosmic + " <i class='icon-share'></i></a></li>" : "" }}
                                    {{cellosaurus ? "<li>Cellosaurus: <a target='_blank' href='https://web.expasy.org/cellosaurus/" + cellosaurus + "'>" + cellosaurus + " <i class='icon-share'></i></a></li>" : "" }}
                                    {{depmap ? "<li>DepMap: <a target='_blank' href='https://depmap.org/portal/cell_line/" + depmap + "'>" + depmap + "</a> <i class='icon-share'></i></li>" : ""}}
                                </ul>
                            </td>
                        </tr>
                        <tr id="cbiolink">
                            <th>Genomic alterations</th>
                            <td>
                                <a class="btn btn-small" href="https://www.cbioportal.org/patient?studyId=cellline_ccle_broad&sampleId={{cbioPortalId}}" target="_blank">view in cBioPortal <i class="icon-share"></i></a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="col-3">
                        <h4>Cell Sample</h4>
                        <img src="img/cellsample.png" class="img-polaroid" width=175 height=175 alt="Cell sample">
                </div>
            </div>
            <h3>Related observations <small id=observation-filter></small></h3>
            <div>
                <button type="button" class="btn btn-secondary" id=subject-wordcloud-button><span id=subject-wordcloud-toggle-word>Show</span> Connected Subjects Word Cloud</button>
                <i class="fas fa-cog fa-lg" style="color:SteelBlue;" id="config-wordcloud"></i>
                <i class="fas fa-download fa-lg" style="color:SteelBlue;" id='download-wordcloud'></i>
                <span id="subject-id" style="display:none"></span>
                <div id=subject-wordcloud class=wordcloud></div>
            </div>

            <table id="cellsample-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="rss-feed">
                Subscribe to the
                <a href="feed/search/{{displayName}}">
                    '{{displayName}}' RSS feed
                </a>
                to receive alerts when new observations are posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="ecoterm-tmpl">
        <div class="container common-container" id="animalmodel-container">
            <h2>{{displayName}}</h2>
            <div class="row">
                <div class="col-9">
                    <table id="animalmodel-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Name</th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Code</th>
                            <td>{{code}}</td>
                        </tr>
                        <tr>
                            <th>Definition</th>
                            <td>{{definition}}</td>
                        </tr>

                        <tr>
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
                            </td>
                        </tr>
                        <tr>
                            <th>References</th>
                            <td><a href="http://purl.obolibrary.org/obo/{{code.replace(':','_')}}" target='_blank'>Ontobee</a>
                                <br/><a href='https://www.ebi.ac.uk/QuickGO/term/{{code}}' target='_blank'>QuickGO</a>
                                <br/><a href="http://www.evidenceontology.org/browse/#{{code.replace(':','_')}}" target='_blank'>Evidence Ontology Browser</a>
                                <br/><a href="http://www.evidenceontology.org/term/{{code}}/" target='_blank'>Evidence Ontology Term Page</a></td>
                        </tr>
                    </table>
                </div>
            </div>
            <h3>Related observations <small>{{tier?"and tier "+tier:""}}</small></h3>

            <table id="ecoterm-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>


            <div class="rss-feed">
                Subscribe to the
                <a href="feed/search/{{code}}">
                    '{{code}}' RSS feed
                </a>
                to receive alerts when new observations are posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="animalmodel-tmpl">
        <div class="container common-container" id="animalmodel-container">
            <h2>{{displayName}}</h2>
            <div class="row">
                <div class="col-9">
                    <table id="animalmodel-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Name</th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
                                <button type="button" class="btn btn-outline-dark" id=see-all-switch></button>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-3">
                    <h4>Animal Model</h4>
                    <img src="img/animalmodel.png" class="img-polaroid" width=175 height=175 alt="Animal model">
                </div>
            </div>
            <h3>Related observations <small id=observation-filter></small></h3>

            <table id="animalmodel-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
                    <th></th><!--hidden-->
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="subject-observations-loading">
                    <td colspan="5">
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>


            <div class="rss-feed">
                Subscribe to the
                <a href="feed/search/{{displayName}}">
                    '{{displayName}}' RSS feed
                </a>
                to receive alerts when new observations are posted to the site.
            </div>

        </div>
    </script>

    <script type="text/template" id="compound-tmpl">
          <div class="container common-container" id="compound-container">
              <h2>{{displayName}}</h2>

              <div class="row">
                  <div class="col-9">
                      <table id="compund-details-grid" class="table table-bordered table-striped">
                          <tr>
                              <th>Name</th>
                              <td>{{displayName}}</td>
                          </tr>
                          <tr>
                              <th>Synonyms</th>
                              <td>
                                  <ul class="synonyms"></ul>
                                  <button type="button" class="btn btn-outline-dark" id=see-all-switch></button>
                              </td>
                          </tr>
                          <tr>
                              <th>SMILES</th>
                              <td><small>{{smilesNotation}}</small></td>
                          </tr>
                          <tr>
                              <th>Compounds with <br/>shared targets <i class="icon-question-sign rc-tooltip" data-content="Within parentheses, the gene symbols of the shared targets"></i></th>
                              <td>
                                  <ul class="synonyms" id="related-compounds"></ul>
                                  <button type="button" class="btn btn-outline-dark" id=see-all-compounds-switch>see all</button>
                              </td>
                          </tr>
                          <tr>
                              <th>References</th>
                              <td>
                                  <ul class="compound-xrefs">
                                      {{pubchem ? "<li>PubChem: <a target='_blank' href='https://pubchem.ncbi.nlm.nih.gov/compound/" + pubchem + "'>" + pubchem + "</a> <i class='icon-share'></i></li>" : ""}}
                                      {{drugbank ? "<li>DrugBank: <a target='_blank' href='https://www.drugbank.ca/drugs/" + drugbank + "'>" + drugbank + "</a> <i class='icon-share'></i></li>" : ""}}
                                      {{ctrpID ? (ctrpName ? "<li>CTRP: <a target='_blank' href='http://portals.broadinstitute.org/ctrp.v2.2/?compoundId="+ctrpID+"&compoundName=" + ctrpName + "'>" + ctrpName + "</a> <i class='icon-share'></i></li>" : "") : ""}}
                                      {{depmap ? "<li>DepMap: <a target='_blank' href='https://depmap.org/portal/compound/" + depmap + "'>" + depmap + "</a> <i class='icon-share'></i></li>" : ""}}
                                      {{cas ? "<li>CAS: " + cas + "</li>" : ""}}

                                  </ul>
                              </td>
                          </tr>
                      </table>
                  </div>
                  <div class="col-3">
                      <h4>Compound</h4>
                      <a href="<%=dataURL%>compounds/{{imageFile}}" target="_blank" class="compound-image clickable-popover" data-content="Compound: {{displayName}}">
                        <img class="img-polaroid" width=200 src="<%=dataURL%>compounds/{{imageFile}}" alt="Compound: {{displayName}}">
                      </a>
                  </div>
              </div>

              <h3>Related observations <small id=observation-filter></small></h3>
                <div>
                    <button type="button" class="btn btn-secondary" id=subject-wordcloud-button><span id=subject-wordcloud-toggle-word>Show</span> Connected Subjects Word Cloud</button>
                    <i class="fas fa-cog fa-lg" style="color:SteelBlue;" id="config-wordcloud"></i>
                    <i class="fas fa-download fa-lg" style="color:SteelBlue;" id='download-wordcloud'></i>
                    <span id="subject-id" style="display:none"></span>
                    <div id=subject-wordcloud class=wordcloud></div>
                </div>

              <table id="compound-observation-grid" class="table table-bordered table-striped observations">
                  <thead>
                  <tr>
                      <th>Date</th>
                      <th width=500>Observation Summary</th>
                      <th>Tier</th>
                      <th>Center</th>
                      <th></th><!--hidden-->
                  </tr>
                  </thead>
                  <tbody>
                  <!-- here will go the rows -->
                  <tr class="subject-observations-loading">
                      <td colspan="5">
                          <h3>Loading observations...</h3>
                          <div class="progress progress-striped active">
                              <div class="bar" style="width: 100%;"></div>
                          </div>
                      </td>
                  </tr>
                  </tbody>
              </table>


              <div class="rss-feed">
                  Subscribe to the
                  <a href="feed/search/{{displayName}}">
                      '{{displayName}}' RSS feed
                  </a>
                  to receive alerts when new observations are posted to the site.
              </div>

          </div>
     </script>

    <script type="text/template" id="observedsubject-summary-row-tmpl">
        <tr>
            <td id="subject-image-{{id}}"></td>
            <td>
                <a href="#{{subject.stableURL}}">
                    {{subject.displayName}}
                </a>
            </td>
            <td>{{subject.type}}</td>
            <td class=subject_role>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>{{observedSubjectRole.displayText}}</td>
        </tr>
    </script>

    <script type="text/template" id="observedsubject-gene-summary-row-tmpl">
        <tr>
            <td id="subject-image-{{id}}"></td>
            <td id="subject.displayName-{{id}}">
                <a href="#{{subject.stableURL}}">
                    {{subject.displayName}}
                </a>  &nbsp;
                <a href="#" class="addGene-{{subject.displayName}} cartAddPlus" data-content="Add gene to cart" >+</a>
            </td>
            <td>{{subject.type}}</td>
            <td class=subject_role>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>{{observedSubjectRole.displayText}}</td>
        </tr>
    </script>

    <script type="text/template" id="observation-row-tmpl">
        <tr submission_id={{submission.id}} {{extra}}>
            <td>
                <a href="#{{stableURL}}">
                    {{submission.submissionDate}}
                </a>
            </td>
            <td id="observation-summary-{{id}}">
                Loading...
            </td>
            <td><span class="badge tier-badge">Tier {{submission.observationTemplate.tier}}</span></td>
            <td>
                <a href="#{{submission.observationTemplate.submissionCenter.stableURL}}">
                    <img alt="{{submission.observationTemplate.submissionCenter.displayName}}" data-content="{{submission.observationTemplate.submissionCenter.displayName}}" class="clickable-popover" width="150" src="img/{{submission.observationTemplate.submissionCenter.displayName}}.png">
                </a>
                <span class="hide-text">{{submission.observationTemplate.submissionCenter.displayName}}</span>
            </td>
            <td>{{ontology}}</td>
        </tr>
    </script>

    <script type="text/template" id="search-empty-tmpl">
        <tr>
            <td colspan="7">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <h3>Sorry, no results found</h3>
                    <p id=suggesting-wildcard>
                        Would you like to extend your search with a wildcard?
                        (<i>e.g.</i> <a href="#/search/{{encodeURIComponent(term)}}*">{{term.replaceAll("`","'")}}*</a>)
                    </p>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="html-story-container-tmpl">
        <div class="container common-container" id="story-container">
            <img src="img/{{centerName}}.png" alt="{{centerName}}" data-content="{{centerName}}" height="50" class="fancy-story-img img-polaroid noclick-popover">
            {{story}}
        </div>
    </script>

    <script type="text/template" id="search-results-image-tmpl">
        <a href="#/{{stableURL}}">
            <img src="{{image}}" class="img-polaroid search-info" data-content="{{label}}" alt="{{label}}" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-result-row-tmpl">
        <tr>
            <td id="search-image-{{id}}"></td>
            <td>
                <img src="img/onto.png" style="float: right;display:{{ontology?'block':'none'}}">
                <a href="#{{stableURL}}">{{subjectName}}</a><br>
                <i>{{organismName ? "(" + organismName + ")" : ""}}</i>
            </td>
            <td>
                <ul id="synonyms-{{id}}">
                    <!-- here will go the synonyms -->
                </ul>
                <button type="button" class="btn btn-outline-dark" id=see-all-switch{{id}}></button>
            </td>
            <td>{{className}}</td>
            <td>
                <ul id="roles-{{id}}">
                    <!-- here will go the roles -->
                </ul>
            </td>
            <td class="nonewline">
                <a href="#{{stableURL}}" id="subject-observation-count-{{id}}" count="{{observationCount}}">{{observationCount}}</a>
                <br/>(from {{centerCount}} Center{{centerCount > 1 ? 's' : ''}})
            </td>
            <td>{{matchNumber}}</td>
            <td>{{ontology}}</td>
        </tr>
    </script>

    <script type="text/template" id="search-result-gene-row-tmpl">
        <tr>
            <td id="search-image-{{id}}"></td>
            <td>
                <img src="img/onto.png" style="float: right;display:{{ontology?'block':'none'}}">
                <a href="#{{stableURL}}">{{subjectName}}</a>
                <a href="#" class="addGene-{{subjectName}} cartAddPlus" data-content="Add gene to cart" >+</a>
                <br>
                <i>{{organismName ? "(" + organismName + ")" : ""}}</i>
            </td>
            <td>
                <ul id="synonyms-{{id}}">
                    <!-- here will go the synonyms -->
                </ul>
                <button type="button" class="btn btn-outline-dark" id=see-all-switch{{id}}></button>
            </td>
            <td>{{className}}</td>
            <td>
                <ul id="roles-{{id}}">
                    <!-- here will go the roles -->
                </ul>
            </td>
            <td class="nonewline">
                <a href="#{{stableURL}}" id="subject-observation-count-{{id}}" count="{{observationCount}}">{{observationCount}}</a>
                <br/>(from {{centerCount}} Center{{centerCount > 1 ? 's' : ''}})
            </td>
            <td>{{matchNumber}}</td>
            <td>{{ontology}}</td>
        </tr>
    </script>

    <script type="text/template" id="search-tmpl">
        <div class="container common-container" id="search-results-container">
            <span class="d-inline-block" tabindex="0" id=ontology-search-wrapper style="float: right;">
            <button class="btn btn-primary" id="ontology-search">Ontology Search</button>
            </span>
            <button class="btn btn-primary" style="position: fixed;top: 50%;left: 50%;transform: translate(-50%, -50%);z-index: 2;display:none" id='ontology-spinner'>
                <span class="spinner-border spinner-border-sm"></span>
                Searching...
            </button>
            <h2>Search <small>for <i>{{term.replaceAll("`","'")}}</i></small></h2>
            <div style='padding: 20px 0px; width=100%'>
            <a href="" onclick="document.getElementById('submission-search-results').scrollIntoView(); return false" id=submission-summary-link>
                See <span id=submission-summary></span> from <span id=center-summary></span></a> 
            | <a href="" onclick="document.getElementById('observation-search-results').scrollIntoView(); return false" id=observation-summary-link>
                See <span id=observation-count></span> observations matching all search terms</a>
            </div>

            <div id='no-result'></div>
            <div class="alert alert-warning" style="display:none" id="oversize-message">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <p>
                    Only top 100 of <span id=oversized></span> subjects are listed in the table below.
                    Please make the query string more specific, e.g., quote the multi-word phrases.
                </p>
            </div>
            <div id=onto-legend style="display:none">Ontology search results flagged in Names column with <img src="img/onto.png"></div>
            <div id=no-onto-legend style="display:none">Ontology search found no additional terms</div>
            <table id="search-results-grid" class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>&nbsp; &nbsp;</th>
                    <th>Name</th>
                    <th>Synonyms</th>
                    <th>Class</th>
                    <th>Roles</th>
                    <th>Observations</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr id="loading-row">
                    <td colspan="7">
                        <h3>Searching...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div id="submission-search-results">
                <h3>Submission descriptions or summaries matching any search term</h3>
                <table id="searched-submissions" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>&nbsp; &nbsp;</th>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Center</th>
                        <th>Tier</th>
                        <th>Details</th>
                        <th></th><!--hidden-->
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>

            <div id="observation-search-results">
                <h3>Observations matching all search terms</h3>
                <div class="alert alert-warning" style="display:none" id="oversize-message-observations">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <p>
                        Only 100 of <span id=oversized-observations></span> observations are listed in the table below.
                    </p>
                </div>
                <table id="searched-observation-grid" class="table table-bordered table-striped observations">
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th width=500>Observation Summary</th>
                        <th>Tier</th>
                        <th>Center</th>
                        <th></th><!-- hidden -->
                    </tr>
                    </thead>
                    <tbody>
                    <!-- here will go the rows -->
                    <tr class="subject-observations-loading">
                        <td colspan="5">
                            <h3>Loading observations...</h3>
                            <div class="progress progress-striped active">
                                <div class="bar" style="width: 100%;"></div>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

        </div>
    </script>

    <script type="text/template" id="search-submission-tbl-row-tmpl">
        <tr>
            <td><a href="#{{stableURL}}"><img src="img/submission.png" width="50" alt="Submission" class=clickable-popover data-content="Submission"></a></td>
            <td><a href="#{{stableURL}}">{{submissionDate}}</a></td>
            <td>{{description}} <img src="img/onto.png" style="float: right;display:{{ontology?'block':'none'}}"></img></td>
            <td><a href="#{{stableURL}}"><img src="img/{{centerName}}.png" class=clickable-popover data-content="{{centerName}}" alt="{{centerName}}" height="50"></a></td>
            <td><span class="badge tier-badge">Tier {{tier}}</span></td>
            <td width=150>
                <a href="#{{stableURL}}" id="search-observation-count-{{id}}">{{observationCount}} observations</a>
            </td>
            <td>{{ontology}}</td>
        </tr>
    </script>

    <script type="text/template" id="synonym-item-tmpl">
        <li class="synonym {{toomany}} synonym-of-{{sid}}"><small>{{displayName}}</small></li>
    </script>

    <script type="text/template" id="role-item-tmpl">
        <li class="synonym"><small>{{role}}</small></li>
    </script>

    <script type="text/template" id="transcript-item-tmpl">
        <li class="synonym"><a href="#{{stableURL}}">{{refseqId}}</a></li>
    </script>

    <script type="text/template" id="count-story-tmpl">
        Read story
    </script>

    <script type="text/template" id="count-observations-tmpl">
        {{count}} observation{{count == 1 ? "" : "s"}}
    </script>

    <script type="text/template" id="count-submission-tmpl">
        {{count}} submission{{count == 1 ? "" : "s"}}
    </script>


    <script type="text/template" id="cytoscape-tmpl">
        <div id="cytoscape-sif"></div>
        <div class="well sif-legend">
            {{description}}
        </div>
    </script>

    <script type="text/template" id="story-homepage-tmpl">
        <h4>{{submission.observationTemplate.description}}</h4>
        <p><a href="#story-summary-{{id}}" data-toggle="collapse">details</a>
            | <a href="#{{submission.stableURL.replace('submission', 'story')}}">view full story</a>
            | <a href="#{{stableURL}}">see observation</a>
            | <a href="#stories">all stories</a>
        </p>
        <p id="story-summary-{{id}}" class="stories-text collapse">
            <!-- leaving this blank, we have to construct the summary from the scratch. -->
        </p>
    </script>

    <script type="text/template" id="text-blurb">
        <div class="alert alert-warning">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p>
                Entries listed below are ordered by relevance, based on the number of Centers providing observations and Tier evidence level.
                For each Center, only its highest Tier of evidence counts toward the score.
                Entries must have Tier 2 evidence or higher to be in the list.
                Currently displaying observations involving {{subject_type}} that have been assigned one of the following roles:
                {{ decodeURIComponent(roles).split(",").join(", ") }}
                (see <a class="blurb-help">background information</a> for the meaning of observations, roles, and Tiers).
            </p>
        </div>
    </script>

    <script type="text/template" id="eco-browse-tmpl">
        <div class="container common-container" id="explore-container">
            <h2>Explore: <i>Evidence terms</i></h2>

            <div id="eco-browse-blurb">
                <div class="alert alert-warning">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <p>
                        This table summarizes submissions and observations based on the experimental techniques used to generate them.
                        This is realized by annotating the various submissions with terms drawn from the
                        <a href='http://www.evidenceontology.org/' target='_blank'>Evidence Ontology</a>. 
                        <p>Note - not all submissions have been annotated with Evidence Ontology codes.
                    </p>
                </div>
            </div>

            <table class="table table-bordered table-striped observations" id="eco-browse-table">
                <thead>
                <tr>
                    <th colspan=2></th>
                    <th colspan=3 style="text-align:center" data-content="A finding or conclusion about one or more subjects made by a Center based on submitted evidence">Observations</th>
                </tr>
                <tr>
                    <th>Name</th>
                    <th>Submissions</th>
                    <th>Tier 3</th>
                    <th>Tier 2</th>
                    <th>Tier 1</th>
                </tr>
                </thead>
                <tbody id="eco-browse-items" style='white-space: nowrap;'>
                    <!-- here will go the rows -->
                </tbody>
            </table>

        </div>
    </script>

    <script type="text/template" id="explore-tmpl" data-url="<%=dataURL%>">
        <div class="container common-container" id="explore-container">
            <h2>Explore: <i>{{rolesLabel}}</i></h2>

            <div id="explore-blurb"></div>
            <div class="container" style="padding-bottom:5px;">
            <button type="button" class="btn btn-outline-dark" id="reset-ordering">Reset initial ordering</button>
            <button type="button" class="btn btn-outline-dark" id="customize-roles">Select Roles</button>
            </div>

            <table class="table table-bordered table-striped observations" id="explore-table">
                <thead>
                <tr>
                    <th colspan=3></th>
                    <th colspan=3 style="text-align:center" data-content="A finding or conclusion about one or more subjects made by a Center based on submitted evidence">Observations</th>
                </tr>
                <tr>
                    <th>Class</th>
                    <th>Name</th>
                    <th>Role</th>
                    <th>Tier 3</th>
                    <th>Tier 2</th>
                    <th>Tier 1</th>
                </tr>
                </thead>
                <tbody id="explore-items" style='white-space: nowrap;'>
                    <!-- here will go the rows -->
                </tbody>
            </table>

        </div>

        <div class="modal hide fade" id="role-modal">
        <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Select roles</h3>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            </div>
            <div class="modal-body">
                <p>Please select roles of interest from the list below.</p>

                <table class="table table-bordered table-compact table-striped" id="customized-roles-tbl">
                    <thead>
                        <tr>
                            <th>Role</th>
                            <th>Show</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="btn btn-block btn-info" data-dismiss="modal" id="select-roles-button">Select</button>
            </div>
        </div>
        </div>
        </div>
    </script>

    <script type="text/template" id="customize-roles-item-tmpl">
        <tr>
            <td>{{displayName}}</td>
            <td><input {{checked ? "checked " : ""}} type="checkbox" data-role="{{displayName}}"></td>
        </tr>
    </script>

    <script type="text/template" id="observedmrafileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" data-content="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view mra file<b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="#{{stableURL}}" data-content="Open Master Regulator View" class="desc-tooltip">
                                mra view
                            </a>
                        </li>
                        <li>
                            <a href="<%=dataURL%>{{evidence.filePath}}" class="desc-tooltip" target="_blank" data-content="type: ({{evidence.mimeType}})">view in browser</a>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="mra-view-tmpl" mra-data-url="<%=dataURL%>">
         <div class="container common-container" id="mra-container" > 
               <div class="row">
                 <div class="col-1"></div>
                 <div class="col-10">
                    <h2>Master Regulator View</h2>
                   
                    <table id="master-regulator-grid" class="table table-bordered table-striped ">
                        <thead>
                        <tr>
                            <th width="20">&nbsp;</th>
                            <th>Master Regulator</th>
                            <th>Score</th>
                            <th>Markers in regulon</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!-- here will go the rows -->
                        </tbody>
                     </table>  
                 </div>
                 <div class="col-1"></div>
             </div>
                   <br/>
                   <br/>
                   <div>
                      <b>Nodes Limit:</b>	
                      <select id="cytoscape-node-limit">
                           <option value="25">25</option>
                           <option value="50">50</option>
                           <option value="100" selected="selected">100</option>
                           <option value="200">200</option>
                           <option value="300">300</option>
                           <option value="400">400</option>
                           <option value="500">500</option>
                      </select>
                      <b>&nbsp;&nbsp;&nbsp;</b>  
                      <b>Layout:</b>	
                      <select id="cytoscape-layouts">
                           <option value="cola" selected="selected">Cola</option>
                           <option value="grid">Grid</option>
                           <option value="random">Random</option>
                           <option value="circle">Circle</option>
                      </select>
                      <b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>
                      <a href="#" data-description="{{observedEvidenceRole.displayText}}" target="_blank" data-content="please select master regulator to create network" class="mra-cytoscape-view clickable-popover">Create Network</a>   				 
                      <br/>
                      <small><font color="grey">Threshold: </font></small>
                      <small id="throttle-input"><font color="grey">e.g. 0.01 </font></small>
                  </div>         
                  <br/>	
                  <table id="mra-barcode-grid" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                          <th width="450" class=clickable-popover data-content="Correlation of regulatory gene with its targets (red +, blue-) ordered by ranked differential expression, lowest at left.">Barcode</th>
                          <th width="50" class=clickable-popover data-content="Differential Activity(e.g. NES from GSEA)">DA</th>
                          <th width="50" class=clickable-popover data-content="Differential Expression">DE</th>
                          <th class=clickable-popover data-content="Rank of regulatory gene in overall DE results.">DE Rank</th>
                          <th class=clickable-popover data-content="E.g.  TF or signalling molecule whose regulon was tested for enrichment in differentially expressed genes.">Regulator</th>
                       </tr>
                     </thead>
                     <tbody>
                       <!-- here will go the rows -->
                     </tbody>
                  </table>

        </div>
    </script>

    <script type="text/template" id="mra-view-row-tmpl">
        <tr>
            <td><input type="checkbox" id="checkbox_{{entrezId}}" value="{{entrezId}}"></td>
            <td>{{geneSymbol}}</td>
            <td>{{score}}</td>
            <td>{{dataRowCount}}</td>
        </tr>
    </script>

    <script type="text/template" id="mra-barcode-view-row-tmpl">
        <tr>             
			<td><canvas id="draw-{{entrezId}}" width="450" height="36"></canvas></td>				  
            <td class="da-color-{{entrezId}}"></td>
            <td class="de-color-{{entrezId}}"></td>
            <td>{{deRank}}</td>
            <td>{{geneSymbol}}</td>
        </tr>
    </script>

    <script type="text/template" id="mra-cytoscape-tmpl">
        <div class="cytoscape-container">
        <div id="mra_progress">
            <img id="mra_progress_indicator" class="centeredImage" src="img/progress_indicator.gif" width="30" height="30" alt="Please wait ......">
        </div>
        <div id="mra-cytoscape">
        </div>
        <div class="well cytoscape-legend">
            <svg width="350" height="30"xmlns="http://www.w3.org/2000/svg">
            <circle cx="20" cy="15" r="10" fill="white" stroke="grey" stroke-width="2"/>
            <text x="40" y="20" fill="grey">TF</text>
            <rect x="100" y="5" width="18" height="18" fill="white" stroke="grey" stroke-width="2"/>
            <text x="130" y="20" fill="grey">K</text>
           <polygon  points="185,5,180,16,185,27,196,27,202,16, 196, 5" fill="white" stroke="grey" stroke-width="2"/>
            <text x="212" y="20" fill="grey">P</text>
            <polygon  points="270,7 260,25,280,25" fill="white" stroke="grey" stroke-width="2"/>
            <text x="290" y="20" fill="grey">none</text>
            </svg>
            <br/>
            {{description}}
        </div>
        </div>
    </script>

    <script type="text/template" id="more-observations-tmpl">
        <div class="alert alert-warning">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p>
                Only {{numOfObservations}} of {{numOfAllObservations}} observations are listed in the table below.
                {{ numOfAllObservations<=1000 ? 'To load all observations please <a href="#" class="load-more-observations">click here</a> (<i>this might take a while</i>).' : 'There are too many observations to display in the browser; <a href="<%=dataURL%>submissions/' + submissionDisplayName + '.zip">download</a> the complete set of observations as a file.' }}
            </p>
        </div>
    </script>


    <script type="text/template" id="more-projects-tmpl">
        <div class="alert alert-warning">
            <p><a href="#" class="close" data-dismiss="alert">&times;</a></p>
            <p>
                The table below lists all submissions that belong to the project "<b>{{filterProject}}</b>".
                To view all submissions from this center, please click <a href="#{{centerStableURL}}">here</a>.
            </p>
        </div>
    </script>

    <script type="text/template" id="ncithesaurus-tmpl">
        <li>
            NCI Thesaurus: <a href="http://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code={{nciId}}" target="_blank">
                {{nciId}}
            <i class='icon-share'></i></a>
        </li>
    </script>

    <script type="text/template" id="annotation-tmpl">
        <li><small>{{displayName}} ({{source}})</small></li>
    </script>

    <script type="text/template" id="maxNumberOfEntites">
        <%=maxNumOfObservations%>
    </script>

    <script type="text/template" id="genelist-view-tmpl">
        <div class="container common-container" id="genelist-container" > 
             <div class=row>
                <div class="col-1">
                </div>
                <div class="col-10" align="center">
                    <div class="alert alert-warning">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                           <p>The Gene Cart allows users to build a list of genes and query them agains a number of gene and protein interaction databases 
                             [<a href="#gene-cart-help">More Details</a>].
                           </p>
                    </div>
                    <h4>Gene List</h4>

                    <select id="geneNames" class="geneSelectList" size="6" 
                                multiple></select>
                    <br/><br/>
                    <a href="#" id="addGene">Add Gene</a>
                    <a href="#" id="deleteGene">Delete Gene</a>
                    <br/><br/>
                    <a href="#" id="clearList">Clear List</a>
                    <a href="#" id="loadGenes">Load Genes from File</a>
                    <br/><input id="geneFileInput" type="file" style="visibility:hidden" />
                    <br/><br/>
                    <h5>Select Action</h5>
                    <select multiple class="form-control" id="gene-cart-action-list" name="sellist2" size=6>
                        <option>CNKB - Find Gene Interactions in Networks</option>
                        <option>Enrichr - Gene Set Enrichment Analysis</option>
                        <option>STRING - Protein-Protein Interaction Networks + Functional Enrichment Analysis</option>
                        <option>Pathway Commons - Data integrated from public pathway and interactions databases</option>
                        <option>- Pathway search: Find pathways</option>
                        <option>- Interactions search: Display network of gene interactions</option>
                    </select>
                    <div align="left" style="padding-top:5px">
                        <small id=gene-cart-action-detail></small>
                    </div>
                    <br/>
                    <button type="submit" class="btn btn-success" id=gene-cart-action>Submit</button>
                 </div>

                 <div class="col-1">
                 </div>
             </div>
        </div>

        <div class="modal hide fade" id="addgene-modal">
        <div class="modal-dialog" role="document">
        <div class="modal-content">
                    
                <div class="modal-body">
                    <br>
                    Add gene symbols
                    <input id="gene-symbols" placeholder="e.g. CTNNB1" class="input-xlarge">
                    <button id="add-gene-symbols" class="btn">Submit</button><br><br>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" data-dismiss="modal">Close</button>
                </div>
        </div>
        </div>
        </div>
      </script>

    <script type="text/template" id="cnkb-query-tmpl">
        <div class="container common-container" id="cnkbquery-container" > 
            <div class=row>
                <div class="col-1">
                </div>
                <div class="col-10">
                       <h3>Cellular Network Knowledge Base</h3>

                       <medium>Select Interactome:</medium>
                       <small id="queryDescription" class="cnkbDescription"></small> 
                       <br/>
                       <select id="interactomeList" class="cnkbSelectList" size="10"></select>
                       <br/>
                       <medium>Network Description: </medium>
                       <small id="interactomeDescription" class="cnkbDescription">
                        &nbsp;&nbsp;
                       </small>
                     <br/><br/>
                      
                    <medium>Version Documentation: </medium>
                    <small id="versionDescription" class="cnkbDescription">
                        &nbsp;&nbsp;
                    </small>
                     <br/>
                     <br/>
                     <a href="#cnkb-result" id="cnkb-result">Submit</a>
                </div>

                <div class="col-1">
                </div>
            </div>
        </div>
      </script>

    <script type="text/template" id="cnkb-result-tmpl">
         <div class="container common-container" id="cnkbresult-container" > 
               <div class="row">
                  <div class="col-1">
                  </div>
                  <div class="col-10">
                     <h3>Cellular Network Knowledge Base</h2>
                   </div>
                   <div class="col-1">
                   </div>
                </div>
                <br/>

                <div id="cnkb_data_progress" align="center">data is loading ......
                    <img id="cnkb_data_progress_indicator" src="img/progress_indicator.gif" width="20" height="20" alt="Please wait ......"><br>
                    <br/><br/><br/>
                </div>
                <div>
                    <div id="createnw_progress_indicator" align="center" style="display: none;">data is loading ......
                        <img id="cnkb_data_progress_indicator" src="img/progress_indicator.gif" width="20" height="20" alt="Please wait ......"><br>
                    </div>
                    <div style="padding:10px;font-size:larger;text-align:center"><b>Gene cart genes:</b> <span id=genecart-genes></span></div>
                    <div style="padding:10px;font-size:larger;text-align:center"><b>Interactome:</b> <span id=interactome-selected></span></div>
                    <div id=interaction-viewer style="display: flex;">
                        <div class="cytoscape-container" ">
                        <div id="cnkb_cytoscape_progress">
                            <img id="cnkb_cytoscape_progress_indicator" class="centeredImage" src="img/progress_indicator.gif" width="30" height="30" alt="Please wait ......">
                        </div>
                        <div id="cytoscape" style="height:600px">
                        </div>
                        <div id="cnkb-cytoscape-legend" class="well cytoscape-legend">
                            <svg  width="500" height="30" xmlns="http://www.w3.org/2000/svg" id=legend-svg>
                            </svg>
                            <br/>
                            <span id=network-description></span>
                        </div>
                        <div style="text-align: center"><button style="margin:5px;" id=cnkb-export-all>Export all interactions</button>
                            <button style="margin:5px;" id=cnkb-export-displayed>Export currently displayed</button></div>
                        </div>
                        <button class="btn btn-primary" style="position: fixed;top: 50%;left: 50%;transform: translate(-50%, -50%);z-index: 2;display:none" id='export-spinner'>
                            <span class="spinner-border spinner-border-sm"></span>
                            Downloading...
                        </button>
                        <div style="width: 200px">
                            <div id=network-detail-viewer style="margin:10px; padding:5px; border-style: solid; border-width:1px; height: 400px;">
                                <h5>Network details</h5>
                                <div id=initial-text style="padding-top: 10px;">Click on a network node (gene) or edge (interaction) to see related information.</div>
                                <div id=gene-detail>
                                    <div><b>Gene symbol:</b> <div id=gene-symbol style="padding-left: 20px;"></div></div>
                                    <div><b>Gene name:</b> <div id=gene-name style="padding-left: 20px;"></div></div>
                                    <div><b>References:</b> <div id=references></div></div>
                                </div>
                                <div id=interaction-detail>
                                    <div><b>Interaction genes:</b><ul><li id=interaction-source></li> <li id=interaction-target></li></ul></div>
                                    <div><b>Interaction values:</b> <ul id=interaction-values></ul></div>
                                </div>
                            </div>
                            <div id=filtering-interactions style="margin:10px; padding:5px; border-style: solid; border-width:1px">
                                <div style="padding:5px;">Filter by:
                                    <select id=supported-confidence-types>
                                    </select>
                                </div>
                                <div style="padding:5px;">Showing <span id=displayed-interaction-number></span> out of <span id=total-interaction-number></span> total interactions.</div>
                                <div style="padding:5px;">Show fewer/more interactions <input type="range" min="10" max="100" style="width:100%" id=interaction-limit></div>
                                <div style="margin:1px;height:1.5em;"><button style="float:left" id=decre1>-</button><button style="float:right" id=incre1>+</button></div>
                                <div style="margin:1px;height:1.5em;"><button style="float:left" id=decre25>--</button><button style="float:right" id=incre25>++</button></div>
                            </div>
                        </div>
                     </div>
                  </div>
                  <br/>	
             </div>
      </script>

    <script type="text/template" id="cnkb-result-row-tmpl">
        <tr id="tr_{{geneName}}">
            <td><input type="checkbox" id="checkbox_{{geneName}}" value="{{geneName}}" class="cnkb_checkbox"></td> 
            <td>{{geneName}}</td>; 
        </tr>
      </script>

    <script type="text/template" id="gene-cart-help-tmpl">
         <div class="container common-container" id="cnkbhelp-container" > 
             <div class=row>
               <div class="col-10">
                    <h3>Gene Cart Help</h3>
                    <p>The Gene Cart allows users to build a list of genes and query the Cellular Networks Knowledge Base (CNKB) for molecular interactions involving these genes.  The CNKB is a repository of molecular interactions networks. It contains computationally-derived networks obtained by applying state of the art Systems and Structure Biology algorithms from the laboratories of Drs. Andrea Califano and Barry Honig at Columbia University. A detailed <a target="_blank" href="http://wiki.c2b2.columbia.edu/workbench/index.php/Cellular_Networks_KnowledgeBase">description of the CNKB</a> is available which also describes how the CNKB can be accessed from within the software platform <a target="_blank" href="http://www.geworkbench.org">geWorkbench</a>.</p>
                    <p>In the Observations for a particular Dashboard submission, those entries that are genes will have a green "+" sign to right of the gene symbol. Clicking this "+" sign will add the gene to the Gene Cart. The Gene Cart is limited to 25 genes.</p>
                    <p>In the Gene Cart, clicking on "Find Interactions in Networks (CNKB)" will bring the user to the Cellular Networks Knowledge Base page where a particular interactome and version can be chosen. Descriptive text for each is available by selecting any particular interactome or version. Clicking "Submit" will initiate a query of the CNKB using the genes in the cart.  "Version" may represent different types of interactions inferred on a given dataset.  See the description of each version for details.</p>
                    <p>The query result is displayed in a table showing the number and type of interactions found for each query gene. A check box to the left of each gene allows individual results to be selected. The interactions for selected genes can then be downloaded in the form of a Cytoscape "SIF"-format file, or displayed directly in Cytoscape.js in the browser. The number of interactions to display is controlled using the "Interactions Limit" pulldown. Interactions to display are then chosen based on a ranking by a measure of likelihood, e.g. the top 100 interactions.</p>
                    <p>Several layout options are available for Cytoscape and can be selected using the "Layout" pulldown.</p>
                    <p>In Cytoscape.js, several common interaction types have been assigned specific colors used for the lines representing them, and these will be shown on the legend of the graph.  The genes used in the CNKB query (hub genes) will be highlighted in yellow.</p>
                </div>
                <div class="col-1">
                   <a href="javascript:history.back()">Back</a>
                </div>
            </div>
         </div>
     </script>

    <script type="text/template" id="gene-cart-option-tmpl">
        <option value="{{displayItem}}">{{displayItem}}</option>
     </script>

    <script type="text/template" id="summary-item-tmpl">
        <tr id=summary-item><td>{{label}}</td><td>{{submissions}}</td><td>{{tier1}}</td><td>{{tier2}}</td><td>{{tier3}}</td><td>{{tier1+tier2+tier3}}</td></tr>
    </script>

    <script type="text/template" id="video-popup-tmpl">
        <div id="ytplayer-{{videoid}}" class="flex-video widescreen" data-video-id="{{videoid}}" style="padding: 70px 20px;">
            <noscript><p><a href="https://www.youtube.com/watch?v={{videoid}}" target="_blank" >
                View this video on YouTube.</a></p>
            </noscript>
            <iframe width="640" height="360" src="//www.youtube.com/embed/{{videoid}}?feature=oembed" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
            <div style='width:640px;'>
                <span style='color: rgb(0, 0, 0);font: bold 10px Verdana,Helvetica,sans-serif;line-height: 1.4em;'>
                    {{description}}
                </span>
            </div>
        </div>
    </script>

    <script type="text/template" id="how-to-cite-tmpl">
        <div class="container common-container">
            <p>&nbsp;</p>
            <h2>How to Cite</h2>
            <p><b>Users of data from the CTD<sup>2</sup> Dashboard must acknowledge and cite the manuscript:</b></p>
            <p>Aksoy BA, Dančík V, Smith K, Mazerik JN, Ji Z, Gross B, Nikolova O, Jaber N, Califano A, Schreiber SL, Gerhard DS, Hermida LC, Jagu S, Sander C, Floratos A, Clemons PA. CTD2 Dashboard: a searchable web interface to connect validated results from the Cancer Target Discovery and Development Network. Database (Oxford). Volume <b>2017</b>:1-10, 1 January 2017. doi: 10.1093/database/bax054.</p>
            <p>PubMed PMID: <a target=_blank href="https://www.ncbi.nlm.nih.gov/pubmed/29220450">29220450</a>; PubMed Central PMCID: <a target=_blank href="https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5569694/">PMC5569694</a>.</p>
            <p><b>Please also provide the following CTD<sup>2</sup> Dashboard URL:</b></p>
            <a target=_blank href="https://ctd2-dashboard.nci.nih.gov/dashboard">https://ctd2-dashboard.nci.nih.gov/dashboard</a>
        </div>
    </script>

    <script type="text/template" id="api-documentation-tmpl">
        <div class="container common-container">
            {{api_document}}
        </div>
    </script>

    <script type="text/template" id="applications-tmpl">
        <div class="container common-container">
            {{api_apps}}
        </div>
    </script>

    <script type="text/template" id="master-export-tmpl">
        <div class="container common-container">
            <p>The full set of observations stored in the Dashboard can be exported in a tabular form, to support downstream analyses and machine learning applications. Each observation is represented with multiple rows, one row per observation subject. Observation- and submission-level data is replicated in each subject row.</p>
            <a target=_blank href="<%=dataURL%>submissions/master_export/master-export.txt.zip">Download file</a>
        </div>
    </script>

    <script type="text/template" id="help-navigate-tmpl">
        <div class="help-navigate-text-container">
            <h3>Navigating and Understanding Dashboard Content</h3>
            <p>
                The CTD<sup>2</sup> Network aims to increase understanding of the underlying molecular causes of distinct cancer types and accelerate development of clinically useful biomarkers and targeted therapies for precision medicine.
                The Dashboard is one tool that provides access to Network findings.
                Results are available as data-related figures, or polished stories, and are formatted to enable navigation and comprehension by most researchers, from computational experts to those with little bioinformatics dexterity.
                Through the Dashboard, the <b>CTD<sup>2</sup> Network</b> gives the research community a method to interrogate experimental observations across the Centers.
                The terms used in the Dashboard and how the Dashboard content is organized are explained below.
            </p>

            <ul>
                <li><i>Center</i>: One of the academic research teams that make up the CTD<sup>2</sup> Network. To learn more about the current Centers, visit <a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/centers">https://ocg.cancer.gov/programs/ctd2/centers</a>.</li>

                <li><i>Submission</i>: A Dashboard entry that represents a dataset associated with positive experimental results, a set of data-related figures, or a polished story.</li>

                <li><i>Subject</i>: The focus of an experiment or result in a Dashboard <b>submission</b> (<i>e.g.</i>, genes, proteins, small molecules, cell lines, animal models).</li>
                <ul>
                    <li>Class</i>: A set of objects representing the same molecular or biological category (DNA, RNA, protein, small molecule, tissue, animal model) and sharing a set of required and optional attributes.</li>
                    <li><i>Role</i>: The <b>Center</b>-designated function of a gene, protein, or compound based on the interpretation of observations within a particular experimental or computational context. Assigning <b>role</b>s from a restricted list of terms (biomarkers, diseases, master regulators, interactors, oncogenes, perturbagens, candidate drugs, or targets) helps organize subjects in Dashboard for browsing and searching.
                </ul>

                <li><i>Evidence</i>: Selected positive or validated results from a scientific experiment (<i>e.g.</i>, numeric values, text labels, data figures).</li>
                <ul>
                    <li><i>Type</i>:  Category of evidence provided in support of the results. Examples include the following: literature, measured, link, reference, background, observed, computed, written, resources, species.
                </ul>

                <li><i>Observation</i>: A <b>Center</b>-determined conclusion that is submitted as a connection between <b>subjects</b> and <b>evidence</b>; the "fundamental unit" of the Dashboard.</li>

                <li><i><a href="http://www.ncbi.nlm.nih.gov/pubmed/27401613" target="_blank">Tier</a></i>: A CTD<sup>2</sup> Network-defined ranking system for <b>evidence</b> that is based on the extent of characterization associated with a particular study.
                    <ul>
                        <li><i>Tier 1</i>: Preliminary results of screening campaigns.</li>
                        <li><i>Tier 2</i>: Confirmation of primary results <i>in vitro</i>.</li>
                        <li><i>Tier 3</i>: Validation of results in a cancer relevant <i>in vivo</i> model.</li>
                    </ul>
                </li>
            </ul>

            <h3>Dashboard Organization</h3>
            <p>
                The <b>subjects</b> from CTD<sup>2</sup> Network studies are ordered by relevance based on the number of different <b>Centers</b> providing <b>observations</b> about that particular <b>subject</b>, the <b>Tiers</b> of these <b>observations</b>, and, in the cases of a tie, the number of <b>observations</b>.  The actual relevance score for a <b>subject</b> is the sum of the highest <b>Tier</b> number from each Center for which there is an <b>observation</b> on that <b>subject</b>.
            </p>

            <br>
            <hr>
            <br>
        </div>
    </script>

    <script id="tbl-project-title-tmpl" type="text/template">
        <tr class="group"><td colspan="5"><a href="#{{centerStableURL}}/{{project_url}}">Project: {{project}}</a></td></tr>
    </script>

    <!-- end of templates -->

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.2.1/dist/js/bootstrap.bundle.min.js" integrity="sha256-MSYVjWgrr6UL/9eQfQvOyt6/gsxb6dpwI1zqM5DbLCs=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/js/bootstrap-select.min.js"></script>
    <script type="text/javascript" src="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.11.3/b-2.0.1/b-html5-2.0.1/datatables.min.js"></script>
    <script src="js/paging.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/underscore@1.13.1/underscore-umd-min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/backbone.js/1.4.0/backbone-min.js" integrity="sha512-9EgQDzuYx8wJBppM4hcxK8iXc5a1rFLp/Chug4kIcSWRDEgjMiClF8Y3Ja9/0t8RDDg19IfY5rs6zaPS9eaEBw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="js/jquery.fancybox.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/jquery-expander@2.0.1/jquery.expander.min.js" integrity="sha256-4Q2/KlcZXZR4sVgZhDhkN8grxZppvC0RrPJaF2JU4fQ=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/cytoscape@3.19.1/dist/cytoscape.min.js" integrity="sha256-wltbgrEBSKmvqq5hNFh2x9WNiP+fORRpHWqFoZcB3o8=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/webcola@3.4.0/WebCola/cola.min.js" integrity="sha256-tF66P7AtItnqmivK0H3b7IKW+AfwXpb4ydSrvEl/Zcc=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/cytoscape-cola@2.5.0/cytoscape-cola.min.js"></script>
    <script src="js/encoder.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-contextmenu/2.9.2/jquery.contextMenu.min.js" integrity="sha512-kvg/Lknti7OoAw0GqMBP8B+7cGHvp4M9O9V6nAYG91FZVDMW3Xkkq5qrdMhrXiawahqU7IZ5CNsY/wWy1PpGTQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/7.6.1/d3.min.js" integrity="sha512-MefNfAGJ/pEy89xLOFs3V6pYPs6AmUhXJrRlydI/9wZuGrqxmrdQ80zKHUcyadAcpH67teDZcBeS6oMJLPtTqw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3-cloud/1.2.5/d3.layout.cloud.min.js" integrity="sha512-HjKxWye8lJGPu5q1u/ZYkHlJrJdm6KGr89E6tOrXeKm1mItb1xusPU8QPcKVhP8F9LjpZT7vsu1Fa+dQywP4eg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script type='module' src="js/ctd2.js?ts=202207"></script>

<script type="text/javascript">_satellite.pageBottom();</script>
</body>

</html>
