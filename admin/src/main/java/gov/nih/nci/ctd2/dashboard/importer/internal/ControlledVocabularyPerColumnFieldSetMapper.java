package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.EvidenceRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;

@Component("controlledVocabularyPerColumnMapper")
public class ControlledVocabularyPerColumnFieldSetMapper implements FieldSetMapper<ControlledVocabulary> {

	private static final String TEMPLATE_NAME = "template_name";
	private static final String COLUMN_NAME = "column_name";
	private static final String SUBJECT = "subject";
	private static final String EVIDENCE = "evidence";
	private static final String ROLE = "role";
	private static final String MIME_TYPE = "mime_type";
	private static final String NUMERIC_UNITS = "numeric_units";
	private static final String DISPLAY_TEXT = "display_text";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("observationTemplateMap")
	private HashMap<String,ObservationTemplate> observationTemplateMap;

	// cache for fast lookup and prevention of duplicate role records
    private HashMap<String, SubjectRole> subjectRoleCache = new HashMap<String, SubjectRole>();
    private HashMap<String, EvidenceRole> evidenceRoleCache = new HashMap<String, EvidenceRole>();

	public ControlledVocabulary mapFieldSet(FieldSet fieldSet) throws BindException {

		String templateName = fieldSet.readString(TEMPLATE_NAME);
		ObservationTemplate observationTemplate = observationTemplateMap.get(templateName);
		if (observationTemplate == null) return new ControlledVocabulary(null, null, null);

		String subject = fieldSet.readString(SUBJECT);
		String evidence = fieldSet.readString(EVIDENCE);
		if (subject.length() > 0) {
			SubjectRole subjectRole = subjectRoleCache.get(subject);
			if (subjectRole == null) {
				subjectRole = dashboardFactory.create(SubjectRole.class);
				subjectRole.setDisplayName(fieldSet.readString(ROLE));
				subjectRoleCache.put(subject, subjectRole);
			}
			ObservedSubjectRole observedSubjectRole = dashboardFactory.create(ObservedSubjectRole.class);
			observedSubjectRole.setSubjectRole(subjectRole);
			observedSubjectRole.setColumnName(fieldSet.readString(COLUMN_NAME));
			observedSubjectRole.setDisplayText(fieldSet.readString(DISPLAY_TEXT));
			observedSubjectRole.setObservationTemplate(observationTemplate);
			return new ControlledVocabulary(observationTemplate, subjectRole, observedSubjectRole);
		}
		else if (evidence.length() > 0) {
			EvidenceRole evidenceRole = evidenceRoleCache.get(evidence);
			if (evidenceRole == null) {
				evidenceRole = dashboardFactory.create(EvidenceRole.class);
				evidenceRole.setDisplayName(fieldSet.readString(ROLE));
				evidenceRoleCache.put(evidence, evidenceRole);
			}
			ObservedEvidenceRole observedEvidenceRole = dashboardFactory.create(ObservedEvidenceRole.class);
			observedEvidenceRole.setEvidenceRole(evidenceRole);
			observedEvidenceRole.setColumnName(fieldSet.readString(COLUMN_NAME));
			observedEvidenceRole.setDisplayText(fieldSet.readString(DISPLAY_TEXT));
			observedEvidenceRole.setObservationTemplate(observationTemplate);
			observedEvidenceRole.setAttribute(getObservedEvidenceRoleAttribute(fieldSet.readString(MIME_TYPE),
																			   fieldSet.readString(NUMERIC_UNITS)));
			return new ControlledVocabulary(observationTemplate, evidenceRole, observedEvidenceRole);
		}
		return new ControlledVocabulary(null, null, null);
	}
	
	private String getObservedEvidenceRoleAttribute(String mimeType, String numericUnits) {
		if (mimeType.length() > 0) return mimeType;
		if (numericUnits.length() > 0) return numericUnits;
		return "";
	}
}
