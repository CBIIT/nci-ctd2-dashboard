package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import gov.nih.nci.ctd2.dashboard.model.EvidenceRole;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("controlledVocabularyProcessor")
public class ControlledVocabularyProcessor implements ItemProcessor<ControlledVocabulary, ControlledVocabulary> {

    @Override
    public ControlledVocabulary process(ControlledVocabulary controlledVocabulary) throws Exception {
		if (controlledVocabulary.role == null || controlledVocabulary.observedRole == null) return null;
		// dont let ObservedRole/Role through without a role class name
		if (controlledVocabulary.role instanceof SubjectRole) {
			if (((SubjectRole)controlledVocabulary.role).getSubjectClassName() == null) return null;
		}
		else if (controlledVocabulary.role instanceof EvidenceRole) {
			if (((EvidenceRole)controlledVocabulary.role).getEvidenceClassName() == null) return null;
		}
		return controlledVocabulary;
	}
}