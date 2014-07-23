package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("compoundsWriter")
public class CompoundsWriter implements ItemWriter<Compound> {
	// optimization - all writing is done in CompoundsDataWriter
	public void write(List<? extends Compound> items) throws Exception {}
}
