package gov.nih.nci.ctd2.dashboard.util;

/* data object for API 2.0 ECO code */
public class ObservationURIsAndTiers {
    final public String[] uris;
    final public int tier1;
    final public int tier2;
    final public int tier3;

    public ObservationURIsAndTiers(final String[] uris, final int t1, final int t2, final int t3) {
        this.uris = uris;
        tier1 = t1;
        tier2 = t2;
        tier3 = t3;
    }
}
