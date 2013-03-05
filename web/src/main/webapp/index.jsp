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
            <a class="brand" href="#">CTD<sup>2</sup>
            </a>
            <div class="nav-collapse collapse">
              <ul class="nav">
                <li class="active"><a href="#">Dashboard</a></li>
                <li><a href="#centers">Centers</a></li>
                <li class="dropdown">
                      <a href="#" class="dropdown-toggle" data-toggle="dropdown">Resources <b class="caret"></b></a>
                      <ul class="dropdown-menu">
                          <li><a href="#">Bioinformatics Tools</a></li>
                          <li><a href="#">Funding opportunities</a></li>
                      </ul>
                  </li>
                  <li><a href="#pub">Publications</a></li>
                  <li><a href="#dm">Data Matrix</a></li>
              </ul>
              <ul class="nav pull-right">
                  <li><a href="#about">About</a></li>
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
                              <form class="form-search">
                                  <input type="text" class="input-medium search-query" id="alteration-search" placeholder="e.g. BRAF V600E">
                                  <a class="btn btn-small btn-info" href="#">Search</a>
                              </form>
                          </div>
                      </div>
                  </div>
             </div>

          </div>

          <a class="left carousel-control" id="prevSlideControl" href="#myCarousel" data-slide="prev">&lsaquo;</a>
          <a class="right carousel-control" id="nextSlideControl" href="#myCarousel" data-slide="next">&rsaquo;</a>
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
              <h3>Search</h3>
              <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
            </div><!-- /.span3 -->
          </div><!-- /.row -->


          <!-- START THE FEATURETTES -->

          <hr class="featurette-divider">

          <div class="featurette">
              <img class="featurette-image pull-right" data-src="holder.js/250x250">
              <h2 class="featurette-heading">CTD<sup>2</sup> <span class="muted">Overview</span></h2>
              <p class="lead">
                  The purpose of the <b>Cancer Target Discovery and Development (CTD<sup>2</sup>)</b> initiative is to bridge the gap between the enormous volumes of data generated by the comprehensive and multidimensional molecular characterizations of a variety of human cancers and the ability to apply the information to improve patient outcomes. Specifically, CTD<sup>2</sup> aims to extract therapeutic targets and diagnostic, prognostic, and drug response markers from The Cancer Genome Atlas (TCGA) and Therapeutically Applicable Research to Generate Effective Treatments (TARGET), Cancer Genomic Characterization Initiative (CGCI) and other data sets. The CTD<sup>2</sup> centers have established a highly collaborative network to achieve these goals. By sharing validated candidates with the cancer research community, CTD<sup>2</sup> will contribute to understanding the mechanism(s) of cancer and potentially accelerate development of clinically useful markers and therapeutics for evidence-based treatments.
              </p>
          </div>

          <!-- /END THE FEATURETTES -->

        </div><!-- /.container -->
    </script>

    <script type="text/template" id="centers-tmpl">
        <div class="container common-container" id="centers-container">
            <h1>Centers</h1>
            <table id="centers-grid" class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Submissions</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- here will go the rows -->
                </tbody>
            </table>
        </div>
    </script>

    <script type="text/template" id="centers-tbl-row-tmpl">
        <tr>
            <td>{{displayName}}</td>
            <td><a href="#center/{{id}}">Submissions</a></td>
        </tr>
    </script>

    <script type="text/template" id="center-tmpl">
        <div class="container common-container" id="center-submission-container">
            <h1>{{displayName}} <small>submissions</small></h1>

            <table id="center-submission-grid" class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Tier</th>
                        <th>Observations</th>
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
            <td>{{id}}</td>
            <td>{{submissionDate}}</td>
            <td>
                <b>{{observationTemplate.description}}</b>
                <small class="template-description" title="{{observationTemplate.displayName}}">
                    (template)
                </small>
            </td>
            <td>{{observationTemplate.tier}}</td>
            <td><a href="#submission/{{id}}">observations</a></td>
        </tr>
    </script>

    <script type="text/template" id="submission-tmpl">
        <div class="container common-container" id="submission-container">
            <h1>Submission <small>(# {{id}})</small></h1>

            <table id="submission-details-grid" class="table table-bordered table-striped">
                <tr>
                    <th>Center</th>
                    <td>{{submissionCenter.displayName}}</td>
                </tr>
                <tr>
                    <th>Tier</th>
                    <td>{{observationTemplate.tier}}</td>
                </tr>
                <tr>
                    <th>Description</th>
                    <td>{{observationTemplate.description}}</td>
                </tr>
                <tr>
                    <th>Template</th>
                    <td>{{observationTemplate.displayName}}</td>
                </tr>
                <tr>
                    <th>Date</th>
                    <td>{{submissionDate}}</td>
                </tr>
            </table>

            <h1>Observations within this submission</h1>
            <table id="submission-observation-grid" class="table table-bordered table-striped observations">
                <thead>
                <tr>
                    <th>Observation</th>
                    <th>Subject</th>
                    <th>Role</th>
                    <th>Description</th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                </tbody>
            </table>
        </div>
    </script>

    <script type="text/template" id="submission-tbl-row-tmpl">
        <tr>
            <td>
                <a href="#/observation/{{observation.id}}">
                    # {{observation.id}}
                </a>
            </td>
            <td>
                <a href="#/subject/{{subject.id}}">
                    {{subject.displayName}}
                </a>
            </td>
            <td>{{observedSubjectRole.subjectRole.displayName}}</td>
            <td>{{observedSubjectRole.description}}</td>
        </tr>
    </script>

    <script type="text/template" id="gene-tmpl">
         <div class="container common-container" id="gene-container">
             <h1>{{displayName}} <small>(# {{id}})</small></h1>

             <table id="gene-details-grid" class="table table-bordered table-striped">
                 <tr>
                     <th>Name</th>
                     <td>{{displayName}}</td>
                 </tr>
                 <tr>
                     <th>Synonyms</th>
                     <td>{{synonymsStr}}</td>
                 </tr>
                 <tr>
                     <th>Type</th>
                     <td>Gene</td>
                 </tr>
                 <tr>
                     <th>Organism</th>
                     <td>{{organism.displayName}}</td>
                 </tr>
                 <tr>
                     <th>Entrez Gene ID</th>
                     <td>{{entrezGeneId}}</td>
                 </tr>
                 <tr>
                     <th>HGNC ID</th>
                     <td>{{HGNCId}}</td>
                 </tr>
                 <tr>
                     <th>References</th>
                     <td class="xrefsColumn">{{xrefStr}}</td>
                 </tr>
             </table>

             <h1>Related Observations</h1>
             <table id="gene-observation-grid" class="table table-bordered table-striped observations">
                 <thead>
                 <tr>
                     <th>Observation</th>
                     <th>Role</th>
                     <th>Observation Type</th>
                     <th>Tier</th>
                     <th>Date</th>
                     <th>Center</th>
                 </tr>
                 </thead>
                 <tbody>
                 <!-- here will go the rows -->
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
                              <td>{{synonymsStr}}</td>
                          </tr>
                          <tr>
                              <th>Type</th>
                              <td>Compound</td>
                          </tr>
                          <tr>
                              <th>SMILES</th>
                              <td>{{smilesNotation}}</td>
                          </tr>
                          <tr>
                              <th>References</th>
                              <td class="xrefsColumn">{{xrefStr}}</td>
                          </tr>
                      </table>
                  </div>
                  <div class="span3">
                      <h4>Structure</h4>
                      <img class="img-polaroid" data-src="holder.js/150x150">
                  </div>
              </div>

              <h1>Related Observations</h1>
              <table id="compound-observation-grid" class="table table-bordered table-striped observations">
                  <thead>
                  <tr>
                      <th>Observation</th>
                      <th>Role</th>
                      <th>Observation Type</th>
                      <th>Tier</th>
                      <th>Date</th>
                      <th>Center</th>
                  </tr>
                  </thead>
                  <tbody>
                  <!-- here will go the rows -->
                  </tbody>
              </table>
          </div>
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
            <td>{{observation.submission.observationTemplate.tier}}</td>
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

    <script type="text/template" id="search-empty-tmpl">
        <tr>
            <td colspan="5">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <strong>Oh snap!</strong> We could not find any subjects related to your search.
                    Please change a few things up and try submitting again.
                </div>
            </td>
        </tr>
    </script>

    <script type="text/template" id="search-result-row-tmpl">
        <tr>
            <td>{{displayName}}</td>
            <td>{{synonymsStr}}</td>
            <td>{{type}}</td>
            <td>{{organism.displayName}}</td>
            <td><a href="#subject/{{id}}">details</a></td>
        </tr>
    </script>

    <script type="text/template" id="search-tmpl">
        <div class="container common-container" id="search-results-container">
            <h1>Search <small>for "{{term}}"</small></h1>

            <table id="search-results-grid" class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Synonyms</th>
                    <th>Type</th>
                    <th>Organism</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody>
                <!-- here will go the rows -->
                <tr id="loading-row">
                    <td colspan="5">
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
    <script src="js/ctd2.js"></script>
  </body>
</html>
