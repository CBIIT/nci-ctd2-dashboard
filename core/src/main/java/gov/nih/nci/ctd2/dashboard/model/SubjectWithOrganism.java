package gov.nih.nci.ctd2.dashboard.model;

public interface SubjectWithOrganism extends Subject {
    public Organism getOrganism();
    public void setOrganism(Organism organism);
}
