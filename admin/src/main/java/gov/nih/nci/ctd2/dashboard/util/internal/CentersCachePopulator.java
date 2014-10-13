package gov.nih.nci.ctd2.dashboard.util.internal;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.util.CachePopulator;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CentersCachePopulator extends CachePopulator {
    private static Log log = LogFactory.getLog(CentersCachePopulator.class);

    @Autowired
    private WebServiceUtil webServiceUtil;

    @Override
    public void populate() {
        List<? extends DashboardEntity> centers = webServiceUtil.getDashboardEntities("center", null);
        log.info("Populating cache for submission centers...");
        for (DashboardEntity dashboardEntity : centers) {
            assert dashboardEntity instanceof SubmissionCenter;
            SubmissionCenter center = (SubmissionCenter) dashboardEntity;
            log.info("Populating cache for center: " + center.getDisplayName());
            webServiceUtil.getDashboardEntities("observation", center.getId());
        }
        log.info("Done populating cache for submission centers.");
    }
}
