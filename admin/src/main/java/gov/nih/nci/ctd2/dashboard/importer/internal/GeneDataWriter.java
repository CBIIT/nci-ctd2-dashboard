package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

@Component("geneDataWriter")
public class GeneDataWriter implements ItemWriter<Gene> {

    @Autowired
	private DashboardDao dashboardDao;

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;
 
	private static final Log log = LogFactory.getLog(GeneDataWriter.class);
 
	public void write(List<? extends Gene> items) throws Exception {
        dashboardDao.batchSave(items, batchSize);
	}
}
