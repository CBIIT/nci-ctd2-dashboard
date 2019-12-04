package gov.nih.nci.ctd2.dashboard.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;

public class OverallSummary {
    private static Log log = LogFactory.getLog(OverallSummary.class);

    @Autowired
    private DashboardDao dashboardDao;

    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    public void summarize() {
        dashboardDao.summarize();
        log.debug("done with overall summary");
    }
}
