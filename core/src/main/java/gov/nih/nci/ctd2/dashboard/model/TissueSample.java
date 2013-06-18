package gov.nih.nci.ctd2.dashboard.model;

public interface TissueSample extends Subject {
	public String getLineage();
    public void setLineage(String lineage);
}
