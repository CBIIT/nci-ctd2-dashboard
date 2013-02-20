package gov.nih.nci.ctd2.dashboard.importer;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractImporter implements Runnable {
    @Autowired
    private DashboardDao dashboardDao;
    @Autowired
    private DashboardFactory dashboardFactory;

    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    public DashboardFactory getDashboardFactory() {
        return dashboardFactory;
    }

    public void setDashboardFactory(DashboardFactory dashboardFactory) {
        this.dashboardFactory = dashboardFactory;
    }
}
