package gov.nih.nci.ctd2.dashboard.model;

public interface Observation extends DashboardEntity, HasStableURL {
    public Submission getSubmission();
    public void setSubmission(Submission submission);
}
