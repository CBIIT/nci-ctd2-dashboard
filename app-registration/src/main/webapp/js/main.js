(function ($) {

    $(function () {
        $("#register").click(function (e) {
            e.preventDefault()
            document.getElementById("registration-form").reset()
            $("#action").val("register")
            $("#registration-form").show()
            $("#modify-message").hide()
            $("#registration-not-found").hide()
            $("#previous-image").hide()
            $(".form-control").each(countCharacters)
        })
        $("#modify").click(function (e) {
            e.preventDefault()
            $("#modify-message").show()
            $("#registration-form").hide()
        })
        $("#retrieve").click(function (e) {
            e.preventDefault()
            const registration_id = $("#registration-id").val()
            $.ajax("registration/" + registration_id).done(function (result) {
                $("#action").val("update")
                $("#app_code").val(result.app_code)
                $("#title").val(result.title)
                $("#url").val(result.url)
                $("#description").val(result.description)
                $("#developers").val(result.developers)
                $("#email").val(result.email)
                $("#institution").val(result.institution)
                $("#lab").val(result.lab)
                if (result.image === undefined) {
                    $("#previous-image").hide()
                } else {
                    $("#previous-image").attr("src", "data:image/jpeg;base64," + result.image)
                    $("#previous-image").show()
                }
                $("#registration-form").show()
                $("#registration-not-found").hide()
            }).fail(function (err) {
                if (err.status === 404) {
                    $("#not-found-id").text(registration_id)
                    $("#registration-not-found").show()
                    $("#registration-form").hide()
                } else {
                    console.log(err)
                }
            });
        })
        $("#title").attr("maxlength", TITLE_LIMIT)
        $("#url").attr("maxlength", URL_LIMIT)
        $("#description").attr("maxlength", DESCRIPTION_LIMIT)
        $("#developers").attr("maxlength", DEVELOPERS_LIMIT)
        $("#email").attr("maxlength", EMAIL_LIMIT)
        $("#institution").attr("maxlength", INSTITUTION_LIMIT)
        $("#lab").attr("maxlength", LAB_LIMIT)
        $(".form-control").each(countCharacters)
        $(".form-control").keyup(countCharacters)
        $("#registration-form").submit(function (event) {
            event.preventDefault();
            const recaptcha_response = grecaptcha.getResponse()
            if (recaptcha_response === "") {
                error('reCaptcha failed: no user response')
                return
            }
            $.post({
                url: "registration/recaptcha",
                data: { recaptcha_response: recaptcha_response }
            }).done(function (response) {
                grecaptcha.reset()
                if (!response.success) {
                    console.log('reCaptcha failed')
                    console.log(response["error-codes"])
                    error('reCaptcha failed')
                    return
                }
                const form = $(event.target)
                const action = form.find("#action").val()
                const title = form.find("#title").val().trim()
                const url = form.find("#url").val().trim()
                const description = form.find("#description").val().trim()
                const developers = form.find("#developers").val().trim()
                const email = form.find("#email").val().trim()
                const institution = form.find("#institution").val().trim()
                const lab = form.find("#lab").val()
                const image = form.find("#image")[0].files[0]

                const v = validate({
                    title: title, url: url, description: description,
                    developers: developers, email: email, institution: institution, lab: lab, image: image
                })
                if (v !== "") {
                    error(`invalid input: ${v}`)
                    return
                }

                const formData = new FormData()
                formData.append('title', title)
                formData.append('url', url)
                formData.append('description', description)
                formData.append('developers', developers)
                formData.append('email', email)
                formData.append('institution', institution)
                formData.append('lab', lab)
                formData.append('image', image)

                if (action == "register") {
                    fetch('registration/register', { method: 'POST', body: formData }).then(response => {
                        if (!response.ok) {
                            throw new Error(`Your registration has not been submitted successfully. Please forward the following error message to CTD2 team:\n ${response.status}`);
                        }
                        return response.json()
                    }).then(result => {
                        info(`Your registration has been submitted. An email is being sent to ${email} with more details.`)
                        console.log(result)
                        $("#registration-form").hide()
                    }).catch(err => {
                        error(err)
                    }).finally(function () {
                        console.log("done with new registration query")
                    })
                } else if (action == "update") {
                    const app_code = form.find("#app_code").val()
                    formData.append('app_code', app_code)

                    fetch('registration/update', { method: 'POST', body: formData }).then(response => {
                        if (!response.ok) {
                            throw new Error(`update query failed. status ${response.status}`);
                        }
                        return response.json()
                    }).then(result => {
                        info(`Your registration ${app_code} has been updated.`)
                        form.find("#image").val(null)
                        console.log(result)
                        $("#registration-form").hide()
                    }).catch(err => {
                        error(err)
                    }).finally(function () {
                        console.log("done with update query")
                    })
                } else {
                    console.log(`incorrect action: ${action}`)
                }
            }).fail(function (err) { console.log(err) })
        })

        $("#omnisearch").submit(function () {
            const search_term = ($("#omni-input").val().trim().replaceAll("'", "`"))
            const too_short = Array.from(search_term.matchAll(/([^"]\S*|".+?")\s*/g), m => m[1].replace(/^"/, "").replace(/"$/, ""))
                .some(x => x.length <= 2)
            if (too_short) {
                error("Search queries containing terms with one or two letters are not allowed as they may return too many results.  Please enclose search terms in quotes or reformulate it. E.g. B cell should be submitted as \"B Cell\".")
                return false
            }
            if (search_term.length < 2) {
                error("You cannot search for a single character.")
                return false
            }
            window.location = "/dashboard/#search/" + encodeURIComponent(search_term)
            return false
        })
        $("#omni-input").popover({
            placement: "bottom",
            trigger: "manual",
            html: true,
            title: function () {
                $(this).attr("title")
            },
            content: function () {
                return $("#search-help-content").html()
            },
        }).on("mouseenter", function () {
            const _this = this
            $(this).popover("show")
            $(".popover").on("mouseleave", function () {
                $(_this).popover('hide')
            })
        }).on("mouseleave", function () {
            const _this = this
            setTimeout(function () {
                if (!$(".popover:hover").length) {
                    $(_this).popover("hide")
                }
            }, 300)
        })
    })

    function error(message) {
        $("#message-modal-text").text(message)
        $("#modal-button").removeClass("btn-primary")
        $("#modal-button").addClass("btn-danger")
        $("#message-modal").modal("show")
    }

    function info(message) {
        $("#message-modal-text").text(message)
        $("#modal-button").removeClass("btn-danger")
        $("#modal-button").addClass("btn-primary")
        $("#message-modal").modal("show")
    }

    function countCharacters() {
        const max = $(this).attr("maxlength")
        const length = $(this).val().length
        const counter = max - length
        const helper = $(this).next(".form-text")
        // Switch to the singular if there's exactly 1 character remaining
        if (counter !== 1) {
            helper.text(counter + " characters remaining");
        } else {
            helper.text(counter + " character remaining");
        }
        // Make it red if there are 0 characters remaining
        if (counter === 0) {
            helper.removeClass("text-muted");
            helper.addClass("text-danger");
        } else {
            helper.removeClass("text-danger");
            helper.addClass("text-muted");
        }
    }

    /* size limit. should match DB schema */
    const TITLE_LIMIT = 30;
    const URL_LIMIT = 100;
    const DESCRIPTION_LIMIT = 500;
    const DEVELOPERS_LIMIT = 100;
    const EMAIL_LIMIT = 50;
    const INSTITUTION_LIMIT = 50;
    const LAB_LIMIT = 100;
    function validate(data) {
        if (data.title === "") {
            return "The title should not be empty."
        }
        if (data.title.length > TITLE_LIMIT) {
            return `The title should be no more than ${TITLE_LIMIT} characters.`
        }
        if (data.url === "") {
            return "The URL should not be empty."
        }
        if (data.url.length > URL_LIMIT) {
            return `The URL should be no more than ${URL_LIMIT} characters.`
        }
        if (data.description === "") {
            return "The description should not be empty."
        }
        if (data.description.length > DESCRIPTION_LIMIT) {
            return `The description should be no more than ${DESCRIPTION_LIMIT} characters.`
        }
        if (data.developers === "") {
            return "The developer name should not be empty."
        }
        if (data.developers.length > DEVELOPERS_LIMIT) {
            return `The developers should be no more than ${DEVELOPERS_LIMIT} characters.`
        }
        if (data.email === "") {
            return "The contact email should not be empty."
        }
        if (data.email.length > EMAIL_LIMIT) {
            return `The email should be no more than ${EMAIL_LIMIT} characters.`
        }
        if (data.institution === "") {
            return "The institution should not be empty."
        }
        if (data.institution.length > INSTITUTION_LIMIT) {
            return `The institution should be no more than ${INSTITUTION_LIMIT} characters.`
        }
        if (data.lab.length > LAB_LIMIT) {
            return `The lab should be no more than ${LAB_LIMIT} characters.`
        }

        return ""
    }
})(window.jQuery)