package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Proxy(proxyClass = Observation.class)
@Table(name = "observation")
public class ObservationImpl extends DashboardEntityImpl implements Observation {
    private List<Evidence> evidences = new ArrayList<Evidence>();
    private List<Subject> subjects = new ArrayList<Subject>();
    private ObservationSource observationSource;
    private ObservationType observationType;
    private ObservationReference observationReference;

    @OneToMany(targetEntity = SubjectImpl.class)
    @JoinTable(name = "observation_to_subject")
    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @ManyToOne(targetEntity = ObservationSourceImpl.class)
    public ObservationSource getObservationSource() {
        return observationSource;
    }

    public void setObservationSource(ObservationSource observationSource) {
        this.observationSource = observationSource;
    }

    @ManyToOne(targetEntity = ObservationTypeImpl.class)
    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }

    @ManyToOne(targetEntity = ObservationReferenceImpl.class)
    public ObservationReference getObservationReference() {
        return observationReference;
    }

    public void setObservationReference(ObservationReference observationReference) {
        this.observationReference = observationReference;
    }

    @OneToMany(targetEntity = EvidenceImpl.class)
    @JoinTable(name = "observation_to_entity")
    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }
}
