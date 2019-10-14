package gov.nih.nci.ctd2.dashboard.api;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import gov.nih.nci.ctd2.dashboard.model.Evidence;
import gov.nih.nci.ctd2.dashboard.model.FileEvidence;
import gov.nih.nci.ctd2.dashboard.model.LabelEvidence;
import gov.nih.nci.ctd2.dashboard.model.UrlEvidence;

public class EvidenceItem {
    public final String clazz, type, description, value, units, mime_type;
    public final String evidenceName, columnName;

    public static String dataURL = "";

    public EvidenceItem(Evidence evidence, String type, String description, String evidenceName, String columnName) {
        this.type = type;
        this.description = description;

        String value = null, units = null, mime_type = null;
        if (evidence instanceof DataNumericValue) {
            clazz = "numeric";
            DataNumericValue dnv = (DataNumericValue) evidence;
            value = dnv.getNumericValue().toString();
            units = dnv.getUnit();
        } else if (evidence instanceof FileEvidence) {
            clazz = "file";
            FileEvidence fe = (FileEvidence) evidence;
            String filePath = fe.getFilePath().replaceAll("\\\\", "/");
            if (filePath.startsWith("./")) {
                filePath = filePath.substring(2); // not absolutely necessary, but cleaner
            }
            value = EvidenceItem.dataURL + filePath;
            mime_type = fe.getMimeType();
        } else if (evidence instanceof LabelEvidence) {
            clazz = "label";
            LabelEvidence le = (LabelEvidence) evidence;
            value = le.getDisplayName();
        } else if (evidence instanceof UrlEvidence) {
            clazz = "url";
            UrlEvidence ue = (UrlEvidence) evidence;
            value = ue.getUrl();
        } else {
            clazz = null;
        }

        this.value = value;
        this.units = units;
        this.mime_type = mime_type;
        this.evidenceName = evidenceName;
        this.columnName = columnName;
    }
}
