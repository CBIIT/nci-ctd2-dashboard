package gov.nih.nci.ctd2.dashboard.model;

public interface Organism extends Subject {
    public String getTaxonomyId();
    public void setTaxonomyId(String taxonomyId);
    public Gene getGene();
    public void setGene(Gene gene);
}
