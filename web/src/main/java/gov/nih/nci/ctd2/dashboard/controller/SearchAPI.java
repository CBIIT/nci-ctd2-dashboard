package gov.nih.nci.ctd2.dashboard.controller;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.ExcludeTransformer;
import gov.nih.nci.ctd2.dashboard.api.FieldNameTransformer;
import gov.nih.nci.ctd2.dashboard.api.SubjectResponse;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.util.DashboardEntityWithCounts;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

@Controller
@RequestMapping("/api/search")
public class SearchAPI {
    private static final Log log = LogFactory.getLog(SearchAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private WebServiceUtil webServiceUtil;

    @Transactional
    @RequestMapping(value = "{term}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubmission(@PathVariable String term,
            @RequestParam(value = "center", required = false, defaultValue = "") String center,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "tier", required = false, defaultValue = "") String tiers,
            @RequestParam(value = "maximum", required = false, defaultValue = "") String maximum) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        SubjectResponse.Filter filter = SubjectResponse.createFilter(center, role, tiers, maximum);

        List<SubjectResponse> allSubjects = new ArrayList<SubjectResponse>();
        List<DashboardEntityWithCounts> results = dashboardDao.search(term.toLowerCase());
        for (DashboardEntityWithCounts resultWithCount : results) {
            DashboardEntity result = resultWithCount.getDashboardEntity();
            if (!(result instanceof Subject))
                continue;
            Subject subject = (Subject) result;
            SubjectResponse subjectResponse = SubjectResponse.createInstance(subject, filter, dashboardDao,
                    webServiceUtil);
            allSubjects.add(subjectResponse);
        }

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class).transform(new FieldNameTransformer("class"), "clazz")
                .transform(new FieldNameTransformer("class"), "observations.subject_list.clazz")
                .transform(new FieldNameTransformer("class"), "observations.evidence_list.clazz")
                .transform(new ExcludeTransformer(), void.class);
        String json = "{}";
        try {
            json = jsonSerializer.exclude("class").exclude("observations.class").deepSerialize(allSubjects);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
