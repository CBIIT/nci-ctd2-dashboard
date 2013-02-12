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
import java.util.HashSet;

@Component("controlledVocabularyMapper")
public class ControlledVocabularyFieldSetMapper implements FieldSetMapper<ControlledVocabulary> {

    @Autowired
    private DashboardFactory dashboardFactory;
	
	@Autowired
	@Qualifier("subjectTypeToClassNameMap")
	private	HashMap<String, String> subjectTypeToClassNameMap;

	@Autowired
	@Qualifier("evidenceTypeToClassNameMap")
	private	HashMap<String, String> evidenceTypeToClassNameMap;

	// cache for fast lookup and prevention of duplicate role records
    private HashMap<String, SubjectRole> subjectRoleCache = new HashMap<String, SubjectRole>();
    private HashMap<String, EvidenceRole> evidenceRoleCache = new HashMap<String, EvidenceRole>();
    private HashMap<String, ObservationTemplate> observationTemplateCache = new HashMap<String, ObservationTemplate>();

	public ControlledVocabulary mapFieldSet(FieldSet fieldSet) throws BindException {
		String templateName = fieldSet.readString(0).toLowerCase();
		String templateDescription = fieldSet.readString(1).toLowerCase();
		Integer templateTier = fieldSet.readInt(2);
		String columnName = fieldSet.readString(3).toLowerCase();
		String subject = fieldSet.readString(4).toLowerCase();
		String evidence = fieldSet.readString(5).toLowerCase();
		String role = fieldSet.readString(6).toLowerCase();
		String description = fieldSet.readString(7).toLowerCase();

		ObservationTemplate observationTemplate = observationTemplateCache.get(templateName);
		if (observationTemplate == null) {
			observationTemplate = dashboardFactory.create(ObservationTemplate.class);
			observationTemplate.setDisplayName(templateName);
			observationTemplate.setDescription(templateDescription);
			observationTemplate.setTier(templateTier);
			observationTemplateCache.put(templateName, observationTemplate);
		}

		if (subject.length() > 0) {
			SubjectRole subjectRole = subjectRoleCache.get(subject);
			if (subjectRole == null) {
				subjectRole = dashboardFactory.create(SubjectRole.class);
				subjectRole.setDisplayName(role);
				if (subjectTypeToClassNameMap.containsKey(subject)) {
					subjectRole.setSubjectClassName(subjectTypeToClassNameMap.get(subject));
				}
				subjectRoleCache.put(subject, subjectRole);
			}
			ObservedSubjectRole observedSubjectRole = dashboardFactory.create(ObservedSubjectRole.class);
			observedSubjectRole.setSubjectRole(subjectRole);
			observedSubjectRole.setColumnName(columnName);
			observedSubjectRole.setDescription(description);
			observedSubjectRole.setObservationTemplate(observationTemplate);
			return new ControlledVocabulary(observationTemplate, subjectRole, observedSubjectRole);
		}
		else if (evidence.length() > 0) {
			EvidenceRole evidenceRole = evidenceRoleCache.get(evidence);
			if (evidenceRole == null) {
				evidenceRole = dashboardFactory.create(EvidenceRole.class);
				evidenceRole.setDisplayName(role);
				if (evidenceTypeToClassNameMap.containsKey(evidence)) {
					evidenceRole.setEvidenceClassName(evidenceTypeToClassNameMap.get(evidence));
				}
				evidenceRoleCache.put(evidence, evidenceRole);
			}
			ObservedEvidenceRole observedEvidenceRole = dashboardFactory.create(ObservedEvidenceRole.class);
			observedEvidenceRole.setEvidenceRole(evidenceRole);
			observedEvidenceRole.setColumnName(columnName);
			observedEvidenceRole.setDescription(description);
			observedEvidenceRole.setObservationTemplate(observationTemplate);
			return new ControlledVocabulary(observationTemplate, evidenceRole, observedEvidenceRole);
		}
		return new ControlledVocabulary(null, null, null);
	}
}
