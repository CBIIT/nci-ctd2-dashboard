package gov.nih.nci.ctd2.dashboard.model;

public interface ObservationTemplate extends DashboardEntity {
    public String getDescription();
    public void setDescription(String description);
    public String getObservationSummary();
    public void setObservationSummary(String observationSummary);
    public Integer getTier();
    public void setTier(Integer tier);
}
