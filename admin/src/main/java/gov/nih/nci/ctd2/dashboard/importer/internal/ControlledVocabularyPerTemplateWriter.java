package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("controlledVocabularyPerTemplateWriter")
public class ControlledVocabularyPerTemplateWriter implements ItemWriter<ObservationTemplate> {
	// optimization - all writing is done in ControlledVocabularyPerColumnWriter
	public void write(List<? extends ObservationTemplate> items) throws Exception {}
}
