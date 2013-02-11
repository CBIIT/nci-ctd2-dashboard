package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static junit.framework.Assert.assertNotNull;

public class AdminTest {
    private DashboardDao dashboardDao;
    private DashboardFactory dashboardFactory;
    private ApplicationContext appContext;

    @Before
    public void initiateDao() {
        this.appContext = new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/testApplicationContext.xml", // this is coming from the core module
                "classpath*:META-INF/spring/testAdminApplicationContext.xml" // and this from the admin module
        );

        this.dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");
        this.dashboardFactory = (DashboardFactory) appContext.getBean("dashboardFactory");
    }

    @Test
    public void dummyTest() {
        assertNotNull(this.dashboardDao);
        assertNotNull(this.dashboardFactory);
    }
}
