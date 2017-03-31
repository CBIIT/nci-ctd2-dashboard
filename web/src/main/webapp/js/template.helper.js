$ctd2 = {
    centerId: 0,
    templateModels: null, // data of all templates, keyed by their ID's
    currentModel: null, // currently selected submission template, or null meaning no template selected
    saveSuccess: true,
}; /* the supporting module of ctd2-dashboard app ctd2.js */

$ctd2.TemplateHelperView = Backbone.View.extend({
    template: _.template($("#template-helper-tmpl").html()),
    el: $("#main-container"),

    render: function () {
        $(this.el).html(this.template(this.model));

        // top menu
        $("#menu_home").click(function () {
            $ctd2.centerId = 0;
            $ctd2.templateModels = null;
            $ctd2.currentModel = null;
            $("#menu_manage").hide();
            $ctd2.hideTemplateMenu();
            $ctd2.showPage("#step1");
        });
        $("#menu_manage").click(function () {
            $ctd2.currentModel = null;
            $ctd2.hideTemplateMenu();
            $ctd2.showPage("#step2");
        }).hide();
        $("#menu_description").click(function () {
            $ctd2.showPage("#step3", this);
        }).hide();
        $("#menu_data").click(function () {
            $ctd2.showPage("#step4", this);
        }).hide();
        $("#menu_summary").click(function () {
            $ctd2.populateTagList();
            $ctd2.showPage("#step5", this);
        }).hide();
        $("#menu_preview").click(function () {
            $ctd2.showPage("#step6", this);
        }).hide();

        var submissionCenters = new $ctd2.SubmissionCenters();
        submissionCenters.fetch({
            success: function () {
                _.each(submissionCenters.models, function (aCenter) {
                    (new $ctd2.TemplateHelperCenterView({
                        model: aCenter.toJSON(),
                        el: $("#template-submission-centers")
                    })).render();
                });
            }
        });

        $("#apply-submission-center").click(function () {
            var centerId_selected = $("#template-submission-centers").val();
            if (centerId_selected.length == 0) {
                console.log("centerId_selected is empty");
                return; // error control
            }
            $ctd2.centerId = centerId_selected;

            $("#menu_manage").show();
            $("#step1").fadeOut();
            $("#step2").slideDown();
            $("span#center-name").text($("#template-submission-centers option:selected").text());
            $ctd2.refreshTemplateList();
        });

        $("#create-new-submission").click(function () {
            $ctd2.hideTemplateMenu();
            if($ctd2.currentModel!=null) {
                console.log('error: unexpected non-null currentModel');
                return;
            }
            $ctd2.populateOneTemplate(); // TODO maybe use a separate method for the case of new template

            $("#step2").fadeOut();
            $("#step3").slideDown();
        });

        // although the other button is called #create-new-submission, this is where it is really created back-end
        $("#save-name-description").click(function () {
            if ($ctd2.currentModel.id == 0) {
                $(this).attr("disabled", "disabled");
                $ctd2.saveNewTemplate();
            } else {
                $ctd2.updateModel_1();
                $ctd2.updateTemplate($(this));
            }
        });
        $("#continue-to-main-data").click(function () { // similar to save, additionally moving to the next
            var ret = true;
            if ($ctd2.currentModel.id == 0) {
                ret = $ctd2.saveNewTemplate(true);
            } else {
                $ctd2.updateModel_1();
                $ctd2.updateTemplate($(this));
            }
            if (ret && $ctd2.saveSuccess) {
                $("#step3").fadeOut();
                $("#step4").slideDown();
                $("#menu_description").removeClass("current-page");
                $("#menu_data").addClass("current-page");
            } else {
                $ctd2.saveSuccess = true; // reset the flag
            }
        });

        $("#save-template-submission-data").click(function () {
            console.log("saving the submission data ...");
            $ctd2.updateModel_2_sync($(this));
            //$ctd2.updateTemplate($(this));
        });
        $("#apply-template-submission-data").click(function () {
            $ctd2.updateModel_2_sync($(this));
            //$ctd2.updateTemplate($(this));
            if ($ctd2.saveSuccess) {
                $("#step4").fadeOut();
                $ctd2.populateTagList();
                $("#step5").slideDown();
                $("#menu_data").removeClass("current-page");
                $("#menu_summary").addClass("current-page");
            } else {
                $ctd2.saveSuccess = true; // reset the flag
            }
        });

        $("#template-obs-summary").change(function () {
            console.log('change triggered on summary');
        });
        $("#save-summary").click(function () {
            console.log("saving the summary ...");
            $ctd2.updateModel_3();
            $ctd2.updateTemplate($(this)); // TODO add lock
        });
        $("#continue-from-summary").click(function () {
            $ctd2.updateModel_3();
            $ctd2.updateTemplate($(this));
            if ($ctd2.saveSuccess) {
                $("#step5").fadeOut();
                $("#step6").slideDown();
                $("#menu_summary").removeClass("current-page");
                $("#menu_preview").addClass("current-page");
            } else {
                $ctd2.saveSuccess = true; // reset the flag
            }
        });

        $("#add-evidence").click(function () {
            $ctd2.addNewEvidence();
        });

        $("#add-subject").click(function () {
            $ctd2.addNewSubject();
        });

        $("#add-observation").click(function () {
            new $ctd2.NewObservationView({
                el: $("#template-table"),
            }).render();
        });

        $("#download-form").submit(function () {
            var model = $ctd2.templateModels[$("#template-id").val()];
            $("#filename-input").val(model.toJSON().displayName);
            return true;
        });

        return this;
    } // end render function
}); // end of TemplateHelperView

