package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.EvidenceRole;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "observed_evidence_role")
@Proxy(proxyClass = ObservedEvidenceRole.class)
public class ObservedEvidenceRoleImpl extends DashboardEntityImpl implements ObservedEvidenceRole {
    private ObservationTemplate observationTemplate;
    private EvidenceRole evidenceRole;
    private String displayText;
    private String columnName;
    private String attribute;

    @ManyToOne(targetEntity = ObservationTemplateImpl.class)
    public ObservationTemplate getObservationTemplate() {
        return observationTemplate;
    }

    public void setObservationTemplate(ObservationTemplate observationTemplate) {
        this.observationTemplate = observationTemplate;
    }

    @ManyToOne(targetEntity = EvidenceRoleImpl.class)
    public EvidenceRole getEvidenceRole() {
        return evidenceRole;
    }

    public void setEvidenceRole(EvidenceRole evidenceRole) {
        this.evidenceRole = evidenceRole;
    }

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

    @Column(length = 128)
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
