package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.TissueSample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

@Component("tissueSampleDataWriter")
public class TissueSampleDataWriter implements ItemWriter<TissueSample> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(TissueSampleDataWriter.class);
 
	public void write(List<? extends TissueSample> items) throws Exception {
		for (TissueSample tissueSample : items) {
			log.info("Storing tissue sample: " + tissueSample.getDisplayName());
			dashboardDao.save(tissueSample);
		}
	}
}
