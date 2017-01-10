$ctd2 = {}; /* the supporting module of ctd2-dashboard app ctd2.js */

// following code used to be in ctd2.js
$ctd2.TemplateHelperView = Backbone.View.extend({
        template: _.template($("#template-helper-tmpl").html()),
        el: $("#main-container"),

        render: function() {
            $(this.el).html(this.template(this.model));

            // top menu
            $("#menu_home").click(function() {
                $("#step1").slideDown();
                $("#step2").fadeOut();
                $("#step3").fadeOut();
                $("#step4").fadeOut();
                $("#step5").fadeOut();
                $("#step6").fadeOut();
            });
            $("#menu_manage").click(function() {
                $("#step1").fadeOut();
                $("#step2").slideDown();
                $("#step3").fadeOut();
                $("#step4").fadeOut();
                $("#step5").fadeOut();
                $("#step6").fadeOut();
            }).hide();
            $("#menu_description").click(function() {
                $("#step1").fadeOut();
                $("#step2").fadeOut();
                $("#step3").slideDown();
                $("#step4").fadeOut();
                $("#step5").fadeOut();
                $("#step6").fadeOut();
            }).hide();
            $("#menu_data").click(function() {
                $("#step1").fadeOut();
                $("#step2").fadeOut();
                $("#step3").fadeOut();
                $("#step4").slideDown();
                $("#step5").fadeOut();
                $("#step6").fadeOut();
            }).hide();
            $("#menu_summary").click(function() {
                $("#step1").fadeOut();
                $("#step2").fadeOut();
                $("#step3").fadeOut();
                $("#step4").fadeOut();
                $("#step5").slideDown();
                $("#step6").fadeOut();
            }).hide();
            $("#menu_preview").click(function() {
                $("#step1").fadeOut();
                $("#step2").fadeOut();
                $("#step3").fadeOut();
                $("#step4").fadeOut();
                $("#step5").fadeOut();
                $("#step6").slideDown();
            }).hide();

            var submissionCenters = new $ctd2.SubmissionCenters();
            submissionCenters.fetch({
                success: function() {
                    _.each(submissionCenters.models, function(aCenter) {
                        (new $ctd2.TemplateHelperCenterView({
                            model: aCenter.toJSON(),
                            el: $("#template-submission-centers")
                        })).render();
                    });
                }
            });

            var self = this;
            $("#apply-submission-center").click(function() {
                var centerId = $("#template-submission-centers").val();
                if(centerId.length == 0) {
                    console.log("centerId is empty");
                    return; // error control
                }

                $("#menu_manage").show();
                $("#step1").fadeOut();
                $("#step2").slideDown();
                $("span#center-name").text($("#template-submission-centers option:selected").text());
                var storedTemplates = new $ctd2.StoredTemplates({centerId: centerId});
                $("#existing-template-table > .stored-template-row").remove();
                storedTemplates.fetch({
                    success: function() {
                        _.each(storedTemplates.models, function(oneTemplate) {
                            var oneTemplateModel = oneTemplate.toJSON();

                            // TODO placeholder for now - subjectClasses should be from database as well
                            var subjectColumnCount = oneTemplateModel.subjectColumns.length;
                            oneTemplateModel.subjectClasses = []; // the length should match other columns, e.g. column tags, subject roles, descriptions.
                            for(var i=0; i<subjectColumnCount; i++) {
                                oneTemplateModel.subjectClasses[i] = Object.keys($ctd2.subjectRoles)[i]; // TODO testing data for now
                            }

                            (new $ctd2.ExistingTemplateView({
                                model: oneTemplateModel,
                                el: $("#existing-template-table")
                            })).render();
                        });
                    }
                });
            });

            $("#create-new-submission").click(function() {
                $("#step2").fadeOut();
                $("#step3").slideDown();
            });

            // although the other button is called #create-new-submission, this is where it is really created back-end
            $("#save-name-description").click(function() {
                $("#save-name-description").attr("disabled", "disabled");
                $ctd2.saveNewTemplate();
            });
            $("#continue-to-main-data").click(function() { // similar to save, additionally moving to the next
                if($ctd2.saveNewTemplate(true)) {
                    $("#step3").fadeOut();
                    $("#step4").slideDown();
                }
            });

            $("#save-template-submission-data").click(function() {
                // TODO save existing template with the updated submission data
                console.log("saving the submission data ...");
                $ctd2.updateTemplate();
            });
            $("#apply-template-submission-data").click(function() {
                //var tmplTier = $("#template-tier").val();
                //self.addMetaColumn("observation_tier", tmplTier);

                $("#step4").fadeOut();
                $("#step5").slideDown();
            });

            if($("#template-table-subject tr").length<=1) {
                (new $ctd2.TemplateSubjectDataRowView({
                    model: {columnTag: 'new column tag', subjectClass: "Compound", subjectRole: "Candidate drug"},
                    el: $("#template-table-subject")
                })).render();
            }
            if($("#template-table-evidence tr").length<=1) {
                (new $ctd2.TemplateEvidenceDataRowView({
                    model: {columnTag: 'new column tag', evidenceType: "background", valueType: "Document"},
                    el: $("#template-table-evidence")
                })).render();
            }

            $("#add-evidence").click(function() {
                (new $ctd2.TemplateEvidenceDataRowView({
                    model: {columnTag: 'new column tag', evidenceType: "background", valueType: "Document"},
                    el: $("#template-table-evidence")
                })).render();
            });

            $("#add-subject").click(function() {
                (new $ctd2.TemplateSubjectDataRowView({
                    model: {columnTag: 'new column tag', subjectClass: "new subject class", subjectRole: "TOBE"},
                    el: $("#template-table-subject")
                })).render();
            });

            $("#add-observation").click(function() {
                var newObvNumber = 1; // TODO a smart serial number
                new $ctd2.OneObservationView({
                    el: $("#template-table"),
                    model: {obvNumber: newObvNumber, obvColumn: "TEMP", obvText: "empty"},
                }).render();
            });

            var helper = function() {
                var input = $( "#template-obs-summary" );
                input.val( input.val() + "<" +$(this).text() + ">" );
            };
            $(".helper-tag").click(helper);

            $("#preview-select").change(function() {
                var selected = $(this).val();
                $(this).children("option").each(function() {
                    var option = $(this).val();
                    var viewId = option.replace("observation ", "#template-preview-");
                    if(option==selected) $(viewId).show();
                    else $(viewId).hide();
                });
            });

            $("#download-template").click(function() {
                //self.addMetaColumn("observation_summary", $("#template-obs-summary").val());
                return this;
            });

            $("#preview-template").click(function() {
                //self.addMetaColumn("observation_summary", $("#template-obs-summary").val());

                $.fancybox(
                    _.template($("#preview-tmpl").html()),
                    {
                        'autoDimensions' : false,
                        'width' : '100%',
                        'height' : '100%',
                        'transitionIn' : 'none',
                        'transitionOut' : 'none'
                    }
                );

                // create submission
                var submission = {
                    // TODO
                };

                // TODO fake data
                var obs1 = {gene_1:"GENE 1", gene_2:"GENE 2", evidence_1:"EVIDENCE 1"};
                var obs2 = {gene_1:"GENE A", gene_2:"GENE B", evidence_1:"EVIDENCE A"};;

                // Create the submission preview
                (new SubmissionPreviewView({
                    model: {
                        submission: submission,
                        observations: [obs1, obs2]
                    }
                })).render();

                $("#preview-container div.common-container").removeClass("common-container");
                $('#preview-tabs a').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });

                return this;
            });

            $("#download-form").submit(function() {
                var table2TSV = function(id) {
                    var text = "";

                    $(id).find("tr").each(function(i, aRow) {
                        var cells = $(aRow).children();
                        cells.each(function(j, aCell) {
                            var input = $(aCell).find("input");
                            if($(aCell).find("i").length > 0) {
                                text += "";
                            } else if(input.length == 0) {
                                text += $(aCell).text();
                            } else {
                                text += $(input).val();
                            }

                            if((j+1) < cells.length) {
                                text += "\t";
                            }
                        });

                        text += "\n";
                    });

                    return text;
                };

                $("#template-input").val(table2TSV("#template-table"));
                $("#filename-input").val($("#meta-submission_name").text());

                return true;
            });

            return this;
        } // end render function
});

