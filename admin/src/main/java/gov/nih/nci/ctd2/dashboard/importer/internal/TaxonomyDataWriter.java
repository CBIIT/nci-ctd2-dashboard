package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component("taxonomyDataWriter")
public class TaxonomyDataWriter implements ItemWriter<Organism> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(TaxonomyDataWriter.class);

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;
 
	public void write(List<? extends Organism> items) throws Exception {
        dashboardDao.batchSave(items, batchSize);
        log.debug("Organism written");
	}
}