$ctd2.updateModel_1 = function () {
    var firstName = $("#first-name").val();
    var lastName = $("#last-name").val();
    var email = $("#email").val();
    var phone = $("#phone").val();
    var submissionName = $("#template-name").val();
    var description = $("#template-submission-desc").val();
    var project = $("#template-project-title").val();
    var tier = $("#template-tier").val();
    var isStory = $("#template-is-story").is(':checked');
    var storyTitle = $('#story-title').val();

    $ctd2.currentModel.set({
        firstName: firstName,
        lastName: lastName,
        email: email,
        phone: phone,
        displayName: submissionName,
        description: description,
        project: project,
        tier: tier,
        isStory: isStory,
        storyTitle: storyTitle,
    });
};

$ctd2.updateModel_2 = function (triggeringButton) {

    $ctd2.validate = function () {
        var message = '';
        for (var i = 0; i < subjects.length; i++) {
            if (subjects[i] == null || subjects[i] == "") {
                subjects[i] = "MISSING_TAG"; // double safe-guard the list itself not be mis-interpreted as empty
                message += "<li>subject column tag cannot be empty";
            }
        }
        if ($ctd2.hasDuplicate(subjects)) {
            message += "<li>There is duplicate in subject column tags. This is not allowed.";
        }
        for (var i = 0; i < evidences.length; i++) {
            if (evidences[i] == null || evidences[i] == "") {
                evidences[i] = "MISSING_TAG"; // double safe-guard the list itself not be mis-interpreted as empty
                message += "<li>evidence column tag cannot be empty";
            }
        }
        if ($ctd2.hasDuplicate(evidences)) {
            message += "<li>There is duplicate in evidence column tags. This is not allowed.";
        }
        return message;
    }

    var subjects = $ctd2.getArray('#template-table-subject input.subject-columntag');
    var subjectClasses = $ctd2.getArray('#template-table-subject select.subject-classes');
    var subjectRoles = $ctd2.getArray('#template-table-subject select.subject-roles');
    var subjectDescriptions = $ctd2.getArray('#template-table-subject input.subject-descriptions');
    var evidences = $ctd2.getArray('#template-table-evidence input.evidence-columntag');
    var evidenceTypes = $ctd2.getArray('#template-table-evidence select.evidence-types');
    var valueTypes = $ctd2.getArray('#template-table-evidence select.value-types');
    var evidenceDescriptions = $ctd2.getArray('#template-table-evidence input.evidence-descriptions');
    var observationNumber = $(".observation-header").length / 2;

    var s = "";
    for (var i = 0; i < $ctd2.observationArray.length; i++) {
        s += $ctd2.observationArray[i] + ",";
    }
    var observations = s.substring(0, s.length - 1);


    var x = $ctd2.validate(); // some arrays are converted to string after validation
    if (x != null && x.length > 0) {
        $ctd2.showAlertMessage("<ul>"+x+"</ul>");
        $ctd2.saveSuccess = false;
        return;
    }

    $ctd2.currentModel.set({
        subjectColumns: subjects,
        subjectClasses: subjectClasses,
        subjectRoles: subjectRoles,
        subjectDescriptions: subjectDescriptions,
        evidenceColumns: evidences,
        evidenceTypes: evidenceTypes,
        valueTypes: valueTypes,
        evidenceDescriptions: evidenceDescriptions,
        observationNumber: observationNumber,
        observations: observations,
    });
    $ctd2.updateTemplate(triggeringButton);
};

$ctd2.updateModel_3 = function () {
    var summary = $("#template-obs-summary").val();
    $ctd2.currentModel.set({
        summary: summary,
    });
};

$ctd2.Subject = Backbone.Model.extend({
    urlRoot: "./get/subject"
});

