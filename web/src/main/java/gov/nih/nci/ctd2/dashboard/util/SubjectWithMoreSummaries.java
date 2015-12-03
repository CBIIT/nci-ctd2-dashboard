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

/* This is similar to SubjectWithSummaries with two differeces:
 * 1) with additional information for the new broswing feature
 * 2) dynamically created without db backing */
public class SubjectWithMoreSummaries extends SubjectWithSummaries implements DashboardEntity {

    public SubjectWithMoreSummaries(Integer id) {
    	this.setId( id );
    }
    public SubjectWithMoreSummaries(SubjectWithSummaries sws) {
        this.setSubject( sws.getSubject() );
        this.setRole( sws.getRole() );
        this.setScore( sws.getScore() );
        this.setId( sws.getSubject().getId() );

        this.setMaxTier( sws.getMaxTier() );
        this.setNumberOfObservations( sws.getNumberOfObservations() );
    }

    private Integer numberOfTier3SubmissionCenters = 0;
    private Integer numberOfTier3Observations = 0;
    private Integer numberOfTier2SubmissionCenters = 0;
    private Integer numberOfTier2Observations = 0;
    private Integer numberOfTier1SubmissionCenters = 0;
    private Integer numberOfTier1Observations = 0;
    
    private Set<Integer> tier3Centers = new TreeSet<Integer>();
    private Set<Integer> tier2Centers = new TreeSet<Integer>();

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
        default: // no-op
        }
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
}
