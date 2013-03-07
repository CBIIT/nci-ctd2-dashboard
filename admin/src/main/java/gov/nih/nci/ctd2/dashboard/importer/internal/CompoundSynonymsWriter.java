package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("compoundSynonymsWriter")
public class CompoundSynonymsWriter implements ItemWriter<Compound> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(CompoundSynonymsWriter.class);
 
	public void write(List<? extends Compound> items) throws Exception {
		for (Compound compound : items) {
			log.info("Storing compound w/Synonym: " + compound.getSynonyms());
			dashboardDao.update(compound);
		}
	}
}