$ctd2.ObservationPreviewView = Backbone.View.extend({
    template: _.template($("#observation-tmpl").html()),
    render: function () {
        var thisModel = this.model;
        var observationId = 'observation-preview-' + thisModel.id;
        var observation_preview = this.template(thisModel)
            .replace('id="observation-container"', 'id="' + observationId + '"')
            .replace('<h2>Observation', '<h2>Observation ' + thisModel.id);
        $(this.el).html(observation_preview);
        $('#' + observationId).css('display', thisModel.display);

        // We will replace the values in this summary
        var summary = thisModel.submission.observationTemplate.observationSummary;

        // Load Subjects
        var thatEl = $("#" + observationId + " #observed-subjects-grid");
        _.each(thisModel.observedSubjects, function (observedSubject) {
            var observedSubjectRowView
                = new $ctd2.ObservedSubjectSummaryRowView({
                    el: $(thatEl).find("tbody"),
                    model: observedSubject
                });
            observedSubjectRowView.render();

            var subject = observedSubject.subject;
            var thatEl2 = $("#" + observationId + " #subject-image-" + observedSubject.id);
            var imgTemplate = $("#search-results-unknown-image-tmpl");
            if (subject.class == "compound") {
                var compound = new $ctd2.Subject({ id: subject.id });
                compound.fetch({ // TODO this does not work because subject.id is not real
                    success: function () {
                        compound = compound.toJSON();
                        _.each(compound.xrefs, function (xref) {
                            if (xref.databaseName == "IMAGE") {
                                compound["imageFile"] = xref.databaseId;
                            }
                        });

                        imgTemplate = $("#search-results-compund-image-tmpl");
                        thatEl2.append(_.template(imgTemplate.html(), compound));
                    }
                });
            } else if (subject.class == "gene") {
                imgTemplate = $("#search-results-gene-image-tmpl");
            } else if (subject.class == "shrna") {
                imgTemplate = $("#search-results-shrna-image-tmpl");
            } else if (subject.class == "tissue_sample") {
                imgTemplate = $("#search-results-tissuesample-image-tmpl");
            } else if (subject.class == "cell_sample") {
                imgTemplate = $("#search-results-cellsample-image-tmpl");
            } else if (subject.class == "animal_model") {
                imgTemplate = $("#search-results-animalmodel-image-tmpl");
            }
            if (subject.class != "compound") // for Compound, this would be set asynchronously and use compound instead of subject
                thatEl2.append(_.template(imgTemplate.html(), subject));

            if (observedSubject.observedSubjectRole == null || observedSubject.subject == null)
                return;

            summary = summary.replace(
                new RegExp("<" + observedSubject.observedSubjectRole.columnName + ">", "g"),
                _.template($("#summary-subject-replacement-tmpl").html(), observedSubject.subject)
            );
        });

        // Load evidences
        var thatEl2 = $("#" + observationId + " #observed-evidences-grid");
        _.each(thisModel.observedEvidences, function (observedEvidence) {
            var observedEvidenceRowView = new $ctd2.ObservedEvidenceRowView({
                el: $(thatEl2).find("tbody"),
                model: observedEvidence
            });

            observedEvidenceRowView.render();
            summary = summary.replace(
                new RegExp("<" + observedEvidence.observedEvidenceRole.columnName + ">", "g"),
                _.template($("#summary-evidence-replacement-tmpl").html(), observedEvidence.evidence)
            );
        });
        $("#" + observationId + " #observation-summary").html(summary);

        $('.desc-tooltip').tooltip({ placement: "left" });
        $("div.expandable").expander({
            slicePoint: 50,
            expandText: '[...]',
            expandPrefix: ' ',
            userCollapseText: '[^]'
        });

        $(".numeric-value").each(function (idx) {
            var val = $(this).html();
            var vals = val.split("e"); // capture scientific notation
            if (vals.length > 1) {
                $(this).html(_.template($("#observeddatanumericevidence-val-tmpl").html(), {
                    firstPart: vals[0],
                    secondPart: vals[1].replace("+", "")
                }));
            }
        });

        $(".no-preview").removeAttr('href');

        $("#small-show-sub-details").click(function (event) {
            event.preventDefault();
            $("#obs-submission-details").slideDown();
            $("#small-show-sub-details").hide();
            $("#small-hide-sub-details").show();
        });

        $("#small-hide-sub-details").click(function (event) {
            event.preventDefault();
            $("#obs-submission-details").slideUp();
            $("#small-hide-sub-details").hide();
            $("#small-show-sub-details").show();
        });

        if (thisModel.submission.observationTemplate.submissionDescription == "") {
            $("#obs-submission-summary").hide();
        }

        return this;
    }
});

// this is the same as the one in ctd2.js for now
$ctd2.ObservedSubjectSummaryRowView = Backbone.View.extend({
    template: _.template($("#observedsubject-summary-row-tmpl").html()),
    render: function () {
        var result = this.model;
        if (result.subject == null) return;
        if (result.subject.type == undefined) {
            result.subject["type"] = result.subject.class;
        }

        if (result.subject.class != "Gene") {
            this.template = _.template($("#observedsubject-summary-row-tmpl").html());
            $(this.el).append(this.template(result));
        } else {
            this.template = _.template($("#observedsubject-gene-summary-row-tmpl").html());
            $(this.el).append(this.template(result));
            var currentGene = result.subject["displayName"];

            $(".addGene-" + currentGene).click(function (e) {
                e.preventDefault();
                updateGeneList(currentGene);
                return this;
            });  //end addGene
        }

        return this;
    }
});

// this is the same as the one in ctd2.js for now
$ctd2.ObservedEvidenceRowView = Backbone.View.extend({
    render: function () {
        var result = this.model;
        var type = result.evidence.class;
        result.evidence["type"] = type;

        if (result.observedEvidenceRole == null) {
            result.observedEvidenceRole = {
                displayText: "-",
                evidenceRole: { displayName: "unknown" }
            };
        }

        var templateId = "#observedevidence-row-tmpl";
        var isHtmlStory = false;
        if (type == "file") {
            result.evidence.filePath = result.evidence.filePath.replace(/\\/g, "/");
            if (result.evidence.mimeType.toLowerCase().search("image") > -1) {
                templateId = "#observedimageevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("gct") > -1) {
                templateId = "#observedgctfileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("pdf") > -1) {
                templateId = "#observedpdffileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("sif") > -1) {
                templateId = "#observedsiffileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("mra") > -1) {
                templateId = "#observedmrafileevidence-row-tmpl";
            } else if (result.evidence.mimeType.toLowerCase().search("html") > -1) {
                templateId = "#observedhtmlfileevidence-row-tmpl";
                isHtmlStory = true;
            } else {
                templateId = "#observedfileevidence-row-tmpl";
            }
        } else if (type == "url") {
            templateId = "#observedurlevidence-row-tmpl";
        } else if (type == "label") {
            templateId = "#observedlabelevidence-row-tmpl";
        } else if (type == "numeric") {
            templateId = "#observeddatanumericevidence-row-tmpl";
        }

        this.template = _.template($(templateId).html());
        var thatEl = $(this.el);
        $(this.el).append(this.template(result));

        if (isHtmlStory) {
            thatEl.find(".html-story-link").on("click", function (e) {
                e.preventDefault();
                var url = $(this).attr("href");
                (new HtmlStoryView({ model: { observation: result.observation, url: url } })).render();
            });
        }

        $(".img-rounded").tooltip({ placement: "left" });
        return this;
    }
});

$ctd2.ObservationOptionView = Backbone.View.extend({
    template: _.template($("#observation-option-tmpl").html()),
    render: function () {
        $(this.el).append(this.template(this.model));
        return this;
    }
});

$ctd2.ColumnTagView = Backbone.View.extend({
    template: _.template($("#column-tag-item-tmpl").html()),
    render: function () {
        $(this.el).append(this.template(this.model));
        return this;
    }
});

