package gov.nih.nci.ctd2.dashboard.model;

public interface ObservedSubject extends DashboardEntity {
    public Subject getSubject();
    public void setSubject(Subject subject);
    public ObservedSubjectRole getObservedSubjectRole();
    public void setObservedSubjectRole(ObservedSubjectRole observedSubjectRole);
    public Observation getObservation();
    public void setObservation(Observation observation);
}
