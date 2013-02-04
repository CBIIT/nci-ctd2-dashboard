package gov.nih.nci.ctd2.dashboard.model;

public interface Organism extends DashboardEntity {
    public String getTaxonomyId();
    public void setTaxonomyId(String taxonomyId);
}
