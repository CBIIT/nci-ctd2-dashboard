package gov.nih.nci.ctd2.dashboard.importer.internal;

import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Component("controlledVocabularyProcessor")
public class ControlledVocabularyProcessor implements ItemProcessor<ControlledVocabulary, ControlledVocabulary> {

    @Override
    public ControlledVocabulary process(ControlledVocabulary controlledVocabulary) throws Exception {
		return (controlledVocabulary.role == null || controlledVocabulary.observedRole == null) ? null : controlledVocabulary;
	}
}