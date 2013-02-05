package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
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

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = SubjectImpl.class)
    @JoinTable(name = "observation_subject_map")
    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @ManyToOne(targetEntity = ObservationSourceImpl.class, cascade = CascadeType.ALL)
    public ObservationSource getObservationSource() {
        return observationSource;
    }

    public void setObservationSource(ObservationSource observationSource) {
        this.observationSource = observationSource;
    }

    @ManyToOne(targetEntity = ObservationTypeImpl.class, cascade = CascadeType.ALL)
    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }

    @ManyToOne(targetEntity = ObservationReferenceImpl.class, cascade = CascadeType.ALL)
    public ObservationReference getObservationReference() {
        return observationReference;
    }

    public void setObservationReference(ObservationReference observationReference) {
        this.observationReference = observationReference;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = EvidenceImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "observation_evidence_map")
    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }
}
