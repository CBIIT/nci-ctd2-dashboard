package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("compoundSynonymsWriter")
public class CompoundSynonymsWriter implements ItemWriter<Compound> {
	// optimization - all writing is done in CompoundDataWriter
	public void write(List<? extends Compound> items) throws Exception {}
}
