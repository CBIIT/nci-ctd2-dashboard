package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseSetupTest {
    @Test
    public void testBeanCreateAndPersist() {
        ApplicationContext appContext =
                new ClassPathXmlApplicationContext("classpath*:META-INF/spring/testApplicationContext.xml");
        DashboardDao dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");
        assertNotNull(dashboardDao);

        DashboardFactory dashboardFactory = new DashboardFactory();
        Gene gene = dashboardFactory.create(Gene.class, 1);
        dashboardDao.save(gene);

        Gene gene2 = dashboardFactory.create(Gene.class);
        dashboardDao.save(gene2);
    }

}

