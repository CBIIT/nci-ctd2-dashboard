package gov.nih.nci.ctd2.dashboard.util;

import javax.persistence.Entity;
import javax.persistence.Table;

import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

@Entity
@Table(name = "overall_summary")
public class Summary extends DashboardEntityImpl implements DashboardEntity {
    private String label;
    private int submissions;
    private int tier1;
    private int tier2;
    private int tier3;

    public Summary() {
    }

    public Summary(String label, int submissions, int tier1, int tier2, int tier3) {
        this.label = label;
        this.submissions = submissions;
        this.tier1 = tier1;
        this.tier2 = tier2;
        this.tier3 = tier3;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSubmissions() {
        return submissions;
    }

    public void setSubmissions(int submissions) {
        this.submissions = submissions;
    }

    public int getTier1() {
        return tier1;
    }

    public void setTier1(int tier1) {
        this.tier1 = tier1;
    }

    public int getTier2() {
        return tier2;
    }

    public void setTier2(int tier2) {
        this.tier2 = tier2;
    }

    public int getTier3() {
        return tier3;
    }

    public void setTier3(int tier3) {
        this.tier3 = tier3;
    }
}