package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.ShRna;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

@Component("TRCshRNADataWriter")
public class TRCshRNADataWriter implements ItemWriter<ShRna> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(TRCshRNADataWriter.class);
 
	public void write(List<? extends ShRna> items) throws Exception {
		for (ShRna shRNA : items) {
			log.info("Storing shRNA: " + shRNA.getDisplayName());
			dashboardDao.save(shRNA);
		}
	}
}
