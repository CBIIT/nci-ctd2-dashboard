package gov.nih.nci.ctd2.dashboard.model;

public interface FileEvidence extends Evidence {
	String getFilePath();
	void setFilePath(String filePath);
	String getMimeType();
	void setMimeType(String mimeType);
}
