package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
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
    public void createAndStatelessPersistTest() {
        Collection<DashboardEntity> entities = new ArrayList<DashboardEntity>();
        Synonym synonym = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S1");
        entities.add(synonym);

        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S2");
        entities.add(synonym2);

        Synonym synonym3 = dashboardFactory.create(Synonym.class);
        synonym3.setDisplayName("S3");
        entities.add(synonym3);

        // Save with id
        Gene gene = dashboardFactory.create(Gene.class);
        gene.setDisplayName("G1");
        gene.setEntrezGeneId("E1");
        gene.getSynonyms().add(synonym);
        gene.getSynonyms().add(synonym2);
        entities.add(gene);

        // save without id
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        gene.setDisplayName("G2");
        entities.add(gene2);

        Transcript transcript = dashboardFactory.create(Transcript.class);
        transcript.setGene(gene2);
        transcript.setRefseqId("NM_21431");
        gene.setDisplayName("T1");
        entities.add(transcript);

        Protein protein = dashboardFactory.create(Protein.class);
        protein.getTranscripts().add(transcript);
        protein.setUniprotId("1000");
        protein.setDisplayName("P1");
        entities.add(protein);

        AnimalModel animalModel = dashboardFactory.create(AnimalModel.class);
        animalModel.getSynonyms().add(synonym3);
        animalModel.setDisplayName("MM1");
        entities.add(animalModel);

        UrlEvidence urlEvidence = dashboardFactory.create(UrlEvidence.class);
        urlEvidence.setUrl("http://ctd2.nci.nih.gov/");
        entities.add(urlEvidence);

        LabelEvidence labelEvidence = dashboardFactory.create(LabelEvidence.class);
        labelEvidence.setDisplayName("L1");
        entities.add(labelEvidence);

        dashboardDao.batchSave(entities, 10);
    }

    @Test
    public void saveAndUpdateTest() {
        // Save with id
        Gene gene = dashboardFactory.create(Gene.class);
        gene.setDisplayName("G1");
        gene.setEntrezGeneId("E1");
        dashboardDao.save(gene);

        gene.setDisplayName("G1U");
        gene.setScore(10);
        dashboardDao.merge(gene);
    }

    @Test
    public void issue24Test() {
        // See https://bitbucket.org/cbio_mskcc/ctd2-dashboard/issue/24/savestateless-issue
        Collection<DashboardEntity> xrefEntities = new ArrayList<DashboardEntity>();

        // Ok let's create the xrefs
        Xref
                // For the first compound
                xref11 = createXref("11"),
                xref12 = createXref("12"),
                xref13 = createXref("13"),
                // For the second compound
                xref21 = createXref("21"),
                xref22 = createXref("22"),
                xref23 = createXref("23"),
                xref24 = createXref("24");
        xrefEntities.add(xref11);
        xrefEntities.add(xref12);
        xrefEntities.add(xref13);
        xrefEntities.add(xref21);
        xrefEntities.add(xref22);
        xrefEntities.add(xref23);
        xrefEntities.add(xref24);
        dashboardDao.batchSave(xrefEntities, 3);
        assertEquals(7, dashboardDao.countEntities(Xref.class).intValue());
        for (Xref xref : dashboardDao.findEntities(Xref.class)) {
            dashboardDao.delete(xref);
        }

        // And then the compounds
        Collection<DashboardEntity> compoundEntities = new ArrayList<DashboardEntity>();
        Compound compound1 = dashboardFactory.create(Compound.class);
        compound1.setDisplayName("cmp1");
        compound1.setSmilesNotation("OHO");
        compound1.getXrefs().add(xref11);
        compound1.getXrefs().add(xref12);
        compound1.getXrefs().add(xref13);

        Compound compound2 = dashboardFactory.create(Compound.class);
        compound2.setDisplayName("cmp2");
        compound2.setSmilesNotation("OH");
        compound2.getXrefs().add(xref21);
        compound2.getXrefs().add(xref22);
        compound2.getXrefs().add(xref23);
        compound2.getXrefs().add(xref24);

        compoundEntities.add(compound1);
        compoundEntities.add(compound2);
        dashboardDao.batchSave(compoundEntities, 2);

        // Let's check if everything is alright
        assertEquals(7, dashboardDao.countEntities(Xref.class).intValue());
        assertEquals(2, dashboardDao.countEntities(Compound.class).intValue());

        List<Compound> ohos = dashboardDao.findCompoundsBySmilesNotation("OHO");
        assertEquals(1, ohos.size());
        assertEquals(3, ohos.get(0).getXrefs().size());

        List<Compound> ohs = dashboardDao.findCompoundsBySmilesNotation("OH");
        assertEquals(1, ohs.size());
        assertEquals(4, ohs.get(0).getXrefs().size());
    }

    private Xref createXref(String id) {
        Xref xref = dashboardFactory.create(Xref.class);
        xref.setDatabaseId(id);
        xref.setDatabaseName("dummy");
        return xref;
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
    public void findGenesBySymbolTest() {
        String e1 = "22880";
        String s1 = "symbol-1";
        String e2 = "74522";
        String s2 = "symbol-2";

        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setEntrezGeneId(e1);
		gene1.setDisplayName(s1);
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId(e2);
		gene2.setDisplayName(s2);
        dashboardDao.save(gene1);
        dashboardDao.save(gene2);

        List<Gene> s1genes = dashboardDao.findGenesBySymbol(s1);
        assertEquals(1, s1genes.size());
        List<Gene> s2genes = dashboardDao.findGenesBySymbol(s2);
        assertEquals(1, s2genes.size());
        assertTrue(dashboardDao.findGenesBySymbol("symbol-3").isEmpty());
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
        String refseq1 = "NM_014219";
        String refseq2 = "NM_203373";
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
        assertTrue(dashboardDao.findProteinsByUniprotId("NM_104573").isEmpty());
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

        Annotation annotation1 = dashboardFactory.create(Annotation.class);
        annotation1.setDisplayName("lymphoid_neoplasm");
        annotation1.setType("primary_site");
        annotation1.setSource("COSMIC (Sanger)");

        Annotation annotation2 = dashboardFactory.create(Annotation.class);
        annotation2.setDisplayName("haematopoietic_and_lymphoid_tissue");
        annotation2.setType("site_subtype_1");
        annotation2.setSource("CCLE (Broad/Novartis)");

        CellSample cellSample1 = dashboardFactory.create(CellSample.class);
        cellSample1.setDisplayName("CL1");
        cellSample1.getXrefs().add(xref1);
        cellSample1.getAnnotations().add(annotation1);
        dashboardDao.save(cellSample1);

        CellSample cellSample2 = dashboardFactory.create(CellSample.class);
        cellSample2.setDisplayName("CL2");
        cellSample2.getXrefs().add(xref2);
        cellSample2.getXrefs().add(xref3);
        cellSample2.getAnnotations().add(annotation2);
        dashboardDao.save(cellSample2);

        List<CellSample> cellSamples = dashboardDao.findCellSampleByAnnoSource("COSMIC (Sanger)");
        assertEquals(1, cellSamples.size());
        cellSamples = dashboardDao.findCellSampleByAnnoType("primary_site");
        assertEquals(1, cellSamples.size());
        cellSamples = dashboardDao.findCellSampleByAnnoName("lymphoid_neoplasm");
        assertEquals(1, cellSamples.size());

        cellSamples = dashboardDao.findCellSampleByAnnotation(annotation1);
        assertEquals(1, cellSamples.size());
        assertEquals(cellSample1, cellSamples.iterator().next());

        List<Subject> subjects1 = dashboardDao.findSubjectsByXref(xref1);
        assertEquals(1, subjects1.size());
        assertEquals(cellSample1, subjects1.iterator().next());

        List<Subject> subjects2 = dashboardDao.findSubjectsByXref(xref2);
        assertEquals(1, subjects2.size());
        assertEquals(cellSample2, subjects2.iterator().next());

        subjects2 = dashboardDao.findSubjectsByXref(xref3.getDatabaseName(), xref3.getDatabaseId());
        assertEquals(1, subjects2.size());
        assertEquals(cellSample2, subjects2.iterator().next());

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
    public void findObservedRolesByColumnNameTest() {

		ObservationTemplate observationTemplate = dashboardFactory.create(ObservationTemplate.class);
		observationTemplate.setDisplayName("template_name");
        observationTemplate.setPrincipalInvestigator("PI");
        dashboardDao.save(observationTemplate);

        EvidenceRole evidenceRole = dashboardFactory.create(EvidenceRole.class);
        evidenceRole.setDisplayName("ER1");
        dashboardDao.save(evidenceRole);

        ObservedEvidenceRole observedEvidenceRole = dashboardFactory.create(ObservedEvidenceRole.class);
		observedEvidenceRole.setObservationTemplate(observationTemplate);
        observedEvidenceRole.setEvidenceRole(evidenceRole);
        String columnName = "role_column1";
        observedEvidenceRole.setColumnName(columnName);
        observedEvidenceRole.setDisplayText("description 1");
        dashboardDao.save(observedEvidenceRole);

		assertTrue(dashboardDao.findObservedEvidenceRole("template_name", columnName) != null);

        SubjectRole subjectRole = dashboardFactory.create(SubjectRole.class);
        subjectRole.setDisplayName("SR1");
        dashboardDao.save(subjectRole);

        ObservedSubjectRole observedSubjectRole = dashboardFactory.create(ObservedSubjectRole.class);
		observedSubjectRole.setObservationTemplate(observationTemplate);
        observedSubjectRole.setSubjectRole(subjectRole);
        observedSubjectRole.setColumnName(columnName);
        observedSubjectRole.setDisplayText("description 1");
        dashboardDao.save(observedSubjectRole);

		assertTrue(dashboardDao.findObservedSubjectRole("template_name", columnName) != null);
    }

    @Test
    public void indexTest() {
        String synonymStr = "Synonym";

        String synStr1 = synonymStr + "11";
        Synonym synonym1 = dashboardFactory.create(Synonym.class);
        synonym1.setDisplayName(synStr1);

        String synStr2 = synonymStr + "12";
        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym2.setDisplayName(synStr2);

        String synStr3 = synonymStr + " 21";
        Synonym synonym3 = dashboardFactory.create(Synonym.class);
        synonym3.setDisplayName(synStr3);

        String synStr4 = synonymStr + " 22";
        Synonym synonym4 = dashboardFactory.create(Synonym.class);
        synonym4.setDisplayName(synStr4);

        Gene gene1 = dashboardFactory.create(Gene.class);
        gene1.setDisplayName("Gene1");
        gene1.setEntrezGeneId("E1");
        gene1.getSynonyms().add(synonym1);
        gene1.getSynonyms().add(synonym2);
        dashboardDao.save(gene1);

        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        gene2.setDisplayName("Gene2");
        gene2.getSynonyms().add(synonym3);
        gene2.getSynonyms().add(synonym4);
        dashboardDao.save(gene2);

        CellSample cellSample1 = dashboardFactory.create(CellSample.class);
        cellSample1.setDisplayName("CL1");
        dashboardDao.save(cellSample1);

        Compound compound = dashboardFactory.create(Compound.class);
        compound.setDisplayName("ABT-737");
        compound.setSmilesNotation("0=0");
        dashboardDao.save(compound);

        ObservationTemplate observationTemplate = dashboardFactory.create(ObservationTemplate.class);
        String obsDesc = "OBS DSEC";
        observationTemplate.setDescription(obsDesc);
        String submsDesc = "SUBMDESC";
        observationTemplate.setSubmissionDescription(submsDesc);
        String submsName = "SUBMNAME";
        observationTemplate.setSubmissionName(submsName);
        observationTemplate.setPrincipalInvestigator("PI");
        dashboardDao.save(observationTemplate);

        dashboardDao.createIndex(10);

        assertTrue(dashboardDao.search("something").isEmpty());

        // Wild-card search with synonyms
        assertEquals(2, dashboardDao.search(synonymStr + "*").size());
        assertEquals(1, dashboardDao.search(synonymStr + "1*").size());
        // But these should work since they are part of the displayName
        assertEquals(2, dashboardDao.search("Gene*").size());
        assertEquals(1, dashboardDao.search("Gene1*").size());
        assertEquals(1, dashboardDao.search("Gene2*").size());

        assertTrue(dashboardDao.search(submsDesc).isEmpty());
        assertTrue(dashboardDao.search(obsDesc).isEmpty());
        assertTrue(dashboardDao.search(submsName).isEmpty());
        Submission submission = dashboardFactory.create(Submission.class);
        submission.setObservationTemplate(observationTemplate);
        dashboardDao.save(submission);

        // The following are tests for tokenization
        assertFalse(dashboardDao.search("ABT-737").isEmpty());
        assertTrue(dashboardDao.search("ABT").isEmpty());
        assertTrue(dashboardDao.search("737").isEmpty());
        assertFalse(dashboardDao.search("ABT*").isEmpty());
    }
}

