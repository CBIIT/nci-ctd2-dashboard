package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.ExcludeTransformer;
import gov.nih.nci.ctd2.dashboard.api.FieldNameTransformer;
import gov.nih.nci.ctd2.dashboard.api.ObservationItem;
import gov.nih.nci.ctd2.dashboard.api.SimpleDateTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

/* API 2.0 */
@Controller
@RequestMapping("/api/observation")
public class ObservationAPI {
    private static final Log log = LogFactory.getLog(ObservationAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "{id}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubmission(@PathVariable String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        ObservationItem observation = dashboardDao.getObservationInfo("observation/" + id);
        if (observation == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new SimpleDateTransformer(), Date.class)
                .transform(new FieldNameTransformer("class"), "clazz")
                .transform(new FieldNameTransformer("class"), "subject_list.clazz")
                .transform(new FieldNameTransformer("class"), "evidence_list.clazz")
                .transform(new FieldNameTransformer("subject_uri"), "subject_list.uri")
                .transform(new ExcludeTransformer(), void.class).exclude("class").exclude("evidence_list.evidenceName")
                .exclude("evidence_list.columnName").exclude("subject_list.columnName").exclude("subject_list.synonyms")
                .exclude("subject_list.xref").exclude("uri").exclude("id");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(observation);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
