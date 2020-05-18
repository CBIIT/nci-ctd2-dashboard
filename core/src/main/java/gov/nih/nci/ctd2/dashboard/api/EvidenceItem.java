package gov.nih.nci.ctd2.dashboard.api;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import gov.nih.nci.ctd2.dashboard.model.Evidence;
import gov.nih.nci.ctd2.dashboard.model.FileEvidence;
import gov.nih.nci.ctd2.dashboard.model.LabelEvidence;
import gov.nih.nci.ctd2.dashboard.model.UrlEvidence;

@Entity
@Table(name = "evidence_item", indexes = @Index(name = "observation_id", columnList = "observation_id", unique = false))
public class EvidenceItem implements Serializable {

    private static final long serialVersionUID = -6215951762324186670L;

    private Integer observation_id;

    @Column(length = 10240)
    private String description;

    private String clazz, type, value, units, mime_type;
    private String evidenceName, columnName;

    public static String dataURL = "";

    @Id
    @GeneratedValue
    private Long id;

    public EvidenceItem() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Column(name = "observation_id")
    public Integer getObservationId() {
        return observation_id;
    }

    public void setObservationId(Integer x) {
        observation_id = x;
    }

    public EvidenceItem(Evidence evidence, String type, String description, String evidenceName, String columnName,
            Integer oid) {
        this.observation_id = oid;
        this.setType(type);
        this.setDescription(description);

        String value = null, units = null, mime_type = null;
        if (evidence instanceof DataNumericValue) {
            setClazz("numeric");
            DataNumericValue dnv = (DataNumericValue) evidence;
            value = dnv.getNumericValue().toString();
            units = dnv.getUnit();
        } else if (evidence instanceof FileEvidence) {
            setClazz("file");
            FileEvidence fe = (FileEvidence) evidence;
            String filePath = fe.getFilePath().replaceAll("\\\\", "/");
            if (filePath.startsWith("./")) {
                filePath = filePath.substring(2); // not absolutely necessary, but cleaner
            }
            value = EvidenceItem.dataURL + filePath;
            mime_type = fe.getMimeType();
        } else if (evidence instanceof LabelEvidence) {
            setClazz("label");
            LabelEvidence le = (LabelEvidence) evidence;
            value = le.getDisplayName();
        } else if (evidence instanceof UrlEvidence) {
            setClazz("url");
            UrlEvidence ue = (UrlEvidence) evidence;
            value = ue.getUrl();
        } else {
            setClazz(null);
        }

        this.setValue(value);
        this.setUnits(units);
        this.setMime_type(mime_type);
        this.setEvidenceName(evidenceName);
        this.setColumnName(columnName);
    }
}
