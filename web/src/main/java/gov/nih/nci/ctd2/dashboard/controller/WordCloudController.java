package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;
import java.util.Map;

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
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

@Controller
@RequestMapping("/wordcloud")
public class WordCloudController {
    private static final Log log = LogFactory.getLog(SummaryController.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubjectCounts() {
        log.debug("request received");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Map<String, Integer> words = dashboardDao.getSubjectCounts();

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new SimpleDateTransformer(), Date.class).transform(new ExcludeTransformer(), void.class);
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(words);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        log.debug("get subject counts");
        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
