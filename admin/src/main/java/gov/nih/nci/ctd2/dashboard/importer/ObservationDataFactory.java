package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import java.util.Date;

public interface ObservationDataFactory {

	Submission createSubmission(String submissionCenterName, Date submissionDate, String observationTemplateName);
	ObservedSubject createObservedSubject(String subjectValue, String columnName, Observation observation, String daoFindQueryName) throws Exception;
	ObservedEvidence createObservedLabelEvidence(String evidenceValue, String columnHeader, Observation observation);
	ObservedEvidence createObservedNumericEvidence(Number evidenceValue, String columnHeader, Observation observation);
	ObservedEvidence createObservedFileEvidence(String evidenceValue, String mimeType, String columnHeader, Observation observation);
	ObservedEvidence createObservedUrlEvidence(String evidenceValue, String columnName, Observation observation);
}
