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
import gov.nih.nci.ctd2.dashboard.api.FieldNameTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;

@Controller
@RequestMapping("/api/eco")
public class ECOTermAPI {
    private static final Log log = LogFactory.getLog(ECOTermAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "{ecocode}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getECOTermFromCode(@PathVariable String ecocode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        ECOTerm ecoterm = dashboardDao.getEcoTerm(ecocode);
        if (ecoterm == null) {
            return new ResponseEntity<String>("{\"name\":\"(not available)\"}", headers, HttpStatus.OK);
        }
        log.debug("ECO term name: " + ecoterm);
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new FieldNameTransformer("name"), "displayName");// CTD2Serializer.createJSONSerializer();
        String json = jsonSerializer.deepSerialize(ecoterm);

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
