package gov.nih.nci.ctd2.dashboard.model;

public interface TemplateObservation extends DashboardEntity {
    public SubmissionTemplate getSubmissionTemplate();
    public void setSubmissionTemplate(SubmissionTemplate submissionTemplate);
    public String[] getSubjectValues();
    public void setSubjectValues(String[] subjects);
    public String[] getEvidenceValues();
    public void setEvidenceValues(String[] evidences);
}
