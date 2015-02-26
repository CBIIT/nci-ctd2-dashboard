package gov.nih.nci.ctd2.dashboard.model;

import java.util.Set;

public interface Protein extends SubjectWithOrganism {
    public String getUniprotId();
    public void setUniprotId(String uniprotId);
    public Set<Transcript> getTranscripts();
    public void setTranscripts(Set<Transcript> transcripts);
}
