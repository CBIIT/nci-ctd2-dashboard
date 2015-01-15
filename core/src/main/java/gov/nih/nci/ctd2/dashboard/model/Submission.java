package gov.nih.nci.ctd2.dashboard.model;

import java.util.Date;

public interface Submission extends DashboardEntity {
    public ObservationTemplate getObservationTemplate();
    public void setObservationTemplate(ObservationTemplate observationTemplate);
    public Date getSubmissionDate();
    public void setSubmissionDate(Date submissionDate);
}
