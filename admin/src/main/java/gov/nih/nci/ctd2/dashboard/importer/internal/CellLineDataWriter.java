package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellLine;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("cellLineDataWriter")
public class CellLineDataWriter implements ItemWriter<CellLine> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(CellLineDataWriter.class);
 
	public void write(List<? extends CellLine> items) throws Exception {
		for (CellLine cellLine : items) {
			log.info("Storing cell line: " + cellLine.getDisplayName());
			dashboardDao.save(cellLine);
		}
	}
}
