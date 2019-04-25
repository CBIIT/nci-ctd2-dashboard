package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import gov.nih.nci.ctd2.dashboard.util.StableURL;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component("proteinDataWriter")
public class ProteinDataWriter implements ItemWriter<ProteinData> {

    @Autowired
    private DashboardDao dashboardDao;

    private static final Log log = LogFactory.getLog(ProteinDataWriter.class);

    @Autowired
    @Qualifier("batchSize")
    private Integer batchSize;

    public void write(List<? extends ProteinData> items) throws Exception {
        ArrayList<DashboardEntity> entities = new ArrayList<DashboardEntity>();

        for (ProteinData proteinData : items) {
            Set<Transcript> transcripts = proteinData.transcripts;
            for (Transcript tra : transcripts) {
                String traStableURL = new StableURL().createURLWithPrefix("transcript", tra.getRefseqId());
                tra.setStableURL(traStableURL);
            }
            entities.addAll(proteinData.transcripts);
            log.info("Storing protein: " + proteinData.protein.getDisplayName());
            String stableURL = new StableURL().createURLWithPrefix("protein", proteinData.protein.getUniprotId());
            proteinData.protein.setStableURL(stableURL);
            entities.add(proteinData.protein);
        }

        dashboardDao.batchSave(entities, batchSize);
    }
}
