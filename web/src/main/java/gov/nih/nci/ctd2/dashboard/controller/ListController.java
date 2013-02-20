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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/list")
public class ListController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value="{type}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getSearchResultsInJson(@PathVariable String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<? extends DashboardEntity> entities;
        if(type.equalsIgnoreCase("submission")) {
            entities = dashboardDao.findEntities(Submission.class);
        } else if(type.equalsIgnoreCase("observation")) {
            entities = dashboardDao.findEntities(Observation.class);
        } else if(type.equals("center")) {
            entities = dashboardDao.findEntities(SubmissionCenter.class);
        } else {
            entities = new ArrayList<DashboardEntity>();
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class);
        return new ResponseEntity<String>(
                jsonSerializer.serialize(entities),
                headers,
                HttpStatus.OK
        );
    }
}
