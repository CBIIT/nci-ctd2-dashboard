<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
    WebApplicationContext context = WebApplicationContextUtils
            .getWebApplicationContext(application);
    String dataURL = (String) context.getBean("dataURL");
    Integer maxNumOfObservations = (Integer) context.getBean("maxNumberOfEntities");
    String dashboardReleaseVersion = (String) context.getBean("dashboardReleaseVersion");
%><!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
  <head>
    <!-- X-UA-Compatible meta tag to disable IE compatibility view must always be first -->
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>CTD² Dashboard</title>
    <meta name="description" content="" />
    <meta name="author" content="" />

    <link rel="shortcut icon" href="img/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" />
    <link rel="stylesheet" href="css/jquery.dataTables.css" type="text/css" />
    <link rel="stylesheet" href="css/buttons.dataTables.min.css" type="text/css" />
    <link rel="stylesheet" href="css/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="css/jquery.contextMenu.css" type="text/css" />
    <link rel="stylesheet" href="css/ctd2.css" type="text/css" />

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="shortcut icon" href="img/favicon.png" />
  </head>

  <body>
    <!-- NAVBAR
    ================================================== -->
    <script src="js/jquery.min.js"></script>
    <script src="js/jquery.ba-hashchange.min.js"></script>
    <script>
    $(function() {
        // Bind an event to window.onhashchange that, when the hash changes, 
        // gets the hash and alters class of desired navlinks
        $(window).hashchange(function() {
            var hash = location.hash || '#';
            $('[id^="navlink-"]').each(function() {
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
                    $('#' + id + ' li a').each(function() {
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
        });
        // Since the event is only triggered when the hash changes, we need to trigger
        // the event now, to handle the hash the page may have been loaded with.
        $(window).hashchange();
    });
    </script>
    <div class="navbar-wrapper">
      <!-- Wrap the .navbar in .container to center it within the absolutely positioned parent. -->
      <div class="container">

        <div class="navbar navbar-inverse">
          <div class="navbar-inner">
            <!-- Responsive Navbar Part 1: Button for triggering responsive navbar (not covered in tutorial). Include responsive CSS to utilize. -->
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </a>
            <div class="nav-collapse collapse">
              <ul id="nav" class="nav">
                <li><a id="navlink-dashboard" class="navlink" href="#">CTD<sup>2</sup> Dashboard</a></li>
                <li><a id="navlink-centers" class="navlink" href="#centers">Centers</a></li>
                <li class="dropdown">
                      <a class="dropdown-toggle navlink" href="#" data-toggle="dropdown">Resources <b class="caret"></b></a>
                      <ul class="dropdown-menu">
                          <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2">CTD<sup>2</sup> Home Page</a></li>
                          <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/publications">Publications</a></li>
                          <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/data-portal">Data Portal - Downloads</a></li>
                          <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/analytical-tools">Analytical Tools</a></li>
                          <li><a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/supported-reagents">Supported Reagents</a></li>
                          <li class="dropdown-submenu"><a tabindex="-1" href="#">Outside Resources</a>
                                <ul class="dropdown-menu">
                                    <li><a target="_blank" href="http://www.lincsproject.org/">LINCS</a></li>
                                </ul>
                           </li>
                      </ul>
                  </li>
                  <li class="dropdown">
                      <a id="navlink-browse" class="dropdown-toggle navlink" href="#" data-toggle="dropdown">Browse <b class="caret"></b></a>
                      <ul id="dropdown-menu-browse" class="dropdown-menu">
                          <li><a href="#stories">Stories</a></li>
                          <li><a href="#explore/target/Biomarker,Target">Genes (Biomarkers, Targets, etc.)</a></li>
                          <li><a href="#explore/compound/Perturbagen,Candidate Drug">Compounds and Perturbagens</a></li>
                          <li><a href="#explore/context/Disease">Disease Contexts</a></li>
                      </ul>
                  </li>
                  <li class="dropdown">
                      <a id="navlink-genecart" class="dropdown-toggle navlink" href="#" data-toggle="dropdown">Gene Cart <b class="caret"></b></a>
                      <ul id="dropdown-menu-genecart" class="dropdown-menu">
                          <li><a href="#genes">Go To Cart</a></li> 
                          <li><a href="#gene-cart-help">Help</a></li>
                      </ul>
                  </li>
              </ul>
              <ul class="nav pull-right">
                  <form class="form-search" id="omnisearch">
                      <div class="input-append">
                          <input type="text" id="omni-input" class="span3 search-query" title="Search" placeholder="e.g. CTNNB1 or ABT-737">
                          <button type="submit" class="btn search-button">Search</button>
                          <span class="hide" id="search-help-content">
                              <p>Please enter the keyword you would like to search on the website.</p>

                              <strong>Examples:</strong>
                              <ul>
                                <li><em>Gene: </em> <a href="#search/CTNNB1">CTNNB1</a> or <a href="#search/YAP*">YAP*</a></li>
                                <li><em>Compound: </em> <a href="#search/ABT-737">ABT-737</a></li>
                                <li><em>Cell Sample: </em> <a href="#search/HPBALL">HPBALL</a></li>
                              </ul>
                              <br>
                          </span>
                      </div>
                  </form>
              </ul>
            </div><!--/.nav-collapse -->
          </div><!-- /.navbar-inner -->
        </div><!-- /.navbar -->

      </div> <!-- /.container -->
    </div><!-- /.navbar-wrapper -->


    <!-- all the backbone magic will happen here, right in this div -->
    <div id="main-container"></div>

    <div class="container footer-container">
        <!-- FOOTER -->
       <footer>
        <p><small>Dashboard Release <%=dashboardReleaseVersion%></small></p>
     	<p>
            <small>
                <p>
                    As the CTD<sup>2</sup> Network continues to refine the Dashboard, input from the research community is highly valued to help improve usability.<br>
                    <b>Note</b>: Data users must acknowledge CTD<sup>2</sup> Network and visit <a href="http://ocg.cancer.gov/programs/ctd%C2%B2/ctd%C2%B2-publication-guidelines">CTD<sup>2</sup> publication guidelines</a> for more information.<br>
                    To provide feedback, please send comments to <a href="mailto:ocg@mail.nih.gov?subject=CTD2 Dashboard Feedback">ocg@mail.nih.gov</a>.
                </p>
           </small>
        </p>
         <p>
             <a href="http://cancer.gov"><img src="img/logos/footer_logo_nci.jpg" alt="NCI logo" title="NCI logo"></a><a href="http://www.dhhs.gov/"><img src="img/logos/footer_logo_hhs.jpg" title="HHS logo" alt="HHS logo"></a><a href="http://www.nih.gov/"><img src="img/logos/footer_logo_nih.jpg" title="NIH logo" alt="NIH logo"></a><a href="http://www.firstgov.gov/"><img src="img/logos/footer_logo_firstgov.jpg" title="First Gov logo" alt="First Gov logo"></a>
         </p>
           <p>
               <a class="help-navigate" href="#help-navigate">Glossary</a> &middot;
               <a href="http://www.cancer.gov/global/web/policies" target="_blank">Policies</a> &middot;
               <a href="http://www.cancer.gov/global/web/policies/accessibility" target="_blank">Accessibility</a> &middot;
               <a href="http://www.cancer.gov/global/web/policies/foia" target="_blank">FOIA</a>
           </p>

       </footer>
    </div>
     
    <div class="modal hide fade" id="alert-message-modal">  <!-- a hidden div for showing alert message -->          
        <div class="modal-body" >
            <br><medium id="alertMessage"></medium>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" data-dismiss="modal">Close</button>
        </div>
    </div>

    <div class="modal hide fade" id="popup-textarea-modal">
        <div class="modal-body" >
            <textarea id="temporary-text" style='width:95%' rows='10' cols='100'></textarea>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" data-dismiss="modal" id="close-tempoary-text">Close</button>
        </div>
        <span id='invoker-id' style='display:none'></span>
    </div>

    <div class="modal fade" tabindex="-1" role="dialog" id="confirmation-modal">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">Delete Confirmation</h4>
                </div>
                <div class="modal-body">
                    <p id="confirmation-message">Are you sure you want to delete this?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="confirmed-delete">Yes, delete it.</button>
                </div>
            </div>
        </div>
    </div>

    <!-- these are the templates -->
    <script type="text/template" id="home-tmpl">
        <div class="overview-container">
            <div class="container overview-box">
                <div class="row">
                    <div class="span9">
                        <div class="featurette" id="overview-text">
                            <a href="https://ocg.cancer.gov/programs/ctd2">
                            <img class="img-polaroid pull-left" src="img/logos/ctd2_overall.png" alt="CTD2 general image" title="CTD2 general image">
                            </a>
                            <p class="lead firstlead">
                                <a href="https://ocg.cancer.gov/programs/ctd2"><b>The Cancer Target Discovery and Development (CTD<sup>2</sup>) Network</b></a> mines and functionally validates data from large-scale adult and pediatric cancer genome characterization initiatives and advances them toward use in precision oncology.
                                The Network developed the open-access “CTD<sup>2</sup> Dashboard,” a web interface that compiles Network-generated summaries about key cancer biology findings and cancer-relevant results related to genes, proteins, and compounds.
                                Submissions are “ranked” with the validation “<a href="http://www.ncbi.nlm.nih.gov/pubmed/27401613" target="_blank">Tier</a>” system to indicate strength of evidence.
                                To learn how the Dashboard is organized, visit <a id="homepage-help-navigate" href="#help-navigate">Navigating and Understanding Dashboard Content</a>.
                                To understand more about the Dashboard functions, please read <a href="https://ocg.cancer.gov/news-publications/e-newsletter-issue/issue-14#1721">CTD<sup>2</sup> Dashboard: A Platform to Explore Evidence-based Observations</a>.
                            </p>
                        </div>
                    </div>
                    <div class="span3">
                        <br><br>
                        <a href="https://ocg.cancer.gov/programs/ctd2/data-portal" style="vertical-align: middle">
                          <img src="img/logos/image001.png" onmouseover="this.src='img/logos/image002.png'" onmouseout="this.src='img/logos/image001.png'" alt="CTD2 general image" title="CTD2 Data Portal image" style="vertical-align: middle">
                        </a>
                        <div>
                </div>
            </div>
        </div>

        <div class="dark-separator"></div>

        <div class="container marketing ctd2-boxes">
          <div style="display:table">
              <!--
            <div class="span3 stories" data-order="0">
              <h4>Stories</h4>
              <p>
                  In each <i>story</i>, Dashboard users can find research highlights from CTD<sup>2</sup> Network projects, a link to the list of observations related to the results, and other resources relevant to the data.
              </p>
              <a class="btn btn-danger btn-block" href="#stories">Browse &raquo;</a>
            </div><!-- /.span3 -->
            <div class="span4 target" data-order="1" style="display:table-cell;float:none;position:relative;padding-bottom:25px">
              <h4>Biomarkers, Targets, <br>Genes &amp; Proteins</h4>
                <p>
                    Users can browse a list of genes and proteins that Centers have identified using analyses that generate results with low frequencies of false positives. In some cases, genes and proteins have been assigned roles as biomarkers or targets based on observations.
                </p>
                <a class="btn btn-success btn-block" href="#explore/target/Biomarker,Target" style="position:absolute;bottom:0;">Browse &raquo;</a>
            </div><!-- /.span3 -->
            <div class="span4 drug" data-order="2" style="display:table-cell;float:none;position:relative;padding-bottom:25px">
              <h4>Compounds &amp; Perturbagens</h4>
                <p>
                    Users can browse compounds and perturbagens, which are modulators of cellular phenotype, genes, or proteins in cancer cell lines or tumor model systems. Some examples include small molecules, FDA approved drugs, natural products, and small regulatory RNAs.
                </p>
                <a class="btn btn-info btn-block" href="#explore/compound/Perturbagen,Candidate Drug" style="position:absolute;bottom:0;">Browse &raquo;</a>
            </div><!-- /.span3 -->
              <div class="span4 context" data-order="3" style="display:table-cell;float:none;position:relative;padding-bottom:25px">
                  <h4 class="homepage-context">Disease <br>Context</h4>
                  <p>
                      Users can browse disease context, which groups subjects by observations pertinent to a particular disease or tumor type.
                  </p>
                  <a class="btn btn-warning btn-block" href="#explore/context/Disease" style="position:absolute;bottom:0;">Browse &raquo;</a>
              </div><!-- /.span3 -->

          </div><!-- /.row -->
        </div><!-- /.container -->

        <!-- Carousel
        ================================================== -->
        <div class="carousel slide">
          <div class="carousel-inner">
            <div class="item active">
              <img src="img/bg-red.png" alt="Red background image" title="red background image" class="cimg">
              <div class="container">
                  <div class="carousel-caption">
                        <h3 class="homepage-stories-title">Recent Stories</h3>
                        <div class="well carousel-well">
                            <div class="tab-content stories-tabs">
                                <div class="container tab-pane active fade in" id="story-1"></div>
                                <div class="container tab-pane fade" id="story-2"></div>
                                <div class="container tab-pane fade" id="story-3"></div>
                                <div class="container tab-pane fade" id="story-4"></div>
                            </div>
                            <div class="pagination pagination-centered stories-pagination">
                                <ul class="nav">
                                    <li class="active"><a href="#story-1" class="story-link">&bull;</a></li>
                                    <li><a href="#story-2" class="story-link">&bull;</a></li>
                                    <li><a href="#story-3" class="story-link">&bull;</a></li>
                                    <li><a href="#story-4" class="story-link">&bull;</a></li>
                                    <li><a href="#stories">More stories &raquo;</a></li>
                                </ul>
                            </div>
                        </div>
                  </div>
              </div>
            </div>
          </div>

        </div><!-- /.carousel -->

    </script>

    <script type="text/template" id="centers-tmpl">
        <div class="container common-container" id="centers-container">
            <h2>Centers</h2>
            <table class="table table-bordered table-striped table-compact" id="centers-list-table">
                <thead>
                    <tr>
                        <th class="center-image-column"></th>
                        <th>Center name</th>
                        <th>Principal Investigator</th>
                        <th class="submission-count">Submissions</th>
                    </tr>
                </thead>
                <tbody id="centers-tbody">
                <!-- here will come the centers... -->
                </tbody>
            </table>
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
        </div>
    </script>


    <script type="text/template" id="stories-tbl-row-tmpl">
        <tr>
            <td class="center-image-column">
                <a href="#center/{{submission.observationTemplate.submissionCenter.id}}">
                    <img src="img/slogos/{{submission.observationTemplate.submissionCenter.displayName}}.png" alt="{{submission.observationTemplate.submissionCenter.displayName}}" title="{{submission.observationTemplate.submissionCenter.displayName}}" class="img-circle">
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
                <a target="_blank" href="<%=dataURL%>" id="file-link2-{{id}}">view full story</a>
                <br>or<br>
                <a href="#observation/{{id}}">see observation</a>
            </td>
        </tr>
    </script>

    <script type="text/template" id="centers-tbl-row-tmpl">
        <tr>
            <td class="center-image-column">
                <a href="#center/{{id}}">
                    <img src="img/{{displayName}}.png" alt="{{displayName}}" title="{{displayName}}" class="img-polaroid">
                </a>
            </td>
            <td class="center-name">
                <a href="#center/{{id}}">
                    {{displayName}}
                </a>
            </td>
            <td class="center-pi">
                <span id="center-pi-{{id}}">loading...</span>
            </td>
            <td>
                <a href="#center/{{id}}" id="submission-count-{{id}}">
                    loading...
                </a>
            </td>
        </tr>
    </script>

    <script type="text/template" id="center-tmpl">
        <div class="container common-container" id="center-submission-container">
            <div class="row">
                <div class="span9">
                    <h2 class="center-title">{{displayName}} <small>submissions</small></h2>
                    <div class="center-link-container">(<span class="center-link"></span>)</div>
                </div>
                <div class="span3">
                    <img src="img/{{displayName}}.png" title="{{displayName}}" alt="{{displayName}}" class="img-polaroid" width="200">
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
                <a href="#submission/{{id}}" class="obs-count" id="observation-count-{{id}}">{{details}}</a>
                <div style="font-size:70%">[<a href="<%=dataURL%>submissions/{{displayName}}.zip">Download</a>]</div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="submission-tmpl">
        <div class="container common-container" id="submission-container">
            <div class="alert alert-block hide" id="redirect-message">

                <p>There is only a single observation in this submission.
                    Redirecting to the observation page in <b id="seconds-left">10</b> seconds.
                    <a href="#" id="cancel-redirect">(cancel)</a>
                </p>
            </div>
            <div class="row">
                <div class="span10">
                    <h2>
                        Submission
                        <span class="badge-tier-container">
                            <span class="badge badge-tier">Tier {{observationTemplate.tier}}</span>
                        </span>
                    </h2>


                    <table id="submission-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Project</th>
                            <td>{{observationTemplate.project}}</td>
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
                <div class="span2">
                    <a href="#/center/{{observationTemplate.submissionCenter.id}}">
                        <img src="img/{{observationTemplate.submissionCenter.displayName}}.png" class="img-polaroid" height=30 alt="{{observationTemplate.submissionCenter.displayName}}" title="{{observationTemplate.submissionCenter.displayName}}">
                    </a>

                    <br>
                    <br>

                    <img src="img/submission.png" class="img-polaroid" width=150 height=150 alt="Submission" title="Submission"><br>
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
        </div>
    </script>

    <script type="text/template" id="submission-obs-tbl-row-tmpl">
        (<a class="button-link" href="#/observation/{{id}}">details &raquo;</a>)
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
                <div class="span10">
                    <h2>Observation <small>(Tier {{submission.observationTemplate.tier}})</small></h2>
                    <blockquote>
                        <p id="observation-summary"></p>
                    </blockquote>

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
                <div class="span2">
                    <a href="#/center/{{submission.observationTemplate.submissionCenter.id}}"><img src="img/{{submission.observationTemplate.submissionCenter.displayName}}.png" class="img-polaroid" width="120" alt="{{submission.observationTemplate.submissionCenter.displayName}}"></a>
                    <br><br>
                    <img src="img/observation.png" alt="Observation" class="img-polaroid" width=120 height=120><br>
                </div>
            </div>


            <h3>Submission <small>(<a href="#" id="small-show-sub-details">show details</a><a href="#" id="small-hide-sub-details" class="hide">hide details</a>)</small></h3>
            <div id="obs-submission-details" class="hide">
                <table id="obs-submission-details-grid" class="table table-bordered table-striped">
                    <tr>
                        <th>Project</th>
                        <td>{{submission.observationTemplate.project}}</td>
                    </tr>
                    <tr>
                        <th>Description</th>
                        <td>
                            {{submission.observationTemplate.description}}
                            <small>(<a href="#submission/{{submission.id}}">details &raquo;</a>)</small>
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
                        <td><a href="<%=dataURL%>submissions/{{submission.displayName}}.zip" class=no-preview>download</a></td>
                    </tr>


                </table>
            </div>


            <h3>Evidence</h3>
            <table id="observed-evidences-grid" class="table table-bordered table-striped evidences">
                <thead>
                <tr>
                    <th>&nbsp;&nbsp;</th>
                    <th>Type</th>
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
            <small><a href="#submission/{{id}}">{{observationTemplate.description}}</a></small>
        </li>
    </script>

    <script type="text/template" id="submission-description-tmpl">
        <h3>Submission summary</h3>
        <blockquote>
            <p>{{observationTemplate.submissionDescription}}</p>
        </blockquote>
    </script>

    <script type="text/template" id="summary-subject-replacement-tmpl">
        <a class="summary-replacement" href="#/subject/{{id}}">{{displayName}}</a>
    </script>

    <script type="text/template" id="summary-evidence-replacement-tmpl">
        <strong class="summary-replacement">{{displayName}}</strong>
    </script>

    <script type="text/template" id="observedevidence-row-tmpl">
        <tr>
            <td>&nbsp;&nbsp;</td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>{{displayName}}</td>
        </tr>
    </script>

    <script type="text/template" id="observedfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" title="Download file ({{evidence.mimeType}})" class="desc-tooltip  no-preview" title="Download File">
                    download file
                </a>
            )</td>
        </tr>
    </script>

    <script type="text/template" id="observedhtmlfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" title="View file ({{evidence.mimeType}})" class="desc-tooltip html-story-link" title="Download File">
                    view
                </a>
                )</td>
        </tr>
    </script>


    <script type="text/template" id="observedpdffileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" title="{{observedEvidenceRole.displayText}}" class="desc-tooltip pdf-file-link">
                    view PDF
                </a>
                )</td>
        </tr>
    </script>

    <script type="text/template" id="observedgctfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view file <b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="http://www.broadinstitute.org/cancer/software/GENE-E/dynamic.php?data=<%=dataURL%>{{evidence.filePath}}" target="_blank" title="open in GENE-E (Java Web-start)" class="desc-tooltip">
                                open with GENE-E
                            </a>
                        </li>
                        <li>
                            <a href="<%=dataURL%>{{evidence.filePath}}" class="desc-tooltip" target="_blank" title="type: ({{evidence.mimeType}})">view in browser</a>
                        </li>

                    </ul>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="observedsiffileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view file <b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="#" data-description="{{observedEvidenceRole.displayText}}" data-sif-url="<%=dataURL%>{{evidence.filePath}}" target="_blank" title="open in Cytoscape.js" class="desc-tooltip cytoscape-view">
                                interactive network view
                            </a>
                        </li>
                        <li>
                            <a href="<%=dataURL%>{{evidence.filePath}}" class="desc-tooltip" target="_blank" title="type: ({{evidence.mimeType}})">view in browser</a>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="observedimageevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="image-evidence-wrapper">
                    <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" title="{{observedEvidenceRole.displayText}}" rel="evidence-images" class="evidence-images">
                        <img src="<%=dataURL%>{{evidence.filePath}}" class="img-polaroid img-evidence" height="140" title="File" alt="File">
                    </a>
                </div>
            </td>
        </tr>
    </script>


    <script type="text/template" id="observedlabelevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><div class="labelevidence expandable">{{displayName}}</div></td>
        </tr>
    </script>

    <script type="text/template" id="observedurlevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                (<a href="{{evidence.url.replace(/^\//, '')}}" target="_blank" class="desc-tooltip no-preview" title="Open link in a new window">
                    open link
                </a>)
            </td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><span class="numeric-value">{{evidence.numericValue}}</span> <em>{{evidence.unit}}</em></td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-val-tmpl">
        {{firstPart}} &times; 10<sup>{{secondPart}}</sup>
    </script>

    <script type="text/template" id="gene-uniprot-tmpl">
        <li id="gene-uniprot-link">
            UniProt: <a href="http://www.uniprot.org/uniprot/{{uniprotId}}" target="_blank">{{uniprotId}} <i class="icon-share"></i></a>
        </li>
    </script>

    <script type="text/template" id="gene-tmpl">
         <div class="container common-container" id="gene-container">
             <h2>{{displayName}}</h2>

             <div class="row">
                 <div class="span9">
                     <table id="gene-details-grid" class="table table-bordered table-striped">
                         <tr>
                             <th>Gene symbol<div style="font-size:10px; font-style:italic">(from HGNC)</div></th>
                             <td>{{displayName}}&nbsp;&nbsp;
                                  <a href="#" class="addGene-{{displayName}} greenColor" title="Add gene to cart">+</a>  
                             </td>
                         </tr>
                         <tr>
                             <th>Synonyms/Related terms<div style="font-size:10px; font-style:italic">(from Entrez)</div></th>
                             <td>
                                 <ul class="synonyms"></ul>
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
                                 </ul>
                             </td>
                         </tr>
                         <tr>
                             <th>Genomic alterations</th>
                             <td>
                                 <a class="btn btn-small" href="http://cbioportal.org/ln?q={{displayName}}" target="blank">view in cBioPortal <i class="icon-share"></i></a>
                             </td>
                         </tr>
                     </table>
                 </div>
                 <div class="span3">
                     <h4>Gene</h4>
                     <img src="img/gene.png" class="img-polaroid" width=175 height=175 alt="Gene">
                 </div>
             </div>

             <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

             <div class="more-observations-message"></div>

             <table id="gene-observation-grid" class="table table-bordered table-striped observations">
                 <thead>
                 <tr>
                     <th>Date</th>
                     <th width=500>Observation Summary</th>
                     <th>Tier</th>
                     <th>Center</th>
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

    <script type="text/template" id="protein-tmpl">
        <div class="container common-container" id="protein-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="span9">
                    <table id="protein-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Gene symbol<div style="font-size:10px; font-style:italic">(from HGNC)</div></th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms/Related terms<div style="font-size:10px; font-style:italic">(from Entrez)</div></th>
                            <td>
                                <ul class="synonyms"></ul>
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
                                Uniprot ID: <a href="http://uniprot.com/{{uniprotId}}" target="_blank">{{uniprotId}} <i class="icon-share"></i></a> <br>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <h4>Protein</h4>
                    <img src="img/protein.png" class="img-polaroid" width=175 height=175 alt="Protein">
                </div>
            </div>

            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="protein-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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

    <script type="text/template" id="shrna-tmpl">
        <div class="container common-container" id="shrna-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="span9">
                    <table id="shrna-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Target Sequence</th>
                            <td>{{targetSequence}}</td>
                        </tr>
                        <tr>
                            <th>Target Transcript</th>
                            <td>
                                <a href="#subject/{{transcript.id}}">{{transcript.refseqId}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Target Gene</th>
                            <td>
                                <a href="#subject/{{transcript.gene.id}}">{{transcript.gene.displayName}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <h4>shRNA</h4>
                    <img src="img/shrna.png" class="img-polaroid" width=175 height=175 alt="shRNA">
                </div>
            </div>

            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="shrna-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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

    <script type="text/template" id="sirna-tmpl">
        <div class="container common-container" id="sirna-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="span9">
                    <table id="shrna-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Target Sequence</th>
                            <td>{{targetSequence}}</td>
                        </tr>
                        <tr>
                            <th>Target Transcript</th>
                            <td>
                                <a href="#subject/{{transcript.id}}">{{transcript.refseqId}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Target Gene</th>
                            <td>
                                <a href="#subject/{{transcript.gene.id}}">{{transcript.gene.displayName}}</a>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <h4>siRNA</h4>
                    <img src="img/sirna.png" class="img-polaroid" width=175 height=175 alt="siRNA">
                </div>
            </div>

            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="sirna-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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


    <script type="text/template" id="transcript-tmpl">
        <div class="container common-container" id="transcript-container">
            <h2>{{refseqId}}</h2>

            <div class="row">
                <div class="span9">
                    <table id="transcript-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Gene</th>
                            <td>
                                <a href="#subject/{{gene.id}}">{{gene.displayName}}</a>
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
                <div class="span3">
                    <h4>Transcript</h4>
                    <img src="img/transcript.png" class="img-polaroid" width=175 height=175 alt="Transcript">
                </div>
            </div>

            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="transcript-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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

    <script type="text/template" id="tissuesample-tmpl">
        <div class="container common-container" id="tissuesample-container">
            <h2>{{displayName}}</h2>

            <div class="row">
                <div class="span9">
                    <table id="tissuesample-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Lineage</th>
                            <td>{{lineage}}</td>
                        </tr>
                        <tr id="tissue-synonyms">
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
                            </td>
                        </tr>
                        <tr id="tissue-refs">
                            <th>References</th>
                            <td>
                                <ul class="xrefs"></ul>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <h4>Tissue Sample</h4>
                    <img src="img/tissuesample.png" class="img-polaroid" width=175 height=175 alt="Tissue sample">
                </div>
            </div>

            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="tissuesample-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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
                <div class="span9">
                    <table id="cellsample-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Name</th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
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
                        <tr id="cbiolink">
                            <th>Genomic alterations</th>
                            <td>
                                <a class="btn btn-small" href="http://www.cbioportal.org/public-portal/case.do?cancer_study_id=cellline_ccle_broad&sample_id={{cbioPortalId}}" target="blank">view in cBioPortal <i class="icon-share"></i></a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                        <h4>Cell Sample</h4>
                        <img src="img/cellsample.png" class="img-polaroid" width=175 height=175 alt="Cell sample">
                </div>
            </div>
            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="cellsample-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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

    <script type="text/template" id="animalmodel-tmpl">
        <div class="container common-container" id="animalmodel-container">
            <h2>{{displayName}}</h2>
            <div class="row">
                <div class="span9">
                    <table id="animalmodel-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Name</th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms</th>
                            <td>
                                <ul class="synonyms"></ul>
                            </td>
                        </tr>
                        <tr>
                            <th>Organism</th>
                            <td>{{organism.displayName}}</td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <h4>Animal Model</h4>
                    <img src="img/animalmodel.png" class="img-polaroid" width=175 height=175 alt="Animal model">
                </div>
            </div>
            <h3>Related observations <small>{{ role?"for the role of "+role:"" }} {{tier?"and tier "+tier:""}}</small></h3>

            <div class="more-observations-message"></div>

            <table id="animalmodel-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Date</th>
                    <th width=500>Observation Summary</th>
                    <th>Tier</th>
                    <th>Center</th>
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

    <script type="text/template" id="compound-tmpl">
          <div class="container common-container" id="compound-container">
              <h2>{{displayName}}</h2>

              <div class="row">
                  <div class="span9">
                      <table id="compund-details-grid" class="table table-bordered table-striped">
                          <tr>
                              <th>Name</th>
                              <td>{{displayName}}</td>
                          </tr>
                          <tr>
                              <th>Synonyms</th>
                              <td>
                                  <ul class="synonyms"></ul>
                              </td>
                          </tr>
                          <tr>
                              <th>SMILES</th>
                              <td><small>{{smilesNotation}}</small></td>
                          </tr>
                          <tr>
                              <th>References</th>
                              <td>
                                  <ul class="compound-xrefs">
                                      {{pubchem ? "<li>PubChem: <a target='_blank' href='https://pubchem.ncbi.nlm.nih.gov/compound/" + pubchem + "'>" + pubchem + "</a> <i class='icon-share'></i></li>" : ""}}
                                      {{cas ? "<li>CAS: " + cas + "</li>" : ""}}

                                  </ul>
                              </td>
                          </tr>
                      </table>
                  </div>
                  <div class="span3">
                      <h4>Compound</h4>
                      <a href="<%=dataURL%>compounds/{{imageFile}}" target="_blank" class="compound-image" title="Compound: {{displayName}}">
                        <img class="img-polaroid" width=200 src="<%=dataURL%>compounds/{{imageFile}}" alt="Compound: {{displayName}}">
                      </a>
                  </div>
              </div>

              <h3>Related observations <small>{{ role?"for the role of "+decodeURI(role):"" }} {{tier?"and tier "+tier:""}}</small></h3>

              <div class="more-observations-message"></div>

              <table id="compound-observation-grid" class="table table-bordered table-striped observations">
                  <thead>
                  <tr>
                      <th>Date</th>
                      <th width=500>Observation Summary</th>
                      <th>Tier</th>
                      <th>Center</th>
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

    <script type="text/template" id="observedsubject-summary-row-tmpl">
        <tr>
            <td id="subject-image-{{id}}"></td>
            <td>
                <a href="#/subject/{{subject.id}}">
                    {{subject.displayName}}
                </a>
            </td>
            <td>{{subject.type}}</td>
            <td>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>{{observedSubjectRole.displayText}}</td>
        </tr>
    </script>
    
    <script type="text/template" id="observedsubject-gene-summary-row-tmpl">
        <tr>
            <td id="subject-image-{{id}}"></td>
            <td id="subject.displayName-{{id}}">
                <a href="#/subject/{{subject.id}}">
                    {{subject.displayName}}
                </a>  &nbsp;
                <a href="#" class="addGene-{{subject.displayName}} greenColor" title="Add gene to cart" >+</a>			  				 
            </td>
            <td>{{subject.type}}</td>
            <td>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>{{observedSubjectRole.displayText}}</td>
        </tr>
    </script>

    <script type="text/template" id="observedsubject-row-tmpl">
        <tr>
            <td>
                <a href="#/observation/{{observation.id}}">
                    {{observation.submission.submissionDate}}
                </a>
            </td>
            <td>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>
                {{observation.submission.observationTemplate.description}}
            </td>
            <td><span class="badge tier-badge">Tier {{observation.submission.observationTemplate.tier}}</span></td>
            <td>
                <a href="#/center/{{observation.submission.observationTemplate.submissionCenter.id}}">
                    <img alt="{{observation.submission.observationTemplate.submissionCenter.displayName}}" title="{{submission.observationTemplate.submissionCenter.displayName}}" width="150" src="img/{{observation.submission.submissionCenter.displayName}}.png">
                </a>
            </td>
        </tr>
    </script>

    <script type="text/template" id="observation-row-tmpl">
        <tr>
            <td>
                <a href="#/observation/{{id}}">
                    {{submission.submissionDate}}
                </a>
            </td>
            <td id="observation-summary-{{id}}">
                Loading...
            </td>
            <td><span class="badge tier-badge">Tier {{submission.observationTemplate.tier}}</span></td>
            <td>
                <a href="#/center/{{submission.observationTemplate.submissionCenter.id}}">
                    <img alt="{{submission.observationTemplate.submissionCenter.displayName}}" title="{{submission.observationTemplate.submissionCenter.displayName}}" width="150" src="img/{{submission.observationTemplate.submissionCenter.displayName}}.png">
                </a>
                <span class="hide-text">{{submission.observationTemplate.submissionCenter.displayName}}</span>

            </td>
        </tr>
    </script>

    <script type="text/template" id="search-empty-tmpl">
        <tr>
            <td colspan="7">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <h3>Sorry, no results found</h3>
                    <p>
                        Would you like to extend your search with a wildcard?
                        (<i>e.g.</i> <a href="#/search/{{encodeURIComponent(term)}}*">{{decodeURIComponent(term)}}*</a>)
                    </p>
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="html-story-container-tmpl">
        <div class="fancy-story-container">
            <img src="img/{{centerName}}.png" alt="{{centerName}}" title="{{centerName}}" height="50" class="fancy-story-img img-polaroid">
            {{story}}
        </div>
    </script>

    <script type="text/template" id="search-results-gene-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/gene.png" class="img-polaroid search-info" title="Gene" alt="Gene" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-protein-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/protein.png" class="img-polaroid search-info" title="Protein" alt="Protein" height="50" width="50">
        </a>
    </script>


    <script type="text/template" id="search-results-shrna-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/shrna.png" class="img-polaroid search-info" title="shRNA" alt="shRNA" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-sirna-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/sirna.png" class="img-polaroid search-info" title="siRNA" alt="siRNA" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-transcript-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/transcript.png" class="img-polaroid search-info" title="Transcript" alt="Transcript" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-compund-image-tmpl">
        <a href="#subject/{{id}}">
            <img class="img-polaroid search-info" title="Compound" alt="Compound" width=50 height=50 src="<%=dataURL%>compounds/{{imageFile}}">
        </a>
    </script>

    <script type="text/template" id="search-results-animalmodel-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/animalmodel.png" title="Animal model" alt="Animal model" class="img-polaroid search-info" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-cellsample-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/cellsample.png" title="Cell sample" alt="Cell sample" class="img-polaroid search-info" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-tissuesample-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/tissuesample.png" title="Tissue sample" alt="Tissue sample" class="img-polaroid search-info" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-unknown-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/unknown.png" title="{{type}}" class="img-polaroid search-info" alt="{{type}}" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-result-row-tmpl">
        <tr>
            <td id="search-image-{{dashboardEntity.id}}"></td>
            <td>
                <a href="#subject/{{dashboardEntity.id}}">{{dashboardEntity.displayName}}</a><br>
                <i>{{dashboardEntity.organism.displayName != '-' ? "(" + dashboardEntity.organism.displayName + ")" : ""}}</i>
            </td>
            <td>
                <ul id="synonyms-{{dashboardEntity.id}}">
                    <!-- here will go the synonyms -->
                </ul>
            </td>
            <td>{{dashboardEntity.type}}</td>
            <td>
                <ul id="roles-{{dashboardEntity.id}}" data-score="{{dashboardEntity.score}}">
                    <!-- here will go the roles -->
                </ul>
            </td>
            <td class="nonewline">
                <a href="#subject/{{dashboardEntity.id}}" id="subject-observation-count-{{dashboardEntity.id}}">{{observationCount}}</a>
                <i class="icon-question-sign obs-tooltip {{observationCount < 1 ? 'hide' : ''}}" title="{{observationCount}} observations from {{centerCount}} centers: Tier {{maxTier}}"></i>
            </td>
        </tr>
    </script>
    
    <script type="text/template" id="search-result-gene-row-tmpl">
        <tr>
            <td id="search-image-{{dashboardEntity.id}}"></td>
            <td>
                <a href="#subject/{{dashboardEntity.id}}">{{dashboardEntity.displayName}}</a>
                <a href="#" class="addGene-{{dashboardEntity.displayName}} greenColor" title="Add gene to cart" >+</a>
                <br>
                <i>{{dashboardEntity.organism.displayName != '-' ? "(" + dashboardEntity.organism.displayName + ")" : ""}}</i>
            </td>
            <td>
                <ul id="synonyms-{{dashboardEntity.id}}">
                    <!-- here will go the synonyms -->
                </ul>
            </td>
            <td>{{dashboardEntity.type}}</td>
            <td>
                <ul id="roles-{{dashboardEntity.id}}" data-score="{{dashboardEntity.score}}">
                    <!-- here will go the roles -->
                </ul>
            </td>
            <td class="nonewline">
                <a href="#subject/{{dashboardEntity.id}}" id="subject-observation-count-{{dashboardEntity.id}}">{{observationCount}}</a>
                <i class="icon-question-sign obs-tooltip {{observationCount < 1 ? 'hide' : ''}}" title="{{observationCount}} observations from {{centerCount}} centers: Tier {{maxTier}}"></i>
            </td>
        </tr>
    </script>

    <script type="text/template" id="search-tmpl">
        <div class="container common-container" id="search-results-container">
            <h2>Search <small>for <i>{{decodeURIComponent(term)}}</i></small></h2>

            <table id="search-results-grid" class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>&nbsp; &nbsp;</th>
                    <th>Name</th>
                    <th>Synonyms</th>
                    <th>Type</th>
                    <th>Roles</th>
                    <th>Observations</th>
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

            <div id="submission-search-results" class="hide">
                <h3>Submissions</h3>
                <table id="searched-submissions" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>&nbsp; &nbsp;</th>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Center</th>
                        <th>Tier</th>
                        <th>Details</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </script>

    <script type="text/template" id="search-submission-tbl-row-tmpl">
        <tr>
            <td><a href="#submission/{{dashboardEntity.id}}"><img src="img/submission.png" width="50" alt="Submission" title="Submission"></a></td>
            <td><a href="#submission/{{dashboardEntity.id}}">{{dashboardEntity.submissionDate}}</a></td>
            <td>{{dashboardEntity.observationTemplate.description}}</td>
            <td><a href="#submission/{{dashboardEntity.id}}"><img src="img/{{dashboardEntity.observationTemplate.submissionCenter.displayName}}.png" title="{{dashboardEntity.observationTemplate.submissionCenter.displayName}}" alt="{{dashboardEntity.observationTemplate.submissionCenter.displayName}}" height="50"></a></td>
            <td><span class="badge tier-badge">Tier {{dashboardEntity.observationTemplate.tier}}</span></td>
            <td width=150>
                <a href="#submission/{{dashboardEntity.id}}" id="search-observation-count-{{dashboardEntity.id}}">{{observationCount}} observations</a>
            </td>
        </tr>
    </script>

    <script type="text/template" id="synonym-item-tmpl">
        <li class="synonym"><small>{{displayName}}</small></li>
    </script>

    <script type="text/template" id="role-item-tmpl">
        <li class="synonym"><small>{{role}}</small></li>
    </script>

    <script type="text/template" id="transcript-item-tmpl">
        <li class="synonym"><a href="#subject/{{id}}">{{refseqId}}</a></li>
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
        <div class="row one-story">
            <div class="span8">
                <h4>{{submission.observationTemplate.description}}</h4>
                <!--<p class="lead stories-lead">{{submission.observationTemplate.description}}</p>-->
                <p id="story-summary-{{id}}" class="stories-text">
                    <!-- leaving this blank, we have to construct the summary from the scratch. -->
                </p>
                <p class="pull-right">
                    (
                        <a target="_blank" href="<%=dataURL%>" id="file-link-{{id}}">
                            view full story</a>
                    |
                    <a href="#observation/{{id}}">see observation</a>)
                </p>
            </div>
            <div class="span4">
                <img class="img-circle" src="img/slogos/{{submission.observationTemplate.submissionCenter.displayName}}.png" alt="{{submission.observationTemplate.submissionCenter.displayName}}" title="{{submission.observationTemplate.submissionCenter.displayName}}" height=150>
            </div>
        </div>
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
                (see <a class="blurb-help" href="#help-navigate">background information</a> for the meaning of observations, roles, and Tiers).
            </p>
        </div>
    </script>

    <script type="text/template" id="explore-tmpl" data-url="<%=dataURL%>">
        <div class="container common-container" id="explore-container">
            <h2>Explore: <i>{{_.map(decodeURIComponent(roles).split(","), function(o) { return o + "s"; }, []).join(", ")}}</i> <small><a href="#" id="customize-roles" class="customize-{{type}}">({{customized ? "customized" : "select roles"}})</a></small></h2>

            <div id="explore-blurb"></div>
            <button type="button" class="btn btn-default" id="reset-ordering">Reset initial ordering</button>

            <table class="table table-bordered table-striped observations" id="explore-table">
                <thead>
                <tr>
                    <th colspan=3></th>
                    <th colspan=3 style="text-align:center">Observations</th>
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
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3>Select roles</h3>
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
                <button type="btn btn-block btn-info" id="select-roles-button">Select</button>
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
                <img src="img/icons/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view mra file<b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="#/evidence/{{id}}" title="Open Master Regulator View" class="desc-tooltip">
                                mra view
                            </a>
                        </li>
                        <li>
                            <a href="<%=dataURL%>{{evidence.filePath}}" class="desc-tooltip" target="_blank" title="type: ({{evidence.mimeType}})">view in browser</a>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>
    </script>  
    
    <script type="text/template" id="mra-view-tmpl" mra-data-url="<%=dataURL%>">
         <div class="container common-container" id="mra-container" > 
               <div class="row">
                 <div class="span10">
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
                 <div class="span1">                                   
                    <a href="javascript:history.back()">Back</a>
                 </div>              
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
                           <option value="arbor" selected="selected">Arbor</option>                         
                           <option value="grid">Grid</option>                           
                           <option value="random">Random</option>
                           <option value="circle">Circle</option>
                      </select>
                      <b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b> 					
                      <a href="#" id="createnetwork" data-description="{{observedEvidenceRole.displayText}}" target="_blank" title="please select master regulator to create network" class="mra-cytoscape-view">Create Network</a>   				 
                      <br/>	                    
			          <small><font color="grey">Threshold: </font></small>
                      <small id="throttle-input"><font color="grey">e.g. 0.01 </font></small>		 
                  </div>         
                  <br/>	
                  <table id="mra-barcode-grid" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                          <th width="450" title="Correlation of regulatory gene with its targets (red +, blue-) ordered by ranked differential expression, lowest at left.">Barcode</th>
                          <th width="50" title="Differential Activity(e.g. NES from GSEA)">DA</th>
                          <th width="50" title="Differential Expression">DE</th>
                          <th title="Rank of regulatory gene in overall DE results.">DE Rank</th>
                          <th title="E.g.  TF or signalling molecule whose regulon was tested for enrichment in differentially expressed genes.">Regulator</th>
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
        <div id="mra_progress">
            <img id="mra_progress_indicator" class="centeredImage" src="img/progress_indicator.gif" width="30" height="30" alt="Please wait ......">
        </div>
        <div id="cytoscape">            
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
    </script>

    <script type="text/template" id="existing-template-row-tmpl">
        <tr id="template-table-row-{{id}}" class='stored-template-row'>
            <td>{{displayName}}</td><td>{{description}}</td><td>{{project}}</td>
                <td>{{tier}}</td><td>{{dateLastModified}}</td><td>{{isStory}}</td>
                <td><select id='template-action-{{id}}'>
                    <option value=''>-</option>
                    <option value='edit'>Edit</option>
                    <option value='preview'>Preview</option>
                    <option value='clone'>Clone</option>
                    <option value='delete'>Delete</option>
                    <option value='download'>Download</option>
                    </select>
                </td>
        </tr>
    </script>

    <script type="text/template" id="role-dropdown-row-tmpl">
        <option {{selected}}>{{roleName}}</option>
    </script>

    <script type="text/template" id="template-subject-data-row-tmpl">
        <tr id="template-subject-row-columntag-{{columnTagId}}" class="template-data-row">
            <td style="text-align:center;"><img src="img/icons/remove.png" style="width:20px;height:20px;" id="delete-subject-{{columnTagId}}" /></td>
            <td><select id="subject-class-dropdown-{{columnTagId}}" class='subject-classes'>
                <option {{ subjectClass=='gene'?'selected=selected':'' }} >gene</option>
                <option {{ subjectClass=='shrna'?'selected=selected':'' }} >shrna</option>
                <option {{ subjectClass=='tissue_sample'?'selected=selected':'' }} >tissue_sample</option>
                <option {{ subjectClass=='cell_sample'?'selected=selected':'' }} >cell_sample</option>
                <option {{ subjectClass=='compound'?'selected=selected':'' }}>compound</option>
                <option {{ subjectClass=='animal_model'?'selected=selected':'' }} >animal_model</option>
            </select></td>
            <td><select id="role-dropdown-{{columnTagId}}" class='subject-roles'></select></td>
            <td><input type="text" class="form-control subject-columntag" value="{{columnTag}}" placeholder="column tag"></td>
            <td><input type="text" class="form-control subject-descriptions collapsed-textarea" id="description-{{columnTagId}}" placeholder="subject description" value="{{subjectDescription}}"></td>
        </tr>
    </script>

    <script type="text/template" id="evidence-type-dropdown-tmpl">
        <option {{ selected?'selected=selected':'' }} >{{evidenceType}}</option>
    </script>

    <script type="text/template" id="template-evidence-data-row-tmpl">
        <tr id="template-evidence-row-columntag-{{columnTagId}}" class="template-data-row">
            <td style="text-align:center;"><img src="img/icons/remove.png" style="width:20px;height:20px;" id="delete-evidence-{{columnTagId}}" /></td>
            <td><select id="value-type-{{columnTagId}}" class='value-types'>
                <option {{ valueType=='numeric'?'selected=selected':'' }} >numeric</option>
                <option {{ valueType=='label'?'selected=selected':'' }} >label</option>
                <option {{ valueType=='file'?'selected=selected':'' }} >file</option>
                <option {{ valueType=='url'?'selected=selected':'' }} >url</option>
            </select></td>
            <td><select id="evidence-type-{{columnTagId}}" class='evidence-types'></select></td>
            <td><input type="text" class="form-control evidence-columntag" value="{{columnTag}}" placeholder="column tag"></td>
            <td><input type="text" class="form-control evidence-descriptions collapsed-textarea" id="evd-descr-{{columnTagId}}" placeholder="evidence description" value="{{evidenceDescription}}"></td>
        </tr>
    </script>

    <script type="text/template" id="submitter-information-tmpl">
        <tr><th>Submitter First Name *</th>
            <td><input id="first-name" placeholder="first name is required" class="input-xxxlarge" value="{{firstName}}"></td></tr>
        <tr><th>Submitter Last Name *</th><td>
            <input id="last-name" placeholder="last name is required" class="input-xxxlarge" value="{{lastName}}"></td></tr>
        <tr><th>Contact E-mail</th><td><input id="email" placeholder="email is required" class="input-xxxlarge" value="{{email}}"></td></tr>
        <tr><th>Contact phone number</th>
            <td><input id="phone" placeholder="phone number is optional" class="input-xxxlarge" value="{{phone}}"></td></tr>
    </script>

    <script type="text/template" id="template-description-tmpl">
        <tr><th>Submission Name *</th>
            <td><input id="template-name" placeholder="e.g. centername_your_description" class="input-xxlarge" value="{{displayName}}"></td></tr>
        <tr><th>Submission Description</th>
            <td><textarea id="template-submission-desc" placeholder="e.g. Down-regulated genes in PTEN-null cell lines" class="input-xxxlarge">{{description}}</textarea></td>
        </tr>
        <tr><th>Project Title</th>
            <td><input id="template-project-title" placeholder="Please enter a title for this or a collection of related subissions (correponds e.g. to an entire paper)" class="input-xxxlarge" value="{{project}}">
            </td>
        </tr>
        <tr><th>Request Tier</th>
            <td><select id="template-tier" style="width:300px">
                <option value=1 {{tier==1?'selected=selected':null}}>Tier 1 (initial or screening)</option>
                <option value=2 {{tier==2?'selected=selected':null}}>Tier 2 (in vitro)</option>
                <option value=3 {{tier==3?'selected=selected':null}}>Tier 3 (in vivo validation)</option>
            </slect></td>
        </tr>
        <tr><th>Is this submission a story?</th><td><input id="template-is-story" type="checkbox" {{isStory?'checked':''}} /></td></tr>
        <tr id='story-title-row'><th>Story Title</th><td><input id='story-title' class="input-xxlarge" value='{{storyTitle}}' /></td></tr>
    </script>

    <script type="text/template" id="template-helper-tmpl">
        <div class="container common-container" id="template-helper-container">
            <h2>CTD<sup>2</sup> Data Submission Builder</h2>

            <div class="btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-link" id='menu_home'>Home</button>
            <button type="button" class="btn btn-link" id='menu_manage'>Manage Submission</button>
            </div>
            <div class="btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-link" id='menu_description'>Submission Description</button>
            <button type="button" class="btn btn-link" id='menu_data'>Submission Data</button>
            <button type="button" class="btn btn-link" id='menu_summary'>Observation Summary</button>
            <button type="button" class="btn btn-link" id='menu_preview'>Preview</button>
            </div>

            <div id="step1">
                <div class="alert alert-warning alert-block">
                  <a href="#" class="close" data-dismiss="alert">&times;</a>
                  <p>
                    <strong>Welcome to the submission template helper!</strong><br>
                    This tool will help create a basic Dashboard submission template from scratch.
                    Once a basic template is prepared, the template can be downloaded for local use and preparation of a Dashboard submission.
                  </p>
                </div>
                <h3>Submission Builder Home</h3>
                <table class="table">
                    <tr>
                        <th>Please choose you CTD<sup>2</sup> group</th>
                        <td>
                            <select id="template-submission-centers" class="input-xxlarge">
                                <option value="">-</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td colspan=2 class="next-cell">
                            <button id="apply-submission-center" class="btn">Continue</button>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="step2" class="hide">
                <h3>Manage Submission</h3>
                <b>Center:</b> <span id="center-name"></span>
                <div class="alert alert-warning alert-block">
                <a href="#" class="close" data-dismiss="alert">&times;</a>
                <p>
                    A submission is a collection of one or more observations that summarize the results of experimental or computational investigations. A scientific publication can give rise to one or multiple such submissions. For example, a single publication may advance a hypothesis step by step, with each step potentially meriting a separate submission. 
                </p>
                </div>
                <div class="alert alert-warning alert-block">
                <a href="#" class="close" data-dismiss="alert">&times;</a>
                <p>
                    You will be able to reuse information entered for one submission in additional, related submissions. A project title is used to group together such related submissions. 
                </p>
                </div>
                <table class="table table-bordered table-striped" id="existing-template-table">
                        <tr id="template-header">
                            <th>Submission Name</th><th>Submission Description</th><th>Project</th><th>Tier</th><th>Date last modified</th><th>Is Story</th><th>Action</th>
                        </tr>
                </table>

                <table  class="table">
                    <tr>
                        <td class="next-cell">
                            <button id="create-new-submission" class="btn">Create New Submission</button>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="step3" class="hide">
                <h3>Submitter Information</h3>
                <b>Center:</b> <span id="center-name"></span>
                <table class="table" id="submitter-information"></table>
                <h3>Submission Description</h3>
                <table class="table" id="template-description"></table>
                <table class="table">
                    <tr>
                        <td class="next-cell"><button id="save-name-description">Save</button></td>
                        <td class="next-cell"><button id="continue-to-main-data">Save and Continue</button></td>
                    </tr>
                </table>
            </div>

            <div id="step4" class="hide">
                <h3>Submission Data</h3>
                <b>Center:</b> <span id="center-name"></span><br/>
                <b>Submission Name:</b> <span id="submission-name"></span>
                <!-- this table contains the main data, which dictate the data structure I should use -->
                <div style="overflow-x:auto;">
                <table class="table table-bordered table-striped" id="template-table">
                    <tbody id="template-table-subject">
                        <tr><th colspan=5 style="background-color:#e6f6ff;">Subject</tr>
                        <tr id="subject-header">
                            <th>Delete Row</th><th>Subject Class</th><th>Subject Role</th><th>Column Tag</th><th>Description</th>
                        </tr>
                        <!-- here goes rows for subject columns -->
                    </tbody>
                    <tbody id="template-table-evidence">
                        <tr><th colspan=5 style="background-color:#e6f6ff;">Evidence</tr>
                        <tr id="evidence-header">
                            <th>Delete Row</th><th>Value Type</th><th>Evidence Type</th><th>Column Tag</th><th>Description</th>
                        </tr>
                        <!-- here goes the rows for evidence columns -->
                    </tbody>
                </table>
                </div>
                <table class="table">
                    <tr>
                        <td><button id="add-subject" class="next-cell">New Subject Row</button></td>
                        <td><button id="add-evidence" class="next-cell">New Evidence Row</button></td>
                        <td><button id="add-observation" class="next-cell">New Observation Column</button></td>
                    </tr>
                    <tr>
                        <td><button id="save-template-submission-data" class="next-cell">Save</button></td>
                        <td><button id="apply-template-submission-data" class="next-cell">Save and Continue</button></td>
                    </tr>
                </table>
            </div>

            <div id="step5" class="hide">
                <h3>Observation Summary</h3>
                <b>Center:</b> <span id="center-name"></span><br/>
                <b>Submission Name:</b> <span id="submission-name"></span><br/>
                <ul>
                    <li>Please write/update a summary of the observations, using any or all of the "tags" pre-entred in the text box.</li>
                    <li>On the dashboard, the tag will be replaced with the actual values you submit.</li>
                    <li>Delete tags you do not need.</li>
                    <li>Use the Helper to see all available tags.</li>
                </ul>
                <table class="table">
                    <tr>
                        <th>Observation summary</th>
                        <td>
                            <textarea id="template-obs-summary" placeholder="e.g. <gene_column> is down-regulated in <label_evidence> cells" class="input-xxxlarge"></textarea>
                        </td>
                        <td>
                        <span class="dropdown">
                            <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Helper
                            <span class="caret"></span></button>
                            <ul class="dropdown-menu" id='column-tag-list'></ul>
                        </span> 
                        </td>
                    </tr>
                </table>
                <button id="save-summary">Save</button>
                <button id="continue-from-summary">Save and Continue</button>

                <form action="template/download" method="POST" id="download-form" style="display:inline">
                            <button id="download-template">Download template</button>
                            <input type="hidden" name="template-id" id="template-id">
                            <input type="hidden" name="filename" id="filename-input">
                </form>
            </div> <!-- end of step 5 -->

            <div id="step6" class="hide">
                <h3>Preview</h3>
                <b>Center:</b> <span id="center-name"></span><br/>
                <b>Submission Name:</b> <span id="submission-name"></span><br/>
                <select id="preview-select"></select>
                <div id='preview-container'></div>
            </div> <!-- end of step 6 -->

        </div><!-- end of template-helper-container -->
    </script>

    <script type="text/template" id="observation-option-tmpl">
        <option value={{observation_id}}>observation {{observation_id+1}}</option>
    </script>

    <script type="text/template" id="column-tag-item-tmpl">
        <li id={{id}} class=helper-tag>{{tag}}</li>
    </script>

    <script type="text/template" id="temp-observation-tmpl">
        <td><div class='uploaded'>{{uploaded}}</div><input type="{{type}}" class="form-control" id="observation-{{obvNumber}}-{{obvColumn}}" value="{{obvText}}" placeholder="enter value"></td>
    </script>

    <script type="text/template" id="template-helper-center-tmpl">
        <option value="{{id}}">{{displayName}}</option>
    </script>

    <script type="text/template" id="template-header-col-tmpl">
        <td class="{{id}}" data-type="{{columnType}}"></td>
    </script>

    <script type="text/template" id="template-sample-data-tmpl">
        <input placeholder="enter data" class="sample-data-input">
    </script>

    <script type="text/template" id="more-observations-tmpl">
        <div class="alert alert-warning">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p>
                Only {{numOfObservations}} of {{numOfAllObservations}} observations are listed in the table below.
                To load all observations please <a href="#" class="load-more-observations">click here</a>
                (<i>this might take a while</i>).
            </p>
        </div>
    </script>


    <script type="text/template" id="more-projects-tmpl">
        <div class="alert alert-warning">
            <p><a href="#" class="close" data-dismiss="alert">&times;</a></p>
            <p>
                The table below lists all submissions that belong to the project "<b>{{filterProject}}</b>".
                To view all submissions from this center, please click <a href="#center/{{centerId}}">here</a>.
            </p>
        </div>
    </script>

    <script type="text/template" id="ncithesaurus-tmpl">
        <li>
            <a href="http://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code={{nciId}}" target="_blank">
                NCI Thesaurus: {{nciId}}
            </a>
        </li>
    </script>

    <script type="text/template" id="annotation-tmpl">
        <li><small>{{displayName}} ({{source}})</small></li>
    </script>

    <script type="text/template" id="maxNumberOfEntites">
        <%=maxNumOfObservations%>
    </script>
    
    <script type="text/template" id="genelist-view-tmpl" >
         <div class="container common-container" id="genelist-container" > 
             
                 <div class="span10" align="center">                   
                    <h4>  Gene List</h4>

                     <div class="alert alert-warning">
                         <button type="button" class="close" data-dismiss="alert">&times;</button>
                           <p>
                             The Gene Cart allows users to build a list of genes and query the Cellular Networks Knowledge Base (CNKB) for molecular interactions involving these genes.  The CNKB is a database of gene interaction networks maintained at Columbia University [
                             <a href="#gene-cart-help">More Details</a>
                             ].
                           </p>
                     </div>

                    <select id="geneNames" class="geneSelectList" size="6" 
								multiple></select>
                    </br></br>
                    <a href="#" id="addGene">Add Gene</a>                         
                    <a href="#" id="deleteGene">Delete Gene</a>                   
                    </br></br>               
                    <a href="#" id="clearList">Clear List</a>                    
                    <a href="#" id="loadGenes">Load Genes from File</a>                                
                    </br><input id="geneFileInput" type="file" style="visibility:hidden" /> 
                    </br>                  	   	   
                    <a href="#cnkb-query" id="cnkb-query">Find Gene Interactions in  Networks (CNKB)</a>                      
                 </div>

                 <div class="span1">                                   
                    <a href="javascript:history.back()">Back</a>
                 </div>              
             </div>

            <div class="modal hide fade" id="addgene-modal">             
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
      </script>     
      
      <script type="text/template" id="cnkb-query-tmpl" >
         <div class="container common-container" id="cnkbquery-container" > 
                    
                    <div class="span10">                       
                       <h3>Cellular Network Knowledge Base</h3>

                       <medium>Select Interactome:</medium>                  
                       <small id="queryDescription" class="cnkbDescription"></small> 
                       </br>                  
                       <select id="interactomeList" name="interactomes"
						    class="cnkbSelectList" size="4"></select>
                       </br>
                       <small id="interactomeDescription" class="cnkbDescription">
                        &nbsp;&nbsp;
                       </small>             
                     </br></br>
                      
                    <medium class="labelDisable" id="selectVersion"> Select Version: </medium>
                    </br>                
                    <select id="interactomeVersionList" name="interactomeVersions"
						 class="cnkbSelectList" size="4"></select>
                     </br>
                    <small id="versionDescription" class="cnkbDescription">
                        &nbsp;&nbsp;
                    </small>             
                     </br>
		             </br>
                     <a href="#cnkb-result" id="cnkb-result">Submit</a>
                 </div>

                 <div class="span1">                                   
                    <a href="javascript:history.back()">Back</a>
                 </div>              
             </div>
      </script>
      
      <script type="text/template" id="cnkb-result-tmpl" >
         <div class="container common-container" id="cnkbresult-container" > 
               <div class="row">
                  <div class="span10">
                     <h3>Cellular Network Knowledge Base</h2>                                  			                    
                     <a href="#" id="cnkbExport"  target="_blank" title="Export all selected interaction to a SIF file."> Export </a>
                     <br>
                     <form method="POST" action="cnkb/download" id="cnkbExport-form" style="display: none;">                           
                             <input type="hidden" name="interactome" id="interactome">
                             <input type="hidden" name="version" id="version">
                             <input type="hidden" name="selectedGenes" id="selectedGenes">  
                             <input type="hidden" name="interactionLimit" id="interactionLimit">
                             <input type="hidden" name="throttle" id="throttle">                             
                     </form>                    
                     <table id="cnkb-result-grid" class="table table-bordered table-striped ">
                        <thead> 
                            <tr>                       
                            <th><input type="checkbox" id="checkbox_selectall" title="select or deselect all checkboxs"></th>
                            <th>GENE</th>
                            </tr>
                         </thead>
                         <tbody>                            
                         </tbody>
                      </table>  
                   </div>
                   <div class="span1">                                   
                      <a href="javascript:history.back()">Back</a>
                   </div>              
                </div>                  
                <br/>

                <div id="cnkb_data_progress" align="center">data is loading ......
                    <img id="cnkb_data_progress_indicator" src="img/progress_indicator.gif" width="20" height="20" alt="Please wait ......"><br>
                    <br/><br/><br/>
                </div>
                <div>         
					<b>Interactions Limit:</b>	
                    <select id="cytoscape-node-limit">
                           <option value="25">25</option>
                           <option value="50">50</option>                        
                           <option value="100" selected="selected">100</option>                                             
                           <option value="200">200</option>
                           <option value="300">300</option>
                           <option value="400">400</option>                                                                        
                     </select>					
				  
                     <b>Layout:</b>	
                     <select id="cytoscape-layouts">
                           <option value="arbor" selected="selected">Arbor</option>                         
                           <option value="grid">Grid</option>                           
                           <option value="random">Random</option>
                           <option value="circle">Circle</option>
                     </select>
                     
                     <a href="#" id="createnetwork"  target="_blank" title="please select cnkb interactions to create network">Create Network</a>   				 
                     <br/>	                    
			         <small><font color="grey">Confidence threshold: </font></small>
                     <small id="throttle-input"><font color="grey">e.g. 0.01 </font></small>	
				   	 <div id="createnw_progress_indicator" align="center" style="display: none;">data is loading ......
                         <img id="cnkb_data_progress_indicator" src="img/progress_indicator.gif" width="20" height="20" alt="Please wait ......"><br>
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
      
      <script type="text/template" id="cnkb-cytoscape-tmpl">
        
        <div id="cnkb_cytoscape_progress">
            <img id="cnkb_cytoscape_progress_indicator" class="centeredImage" src="img/progress_indicator.gif" width="30" height="30" alt="Please wait ......">
        </div>
        <div id="cytoscape">            
        </div>
        <div id="cnkb-cytoscape-legend" class="well cytoscape-legend">       
            <svg  width="500" height="30"xmlns="http://www.w3.org/2000/svg">            
               {{svgHtml}}
            </svg>
            <br/>
            {{description}}  
        </div>
      </script>
     
    <script type="text/template" id="gene-cart-help-tmpl" >
         <div class="container common-container" id="cnkbhelp-container" > 
               <div class="span10">                       
                    <h3>Gene Cart Help</h3>
                    <p>The Gene Cart allows users to build a list of genes and query the Cellular Networks Knowledge Base (CNKB) for molecular interactions involving these genes.  The CNKB is a repository of molecular interactions networks. It contains computationally-derived networks obtained by applying state of the art Systems and Structure Biology algorithms from the laboratories of Drs. Andrea Califano and Barry Honig at Columbia University. A detailed <a target="_blank" href="http://wiki.c2b2.columbia.edu/workbench/index.php/Cellular_Networks_KnowledgeBase">description of the CNKB</a> is available which also describes how the CNKB can be accessed from within the software platform <a target="_blank" href="http://www.geworkbench.org">geWorkbench</a>.</p>
                    <p>In the Observations for a particular Dashboard submission, those entries that are genes will have a green "+" sign to right of the gene symbol. Clicking this "+" sign will add the gene to the Gene Cart. The Gene Cart is limited to 25 genes.</p>
                    <p>In the Gene Cart, clicking on "Find Interactions in Networks (CNKB)" will bring the user to the Cellular Networks Knowledge Base page where a particular interactome and version can be chosen. Descriptive text for each is available by selecting any particular interactome or version. Clicking "Submit" will initiate a query of the CNKB using the genes in the cart.  "Version" may represent different types of interactions inferred on a given dataset.  See the description of each version for details.</p>
                    <p>The query result is displayed in a table showing the number and type of interactions found for each query gene. A check box to the left of each gene allows individual results to be selected. The interactions for selected genes can then be downloaded in the form of a Cytoscape "SIF"-format file, or displayed directly in Cytoscape.js in the browser. The number of interactions to display is controlled using the "Interactions Limit" pulldown. Interactions to display are then chosen based on a ranking by a measure of likelihood, e.g. the top 100 interactions.</p>
                    <p>Several layout options are available for Cytoscape and can be selected using the "Layout" pulldown.</p>
                    <p>In Cytoscape.js, several common interaction types have been assigned specific colors used for the lines representing them, and these will be shown on the legend of the graph.  The genes used in the CNKB query (hub genes) will be highlighted in yellow.</p>
                </div>
                <div class="span1">                                   
                   <a href="javascript:history.back()">Back</a>
                </div>              
         </div>
     </script>
     
     <script type="text/template" id="gene-cart-option-tmpl">
        <option value="{{displayItem}}">{{displayItem}}</option>
     </script>

     <script type="text/template" id="gene-cart-option-tmpl-preselected">
        <option value="{{displayItem}}" selected>{{displayItem}}</option>
     </script>

    <script type="text/template" id="help-navigate-tmpl">
        <div class="help-navigate-text-container">
            <h3>Navigating and Understanding Dashboard Content</h3>
            <p>
                The CTD<sup>2</sup> Network aims to increase understanding of the underlying molecular causes of distinct cancer types and accelerate development of clinically useful biomarkers and targeted therapies for precision medicine.
                The Dashboard is one tool that provides access to Network findings.
                Results are available as bulk datasets, data-related figures, or polished stories, and are formatted to enable navigation and comprehension by most researchers, from computational experts to those with little bioinformatics dexterity.
                Through the Dashboard, the <b>CTD<sup>2</sup> Network</b> gives the research community a method to interrogate experimental observations across the Centers.
                Before using the Dashboard, read the following to learn how Dashboard content is organized.
            </p>

            <ul>
                <li><i>Center</i>: One of 13 academic research teams that make up the CTD<sup>2</sup> Network. To learn more about the current Centers, visit <a target="_blank" href="https://ocg.cancer.gov/programs/ctd2/centers">https://ocg.cancer.gov/programs/ctd2/centers</a>.</li>

                <li><i>Submission</i>: A Dashboard entry that represents a dataset associated with positive experimental results, a set of data-related figures, or a polished story.</li>

                <li><i>Subject</i>: The focus of an experiment or result in a Dashboard <b>submission</b> (<i>e.g.</i>, genes, proteins, small molecules, cell lines, animal models).</li>
                <ul>
                    <li>Class</i>: A set of objects representing the same molecular or biological category (DNA, RNA, protein, small molecule, tissue, animal model) and sharing a set of required and optional attributes.</li>
                    <li><i>Role</i>: The <b>Center</b>-designated function of a gene, protein, or compound based on their interpretation of observations within a particular experimental or computational context. Assigning <b>role</b>s from a restricted list of terms (biomarkers, diseases, master regulators, interactors, oncogenes, perturbagens, candidate drugs, or targets) helps organize subjects in Dashboard for browsing and searching.
                </ul>

                <li><i>Evidence</i>: Selected positive or validated results from a scientific experiment (<i>e.g.</i>, numeric values, text labels, data figures).</li>
                <ul>
                    <li><i>Type</i>:  Category of evidence provided in support of the results. Examples include the following: literature, measured, link, reference, background, observed, computed, written, resources, species.
                </ul>

                <li><i>Observation</i>: A <b>Center</b>-determined conclusion that is submitted as a connection between <b>subjects</b> and <b>evidence</b>; the "fundamental unit" of the Dashboard.</li>

                <li><i><a href="http://www.ncbi.nlm.nih.gov/pubmed/27401613" target="_blank">Tier</a></i>: A CTD<sup>2</sup> Network-defined ranking system for <b>evidence</b> that is based on the extent of characterization associated with a particular study.
                    <ul>
                        <li><i>Tier 1</i>: Preliminary positive observations.</li>
                        <li><i>Tier 2</i>: Confirmation of primary results <i>in vitro</i>.</li>
                        <li><i>Tier 3</i>: Validation of results in a cancer relevant <i>in vivo</i> model.</li>
                    </ul>
                </li>
            </ul>

            <h3>Dashboard Organization</h3>
            <p>
                During the <b>submission</b> process, <b>subjects</b> from CTD2 Network studies are ordered by relevance based on the number of <b>observations</b> associated with a <b>submission</b>, the <b>Tiers</b> of these <b>observations</b>, and the number of different <b>Centers</b> providing <b>observations</b> about that particular <b>subject</b>.
            </p>

            <br>
            <hr>
            <br>
        </div>
    </script>

    <script id="tbl-project-title-tmpl" type="text/template">
        <tr class="group"><td colspan="5"><a href="#center/{{centerId}}/{{project}}">Project: {{project}}</a></td></tr>
    </script>

    <script id="center-specific-information-tmpl" type="text/template">
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#broad-institute" data-center="Broad Institute" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#cold-spring-harbor-laboratory" data-center="Cold Spring Harbor Laboratory" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#columbia-university" data-center="Columbia University" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#dana-farber-cancer-institute" data-center="Dana-Farber Cancer Institute" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#emory-university" data-center="Emory University" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#fred-hutchinson-cancer-research-center-1" data-center="Fred Hutchinson Cancer Research Center (1)" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#fred-hutchinson-cancer-research-center-2" data-center="Fred Hutchinson Cancer Research Center (2)" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#university-of-texas-md-anderson-cancer-center" data-center="University of Texas MD Anderson Cancer Center" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#stanford-university" data-center="Stanford University" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#university-of-california-san-francisco-1" data-center="University of California San Francisco (1)" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#university-of-california-san-francisco-2" data-center="University of California San Francisco (2)" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#university-of-texas-southwestern-medical-center" data-center="University of Texas Southwestern Medical Center" target="_blank">view center description</a>
        <a href="https://ocg.cancer.gov/programs/ctd2/centers#translational-genomics-research-institute" data-center="Translational Genomics Research Institute" target="_blank">view center description</a>
    </script>

    <!-- end of templates -->
    
    <script src="js/jquery.dataTables.min.js"></script>
    <script src="js/dataTables.buttons.min.js"></script>
    <script src="js/buttons.html5.min.js"></script>
    <script src="js/jszip.min.js"></script>
    <script src="js/paging.js"></script>
    <script src="js/holder.js"></script>
    <script src="js/underscore.js"></script>
    <script src="js/json2.js"></script>
    <script src="js/backbone-min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/jquery.fancybox-1.3.4.pack.js"></script>
    <script src="js/jquery.easing-1.3.pack.js"></script>
    <script src="js/jquery.expander.min.js"></script>
    <script src="js/arbor.js"></script>
    <script src="js/cytoscape.min.js"></script>  
    <script src="js/encoder.js"></script>
    <script src="js/jquery.contextMenu.js"></script>   
    <script src="js/jquery.ui.position.js"></script>    
    <script src="js/ctd2.js"></script>
    <script src="js/template.helper.js"></script>

  </body>
</html>
