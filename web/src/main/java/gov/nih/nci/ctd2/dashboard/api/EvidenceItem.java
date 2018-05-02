package gov.nih.nci.ctd2.dashboard.api;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import gov.nih.nci.ctd2.dashboard.model.FileEvidence;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidence;

public class EvidenceItem {
    public final String clazz, type, description, value, units, mime_type;

    public EvidenceItem(ObservedEvidence observedEvidence) {
        gov.nih.nci.ctd2.dashboard.model.Evidence evidence = observedEvidence.getEvidence();
        clazz = evidence.getClass().getSimpleName().replace("Impl", "");
        this.type = observedEvidence.getObservedEvidenceRole().getEvidenceRole().getDisplayName();
        this.description = observedEvidence.getObservedEvidenceRole().getDisplayText();

        String value = null, units = null, mime_type = null;
        if (evidence instanceof DataNumericValue) {
            DataNumericValue dnv = (DataNumericValue) evidence;
            value = dnv.getNumericValue().toString();
            units = dnv.getUnit();
        } else if (evidence instanceof FileEvidence) {
            FileEvidence fe = (FileEvidence) evidence;
            value = fe.getFileName();
            mime_type = fe.getMimeType();
        }

        this.value = value;
        this.units = units;
        this.mime_type = mime_type;
    }
}
