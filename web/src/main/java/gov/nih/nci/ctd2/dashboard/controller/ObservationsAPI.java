package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
@RequestMapping("/api/observations")
public class ObservationsAPI {
    private static final Log log = LogFactory.getLog(ObservationsAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "{submissionId}/{indexRanges}", method = {
            RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getObservations(@PathVariable String submissionId, @PathVariable String indexRanges) {
        /* submissionId is not internal ID as other parts of code. it is the identifier as part of stableURL */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Set<Integer> indexes = new HashSet<Integer>();
        try {
            for (String range : indexRanges.split(",")) {
                String[] x = range.split("-");
                if (x.length == 1) {
                    int index = Integer.parseInt(x[0]);
                    indexes.add(index);
                } else if (x.length == 2) {
                    int index1 = Integer.parseInt(x[0]);
                    int index2 = Integer.parseInt(x[1]);
                    if (index2 <= index1) {
                        log.warn("incorrect ranges syntax:" + indexRanges);
                        return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
                    }
                    for (int i = index1; i <= index2; i++) {
                        indexes.add(i);
                    }
                } else {
                    log.warn("incorrect ranges syntax:" + indexRanges);
                    return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
                }

            }
        } catch (NumberFormatException ex) {
            log.warn(ex.getMessage());
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        try{
        ObservationItem[] observations = dashboardDao.getObservations(submissionId, indexes);

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
            json = jsonSerializer.deepSerialize(observations);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);} catch(Exception e){e.printStackTrace();}
        return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
    }
}
