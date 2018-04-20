package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Proxy(proxyClass = Observation.class)
@Table(name = "observation")
public class ObservationImpl extends DashboardEntityImpl implements Observation {
    private static final long serialVersionUID = -6318164630504388166L;
    private Submission submission;
    private String stableURL;

    @ManyToOne(targetEntity = SubmissionImpl.class)
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @Override
    public String getStableURL() {
        return stableURL;
    }

    @Override
    public void setStableURL(String stableURL) {
        this.stableURL = stableURL;
    }
}
