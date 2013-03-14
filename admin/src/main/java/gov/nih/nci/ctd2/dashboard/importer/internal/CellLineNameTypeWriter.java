package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("cellLineNameTypeWriter")
public class CellLineNameTypeWriter implements ItemWriter<CellSample> {
	// we don't want to write anything here
	public void write(List<? extends CellSample> items) throws Exception {}
}
