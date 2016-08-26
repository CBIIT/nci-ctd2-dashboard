package gov.nih.nci.ctd2.dashboard.impl;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.model.SubmissionTemplate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = SubmissionTemplate.class)
@Table(name = "submission_template")
@Indexed
public class SubmissionTemplateImpl extends DashboardEntityImpl implements SubmissionTemplate {
    private SubmissionCenter submissionCenter;
    private String description;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String project;
    private Integer tier;
    private Boolean complete;
    private Date dateLastModified;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(targetEntity = SubmissionCenterImpl.class)
    public SubmissionCenter getSubmissionCenter() {
        return submissionCenter;
    }

    public void setSubmissionCenter(SubmissionCenter submissionCenter) {
        this.submissionCenter = submissionCenter;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Date getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Date dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
