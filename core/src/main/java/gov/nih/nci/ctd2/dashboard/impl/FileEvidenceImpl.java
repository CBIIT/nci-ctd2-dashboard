package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.FileEvidence;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.beans.ConstructorProperties;

@Entity
@Proxy(proxyClass = FileEvidence.class)
@Table(name = "file_evidence")
public class FileEvidenceImpl extends EvidenceImpl implements FileEvidence {
    private String filePath;
	private String fileName;
    private String mimeType;

    @Column(length = 1024)
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

	@Column(length = 1024)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(length = 256)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
