package gov.nih.nci.ctd2.dashboard.dao;

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
        Gene gene = dashboardFactory.create(Gene.class);
        gene.setDisplayName("G1");
        gene.setEntrezGeneId("E1");
        gene.getSynonyms().add(synonym);
        gene.getSynonyms().add(synonym2);
        dashboardDao.save(gene);

        // save without id
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        gene.setDisplayName("G2");
        dashboardDao.save(gene2);

        Transcript transcript = dashboardFactory.create(Transcript.class);
        transcript.setGene(gene2);
        transcript.setRefseqId("NM_21431");
        gene.setDisplayName("T1");
        dashboardDao.save(transcript);

        Protein protein = dashboardFactory.create(Protein.class);
        protein.getTranscripts().add(transcript);
        protein.setUniprotId("1000");
        protein.setDisplayName("P1");
        dashboardDao.save(protein);

        AnimalModel animalModel = dashboardFactory.create(AnimalModel.class);
        animalModel.getSynonyms().add(synonym3);
        animalModel.setDisplayName("MM1");
        dashboardDao.save(animalModel);

        UrlEvidence urlEvidence = dashboardFactory.create(UrlEvidence.class);
        urlEvidence.setUrl("http://ctd2.nci.nih.gov/");

        LabelEvidence labelEvidence = dashboardFactory.create(LabelEvidence.class);
        labelEvidence.setDisplayName("L1");
    }

    @Test
    public void saveAndDeleteTest() {
        Synonym synonym = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S1");

        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S2");

        // Save with id
        Gene gene = dashboardFactory.create(Gene.class);
        gene.setDisplayName("G1");
        gene.setEntrezGeneId("E1");
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
        gene1.setEntrezGeneId("E1");
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        assertNotNull(dashboardDao.getEntityById(Subject.class, gene1.getId()));
        assertNotNull(dashboardDao.getEntityById(SubjectWithOrganism.class, gene2.getId()));
        assertNotNull(dashboardDao.getEntityById(Gene.class, gene1.getId()));
        assertNull(dashboardDao.getEntityById(Protein.class, gene1.getId()));
    }

    @Test
    public void findEntitiesVsCountTest() {
        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setEntrezGeneId("E1");
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        assertEquals(dashboardDao.countEntities(Gene.class).intValue(), dashboardDao.findEntities(Gene.class).size());
    }

    @Test
    public void findGenesByEntrezIdTest() {
        String e1 = "22880";
        String e2 = "74522";

        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setEntrezGeneId(e1);
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId(e2);
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        List<Gene> e1genes = dashboardDao.findGenesByEntrezId(e1);
        assertEquals(1, e1genes.size());
        List<Gene> e2genes = dashboardDao.findGenesByEntrezId(e2);
        assertEquals(1, e2genes.size());
        assertNotSame(e1genes.iterator().next(), e2genes.iterator().next());
        assertTrue(dashboardDao.findGenesByEntrezId("12345").isEmpty());
    }

    @Test
    public void findProteinsByUniprotIdTest() {
        String uid1 = "Q50496";
        String uid2 = "Q9Y6X9";

        Protein protein1 = dashboardFactory.create(Protein.class);
        protein1.setUniprotId(uid1);
        dashboardDao.save(protein1);

        Protein protein2 = dashboardFactory.create(Protein.class);
        protein2.setUniprotId(uid2);
        dashboardDao.save(protein2);

        List<Protein> uid1proteins = dashboardDao.findProteinsByUniprotId(uid1);
        assertEquals(1, uid1proteins.size());
        List<Protein> uid2proteins = dashboardDao.findProteinsByUniprotId("Q9Y6X9");
        assertEquals(1, uid2proteins.size());
        assertNotSame(uid1proteins.iterator().next(), uid2proteins.iterator().next());
        assertTrue(dashboardDao.findProteinsByUniprotId("Q1A3Y5").isEmpty());
    }

    @Test
    public void findTranscriptByRefseqIdTest() {
        String refseq1 = "NM_014219.2";
        String refseq2 = "NM_203373.2";
        Transcript transcript1 = dashboardFactory.create(Transcript.class);
        transcript1.setRefseqId(refseq1);
        dashboardDao.save(transcript1);

        Transcript transcript2 = dashboardFactory.create(Transcript.class);
        transcript2.setRefseqId(refseq2);
        dashboardDao.save(transcript2);

        List<Transcript> r1transcripts = dashboardDao.findTranscriptsByRefseqId(refseq1);
        assertEquals(1, r1transcripts.size());
        List<Transcript> r2transcripts = dashboardDao.findTranscriptsByRefseqId(refseq1);
        assertEquals(1, r2transcripts.size());
        assertNotSame(r1transcripts.iterator().next(), r2transcripts.iterator().next());
        assertTrue(dashboardDao.findProteinsByUniprotId("NM_104573.10").isEmpty());
    }

    @Test
    public void findCompoundsBySmilesNotationTest() {
        String pyrethrinII = "COC(=O)C(\\C)=C\\C1C(C)(C)[C@H]1C(=O)O[C@@H]2C(C)=C(C(=O)C2)CC=CC=C";
        String flavopereirin = "CCc(c1)ccc2[n+]1ccc3c2Nc4c3cccc4";
        String nicotine = "CN1CCC[C@H]1c2cccnc2";

        Compound compound1 = dashboardFactory.create(Compound.class);
        compound1.setSmilesNotation(pyrethrinII);
        dashboardDao.save(compound1);

        Compound compound2 = dashboardFactory.create(Compound.class);
        compound2.setSmilesNotation(flavopereirin);
        dashboardDao.save(compound2);

        List<Compound> c1list = dashboardDao.findCompoundsBySmilesNotation(pyrethrinII);
        assertEquals(1, c1list.size());
        List<Compound> c2list = dashboardDao.findCompoundsBySmilesNotation(flavopereirin);
        assertEquals(1, c2list.size());
        assertNotSame(c1list.iterator().next(), c2list.iterator().next());
        assertTrue(dashboardDao.findCompoundsBySmilesNotation(nicotine).isEmpty());
    }

    @Test
    public void findSubjectsByXref() {
        String id1 = "TestId1";
        String db1 = "DashboardTest";
        Xref xref1 = dashboardFactory.create(Xref.class);
        xref1.setDatabaseId(id1);
        xref1.setDatabaseName(db1);

        String id2 = "TestId2";
        String db2 = "DashboardTest2";
        Xref xref2 = dashboardFactory.create(Xref.class);
        xref2.setDatabaseId(id2);
        xref2.setDatabaseName(db2);

        String id3 = "TestId3";
        String db3 = "DashboardTest3";
        Xref xref3 = dashboardFactory.create(Xref.class);
        xref3.setDatabaseId(id3);
        xref3.setDatabaseName(db3);

        CellLine cellLine1 = dashboardFactory.create(CellLine.class);
        cellLine1.setDisplayName("CL1");
        cellLine1.getXrefs().add(xref1);
        dashboardDao.save(cellLine1);

        CellLine cellLine2 = dashboardFactory.create(CellLine.class);
        cellLine2.setDisplayName("CL2");
        cellLine2.getXrefs().add(xref2);
        cellLine2.getXrefs().add(xref3);
        dashboardDao.save(cellLine2);

        List<Subject> subjects1 = dashboardDao.findSubjectsByXref(xref1);
        assertEquals(1, subjects1.size());
        assertEquals(cellLine1, subjects1.iterator().next());

        List<Subject> subjects2 = dashboardDao.findSubjectsByXref(xref2);
        assertEquals(1, subjects2.size());
        assertEquals(cellLine2, subjects2.iterator().next());

        subjects2 = dashboardDao.findSubjectsByXref(xref3.getDatabaseName(), xref3.getDatabaseId());
        assertEquals(1, subjects2.size());
        assertEquals(cellLine2, subjects2.iterator().next());

        assertTrue(dashboardDao.findSubjectsByXref("RandomDB", "RandomId").isEmpty());
    }

    @Test
    public void organismFilteringTest() {
        Organism organism1 = dashboardFactory.create(Organism.class);
        organism1.setTaxonomyId("O1");
        dashboardDao.save(organism1);

        Organism organism2 = dashboardFactory.create(Organism.class);
        organism2.setTaxonomyId("O2");
        dashboardDao.save(organism2);

        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setEntrezGeneId("E1");
        gene1.setOrganism(organism1);
        dashboardDao.save(gene1);

        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        gene2.setOrganism(organism2);
        dashboardDao.save(gene2);

        List<Organism> olist1 = dashboardDao.findOrganismByTaxonomyId("O1");
        assertEquals(1, olist1.size());
        List<Organism> olist2 = dashboardDao.findOrganismByTaxonomyId("O2");
        assertEquals(1, olist2.size());
        assertTrue(dashboardDao.findOrganismByTaxonomyId("O3").isEmpty());

        List<SubjectWithOrganism> subjectByOrganism = dashboardDao.findSubjectByOrganism(olist1.iterator().next());
        assertEquals(1, subjectByOrganism.size());
        assertEquals(gene1.getEntrezGeneId(), ((Gene) subjectByOrganism.iterator().next()).getEntrezGeneId());

        List<SubjectWithOrganism> subjectByOrganism2 = dashboardDao.findSubjectByOrganism(olist2.iterator().next());
        assertEquals(1, subjectByOrganism2.size());
        assertEquals(gene2.getEntrezGeneId(), ((Gene) subjectByOrganism2.iterator().next()).getEntrezGeneId());
    }

    @Test
    public void findSubjectBySynonymTest() {
        String synonymStr = "Synonym";

        String synStr1 = synonymStr + " 11";
        Synonym synonym1 = dashboardFactory.create(Synonym.class);
        synonym1.setDisplayName(synStr1);

        String synStr2 = synonymStr + " 12";
        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym2.setDisplayName(synStr2);

        String synStr3 = synonymStr + " 21";
        Synonym synonym3 = dashboardFactory.create(Synonym.class);
        synonym3.setDisplayName(synStr3);

        String synStr4 = synonymStr + " 22";
        Synonym synonym4 = dashboardFactory.create(Synonym.class);
        synonym4.setDisplayName(synStr4);

        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setEntrezGeneId("E1");
        gene1.getSynonyms().add(synonym1);
        gene1.getSynonyms().add(synonym2);
        dashboardDao.save(gene1);

        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        gene2.getSynonyms().add(synonym3);
        gene2.getSynonyms().add(synonym4);
        dashboardDao.save(gene2);

        List<Subject> l1 = dashboardDao.findSubjectsBySynonym(synStr1, true);
        List<Subject> l2 = dashboardDao.findSubjectsBySynonym(synStr2, true);
        List<Subject> l3 = dashboardDao.findSubjectsBySynonym(synStr3, true);
        List<Subject> l4 = dashboardDao.findSubjectsBySynonym(synStr4, true);

        assertEquals(1, l1.size());
        assertEquals(1, l2.size());
        assertEquals(1, l3.size());
        assertEquals(1, l4.size());

        l1.removeAll(l2);
        l3.removeAll(l4);
        assertTrue(l1.isEmpty());
        assertTrue(l3.isEmpty());

        Subject next2 = l2.iterator().next();
        assertTrue(next2 instanceof Gene);
        assertEquals("E1", ((Gene) next2).getEntrezGeneId());

        Subject next4 = l4.iterator().next();
        assertTrue(next4 instanceof Gene);
        assertEquals("E2", ((Gene) next4).getEntrezGeneId());

        List<Subject> l5 = dashboardDao.findSubjectsBySynonym(synonymStr, false);
        assertEquals(2, l5.size());

        String randomText = "RANDOMTEXT";
        assertTrue(dashboardDao.findSubjectsBySynonym(randomText, true).isEmpty());
        assertTrue(dashboardDao.findSubjectsBySynonym(randomText, false).isEmpty());
    }

    @Test
    public void findObservedRolesByColumnName() {
        EvidenceRole evidenceRole = dashboardFactory.create(EvidenceRole.class);
        evidenceRole.setDisplayName("ER1");
        dashboardDao.save(evidenceRole);

        ObservedEvidenceRole observedEvidenceRole = dashboardFactory.create(ObservedEvidenceRole.class);
        observedEvidenceRole.setEvidenceRole(evidenceRole);
        String columnName = "role_column1";
        observedEvidenceRole.setColumnName(columnName);
        observedEvidenceRole.setDescription("description 1");
        dashboardDao.save(observedEvidenceRole);

        SubjectRole subjectRole = dashboardFactory.create(SubjectRole.class);
        subjectRole.setDisplayName("SR1");
        dashboardDao.save(subjectRole);

        ObservedSubjectRole observedSubjectRole = dashboardFactory.create(ObservedSubjectRole.class);
        observedSubjectRole.setSubjectRole(subjectRole);
        observedSubjectRole.setColumnName(columnName);
        observedSubjectRole.setDescription("description 1");
        dashboardDao.save(observedSubjectRole);

        List<ObservedEvidenceRole> observedEvidenceRoles
                = dashboardDao.findObservedEvidenceRoleByColumnName(columnName);
        assertEquals(1, observedEvidenceRoles.size());

        List<ObservedSubjectRole> observedSubjectRoles
                = dashboardDao.findObservedSubjectRoleByColumnName(columnName);
        assertEquals(1, observedSubjectRoles.size());
    }
}

