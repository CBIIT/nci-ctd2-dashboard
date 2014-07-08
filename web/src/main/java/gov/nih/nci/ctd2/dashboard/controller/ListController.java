package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/list")
public class ListController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value="{type}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getSearchResultsInJson(@PathVariable String type, @RequestParam("filterBy") Integer filterBy) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<? extends DashboardEntity> entities = new ArrayList<DashboardEntity>();
        if(type.equalsIgnoreCase("submission")) {
            if(filterBy != null) {
                SubmissionCenter submissionCenter = dashboardDao.getEntityById(SubmissionCenter.class, filterBy);
                if(submissionCenter != null) {
                    entities = dashboardDao.findSubmissionBySubmissionCenter(submissionCenter);
                }
            } else {
                entities = dashboardDao.findEntities(Submission.class);
            }
        } else if(type.equalsIgnoreCase("observation")) {
            if(filterBy != null) {
                Submission submission = dashboardDao.getEntityById(Submission.class, filterBy);
                if(submission != null) {
                    entities = dashboardDao.findObservationsBySubmission(submission);
                } else {
                    Subject subject = dashboardDao.getEntityById(Subject.class, filterBy);
                    if(subject != null) {
                        ArrayList<Observation> observations = new ArrayList<Observation>();
                        for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectBySubject(subject)) {
                            observations.add(observedSubject.getObservation());
                        }
                        entities = observations;
                    }
                }
            } else {
                entities = dashboardDao.findEntities(Observation.class);
            }
        } else if(type.equals("center")) {
            entities = dashboardDao.findEntities(SubmissionCenter.class);
        } else if(type.equals("observedsubject") && filterBy != null) {
            Subject subject = dashboardDao.getEntityById(Subject.class, filterBy);
            if(subject != null) {
                entities = dashboardDao.findObservedSubjectBySubject(subject);
            } else {
                Observation observation = dashboardDao.getEntityById(Observation.class, filterBy);
                if(observation != null) {
                    entities = dashboardDao.findObservedSubjectByObservation(observation);
                }
            }
        } else if(type.equals("observedevidence") && filterBy != null) {
            Observation observation = dashboardDao.getEntityById(Observation.class, filterBy);
            if(observation != null) {
                entities = dashboardDao.findObservedEvidenceByObservation(observation);
            }
        } else if(type.equals("observationtemplate") && filterBy != null) {
            SubmissionCenter submissionCenter = dashboardDao.getEntityById(SubmissionCenter.class, filterBy);
            if(submissionCenter != null) {
                entities = dashboardDao.findObservationTemplateBySubmissionCenter(submissionCenter);
            }
        }

        // TODO: Remove this and add a pagination option
        if(entities.size() > 500)
            entities = entities.subList(0, 499);

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
        ;

        return new ResponseEntity<String>(
                jsonSerializer.serialize(entities),
                headers,
                HttpStatus.OK
        );
    }
}
