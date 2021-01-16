package gov.nih.nci.ctd2.dashboard.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.DashboardEntityWithCounts;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

@Controller
@RequestMapping("/ontology-search")
public class OntologySearchController {
    private static final Log log = LogFactory.getLog(OntologySearchController.class);

    @Autowired
    private DashboardDao dashboardDao;

    @RequestMapping(method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> ontologySearch(@RequestParam("terms") String terms) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<DashboardEntityWithCounts> ontologyResult = dashboardDao.ontologySearch(terms);
        log.debug("result list size=" + ontologyResult.size());
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(ontologyResult), headers, HttpStatus.OK);
    }
}
