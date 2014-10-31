package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class WebServiceUtil {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @Cacheable(value = "entityCache")
    public List<? extends DashboardEntity> getDashboardEntities(String type, Integer filterBy) {
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
                        Collections.sort(observations, new Comparator<Observation>() {
                            @Override
                            public int compare(Observation o1, Observation o2) {
                                Integer tier2 = o2.getSubmission().getObservationTemplate().getTier();
                                Integer tier1 = o1.getSubmission().getObservationTemplate().getTier();
                                return tier2 - tier1;
                            }
                        });
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
        return entities;
    }

    @Transactional
    @Cacheable(value = "exploreCache")
    public List<SubjectWithSummaries> exploreSubjects(String keyword) {
        HashSet<Subject> subjects = new HashSet<Subject>();
        for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectByRole(keyword)) {
            subjects.add(observedSubject.getSubject());
        }

        List<SubjectWithSummaries> subjectWithSummariesList = new ArrayList<SubjectWithSummaries>();
        for (Subject subject : subjects) {
            SubjectWithSummaries subjectWithSummaries = new SubjectWithSummaries();
            subjectWithSummaries.setSubject(subject);

            List<ObservedSubject> observedSubjectBySubject = dashboardDao.findObservedSubjectBySubject(subject);
            subjectWithSummaries.setNumberOfObservations(observedSubjectBySubject.size());

            HashSet<Submission> submissions = new HashSet<Submission>();
            HashSet<SubmissionCenter>  submissionCenters = new HashSet<SubmissionCenter>();
            Integer maxTier = 0;
            for (ObservedSubject observedSubject : observedSubjectBySubject) {
                Submission submission = observedSubject.getObservation().getSubmission();
                submissions.add(submission);
                ObservationTemplate observationTemplate = submission.getObservationTemplate();
                submissionCenters.add(observationTemplate.getSubmissionCenter());
                maxTier = Math.max(maxTier, observationTemplate.getTier());
            }

            subjectWithSummaries.setMaxTier(maxTier);
            subjectWithSummaries.setNumberOfSubmissions(submissions.size());
            subjectWithSummaries.setNumberOfSubmissionCenters(submissionCenters.size());
            subjectWithSummariesList.add(subjectWithSummaries);

            subjectWithSummaries.setRole(keyword);
        }

        return subjectWithSummariesList;
    }
}
