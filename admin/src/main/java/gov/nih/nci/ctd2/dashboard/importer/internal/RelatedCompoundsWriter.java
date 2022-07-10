package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;

@Component("relatedCompoundsWriter")
public class RelatedCompoundsWriter implements ItemWriter<String[]> {

    @Autowired
    private DashboardDao dashboardDao;

    private static final Log log = LogFactory.getLog(RelatedCompoundsWriter.class);

    public void write(List<? extends String[]> items) throws Exception {
        for (String[] x : items) {
            System.out.println(x);
        }
        // dashboardDao.storeRelatedCompounds(...);
        log.debug("RelatedCompoundsWriter called. size=" + items.size());
    }
}
