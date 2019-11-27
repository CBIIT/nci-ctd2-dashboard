package gov.nih.nci.ctd2.dashboard.dao;

public class Summary {
    public final String label;
    public final int submissions;
    public final int tier1;
    public final int tier2;
    public final int tier3;

    public Summary(String label, int submissions, int tier1, int tier2, int tier3) {
        this.label = label;
        this.submissions = submissions;
        this.tier1 = tier1;
        this.tier2 = tier2;
        this.tier3 = tier3;
    }
}