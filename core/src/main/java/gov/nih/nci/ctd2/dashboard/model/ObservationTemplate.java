package gov.nih.nci.ctd2.dashboard.model;

public interface ObservationTemplate extends DashboardEntity {
    public String getDescription();
    public void setDescription(String description);
    public String getObservationSummary();
    public void setObservationSummary(String observationSummary);
    public Integer getTier();
    public void setTier(Integer tier);
    public String getSubmissionName();
    public void setSubmissionName(String submissionName);
    public String getSubmissionDescription();
    public void setSubmissionDescription(String submissionDescription);
    public String getProject();
    public void setProject(String project);
	public Boolean getIsSubmissionStory();
	public void setIsSubmissionStory(Boolean isSubmissionStory);
    public Integer getSubmissionStoryRank();
    public void setSubmissionStoryRank(Integer submissionStoryRank);
    public SubmissionCenter getSubmissionCenter();
    public void setSubmissionCenter(SubmissionCenter submissionCenter);
    public void setPrincipalInvestigator(String principalInvestigator);
    public String getPrincipalInvestigator();
}
