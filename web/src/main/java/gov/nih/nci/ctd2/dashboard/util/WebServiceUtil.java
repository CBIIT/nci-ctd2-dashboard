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
        } else if(type.equals("role")) {
            List<SubjectRole> sRoles = dashboardDao.findEntities(SubjectRole.class);
            Collections.sort(sRoles, new Comparator<SubjectRole>() {
                @Override
                public int compare(SubjectRole o1, SubjectRole o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
            entities = sRoles;
        }

        return entities;
    }

    @Transactional
    @Cacheable(value = "exploreCache")
    public List<SubjectWithSummaries> exploreSubjects(String keyword) {
        return dashboardDao.findSubjectWithSummariesByRole(keyword, 1);
    }

    @Transactional
    @Cacheable(value = "similarCache")
    public List<Submission> getSimilarSubmissions(Integer submissionId) {
        ArrayList<Submission> submissions = new ArrayList<Submission>();
        Submission seedSubmission = dashboardDao.getEntityById(Submission.class, submissionId);
        String seedProject = seedSubmission.getObservationTemplate().getProject();

        SubmissionCenter submissionCenter = seedSubmission.getObservationTemplate().getSubmissionCenter();
        for (Submission submission : dashboardDao.findSubmissionBySubmissionCenter(submissionCenter)) {
            if(submission.getObservationTemplate().getProject().equals(seedProject)
                    && !submission.equals(seedSubmission)) {
                submissions.add(submission);
            }
        }

        return submissions;
    }
}
