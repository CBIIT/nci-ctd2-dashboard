package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
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
@RequestMapping("/centers")
public class APIController {
    private static final Log log = LogFactory.getLog(APIController.class);
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
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(apiCenters), headers, HttpStatus.OK);

    }

    static class APISubmission {
        final String id, project, description, storyTitle;
        final Date date;
        final Integer tier;
        final Integer observationCount;

        public APISubmission(final Submission s, int observationCount) {
            ObservationTemplate observationTemplate = s.getObservationTemplate();
            // required part
            this.id = s.getId().toString();
            this.date = s.getSubmissionDate();
            this.tier = observationTemplate.getTier();
            this.project = observationTemplate.getProject();
            this.description = observationTemplate.getDescription();
            this.observationCount = new Integer(observationCount);

            // not-required
            String st = null;
            if (observationTemplate.getIsSubmissionStory())
                st = observationTemplate.getDescription();
            storyTitle = st;
        }
    }

    static class APICenter {
        final String name, id, pi;
        final APISubmission[] submissions;

        public APICenter(SubmissionCenter c, String pi, APISubmission[] submissions) {
            this.name = c.getDisplayName();
            this.id = c.getId().toString();
            this.pi = pi;
            this.submissions = submissions;

        }
    }

}
