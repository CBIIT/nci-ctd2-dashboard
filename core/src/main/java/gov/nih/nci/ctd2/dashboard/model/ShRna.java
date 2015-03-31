package gov.nih.nci.ctd2.dashboard.model;

public interface ShRna extends SubjectWithOrganism {
    public String getTargetSequence();
    public void setTargetSequence(String targetSequence);
    public Transcript getTranscript();
    public void setTranscript(Transcript transcript);
    public String getType();
    public void setType(String type);
    public String getReagentName();
    public void setReagentName(String reagentName);
}
