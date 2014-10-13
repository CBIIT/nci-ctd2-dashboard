package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.model.Subject;

import java.io.Serializable;

public class SubjectWithSummaries implements Serializable {
    private Subject subject;
    private Integer numberOfSubmissions;
    private Integer numberOfSubmissionCenters;
    private Integer numberOfObservations;
    private Integer maxTier;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getNumberOfSubmissions() {
        return numberOfSubmissions;
    }

    public void setNumberOfSubmissions(Integer numberOfSubmissions) {
        this.numberOfSubmissions = numberOfSubmissions;
    }

    public Integer getNumberOfSubmissionCenters() {
        return numberOfSubmissionCenters;
    }

    public void setNumberOfSubmissionCenters(Integer numberOfSubmissionCenters) {
        this.numberOfSubmissionCenters = numberOfSubmissionCenters;
    }

    public Integer getNumberOfObservations() {
        return numberOfObservations;
    }

    public void setNumberOfObservations(Integer numberOfObservations) {
        this.numberOfObservations = numberOfObservations;
    }

    public Integer getMaxTier() {
        return maxTier;
    }

    public void setMaxTier(Integer maxTier) {
        this.maxTier = maxTier;
    }
}
