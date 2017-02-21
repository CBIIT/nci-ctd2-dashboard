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
    @Cacheable(value = "entityCache")
    public List<? extends DashboardEntity> getObservationsPerRoleTier(Integer filterBy, String role, Integer tier) {
        List<? extends DashboardEntity> entities = new ArrayList<DashboardEntity>();
        if(filterBy != null) {
            Submission submission = dashboardDao.getEntityById(Submission.class, filterBy);
            if(submission != null) {
                entities = dashboardDao.findObservationsBySubmission(submission);
            } else {
                Subject subject = dashboardDao.getEntityById(Subject.class, filterBy);
                if(subject != null) {
                        ArrayList<Observation> observations = new ArrayList<Observation>();
                        for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectBySubject(subject)) {
                            ObservedSubjectRole observedSubjectRole = observedSubject.getObservedSubjectRole();
                            String subjectRole = observedSubjectRole.getSubjectRole().getDisplayName();
                            Integer observationTier = observedSubject.getObservation().getSubmission().getObservationTemplate().getTier();
                            if( (role.equals("") || role.equals(subjectRole)) && (tier==0 || tier==observationTier) ) {
                                observations.add(observedSubject.getObservation());
                            }
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

    public List<? extends DashboardEntity> getTemplates(Integer centerId) {
        List<SubmissionTemplate> list = new ArrayList<SubmissionTemplate>();
        SubmissionCenter submissionCenter = dashboardDao.getEntityById(SubmissionCenter.class, centerId);
        for (SubmissionTemplate submissionTemplate : dashboardDao.findEntities(SubmissionTemplate.class)) {
            if (submissionTemplate.getSubmissionCenter().equals(submissionCenter)) {
                forceConsistency(submissionTemplate);
                list.add(submissionTemplate);
            }
        }
        return list;
    }

    /* safe-guard the data for the client in case it is inconsistent for some reason */
    private void forceConsistency(SubmissionTemplate submissionTemplate) {
        // TODO complete this for all relevant fields
        String[] subjectColumns = submissionTemplate.getSubjectColumns();
        if(subjectColumns==null) {
            subjectColumns = new String[0];
            submissionTemplate.setSubjectColumns( subjectColumns );
        }
        String[] evidenceColumns = submissionTemplate.getEvidenceColumns();
        if(evidenceColumns==null) {
            evidenceColumns = new String[0];
            submissionTemplate.setEvidenceColumns( evidenceColumns );
        }
        String[] evidenceTypes = submissionTemplate.getEvidenceTypes();
        if(evidenceTypes==null) {
            submissionTemplate.setEvidenceTypes( new String[0] );
        }
        String[] valueTypes = submissionTemplate.getValueTypes();
        if(valueTypes==null) {
            submissionTemplate.setValueTypes( new String[0] );
        }
        String[] evidenceDescription = submissionTemplate.getEvidenceDescriptions();
        if(evidenceDescription==null) {
            submissionTemplate.setEvidenceDescriptions( new String[0] );
        }
        Integer observationNumber = submissionTemplate.getObservationNumber();
        if(observationNumber==null) {
            observationNumber = 0;
            submissionTemplate.setObservationNumber(observationNumber);
        }
        String[] observations = new String[0];
        if(submissionTemplate.getObservations()!=null) {
            observations = submissionTemplate.getObservations().split(",", -1);
        }
        int t = observationNumber*(subjectColumns.length+evidenceColumns.length);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<observations.length; i++) {
            sb.append( observations[i] ).append(",");
        }
        for(int i=observations.length; i<t; i++) { // in case we need more commas
            sb.append(",");
        }
        submissionTemplate.setObservations(sb.toString());
    }
}
