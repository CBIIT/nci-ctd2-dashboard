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

@Controller
@RequestMapping("/related-compounds")
public class RelatedCompoundsController {
    private static final Log log = LogFactory.getLog(RelatedCompoundsController.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "{ctrpID}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubjectCountsForRoles(@PathVariable Integer ctrpID) {
        log.debug("request received for ctrpID " + ctrpID);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        String json = "{}";
        try {
            String[] related = dashboardDao.getRelatedCompounds(ctrpID);
            JSONSerializer jsonSerializer = new JSONSerializer().exclude("class");
            json = jsonSerializer.deepSerialize(related);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
