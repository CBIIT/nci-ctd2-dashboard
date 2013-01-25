package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.*;

import java.util.ArrayList;
import java.util.List;

public class ObservationImpl extends DashboardEntityImpl implements Observation {
    private List<Subject> subjects = new ArrayList<Subject>();
    private ObservationSource observationSource;
    private ObservationType observationType;
    private ObservationReference observationReference;

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public ObservationSource getObservationSource() {
        return observationSource;
    }

    public void setObservationSource(ObservationSource observationSource) {
        this.observationSource = observationSource;
    }

    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }

    public ObservationReference getObservationReference() {
        return observationReference;
    }

    public void setObservationReference(ObservationReference observationReference) {
        this.observationReference = observationReference;
    }
}
