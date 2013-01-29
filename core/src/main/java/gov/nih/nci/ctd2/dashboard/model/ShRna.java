package gov.nih.nci.ctd2.dashboard.model;

public interface ShRna extends Subject {
    public String getTargetSequence();
    public void setTargetSequence(String targetSequence);
    public Transcript getTranscript();
    public void setTranscript(Transcript transcript);
}
