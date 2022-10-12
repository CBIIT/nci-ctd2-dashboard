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
import org.springframework.web.bind.annotation.PathVariable;
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
    @RequestMapping(value = "{tree_id}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getTree(@PathVariable String tree_id) {
        log.debug("request received by getTree");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Node tree = null;
        if (tree_id.equals("disease-context")) {
            Map<Integer, Integer> count = dashboardDao.tissueSampleCodeToObservationNumber();
            tree = Hierarchy.DISEASE_CONTEXT.getTree(count);
            dashboardDao.setTissueSampleLabels(tree);
        } else if (tree_id.equals("evidence-type")) {
            Map<Integer, Integer> count = dashboardDao.evidenceTypeToObservationNumber();
            tree = Hierarchy.EXPERIMENTAL_EVIDENCE.getTree(count);
            dashboardDao.setEvidenceLabels(tree);
        } else {
            log.warn("not supported tree_id: " + tree_id);
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
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