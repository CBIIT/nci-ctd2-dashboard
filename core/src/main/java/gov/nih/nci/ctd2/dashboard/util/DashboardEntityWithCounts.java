package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DashboardEntityWithCounts implements Serializable {
    private static final long serialVersionUID = -1315522615138058767L;

    private DashboardEntity dashboardEntity;
    private int observationCount = 0;
    private int centerCount = 0;
    private int maxTier = 0;
    private Set<String> roles = new HashSet<String>();
    private int matchNumber = 0;

    public DashboardEntityWithCounts() {
    }

    public DashboardEntityWithCounts(DashboardEntity dashboardEntity, int observationCount, int centerCount) {
        this.dashboardEntity = dashboardEntity;
        this.observationCount = observationCount;
        this.centerCount = centerCount;
    }

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

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int x) {
        matchNumber = x;
    }

    @Override
    public int hashCode() {
        return dashboardEntity.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DashboardEntityWithCounts))
            return false;

        DashboardEntityWithCounts e = (DashboardEntityWithCounts) o;
        if (e.dashboardEntity.equals(this.dashboardEntity))
            return true;
        else
            return false;
    }
}
