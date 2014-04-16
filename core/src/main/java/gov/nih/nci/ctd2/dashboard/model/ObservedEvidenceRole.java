package gov.nih.nci.ctd2.dashboard.model;

public interface ObservedEvidenceRole extends DashboardEntity {
    public ObservationTemplate getObservationTemplate();
    public void setObservationTemplate(ObservationTemplate observationTemplate);
    public EvidenceRole getEvidenceRole();
    public void setEvidenceRole(EvidenceRole evidenceRole);
    public String getDisplayText();
    public void setDisplayText(String displayText);
    public String getColumnName();
    public void setColumnName(String columnName);
    public String getAttribute();
    public void setAttribute(String attribute);
}
