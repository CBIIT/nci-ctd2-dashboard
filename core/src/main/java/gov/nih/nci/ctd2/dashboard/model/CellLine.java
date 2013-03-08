package gov.nih.nci.ctd2.dashboard.model;

public interface CellLine extends SubjectWithOrganism {
	public String getTissue();
    public void setTissue(String tissue);
}
