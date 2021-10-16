import {
    BASE_URL
} from './ctd2.constants.js'

const ObservedSubject = Backbone.Model.extend({
    urlRoot: BASE_URL + "get/observedsubject"
});

export const ObservedSubjects = Backbone.Collection.extend({
    url: BASE_URL + "list/observedsubject/?filterBy=",
    model: ObservedSubject,

    initialize: function(attributes) {
        if (attributes.subjectId != undefined) {
            this.url += attributes.subjectId;
        } else {
            this.url += attributes.observationId;
        }
    }
});

export const ObservedEvidence = Backbone.Model.extend({
    urlRoot: BASE_URL + "get/observedevidence"
});

export const ObservedEvidences = Backbone.Collection.extend({
    url: BASE_URL + "list/observedevidence/?filterBy=",
    model: ObservedEvidence,

    initialize: function(attributes) {
        this.url += attributes.observationId;
    }
});