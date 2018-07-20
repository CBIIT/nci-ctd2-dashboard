package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.CTD2Serializer;
import gov.nih.nci.ctd2.dashboard.api.ObservationItem;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/submission")
public class SubmissionAPI {
    private static final Log log = LogFactory.getLog(SubmissionAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private WebServiceUtil webServiceUtil;

    @Autowired
    private String dataURL;

    @Transactional
    @RequestMapping(value = "{id}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubmission(@PathVariable String id,
            @RequestParam(value = "maximum", required = false, defaultValue = "") String maximum) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        int limit = 0;
        if (maximum != null && maximum.trim().length() > 0) {
            try {
                limit = Integer.parseInt(maximum.trim());
            } catch (NumberFormatException e) {
                // no-op
            }
        }
        Submission submission = dashboardDao.getEntityByStableURL("submission", "submission/" + id);
        List<? extends DashboardEntity> observations = webServiceUtil.getDashboardEntities("observation",
                submission.getId());
        if (limit > 0 && limit < observations.size()) {
            observations = observations.subList(0, limit);
        }
        ObservationItem.dataURL = dataURL;
        ObservationItem[] obvs = new ObservationItem[observations.size()];
        for (int i = 0; i < observations.size(); i++) {
            obvs[i] = new ObservationItem((Observation) observations.get(i), dashboardDao);
        }
        APISubmission apiSubmission = new APISubmission(submission, obvs);

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = CTD2Serializer.createJSONSerializer();
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(apiSubmission);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    public static class APISubmission {
        public final String submission_center, submission_name;
        public final Date submisstion_date;
        public final Integer tier;
        public final String project, submission_description, story_title;
        public final Integer observation_count;
        public final ObservationItem[] observations;

        public APISubmission(Submission submission, ObservationItem[] observations) {
            ObservationTemplate template = submission.getObservationTemplate();
            this.submission_center = template.getSubmissionCenter().getDisplayName();
            this.submission_name = submission.getDisplayName();
            this.submisstion_date = submission.getSubmissionDate();
            this.tier = template.getTier();
            this.project = template.getProject();
            this.submission_description = template.getDescription();

            String st = null;
            if (template.getIsSubmissionStory())
                st = template.getDescription();
            this.story_title = st;

            this.observation_count = observations.length;
            this.observations = observations;
        }
    }
}
