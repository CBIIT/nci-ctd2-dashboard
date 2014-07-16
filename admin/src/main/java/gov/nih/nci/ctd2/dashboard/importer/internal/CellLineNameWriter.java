package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("cellLineNameWriter")
public class CellLineNameWriter implements ItemWriter<CellSample> {
	// optimization - all writing is done in CellLineDataWriter
	public void write(List<? extends CellSample> items) throws Exception {}
}
