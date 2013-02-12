package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.EvidenceRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
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

	public ControlledVocabulary mapFieldSet(FieldSet fieldSet) throws BindException {

		String columnName = fieldSet.readString(0);
		String subject = fieldSet.readString(1);
		String evidence = fieldSet.readString(2);
		String role = fieldSet.readString(3);
		String description = fieldSet.readString(4);

		if (subject.length() > 0) {
			SubjectRole subjectRole = subjectRoleCache.get(subject.toLowerCase());
			if (subjectRole == null) {
				subjectRole = dashboardFactory.create(SubjectRole.class);
				subjectRole.setDisplayName(role.toLowerCase());
				if (subjectTypeToClassNameMap.containsKey(subject.toLowerCase())) {
					subjectRole.setSubjectClassName(subjectTypeToClassNameMap.get(subject.toLowerCase()));
				}
				subjectRoleCache.put(subject.toLowerCase(), subjectRole);
			}
			ObservedSubjectRole observedSubjectRole = dashboardFactory.create(ObservedSubjectRole.class);
			observedSubjectRole.setSubjectRole(subjectRole);
			observedSubjectRole.setColumnName(columnName.toLowerCase());
			observedSubjectRole.setDescription(description);
			return new ControlledVocabulary(subjectRole, observedSubjectRole);
		}
		else if (evidence.length() > 0) {
			EvidenceRole evidenceRole = evidenceRoleCache.get(evidence.toLowerCase());
			if (evidenceRole == null) {
				evidenceRole = dashboardFactory.create(EvidenceRole.class);
				evidenceRole.setDisplayName(role.toLowerCase());
				if (evidenceTypeToClassNameMap.containsKey(evidence.toLowerCase())) {
					evidenceRole.setEvidenceClassName(evidenceTypeToClassNameMap.get(evidence.toLowerCase()));
				}
				evidenceRoleCache.put(evidence.toLowerCase(), evidenceRole);
			}
			ObservedEvidenceRole observedEvidenceRole = dashboardFactory.create(ObservedEvidenceRole.class);
			observedEvidenceRole.setEvidenceRole(evidenceRole);
			observedEvidenceRole.setColumnName(columnName.toLowerCase());
			observedEvidenceRole.setDescription(description);
			return new ControlledVocabulary(evidenceRole, observedEvidenceRole);
		}
		return new ControlledVocabulary(null, null);
	}
}
