package gov.nih.nci.ctd2.dashboard.model;

public interface Gene extends SubjectWithOrganism {
    String getEntrezGeneId();
    void setEntrezGeneId(String entrezGeneId);
    String getHGNCId();
    void setHGNCId(String hgncId);
}
