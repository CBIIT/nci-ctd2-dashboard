package gov.nih.nci.ctd2.dashboard.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;

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
    public void scoreAllRoles() {
        log.info("Removing all role-based scores...");
        List<SubjectWithSummaries> oldEntities = dashboardDao.findEntities(SubjectWithSummaries.class);
        for (SubjectWithSummaries subjectWithSummaries : oldEntities) {
            dashboardDao.delete(subjectWithSummaries);
        }
        log.info("Removed " + oldEntities.size() + " old scores.");

        log.info("Re-scoring all roles...");
        List<SubjectWithSummaries> subjectWithSummariesList = new ArrayList<SubjectWithSummaries>();

        List<SubjectRole> entities = dashboardDao.findEntities(SubjectRole.class);
        for (SubjectRole subjectRole : entities) {
            String keyword = subjectRole.getDisplayName();
            log.info("Scoring subject with role: " + keyword);

            HashMap<Subject, SubjectWithSummaries> subjectToSummaries = new HashMap<Subject, SubjectWithSummaries>();
            HashMap<Subject, HashSet<SubmissionCenter>> subjectToCenters = new HashMap<Subject, HashSet<SubmissionCenter>>();
            HashMap<Subject, HashMap<SubmissionCenter, Integer>> centerBasedScores = new HashMap<Subject, HashMap<SubmissionCenter, Integer>>();
            for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectByRole(keyword)) {
                Subject subject = observedSubject.getSubject();
                SubjectWithSummaries withSummaries = subjectToSummaries.get(subject);

                ObservationTemplate observationTemplate = observedSubject.getObservation().getSubmission()
                        .getObservationTemplate();
                SubmissionCenter submissionCenter = observationTemplate.getSubmissionCenter();
                Integer tier = observationTemplate.getTier();
                if (withSummaries == null) {
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
                    withSummaries.setNumberOfObservations(withSummaries.getNumberOfObservations() + 1);

                    HashSet<SubmissionCenter> submissionCenters = subjectToCenters.get(subject);
                    submissionCenters.add(submissionCenter);
                    withSummaries.setNumberOfSubmissionCenters(submissionCenters.size());
                    withSummaries.addSubmission(tier, submissionCenter.getId());

                    HashMap<SubmissionCenter, Integer> cScores = centerBasedScores.get(subject);
                    Integer previousScore = cScores.get(submissionCenter);
                    cScores.put(submissionCenter, previousScore == null ? tier : Math.max(tier, previousScore));
                }
            }

            Collection<SubjectWithSummaries> perRole = subjectToSummaries.values();
            for (SubjectWithSummaries subjectWithSummaries : perRole) {
                Integer totalScore = 0;
                for (Integer aScore : centerBasedScores.get(subjectWithSummaries.getSubject()).values()) {
                    totalScore += aScore;
                }
                subjectWithSummaries.setScore(totalScore);
            }
            subjectWithSummariesList.addAll(perRole);

            log.info("Done scoring role: " + keyword);
        }
        dashboardDao.batchSave(subjectWithSummariesList, 0);

        log.info("Done scoring all roles...");
    }
}
