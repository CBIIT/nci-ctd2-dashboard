package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DashboardEntityWithCounts implements Serializable {
    private DashboardEntity dashboardEntity;
    private int observationCount = 0;
    private int centerCount = 0;
    private int maxTier = 0;
    private Set<String> roles = new HashSet<String>();

    public DashboardEntity getDashboardEntity() {
        return dashboardEntity;
    }

    public void setDashboardEntity(DashboardEntity dashboardEntity) {
        this.dashboardEntity = dashboardEntity;
    }

    public int getObservationCount() {
        return observationCount;
    }

    public void setObservationCount(int observationCount) {
        this.observationCount = observationCount;
    }

    public int getCenterCount() {
        return centerCount;
    }

    public void setCenterCount(int centerCount) {
        this.centerCount = centerCount;
    }

    public int getMaxTier() {
        return maxTier;
    }

    public void setMaxTier(int maxTier) {
        this.maxTier = maxTier;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
