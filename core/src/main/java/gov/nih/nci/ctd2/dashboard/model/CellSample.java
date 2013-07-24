package gov.nih.nci.ctd2.dashboard.model;

public interface CellSample extends SubjectWithOrganism {
	public String getLineage();
    public void setLineage(String lineage);
}
