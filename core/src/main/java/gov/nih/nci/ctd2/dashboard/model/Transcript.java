package gov.nih.nci.ctd2.dashboard.model;

public interface Transcript extends SubjectWithOrganism {
    public String getRefseqId();
    public void setRefseqId(String refseqId);
    public Gene getGene();
    public void setGene(Gene gene);
}
