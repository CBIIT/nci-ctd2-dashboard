package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Component("proteinDataWriter")
public class ProteinDataWriter implements ItemWriter<ProteinData> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(ProteinDataWriter.class);

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;

    public void write(List<? extends ProteinData> items) throws Exception {
        ArrayList<DashboardEntity> entities = new ArrayList<DashboardEntity>();

		for (ProteinData proteinData : items) {
            entities.addAll(proteinData.transcripts);
			log.info("Storing protein: " + proteinData.protein.getDisplayName());
            entities.add(proteinData.protein);
		}

        dashboardDao.batchSave(entities, batchSize);
	}
}
