package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.impl.GeneImpl;
import gov.nih.nci.ctd2.dashboard.impl.SubjectImpl;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "subject_with_summaries")
public class SubjectWithSummaries extends DashboardEntityImpl implements DashboardEntity {
    private Subject subject;

    private Integer numberOfSubmissions;
    private Integer numberOfSubmissionCenters;
    private Integer numberOfObservations;
    private Integer maxTier;
    private String role;
    private Integer score;

    @ManyToOne(targetEntity = SubjectImpl.class)
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

    @Column(length = 128, nullable = false)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getScore() {
        return score;
    }

    public Integer calculateScore() {
        return getMaxTier() * getNumberOfSubmissionCenters();
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
