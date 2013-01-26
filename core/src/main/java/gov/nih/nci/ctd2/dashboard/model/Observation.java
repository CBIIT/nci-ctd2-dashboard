package gov.nih.nci.ctd2.dashboard.model;

import java.util.List;

public interface Observation extends DashboardEntity {
    public List<Subject> getSubjects();
    public void setSubjects(List<Subject> subjects);
    public ObservationSource getObservationSource();
    public void setObservationSource(ObservationSource observationSource);
    public ObservationType getObservationType();
    public void setObservationType(ObservationType observationType);
    public ObservationReference getObservationReference();
    public void setObservationReference(ObservationReference observationReference);
    public List<Evidence> getEvidences();
    public void setEvidences(List<Evidence> evidences);
}
