package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.util.StableURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component("geneDataWriter")
public class GeneDataWriter implements ItemWriter<Gene> {

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    @Qualifier("batchSize")
    private Integer batchSize;

    private static final Log log = LogFactory.getLog(GeneDataWriter.class);

    public void write(List<? extends Gene> items) throws Exception {
        StableURL stableURL = new StableURL();
        for (Gene gene : items) {
            char species = gene.getOrganism().getDisplayName().charAt(0);
            species = Character.toLowerCase(species);
            gene.setStableURL(stableURL.createURLWithPrefix("gene/" + species, gene.getDisplayName()));
        }
        dashboardDao.batchSave(items, batchSize);
        log.debug("Gene written");
    }
}
