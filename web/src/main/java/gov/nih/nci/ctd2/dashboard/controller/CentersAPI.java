package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;
import java.util.List;

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

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.ExcludeTransformer;
import gov.nih.nci.ctd2.dashboard.api.SimpleDateTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

/* API 2.0 */
@Controller
@RequestMapping("/api/centers")
public class CentersAPI {
    private static final Log log = LogFactory.getLog(CentersAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

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
            String[] ss = submissions.stream().map(x -> x.getStableURL()).toArray(String[]::new);
            String pi = submissions.get(0).getObservationTemplate().getPrincipalInvestigator(); // design flaw
            apiCenters[centerIndex++] = new APICenter(center, pi, ss);
        }

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new SimpleDateTransformer(), Date.class).transform(new ExcludeTransformer(), void.class);
        String json = "{}";
        try {
            json = jsonSerializer.exclude("class").exclude("submissions.class").deepSerialize(apiCenters);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    public static class APICenter {
        public final String center_name, center_id, principal_investigator;
        public final String[] submissions; // DashboardURI

        public APICenter(SubmissionCenter c, String pi, String[] submissions) {
            this.center_name = c.getDisplayName();
            this.center_id = c.getStableURL().substring("center/".length());
            this.principal_investigator = pi;
            this.submissions = submissions;
        }
    }
}
