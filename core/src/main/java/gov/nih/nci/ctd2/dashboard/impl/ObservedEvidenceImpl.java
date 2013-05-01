package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Evidence;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidence;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = ObservedEvidence.class)
@Table(name = "observed_evidence")
public class ObservedEvidenceImpl extends DashboardEntityImpl implements ObservedEvidence {
    private Evidence evidence;
    private ObservedEvidenceRole observedEvidenceRole;
    private Observation observation;

    @ManyToOne(targetEntity = EvidenceImpl.class)
    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    @ManyToOne(targetEntity = ObservedEvidenceRoleImpl.class)
    public ObservedEvidenceRole getObservedEvidenceRole() {
        return observedEvidenceRole;
    }

    public void setObservedEvidenceRole(ObservedEvidenceRole observedEvidenceRole) {
        this.observedEvidenceRole = observedEvidenceRole;
    }

    @ManyToOne(targetEntity = ObservationImpl.class)
    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }
}
