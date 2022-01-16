package gov.nih.nci.ctd2.dashboard.controller;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.util.DashboardEntityWithCounts;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.SearchResults;

@Controller
@RequestMapping("/ontology-search")
public class OntologySearchController {
    private static final Log log = LogFactory.getLog(OntologySearchController.class);

    @Autowired
    private DashboardDao dashboardDao;

    @RequestMapping(method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> ontologySearch(@RequestParam("terms") String terms) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        SearchResults ontologyResult = dashboardDao.ontologySearch(URLDecoder.decode(terms, Charset.defaultCharset()));
        log.debug("number of subject results from search " + ontologyResult.numberOfSubjects());
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(ontologyResult), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "extra-submissions", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> searchExtraSubmissions(@RequestParam("subject-name") String subjectName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        try {
            List<Submission> submissions = dashboardDao.getSubmissionsForSubjectName(subjectName);
            List<DashboardEntityWithCounts> submission_result = new ArrayList<DashboardEntityWithCounts>();
            submissions.forEach(submission -> {
                DashboardEntityWithCounts entityWithCounts = new DashboardEntityWithCounts();
                entityWithCounts.setDashboardEntity(submission);
                entityWithCounts.setObservationCount(dashboardDao.findObservationsBySubmission(submission).size());
                entityWithCounts.setMaxTier(submission.getObservationTemplate().getTier());
                entityWithCounts.setCenterCount(1);
                submission_result.add(entityWithCounts);
            });

            JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                    .transform(new DateTransformer(), Date.class);
            return new ResponseEntity<String>(jsonSerializer.deepSerialize(submission_result), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
    }
}
