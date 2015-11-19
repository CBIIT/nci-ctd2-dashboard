package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;

@Entity
@Proxy(proxyClass= ObservationTemplate.class)
@Table(name = "observation_template")
@Indexed
public class ObservationTemplateImpl extends DashboardEntityImpl implements ObservationTemplate {
    public final static String FIELD_DESCRIPTION = "description";
    public final static String FIELD_SUBMISSIONDESC = "submissionDesc";
    public final static String FIELD_PROJECT = "project";
    public final static String FIELD_SUBMISSIONNAME = "submissionName";

    private String description;
	private String observationSummary;
    private Integer tier = 0;
	private String submissionName;
	private String submissionDescription;
    private String project;
	private Boolean isSubmissionStory;
	private Integer submissionStoryRank = 0;
    private SubmissionCenter submissionCenter;
    private String principalInvestigator;

    @Field(name=FIELD_DESCRIPTION, index = Index.TOKENIZED)
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

    @Field(name=FIELD_SUBMISSIONNAME, index = Index.TOKENIZED)
    @Column(length = 128)
    public String getSubmissionName() {
        return submissionName;
    }

    public void setSubmissionName(String submissionName) {
        this.submissionName = submissionName;
    }

    @Field(name=FIELD_SUBMISSIONDESC, index = Index.TOKENIZED)
    @Column(length = 1024)
    public String getSubmissionDescription() {
        return submissionDescription;
    }

    public void setSubmissionDescription(String submissionDescription) {
        this.submissionDescription = submissionDescription;
    }
    
    @Field(name=FIELD_PROJECT, index = Index.TOKENIZED)
    @Column(length = 1024)
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

    @ManyToOne(targetEntity = SubmissionCenterImpl.class)
    public SubmissionCenter getSubmissionCenter() {
        return submissionCenter;
    }

    public void setSubmissionCenter(SubmissionCenter submissionCenter) {
        this.submissionCenter = submissionCenter;
    }

    @Column(length=64, nullable=false)
    public String getPrincipalInvestigator() {
        return principalInvestigator;
    }

    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }
}
