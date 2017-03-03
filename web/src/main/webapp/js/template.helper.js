$ctd2 = {
    templateId: 0, // currently selected submission template ID, or 0 meaning no template selected
    templateModels: {}, // data of all templates, keyed by their ID's
    saveSuccess: true,
}; /* the supporting module of ctd2-dashboard app ctd2.js */

$ctd2.TemplateHelperView = Backbone.View.extend({
    template: _.template($("#template-helper-tmpl").html()),
    el: $("#main-container"),

    render: function() {
            $(this.el).html(this.template(this.model));

            // top menu
            $("#menu_home").click(function() {
                $ctd2.showPage("#step1");
                $ctd2.setCurrentPageIndicator();
            });
            $("#menu_manage").click(function() {
                $ctd2.showPage("#step2");
                $ctd2.setCurrentPageIndicator();
            }).hide();
            $("#menu_description").click(function() {
                $ctd2.showPage("#step3");
                $ctd2.setCurrentPageIndicator(this);
            }).hide();
            $("#menu_data").click(function() {
                $ctd2.showPage("#step4");
                $ctd2.setCurrentPageIndicator(this);
            }).hide();
            $("#menu_summary").click(function() {
                $ctd2.populateTagList();
                $ctd2.showPage("#step5");
                $ctd2.setCurrentPageIndicator(this);
            }).hide();
            $("#menu_preview").click(function() {
                $ctd2.showPage("#step6");
                $ctd2.setCurrentPageIndicator(this);
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
                $ctd2.refreshTemplateList(centerId);
            });

            $("#create-new-submission").click(function() {
                $ctd2.hideTemplateMenu();
                $ctd2.templateId = 0;
                $("#submitter-information").empty();
                $("#template-description").empty();
                (new $ctd2.SubmitterInformationView({
                    model: {firstname:null, lastname:null, email:null, phone:null},
                    el: $("#submitter-information")
                })).render();
                (new $ctd2.TemplateDescriptionView({
                    model: {name:null, description:null, projecttitle:null, tier:null, isstory:null},
                    el: $("#template-description")
                })).render();

                // empty the data page and the summary page
                $ctd2.populateOneTemplate({
                    id: 0, // this is kind of important becasue it will reset $ctd2.templateId
                    subjectColumns: [],
                    subjectClasses: [],
                    evidenceColumns: [],
                    evidenceTypes: [],
                    valueTypes: [],
                    observationNumber: 0,
                    observations: "",
                });

                $("#step2").fadeOut();
                $("#step3").slideDown();
            });

            // although the other button is called #create-new-submission, this is where it is really created back-end
            $("#save-name-description").click(function() {
                if($ctd2.templateId==0) {
                    $(this).attr("disabled", "disabled");
                    $ctd2.saveNewTemplate();
                } else {
                    $ctd2.updateTemplate($(this));
                }
            });
            $("#continue-to-main-data").click(function() { // similar to save, additionally moving to the next
                var ret = true;
                if($ctd2.templateId==0) {
                    ret = $ctd2.saveNewTemplate(true);
                } else {
                    $ctd2.updateTemplate($(this));
                }
                if(ret && $ctd2.saveSuccess) {
                    $("#step3").fadeOut();
                    $("#step4").slideDown();
                    $("#menu_description").removeClass("current-page");
                    $("#menu_data").addClass("current-page");
                } else {
                    $ctd2.saveSuccess  = true; // reset the flag
                }
            });

            $("#save-template-submission-data").click(function() {
                console.log("saving the submission data ...");
                $ctd2.updateTemplate($(this));
            });
            $("#apply-template-submission-data").click(function() {
                $ctd2.updateTemplate($(this));
                if($ctd2.saveSuccess) {
                    $("#step4").fadeOut();
                    $ctd2.populateTagList();
                    $("#step5").slideDown();
                    $("#menu_data").removeClass("current-page");
                    $("#menu_summary").addClass("current-page");
                } else {
                    $ctd2.saveSuccess  = true; // reset the flag
                }
            });

            $("#save-summary").click(function() {
                console.log("saving the summary ...");
                $ctd2.updateTemplate($(this)); // TODO add lock
            });
            $("#continue-from-summary").click(function() {
                $ctd2.updateTemplate($(this));
                if($ctd2.saveSuccess) {
                    $("#step5").fadeOut();
                    $("#step6").slideDown();
                    $("#menu_summary").removeClass("current-page");
                    $("#menu_preview").addClass("current-page");
                } else {
                    $ctd2.saveSuccess  = true; // reset the flag
                }
            });

            $("#add-evidence").click(function() {
                $ctd2.addNewEvidence();
            });

            $("#add-subject").click(function() {
                $ctd2.addNewSubject();
            });

            $("#add-observation").click(function() {
                new $ctd2.NewObservationView({
                    el: $("#template-table"),
                }).render();
            });

            $("#preview-select").change(function() {
                var selected = $(this).val();
                $(this).children("option").each(function() {
                    var option = $(this).val();
                    var viewId = option.replace("observation ", "#observation-preview-");
                    if(option==selected) $(viewId).show();
                    else $(viewId).hide();
                });
            });

            $("#download-form").submit(function() {
                var model = $ctd2.templateModels[$("#template-id").val()];
                $("#filename-input").val(model.displayName);
                return true;
            });

            return this;
    } // end render function
}); // end of TemplateHelperView

