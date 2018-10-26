package gov.nih.nci.ctd2.dashboard.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import gov.nih.nci.ctd2.dashboard.model.FileEvidence;
import gov.nih.nci.ctd2.dashboard.model.Widget;

@Entity
@Proxy(proxyClass = FileEvidence.class)
@Table(name = "file_evidence")
public class FileEvidenceImpl extends EvidenceImpl implements FileEvidence {
    private static final long serialVersionUID = 93374005845035626L;
    private String filePath;
	private String fileName;
    private String mimeType;
    private Widget widget;

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

    @ManyToOne(targetEntity = WidgetImpl.class)
    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }
}
