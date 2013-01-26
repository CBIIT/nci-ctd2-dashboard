package gov.nih.nci.ctd2.dashboard.model;

public interface Protein extends Subject {
    public String getUniprotId();
    public void setUniprotId(String uniprotId);
    public Transcript getTranscript();
    public void setTranscript(Transcript transcript);
}
