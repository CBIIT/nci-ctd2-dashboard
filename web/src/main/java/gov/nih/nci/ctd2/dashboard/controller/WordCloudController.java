package gov.nih.nci.ctd2.dashboard.controller;

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

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.WordCloudEntry;

@Controller
@RequestMapping("/wordcloud")
public class WordCloudController {
    private static final Log log = LogFactory.getLog(WordCloudController.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubjectCounts() {
        log.debug("request received");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        WordCloudEntry[] words = dashboardDao.getSubjectCounts();

        JSONSerializer jsonSerializer = new JSONSerializer().exclude("class");
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

    @Transactional
    @RequestMapping(value = "{roles}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubjectCountsForRoles(@PathVariable String roles) {
        log.debug("request received for roles: " + roles);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        String[] role_array = roles.split(",");
        WordCloudEntry[] words = new WordCloudEntry[0];
        if (role_array.length > 0) {
            words = dashboardDao.getSubjectCountsForRoles(role_array);
        } else {
            words = dashboardDao.getSubjectCounts();
        }

        JSONSerializer jsonSerializer = new JSONSerializer().exclude("class");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(words);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "subject/{subject_id}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getCountsForSubject(@PathVariable Integer subject_id) {
        log.debug("request received for subject id: " + subject_id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        WordCloudEntry[] words = dashboardDao.getSubjectCounts(subject_id);

        JSONSerializer jsonSerializer = new JSONSerializer().exclude("class");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(words);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
