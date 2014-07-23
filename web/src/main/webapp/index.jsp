<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %><%@page import="org.springframework.web.context.WebApplicationContext"%><%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%><%
    WebApplicationContext context = WebApplicationContextUtils
            .getWebApplicationContext(application);
    String dataURL = (String) context.getBean("dataURL");
    Integer maxNumOfObservations = (Integer) context.getBean("maxNumberOfEntities");
%><!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
  <head>
    <meta charset="utf-8">
    <title>CTD^2: Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/jquery.dataTables.css" rel="stylesheet">
    <link href="css/jquery.fancybox-1.3.4.css" rel="stylesheet" type="text/css" media="screen">
    <link href="css/flippant.css" rel="stylesheet">
    <link href="css/ctd2.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="shortcut icon" href="img/favicon.png">
  </head>

  <body>

    <!-- NAVBAR
    ================================================== -->
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
              <ul class="nav topmenu">
                <li class="active"><a href="#">CTD<sup>2</sup> Dashboard</a></li>
                <li><a href="#about">About</a></li>
                <li><a href="#centers">Centers</a></li>
                <li class="dropdown">
                      <a href="#" class="dropdown-toggle" data-toggle="dropdown">Resources <b class="caret"></b></a>
                      <ul class="dropdown-menu">
                          <li><a target="_blank" href="http://ocg.cancer.gov/programs/ctd2">CTD<sup>2</sup> Home page</a></li>
                          <li><a target="_blank" href="http://ocg.cancer.gov/programs/ctd2/publications">Publications</a></li>
                          <li><a target="_blank" href="http://ctd2.nci.nih.gov/DataMatrix/CTD2_DataMatrix.html">Data Matrix</a></li>
                          <li><a target="_blank" href="http://ocg.cancer.gov/about-ocg/rss-feeds">RSS feed</a></li>
                          <li class="divider"></li>
                          <li><a href="#template-helper">Submission Template Helper</a></li>
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
     	<p>
             CTD<sup>2</sup> Dashboard &middot;
             <a href="http://ctd2.nci.nih.gov/centers.html" target="_blank">Contact</a>
         </p>
         <p>
             <a href="http://cancer.gov"><img src="img/logos/footer_logo_nci.jpg" alt="NCI logo" title="NCI logo"></a><a href="http://www.dhhs.gov/"><img src="img/logos/footer_logo_hhs.jpg" title="HHS logo" alt="HHS logo"></a><a href="http://www.nih.gov/"><img src="img/logos/footer_logo_nih.jpg" title="NIH logo" alt="NIH logo"></a><a href="http://www.firstgov.gov/"><img src="img/logos/footer_logo_firstgov.jpg" title="First Gov logo" alt="First Gov logo"></a>
         </p>
       </footer>
    </div>

    <!-- these are the templates -->
    <script type="text/template" id="home-tmpl">
        <div class="overview-container">
            <div class="container overview-box">
                <div class="row">
                    <div class="span10 offset1">
                        <div class="featurette" id="overview-text">
                            <img class="img-polaroid pull-right" src="img/logos/ctd2_overall.png" alt="CTD2 general image" title="CTD2 general image" width="200">
                            <h2 class="featurette-heading">CTD<sup>2</sup> Dashboard</h2>
                            <p class="lead">
                                <b>The Dashboard</b> hosts data generated and analyzed by the <b>CTD<sup>2</sup> Network</b> and provides an intuitive mechanism for the research community to search across <i>Centers</i>' data.
                                <i>Observation</i>s, or Center-determined conclusions, are submitted as connections between two related types of components:
                                <b>subjects</b> (<i>e.g.</i>, gene, transcript, protein, small molecules, animal model) and <b>evidence</b> (<i>e.g.</i>, numeric value, text label, figure legend).
                                Users can retrieve evidence and observations pertinent to their queries by searching across subjects using standardized terms and vocabulary.
                                Results are available as bulk datasets, data-related figures, or polished stories, and are formatted to enable easy navigation and comprehension for most researchers, even those with little bioinformatics expertise.
                            </p>

                            <div id="overview-hidden-part" class="hide">
                                <p class="lead">
                                    The Dashboard aims to provide the research community with access to sets of positive results from one Center or from multiple Centers that can be retrieved using a single keyword.
                                    In doing this, the <b>CTD<sup>2</sup> Network</b> strives to increase the understanding of the underlying molecular causes of distinct cancer types and accelerate the development of clinically useful markers and targeted therapies for precision medicine.
                                </p>
                                <p class="lead">
                                    For more information about the <b>CTD<sup>2</sup> Network</b>, visit <a href="http://ocg.cancer.gov/programs/ctd2" title="CTD2 Center website" class="overview-link">http://ocg.cancer.gov/programs/ctd2</a>.
                                </p>
                            </div>

                            <div id="show-hide-overview">
                                <a href="#" class="show-more">(learn more)</a>
                                <a href="#" class="show-less hide">(show less)</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="dark-separator"></div>

        <!-- Carousel
        ================================================== -->
        <div id="myCarousel" class="carousel slide">
          <div class="carousel-inner">
            <div class="item active">
              <img src="img/bg-red.png" alt="Red background image" title="red background image" class="cimg">
              <div class="container">
                  <div class="carousel-caption">
                        <h1 class="homepage-stories-title">Stories</h1>
                        <div class="well carousel-well">
                            <div class="tab-content stories-tabs">
                                <div class="container tab-pane active fade in" id="story-1"></div>
                                <div class="container tab-pane fade" id="story-2"></div>
                                <div class="container tab-pane fade" id="story-3"></div>
                                <div class="container tab-pane fade" id="story-4"></div>
                            </div>
                            <br/>
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

        <div class="container marketing ctd2-boxes">
          <div class="row">
            <div class="span3 stories" data-order="0">
              <h4>Stories</h4>
              <p>
                  In each <i>story</i>, Dashboard users can find research highlights from CTD<sup>2</sup> Network projects, a link to the list of observations related to the results, and other resources relevant to the data.
              </p>
              <a class="btn btn-danger btn-block" href="#stories">Browse &raquo;</a>
            </div><!-- /.span3 -->
            <div class="span3 target" data-order="1">
              <h4>Targets</h4>
                <p>
                    <i>Targets</i> are molecules, such as genes or proteins, which have been experimentally identified as tumor markers or drivers.
                </p>
                <a class="btn btn-success btn-block" href="#browse/target/A">Browse &raquo;</a>
            </div><!-- /.span3 -->
            <div class="span3 drug" data-order="2">
              <h4>Compounds</h4>
                <p>
                    In Dashboard, <i>compounds</i> are modulators of specific targets in cancer cell lines or tumor model systems. Some examples include small molecules, FDA approved drugs, natural products, and small regulatory RNAs.
                </p>
                <a class="btn btn-warning btn-block" href="#browse/compound/A">Browse &raquo;</a>
            </div><!-- /.span3 -->
              <div class="span3 context" data-order="3">
                  <h4>Context</h4>
                  <p>
                      Dashboard entries provide a list of observations and evidence, and each is associated with a description.
                      In some instances, the description is labeled as <i>context</i>.
                      This refers to the genomic context, or set of circumstances or conditions which a target, compound, or biomarker may be relevant.
                  </p>
              </div><!-- /.span3 -->

          </div><!-- /.row -->
        </div><!-- /.container -->
    </script>

    <script type="text/template" id="centers-tmpl">
        <div class="container common-container" id="centers-container">
            <h1>Centers</h1>
            <br>
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
            <h1>Stories</h1>
            <br>
            <ul class="thumbnails stories-list">
                <!-- here will come the stories... -->
            </ul>
        </div>
    </script>


    <script type="text/template" id="stories-tbl-row-tmpl">
        <li class="span6 story-cards" id="story-observation-link-{{id}}" title="Click for more details">
            <a href="#observation/{{id}}" class="thumbnail">
                <img class="img-circle" src="img/slogos/{{submission.observationTemplate.submissionCenter.displayName}}.png" alt="{{submission.observationTemplate.submissionCenter.displayName}}" title="{{submission.observationTemplate.submissionCenter.displayName}}">
                <center>
                    {{submission.observationTemplate.description}}<br>
                    <small>({{submission.submissionDate}})</small><br>
                    <br>
                </center>
            </a>
        </li>
        <div class="hide" id="back-of-story-{{id}}">
            <h4>{{submission.observationTemplate.description}}</h4>

            <p id="story-list-summary-{{id}}" class="stories-text"></p>

            <p class="pull-right">
                (<small>
                <a target="_blank" href="<%=dataURL%>" id="file-link2-{{id}}">
                    view full story
                </a>
            </small>
                |
                <small><a href="#observation/{{id}}">see observation</a></small>)
            </p>
        </div>
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
                    <h1>{{displayName}} <small>submissions</small></h1>
                </div>
                <div class="span3">
                    <img src="img/{{displayName}}.png" title="{{displayName}}" alt="{{displayName}}" class="img-polaroid" width="200">
                </div>
            </div>

            </br>

            <table id="center-submission-grid" class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th width="150">Submission Date</th>
                        <th>Description</th>
                        <th>Tier</th>
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
            <td><a href="#submission/{{id}}">{{submissionDate}}</a></td>
            <td>
                {{(observationTemplate.submissionDescription != "") ? observationTemplate.submissionDescription : observationTemplate.description}}
            </td>
            <td><span class="badge tier-badge">Tier {{observationTemplate.tier}}</span></td>
            <td width=150>
                <a href="#submission/{{id}}" id="observation-count-{{id}}">loading...</a>
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
                    <h1>
                        Submission
                        <span class="badge-tier-container">
                            <span class="badge badge-tier">Tier {{observationTemplate.tier}}</span>
                        </span>
                    </h1>


                    <table id="submission-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Description</th>
                            <td>{{observationTemplate.description}}</td>
                        </tr>
                        <tr>
                            <th width="175">Submission Date</th>
                            <td>{{submissionDate}}</td>
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

            <table id="submission-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Observation Summary</th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="submission-observations-loading">
                    <td>
                        <h3>Loading observations...</h3>
                        <div class="progress progress-striped active">
                            <div class="bar" style="width: 100%;"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="more-observations-message"></div>

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
                    <h1>Observation <small>(Tier {{submission.observationTemplate.tier}})</small></h1>
                    <blockquote>
                        <p id="observation-summary"></p>
                    </blockquote>

                    <table id="observed-subjects-grid" class="table table-bordered table-striped subjects">
                        <thead>
                        <tr>
                            <th width="60">&nbsp;&nbsp;&nbsp;&nbsp;</th>
                            <th>Name</th>
                            <th>Type</th>
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
                    <a href="#/center/{{submission.observationTemplate.submissionCenter.id}}"><img src="img/{{submission.observationTemplate.submissionCenter.displayName}}.png" class="img-polaroid" height=30 alt="{{submission.observationTemplate.submissionCenter.displayName}}"></a>
                    <br><br>
                    <img src="img/observation.png" alt="Observation" class="img-polaroid" width=150 height=150><br>
                </div>
            </div>


            <h3>Submission <small>(<a href="#" id="small-show-sub-details">show details</a><a href="#" id="small-hide-sub-details" class="hide">hide details</a>)</small></h3>
            <div id="obs-submission-details" class="hide">
                <table id="obs-submission-details-grid" class="table table-bordered table-striped">
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


                </table>
            </div>


            <h3>Evidence</h3>
            <table id="observed-evidences-grid" class="table table-bordered table-striped evidences">
                <thead>
                <tr>
                    <th>&nbsp;&nbsp;</th>
                    <th>Role</th>
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="<%=dataURL%>{{evidence.filePath}}" target="_blank" title="Download file ({{evidence.mimeType}})" class="desc-tooltip" title="Download File">
                    download file
                </a>
            )</td>
        </tr>
    </script>

    <script type="text/template" id="observedhtmlfileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><div class="labelevidence expandable">{{displayName}}</div></td>
        </tr>
    </script>

    <script type="text/template" id="observedurlevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                (<a href="{{evidence.url.replace(/^\//, '')}}" target="_blank" class="desc-tooltip" title="Open link in a new window">
                    open link
                </a>)
            </td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><span class="numeric-value">{{evidence.numericValue}}</span> <em>{{evidence.unit}}</em></td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-val-tmpl">
        {{firstPart}} &times; 10<sup>{{secondPart}}</sup>
    </script>

    <script type="text/template" id="gene-tmpl">
         <div class="container common-container" id="gene-container">
             <h1>{{displayName}}</h1>

             <div class="row">
                 <div class="span9">
                     <table id="gene-details-grid" class="table table-bordered table-striped">
                         <tr>
                             <th>Gene symbol</th>
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
                             <th>References</th>
                             <td>
                                 Entrez: <a href="http://www.ncbi.nlm.nih.gov/gene/{{entrezGeneId}}" target="_blank">{{entrezGeneId}} <i class="icon-share"></i></a> <br>
                             </td>
                         </tr>
                         <tr>
                             <th>Genomic alterations</th>
                             <td>
                                 <a class="btn btn-small" href="http://cbio.mskcc.org/ctd2-dashboard-portal/cross_cancer.do?tab_index=tab_visualize&clinical_param_selection=null&cancer_study_id=all&genetic_profile_ids_PROFILE_MUTATION_EXTENDED=gbm_tcga_mutations&genetic_profile_ids_PROFILE_COPY_NUMBER_ALTERATION=gbm_tcga_gistic&Z_SCORE_THRESHOLD=2.0&RPPA_SCORE_THRESHOLD=1.0&case_set_id=gbm_tcga_cnaseq&case_ids=&gene_list={{displayName}}&gene_set_choice=user-defined-list&Action=Submit" target="blank">view in cBioPortal <i class="icon-share"></i></a>
                             </td>
                         </tr>
                     </table>
                 </div>
                 <div class="span3">
                     <h4>Gene</h4>
                     <img src="img/gene.png" class="img-polaroid" width=175 height=175 alt="Gene">
                 </div>
             </div>

             <h3>Related observations</h3>
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

             <div class="more-observations-message"></div>

         </div>
    </script>

    <script type="text/template" id="protein-tmpl">
        <div class="container common-container" id="protein-container">
            <h1>{{displayName}}</h1>

            <div class="row">
                <div class="span9">
                    <table id="protein-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Gene symbol</th>
                            <td>{{displayName}}</td>
                        </tr>
                        <tr>
                            <th>Synonyms</th>
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

            <h3>Related observations</h3>
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

            <div class="more-observations-message"></div>

        </div>
    </script>

    <script type="text/template" id="shrna-tmpl">
        <div class="container common-container" id="shrna-container">
            <h1>{{displayName}}</h1>

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

            <h3>Related observations</h3>
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

            <div class="more-observations-message"></div>

        </div>
    </script>

    <script type="text/template" id="transcript-tmpl">
        <div class="container common-container" id="transcript-container">
            <h1>{{refseqId}}</h1>

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

            <h3>Related observations</h3>
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

            <div class="more-observations-message"></div>

        </div>
    </script>

    <script type="text/template" id="tissuesample-tmpl">
        <div class="container common-container" id="tissuesample-container">
            <h1>{{displayName}}</h1>

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

            <h3>Related observations</h3>
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

            <div class="more-observations-message"></div>

        </div>
    </script>

    <script type="text/template" id="cellsample-tmpl">
        <div class="container common-container" id="cellsample-container">
            <h1>{{displayName}}</h1>
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
                        <tr>
                            <th>Genomic alterations</th>
                            <td>
                                <a class="btn btn-small" href="http://cbio.mskcc.org/ctd2-dashboard-portal/tumormap.do?case_id={{cbioPortalId}}&cancer_study_id=ccle_broad" target="blank">view in cBioPortal <i class="icon-share"></i></a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                        <h4>Cell Sample</h4>
                        <img src="img/cellsample.png" class="img-polaroid" width=175 height=175 alt="Cell sample">
                </div>
            </div>
            <h3>Related observations</h3>
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

            <div class="more-observations-message"></div>

        </div>
    </script>

    <script type="text/template" id="animalmodel-tmpl">
        <div class="container common-container" id="animalmodel-container">
            <h1>{{displayName}}</h1>
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
            <h3>Related observations</h3>
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

            <div class="more-observations-message"></div>

        </div>
    </script>

    <script type="text/template" id="compound-tmpl">
          <div class="container common-container" id="compound-container">
              <h1>{{displayName}}</h1>

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
                              <td>{{smilesNotation}}</td>
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

              <h3>Related observations</h3>
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

              <div class="more-observations-message"></div>

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
            <td id="search-image-{{id}}"></td>
            <td><a href="#subject/{{id}}">{{displayName}}</a></td>
            <td>
                <ul id="synonyms-{{id}}">
                    <!-- here will go the synonyms -->
                </ul>
            </td>
            <td>{{type}}</td>
            <td>{{organism.displayName}}</td>
            <td><a href="#subject/{{id}}" id="subject-observation-count-{{id}}">loading...</a></td>
        </tr>
    </script>

    <script type="text/template" id="search-tmpl">
        <div class="container common-container" id="search-results-container">
            <h1>Search <small>for "{{term}}"</small></h1>

            <table id="search-results-grid" class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>&nbsp; &nbsp;</th>
                    <th>Name</th>
                    <th>Synonyms</th>
                    <th>Type</th>
                    <th>Organism</th>
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
            <td><a href="#submission/{{id}}"><img src="img/submission.png" width="50" alt="Submission" title="Submission"></a></td>
            <td><a href="#submission/{{id}}">{{submissionDate}}</a></td>
            <td>{{observationTemplate.description}}</td>
            <td><a href="#submission/{{id}}"><img src="img/{{observationTemplate.submissionCenter.displayName}}.png" title="{{observationTemplate.submissionCenter.displayName}}" alt="{{observationTemplate.submissionCenter.displayName}}" height="50"></a></td>
            <td><span class="badge tier-badge">Tier {{observationTemplate.tier}}</span></td>
            <td width=150><a href="#submission/{{id}}" id="search-observation-count-{{id}}"></a></td>
        </tr>
    </script>

    <script type="text/template" id="synonym-item-tmpl">
        <li class="synonym"><small>{{displayName}}</small></li>
    </script>

    <script type="text/template" id="transcript-item-tmpl">
        <li class="synonym"><a href="#subject/{{id}}">{{refseqId}}</a></li>
    </script>


    <script type="text/template" id="count-observations-tmpl">
        {{count}} observation{{count > 1? "s" : ""}}
    </script>

    <script type="text/template" id="count-submission-tmpl">
        {{count}} submission{{count > 1? "s" : ""}}
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
                    (<small>
                        <a target="_blank" href="<%=dataURL%>" id="file-link-{{id}}">
                            view full story</a>
                    </small>
                    |
                    <small><a href="#observation/{{id}}">see observation</a></small>)
                </p>
            </div>
            <div class="span4">
                <img class="img-circle" src="img/slogos/{{submission.observationTemplate.submissionCenter.displayName}}.png" alt="{{submission.observationTemplate.submissionCenter.displayName}}" title="{{submission.observationTemplate.submissionCenter.displayName}}" height=150>
            </div>
        </div>
    </script>

    <script type="text/template" id="browse-tmpl">
        <div class="container common-container" id="browse-container">
            <h1>Browse {{type}}s</h1>

            <div class="alert alert-block">
                <a href="#" class="close" data-dismiss="alert">&times;</a>
                <p>
                    Below is a list of {{type}}s that have at least one observation associated with it.
                    The number in the parentheses show how many observations there are for the corresponding {{type}}.
                </p>
            </div>

            <div class="pagination browse-pagination" id="browse-pagination">
                <ul class="nav">
                </ul>
            </div>


            <h3 class="loading">Loading...</h3>

            <div class="alert alert-error alert-block hide" id="noitems-to-browse">
                No {{type}}s were found starting with <strong>{{character.toUpperCase()}}</strong>
            </div>

            <ul id="browsed-items-list">

            </ul>
        </div>
    </script>

    <script type="text/template" id="browse-pagination-template">
        <li class="{{className}}">
            <a href="#browse/{{type}}/{{character}}" id="character-link-{{character}}" class="character-link">{{character.toUpperCase()}}</a>
        </li>
    </script>

    <script type="text/template" id="browsed-item-tmpl">
        <li class="browsed-item span3">
            <a href="#subject/{{id}}">{{displayName}}</a>
            (<small><i><span id="browsed-item-count-{{id}}">loading...</span></i></small>)
        </li>
    </script>
    
    <script type="text/template" id="observedmrafileevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}" alt="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <div class="dropdown">
                    ( <a class="dropdown-toggle" data-toggle="dropdown" href="#">view mra file<b class="caret"></b></a> )
                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            <a href="#/evidence/{{id}}" title="open in mra view" class="desc-tooltip">
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
                    <h3>Master Regulator View</h2>
                   
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
			          <small><font color="grey">Throttle: </font></small>
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
        <div id="cytoscape-mra">
            <img id="mra_progress_indicator" class="centeredImage" src="img/progress_indicator.gif" width="30" height="30" alt="Please wait ......"><br>
        </div>
        <div class="well mra-legend">       
            <svg width="350" height="30"xmlns="http://www.w3.org/2000/svg">
            <circle cx="20" cy="15" r="10" fill="white" stroke="grey" stroke-width="2"/>
            <text x="40" y="20" fill="grey">TF</text>
            <rect x="100" y="5" width="18" height="18" fill="white" stroke="grey" stroke-width="2"/>
            <text x="130" y="20" fill="grey">K</text>
            <polygon  points="191,5,180,16,191,27,202,16" fill="white" stroke="grey" stroke-width="2"/>
            <text x="212" y="20" fill="grey">P</text>
            <polygon  points="270,7 260,25,280,25" fill="white" stroke="grey" stroke-width="2"/>
            <text x="290" y="20" fill="grey">none</text>
            </svg>
            <br/>
            {{description}}
        </div>
    </script>

    <script type="text/template" id="template-helper-tmpl">
        <div class="container common-container" id="template-helper-container">
            <h1>Submission Template Helper</h1>

            <div class="alert alert-warning alert-block">
                <a href="#" class="close" data-dismiss="alert">&times;</a>
                <p>
                    <strong>Welcome to the submission template helper!</strong><br>
                    This tool will help you create a basic Dashboard submission template from scratch.
                    Once a basic template is prepared, you will be able to download the template for your local use and preparation of a Dashboard submission.
                </p>
            </div>

            <div id="step1">
                <h3>Step 1: Select submission center</h3>
                <table class="table">
                    <tr>
                        <th>Select a center</th>
                        <td>
                            <select id="template-submission-centers" class="input-xxlarge">
                                <option value="">-</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ... or enter a new one:
                        </th>
                        <td>
                            <input id="template-submission-centers-custom" placeholder="e.g. National Cancer Institute" class="input-xxlarge">
                        </td>
                        </tr>
                    <tr>
                        <td colspan=2 class="next-cell">
                            <button id="apply-submission-center" class="btn">Next</button>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="step2" class="hide">
                <h3>Step 2: Enter a template name</h3>
                <table  class="table">
                    <tr>
                        <td>
                            <input id="template-name" placeholder="e.g. centername_your_description" class="input-xxlarge">
                            <button id="apply-template-name" class="btn">Next</button>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="step3" class="hide">
                <h3>Step 3: Enter a template/submission description</h3>
                <table  class="table">
                    <tr>
                        <th>
                            Template description
                        </th>
                        <td>
                            <input id="template-desc" placeholder="e.g. Analysis of differentially expressed transcripts in some condition" class="input-xxxlarge">
                        </td>
                    </tr>
                    <tr>
                        <th>Submission description</th>
                        <td>
                            <input id="template-submission-desc" placeholder="e.g. Down-regulated genes in PTEN-null cell lines" class="input-xxxlarge">
                        </td>
                    </tr>
                    <tr>
                        <td colspan=2 class="next-cell">
                            <button id="apply-template-desc" class="btn">Next</button>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="step4" class="hide">
                <h3>Step 4: Select a tier</h3>
                <table  class="table">
                    <tr>
                        <td>
                            <select id="template-tier">
                                <option selected="selected" value="1">Tier 1</option>
                                <option value="2">Tier 2</option>
                                <option value="3">Tier 3</option>
                                <option value="4">Tier 4</option>
                            </select>
                            <button id="apply-template-tier" class="btn">Next</button>
                        </td>
                    </tr>
                </table>
            </div>

            <div id="step5" class="hide">
                <h3>Step 5: Add subjects/evidence</h3>
                <table class="table">
                    <tr>
                        <td>
                            <button class="btn" id="add-subject">Subject <i class="icon-plus"></i></button>
                            <button class="btn" id="add-evidence">Evidence <i class="icon-plus"></i></button>
                        </td>
                    </tr>
                </table>

                <hr>
                <h3>Step 6: Create an observation summary</h3>
                <table class="table">
                    <tr>
                        <th>Observation summary</th>
                        <td>
                            <input id="template-obs-summary" placeholder="e.g. <gene_column> is down-regulated in <label_evidence> cells" class="input-xxxlarge">
                        </td>
                    </tr>
                </table>
            </div>

            <div class="modal hide fade" id="subject-modal">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3>Add a subject</h3>
                </div>
                <div class="modal-body">
                    <div id="subject-step1">
                        <h4>Step #1: Subject type</h4>
                        <table class="table">
                            <tr>
                                <th>Select a type</th>
                                <td>
                                    <select id="subject-type">
                                        <option value="">-</option>
                                        <option value="Animal Model">Animal Model</option>
                                        <option value="CellSample">Cell Sample</option>
                                        <option value="Compound">Compound</option>
                                        <option value="Gene">Gene</option>
                                        <option value="ShRna">shRNA</option>
                                        <option value="TissueSample">Tissue Sample</option>
                                    </select>
                                    <button id="apply-subject-type" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="subject-step2" class="hide">
                        <h4>Step #2: Subject column name</h4>
                        <table class="table">
                            <tr>
                                <th>Select a column name</th>
                                <td>
                                    <select id="subject-cname">
                                        <option value="">-</option>
                                        <option value='cell_line_1'>cell_line_1</option>
                                        <option value='cell_line_2'>cell_line_2</option>
                                        <option value='cell_lineage'>cell_lineage</option>
                                        <option value='column_name'>column_name</option>
                                        <option value='compound_name'>compound_name</option>
                                        <option value='disease_condition'>disease_condition</option>
                                        <option value='disease_condition_1'>disease_condition_1</option>
                                        <option value='disease_condition_2'>disease_condition_2</option>
                                        <option value='drug_candidate'>drug_candidate</option>
                                        <option value='entrez_gene_id'>entrez_gene_id</option>
                                        <option value='gene_symbol'>gene_symbol</option>
                                        <option value='gene_symbol_1'>gene_symbol_1</option>
                                        <option value='gene_symbol_2'>gene_symbol_2</option>
                                        <option value='gene_symbol_3'>gene_symbol_3</option>
                                        <option value='gene_symbol_4'>gene_symbol_4</option>
                                        <option value='gene_symbol_5'>gene_symbol_5</option>
                                        <option value='shRNA_id'>shRNA_id</option>
                                        <option value='target_group'>target_group</option>
                                        <option value='tissue_sample'>tissue_sample</option>
                                        <option value='tissue_sample_1'>tissue_sample_1</option>
                                        <option value='tissue_sample_2'>tissue_sample_2</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    or enter new one:
                                </th>
                                <td>
                                    <input id="subject-cname-custom" placeholder="e.g. gene_symbol" class="input-large">
                                </td>
                            </tr>
                            <tr>
                                <td colspan=2 class="next-cell">
                                    <button id="apply-subject-cname" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="subject-step3" class="hide">
                        <h4>Step #3: Select a role</h4>
                        <table class="table">
                            <tr>
                                <th>Select a role </th>
                                <td>
                                    <select id="subject-role">
                                        <option value="">-</option>
                                        <option value='drug candidate'>drug candidate</option>
                                        <option value='enriched feature'>enriched feature</option>
                                        <option value='enriched regulon'>enriched regulon</option>
                                        <option value='histology type'>histology type</option>
                                        <option value='master regulator'>master regulator</option>
                                        <option value='modulator'>modulator</option>
                                        <option value='oncogene'>oncogene</option>
                                        <option value='perturbagen'>perturbagen</option>
                                        <option value='primary site'>primary site</option>
                                        <option value='regulator'>regulator</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    ... or enter new one:
                                </th>
                                <td>
                                    <input id="subject-role-custom" placeholder="e.g. perturbagen" class="input-large">
                                </td>
                            </tr>
                            <tr>
                                <td colspan=2 class="next-cell">
                                    <button id="apply-subject-role" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="subject-step4" class="hide">
                        <h4>Step #4: Enter description</h4>
                        <table class="table">
                            <tr>
                                <td>
                                    <input id="subject-desc" placeholder="e.g. mutated gene" class="input-xlarge">
                                    <button id="apply-subject-desc" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" data-dismiss="modal">Close</button>
                </div>
            </div>

            <div class="modal hide fade" id="evidence-modal">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3>Add an evidence</h3>
                </div>
                <div class="modal-body">
                    <div id="evidence-step1">
                        <h4>Step #1: Evidence type</h4>
                        <table class="table">
                            <tr>
                                <td>
                                    <select id="evidence-type">
                                        <option value="">Select a type</option>
                                        <option value="File">File</option>
                                        <option value="Label">Label</option>
                                        <option value="Label">Numeric</option>
                                        <option value="URL">URL</option>
                                    </select>
                                    <button id="apply-evidence-type" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="evidence-step1-mime" class="hide">
                        <h4>Step #1 cont.: File (MIME) type</h4>
                        <table class="table">
                            <tr>
                                <th>Select a file type</th>
                                <td>
                                    <select id="evidence-mime-type">
                                        <option value="">-</option>
                                        <option value="application/pdf">PDF (application/pdf)</option>
                                        <option value="image/png">PNG (image/png)</option>
                                        <option value="text/gct">GCT (text/gct)</option>
                                        <option value="text/sif">SIF (text/sif)</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th>... or enter new one</th>
                                <td>
                                    <input id="evidence-mime-type-custom" placeholder="e.g. image/gif" class="input-large">
                                </td>
                            </tr>
                            <tr>
                                <td colspan=2 class="next-cell">
                                    <button id="apply-evidence-mime-type" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="evidence-step1-unit" class="hide">
                        <h4>Step #1 cont.: Numeric unit</h4>
                        <table class="table">
                            <tr>
                                <th>Numeric unit (optional)</th>
                                <td>
                                    <input id="evidence-numeric-unit" placeholder="e.g. pL" class="input-large">
                                    <button id="apply-evidence-numeric-unit" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="evidence-step2" class="hide">
                        <h4>Step #2: Subject column name</h4>
                        <table class="table">
                            <tr>
                                <th>Select a column name</th>
                                <td>
                                    <select id="evidence-cname">
                                        <option value="">-</option>
                                        <option value='additional_evidence'>additional_evidence</option>
                                        <option value='c_score'>c_score</option>
                                        <option value='cell_line_exclusion'>cell_line_exclusion</option>
                                        <option value='cell_line_subset'>cell_line_subset</option>
                                        <option value='cell_type'>cell_type</option>
                                        <option value='column_name'>column_name</option>
                                        <option value='drug_candidate_class'>drug_candidate_class</option>
                                        <option value='enrichment_direction'>enrichment_direction</option>
                                        <option value='fdr'>fdr</option>
                                        <option value='feature_data_set'>feature_data_set</option>
                                        <option value='feature_image_path'>feature_image_path</option>
                                        <option value='figure_1'>figure_1</option>
                                        <option value='figure_2'>figure_2</option>
                                        <option value='figure_3'>figure_3</option>
                                        <option value='figure_4'>figure_4</option>
                                        <option value='figure_5'>figure_5</option>
                                        <option value='figure_6'>figure_6</option>
                                        <option value='func_type'>func_type</option>
                                        <option value='gct_path'>gct_path</option>
                                        <option value='gene_scoring'>gene_scoring</option>
                                        <option value='log_fdr'>log_fdr</option>
                                        <option value='mr_gsea_es'>mr_gsea_es</option>
                                        <option value='mr_gsea_fdr'>mr_gsea_fdr</option>
                                        <option value='mr_gsea_p_value'>mr_gsea_p_value</option>
                                        <option value='mr_regulon_set_size'>mr_regulon_set_size</option>
                                        <option value='mra_fet_p_value'>mra_fet_p_value</option>
                                        <option value='mra_overlap_rank'>mra_overlap_rank</option>
                                        <option value='mra_regulon_signature_overlap'>mra_regulon_signature_overlap</option>
                                        <option value='nci_portal'>nci_portal</option>
                                        <option value='network_1'>network_1</option>
                                        <option value='num_shRNAs'>num_shRNAs</option>
                                        <option value='number_of_cell_lines'>number_of_cell_lines</option>
                                        <option value='number_of_cell_lines_in_target_group'>number_of_cell_lines_in_target_group</option>
                                        <option value='number_of_example_cell_lines'>number_of_example_cell_lines</option>
                                        <option value='number_of_mutant_cell_lines'>number_of_mutant_cell_lines</option>
                                        <option value='p_value'>p_value</option>
                                        <option value='probeset_id'>probeset_id</option>
                                        <option value='publication_reference'>publication_reference</option>
                                        <option value='publication_url'>publication_url</option>
                                        <option value='response_image_path'>response_image_path</option>
                                        <option value='shrna_diff_rep_fdr_combined'>shrna_diff_rep_fdr_combined</option>
                                        <option value='shrna_diff_rep_net_direction_combined'>shrna_diff_rep_net_direction_combined</option>
                                        <option value='shrna_diff_rep_p_value_combined'>shrna_diff_rep_p_value_combined</option>
                                        <option value='shrna_diff_rep_z_score_combined'>shrna_diff_rep_z_score_combined</option>
                                        <option value='solution_name'>solution_name</option>
                                        <option value='story_location'>story_location</option>
                                        <option value='target_group'>target_group</option>
                                        <option value='tier1_evidence'>tier1_evidence</option>
                                        <option value='tissue'>tissue</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th>... or enter new one</th>
                                <td>
                                    <input id="evidence-cname-custom" placeholder="e.g. feature_image_path" class="input-xlarge">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" class="next-cell">
                                    <button id="apply-evidence-cname" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="evidence-step3" class="hide">
                        <h4>Step #3: Select a role</h4>
                        <table class="table">
                            <tr>
                                <th>Select a role</th>
                                <td>
                                    <select id="evidence-role">
                                        <option value="">-</option>
                                        <option value='computed'>computed</option>
                                        <option value='context'>context</option>
                                        <option value='literature'>literature</option>
                                        <option value='modulator'>modulator</option>
                                        <option value='oncogene'>oncogene</option>
                                        <option value='perturbagen'>perturbagen</option>
                                        <option value='primary site'>primary site</option>
                                        <option value='regulator'>regulator</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th>... or enter new one</th>
                                <td>
                                    <input id="evidence-role-custom" placeholder="e.g. enriched feature" class="input-xlarge">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" class="next-cell">
                                    <button id="apply-evidence-role" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div id="evidence-step4" class="hide">
                        <h4>Step #4: Enter description</h4>
                        <table class="table">
                            <tr>
                                <td>
                                    <input id="evidence-desc" placeholder="e.g. heatmap image" class="input-xlarge">
                                    <button id="apply-evidence-desc" class="btn">Next</button>
                                </td>
                            </tr>
                        </table>
                    </div>

                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" data-dismiss="modal">Close</button>
                </div>
            </div>

            <div class="row hide" id="template-preview">
                <hr>
                <div class="template-preview-wrapper span12">
                    <h2>Template Preview</h2>
                    <table class="table table-bordered table-striped" id="template-table">
                        <tr id="template-header">
                            <td><!--intentionally left blank--></td>
                        </tr>
                        <tr id="template-subject">
                            <th>subject</th>
                        </tr>
                        <tr id="template-evidence">
                            <th>evidence</th>
                        </tr>
                        <tr id="template-role">
                            <th>role</th>
                        </tr>
                        <tr id="template-mime_type">
                            <th>mime_type</th>
                        </tr>
                        <tr id="template-numeric_units">
                            <th>numeric_units</th>
                        </tr>
                        <tr id="template-display_text">
                            <th>display_text</th>
                        </tr>
                        <tr id="template-sample-data1" class="sample-data">
                            <td><i>sample data row #1</i></td>
                        </tr>
                        <tr id="template-sample-data2" class="sample-data">
                            <td><i>sample data row #2</i></td>
                        </tr>
                    </table>
                </div>

                <div class="template-preview-wrapper span12">
                    <h2>Template Meta-Data Preview</h2>
                    <table class="table table-bordered table-striped" id="template-meta-table">
                        <tr>
                            <th>observation_tier</th>
                            <th>template_name</th>
                            <th>observation_summary</th>
                            <th>template_description</th>
                            <th>submission_name</th>
                            <th>submission_description</th>
                        </tr>
                        <tr>
                            <td id="meta-observation_tier"></td>
                            <td id="meta-template_name"></td>
                            <td id="meta-observation_summary"></td>
                            <td id="meta-template_description"></td>
                            <td id="meta-submission_name"></td>
                            <td id="meta-submission_description"></td>
                        </tr>
                    </table>
                </div>

                <div class="span8 offset2 template-download">
                    <button class="btn btn-large" id="preview-template">Preview template</button>
                    <div class="span4">
                        <form action="download/template" method="POST" id="download-form">
                            <button class="btn btn-warning btn-large" id="download-template">Download template</button>
                            <input type="hidden" name="template" id="template-input">
                            <input type="hidden" name="metatemplate" id="template-meta-input">
                            <input type="hidden" name="filename" id="filename-input">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </script>

    <script type="text/template" id="template-helper-center-tmpl">
        <option value="{{displayName}}">{{displayName}}</option>
    </script>

    <script type="text/template" id="template-header-col-tmpl">
        <td class="{{id}}" data-type="{{columnType}}"></td>
    </script>

    <script type="text/template" id="template-sample-data-tmpl">
        <input placeholder="enter data" class="sample-data-input">
    </script>

    <script type="text/template" id="preview-tmpl">
        <div id="preview-container" class="container">
            <h1>Template preview</h1>
            <ul class="nav nav-tabs" id="preview-tabs">
                <li class="active"><a href="#submission-preview">Submission</a></li>
                <li><a href="#obs1-preview">Observation</a></li>
            </ul>

            <div class="tab-content">
                <div class="tab-pane active" id="submission-preview">
                    <h1>Here will come the submission</h1>
                </div>
                <div class="tab-pane" id="obs1-preview">
                    <h1>Here will come the observation #1</h1>
                </div>
            </div>
        </div>
    </script>

    <script type="text/template" id="more-observations-tmpl">
        <div class="alert alert-warning">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p>
                Only {{numOfObservations}} of {{numOfAllObservations}} observations are listed in the table above.
                To load all observations please <a href="#" class="load-more-observations">click here</a>
                (<i>this might take a while</i>).
            </p>
        </div>
    </script>

    <script type="text/template" id="ncithesaurus-tmpl">
        <li>
            <a href="http://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI%20Thesaurus&code={{nciId}}" target="_blank">
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

    <!-- end of templates -->

    <script src="js/jquery.min.js"></script>
    <script src="js/jquery.dataTables.min.js"></script>
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
    <script src="js/flippant.js"></script>
    <script src="js/ctd2.js"></script>
  </body>
</html>
