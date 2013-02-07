package gov.nih.nci.ctd2.dashboard.model;

public interface ObservationTemplate extends DashboardEntity {
    public String getDescription();
    public void setDescription(String description);
    public Integer getTier();
    public void setTier(Integer tier);
}
