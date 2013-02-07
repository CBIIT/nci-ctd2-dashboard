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
    private String description;
    private String columnName;

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

    @Column(length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 1024)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
