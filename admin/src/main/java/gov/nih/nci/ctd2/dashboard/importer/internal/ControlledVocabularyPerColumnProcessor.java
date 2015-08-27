package gov.nih.nci.ctd2.dashboard.importer.internal;

import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Component("controlledVocabularyPerColumnProcessor")
public class ControlledVocabularyPerColumnProcessor implements ItemProcessor<ControlledVocabulary, ControlledVocabulary> {

    @Override
    public ControlledVocabulary process(ControlledVocabulary controlledVocabulary) throws Exception {
		return (controlledVocabulary.observationTemplate == null ||
				controlledVocabulary.role == null ||
				controlledVocabulary.observedRole == null) ? null : controlledVocabulary;
	}
}