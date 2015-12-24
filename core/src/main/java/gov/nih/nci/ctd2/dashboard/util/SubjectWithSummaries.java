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

import java.util.*;

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

    private Integer numberOfTier3SubmissionCenters = 0;
    private Integer numberOfTier3Observations = 0;
    private Integer numberOfTier2SubmissionCenters = 0;
    private Integer numberOfTier2Observations = 0;
    private Integer numberOfTier1SubmissionCenters = 0;
    private Integer numberOfTier1Observations = 0;

    private Set<Integer> tier3Centers = new TreeSet<Integer>();
    private Set<Integer> tier2Centers = new TreeSet<Integer>();
    private Set<Integer> tier1Centers = new TreeSet<Integer>();

    public void addSubmission(Integer tier, Integer submissionCenterId) {
        switch(tier) {
        case 3: 
            numberOfTier3Observations++;
            tier3Centers.add(submissionCenterId);
            numberOfTier3SubmissionCenters = tier3Centers.size();
            break;
        case 2: 
            numberOfTier2Observations++;
            tier2Centers.add(submissionCenterId);
            numberOfTier2SubmissionCenters = tier2Centers.size();
            break;
        case 1: 
            numberOfTier1Observations++;
            tier1Centers.add(submissionCenterId);
            numberOfTier1SubmissionCenters = tier1Centers.size();
            break;
        default: // no-op
        }
    }

    
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
    
    public Integer getNumberOfTier3SubmissionCenters() {
        return numberOfTier3SubmissionCenters;
    }

    public Integer getNumberOfTier3Observations() {
        return numberOfTier3Observations;
    }

    public Integer getNumberOfTier2SubmissionCenters() {
        return numberOfTier2SubmissionCenters;
    }

    public Integer getNumberOfTier2Observations() {
        return numberOfTier2Observations;
    }

    public Integer getNumberOfTier1SubmissionCenters() {
        return numberOfTier1SubmissionCenters;
    }

    public Integer getNumberOfTier1Observations() {
        return numberOfTier1Observations;
    }
    
    public void setNumberOfTier3SubmissionCenters(Integer n) {
        numberOfTier3SubmissionCenters = n;
    }

    public void setNumberOfTier3Observations(Integer n) {
        numberOfTier3Observations = n;
    }

    public void setNumberOfTier2SubmissionCenters(Integer n) {
        numberOfTier2SubmissionCenters = n;
    }

    public void setNumberOfTier2Observations(Integer n) {
        numberOfTier2Observations = n;
    }

    public void setNumberOfTier1SubmissionCenters(Integer n) {
        numberOfTier1SubmissionCenters = n;
    }

    public void setNumberOfTier1Observations(Integer n) {
        numberOfTier1Observations = n;
    }
}
