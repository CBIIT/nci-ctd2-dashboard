package gov.nih.nci.ctd2.dashboard.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* utility class to implement the 'guidelines' */
public class StableURL {
    private static final Log log = LogFactory.getLog(StableURL.class);

    final public String createURLWithPrefix(String prefix, String urlProperty) {
        /* 
        The guideline requires the complete URL to be 100 characters or less.
        Considering the production URL starts with https://ctd2-dashboard.nci.nih.gov/dashboard/ of length 45,
        the maximum length the unique part is set to be 50.
        */
        final int MAX_LENGTH = 50;
        String stableURL = prefix + "/" + urlProperty.toLowerCase().replaceAll("[^a-zA-Z0-9]", "-");
        if (stableURL.length() > MAX_LENGTH) {
            log.warn("The following string is too long for stable URL and truncated:\n" + stableURL);
            stableURL = stableURL.substring(0, MAX_LENGTH);
        }
        return stableURL;
    }
}