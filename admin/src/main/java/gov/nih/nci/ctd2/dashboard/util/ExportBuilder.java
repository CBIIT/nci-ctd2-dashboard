package gov.nih.nci.ctd2.dashboard.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;

public class ExportBuilder {
    private static Log log = LogFactory.getLog(ExportBuilder.class);

    @Autowired
    private DashboardDao dashboardDao;

    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    public void prepareData(String downloadFileLocation, Boolean zipExport) {
        dashboardDao.masterExport(downloadFileLocation, zipExport);
        log.debug("finish preparing download data");
    }
}
