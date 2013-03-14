package gov.nih.nci.ctd2.dashboard.model;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DashboardFactoryTest {
    @Test
    public void testBeanCreate() {
        DashboardFactory dashboardFactory;

        ApplicationContext appContext =
                new ClassPathXmlApplicationContext("classpath*:META-INF/spring/testApplicationContext.xml");
        dashboardFactory = (DashboardFactory) appContext.getBean("dashboardFactory");

        assertNotNull(dashboardFactory.create(ShRna.class));
        assertNotNull(dashboardFactory.create(CellSample.class));
        assertNotNull(dashboardFactory.create(Gene.class));
        assertNotNull(dashboardFactory.create(AnimalModel.class));
        assertNotNull(dashboardFactory.create(Observation.class));
        assertNotNull(dashboardFactory.create(Protein.class));
        assertNotNull(dashboardFactory.create(DataNumericValue.class));
        FileEvidence fileEvidence = dashboardFactory.create(FileEvidence.class);
        assertNotNull(fileEvidence);
        assertEquals(null, fileEvidence.getId());
    }

}