$ctd2.setCurrentPageIndicator = function(currentPage) {
    $("#menu_description").removeClass('current-page');
    $("#menu_data").removeClass('current-page');
    $("#menu_summary").removeClass('current-page');
    $("#menu_preview").removeClass('current-page');
    $(currentPage).addClass("current-page"); // if currentPage is null, it is OK
};

$ctd2.ObservationPreviewView = Backbone.View.extend({
    template: _.template($("#observation-preview-tmpl").html()),
    render: function() {
        $(this.el).append(this.template(this.model));
        return this;
    }
});

$ctd2.ObservationOptionView = Backbone.View.extend({
    template: _.template($("#observation-option-tmpl").html()),
    render: function() {
        $(this.el).append(this.template(this.model));
        return this;
    }
});

$ctd2.ColumnTagView = Backbone.View.extend({
    template: _.template($("#column-tag-item-tmpl").html()),
    render: function() {
        $(this.el).append(this.template(this.model));
        return this;
    }
});

$ctd2.TemplateHelperCenterView = Backbone.View.extend({
        template: _.template($("#template-helper-center-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
});

$ctd2.SubmitterInformationView = Backbone.View.extend({
        template: _.template($("#submitter-information-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
});

$ctd2.TemplateDescriptionView = Backbone.View.extend({
        template: _.template($("#template-description-tmpl").html()),

        render: function() {
            $(this.el).append(this.template(this.model));
            return this;
        }
});

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
                        if($ctd2.templateId != rowModel.id) {
                            $ctd2.populateOneTemplate(rowModel);
                        }

                        $("#step2").fadeOut();
                        $("#step4").slideDown();
                        $ctd2.setCurrentPageIndicator("#menu_data");
                        break;
                    case 'delete':
                        $ctd2.deleteTemplate(rowModel.id);
                        $("#template-action-"+rowModel.id).val(""); // in case not confirmed 
                        break;
                    case 'preview':
                        $ctd2.showTemplateMenu();
                        $("#step2").fadeOut();
                        $("#step6").slideDown();
                        $ctd2.setCurrentPageIndicator("#menu_preview");
                        break;
                    case 'clone':
                        $ctd2.clone(rowModel.id);
                        break;
                    case 'download':
                        $("#template-id").val(rowModel.id);
                        $("#download-form").submit();
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
            var columnTagId = this.model.columnTagId;
            $("#delete-subject-"+columnTagId).click(function()  {
                $("#confirmed-delete").unbind('click').click(function(){
                    $('tr#template-subject-row-columntag-'+columnTagId).remove();
                });
                $('#confirmation-message').text("Are you sure you want to delete this subject row?");
                $("#confirmation-modal").modal('show');
            });

            var role = this.model.subjectRole;
            var subjectClass = this.model.subjectClass;
            if(subjectClass===undefined || subjectClass==null) subjectClass = "Compound"; // simple default value

            // the list of role depends on subject class; 'selected' is row-specific
            var roleOptions = $ctd2.subjectRoles[subjectClass];

            if(roleOptions===undefined) { // exceptional case
                return;
            }

            for (var i = 0; i < roleOptions.length; i++) {
                var roleName = roleOptions[i]; //.toJSON().displayName; // TODO temparily using hard-coded values
                var cName = roleName.charAt(0).toUpperCase() + roleName.slice(1);
                new $ctd2.SubjectRoleDropdownRowView(
                        {
                            el: $('#role-dropdown-'+columnTagId),
                            model: { roleName:roleName, cName: cName, selected:roleName==role?'selected':null }
                        } ).render();
            }
            $('#subject-class-dropdown-'+columnTagId).change(function() {
                roleOptions = $ctd2.subjectRoles[$(this).val()];
                $('#role-dropdown-'+columnTagId).empty();
                for (var i = 0; i < roleOptions.length; i++) {
                    var roleName = roleOptions[i];
                    var cName = roleName.charAt(0).toUpperCase() + roleName.slice(1);
                    new $ctd2.SubjectRoleDropdownRowView(
                        {
                            el: $('#role-dropdown-'+columnTagId),
                            model: { roleName:roleName, cName: cName, selected:null }
                        } ).render();
                }
            });

            // render observation cells for one row (subject or evidence column tag)
            var tableRow = $('#template-subject-row-columntag-'+columnTagId);
            var totalRows = this.model.totalRows;
            var row = this.model.row;
            var observationNumber = this.model.observationNumber;
            var observations = this.model.observations;
            new $ctd2.TempObservationView({
                el: tableRow,
                model: {columnTagId: columnTagId, observationNumber: observationNumber, observations: observations, obvsType: 'text'},
            }).render();

            return this;
        }
});

$ctd2.TemplateEvidenceDataRowView = Backbone.View.extend({
        template: _.template($("#template-evidence-data-row-tmpl").html()),
        render: function() {
            $(this.el).append(this.template(this.model));
            var columnTagId = this.model.columnTagId;
            $("#delete-evidence-"+columnTagId).click(function()  {
                $("#confirmed-delete").unbind('click').click(function(){
                    $('tr#template-evidence-row-columntag-'+columnTagId).remove();
                });
                $('#confirmation-message').text("Are you sure you want to delete this evidence row?");
                $("#confirmation-modal").modal('show');
            });

            var valueType = this.model.valueType;
            var evidenceTypeOptions = $ctd2.evidenceTypes[valueType];
            if(evidenceTypeOptions===undefined) { // exceptional case
                alert('incorrect value type: '+this.model.valueType);
                return;
            }

            for (var i = 0; i <evidenceTypeOptions.length; i++) {
                new $ctd2.EvidenceTypeDropdownView( {
                    el: $('#evidence-type-'+columnTagId),
                    model: { evidenceType:evidenceTypeOptions[i], selected:evidenceTypeOptions[i]==this.model.evidenceType }
                } ).render();
            }
            $('#value-type-'+columnTagId).change(function() {
                console.log("DEBUG CHANGED valueType="+valueType+" "+$(this).val());
                evidenceTypeOptions = $ctd2.evidenceTypes[$(this).val()];
                $('#evidence-type-'+columnTagId).empty();
                for (var i = 0; i < evidenceTypeOptions.length; i++) {
                    new $ctd2.EvidenceTypeDropdownView( {
                        el: $('#evidence-type-'+columnTagId),
                        model: { evidenceType:evidenceTypeOptions[i], selected:false }
                    } ).render();
                }
            });

            // render observation cells for one row (evidence column tag)
            var tableRow = $('#template-evidence-row-columntag-'+columnTagId);
            var totalRows = this.model.totalRows;
            var row = this.model.row;
            var observationNumber = this.model.observationNumber;
            var observations = this.model.observations;
            var obsvType = 'text';
            if(this.model.valueType=='Document' || this.model.valueType=='Image') {
                obsvType = 'file';
            };
            new $ctd2.TempObservationView({
                el: tableRow,
                model: {columnTagId: columnTagId, observationNumber: observationNumber, observations: observations, obvsType: obsvType},
            }).render();

            tableRow.find('.value-types').change(function() {
                var fields = $('#template-evidence-row-columntag-'+columnTagId+" [id^=observation-]");
                var prev_type = fields[0].type;
                var new_type = 'text';
                var option = $(this).val();
                if(option=='Document' || option=='Image') {
                    new_type = 'file';
                }
                if(new_type!=prev_type) {
                    for(var i=0; i<fields.length; i++) {
                        fields[i].type = new_type;
                    }
                }
            });

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

$ctd2.EvidenceTypeDropdownView = Backbone.View.extend({
    template: _.template($('#evidence-type-dropdown-tmpl').html()),
    render: function() {
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
                var u = '';
                if(obvModel.obvsType=='file') {
                    if(obvContent===undefined || obvContent==null || obvContent=="undefined") {
                    } else {
                        var i = obvContent.lastIndexOf('\\');
                        if(i<0)i = obvContent.lastIndexOf('/');
                        if(i>0)u = obvContent.substring(i+1);
                    }
                }
                var cellModel = {
                    obvNumber: column, 
                    obvColumn: obvModel.columnTagId,
                    obvText: obvContent, 
                    type: obvModel.obvsType,
                    uploaded: u};
                var obvTemp = this.template(cellModel);
                $(this.el).append(obvTemp);
            }

        }
});

/* this is one NEW column in the observation data table. because it is new, it is meant to be empty */
$ctd2.NewObservationView = Backbone.View.extend({
        template: _.template($("#temp-observation-tmpl").html()),
        render: function() {
            var tmplt = this.template;
            var obvNumber = $('#template-table tr#subject-header').find('th').length-4;
            var columnTagId = 0;
            $(this.el).find("tr.template-data-row").each( function() {
                var value_type = $(this).find(".value-types").val();
                var input_type = 'text';
                if(value_type=='Image' || value_type=='Document') input_type = 'file';
                var obvTemp = tmplt({
                    obvNumber: obvNumber,
                    obvColumn: columnTagId,
                    obvText: null,
                    type: input_type,
                    uploaded: ""
                });
                $(this).append(obvTemp);
                columnTagId++;
            }
            );
            var deleteButton = "delete-column-"+obvNumber;
            $(this.el).find("tr#subject-header").append("<th class=observation-header>Observation "+obvNumber+"<br>(<button class='btn btn-link' id='"+deleteButton+"'>delete</button>)</th>");
            $(this.el).find("tr#evidence-header").append("<th class=observation-header>Observation "+obvNumber+"</th>");
            $("#"+deleteButton).click(function() {
                var c = $('#template-table tr#subject-header').find('th').index($(this).parent());
                $('#template-table tr').find('td:eq('+c+'),th:eq('+c+')').remove();
            });
            $ctd2.obvNumber++;
            $(this.el).parent().scrollLeft($(this.el).width());
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

$ctd2.subjectRoles = {'Compound':['Candidate drug', 'Control compound', 'Perturbagen'],
        'Gene': ['Background', 'Biomarker', 'Condidate master regulator', 'Interactor', 'Master regultor', 'Oncogone', 'Perturbagen', 'Target'],
        'RNA': ['Perturbagen'],
        'Tissue': ['Disease', 'Metastasis', 'Tissue'],
        'Cell': ['Cell line'],
        'Animal': ['Strain'],
};
$ctd2.evidenceTypes = {
    'Number': ['measured','observed','computed','background'],
    'Text': ['measured','observed','computed','species','background'],
    'Document': ['literature','measured','observed','computed','written','background'],
    'Image': ['literature','measured','observed','computed','written','background'],
    'URL': ['measured','computed','reference','resource','link'],
    'Internal dashboard link': ['measured','computed','reference','resource','link'],
};

$ctd2.showPage = function(page_name) {
    $("#step1").fadeOut();
    $("#step2").fadeOut();
    $("#step3").fadeOut();
    $("#step4").fadeOut();
    $("#step5").fadeOut();
    $("#step6").fadeOut();
    $(page_name).slideDown();
};

$ctd2.showTemplateMenu = function() {
        $("#menu_description").show();
        $("#menu_data").show();
        $("#menu_summary").show();
        $("#menu_preview").show();
};

$ctd2.hideTemplateMenu = function() {
    $("#menu_description").hide();
    $("#menu_data").hide();
    $("#menu_summary").hide();
    $("#menu_preview").hide();
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
        $('#confirmation-message').text("Are you sure you want to delete this submission template?");
        $("#confirmation-modal").modal('show');
};

$ctd2.getArray = function(searchTag) {
    var s = [];
    $(searchTag).each(function (i, row) {
        s.push( $(row).val().trim() );
    });
    return s;
};

$ctd2.getStringList = function(searchTag) {
    var s = "";
    $(searchTag).each(function (i, row) {
        if(i>0) s += ","
        s += $(row).val();
    });
    return s;
};

$ctd2.array2StringList = function(a) {
    var s = "";
    for(var i=0; i<a.length; i++) {
        if(i>0) s += ","
        s += a[i];
    }
    return s;
};

$ctd2.observationArray = [];
$ctd2.getObservations = function() {
    var columns = $(".observation-header").length/2;
    var rows = $("#template-table tr").length-2;
    $ctd2.observationArray = new Array(rows*columns);
    $("#template-table tr.template-data-row").each(function (i, row) {
        $(row).find("[id^=observation]").each(function(j, c){
            $ctd2.observationArray[j*rows+i] = $(c).val();

            var id = $(c).attr('id');
            var index = id.indexOf('-', 12); // skip the first dash
            var columntag = $(c).attr('id').substring(index+1);
            var valuetype = $("#value-type-"+columntag).val();
            if(valuetype=='Document' || valuetype=='Image') {
                var p = $(c).prop('files');
                if(p!=null && p.length>0) {
                    var file = p[0];
                    $ctd2.dataReady = true;
                    var reader  = new FileReader();
                    var savebutton = $("#save-template-submission-data");
                    reader.addEventListener("load", function () {
                        var filecontent = reader.result.replace("base64,", "base64:"); // comma breaks later processing
                        $ctd2.observationArray[j*rows+i] = file.name+":"+filecontent;
                        savebutton.removeAttr("disabled");
                        $ctd2.dataReady = true;
                    }, false);
                    if (file) {
                        $ctd2.dataReady = false;
                        savebutton.attr("disabled", "disabled");
                        reader.readAsDataURL(file);
                    } else {
                        console.log("NEVER HAPPEN. TODO: simplify this.");
                    }
                }
            }
        });
    });
};

$ctd2.dataReady = true;
$ctd2.updateTemplate = function(triggeringButton) {
	$ctd2.getObservations(); // this set $ctd2.dataReady to be false until the data is ready
	$ctd2.processObservationArray(triggeringButton);
}

$ctd2.processObservationArray = function(triggeringButton) {
    if ($ctd2.dataReady === true) {
         $ctd2.updateTemplate_1(triggeringButton);
         return;
    }
    setTimeout($ctd2.processObservationArray, 1000, triggeringButton);
}

$ctd2.hasDuplicate = function(a) {
    if(!Array.isArray(a)) {
        console.log("ERROR: duplicate checking for an object that is not an array");
        return false;
    }
    var tmp = [];
    for(var i=0; i<a.length; i++) {
        if(tmp.indexOf(a[i])>=0) {
            console.log("duplicate item: "+a[i]);
            return true;
        }
        tmp.push(a[i]);
    }
    return false;
}

$ctd2.updateTemplate_1 = function(triggeringButton) {
    $ctd2.validate = function() {
        if($ctd2.templateId==0) {
        	return 'error: $ctd2.templateId==0';
        }
        var message = '';
        for(var i=0; i<subjects.length; i++) {
            if(subjects[i]==null || subjects[i]=="") {
                subjects[i] = "MISSING_TAG"; // double safe-guard the list itself not be mis-interpreted as empty
                message += "\nsubject column tag cannot be empty";
            }
        }
        if($ctd2.hasDuplicate(subjects)) {
            return "There is duplicate in subject column tags. This is not allowed.";
        }
        subjects = $ctd2.array2StringList(subjects);
        for(var i=0; i<evidences.length; i++) {
            if(evidences[i]==null || evidences[i]=="") {
                evidences[i] = "MISSING_TAG"; // double safe-guard the list itself not be mis-interpreted as empty
                message += "\nevidence column tag cannot be empty";
            }
        }
        if($ctd2.hasDuplicate(evidences)) {
            return "There is duplicate in evidence column tags. This is not allowed.";
        }
        evidences = $ctd2.array2StringList(evidences);
        return message;
    }

    var firstName = $("#first-name").val();
    var lastName = $("#last-name").val();
    var email = $("#email").val();
    var phone = $("#phone").val();
    var submissionName = $("#template-name").val();
    var description = $("#template-submission-desc").val();
    var project = $("#template-project-title").val();
    var tier = $("#template-tier").val();
    var isStory = $("#template-is-story").is(':checked');

    var subjects = $ctd2.getArray('#template-table-subject input.subject-columntag');
    var subjectClasses = $ctd2.getStringList('#template-table-subject select.subject-classes');
    var subjectRoles = $ctd2.getStringList('#template-table-subject select.subject-roles');
    var subjectDescriptions = $ctd2.getStringList('#template-table-subject input.subject-descriptions');
    var evidences = $ctd2.getArray('#template-table-evidence input.evidence-columntag');
    var evidenceTypes = $ctd2.getStringList('#template-table-evidence select.evidence-types');
    var valueTypes = $ctd2.getStringList('#template-table-evidence select.value-types');
    var evidenceDescriptions = $ctd2.getStringList('#template-table-evidence input.evidence-descriptions');
    var observationNumber = $(".observation-header").length/2;

    var s = "";
    for(var i=0; i<$ctd2.observationArray.length; i++) {
            s += $ctd2.observationArray[i]+",";
    }
    var observations = s.substring(0, s.length-1);

    var summary = $("#template-obs-summary").val();

    var x = $ctd2.validate(); // some arrays are converted to string after validation
    if(x!=null && x.length>0) {
        alert(x);
        $ctd2.saveSuccess = false;
        return;
    }

    triggeringButton.attr("disabled", "disabled");
    $.ajax({
            url: "template/update",
            type: "POST",
            data: jQuery.param({
                templateId: $ctd2.templateId,
                firstName: firstName,
                lastName: lastName,
                email: email,
                phone: phone,
                name : submissionName,
                description: description,
                project: project,
                tier: tier,
                isStory: isStory,
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
                summary: summary,
               }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: function(data) {
                console.log("return value: "+data);
                triggeringButton.removeAttr("disabled");
                var centerId = $("#template-submission-centers").val();
                $ctd2.refreshTemplateList(centerId);
           },
           error: function(response, status) {
               triggeringButton.removeAttr("disabled");
               // response.responseText is an HTML page
               alert(status+": "+response.responseText);
           }
    });
};

$ctd2.saveNewTemplate = function(sync) {
        var centerId = $("#template-submission-centers").val();
        var submissionName = $("#template-name").val();

        var firstName = $("#first-name").val();
        var lastName = $("#last-name").val();
        var email = $("#email").val();
        var phone = $("#phone").val();
        var description = $("#template-submission-desc").val();
        var project = $("#template-project-title").val();
        var tier = $("#template-tier").val();
        var isStory = $("#template-is-story").is(':checked');

        if(centerId.length==0 || firstName.length == 0 || lastName.length == 0
            || submissionName.length == 0) {
            console.log("not saved due to incomplete information");
            $("#save-name-description").removeAttr("disabled");
            alert("not saved due to incomplete information");
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
                email: email,
                phone: phone,
                description: description,
                project: project,
                tier: tier,
                isStory: isStory,
               }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: function(resultId) {
                $("#save-name-description").removeAttr("disabled");
                result = true;
                $ctd2.templateId = resultId;
                $("span#submission-name").text(submissionName);
                $ctd2.showTemplateMenu();
                $ctd2.refreshTemplateList(centerId);
                },
            error: function(response, status) {
                $("#save-name-description").removeAttr("disabled");
                alert("create failed\n"+status+": "+response.responseText);
                }
         });
        if (async || result)
            return true;
        else
            return false;
};

$ctd2.clone = function(templateId) {
    var centerId = $("#template-submission-centers").val(); // TODO why is this not part of the model?
    $("#template-table-row-"+templateId).attr("disabled", "disabled");
    var result = false;
    $.ajax({
        url: "template/clone",
        type: "POST",
        data: jQuery.param({
                centerId: centerId,
                templateId: templateId
               }),
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function(resultId) {
                $("#template-table-row-"+templateId).removeAttr("disabled");
                result = true;
                $ctd2.templateId = 0;
                $ctd2.showTemplateMenu();
                $ctd2.refreshTemplateList(centerId);
                console.log('clone succeeded '+templateId+' -> '+resultId);
           },
        error: function(response, status) {
               alert("clone failed\n"+status+": "+response.responseText);
               $("#template-table-row-"+tmpltModel.id).removeAttr("disabled");
           }
    });
}

$ctd2.addNewSubject = function(tag) {
    var tagid = $("#template-table-subject tr").length;
    var observationNumber = $(".observation-header").length/2;
    (new $ctd2.TemplateSubjectDataRowView({
        model: {
            columnTagId: tagid, columnTag: tag, subjectClass: null, subjectRole: null, subjectDescription:null,
            observationNumber: observationNumber,
            observations: []
            },
        el: $("#template-table-subject")
    })).render();
};

$ctd2.addNewEvidence = function(tag) {
    var tagid = $("#template-table-evidence tr").length;
    var observationNumber = $(".observation-header").length/2;
    (new $ctd2.TemplateEvidenceDataRowView({
        model: {
            columnTagId: tagid, columnTag: tag, evidenceType: "background", valueType: "Document", evidenceDescription:null,
            observationNumber: observationNumber,
            observations: []
            },
        el: $("#template-table-evidence")
    })).render();
};

$ctd2.populateOneTemplate = function(rowModel) {
                        $("#template-id").val(rowModel.id);
                        $ctd2.templateId = rowModel.id;

                        $("span#submission-name").text(rowModel.displayName);

                        $("#submitter-information").empty();
                        $("#template-description").empty();
                        (new $ctd2.SubmitterInformationView({
                            model: {firstname: rowModel.firstName, lastname: rowModel.lastName, email:rowModel.email, phone:rowModel.phone},
                            el: $("#submitter-information")
                        })).render();
                        (new $ctd2.TemplateDescriptionView({
                            model: {name: rowModel.displayName, description:rowModel.description, projecttitle:rowModel.project, tier:rowModel.tier, isstory:rowModel.isStory},
                           el: $("#template-description")
                        })).render();

                        $("#template-table-subject > .template-data-row").remove();
                        var subjectColumns = rowModel.subjectColumns; // this is an array of strings
                        var subjectClasses = rowModel.subjectClasses; // this is an array of strings
                        var observations = rowModel.observations.split(",");
                        var observationNumber = rowModel.observationNumber;
                        var evidenceColumns = rowModel.evidenceColumns;

                        // make headers for observation part
                        $("th.observation-header").remove();
                        for(var column=1; column<=observationNumber; column++) {
                            var deleteButton = "delete-column-"+column;
                            $("#template-table tr#subject-header").append("<th class=observation-header>Observation "+column+"<br>(<button class='btn btn-link' id='"+deleteButton+"'>delete</button>)</th>");
                            $("#template-table tr#evidence-header").append("<th class=observation-header>Observation "+column+"</th>");
                            $("#"+deleteButton).click(function() {
                                var c = $('#template-table tr#subject-header').find('th').index($(this).parent());
                                $('#template-table tr').find('td:eq('+c+'),th:eq('+c+')').remove();
                            });
                        }

                        var subjectRows = subjectColumns.length;
                        var evidenceRows = evidenceColumns.length;
                        var totalRows = subjectRows+evidenceRows;
                        for (var i=0; i < subjectColumns.length; i++) {
                            var observationsPerRow = new Array(observationNumber);
                            for(var column=0; column<observationNumber; column++) {
                                observationsPerRow[column] = observations[totalRows*column+i];
                            };

                            (new $ctd2.TemplateSubjectDataRowView({
                                model: {
                                    columnTagId: i,
                                    columnTag: subjectColumns[i],
                                    subjectClass: subjectClasses[i],
                                    subjectRole: rowModel.subjectRoles[i],
                                    subjectDescription: rowModel.subjectDescriptions[i],
                                    totalRows: totalRows, row: i, 
                                    observationNumber: observationNumber,
                                    observations: observationsPerRow
                                    },
                                el: $("#template-table-subject")
                            })).render();
                        }
                        if(subjectRows==0) $ctd2.addNewSubject('subject 1');

                        $("#template-table-evidence > .template-data-row").remove();
                        var evidenceTypes = rowModel.evidenceTypes;
                        var valueTypes = rowModel.valueTypes;
                        var evidenceDescriptions = rowModel.evidenceDescriptions;
                        for (var i=0; i < evidenceColumns.length; i++) {
                            var observationsPerRow = new Array(observationNumber);
                            for(var column=0; column<observationNumber; column++) {
                                observationsPerRow[column] = observations[totalRows*column+i+subjectRows];
                            };
                            (new $ctd2.TemplateEvidenceDataRowView({
                                model: {
                                    columnTagId: i,
                                    columnTag: evidenceColumns[i],
                                    evidenceType: evidenceTypes[i], 
                                    valueType: valueTypes[i], 
                                    evidenceDescription: evidenceDescriptions[i],
                                    totalRows: totalRows, row: i+subjectRows,
                                    observationNumber: observationNumber,
                                    observations: observationsPerRow
                                    },
                                el: $("#template-table-evidence")
                            })).render();
                        }
                        if(evidenceColumns.length==0) $ctd2.addNewEvidence('evidence 1');

                        $("#template-obs-summary").val(rowModel.summary);

                        $("#preview-select").empty();
                        $("#step6 [id^=observation-preview-]").remove();
                        for (var i=0; i < observationNumber; i++) {
                            (new $ctd2.ObservationOptionView( {
                                model: {observation_id: i+1}, 
                                el: $("#preview-select")
                            })).render();
                            var oneColumn = new Array(subjectRows);
                            for(var r=0; r<subjectRows; r++) {
                                oneColumn[r] = observations[totalRows*i+r];
                            };
                            (new $ctd2.ObservationPreviewView( {
                                model: {id: i+1, display: (i==0?'block':'none'), observations: oneColumn}, 
                                el: $("#step6")
                            })).render();
                        }
};

$ctd2.refreshTemplateList = function(centerId) {
                $ctd2.templateModels = {};
                var storedTemplates = new $ctd2.StoredTemplates({centerId: centerId});
                $("#existing-template-table > .stored-template-row").remove();
                storedTemplates.fetch({
                    success: function() {
                        _.each(storedTemplates.models, function(oneTemplate) {
                            var oneTemplateModel = oneTemplate.toJSON();
                            $ctd2.templateModels[oneTemplateModel.id] = oneTemplateModel;

                            (new $ctd2.ExistingTemplateView({
                                model: oneTemplateModel,
                                el: $("#existing-template-table")
                            })).render();
                        });
                    }
                });
};

$ctd2.populateTagList = function() {
    $("#column-tag-list").empty();
    $('#template-table').find('.subject-columntag').each( function(index, item) {
        (new $ctd2.ColumnTagView({
            model: { id:index, tag: $(item).val() },
            el: $("#column-tag-list")
        })).render();
    });
    $('#template-table').find('.evidence-columntag').each( function(index, item) {
        (new $ctd2.ColumnTagView({
            model: { id:index, tag: $(item).val() },
            el: $("#column-tag-list")
        })).render();
    });
    $(".helper-tag").click( function() {
        var input = $( "#template-obs-summary" );
        input.val( input.val() + "<" +$(this).text() + ">" );
    });
};
