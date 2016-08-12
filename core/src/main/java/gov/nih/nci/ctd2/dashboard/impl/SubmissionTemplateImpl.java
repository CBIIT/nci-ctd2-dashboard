package gov.nih.nci.ctd2.dashboard.impl;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import gov.nih.nci.ctd2.dashboard.model.SubmissionTemplate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = SubmissionTemplate.class)
@Table(name = "submission_template")
@Indexed
public class SubmissionTemplateImpl extends DashboardEntityImpl implements SubmissionTemplate {
    private String submissionCenter;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmissionCenter() {
        return submissionCenter;
    }

    public void setSubmissionCenter(String submissionCenter) {
        this.submissionCenter = submissionCenter;
    }
}
