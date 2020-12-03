package gov.nih.nci.ctd2.dashboard.model;

public interface TissueSample extends Subject {
	public int getCode(); // digit part of NCI thesaurus code
    public void setCode(int code);
}
