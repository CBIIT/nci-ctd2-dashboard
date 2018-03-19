package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/get")
public class JSONController {
    private static final Log log = LogFactory.getLog(JSONController.class);
    @Autowired
    private DashboardDao dashboardDao;

    private static Set<String> typesWithStableURL = new HashSet<String>();
    static {
        Collections.addAll(typesWithStableURL, new String[] { "center", "animal-model", "cell-sample", "compound",
                "protein", "rna", "tissue", "transcript", "submission", "observation" });
    }

    /* gene needs a separate method because it asks for different number of parameters */
    @Transactional
    @RequestMapping(value = "{type}/{species}/{symbol}", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getGeneInJson(@PathVariable String type, @PathVariable String species,
            @PathVariable String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (!type.equalsIgnoreCase("gene")) {
            log.info("query with wrong type");
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        List<Gene> genes = dashboardDao.findGenesBySymbol(symbol);
        Gene gene = null;
        for (Gene g : genes) {
            if (g.getOrganism().getDisplayName().toLowerCase().startsWith(species)) {
                gene = g;
                break;
            }
        }

        if (gene == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } else {
            JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                    .transform(new DateTransformer(), Date.class);
            return new ResponseEntity<String>(jsonSerializer.deepSerialize(gene), headers, HttpStatus.OK);
        }
    }

    @Transactional
    @RequestMapping(value = "{type}/{id}", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getEntityInJson(@PathVariable String type, @PathVariable String id) {
        DashboardEntity entityById = null;

        Class<? extends DashboardEntity> clazz = Subject.class;
        if (type.equalsIgnoreCase("subject")) {
            clazz = Subject.class;
        } else if (type.equalsIgnoreCase("submission")) {
            clazz = Submission.class;
        } else if (type.equalsIgnoreCase("observation")) {
            clazz = Observation.class;
        } else if (type.equals("observedsubject")) {
            clazz = ObservedSubject.class;
        } else if (type.equals("observedevidence")) {
            clazz = ObservedEvidence.class;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        log.debug("JSONController " + type + " " + id);
        if (typesWithStableURL.contains(type)) {
            entityById = dashboardDao.getEntityByStableURL(type, type + "/" + id);
        } else {
            entityById = dashboardDao.getEntityById(clazz, Integer.parseInt(id));
        }
        if (entityById == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(entityById), headers, HttpStatus.OK);
    }
}
