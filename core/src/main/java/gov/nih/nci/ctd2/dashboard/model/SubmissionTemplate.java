package gov.nih.nci.ctd2.dashboard.model;

public interface SubmissionTemplate extends DashboardEntity {
    public SubmissionCenter getSubmissionCenter();
    public void setSubmissionCenter(SubmissionCenter submissionCenter);
}
