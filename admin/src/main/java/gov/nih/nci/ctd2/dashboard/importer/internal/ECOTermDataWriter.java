package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.StableURL;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;

@Component("ecotermDataWriter")
public class ECOTermDataWriter implements ItemWriter<ECOTerm> {

    @Autowired
    private DashboardDao dashboardDao;

    private static final Log log = LogFactory.getLog(ECOTermDataWriter.class);

    @Autowired
    @Qualifier("batchSize")
    private Integer batchSize;

    public void write(List<? extends ECOTerm> items) throws Exception {
        StableURL stableURL = new StableURL();
        for (ECOTerm ecoterm : items) {
            ecoterm.setStableURL(stableURL.createURLWithPrefix("eco", ecoterm.getCode()));
        }
        dashboardDao.batchSave(items, batchSize);
        log.debug("ECO term written");
    }
}
