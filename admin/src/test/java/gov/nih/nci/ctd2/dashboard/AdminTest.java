package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.importer.internal.CellLineNameFieldSetMapper;
import gov.nih.nci.ctd2.dashboard.importer.internal.CompoundsFieldSetMapper;
import gov.nih.nci.ctd2.dashboard.importer.internal.CompoundStructuresFieldSetMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import org.junit.Test;
import org.junit.Before;
import static junit.framework.Assert.*;
import java.util.List;

public class AdminTest {
    private DashboardDao dashboardDao;
    private DashboardFactory dashboardFactory;
    private ApplicationContext appContext;
    private JobLauncher jobLauncher;
	private JobExecution jobExecution;

    @Before
    public void initiateDao() {
        this.appContext = new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/testApplicationContext.xml", // this is coming from the core module
				"classpath*:META-INF/spring/testCellLineDataApplicationContext.xml", // and this is for cellLine data importer beans
				"classpath*:META-INF/spring/testCompoundDataApplicationContext.xml", // and this is for compound data importer beans
				"classpath*:META-INF/spring/testGeneDataApplicationContext.xml", // and this is for gene data importer beans
				"classpath*:META-INF/spring/testProteinDataApplicationContext.xml", // and this is for protein data importer beans
				"classpath*:META-INF/spring/testControlledVocabularyApplicationContext.xml", // and this is for controlled vocabulary importer beans
				"classpath*:META-INF/spring/testObservationDataApplicationContext.xml", // and this is for observation data importer beans
				"classpath*:META-INF/spring/taxonomyDataApplicationContext.xml" // and this is for taxonomy data importer beans
        );

        this.dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");
        this.dashboardFactory = (DashboardFactory) appContext.getBean("dashboardFactory");
		this.jobLauncher = (JobLauncher) appContext.getBean("jobLauncher");
    }

    @Test
    public void dummyTest() {
        assertNotNull(this.dashboardDao);
        assertNotNull(this.dashboardFactory);
    }

	@Test
	public void importerTest() throws Exception {

		// import taxonomy data
		jobExecution = executeJob("taxonomyDataImporterJob");
		assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(2, dashboardDao.countEntities(Organism.class).intValue());
		List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId("9606");
		assertEquals(1, organisms.size());
		assertEquals("Homo sapiens", organisms.iterator().next().getDisplayName());

		// import some cell line data
		jobExecution = executeJob("cellLineDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(13, dashboardDao.countEntities(CellSample.class).intValue());
		List<Subject> cellSampleSubjects = dashboardDao.findSubjectsByXref("CTD2", "idCell:9");
		assertEquals(1, cellSampleSubjects.size());
		CellSample cellSample = (CellSample)cellSampleSubjects.iterator().next();
		assertEquals("697", cellSample.getDisplayName());
		assertEquals("HAEMATOPOIETIC_AND_LYMPHOID_TISSUE", cellSample.getLineage());
		assertEquals(3, cellSample.getSynonyms().size());
		assertEquals(8, cellSample.getXrefs().size());
		cellSampleSubjects = dashboardDao.findSubjectsByXref(CellLineNameFieldSetMapper.CBIO_PORTAL,
															 "5637_URINARY_TRACT");
		assertEquals(1, cellSampleSubjects.size());

		// import some compound data
		jobExecution = executeJob("compoundDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(9, dashboardDao.countEntities(Compound.class).intValue());
		List<Subject> compoundSubjects = 
			dashboardDao.findSubjectsByXref(CompoundsFieldSetMapper.BROAD_COMPOUND_DATABASE, "411739");
		assertEquals(1, compoundSubjects.size());
		List<Compound> compounds = dashboardDao.findCompoundsBySmilesNotation("CCCCCCCCC1OC(=O)C(=C)C1C(O)=O");
        assertEquals(1, compounds.size());
		assertEquals(3, compounds.iterator().next().getSynonyms().size());
		List<Subject> compoundSubjectsWithImage = 
			dashboardDao.findSubjectsByXref(CompoundStructuresFieldSetMapper.COMPOUND_IMAGE_DATABASE,
											"BRD-A01145011.png");
		assertEquals(1, compoundSubjectsWithImage.size());
		assertEquals("zebularine", compoundSubjectsWithImage.iterator().next().getDisplayName());

		// import some gene data
		jobExecution = executeJob("geneDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(19, dashboardDao.countEntities(Gene.class).intValue());
		List<Gene> genes = dashboardDao.findGenesByEntrezId("7529");
		assertEquals(1, genes.size());
		assertEquals(5, genes.iterator().next().getSynonyms().size());
		List<Subject> geneSubjects = dashboardDao.findSubjectsBySynonym("RB1", true);
		assertEquals(1, geneSubjects.size());

		// import some protein data
		jobExecution = executeJob("proteinDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(15, dashboardDao.countEntities(Protein.class).intValue());
		List<Protein> proteins = dashboardDao.findProteinsByUniprotId("P31946");
		assertEquals(1, proteins.size());
		// some transcripts get created along with proteins
		assertEquals(35, dashboardDao.countEntities(Transcript.class).intValue());
		List<Transcript> transcripts = dashboardDao.findTranscriptsByRefseqId("NM_003404.3");
		assertEquals(1, transcripts.size());

		// import controlled vocabulary
		jobExecution = executeJob("controlledVocabularyImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		// we get some subject/observed subject roles
		assertEquals(5, dashboardDao.countEntities(SubjectRole.class).intValue());
		assertEquals(16, dashboardDao.countEntities(ObservedSubjectRole.class).intValue());
		assertTrue(dashboardDao.findObservedSubjectRole("broad_compound_sensitivity_enrichment", "compound_name") != null);
		// we get some evidence/observed evidence roles
		assertEquals(4, dashboardDao.countEntities(EvidenceRole.class).intValue());
		assertEquals(43, dashboardDao.countEntities(ObservedEvidenceRole.class).intValue());
		assertTrue(dashboardDao.findObservedEvidenceRole("broad_compound_sensitivity_enrichment", "cell_line_subset") != null);
		// we get observation template data
		assertEquals(4, dashboardDao.countEntities(ObservationTemplate.class).intValue());
		assertTrue(dashboardDao.findObservationTemplateByName("broad_compound_sensitivity_enrichment") != null);

		// import observation data
		jobExecution = executeJob("testTierOneObservationDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(1, dashboardDao.countEntities(Submission.class).intValue());
		assertEquals(1, dashboardDao.countEntities(SubmissionCenter.class).intValue());
		assertEquals(9, dashboardDao.countEntities(Observation.class).intValue());
		assertEquals(27, dashboardDao.countEntities(ObservedSubject.class).intValue());
		assertEquals(99, dashboardDao.countEntities(ObservedEvidence.class).intValue());
		assertEquals(36, dashboardDao.countEntities(LabelEvidence.class).intValue());
		assertEquals(27, dashboardDao.countEntities(DataNumericValue.class).intValue());
		assertEquals(27, dashboardDao.countEntities(FileEvidence.class).intValue());
		assertEquals(9, dashboardDao.countEntities(UrlEvidence.class).intValue());
	}

	private JobExecution executeJob(String jobName) throws Exception {

		JobParametersBuilder builder = new JobParametersBuilder();
		Job job = (Job) appContext.getBean(jobName);
        return jobLauncher.run(job, builder.toJobParameters());
	}
}
