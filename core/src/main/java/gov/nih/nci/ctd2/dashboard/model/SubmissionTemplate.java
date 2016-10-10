package gov.nih.nci.ctd2.dashboard.model;

import java.util.Date;

public interface SubmissionTemplate extends DashboardEntity {
    public SubmissionCenter getSubmissionCenter();
    public void setSubmissionCenter(SubmissionCenter submissionCenter);
    public String getDescription();
    public void setDescription(String description);
    public String getProject();
    public void setProject(String project);
    public Integer getTier();
    public void setTier(Integer tier);
    public Boolean getComplete();
    public void setComplete(Boolean complete);
    public Date getDateLastModified();
    public void setDateLastModified(Date dateLastModified);
    public String getFirstName();
    public void setFirstName(String firstName);
    public String getLastName();
    public void setLastName(String lastName);
    public String getEmail();
    public void setEmail(String email);
    public String getPhone();
    public void setPhone(String phone);
    public String[] getSubjectColumns();
    public void setSubjectColumns(String[] subjects);
    public String[] getEvidenceColumns();
    public void setEvidenceColumns(String[] evidences);
}