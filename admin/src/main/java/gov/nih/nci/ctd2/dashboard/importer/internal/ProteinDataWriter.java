package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Transcript;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("proteinDataWriter")
public class ProteinDataWriter implements ItemWriter<ProteinData> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(ProteinDataWriter.class);
 
	public void write(List<? extends ProteinData> items) throws Exception {
		for (ProteinData proteinData : items) {
			for (Transcript transcript : proteinData.transcripts) {
				dashboardDao.save(transcript);
			}
			log.info("Storing protein: " + proteinData.protein.getDisplayName());
			dashboardDao.save(proteinData.protein);
		}
	}
}
