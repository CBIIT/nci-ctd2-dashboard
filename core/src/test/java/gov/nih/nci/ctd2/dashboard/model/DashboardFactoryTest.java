package gov.nih.nci.ctd2.dashboard.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DashboardFactoryTest {
    @Test
    public void testBeanCreate() {
        DashboardFactory dashboardFactory = new DashboardFactory();
        int id = 0;
        assertNotNull(dashboardFactory.create(ShRna.class, id++));
        assertNotNull(dashboardFactory.create(CellLine.class, id++));
        assertNotNull(dashboardFactory.create(Gene.class, id++));
        assertNotNull(dashboardFactory.create(MouseModel.class, id++));
        assertNotNull(dashboardFactory.create(Observation.class, id++));
        assertNotNull(dashboardFactory.create(Protein.class, id++));
        assertNotNull(dashboardFactory.create(DataNumericValue.class, id++));
        FileEvidence fileEvidence = dashboardFactory.create(FileEvidence.class, id);
        assertNotNull(fileEvidence);
        assertEquals(id, fileEvidence.getId().intValue());
    }

}
