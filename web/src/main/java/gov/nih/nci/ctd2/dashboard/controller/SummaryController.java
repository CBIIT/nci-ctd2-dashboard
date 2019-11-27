package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.ExcludeTransformer;
import gov.nih.nci.ctd2.dashboard.api.SimpleDateTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

import gov.nih.nci.ctd2.dashboard.dao.Summary;
import gov.nih.nci.ctd2.dashboard.model.*;

@Controller
@RequestMapping("/api/summary")
/*
 * this is not part of API. Using the URL format so to be consistent with
 * RESTful style.
 */
public class SummaryController {
    private static final Log log = LogFactory.getLog(SummaryController.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getCenters() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Class<?>[] summaryClasses = new Class<?>[] { AnimalModel.class, CellSample.class, Compound.class, Gene.class,
                ShRna.class };
        List<Summary> summary = new ArrayList<Summary>();
        for (Class<?> c : summaryClasses) {
            summary.add(dashboardDao.getSummaryPerSubject((Class<? extends Subject>) c));
        }
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new SimpleDateTransformer(), Date.class).transform(new ExcludeTransformer(), void.class);
        String json = "{}";
        try {
            json = jsonSerializer.exclude("class").exclude("submissions.class")
                    .deepSerialize(summary.toArray(new Summary[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