$ctd2.TemplateHelperCenterView = Backbone.View.extend({
    template: _.template($("#template-helper-center-tmpl").html()),

    render: function () {
        $(this.el).append(this.template(this.model));
        return this;
    }
});

$ctd2.SubmitterInformationView = Backbone.View.extend({
    template: _.template($("#submitter-information-tmpl").html()),

    render: function () {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    },
    events: {
        change: function () {
            console.log('change triggered on submitter information');
        }
    },
});

$ctd2.TemplateDescriptionView = Backbone.View.extend({
    template: _.template($("#template-description-tmpl").html()),

    render: function () {
        $(this.el).html(this.template(this.model.toJSON()));
        if ($("#template-is-story").is(':checked')) $('#story-title-row').show();
        else $('#story-title-row').hide();
        $("#template-is-story").change(function () {
            if ($(this).is(':checked')) $('#story-title-row').show();
            else $('#story-title-row').hide();
        });

        return this;
    },
    events: {
        change: function () {
            console.log('change triggered on template description');
        }
    },
});

$ctd2.ExistingTemplateView = Backbone.View.extend({
    template: _.template($("#existing-template-row-tmpl").html()),

    render: function () {
        $(this.el).append(this.template(this.model.toJSON()));
        var templateId = this.model.id;
        $("#template-action-" + templateId).change(function () {
            var action = $(this).val();
            switch (action) {
                case 'edit':
                    $ctd2.showTemplateMenu();
                    $ctd2.currentModel = $ctd2.templateModels[templateId];
                    $ctd2.populateOneTemplate();
                    $ctd2.showPage("#step4", "#menu_data");
                    break;
                case 'delete':
                    $ctd2.deleteTemplate(templateId);
                    $("#template-action-" + templateId).val(""); // in case not confirmed 
                    break;
                case 'preview':
                    $ctd2.showTemplateMenu();
                    $ctd2.currentModel = $ctd2.templateModels[templateId];
                    $ctd2.populateOneTemplate();
                    $ctd2.showPage("#step6", "#menu_preview");
                    break;
                case 'clone':
                    $ctd2.clone(templateId);
                    break;
                case 'download':
                    $("#template-id").val(templateId);
                    $("#download-form").submit();
                    break;
                default: /* this should never happen */
                    alert('template #' + templateId + ': action ' + action + ' clicked');
            };
            $(this).val('');
        });
        return this;
    }
});

$ctd2.popupLargeTextfield = function() {
    $("#invoker-id").text( $(this).attr('id') );
    $("#temporary-text").val( $(this).val() );
    $("#popup-textarea-modal").modal('show');
}

$ctd2.closeLargeTextfield = function() {
    var invoker_id = $("#invoker-id").text();
    $('#'+invoker_id).val( $("#temporary-text").val() );
}

$ctd2.TemplateSubjectDataRowView = Backbone.View.extend({
    template: _.template($("#template-subject-data-row-tmpl").html()),
    render: function () {
        $(this.el).append(this.template(this.model));
        var columnTagId = this.model.columnTagId;
        $("#delete-subject-" + columnTagId).click(function () {
            $("#confirmed-delete").unbind('click').click(function () {
                $('tr#template-subject-row-columntag-' + columnTagId).remove();
            });
            $('#confirmation-message').text("Are you sure you want to delete this subject row?");
            $("#confirmation-modal").modal('show');
        });

        var subjectClass = this.model.subjectClass;
        if (subjectClass === undefined || subjectClass == null) subjectClass = "gene"; // simple default value

        var resetRoleDropdown = function (sc, sr) {
            roleOptions = $ctd2.subjectRoles[sc];
            if (roleOptions === undefined) { // exceptional case
                console.log("error: roleOption is undefined for subject class " + sc);
                // because this happens for previous stored data, let's allow this
                //return;
                roleOptions = $ctd2.subjectRoles['gene'];
            }
            $('#role-dropdown-' + columnTagId).empty();
            for (var i = 0; i < roleOptions.length; i++) {
                var roleName = roleOptions[i];
                new $ctd2.SubjectRoleDropdownRowView(
                    {
                        el: $('#role-dropdown-' + columnTagId),
                        model: { roleName: roleName, selected: roleName == sr ? 'selected' : null }
                    }).render();
            }
        };
        resetRoleDropdown(subjectClass, this.model.subjectRole);
        $('#subject-class-dropdown-' + columnTagId).change(function () {
            resetRoleDropdown($(this).val(), null);
        });

        // render observation cells for one row (subject or evidence column tag)
        var tableRow = $('#template-subject-row-columntag-' + columnTagId);
        var totalRows = this.model.totalRows;
        var row = this.model.row;
        var observationNumber = this.model.observationNumber;
        var observations = this.model.observations;
        new $ctd2.TempObservationView({
            el: tableRow,
            model: { columnTagId: columnTagId, observationNumber: observationNumber, observations: observations, obvsType: 'text' },
        }).render();

        $(".collapsed-textarea").click($ctd2.popupLargeTextfield);
        $("#close-tempoary-text").unbind('click').click($ctd2.closeLargeTextfield);

        return this;
    },
    events: {
        change: function () {
            console.log('change triggered on subject data');
        }
    },
});

