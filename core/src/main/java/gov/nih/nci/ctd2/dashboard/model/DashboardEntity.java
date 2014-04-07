package gov.nih.nci.ctd2.dashboard.model;

import java.io.Serializable;

public interface DashboardEntity extends Serializable {
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public Integer getId();
    public void setId(Integer id);
}
