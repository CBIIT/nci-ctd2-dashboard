import {BASE_URL} from './ctd2.constants.js'

export const ECOTerm = Backbone.Model.extend({
    urlRoot: BASE_URL + "eco/term"
});
