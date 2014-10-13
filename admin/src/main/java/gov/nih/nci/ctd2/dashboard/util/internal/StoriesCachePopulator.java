package gov.nih.nci.ctd2.dashboard.util.internal;

import gov.nih.nci.ctd2.dashboard.util.CachePopulator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StoriesCachePopulator extends CachePopulator {
    private static Log log = LogFactory.getLog(StoriesCachePopulator.class);

    @Override
    public void populate() {
        log.info("Populating cache for stories...");
        getDashboardDao().findSubmissionByIsStory(true, true);
        log.info("Done with populating cache for stories...");
    }
}
