package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/get")
public class JSONController {
    @Autowired
    private DashboardDao dashboardDao;
    @Autowired
    private DashboardFactory dashboardFactory;

    @Transactional
    @RequestMapping(value="{filter}/{id}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getEntityInJson(@PathVariable String filter, @PathVariable Integer id) {
        DashboardEntity entityById = dashboardDao.getEntityById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        if(entityById == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(
                new JSONSerializer().exclude("*.class").deepSerialize(entityById),
                headers,
                HttpStatus.OK
        );
    }
}