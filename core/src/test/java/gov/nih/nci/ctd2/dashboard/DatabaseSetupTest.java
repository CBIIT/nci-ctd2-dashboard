package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DatabaseSetupTest {
    private DashboardDao dashboardDao;
    private DashboardFactory dashboardFactory;

    @Before
    public void initiateDao() {
        ApplicationContext appContext =
                new ClassPathXmlApplicationContext("classpath*:META-INF/spring/testApplicationContext.xml");
        this.dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");
        this.dashboardFactory = (DashboardFactory) appContext.getBean("dashboardFactory");
    }

    @Test
    public void createDaoTest() {
        assertNotNull(dashboardDao);
    }

    @Test
    public void createAndPersistTest() {
        Synonym synonym = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S1");

        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S2");

        Synonym synonym3 = dashboardFactory.create(Synonym.class);
        synonym3.setDisplayName("S3");

        // Save with id
        Gene gene = dashboardFactory.create(Gene.class, 1);
        gene.setDisplayName("G1");
        gene.getSynonyms().add(synonym);
        gene.getSynonyms().add(synonym2);
        dashboardDao.save(gene);

        // save without id
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene.setDisplayName("G2");
        dashboardDao.save(gene2);

        Transcript transcript = dashboardFactory.create(Transcript.class);
        transcript.setGene(gene2);
        transcript.setRefseqId("NM_21431");
        gene.setDisplayName("T1");
        dashboardDao.save(transcript);

        Protein protein = dashboardFactory.create(Protein.class);
        protein.setTranscript(transcript);
        protein.setUniprotId("1000");
        protein.setDisplayName("P1");
        dashboardDao.save(protein);

        MouseModel mouseModel = dashboardFactory.create(MouseModel.class);
        mouseModel.getSynonyms().add(synonym3);
        mouseModel.setDisplayName("MM1");
        dashboardDao.save(mouseModel);

        UrlEvidence urlEvidence = dashboardFactory.create(UrlEvidence.class);
        urlEvidence.setUrl("http://ctd2.nci.nih.gov/");

        LabelEvidence labelEvidence = dashboardFactory.create(LabelEvidence.class);
        labelEvidence.setDisplayName("L1");

        ObservationReference observationReference = dashboardFactory.create(ObservationReference.class);
        observationReference.setDisplayName("OR1");

        ObservationSource observationSource = dashboardFactory.create(ObservationSource.class);
        observationSource.setDisplayName("OS1");

        ObservationType observationType = dashboardFactory.create(ObservationType.class);
        observationType.setDisplayName("OT1");

        Observation observation = dashboardFactory.create(Observation.class);
        observation.setObservationReference(observationReference);
        observation.setObservationType(observationType);
        observation.setObservationSource(observationSource);
        observation.getSubjects().add(mouseModel);
        observation.getSubjects().add(gene2);
        observation.getSubjects().add(protein);
        observation.getEvidences().add(urlEvidence);
        observation.getEvidences().add(labelEvidence);
        dashboardDao.save(observation);
    }

    @Test
    public void saveAndDeleteTest() {
        Synonym synonym = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S1");

        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S2");

        // Save with id
        Gene gene = dashboardFactory.create(Gene.class, 1);
        gene.setDisplayName("G1");
        gene.getSynonyms().add(synonym);
        gene.getSynonyms().add(synonym2);
        dashboardDao.save(gene);
        assertEquals(1, dashboardDao.countEntities(Gene.class).intValue());
        assertEquals(2, dashboardDao.countEntities(Synonym.class).intValue());
        dashboardDao.delete(gene);
        assertEquals(0, dashboardDao.countEntities(Gene.class).intValue());
        assertEquals(0, dashboardDao.countEntities(Synonym.class).intValue());
    }

    @Test
    public void findByIdTest() {
        Gene gene1 = dashboardFactory.create(Gene.class);
        Gene gene2 = dashboardFactory.create(Gene.class);
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        assertNotNull(dashboardDao.getEntityById(gene1.getId()));
        assertNotNull(dashboardDao.getEntityById(gene2.getId()));
        assertNull(dashboardDao.getEntityById(gene1.getId() + 100));
        assertNotNull(dashboardDao.getEntityById(Gene.class, gene1.getId()));
        assertNull(dashboardDao.getEntityById(Protein.class, gene1.getId()));
    }

}

