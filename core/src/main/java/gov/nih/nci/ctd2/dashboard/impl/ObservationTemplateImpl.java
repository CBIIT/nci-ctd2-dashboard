package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass= ObservationTemplate.class)
@Table(name = "observation_template")
public class ObservationTemplateImpl extends DashboardEntityImpl implements ObservationTemplate {
    private String description;
	private String observationSummary;
    private Integer tier = 0;
	private String submissionName;
	private String submissionDescription;
	private Boolean isSubmissionStory;
	private Integer submissionStoryRank = 0;

    @Column(length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 1024)
    public String getObservationSummary() {
        return observationSummary;
    }

    public void setObservationSummary(String observationSummary) {
        this.observationSummary = observationSummary;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    @Column(length = 128)
    public String getSubmissionName() {
        return submissionName;
    }

    public void setSubmissionName(String submissionName) {
        this.submissionName = submissionName;
    }

    @Column(length = 1024)
    public String getSubmissionDescription() {
        return submissionDescription;
    }

    public void setSubmissionDescription(String submissionDescription) {
        this.submissionDescription = submissionDescription;
    }

	public Boolean getIsSubmissionStory() {
		return isSubmissionStory;
	}

	public void setIsSubmissionStory(Boolean isSubmissionStory) {
		this.isSubmissionStory = isSubmissionStory;
	}

    public Integer getSubmissionStoryRank() {
        return submissionStoryRank;
    }

    public void setSubmissionStoryRank(Integer submissionStoryRank) {
        this.submissionStoryRank = submissionStoryRank;
    }
}
