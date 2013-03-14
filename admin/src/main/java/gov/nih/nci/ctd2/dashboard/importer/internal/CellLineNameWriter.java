package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("cellLineNameWriter")
public class CellLineNameWriter implements ItemWriter<CellSample> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(CellLineNameWriter.class);
 
	public void write(List<? extends CellSample> items) throws Exception {
		for (CellSample cellSample : items) {
			dashboardDao.update(cellSample);
		}
	}
}
