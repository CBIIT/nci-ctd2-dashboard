package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("cellLineSampleWriter")
public class CellLineSampleWriter implements ItemWriter<CellSample> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(CellLineSampleWriter.class);
 
	public void write(List<? extends CellSample> items) throws Exception {
		for (CellSample cellSample : items) {
			//log.info("Storing cell sample: " + cellSample.getDisplayName());
			dashboardDao.save(cellSample);
		}
	}
}
