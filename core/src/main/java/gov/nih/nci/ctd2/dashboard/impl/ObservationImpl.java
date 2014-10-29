package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Proxy(proxyClass = Observation.class)
@Table(name = "observation")
public class ObservationImpl extends DashboardEntityImpl implements Observation {
    private Submission submission;

    @ManyToOne(targetEntity = SubmissionImpl.class)
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
}
