package gov.nih.nci.ctd2.dashboard.model;

public interface Compound extends Subject {
    public String getSmilesNotation();
    public void setSmilesNotation(String smilesNotation);
}
