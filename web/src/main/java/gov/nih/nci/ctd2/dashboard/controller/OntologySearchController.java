package gov.nih.nci.ctd2.dashboard.controller;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.SearchResults;
import gov.nih.nci.ctd2.dashboard.util.SearchResults.SubmissionResult;

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

        SearchResults ontologyResult = dashboardDao.ontologySearch(terms.replaceAll("`", "'"));
        log.debug("number of subject results from ontology search " + ontologyResult.numberOfSubjects());
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(ontologyResult), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "extra-submissions", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> searchExtraSubmissions(@RequestParam("subject-name") String subjectName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        try {
            List<SubmissionResult> submission_result = dashboardDao.getSubmissionsForSubjectName(subjectName).stream()
                    .map(submission -> {
                        ObservationTemplate template = submission.getObservationTemplate();
                        return new SearchResults.SubmissionResult(
                                submission.getStableURL(),
                                submission.getSubmissionDate(),
                                template.getDescription(),
                                template.getTier(),
                                template.getSubmissionCenter().getDisplayName(),
                                submission.getId(),
                                dashboardDao.findObservationsBySubmission(submission).size(),
                                template.getIsSubmissionStory());
                    }).collect(Collectors.toList());

            JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                    .transform(new DateTransformer(), Date.class);
            return new ResponseEntity<String>(jsonSerializer.deepSerialize(submission_result), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
    }
}
