package gov.nih.nci.ctd2.dashboard.util;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

import java.io.Serializable;

public class DashboardEntityWithCounts implements Serializable {
    private DashboardEntity dashboardEntity;
    private int observationCount = 0;

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
}
