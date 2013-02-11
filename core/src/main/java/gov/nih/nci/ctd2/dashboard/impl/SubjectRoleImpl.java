package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = SubjectRole.class)
@Table(name = "subject_role")
public class SubjectRoleImpl extends DashboardEntityImpl implements SubjectRole {
    private String subjectClassName;

    @Column(length = 32, nullable = false)
    public String getSubjectClassName() {
        return subjectClassName;
    }

    public void setSubjectClassName(String subjectClassName) {
        this.subjectClassName = subjectClassName;
    }
}
