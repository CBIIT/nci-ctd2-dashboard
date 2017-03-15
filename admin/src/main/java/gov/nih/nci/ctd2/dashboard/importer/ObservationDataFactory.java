package gov.nih.nci.ctd2.dashboard.importer;

import gov.nih.nci.ctd2.dashboard.model.*;
import java.util.Date;

public interface ObservationDataFactory {

	Submission createSubmission(String submissionName, Date submissionDate, String observationTemplateName);
	ObservedSubject createObservedSubject(String subjectValue, String columnName, String templateName, Observation observation, String daoFindQueryName) throws Exception;
	ObservedEvidence createObservedLabelEvidence(String evidenceValue, String columnHeader, String templateName, Observation observation);
	ObservedEvidence createObservedNumericEvidence(Number evidenceValue, String columnHeader, String templateName, Observation observation);
	ObservedEvidence createObservedFileEvidence(String evidenceValue, String columnHeader, String templateName, Observation observation);
	ObservedEvidence createObservedUrlEvidence(String evidenceValue, String columnName, String templateName, Observation observation);
}
