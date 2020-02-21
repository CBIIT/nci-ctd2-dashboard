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

    static final class ObservationWithSummary {
        public ObservationWithSummary(Observation observation, String summary) {
            this.observation = observation;
            this.summary = summary;
        }

        public Observation getObservation() {
            return observation;
        }

        public String getSummary() {
            return summary;
        }

        final private Observation observation;
        final private String summary;
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

        List<ObservationWithSummary> list = new ArrayList<ObservationWithSummary>();
        for (Observation observation : observations) {
            String expanded = dashboardDao.expandSummary(observation.getId(), summaryTemplate)
                    + " (<a class='button-link' href='#" + observation.getStableURL() + "'>details &raquo;</a>)";
            list.add(new ObservationWithSummary(observation, expanded));
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(list), headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "bySubmissionAndEcoTerm", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getObservationsBySubmissionIdAndEcoTerm(
            @RequestParam("submissionId") Integer submissionId, @RequestParam("ecocode") String ecocode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        String summaryTemplate = dashboardDao.getEntityById(Submission.class, submissionId).getObservationTemplate()
                .getObservationSummary();

        List<Observation> observations = dashboardDao.getObservationsForSubmissionAndEcoCode(submissionId, ecocode);
        List<ObservationWithSummary> list = new ArrayList<ObservationWithSummary>();
        for (Observation observation : observations) {
            String expanded = dashboardDao.expandSummary(observation.getId(), summaryTemplate)
                    + " (<a class='button-link' href='#" + observation.getStableURL() + "'>details &raquo;</a>)";
            list.add(new ObservationWithSummary(observation, expanded));
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(list), headers, HttpStatus.OK);
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

    @Transactional
    @RequestMapping(value = "onePerSubmissionByEcoTerm", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getOneObservationsPerSubmissionByECOTerm(@RequestParam("ecocode") String ecocode,
            @RequestParam(value = "tier", required = false, defaultValue = "0") Integer tier) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Map<Observation, BigInteger> observationAndCount = dashboardDao.getOneObservationPerSubmissionByEcoCode(ecocode,
                tier);
        List<ObservationWithCount> list = new ArrayList<ObservationWithCount>();
        for (Observation observation : observationAndCount.keySet()) {
            list.add(new ObservationWithCount(observation, observationAndCount.get(observation).intValue()));
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);

        return new ResponseEntity<String>(jsonSerializer.serialize(list), headers, HttpStatus.OK);
    }
}
