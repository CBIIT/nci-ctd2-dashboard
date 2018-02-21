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
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/get")
public class JSONController {
    @Autowired
    private DashboardDao dashboardDao;

    private static Map<String, Class<? extends DashboardEntity>> type2class = new HashMap<String, Class<? extends DashboardEntity>>();
    static {
        type2class.put("center", SubmissionCenter.class);
        type2class.put("animal-model", AnimalModel.class);
    }

    /* gene needs a separate method because it asks for different number of parameters */
    @Transactional
    @RequestMapping(value="{type}/{species}/{symbol}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getGeneInJson(@PathVariable String type, @PathVariable String species, @PathVariable String symbol) {
        System.out.println("DEBUG request "+type+" "+species+" "+symbol);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND); // FIXME for test only
    }

    @Transactional
    @RequestMapping(value="{type}/{id}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getEntityInJson(@PathVariable String type, @PathVariable String id) {
        DashboardEntity entityById = null;

        Class<? extends DashboardEntity> clazz = Subject.class;
        if(type.equalsIgnoreCase("subject")) {
            clazz = Subject.class;
        } else if(type.equalsIgnoreCase("submission")) {
            clazz = Submission.class;
        } else if(type.equalsIgnoreCase("observation")) {
            clazz = Observation.class;
        } else if(type.equals("observedsubject")) {
            clazz = ObservedSubject.class;
        } else if(type.equals("observedevidence")) {
            clazz = ObservedEvidence.class;
        } else {
            clazz = type2class.get(type);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        if(type2class.keySet().contains(type)) { // as the early step to implement stable links.
            entityById = dashboardDao.getEntity(clazz, id);
        } else {
            entityById = dashboardDao.getEntityById(clazz, Integer.parseInt(id));
        }
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
