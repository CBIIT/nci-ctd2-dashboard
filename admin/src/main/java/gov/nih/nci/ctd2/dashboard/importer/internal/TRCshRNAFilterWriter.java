package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.ShRna;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("TRCshRNAFilterWriter")
public class TRCshRNAFilterWriter implements ItemWriter<ShRna> {
	// optimization - all writing is done in TRCshRNADataWriter
	public void write(List<? extends ShRna> items) throws Exception {}
}
