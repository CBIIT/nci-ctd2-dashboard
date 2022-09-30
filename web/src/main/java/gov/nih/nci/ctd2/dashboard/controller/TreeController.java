package gov.nih.nci.ctd2.dashboard.controller;

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
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.Hierarchy;
import gov.nih.nci.ctd2.dashboard.util.Node;

@Controller
@RequestMapping("/tree")
public class TreeController {
    private static final Log log = LogFactory.getLog(TreeController.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "disease-context", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getDiseaseContextTree() {
        log.debug("request received by getDiseaseContextTree ");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Map<Integer, Integer> count = dashboardDao.tissueSampleCodeToObservationNumber();
        Node tree = Hierarchy.DISEASE_CONTEXT.getTree(count);
        dashboardDao.setTissueSampleLabels(tree);
        JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "evidence-type", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getEvidenceTypeTree() {
        log.debug("request received by getEvidenceTypeTree ");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Map<Integer, Integer> count = dashboardDao.evidenceTypeToObservationNumber();
        Node tree = Hierarchy.EXPERIMENTAL_EVIDENCE.getTree(count);
        dashboardDao.setEvidenceLabels(tree);
        JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}