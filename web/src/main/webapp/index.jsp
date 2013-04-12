<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
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
                <li><a href="#centers">Centers</a></li>
                <li class="dropdown">
                      <a href="#" class="dropdown-toggle" data-toggle="dropdown">Resources <b class="caret"></b></a>
                      <ul class="dropdown-menu">
                          <li><a target="_blank" href="http://ctd2.nci.nih.gov/index.html">CTD<sup>2</sup> Home page</a></li>
                          <li><a target="_blank" href="http://ctd2.nci.nih.gov/publication.html">Publications</a></li>
                          <li><a target="_blank" href="http://ctd2.nci.nih.gov/DataMatrix/CTD2_DataMatrix.html">Data Matrix</a></li>
                          <li><a target="_blank" href="http://ocg.cancer.gov/resources/fnd.asp">Funding Opportunities</a></li>
                      </ul>
                  </li>
              </ul>
              <ul class="nav pull-right">
                  <form class="form-search" id="omnisearch">
                      <div class="input-append">
                          <input type="text" id="omni-input" class="span3 search-query" title="Search" placeholder="e.g. BRAF or ABT-137">
                          <button type="submit" class="btn search-button">Search</button>
                          <span class="hide" id="search-help-content">
                              <p>Please enter the name of the subject you would like to search in the database.</p>

                              <strong>Examples:</strong>
                              <ul>
                                <li><em>Gene: </em> <a href="#search/exact/CTNNB1">CTNNB1</a></li>
                                <li><em>Compound: </em> <a href="#search/exact/ABT-737">ABT-737</a></li>
                                <li><em>Cell Sample: </em> <a href="#search/exact/HPBALL">HPBALL</a></li>
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

    <div class="container">
        <hr class="featurette-divider">

        <!-- FOOTER -->
       <footer>
     	<p>
             CTD<sup>2</sup> Dashboard &middot;
             <a href="#">Contact</a>
         </p>
         <p class="pull-right">
             <a href=""><img src="img/logos/footer_logo_nci.jpg"></a><a href=""><img src="img/logos/footer_logo_hhs.jpg"></a><a href=""><img src="img/logos/footer_logo_nih.jpg"></a><a href=""><img src="img/logos/footer_logo_firstgov.jpg"></a>
         </p>
       </footer>
    </div>

    <!-- these are the templates -->
    <script type="text/template" id="home-tmpl">

        <!-- Carousel
        ================================================== -->
        <div id="myCarousel" class="carousel slide">
          <div class="carousel-inner">
            <div class="item active">
              <img data-src="holder.js/1500x450/#a70001:#a70001" alt="" class="cimg">
              <div class="container">
                  <div class="carousel-caption">
                        <h1>Stories</h1>
                        <div class="well carousel-well">
                            <div class="container">
                            <div class="row">
                                <div class="span8">
                                    <p class="lead stories-lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam.
                                    </p>
                                    <p class="stories-text">
                                        Donec id elit <a href="#" class="target-link" title="Information about target">sample target</a> gravida at eget metus.
                                        Nullam id dolor id nibh ultricies vehicula ut id elit.
                                        Cras mattis <a href="#" class="drug-link" title="Information about drug">sample drug</a> purus sit amet fermentum.
                                        Duis mollis, est non commodo luctus,
                                        nisi erat porttitor ligula, eget lacinia odio sem nec elit.
                                        Fusce dapibus,
                                        tellus ac cursus <a href="#" class="genomics-link" title="Information about alteration">sample alteration</a>,
                                        tortor mauris condimentum nibh,
                                        ut fermentum massa justo sit amet risus... (<a href="#">read more</a>)
                                    </p>
                                </div>
                                <div class="span4">
                                    <img class="img-circle" data-src="holder.js/180x180/text:portrait or logo">
                                </div>
                            </div>
                            </div>
                            <br/>
                            <div class="pagination pagination-centered stories-pagination">
                                <ul>
                                    <li class="active"><a href="#">1</a></li>
                                    <li><a href="#">2</a></li>
                                    <li><a href="#">3</a></li>
                                    <li><a href="#">4</a></li>
                                    <li><a href="#">5</a></li>
                                    <li><a href="#">More stories →</a></li>
                                </ul>
                            </div>
                        </div>
                  </div>
              </div>
            </div>
            <div class="item">
              <img data-src="holder.js/1500x450/#1ea44b:#1ea44b" class="cimg" alt="">
              <div class="container">
                <div class="carousel-caption">
                  <h1>Targets</h1>
                    <div class="well carousel-well">
                        <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam.
                            Donec id elit non mi porta gravida at eget metus.
                            Nullam id dolor id nibh ultricies vehicula ut id elit.
                            Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit.

                        </p>
                            <form class="form-search">
                                <input type="text" class="input-medium search-query" id="target-search" placeholder="e.g. BRAF">
                                <a class="btn btn-small btn-success" href="#">Search Targets</a>
                                or <a class="btn btn-small" href="#"><i class="icon-th-list"></i> Browse all targets</a>
                            </form>
                    </div>
                </div>
              </div>
            </div>
            <div class="item">
              <img data-src="holder.js/1500x450/#f99910:#f99910" alt="" class="cimg">
              <div class="container">
                <div class="carousel-caption">
                  <h1>Compounds</h1>
                    <div class="well carousel-well">
                        <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam.
                            Donec id elit non mi porta gravida at eget metus.
                            Nullam id dolor id nibh ultricies vehicula ut id elit.
                            Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit.

                        </p>
                        <form class="form-search">
                            <input type="text" class="input-medium search-query" id="drug-search" placeholder="e.g. Vemurafenib">
                            <a class="btn btn-small btn-warning" href="#">Search Compounds</a>
                            or <a class="btn btn-small" href="#"><i class="icon-th-list"></i> Browse all compounds</a>
                        </form>
                    </div>
                </div>
              </div>
            </div>
            <div class="item">
                  <img data-src="holder.js/1500x450/#006bb9:#006bb9" alt="" class="cimg">
                  <div class="container">
                      <div class="carousel-caption">
                          <h1>Search</h1>
                          <div class="well carousel-well">
                              <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam.
                                  Donec id elit non mi porta gravida at eget metus.
                                  Nullam id dolor id nibh ultricies vehicula ut id elit.
                                  Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit.

                              </p>
                              <form class="form-search" id="omni-search-form">
                                  <input type="text" class="input-medium search-query" id="omni-search" placeholder="e.g. BRAF or aspirin">
                                  <button class="btn btn-small btn-info" href="#">Search</button>
                              </form>
                          </div>
                      </div>
                  </div>
             </div>

          </div>

         <!-- <a class="left carousel-control" id="prevSlideControl" href="#myCarousel" data-slide="prev">&lsaquo;</a>
          <a class="right carousel-control" id="nextSlideControl" href="#myCarousel" data-slide="next">&rsaquo;</a>
          -->
        </div><!-- /.carousel -->

        <div class="container marketing ctd2-boxes">
          <div class="row">
            <div class="span3 stories" data-order="0">
              <h3>Stories</h3>
              <p>Donec sed odio dui. Etiam porta sem malesuada magna mollis euismod. Nullam id dolor id nibh ultricies vehicula ut id elit. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Praesent commodo cursus magna, vel scelerisque nisl consectetur et.</p>
            </div><!-- /.span3 -->
            <div class="span3 target" data-order="1">
              <h3>Targets</h3>
                <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit. Cras mattis consectetur purus sit amet fermentum. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
            </div><!-- /.span3 -->
            <div class="span3 drug" data-order="2">
              <h3>Compounds</h3>
                <p>Donec sed odio dui. Etiam porta sem malesuada magna mollis euismod. Nullam id dolor id nibh ultricies vehicula ut id elit. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Praesent commodo cursus magna, vel scelerisque nisl consectetur et.</p>
            </div><!-- /.span3 -->
            <div class="span3 genomics" data-order="3">
              <h3>Context</h3>
              <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
            </div><!-- /.span3 -->
          </div><!-- /.row -->


          <!-- START THE FEATURETTES -->

          <hr class="featurette-divider">

          <div class="featurette">
              <img class="featurette-image pull-right" data-src="holder.js/250x250">
              <h2 class="featurette-heading">CTD<sup>2</sup> Dashboard<span class="muted"> Overview</span></h2>
              <p class="lead">
                  The interactive dashboard will ensure the timely and efficient dissemination of <b>CTD<sup>2</sup></b> targets and biomarkers, across the final stages of their validation process, in conjunction with the primary data and methodologies used for their discovery and characterization.
              </p>
          </div>

          <!-- /END THE FEATURETTES -->

        </div><!-- /.container -->
    </script>

    <script type="text/template" id="centers-tmpl">
        <div class="container common-container" id="centers-container">
            <h1>Centers</h1>
            <br>
            <ul class="thumbnails">
                <!-- here will come the centers... -->
            </ul>
        </div>
    </script>

    <script type="text/template" id="centers-tbl-row-tmpl">
        <li class="span4">
            <a href="#center/{{id}}" class="thumbnail">
                <img src="img/{{displayName}}.png" alt="{{displayName}}" class="img-polaroid" height="50"><br>
                <center>
                    {{displayName}} submissions &raquo;
                </center>
            </a>
        </li>
    </script>

    <script type="text/template" id="center-tmpl">
        <div class="container common-container" id="center-submission-container">
            <div class="row">
                <div class="span9">
                    <h1>{{displayName}} <small>submissions</small></h1>
                </div>
                <div class="span3">
                    <img src="img/{{displayName}}.png" class="img-polaroid" width="200">
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
                {{observationTemplate.description}}
            </td>
            <td><span class="badge tier-badge">Tier {{observationTemplate.tier}}<span></td>
            <td><a href="#submission/{{id}}">observations</a></td>
        </tr>
    </script>

    <script type="text/template" id="submission-tmpl">
        <div class="container common-container" id="submission-container">
            <h1>Submission <small>(# {{id}})</small></h1>

            <div class="row">
                <div class="span9">
                    <table id="submission-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>Description</th>
                            <td>{{observationTemplate.description}}</td>
                        </tr>
                        <tr>
                            <th width="175">Submission Date</th>
                            <td>{{submissionDate}}</td>
                        </tr>
                        <tr>
                            <th>Submission Center</th>
                            <td>
                                <a href="#/center/{{submissionCenter.id}}">
                                    <img src="img/{{submissionCenter.displayName}}.png" class="img-polaroid" height=30 alt="{{submissionCenter.displayName}}">
                                </a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <img src="img/submission.png" class="img-polaroid" width=200 height=200><br>
                    <center>
                        <span class="badge">Tier {{observationTemplate.tier}}</span>
                    </center>
                </div>
            </div>

            <div id="optional-submission-description">

            </div>

            <h3>Observations within this submission</h3>
            <table id="submission-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Observation</th>
                    <th>Observation Summary</th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr class="submission-observations-loading">
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

    <script type="text/template" id="submission-tbl-row-tmpl">
        <tr>
            <td width="60">
                <a href="#/observation/{{id}}">
                    # {{id}}
                </a>
            </td>
            <td id="submission-observation-summary-{{id}}">
                Loading...
            </td>
        </tr>
    </script>

    <script type="text/template" id="observation-tmpl">
        <div class="container common-container" id="observation-container">
            <h1>Observation <small>(# {{id}})</small></h1>

            <div class="row">
                <div class="span9">
                    <table id="observation-details-grid" class="table table-bordered table-striped">
                        <tr>
                            <th>
                                Submission<br>
                            </th>
                            <td>
                                <p>{{submission.observationTemplate.description}} (<a href="#/submission/{{submission.id}}">details &raquo;</a>)</p>
                            </td>
                        </tr>
                        <tr>
                            <th>Submission Center</th>
                            <td>
                                <a href="#/center/{{submission.submissionCenter.id}}">
                                    <img src="img/{{submission.submissionCenter.displayName}}.png" class="img-polaroid" height=30 alt="{{submission.submissionCenter.displayName}}">
                                </a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="span3">
                    <img src="img/observation.png" class="img-polaroid" width=200 height=200><br>
                    <center>
                        <span class="badge">Tier {{submission.observationTemplate.tier}}</span>
                    </center>
                </div>
            </div>

            <h3>Observation summary</h3>
            <blockquote>
                <p id="observation-summary"></p>
            </blockquote>


            <h3>Observed subjects</h3>
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
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>(
                <a href="http://cbio.mskcc.org/cancergenomics/ctd2-dashboard/{{evidence.filePath}}" target="_blank" title="Download file ({{evidence.mimeType}})" class="desc-tooltip" title="Download File">
                    download file
                </a>
            )</td>
        </tr>
    </script>

    <script type="text/template" id="observedimageevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                <a href="http://cbio.mskcc.org/cancergenomics/ctd2-dashboard/{{evidence.filePath}}" target="_blank" title="{{observedEvidenceRole.displayText}}" rel="evidence-images" class="evidence-images">
                    <img src="http://cbio.mskcc.org/cancergenomics/ctd2-dashboard/{{evidence.filePath}}" class="img-polaroid img-evidence" height="60">
                </a>
                </td>
        </tr>
    </script>


    <script type="text/template" id="observedlabelevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td><span class="label">{{displayName}}</span></td>
        </tr>
    </script>

    <script type="text/template" id="observedurlevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>
                (<a href="{{evidence.url}}" target="_blank" class="desc-tooltip" title="Open link in a new window">
                    open link
                </a>)
            </td>
        </tr>
    </script>

    <script type="text/template" id="observeddatanumericevidence-row-tmpl">
        <tr>
            <td>
                <img src="img/{{observedEvidenceRole.evidenceRole.displayName}}.png" class="img-rounded" title="{{observedEvidenceRole.evidenceRole.displayName}}">
            </td>
            <td>{{observedEvidenceRole.evidenceRole.displayName}}</td>
            <td>{{observedEvidenceRole.displayText}}</td>
            <td>{{evidence.numericValue}} <em>{{evidence.unit}}</em></td>
        </tr>
    </script>

    <script type="text/template" id="gene-tmpl">
         <div class="container common-container" id="gene-container">
             <h1>{{displayName}} <small>(# {{id}})</small></h1>

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
                                 Entrez:<a href="http://www.ncbi.nlm.nih.gov/gene/{{entrezGeneId}}" target="_blank">{{entrezGeneId}} <i class="icon-share"></i></a> <br>
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
                     <img src="img/gene.png" class="img-polaroid" width=175 height=175>
                 </div>
             </div>

             <h3>Related observations</h3>
             <table id="gene-observation-grid" class="table table-bordered table-striped observations">
                 <thead>
                 <tr>
                     <th>Observation</th>
                     <th width=400>Observation Summary</th>
                     <th>Tier</th>
                     <th>Date</th>
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
            <h1>{{displayName}} <small>(# {{id}})</small></h1>
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
                            <th>Lineage</th>
                            <td>{{lineage}}</td>
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
                        <img src="img/cellsample.png" class="img-polaroid" width=175 height=175>
                </div>
            </div>
            <h3>Related observations</h3>
            <table id="cellsample-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Observation</th>
                    <th>Observation Summary</th>
                    <th>Tier</th>
                    <th>Date</th>
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
              <h1>{{displayName}} <small>(# {{id}})</small></h1>

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
                      <a href="http://cbio.mskcc.org/cancergenomics/ctd2-dashboard/images/compounds/{{imageFile}}" target="_blank">
                        <img class="img-polaroid" width=200 src="http://cbio.mskcc.org/cancergenomics/ctd2-dashboard/images/compounds/{{imageFile}}">
                      </a>
                  </div>
              </div>

              <h3>Related observations</h3>
              <table id="compound-observation-grid" class="table table-bordered table-striped observations">
                  <thead>
                  <tr>
                      <th>Observation</th>
                      <th>Observation Summary</th>
                      <th>Tier</th>
                      <th>Date</th>
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

    <script type="text/template" id="observedsubject-row-tmpl">
        <tr>
            <td>
                <a href="#/observation/{{observation.id}}">
                    # {{observation.id}}
                </a>
            </td>
            <td>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>
                {{observation.submission.observationTemplate.description}}
            </td>
            <td><span class="badge tier-badge">Tier {{observation.submission.observationTemplate.tier}}</span></td>
            <td>
                <a href="#/submission/{{observation.submission.id}}">
                    {{observation.submission.submissionDate}}
                </a>
            </td>
            <td>
                <a href="#/center/{{observation.submission.submissionCenter.id}}">
                    {{observation.submission.submissionCenter.displayName}}
                </a>
            </td>
        </tr>
    </script>

    <script type="text/template" id="observation-row-tmpl">
        <tr>
            <td>
                <a href="#/observation/{{id}}">
                    # {{id}}
                </a>
            </td>
            <td id="observation-summary-{{id}}">
                Loading...
            </td>
            <td><span class="badge tier-badge">Tier {{submission.observationTemplate.tier}}</span></td>
            <td>
                <a href="#/submission/{{submission.id}}">
                    {{submission.submissionDate}}
                </a>
            </td>
            <td>
                <a href="#/center/{{submission.submissionCenter.id}}">
                    {{submission.submissionCenter.displayName}}
                </a>

            </td>
        </tr>
    </script>

    <script type="text/template" id="search-empty-tmpl">
        <tr>
            <td colspan="7">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <strong>No results found!</strong> We could not find any subjects related to your search.
                    Please try again with another keyword.
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="search-results-gene-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/gene.png" class="img-polaroid search-info" title="Gene" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-compund-image-tmpl">
        <a href="#subject/{{id}}">
            <img class="img-polaroid search-info" title="Compound" width=50 height=50 src="http://cbio.mskcc.org/cancergenomics/ctd2-dashboard/images/compounds/{{imageFile}}">
        </a>
    </script>

    <script type="text/template" id="search-results-cellsample-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/cellsample.png" title="Cell sample" class="img-polaroid search-info" height="50" width="50">
        </a>
    </script>

    <script type="text/template" id="search-results-unknown-image-tmpl">
        <a href="#subject/{{id}}">
            <img src="img/unknown.png" title="{{type}}" class="img-polaroid search-info" height="50" width="50">
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
            <td><a href="#subject/{{id}}">observations</a></td>
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
                    <th>Subject Type</th>
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
        </div>
    </script>

    <script type="text/template" id="synonym-item-tmpl">
        <li class="synonym"><small>{{displayName}}</small></li>
    </script>
    <!-- end of templates -->

    <script src="js/targets.js"></script>
    <script src="js/drugs.js"></script>
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
    <script src="js/ctd2.js"></script>
  </body>
</html>
