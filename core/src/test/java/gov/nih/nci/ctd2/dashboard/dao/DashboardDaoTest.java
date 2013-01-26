package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.*;

public class DashboardDaoTest {
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

    @Test
    public void findEntitiesVsCountTest() {
        Gene gene1 = dashboardFactory.create(Gene.class);
        Gene gene2 = dashboardFactory.create(Gene.class);
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        assertEquals(dashboardDao.countEntities(Gene.class).intValue(), dashboardDao.findEntities(Gene.class).size());
    }

    @Test
    public void findGenesByEntrezIdTest() {
        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setEntrezGeneId("E1");
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        List<Gene> e1genes = dashboardDao.findGenesByEntrezId("E1");
        assertEquals(1, e1genes.size());
        List<Gene> e2genes = dashboardDao.findGenesByEntrezId("E2");
        assertEquals(1, e2genes.size());
        assertNotSame(e1genes.iterator().next(), e2genes.iterator().next());
        assertTrue(dashboardDao.findGenesByEntrezId("E3").isEmpty());
    }

    @Test
    public void findProteinsByUniprotIdTest() {
        Protein protein1 = dashboardFactory.create(Protein.class);
        protein1.setUniprotId("UID1");
        dashboardDao.save(protein1);

        Protein protein2 = dashboardFactory.create(Protein.class);
        protein2.setUniprotId("UID2");
        dashboardDao.save(protein2);

        List<Protein> uid1proteins = dashboardDao.findProteinsByUniprotId("UID1");
        assertEquals(1, uid1proteins.size());
        List<Protein> uid2proteins = dashboardDao.findProteinsByUniprotId("UID2");
        assertEquals(1, uid2proteins.size());
        assertNotSame(uid1proteins.iterator().next(), uid2proteins.iterator().next());
        assertTrue(dashboardDao.findProteinsByUniprotId("UID3").isEmpty());
    }

    @Test
    public void findTranscriptByRefseqIdTest() {
        Transcript transcript1 = dashboardFactory.create(Transcript.class);
        transcript1.setRefseqId("R1");
        dashboardDao.save(transcript1);

        Transcript transcript2 = dashboardFactory.create(Transcript.class);
        transcript2.setRefseqId("R2");
        dashboardDao.save(transcript2);

        List<Transcript> r1transcripts = dashboardDao.findTranscriptsByRefseqId("R1");
        assertEquals(1, r1transcripts.size());
        List<Transcript> r2transcripts = dashboardDao.findTranscriptsByRefseqId("R2");
        assertEquals(1, r2transcripts.size());
        assertNotSame(r1transcripts.iterator().next(), r2transcripts.iterator().next());
        assertTrue(dashboardDao.findProteinsByUniprotId("R3").isEmpty());
    }
}

