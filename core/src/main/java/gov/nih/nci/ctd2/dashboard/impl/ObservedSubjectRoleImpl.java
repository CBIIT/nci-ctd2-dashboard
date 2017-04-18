package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Table(name="observed_subject_role")
@Proxy(proxyClass = ObservedSubjectRole.class)
public class ObservedSubjectRoleImpl extends DashboardEntityImpl implements ObservedSubjectRole {
    private String displayText;
    private String columnName;
    private SubjectRole subjectRole;
    private ObservationTemplate observationTemplate;

    @Column(length = 10240)
    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Column(length = 1024)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @ManyToOne(targetEntity = SubjectRoleImpl.class)
    public SubjectRole getSubjectRole() {
        return subjectRole;
    }

    public void setSubjectRole(SubjectRole subjectRole) {
        this.subjectRole = subjectRole;
    }

    @ManyToOne(targetEntity = ObservationTemplateImpl.class)
    public ObservationTemplate getObservationTemplate() {
        return observationTemplate;
    }

    public void setObservationTemplate(ObservationTemplate observationTemplate) {
        this.observationTemplate = observationTemplate;
    }
}