$ctd2.TemplateHelperCenterView = Backbone.View.extend({
        template: _.template($("#template-helper-center-tmpl").html()),

        render: function() {        	
            $(this.el).append(this.template(this.model));
            return this;
        }
});

$ctd2.templateId = 0;

$ctd2.ExistingTemplateView = Backbone.View.extend({
        template: _.template($("#existing-template-row-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            var rowModel = this.model;
            $("#template-action-"+rowModel.id).change(function() {
                var action = $(this).val();
                switch(action) {
                    case 'edit':
                        $ctd2.showTemplateMenu();
                        $ctd2.templateId = rowModel.id;
                        $("span#submission-name").text(rowModel.displayName);
                        // TODO subject data, not complete
                        $("#template-table-subject > .template-data-row").remove();
                        var subjectColumns = rowModel.subjectColumns; // this is an array of strings
                        var subjectClasses = rowModel.subjectClasses; // this is an array of strings
                        var observations = rowModel.observations;
                        var observationNumber = rowModel.observationNumber;

                        // make headers for observation part
                        $("th.observation-header").remove();
                        for(var column=1; column<=observationNumber; column++) {
                            var deleteButton = "delete-column-"+column;
                            $("#template-table tr#subject-header").append("<th class=observation-header>Observation "+column+"<br>(<button class='btn btn-link' id='"+deleteButton+"'>delete</button>)</th>");
                            $("#template-table tr#evidence-header").append("<th class=observation-header>Observation "+column+"</th>");
                            $("#"+deleteButton).click(function() {
                                console.log($(this).attr('id')+" to be implemented");
                            });
                        }

                        var totalRows = subjectColumns.length; // FIXME, add the evidence columns
                        for (var i=0; i < subjectColumns.length; i++) {
                            var observationsPerRow = new Array(observationNumber);
                            for(var column=0; column<observationNumber; column++) {
                                observationsPerRow[column] = observations[totalRows*column+i];
                            };

                            (new $ctd2.TemplateSubjectDataRowView({
                                model: {columnTag: subjectColumns[i].replace(/ /g, "-"), subjectClass: subjectClasses[i], subjectRole: "cell line",
                                    totalRows: totalRows, row: i, 
                                    observationNumber: observationNumber,
                                    observations: observationsPerRow
                                    },
                                el: $("#template-table-subject")
                            })).render();
                        }
                        // TODO evidence data, not complete
                        $("#template-table-evidence > .template-data-row").remove();
                        var evidenceColumns = rowModel.evidenceColumns;
                        var evidenceTypes = rowModel.evidenceTypes;
                        var valueTypes = rowModel.valueTypes;
                        var evidenceDescriptions = rowModel.evidenceDescriptions;
                        for (var i=0; i < evidenceColumns.length; i++) {
                            (new $ctd2.TemplateEvidenceDataRowView({
                                model: {columnTag: evidenceColumns[i].replace(/ /g, "-"), 
                                    evidenceType: evidenceTypes[i], 
                                    valueType: valueTypes[i], 
                                    evidenceDescription: evidenceDescriptions[i]},
                                el: $("#template-table-evidence")
                            })).render();
                        }

                        $("#step2").fadeOut();
                        $("#step4").slideDown();
                        break;
                    case 'delete':
                        $ctd2.deleteTemplate(rowModel.id);
                        $("#template-action-"+rowModel.id).val(""); // in case not confirmed 
                        break;
                    case 'preview':
                        $ctd2.showTemplateMenu();
                        $("#step2").fadeOut();
                        $("#step6").slideDown();
                        break;
                    case 'clone':
                        $ctd2.showTemplateMenu();
                        $("#step2").fadeOut();
                        // TODO clone the template, store it, show the main data page
                        $("#step4").slideDown();
                        break;
                    case 'download':
                        window.open("/ctd2test.zip"); // TODO only to demo the feature
                        break;
                    default:
                        alert(rowModel.displayName+' '+action+' clicked');
                };
                $(this).val('');
            });
            return this;
        }
});

$ctd2.TemplateSubjectDataRowView = Backbone.View.extend({
        template: _.template($("#template-subject-data-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            var columnTag = this.model.columnTag;
            $("#delete-subject-"+columnTag).click(function()  {
                $("#confirmed-delete").unbind('click').click(function(){
                    $('tr#template-subject-row-columntag-'+columnTag).remove();
                });
                $("#confirmation-modal").modal('show'); // TODO: the text needs to be cutomized
            });

            var role = this.model.subjectRole;
            var subjectClass = this.model.subjectClass;
            console.log("subjectClass="+subjectClass);
            if(subjectClass===undefined) subjectClass = "Compound"; // simple default value

            // the list of role depends on subject class; 'selected' is row-specific
            var roleOptions = $ctd2.subjectRoles[subjectClass]; //subjectRoles.models; // TODO temperarily using hard-coded object

            if(roleOptions===undefined) { // exceptional case
                return;
            }

            for (var i = 0; i < roleOptions.length; i++) {
                var roleName = roleOptions[i]; //.toJSON().displayName; // TODO temparily using hard-coded values
                var cName = roleName.charAt(0).toUpperCase() + roleName.slice(1);
                new $ctd2.SubjectRoleDropdownRowView(
                        {
                            el: $('#role-dropdown-'+columnTag.replace(/ /g, "-")),
                            model: { roleName:roleName, cName: cName, selected:roleName==role?'selected':null }
                        } ).render();
            }

            // render observation celss for one row (subject or evidence column tag)
            var tableRow = $('#template-subject-row-columntag-'+this.model.columnTag);
            var totalRows = this.model.totalRows;
            var row = this.model.row;
            var observationNumber = this.model.observationNumber;
            var observations = this.model.observations;
            new $ctd2.TempObservationView({
                el: tableRow,
                model: {columnTag: columnTag, observationNumber: observationNumber, observations: observations},
            }).render();

            return this;
        }
});

$ctd2.TemplateEvidenceDataRowView = Backbone.View.extend({
        template: _.template($("#template-evidence-data-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            var columnTag = this.model.columnTag;
            $("#delete-evidence-"+columnTag).click(function()  {
                $("#confirmed-delete").unbind('click').click(function(){
                    $('tr#template-evidence-row-columntag-'+columnTag).remove();
                });
                $("#confirmation-modal").modal('show'); // TODO: the text needs to be customized
            });

            /*
            var selectedValueType = this.model.valueType;
            // the list of role is fixed, but 'selected' is row-specific
            var roleModels = valueTypes.models;
            for (var i = 0; i < valueTypes.length; i++) {
                var valueType = valueTypes[i].toJSON().displayName;
                var vtName = valueType.charAt(0).toUpperCase() + valueType.slice(1);
                new EvidenceTypeDropdownRowView( // TODO not existing yet
                        {
                            el: $('#role-dropdown-'+columnTag.replace(/ /g, "-")),
                            model: { valueType:valueType, vtName: vtName, selected:valueType==selectedValueType?'selected':null }
                        } ).render();
            }*/

            return this;
        }
});

$ctd2.SubjectRoleDropdownRowView = Backbone.View.extend({
        template: _.template($('#role-dropdown-row-tmpl').html()),
        render: function() {
            // the template expects roleName, selected, cName from the model
            $(this.el).append(this.template(this.model));
        }
});

/* This view's model covers observation data of one row (i.e. subject column tag),
 * but the template is for individual cells 
 * so the template's own model contains individual cell's data.
 * This is necessary because the number of observations, and thus the column number, is a variable. 
 */
$ctd2.TempObservationView = Backbone.View.extend({
        template: _.template($("#temp-observation-tmpl").html()),
        render: function() {
            // this.model should have fields: obvNumber, obvColumn, and obvText for now
            var obvModel = this.model;
            for(var column=0; column<obvModel.observationNumber; column++) {
                var obvContent = obvModel.observations[column];
                var cellModel = {
                    obvNumber: column, 
                    obvColumn: obvModel.columnTag,
                    obvText: obvContent, };
                var obvTemp = this.template(cellModel);
                $(this.el).append(obvTemp);
            }

            for(var column=0; column<obvModel.observationNumbers; column++) {
                var deleteButton = "delete-column-"+column;
                $(this.el).find("tr#subject-header").append("<th>Observation "+column+"<br>(<button class='btn btn-link' id='"+deleteButton+"'>delete</button>)</th>");
                $(this.el).find("tr#evidence-header").append("<th>Observation "+column+"</th>");
                $("#"+deleteButton).click(function() {
                    console.log(deleteButton+" to be implemented");
                });
            }
        }
});

// this is one column in the data table
$ctd2.OneObservationView = Backbone.View.extend({
        template: _.template($("#temp-observation-tmpl").html()),
        render: function() {
            var obvTemp = this.template(this.model);
            $(this.el).find("tr.template-data-row").each( function() {
                $(this).append(obvTemp);
            }
            );
            var obvNumber = 1; // TODO add a smart serial number
            var deleteButton = "delete-column-"+obvNumber;
            $(this.el).find("tr#subject-header").append("<th>Observation "+obvNumber+"<br>(<button class='btn btn-link' id='"+deleteButton+"'>delete</button>)</th>");
            $(this.el).find("tr#evidence-header").append("<th>Observation "+obvNumber+"</th>");
            $("#"+deleteButton).click(function() {
                console.log(deleteButton+" to be implemented");
            });
            $ctd2.obvNumber++;
        }
});

$ctd2.CORE_API_URL = "./";

$ctd2.SubmissionTemplate = Backbone.Model.extend({
        urlRoot: $ctd2.CORE_API_URL + "get/template"
});

$ctd2.StoredTemplates = Backbone.Collection.extend({
        url: $ctd2.CORE_API_URL + "list/template/?filterBy=",
        model: $ctd2.SubmissionTemplate,
        initialize: function(attributes) {
            this.url += attributes.centerId;
        }
});

//$ctd2.subjectRoles = new SubjectRoles();
//subjectRoles.fetch( {async:false} ); // TODO hard-coded for now
$ctd2.subjectRoles = {'Compound':['Candidate drug', 'Control compound', 'Perturbagen'],
        'Gene': ['Background', 'Biomarker', 'Condidate master regulator', 'Interactor', 'Master regultor', 'Oncogone', 'Perturbagen', 'Target'],
        'RNA': ['Perturbagen'],
        'Tissue': ['Disease', 'Metastasis', 'Tissue'],
        'Cell': ['Cell line'],
        'Animal': ['Strain'],
};

$ctd2.showTemplateMenu = function() {
        $("#menu_description").show();
        $("#menu_data").show();
        $("#menu_summary").show();
        $("#menu_preview").show();
};

$ctd2.deleteTemplate = function(tobeDeleted) {
        $("#confirmed-delete").unbind('click').click(function(){
            $(this).attr("disabled", "disabled");
            $.ajax({
                async: false,
                url: "template/delete",
                type: "POST",
                data: jQuery.param({
                    templateId: tobeDeleted,
                }),
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                success: function(response) {
                    console.log(response);
                    $("#template-table-row-"+tobeDeleted).remove();
                }
            });
            $(this).removeAttr("disabled");
        });
        //$("#confirmation-message").text("... updated message (if ncessary)");
        $("#confirmation-modal").modal('show');
};

$ctd2.getStringList = function(searchTag) {
    var s = "";
    $(searchTag).each(function (i, row) {
        if(i>0) s += ","
        s += $(row).val();
    });
    return s;
}

$ctd2.updateTemplate = function(sync) {
        if($ctd2.templateId==0) {
        	alert('error: $ctd2.templateId==0');
        	return;
        }
        var subjects = $ctd2.getStringList('#template-table-subject input.subject-columntag');
        var subjectClasses = $ctd2.getStringList('#template-table-subject input.subject-classes');
        var subjectRoles = $ctd2.getStringList('#template-table-subject input.subject-roles');
        var subjectDescriptions = $ctd2.getStringList('#template-table-subject input.subject-descriptions');
        var evidences = $ctd2.getStringList('#template-table-evidence input.evidence-columntag');
        var evidenceTypes = $ctd2.getStringList('#template-table-evidence input.evidence-types');
        var valueTypes = $ctd2.getStringList('#template-table-evidence input.value-types');
        var evidenceDescriptions = $ctd2.getStringList('#template-table-evidence input.evidence-descriptions');
        var observationNumber = 2; //$ctd2.get...;
        var observations = 'a, b, c, d, e, f'; //$ctd2.getStringList...;

        var async = true;
        if(sync) async = false;
        var result = false;
        $.ajax({
            async: async,
            url: "template/update",
            type: "POST",
            data: jQuery.param({
                templateId: $ctd2.templateId,
                subjects: subjects,
                subjectClasses: subjectClasses,
                subjectRoles: subjectRoles,
                subjectDescriptions: subjectDescriptions,
                evidences: evidences,
                evidenceTypes: evidenceTypes,
                valueTypes: valueTypes,
                evidenceDescriptions: evidenceDescriptions,
                observationNumber: observationNumber,
                observations: observations,
               }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: function(data) {
                console.log("return value: "+data);
                $("#template-name").val("");
                $("#save-name-description").removeAttr("disabled");
                result = true;
           }
         });
        if (async || result)
            return true;
        else
            return false;
};

$ctd2.saveNewTemplate = function(sync) {
        var centerId = $("#template-submission-centers").val();
        var submissionName = $("#template-name").val();

        var firstName = $("#first-name").val();
        var lastName = $("#last-name").val();
        var description = $("#template-submission-desc").val();
        var project = $("#template-project-title").val();

        if(centerId.length==0 || firstName.length == 0 || lastName.length == 0
            || submissionName.length == 0) {
            console.log("not saved due to incomplete information");
            return false; // error control
        }

        var async = true;
        if(sync) async = false;
        var result = false;
        $.ajax({
            async: async,
            url: "template/create",
            type: "POST",
            data: jQuery.param({
                centerId: centerId,
                name : submissionName,
                firstName: firstName,
                lastName: lastName,
                description: description,
                project: project,
               }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: function(resultId) {
                $("#template-name").val("");
                $("#save-name-description").removeAttr("disabled");
                result = true;
                $ctd2.templateId = resultId;
                $("span#submission-name").text(submissionName);
                $ctd2.showTemplateMenu();
           }
         });
        if (async || result)
            return true;
        else
            return false;
};
