package gov.nih.nci.ctd2.dashboard.api;

import java.util.HashMap;
import java.util.Map;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import gov.nih.nci.ctd2.dashboard.model.FileEvidence;
import gov.nih.nci.ctd2.dashboard.model.LabelEvidence;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidence;
import gov.nih.nci.ctd2.dashboard.model.UrlEvidence;

public class EvidenceItem {
    public final String clazz, type, description, value, units, mime_type;

    public EvidenceItem(ObservedEvidence observedEvidence) {
        gov.nih.nci.ctd2.dashboard.model.Evidence evidence = observedEvidence.getEvidence();
        clazz = simpleClassName.get( evidence.getClass().getSimpleName().replace("Impl", "") );
        this.type = observedEvidence.getObservedEvidenceRole().getEvidenceRole().getDisplayName();
        this.description = observedEvidence.getObservedEvidenceRole().getDisplayText();

        String value = null, units = null, mime_type = null;
        if (evidence instanceof DataNumericValue) {
            DataNumericValue dnv = (DataNumericValue) evidence;
            value = dnv.getNumericValue().toString();
            units = dnv.getUnit();
        } else if (evidence instanceof FileEvidence) {
            FileEvidence fe = (FileEvidence) evidence;
            String filePath = fe.getFilePath().replaceAll("\\\\", "/");
            if(filePath.startsWith("./")) {
                filePath = filePath.substring(2); // not absolutely necessary, but cleaner
            }
            value = ObservationItem.dataURL + filePath;
            mime_type = fe.getMimeType();
        } else if (evidence instanceof LabelEvidence) {
            LabelEvidence le = (LabelEvidence) evidence;
            value = le.getDisplayName();
        } else if (evidence instanceof UrlEvidence) {
            UrlEvidence ue = (UrlEvidence) evidence;
            value = ue.getUrl();
        }

        this.value = value;
        this.units = units;
        this.mime_type = mime_type;
    }

    private static Map<String, String> simpleClassName = new HashMap<String, String>();
    static {
        simpleClassName.put("LabelEvidence", "label");
        simpleClassName.put("UrlEvidence", "url");
        simpleClassName.put("DataNumericValue", "numeric");
        simpleClassName.put("FileEvidence", "file");
    }
}
