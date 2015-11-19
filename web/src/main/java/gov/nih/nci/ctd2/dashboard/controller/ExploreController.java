package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.SubjectWithSummaries;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
@RequestMapping("/explore")
public class ExploreController {
    @Autowired
    private DashboardDao dashboardDao;

    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    @Autowired
    private WebServiceUtil webServiceUtil;

    public WebServiceUtil getWebServiceUtil() {
        return webServiceUtil;
    }

    public void setWebServiceUtil(WebServiceUtil webServiceUtil) {
        this.webServiceUtil = webServiceUtil;
    }

    @Transactional
    @RequestMapping(value = "{roles}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> browseByKeyword(@PathVariable String roles) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<SubjectWithSummaries> entities = new ArrayList<SubjectWithSummaries>();
        for (String s : roles.split(",")) {
            String role = s.trim().toLowerCase();
            entities.addAll(getWebServiceUtil().exploreSubjects(role));
        }

        Collections.sort(entities, new Comparator<SubjectWithSummaries>() {
            @Override
            public int compare(SubjectWithSummaries o1, SubjectWithSummaries o2) {
                int i = o2.getScore() - o1.getScore();
                if(i == 0) {
                    int j = o2.getMaxTier() - o1.getMaxTier();
                    if(j == 0) {
                        return o2.getNumberOfObservations() - o1.getNumberOfObservations();
                    } else {
                        return j;
                    }
                } else {
                    return i;
                }
            }
        });

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                ;
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(entities),
                headers,
                HttpStatus.OK
        );
    }
}
