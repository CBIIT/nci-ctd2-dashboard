package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/stories")
public class StoriesController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, headers = "Accept=application/json")
    public ResponseEntity<String> getSearchResultsInJson(@RequestParam("limit") Integer limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        ArrayList<Observation> entities = new ArrayList<Observation>();

        for (Submission submission : dashboardDao.findSubmissionByIsStory(true, true)) {
            List<Observation> observationsBySubmission = dashboardDao.findObservationsBySubmission(submission);
            // Story submissions have a single observation in them
            assert observationsBySubmission.size() == 1;
            entities.addAll(observationsBySubmission);
        }

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                ;

        return new ResponseEntity<String>(
                jsonSerializer.serialize(entities.subList(0, limit)),
                headers,
                HttpStatus.OK
        );
    }
}
