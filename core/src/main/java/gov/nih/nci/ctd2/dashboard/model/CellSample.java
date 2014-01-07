package gov.nih.nci.ctd2.dashboard.model;

import java.util.Set;

public interface CellSample extends SubjectWithOrganism {
    public String getGender();
    public void setGender(String source);
    public Set<Annotation> getAnnotations();
    public void setAnnotations(Set<Annotation> annotations);
}
