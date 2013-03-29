package gov.nih.nci.ctd2.dashboard.model;

public interface ObservedSubjectRole extends DashboardEntity {
    public String getDisplayText();
    public void setDisplayText(String displayText);
    public String getColumnName();
    public void setColumnName(String columnName);
    public SubjectRole getSubjectRole();
    public void setSubjectRole(SubjectRole subjectRole);
    public ObservationTemplate getObservationTemplate();
    public void setObservationTemplate(ObservationTemplate observationTemplate);
}
