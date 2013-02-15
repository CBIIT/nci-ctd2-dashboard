package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.ObservationDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Method;

public class ObservationDataFactoryImpl implements ObservationDataFactory {

	private static final Log log = LogFactory.getLog(ObservationDataFactoryImpl.class);

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	private DashboardDao dashboardDao;

	// cache for fast lookup and prevention of duplicate role records
	private HashMap<String, SubmissionCenter> submissionCenterCache = new HashMap<String, SubmissionCenter>();
    private HashMap<String, Subject> subjectCache = new HashMap<String, Subject>();
    private HashMap<String, ObservedSubjectRole> observedSubjectRoleCache = new HashMap<String, ObservedSubjectRole>();
    private HashMap<String, ObservedEvidenceRole> observedEvidenceRoleCache = new HashMap<String, ObservedEvidenceRole>();

	@Override
	public Submission createSubmission(String submissionCenterName, Date submissionDate, String observationTemplateName) {
		SubmissionCenter submissionCenter = submissionCenterCache.get(submissionCenterName);
		if (submissionCenter == null) {
			submissionCenter = dashboardDao.findSubmissionCenterByName(submissionCenterName);
			if (submissionCenter == null) {
				submissionCenter = dashboardFactory.create(SubmissionCenter.class);
				submissionCenter.setDisplayName(submissionCenterName);
			}
			submissionCenterCache.put(submissionCenterName, submissionCenter);
		}
		Submission submission = dashboardFactory.create(Submission.class);
		submission.setSubmissionCenter(submissionCenter);
		submission.setSubmissionDate(submissionDate);
		ObservationTemplate observationTemplate  = dashboardDao.findObservationTemplateByName(observationTemplateName);
		if (observationTemplate != null) {
			submission.setObservationTemplate(observationTemplate);
		}
		return submission;
	}

	@Override
	public ObservedSubject createObservedSubject(String subjectValue, String columnName,
												 Observation observation, String daoFindQueryName) throws Exception {
		ObservedSubject observedSubject = dashboardFactory.create(ObservedSubject.class);
		observedSubject.setDisplayName(subjectValue);
		observedSubject.setObservation(observation);
		Subject subject = subjectCache.get(subjectValue);
		if (subject == null) {
			Method method = dashboardDao.getClass().getMethod(daoFindQueryName, String.class);
			List<Subject> dashboardEntities = (List<Subject>)method.invoke(dashboardDao, subjectValue);
			if (dashboardEntities.size() > 0) {
				subject = dashboardEntities.iterator().next();
				subjectCache.put(subjectValue, subject);
			}
		}
		if (subject != null) observedSubject.setSubject(subject);
		if (subject == null) log.info("Uknown subject: " + subjectValue);
		ObservedSubjectRole observedSubjectRole = observedSubjectRoleCache.get(columnName);
		if (observedSubjectRole == null) {
			observedSubjectRole = dashboardDao.findObservedSubjectRoleByColumnName(columnName);
			if (observedSubjectRole != null) {
				observedSubjectRoleCache.put(columnName, observedSubjectRole);
			}
		}
		if (observedSubjectRole != null) observedSubject.setObservedSubjectRole(observedSubjectRole);
		if (observedSubjectRole == null) log.info("Cannot find observed subject role via column: " + columnName);
		return observedSubject;
	}

	@Override
	public ObservedEvidence createObservedLabelEvidence(String evidenceValue, String columnName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		Evidence evidence = dashboardFactory.create(LabelEvidence.class);
		evidence.setDisplayName(evidenceValue);
		observedEvidence.setEvidence(evidence);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		return observedEvidence;
	}

	@Override
	public ObservedEvidence createObservedNumericEvidence(Number evidenceValue, String columnName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(String.valueOf(evidenceValue));
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(DataNumericValue.class);
		((DataNumericValue)evidence).setNumericValue(evidenceValue);
		if (observedEvidenceRole != null && observedEvidenceRole.getType().length() > 0) {
			((DataNumericValue)evidence).setUnit(observedEvidenceRole.getType());
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}

	@Override
	public ObservedEvidence createObservedFileEvidence(String evidenceValue, String columnName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(FileEvidence.class);
		File file = new File(evidenceValue);

		((FileEvidence)evidence).setFileName(file.getName());
		((FileEvidence)evidence).setFilePath(file.getPath());
		if (observedEvidenceRole != null && observedEvidenceRole.getType().length() > 0) {
			((FileEvidence)evidence).setMimeType(observedEvidenceRole.getType());
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}


	@Override
	public ObservedEvidence createObservedUrlEvidence(String evidenceValue, String columnName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(UrlEvidence.class);
		if (observedEvidenceRole != null && observedEvidenceRole.getType().length() > 0) {
			((UrlEvidence)evidence).setUrl(observedEvidenceRole.getType() + evidenceValue);				
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}

	private ObservedEvidenceRole getObservedEvidenceRole(String columnName) {
		ObservedEvidenceRole observedEvidenceRole = observedEvidenceRoleCache.get(columnName);
		if (observedEvidenceRole == null) {
			observedEvidenceRole = dashboardDao.findObservedEvidenceRoleByColumnName(columnName);
			if (observedEvidenceRole != null) {
				observedEvidenceRoleCache.put(columnName, observedEvidenceRole);
			}
		}
		if (observedEvidenceRole == null) log.info("Cannot find observed evidence role via column: " + columnName);
		return observedEvidenceRole;
	}

}