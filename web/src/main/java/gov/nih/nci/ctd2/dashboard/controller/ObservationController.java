package gov.nih.nci.ctd2.dashboard.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

@Controller
@RequestMapping("/observations")
public class ObservationController {
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    @Qualifier("maxNumberOfEntities")
    private Integer maxNumberOfEntities = 100;

    public Integer getMaxNumberOfEntities() {
        return maxNumberOfEntities;
    }

    public void setMaxNumberOfEntities(Integer maxNumberOfEntities) {
        this.maxNumberOfEntities = maxNumberOfEntities;
    }

    private List<Observation> getBySubmissionId(Integer submissionId) {
        Submission submission = dashboardDao.getEntityById(Submission.class, submissionId);
        if (submission != null) {
            return dashboardDao.findObservationsBySubmission(submission);
        } else {
            return new ArrayList<Observation>();
        }
    }

    private List<Observation> getBySubjectId(Integer subjectId, String role, Integer tier) {
        Subject subject = dashboardDao.getEntityById(Subject.class, subjectId);
        if (subject != null) {
            Set<Observation> observations = new HashSet<Observation>();
            for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectBySubject(subject)) {
                ObservedSubjectRole observedSubjectRole = observedSubject.getObservedSubjectRole();
                String subjectRole = observedSubjectRole.getSubjectRole().getDisplayName();
                Integer observationTier = observedSubject.getObservation().getSubmission().getObservationTemplate()
                        .getTier();
                if ((role.equals("") || role.equals(subjectRole)) && (tier == 0 || tier == observationTier)) {
                    observations.add(observedSubject.getObservation());
                }
            }
            List<Observation> list = new ArrayList<Observation>(observations);
            Collections.sort(list, new Comparator<Observation>() {
                @Override
                public int compare(Observation o1, Observation o2) {
                    Integer tier2 = o2.getSubmission().getObservationTemplate().getTier();
                    Integer tier1 = o1.getSubmission().getObservationTemplate().getTier();
                    return tier2 - tier1;
                }
            });

            return list;
        } else {
            return new ArrayList<Observation>();
        }
    }

    private List<ObservationWithCount> onePerSubmissionBySubjectId(Integer subjectId, String role, Integer tier) {
        Subject subject = dashboardDao.getEntityById(Subject.class, subjectId);
        if (subject != null) {
            Map<Integer, Integer> submissionIds = new HashMap<Integer, Integer>();
            Map<Integer, Observation> observations = new HashMap<Integer, Observation>();
            for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectBySubject(subject)) {
                Observation observation = observedSubject.getObservation();
                Submission submission = observation.getSubmission();
                Integer submissionId = submission.getId();

                ObservedSubjectRole observedSubjectRole = observedSubject.getObservedSubjectRole();
                String subjectRole = observedSubjectRole.getSubjectRole().getDisplayName();
                Integer observationTier = observedSubject.getObservation().getSubmission().getObservationTemplate()
                        .getTier();
                if ((role.equals("") || role.equals(subjectRole)) && (tier == 0 || tier == observationTier)) {
                    if (!submissionIds.containsKey(submissionId)) {
                        submissionIds.put(submissionId, 1);
                        observations.put(submissionId, observation);
                    } else {
                        submissionIds.put(submissionId, submissionIds.get(submissionId) + 1);
                    }
                }
            }
            List<ObservationWithCount> list = new ArrayList<ObservationWithCount>();
            for (Integer submissionId : observations.keySet()) {
                Observation obs = observations.get(submissionId);
                Integer count = submissionIds.get(submissionId);
                list.add(new ObservationWithCount(obs, count));
            }
            Collections.sort(list, new Comparator<ObservationWithCount>() {
                @Override
                public int compare(ObservationWithCount o1, ObservationWithCount o2) {
                    Integer tier2 = o2.observation.getSubmission().getObservationTemplate().getTier();
                    Integer tier1 = o1.observation.getSubmission().getObservationTemplate().getTier();
                    return tier2 - tier1;
                }
            });

            return list;
        } else {
            return new ArrayList<ObservationWithCount>();
        }
    }

    @Transactional
    @RequestMapping(value = "countBySubmission", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> countBySubmissionId(@RequestParam("submissionId") Integer submissionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<? extends DashboardEntity> entities = getBySubmissionId(submissionId);

        return new ResponseEntity<String>("" + entities.size(), headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "countBySubject", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> countBySubjectId(@RequestParam("subjectId") Integer subjectId,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "tier", required = false, defaultValue = "0") Integer tier) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<? extends DashboardEntity> entities = getBySubjectId(subjectId, role, tier);
        return new ResponseEntity<String>("" + entities.size(), headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "bySubmission", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getObservationsBySubmissionId(@RequestParam("submissionId") Integer submissionId,

            @RequestParam(value = "getAll", required = false, defaultValue = "false") Boolean getAll) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<? extends DashboardEntity> entities = getBySubmissionId(submissionId);
        if (!getAll && entities.size() > getMaxNumberOfEntities()) {
            entities = entities.subList(0, getMaxNumberOfEntities());
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(entities), headers, HttpStatus.OK);
    }

    /*
     * For a given submission, tier is decided so there is point of further
     * specifiying tier.
     */
    @Transactional
    @RequestMapping(value = "bySubmissionAndSubject", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getObservationsBySubmissionIdAndSubjuectId(
            @RequestParam("submissionId") Integer submissionId, @RequestParam("subjectId") Integer subjectId,
            @RequestParam(value = "role", required = false, defaultValue = "") String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Date time1 = new Date();
        String summaryTemplate = null;

        Set<Observation> observations = new HashSet<Observation>();
        Subject subject = dashboardDao.getEntityById(Subject.class, subjectId);
        for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectBySubject(subject)) {
            Observation observation = observedSubject.getObservation();
            Submission submission = observation.getSubmission();
            if (!submission.getId().equals(submissionId)) {
                continue;
            } else if (summaryTemplate == null) {
                summaryTemplate = submission.getObservationTemplate().getObservationSummary();
            }
            String subjectRole = observedSubject.getObservedSubjectRole().getSubjectRole().getDisplayName();
            if ((role.equals("") || role.equals(subjectRole))) {
                observations.add(observation);
            }
        }

        List<Observation> list = new ArrayList<Observation>(observations);

        Date time2 = new Date();
        System.out.println((time2.getTime() - time1.getTime()) / 1000 + " seconds to get 'obervations with summary'");

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(list), headers, HttpStatus.OK);
    }

    static final class SummaryText {
        public SummaryText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        private String text;
    }

    @Transactional
    @RequestMapping(value = "expandedsummary/{observationId}", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getExpandedSummary(@PathVariable String observationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Integer id = Integer.parseInt(observationId);
        Observation observation = dashboardDao.getEntityById(Observation.class, id);
        String summaryTemplate = observation.getSubmission().getObservationTemplate().getObservationSummary();

        String expanded = dashboardDao.expandSummary(id, summaryTemplate) + " (<a class='button-link' href='#"
                + observation.getStableURL() + "'>details &raquo;</a>)";

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(new SummaryText(expanded)), headers, HttpStatus.OK);
    }

    static final class ObservationWithCount {
        public ObservationWithCount(Observation observation, int count) {
            this.observation = observation;
            this.count = count;
        }

        public Observation getObservation() {
            return observation;
        }

        public Integer getCount() {
            return count;
        }

        final private Observation observation;
        final private Integer count;
    }

    @Transactional
    @RequestMapping(value = "onePerSubmissionBySubject", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getOneObservationsPerSubmission(@RequestParam("subjectId") Integer subjectId,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "tier", required = false, defaultValue = "0") Integer tier) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<ObservationWithCount> list = null;
        if (tier > 0 || role.trim().length() > 0) {
            list = onePerSubmissionBySubjectId(subjectId, role, tier);
        } else {
            Map<Observation, BigInteger> observationAndCount = dashboardDao.getOneObservationPerSubmission(subjectId);
            list = new ArrayList<ObservationWithCount>();
            for (Observation observation : observationAndCount.keySet()) {
                list.add(new ObservationWithCount(observation, observationAndCount.get(observation).intValue()));
            }
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(list), headers, HttpStatus.OK);
    }
}