$ctd2.TemplateEvidenceDataRowView = Backbone.View.extend({
    template: _.template($("#template-evidence-data-row-tmpl").html()),
    render: function () {
        $(this.el).append(this.template(this.model));
        var columnTagId = this.model.columnTagId;
        $("#delete-evidence-" + columnTagId).click(function () {
            $("#confirmed-delete").unbind('click').click(function () {
                $('tr#template-evidence-row-columntag-' + columnTagId).remove();
            });
            $('#confirmation-message').text("Are you sure you want to delete this evidence row?");
            $("#confirmation-modal").modal('show');
        });

        var resetEvidenceTypeDropdown = function (vt, et) {
            var evidenceTypeOptions = $ctd2.evidenceTypes[vt];
            if (evidenceTypeOptions === undefined) { // exceptional case
                console.log('incorrect value type: ' + vt);
                //return;
                evidenceTypeOptions = $ctd2.evidenceTypes['numeric'];
            }
            $('#evidence-type-' + columnTagId).empty();
            for (var i = 0; i < evidenceTypeOptions.length; i++) {
                new $ctd2.EvidenceTypeDropdownView({
                    el: $('#evidence-type-' + columnTagId),
                    model: { evidenceType: evidenceTypeOptions[i], selected: evidenceTypeOptions[i] == et }
                }).render();
            }
        };
        resetEvidenceTypeDropdown(this.model.valueType, this.model.evidenceType);
        $('#value-type-' + columnTagId).change(function () {
            resetEvidenceTypeDropdown($(this).val(), null);
        });

        // render observation cells for one row (evidence column tag)
        var tableRow = $('#template-evidence-row-columntag-' + columnTagId);
        var totalRows = this.model.totalRows;
        var row = this.model.row;
        var observationNumber = this.model.observationNumber;
        var observations = this.model.observations;
        var obsvType = this.model.valueType;
        new $ctd2.TempObservationView({
            el: tableRow,
            model: { columnTagId: columnTagId, observationNumber: observationNumber, observations: observations, obvsType: obsvType },
        }).render();

        tableRow.find('.value-types').change(function () {
            var fields = $('#template-evidence-row-columntag-' + columnTagId + " [id^=observation-]");
            var prev_type = fields[0].type;
            var new_type = $(this).val();
            if (new_type != prev_type) {
                for (var i = 0; i < fields.length; i++) {
                    fields[i].type = new_type;
                }
            }
        });

        $(".collapsed-textarea").click($ctd2.popupLargeTextfield);
        $("#close-tempoary-text").unbind('click').click($ctd2.closeLargeTextfield);

        return this;
    },
    events: {
        change: function () {
            console.log('change triggered on evidence data');
        }
    },
});

$ctd2.SubjectRoleDropdownRowView = Backbone.View.extend({
    template: _.template($('#role-dropdown-row-tmpl').html()),
    render: function () {
        // the template expects roleName, selected, cName from the model
        $(this.el).append(this.template(this.model));
    }
});

