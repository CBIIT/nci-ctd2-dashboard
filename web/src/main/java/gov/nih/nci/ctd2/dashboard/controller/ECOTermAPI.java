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

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;

@Controller
@RequestMapping("/api/eco")
public class ECOTermAPI {
    private static final Log log = LogFactory.getLog(ECOTermAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "name/{ecocode}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getNameFromCode(@PathVariable String ecocode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        String name = dashboardDao.getEcoTermName(ecocode);
        log.debug("ECO term name: " + name);
        return new ResponseEntity<String>("{\"name\":\"" + name + "\"}", headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "definition/{ecocode}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getDefinitionFromCode(@PathVariable String ecocode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        String definition = dashboardDao.getEcoTermDefinition(ecocode);
        log.debug("ECO term definition: " + definition);
        return new ResponseEntity<String>("{\"definition\":\"" + definition + "\"}", headers, HttpStatus.OK);
    }
}
