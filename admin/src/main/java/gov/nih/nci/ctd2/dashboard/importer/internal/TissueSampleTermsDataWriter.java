package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.TissueSample;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("tissueSampleTermsDataWriter")
public class TissueSampleTermsDataWriter implements ItemWriter<TissueSample> {
	// optimization - all writing is done in TissueSampleDataWriter
	public void write(List<? extends TissueSample> items) throws Exception {}
}
