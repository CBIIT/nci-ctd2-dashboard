package gov.nih.nci.ctd2.dashboard.util.internal;

import gov.nih.nci.ctd2.dashboard.util.CachePopulator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BrowseCachePopulator extends CachePopulator {
    private static Log log = LogFactory.getLog(BrowseCachePopulator.class);

    @Override
    public void populate() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : chars.toCharArray()) {
            String cs = "" + c;
            log.info("Populating browse caches with '" + cs + "'");
            getDashboardDao().browseCompounds(cs);
            getDashboardDao().browseTargets(cs);
        }

        log.info("Browse caches were populated with all characters [" + chars + "]");
    }
}
