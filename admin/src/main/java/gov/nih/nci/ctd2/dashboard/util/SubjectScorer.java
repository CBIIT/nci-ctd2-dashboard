package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public Integer score(Subject subject) {
        List<ObservedSubject> observedSubjectBySubject = dashboardDao.findObservedSubjectBySubject(subject);
        if(observedSubjectBySubject.isEmpty()) {
            return 0;
        }

        HashSet<Submission> submissions = new HashSet<Submission>();
        HashSet<SubmissionCenter> submissionCenters = new HashSet<SubmissionCenter>();
        for (ObservedSubject observedSubject : observedSubjectBySubject) {
            Observation observation = observedSubject.getObservation();
            Submission submission = observation.getSubmission();
            submissions.add(submission);
            submissionCenters.add(submission.getObservationTemplate().getSubmissionCenter());
        }

        int tierScore = 0;
        for (Submission submission : submissions) {
            tierScore += submission.getObservationTemplate().getTier();
        }

        return tierScore * submissionCenters.size();
    }
}
