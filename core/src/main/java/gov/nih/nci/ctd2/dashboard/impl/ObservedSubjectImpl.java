package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "observed_subject")
@Proxy(proxyClass = ObservedSubject.class)
public class ObservedSubjectImpl extends DashboardEntityImpl implements ObservedSubject {
    private Subject subject;
    private ObservedSubjectRole observedSubjectRole;
    private Observation observation;

    @ManyToOne(targetEntity = SubjectImpl.class)
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @ManyToOne(targetEntity = ObservedSubjectRoleImpl.class)
    public ObservedSubjectRole getObservedSubjectRole() {
        return observedSubjectRole;
    }

    public void setObservedSubjectRole(ObservedSubjectRole observedSubjectRole) {
        this.observedSubjectRole = observedSubjectRole;
    }

    @ManyToOne(targetEntity = ObservationImpl.class)
    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }
}
