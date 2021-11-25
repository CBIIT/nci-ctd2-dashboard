package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Proxy(proxyClass = Submission.class)
@Table(name = "submission")
@Indexed
public class SubmissionImpl extends DashboardEntityImpl implements Submission {
    private static final long serialVersionUID = -6902361235500057743L;
	private ObservationTemplate observationTemplate;
    private Date submissionDate;
    private String stableURL;

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

    @Override
    public String getStableURL() {
        return stableURL;
    }

    @Override
    public void setStableURL(String stableURL) {
        this.stableURL = stableURL;
    }
}
