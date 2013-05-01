package gov.nih.nci.ctd2.dashboard.model;

public interface ObservedEvidence extends DashboardEntity {
    public Evidence getEvidence();
    public void setEvidence(Evidence evidence);
    public ObservedEvidenceRole getObservedEvidenceRole();
    public void setObservedEvidenceRole(ObservedEvidenceRole observedEvidenceRole);
    public Observation getObservation();
    public void setObservation(Observation observation);
}