$ctd2.EvidenceTypeDropdownView = Backbone.View.extend({
    template: _.template($('#evidence-type-dropdown-tmpl').html()),
    render: function () {
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
    render: function () {
        // this.model should have fields: obvNumber, obvColumn, and obvText for now
        var obvModel = this.model;
        for (var column = 0; column < obvModel.observationNumber; column++) {
            var obvContent = obvModel.observations[column];
            var u = '';
            if (obvModel.obvsType == 'file') {
                if (obvContent === undefined || obvContent == null || obvContent == "undefined") {
                } else {
                    var i = obvContent.lastIndexOf('\\');
                    if (i < 0) i = obvContent.lastIndexOf('/');
                    if (i > 0) u = obvContent.substring(i + 1);
                }
            }
            var cellModel = {
                obvNumber: column,
                obvColumn: obvModel.columnTagId,
                obvText: obvContent,
                type: obvModel.obvsType,
                uploaded: u
            };
            var obvTemp = this.template(cellModel);
            $(this.el).append(obvTemp);
        }

    }
});

/* this is one NEW column in the observation data table. because it is new, it is meant to be empty */
$ctd2.NewObservationView = Backbone.View.extend({
    template: _.template($("#temp-observation-tmpl").html()),
    render: function () {
        var tmplt = this.template;
        var obvNumber = $('#template-table tr#subject-header').find('th').length - 4;
        var columnTagId = 0;
        $(this.el).find("tr.template-data-row").each(function () {
            var value_type = $(this).find(".value-types").val();
            var obvTemp = tmplt({
                obvNumber: obvNumber,
                obvColumn: columnTagId,
                obvText: null,
                type: value_type,
                uploaded: ""
            });
            $(this).append(obvTemp);
            columnTagId++;
        }
        );
        var deleteButton = "delete-column-" + obvNumber;
        $(this.el).find("tr#subject-header").append("<th class=observation-header>Observation " + obvNumber + "<br>(<button class='btn btn-link' id='" + deleteButton + "'>delete</button>)</th>");
        $(this.el).find("tr#evidence-header").append("<th class=observation-header>Observation " + obvNumber + "</th>");
        $("#" + deleteButton).click(function () {
            var c = $('#template-table tr#subject-header').find('th').index($(this).parent());
            $('#template-table tr').find('td:eq(' + c + '),th:eq(' + c + ')').remove();
        });
        $ctd2.obvNumber++;
        $(this.el).parent().scrollLeft($(this.el).width());
    }
});

$ctd2.SubmissionTemplate = Backbone.Model.extend({
    defaults: {
        id: 0,
        firstName: null,
        lastName: null,
        email: null,
        phone: null,
        displayName: null,
        description: null,
        project: null,
        tier: null,
        isStory: null,
        storyTitle: null,
        subjectColumns: [],
        subjectClasses: [],
        evidenceColumns: [],
        evidenceTypes: [],
        valueTypes: [],
        observationNumber: 0,
        observations: "",
        summary: "",
    },
    getPreviewModel: function (obvIndex) {
        // re-structure the data for the preview, required by the original observation template
        var obj = this.toJSON();

        var observationTemplate = {
            tier: obj.tier,
            submissionCenter: obj.submissionCenter,
            project: obj.project,
            submissionDescription: obj.description,
            observationSummary: obj.summary,
        };

        var subjectColumns = obj.subjectColumns;
        var evidenceColumns = obj.evidenceColumns;
        var observations = obj.observations.split(",");
        var totalRows = subjectColumns.length + evidenceColumns.length;

        var observedSubjects = [];
        for (var i = 0; i < subjectColumns.length; i++) {
            observedSubjects.push({
                subject: {
                    id: 0, // TODO proper value needed for the correct image? 
                    class: obj.subjectClasses[i],
                    displayName: observations[totalRows * obvIndex + i],
                },
                id: i, //'SUBJECT ID placeholder', // depend on the man dashboard. for image?
                observedSubjectRole: {
                    columnName: subjectColumns[i],
                    subjectRole: {
                        displayName: obj.subjectRoles[i],
                    },
                    displayText: obj.subjectDescriptions[i],
                }
            });
        }
        var observedEvidences = [];
        for (var i = 0; i < obj.evidenceColumns.length; i++) {
            observedEvidences.push({
                evidence: {
                    id: 0, // TODO usage?
                    class: obj.valueTypes[i],
                    displayName: 'EVIDENCE_NAME',// TODO this is required by the tempalte, but what is this for?
                    filePath: '', // TODO when needed
                    mimeType: '', // TODO add when needed
                    url: '', // TODO add when needed
                },
                id: i, // TODO usage?
                observedEvidenceRole: {
                    evidenceRole: {
                        displayName: obj.evidenceTypes[i],
                    },
                    displayText: obj.evidenceDescriptions[i],
                },
                displayName: observations[totalRows * obvIndex + subjectColumns.length + i], // This is put in the Details column in the preview.
            });
        }

        return {
            id: obvIndex + 1,
            submission: {
                id: 0, // this field is used detail-detail. 0 in effect disables it
                observationTemplate: observationTemplate,
                submissionDate: obj.dateLastModified,
                displayName: obj.displayName,
            },
            observedSubjects: observedSubjects,
            observedEvidences: observedEvidences,
        };
    },
});

$ctd2.StoredTemplates = Backbone.Collection.extend({
    url: "list/template/?filterBy=",
    model: $ctd2.SubmissionTemplate,
    initialize: function (attributes) {
        this.url += attributes.centerId;
    }
});

$ctd2.subjectRoles = {
    'gene': ['target', 'biomarker', 'oncogene', 'perturbagen', 'master regulator', 'candidate master regulator', 'interactor', 'background'],
    'shrna': ['perturbagen'],
    'tissue_sample': ['metastasis', 'disease', 'tissue'],
    'cell_sample': ['cell line'],
    'compound': ['candidate drug', 'perturbagen', 'metabolite', 'control compound'],
    'animal_model': ['strain'],
};
$ctd2.evidenceTypes = {
    'numeric': ['measured', 'observed', 'computed', 'background'],
    'label': ['measured', 'observed', 'computed', 'species', 'background'],
    'file': ['literature', 'measured', 'observed', 'computed', 'written', 'background'],
    'url': ['measured', 'computed', 'reference', 'resource', 'link'],
};

$ctd2.showPage = function (page_name, menu_item) {
    $("#step1").fadeOut();
    $("#step2").fadeOut();
    $("#step3").fadeOut();
    $("#step4").fadeOut();
    $("#step5").fadeOut();
    $("#step6").fadeOut();
    $(page_name).slideDown();
    // set current page indicator
    $("#menu_description").removeClass('current-page');
    $("#menu_data").removeClass('current-page');
    $("#menu_summary").removeClass('current-page');
    $("#menu_preview").removeClass('current-page');
    $(menu_item).addClass("current-page"); // if menu_item is null, it is OK
};

$ctd2.showTemplateMenu = function () {
    $("#menu_description").show();
    $("#menu_data").show();
    $("#menu_summary").show();
    $("#menu_preview").show();
};

$ctd2.hideTemplateMenu = function () {
    $("#menu_description").hide();
    $("#menu_data").hide();
    $("#menu_summary").hide();
    $("#menu_preview").hide();
};

$ctd2.deleteTemplate = function (tobeDeleted) {
    $("#confirmed-delete").unbind('click').click(function () {
        $(this).attr("disabled", "disabled");
        $.ajax({
            async: false,
            url: "template/delete",
            type: "POST",
            data: jQuery.param({
                templateId: tobeDeleted,
            }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: function (response) {
                console.log(response);
                $("#template-table-row-" + tobeDeleted).remove();
            }
        });
        $(this).removeAttr("disabled");
    });
    $('#confirmation-message').text("Are you sure you want to delete this submission template?");
    $("#confirmation-modal").modal('show');
};

$ctd2.getArray = function (searchTag) {
    var s = [];
    $(searchTag).each(function (i, row) {
        s.push($(row).val().trim());
    });
    return s;
};

$ctd2.observationArray = [];
$ctd2.getObservations = function () {
    var columns = $(".observation-header").length / 2;
    var rows = $("#template-table tr").length - 4; // two rows for subject/evidence headers, two rows for the headers of each section
    $ctd2.observationArray = new Array(rows * columns);
    $("#template-table tr.template-data-row").each(function (i, row) {
        $(row).find("[id^=observation]").each(function (j, c) {
            $ctd2.observationArray[j * rows + i] = $(c).val();

            var id = $(c).attr('id');
            var index = id.indexOf('-', 12); // skip the first dash
            var columntag = $(c).attr('id').substring(index + 1);
            var valuetype = $("#value-type-" + columntag).val();
            if (valuetype == 'file') {
                var p = $(c).prop('files');
                if (p != null && p.length > 0) {
                    var file = p[0];
                    $ctd2.dataReady = true;
                    var reader = new FileReader();
                    var savebutton = $("#save-template-submission-data");
                    reader.addEventListener("load", function () {
                        var filecontent = reader.result.replace("base64,", "base64:"); // comma breaks later processing
                        $ctd2.observationArray[j * rows + i] = file.name + "::" + filecontent;
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
$ctd2.updateModel_2_sync = function (triggeringButton) {
    $ctd2.getObservations(); // this set $ctd2.dataReady to be false until the data is ready
    $ctd2.processObservationArray(triggeringButton);
}

$ctd2.processObservationArray = function (triggeringButton) {
    if ($ctd2.dataReady === true) {
        $ctd2.updateModel_2(triggeringButton);
        return;
    }
    setTimeout($ctd2.processObservationArray, 1000, triggeringButton);
}

$ctd2.hasDuplicate = function (a) {
    if (!Array.isArray(a)) {
        console.log("ERROR: duplicate checking for an object that is not an array");
        return false;
    }
    var tmp = [];
    for (var i = 0; i < a.length; i++) {
        if (tmp.indexOf(a[i]) >= 0) {
            console.log("duplicate item: " + a[i]);
            return true;
        }
        tmp.push(a[i]);
    }
    return false;
}

$ctd2.updateTemplate = function (triggeringButton) {

    triggeringButton.attr("disabled", "disabled");
    $.ajax({
        url: "template/update",
        type: "POST",
        data: jQuery.param($ctd2.currentModel.toJSON()),
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (data) {
            console.log("return value: " + data);
            triggeringButton.removeAttr("disabled");
            $ctd2.refreshTemplateList();
            $ctd2.updatePreview();
        },
        error: function (response, status) {
            triggeringButton.removeAttr("disabled");
            // response.responseText is an HTML page
            $ctd2.showInvalidMessage(status + ": " + response.responseText);
        }
    });
};

$ctd2.saveNewTemplate = function (sync) {
    if($ctd2.centerId==0) {
        console.log('error: unexpected $cdt2.centerId==0');
        return;
    }
    var submissionName = $("#template-name").val();

    var firstName = $("#first-name").val();
    var lastName = $("#last-name").val();
    var email = $("#email").val();
    var phone = $("#phone").val();
    var description = $("#template-submission-desc").val();
    var project = $("#template-project-title").val();
    var tier = $("#template-tier").val();
    var isStory = $("#template-is-story").is(':checked');
    var storyTitle = $('#story-title').val();

    if (firstName.length == 0 || lastName.length == 0
        || submissionName.length == 0) {
        console.log("not saved due to incomplete information");
        $("#save-name-description").removeAttr("disabled");
        $ctd2.showAlertMessage("new template cannot be created withnot required information: first name, last name, and a submission name");
        return false; // error control
    }

    var async = true;
    if (sync) async = false;
    var result = false;
    $.ajax({
        async: async,
        url: "template/create",
        type: "POST",
        data: jQuery.param({
            centerId: $ctd2.centerId,
            displayName: submissionName,
            firstName: firstName,
            lastName: lastName,
            email: email,
            phone: phone,
            description: description,
            project: project,
            tier: tier,
            isStory: isStory,
            storyTitle: storyTitle,
        }),
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (resultId) {
            $("#save-name-description").removeAttr("disabled");
            result = true;
            $("span#submission-name").text(submissionName);
            $ctd2.showTemplateMenu();
            $ctd2.refreshTemplateList();
            $ctd2.currentModel = $ctd2.templateModels[resultId];
       },
        error: function (response, status) {
            $("#save-name-description").removeAttr("disabled");
            $ctd2.showInvalidMessage("create failed\n" + status + ": " + response.responseText);
        }
    });
    if (async || result)
        return true;
    else
        return false;
};

$ctd2.clone = function (templateId) {
    if($ctd2.centerId==0) {
        console.log('error: unexpected $cdt2.centerId==0');
        return;
    }
    $("#template-table-row-" + templateId).attr("disabled", "disabled");
    var result = false;
    $.ajax({
        url: "template/clone",
        type: "POST",
        data: jQuery.param({
            centerId: $ctd2.centerId,
            templateId: templateId
        }),
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function (resultId) {
            $("#template-table-row-" + templateId).removeAttr("disabled");
            result = true;
            $ctd2.currentModel = null;
            $ctd2.showTemplateMenu();
            $ctd2.refreshTemplateList();
            console.log('clone succeeded ' + templateId + ' -> ' + resultId);
        },
        error: function (response, status) {
            $ctd2.showInvalidMessage("clone failed\n" + status + ": " + response.responseText);
            $("#template-table-row-" + tmpltModel.id).removeAttr("disabled");
        }
    });
}

$ctd2.addNewSubject = function (tag) {
    var tagid = $("#template-table-subject tr").length - 1;
    var observationNumber = $(".observation-header").length / 2;
    (new $ctd2.TemplateSubjectDataRowView({
        model: {
            columnTagId: tagid, columnTag: tag, subjectClass: null, subjectRole: null, subjectDescription: null,
            observationNumber: observationNumber,
            observations: []
        },
        el: $("#template-table-subject")
    })).render();
};

$ctd2.addNewEvidence = function (tag) {
    var tagid = $("#template-table-evidence tr").length - 1;
    var observationNumber = $(".observation-header").length / 2;
    (new $ctd2.TemplateEvidenceDataRowView({
        model: {
            columnTagId: tagid, columnTag: tag, evidenceType: null, valueType: null, evidenceDescription: null,
            observationNumber: observationNumber,
            observations: []
        },
        el: $("#template-table-evidence")
    })).render();
};

$ctd2.populateOneTemplate = function () {
    if ($ctd2.currentModel==null || $ctd2.currentModel === undefined) /* case of new template */
        $ctd2.currentModel = new $ctd2.SubmissionTemplate();
    $("#template-id").val($ctd2.currentModel.id); /* used by download form only */
    var rowModel = $ctd2.currentModel.toJSON();

    $("span#submission-name").text(rowModel.displayName);

    (new $ctd2.SubmitterInformationView({
        model: $ctd2.currentModel,
        el: $("#submitter-information")
    })).render();
    (new $ctd2.TemplateDescriptionView({
        model: $ctd2.currentModel,
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
    for (var column = 1; column <= observationNumber; column++) {
        var deleteButton = "delete-column-" + column;
        $("#template-table tr#subject-header").append("<th class=observation-header>Observation " + column + "<br>(<button class='btn btn-link' id='" + deleteButton + "'>delete</button>)</th>");
        $("#template-table tr#evidence-header").append("<th class=observation-header>Observation " + column + "</th>");
        $("#" + deleteButton).click(function () {
            var c = $('#template-table tr#subject-header').find('th').index($(this).parent());
            $('#template-table tr').find('td:eq(' + c + '),th:eq(' + c + ')').remove();
        });
    }

    var subjectRows = subjectColumns.length;
    var evidenceRows = evidenceColumns.length;
    var totalRows = subjectRows + evidenceRows;
    for (var i = 0; i < subjectColumns.length; i++) {
        var observationsPerRow = new Array(observationNumber);
        for (var column = 0; column < observationNumber; column++) {
            observationsPerRow[column] = observations[totalRows * column + i];
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
    if (subjectRows == 0) $ctd2.addNewSubject('subject 1');

    $("#template-table-evidence > .template-data-row").remove();
    var evidenceTypes = rowModel.evidenceTypes;
    var valueTypes = rowModel.valueTypes;
    var evidenceDescriptions = rowModel.evidenceDescriptions;
    for (var i = 0; i < evidenceColumns.length; i++) {
        var observationsPerRow = new Array(observationNumber);
        for (var column = 0; column < observationNumber; column++) {
            observationsPerRow[column] = observations[totalRows * column + i + subjectRows];
        };
        (new $ctd2.TemplateEvidenceDataRowView({
            model: {
                columnTagId: i,
                columnTag: evidenceColumns[i],
                evidenceType: evidenceTypes[i],
                valueType: valueTypes[i],
                evidenceDescription: evidenceDescriptions[i],
                totalRows: totalRows, row: i + subjectRows,
                observationNumber: observationNumber,
                observations: observationsPerRow
            },
            el: $("#template-table-evidence")
        })).render();
    }
    if (evidenceColumns.length == 0) $ctd2.addNewEvidence('evidence 1');

    $("#template-obs-summary").val(rowModel.summary);
    $ctd2.updatePreview();
}

$ctd2.updatePreview = function () { // this should be called when the template data (model) changes
    $("#preview-select").empty();
    $("#step6 [id^=observation-preview-]").remove();
    var observationNumber = $ctd2.currentModel.get('observationNumber');
    for (var i = 0; i < observationNumber; i++) {
        (new $ctd2.ObservationOptionView({
            model: { observation_id: i },
            el: $("#preview-select")
        })).render();
    }

    $ctd2.observationPreviewView = new $ctd2.ObservationPreviewView({
        el: $("#preview-container")
    });
    if (observationNumber > 0) {
        $ctd2.observationPreviewView.model = $ctd2.currentModel.getPreviewModel(0);
        $ctd2.observationPreviewView.render();
    }

    $("#preview-select").unbind('change').change(function () {
        var selected = parseInt($(this).val());
        if (selected < 0 || selected >= observationNumber) {
            console.log('error in preview selected ' + selected);
            return;
        }
        $ctd2.observationPreviewView.model = $ctd2.currentModel.getPreviewModel(selected);
        $ctd2.observationPreviewView.render();
    });
};

$ctd2.refreshTemplateList = function () {
    if($ctd2.centerId==0) {
        console.log('error: unexpected $ctd2.centerId==0');
        return;
    }
    $ctd2.templateModels = {};
    var storedTemplates = new $ctd2.StoredTemplates({ centerId: $ctd2.centerId });
    $("#existing-template-table > .stored-template-row").remove();
    storedTemplates.fetch({
        async: false,
        success: function () {
            _.each(storedTemplates.models, function (oneTemplateModel) {
                $ctd2.templateModels[oneTemplateModel.id] = oneTemplateModel;

                (new $ctd2.ExistingTemplateView({
                    model: oneTemplateModel,
                    el: $("#existing-template-table")
                })).render();
            });
        }
    });
};

$ctd2.populateTagList = function () {
    $("#column-tag-list").empty();
    $('#template-table').find('.subject-columntag').each(function (index, item) {
        (new $ctd2.ColumnTagView({
            model: { id: index, tag: $(item).val() },
            el: $("#column-tag-list")
        })).render();
    });
    $('#template-table').find('.evidence-columntag').each(function (index, item) {
        (new $ctd2.ColumnTagView({
            model: { id: index, tag: $(item).val() },
            el: $("#column-tag-list")
        })).render();
    });
    $(".helper-tag").click(function () {
        var input = $("#template-obs-summary");
        input.val(input.val() + "<" + $(this).text() + ">");
    });
};

$ctd2.showAlertMessage = function(message) {
    $("#alertMessage").html(message);
    $("#alertMessage").css('color', '#5a5a5a');   
    $("#alert-message-modal").modal('show');
}
$ctd2.showInvalidMessage = function(message) {
    $("#alertMessage").text(message);
    $("#alertMessage").css('color', 'red');
    $("#alert-message-modal").modal('show');
}
