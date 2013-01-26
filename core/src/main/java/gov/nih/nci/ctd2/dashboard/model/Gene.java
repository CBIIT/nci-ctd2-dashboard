package gov.nih.nci.ctd2.dashboard.model;

public interface Gene extends Subject {
    String getEntrezGeneId();
    void setEntrezGeneId(String entrezGeneId);
}
