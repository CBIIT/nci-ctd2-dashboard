package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Proxy(proxyClass= Submission.class)
@Table(name = "submission")
@Indexed
public class SubmissionImpl extends DashboardEntityImpl implements Submission {
    private ObservationTemplate observationTemplate;
    private Date submissionDate;

    @ManyToOne(targetEntity = ObservationTemplateImpl.class)
    public ObservationTemplate getObservationTemplate() {
        return observationTemplate;
    }

    public void setObservationTemplate(ObservationTemplate observationTemplate) {
        this.observationTemplate = observationTemplate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "SS")
    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
}
