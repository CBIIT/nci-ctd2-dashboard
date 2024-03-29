package gov.nih.nci.ctd2.dashboard.controller;

import java.util.ArrayList;
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
import gov.nih.nci.ctd2.dashboard.api.CTD2Serializer;
import gov.nih.nci.ctd2.dashboard.api.SubjectResponse;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.util.SubjectResult;

/* API 2.0 */
@Controller
@RequestMapping("/api/search")
public class SearchAPI {
    private static final Log log = LogFactory.getLog(SearchAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

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
        List<SubjectResult> results = dashboardDao.search(term.toLowerCase()).subject_result;
        for (SubjectResult subjectResult : results) {
            try {
                Class<? extends DashboardEntity> clazz = Class
                        .forName("gov.nih.nci.ctd2.dashboard.model." + subjectResult.className)
                        .asSubclass(DashboardEntity.class);
                ;
                DashboardEntity result = dashboardDao.getEntityById(clazz, subjectResult.id);
                SubjectResponse subjectResponse = SubjectResponse.createInstance(result, filter, dashboardDao);
                if (subjectResponse == null) {
                    continue;
                }
                allSubjects.add(subjectResponse);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = CTD2Serializer.createJSONSerializer();
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(allSubjects);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
