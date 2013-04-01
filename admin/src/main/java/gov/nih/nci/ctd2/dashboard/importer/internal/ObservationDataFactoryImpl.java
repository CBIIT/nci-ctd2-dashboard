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
	public ObservedSubject createObservedSubject(String subjectValue, String columnName, String templateName,
												 Observation observation, String daoFindQueryName) throws Exception {
		ObservedSubject observedSubject = dashboardFactory.create(ObservedSubject.class);
		observedSubject.setDisplayName(subjectValue);
		observedSubject.setObservation(observation);
		Subject subject = subjectCache.get(subjectValue);
		if (subject == null) {
			List<Subject> dashboardEntities = null;
			if (daoFindQueryName.contains("findSubjectsBySynonym")) {
				Method method = dashboardDao.getClass().getMethod(daoFindQueryName, String.class, Boolean.TYPE);
				dashboardEntities = (List<Subject>)method.invoke(dashboardDao, subjectValue, true);
			}
			else {
				Method method = dashboardDao.getClass().getMethod(daoFindQueryName, String.class);
				dashboardEntities = (List<Subject>)method.invoke(dashboardDao, subjectValue);
			}
			if (dashboardEntities.size() > 0) {
				subject = dashboardEntities.iterator().next();
				subjectCache.put(subjectValue, subject);
			}
		}
		if (subject != null) observedSubject.setSubject(subject);
		if (subject == null) {
			log.info("******");
			log.info("Uknown subject value: " + subjectValue);
			log.info("columnName: " + columnName);
			log.info("templateName: " + templateName);
			log.info("query method: " + daoFindQueryName);
		}
		ObservedSubjectRole observedSubjectRole = observedSubjectRoleCache.get(columnName);
		if (observedSubjectRole == null) {
			observedSubjectRole = dashboardDao.findObservedSubjectRole(templateName, columnName);
			if (observedSubjectRole != null) {
				observedSubjectRoleCache.put(columnName, observedSubjectRole);
			}
		}
		if (observedSubjectRole != null) observedSubject.setObservedSubjectRole(observedSubjectRole);
		if (observedSubjectRole == null) log.info("Cannot find observed subject role via column: " + columnName);
		return observedSubject;
	}

	@Override
	public ObservedEvidence createObservedLabelEvidence(String evidenceValue, String columnName,
														String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		Evidence evidence = dashboardFactory.create(LabelEvidence.class);
		evidence.setDisplayName(evidenceValue);
		observedEvidence.setEvidence(evidence);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		return observedEvidence;
	}

	@Override
	public ObservedEvidence createObservedNumericEvidence(Number evidenceValue, String columnName,
														  String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(String.valueOf(evidenceValue));
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(DataNumericValue.class);
		((DataNumericValue)evidence).setNumericValue(evidenceValue);
		if (observedEvidenceRole != null && observedEvidenceRole.getAttribute().length() > 0) {
			((DataNumericValue)evidence).setUnit(observedEvidenceRole.getAttribute());
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}

	@Override
	public ObservedEvidence createObservedFileEvidence(String evidenceValue, String columnName,
													   String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(FileEvidence.class);
		File file = new File(evidenceValue);

		((FileEvidence)evidence).setFileName(file.getName());
		((FileEvidence)evidence).setFilePath(file.getPath());
		if (observedEvidenceRole != null && observedEvidenceRole.getAttribute().length() > 0) {
			((FileEvidence)evidence).setMimeType(observedEvidenceRole.getAttribute());
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}


	@Override
	public ObservedEvidence createObservedUrlEvidence(String evidenceValue, String columnName,
													  String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(UrlEvidence.class);
		((UrlEvidence)evidence).setUrl(evidenceValue);				
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}

	private ObservedEvidenceRole getObservedEvidenceRole(String templateName, String columnName) {
		ObservedEvidenceRole observedEvidenceRole = observedEvidenceRoleCache.get(columnName);
		if (observedEvidenceRole == null) {
			observedEvidenceRole = dashboardDao.findObservedEvidenceRole(templateName, columnName);
			if (observedEvidenceRole != null) {
				observedEvidenceRoleCache.put(columnName, observedEvidenceRole);
			}
		}
		if (observedEvidenceRole == null) log.info("Cannot find observed evidence role via column: " + columnName);
		return observedEvidenceRole;
	}

}