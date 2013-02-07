package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
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

    @Transactional
    @RequestMapping(value="{id}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getEntityInJson(@PathVariable Integer id) {
        DashboardEntity entityById = dashboardDao.getEntityById(Subject.class, id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        if(entityById == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class);
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(entityById),
                headers,
                HttpStatus.OK
        );
    }
}