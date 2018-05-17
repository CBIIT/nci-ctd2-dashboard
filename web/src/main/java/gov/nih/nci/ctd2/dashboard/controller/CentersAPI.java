package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.ExcludeTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/centers")
public class CentersAPI {
    private static final Log log = LogFactory.getLog(CentersAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private WebServiceUtil webServiceUtil;

    @Transactional
    @RequestMapping(method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getCenters() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<SubmissionCenter> centers = dashboardDao.findEntities(SubmissionCenter.class);
        APICenter[] apiCenters = new APICenter[centers.size()];
        int centerIndex = 0;
        for (SubmissionCenter center : centers) {
            List<Submission> submissions = dashboardDao.findSubmissionBySubmissionCenter(center);
            APISubmission[] ss = new APISubmission[submissions.size()];
            String pi = submissions.get(0).getObservationTemplate().getPrincipalInvestigator(); // design flaw
            int i = 0;
            for (Submission s : submissions) {
                int observationCount = webServiceUtil.getDashboardEntities("observation", s.getId()).size();
                ss[i++] = new APISubmission(s, observationCount);
            }
            apiCenters[centerIndex++] = new APICenter(center, pi, ss);
        }

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class).transform(new ExcludeTransformer(), void.class);
        String json = "{}";
        try {
            json = jsonSerializer.exclude("class").exclude("submissions.class").deepSerialize(apiCenters);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    public static class APISubmission {
        public final String submission_id, project, submission_description, story_title;
        public final Date submission_date;
        public final Integer tier;
        public final Integer observation_count;

        public APISubmission(final Submission s, int observationCount) {
            ObservationTemplate observationTemplate = s.getObservationTemplate();
            // required part
            this.submission_id = s.getStableURL().substring("submission/".length());
            this.submission_date = s.getSubmissionDate();
            this.tier = observationTemplate.getTier();
            this.project = observationTemplate.getProject();
            this.submission_description = observationTemplate.getDescription();
            this.observation_count = new Integer(observationCount);

            // not-required
            String st = null;
            if (observationTemplate.getIsSubmissionStory())
                st = observationTemplate.getDescription();
            story_title = st;
        }
    }

    public static class APICenter {
        public final String center_name, center_id, principal_investigator;
        public final APISubmission[] submissions;

        public APICenter(SubmissionCenter c, String pi, APISubmission[] submissions) {
            this.center_name = c.getDisplayName();
            this.center_id = c.getStableURL().substring("center/".length());
            this.principal_investigator = pi;
            this.submissions = submissions;
        }
    }
}
