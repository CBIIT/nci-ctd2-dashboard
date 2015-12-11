package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SubjectScorer {
    private static Log log = LogFactory.getLog(SubjectScorer.class);

    @Autowired
    private DashboardDao dashboardDao;

    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    @Transactional
    public void score() {
        score(true);
    }

    @Transactional
    public void score(boolean optimized) {
        List<Subject> entities = new ArrayList<Subject>();

        if(optimized) {
            log.info("Optimized scoring: working only on the subjects that have some observation to them...");
            HashSet<Subject> subjects = new HashSet<Subject>();
            List<ObservedSubject> observedSubjects = dashboardDao.findEntities(ObservedSubject.class);
            for (ObservedSubject observedSubject : observedSubjects) {
                subjects.add(observedSubject.getSubject());
            }

            entities.addAll(subjects);
        } else {
            log.info("Scoring all subjects in the database -- not optimized...");
            entities.addAll(dashboardDao.findEntities(Subject.class));
        }

        log.info("Scoring " + entities.size() + " subjects...");
        for(int i=0; i < entities.size(); i++) {
            Subject subject = entities.get(i);
            subject.setScore(score(subject));
            dashboardDao.merge(subject);

            if(i % 1000 == 0) {
                log.info("Done with scoring " + i + "/" + entities.size());
            }
        }
        log.info("Scoring is done...");
    }

    /*
    PROPOSED SORT ORDER
        for subjects:
        (1) sum over Centers of (top Tier per Center)
        (2) top Tier overall
        (3) number of observations
        (4) alpha by subject name
     */
    @Transactional
    public Integer score(Subject subject) {
        List<ObservedSubject> observedSubjectBySubject = dashboardDao.findObservedSubjectBySubject(subject);
        if(observedSubjectBySubject.isEmpty()) {
            return 0;
        }

        HashMap<SubmissionCenter, Integer> submissionScores = new HashMap<SubmissionCenter, Integer>();
        HashSet<Submission> submissions = new HashSet<Submission>();
        for (ObservedSubject observedSubject : observedSubjectBySubject) {
            Observation observation = observedSubject.getObservation();
            Submission submission = observation.getSubmission();
            submissions.add(submission);
            SubmissionCenter submissionCenter = submission.getObservationTemplate().getSubmissionCenter();
            Integer tier = submission.getObservationTemplate().getTier();

            Integer earlierScore = submissionScores.get(submissionCenter);
            submissionScores.put(submissionCenter, earlierScore == null ? tier : Math.max(tier, earlierScore));
        }

        int totalScore = 0;
        for (Integer centerScores : submissionScores.values()) {
            totalScore += centerScores;
        }

        return totalScore;
    }

    @Transactional
    public void scoreAllRoles() {
        log.info("Removing all role-based scores...");
        List<SubjectWithSummaries> oldEntities = dashboardDao.findEntities(SubjectWithSummaries.class);
        for (SubjectWithSummaries subjectWithSummaries : oldEntities) {
            dashboardDao.delete(subjectWithSummaries);
        }
        log.info("Removed " + oldEntities.size() + " old scores.");

        log.info("Re-scoring all roles...");
        List<SubjectRole> entities = dashboardDao.findEntities(SubjectRole.class);
        for (SubjectRole subjectRole : entities) {
            String keyword = subjectRole.getDisplayName();
            log.info("Scoring subject with role: " + keyword);

            HashMap<Subject, SubjectWithSummaries> subjectToSummaries = new HashMap<Subject, SubjectWithSummaries>();
            HashMap<Subject, HashSet<SubmissionCenter>> subjectToCenters = new HashMap<Subject, HashSet<SubmissionCenter>>();
            HashMap<Subject, HashMap<SubmissionCenter, Integer>> centerBasedScores
                    = new HashMap<Subject, HashMap<SubmissionCenter, Integer>>();
            for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectByRole(keyword)) {
                Subject subject = observedSubject.getSubject();
                SubjectWithSummaries withSummaries = subjectToSummaries.get(subject);

                ObservationTemplate observationTemplate = observedSubject.getObservation().getSubmission().getObservationTemplate();
                SubmissionCenter submissionCenter = observationTemplate.getSubmissionCenter();
                Integer tier = observationTemplate.getTier();
                if(withSummaries == null) {
                    withSummaries = new SubjectWithSummaries();
                    withSummaries.setRole(keyword);
                    withSummaries.setSubject(subject);
                    withSummaries.setMaxTier(tier);
                    withSummaries.setNumberOfObservations(1);
                    HashSet<SubmissionCenter> centers = new HashSet<SubmissionCenter>();
                    centers.add(submissionCenter);
                    withSummaries.setNumberOfSubmissionCenters(1);
                    withSummaries.addSubmission(tier, submissionCenter.getId());
                    subjectToCenters.put(subject, centers);
                    subjectToSummaries.put(subject, withSummaries);
                    HashMap<SubmissionCenter, Integer> cScores = new HashMap<>();
                    cScores.put(submissionCenter, tier);
                    centerBasedScores.put(subject, cScores);
                } else {
                    withSummaries.setMaxTier(Math.max(withSummaries.getMaxTier(), tier));
                    withSummaries.setNumberOfObservations(withSummaries.getNumberOfObservations()+1);

                    HashSet<SubmissionCenter> submissionCenters = subjectToCenters.get(subject);
                    submissionCenters.add(submissionCenter);
                    withSummaries.setNumberOfSubmissionCenters(submissionCenters.size());
                    withSummaries.addSubmission(tier, submissionCenter.getId());

                    HashMap<SubmissionCenter, Integer> cScores = centerBasedScores.get(subject);
                    Integer previousScore = cScores.get(submissionCenter);
                    cScores.put(submissionCenter, previousScore == null ? tier : Math.max(tier, previousScore));
                }
            }

            List<SubjectWithSummaries> subjectWithSummariesList = new ArrayList<SubjectWithSummaries>();
            subjectWithSummariesList.addAll(subjectToSummaries.values());
            for (SubjectWithSummaries subjectWithSummaries : subjectWithSummariesList) {
                Integer totalScore = 0;
                for (Integer aScore : centerBasedScores.get(subjectWithSummaries.getSubject()).values()) {
                    totalScore += aScore;
                }
                subjectWithSummaries.setScore(totalScore);
                dashboardDao.save(subjectWithSummaries);
            }

            log.info("Done scoring role: " + keyword);
        }

        log.info("Done scoring all roles...");
    }
}
