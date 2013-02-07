package gov.nih.nci.ctd2.dashboard.model;

public interface ObservedEvidenceRole extends DashboardEntity {
    public ObservationTemplate getObservationTemplate();
    public void setObservationTemplate(ObservationTemplate observationTemplate);
    public EvidenceRole getEvidenceRole();
    public void setEvidenceRole(EvidenceRole evidenceRole);
    public String getDescription();
    public void setDescription(String description);
    public String getColumnName();
    public void setColumnName(String columnName);
}
