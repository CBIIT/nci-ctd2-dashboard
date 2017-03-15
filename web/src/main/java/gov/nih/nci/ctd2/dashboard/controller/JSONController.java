package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
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

import java.util.Date;

@Controller
@RequestMapping("/get")
public class JSONController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value="{type}/{id}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getEntityInJson(@PathVariable String type, @PathVariable Integer id) {
        DashboardEntity entityById = null;

        Class<? extends DashboardEntity> clazz = Subject.class;
        if(type.equalsIgnoreCase("subject")) {
            clazz = Subject.class;
        } else if(type.equalsIgnoreCase("submission")) {
            clazz = Submission.class;
        } else if(type.equalsIgnoreCase("observation")) {
            clazz = Observation.class;
        } else if(type.equalsIgnoreCase("center")) {
            clazz = SubmissionCenter.class;
        } else if(type.equals("observedsubject")) {
            clazz = ObservedSubject.class;
        } else if(type.equals("observedevidence")) {
            clazz = ObservedEvidence.class;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        entityById = dashboardDao.getEntityById(clazz, id);
        if(entityById == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                ;
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(entityById),
                headers,
                HttpStatus.OK
        );
    }
}