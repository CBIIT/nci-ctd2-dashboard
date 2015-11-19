package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.importer.internal.CellLineNameFieldSetMapper;
import gov.nih.nci.ctd2.dashboard.importer.internal.CompoundsFieldSetMapper;

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
                "classpath*:META-INF/spring/adminApplicationContext.xml", // use this instead of testObservationDataApplicationContext - gets observationDataFactory bean
                "classpath*:META-INF/spring/testApplicationContext.xml", // this is coming from the core module
				"classpath*:META-INF/spring/testAnimalModelApplicationContext.xml", // and this is for cell line data importer beans
				"classpath*:META-INF/spring/testCellLineDataApplicationContext.xml", // and this is for cell line data importer beans
				"classpath*:META-INF/spring/testCompoundDataApplicationContext.xml", // and this is for compound data importer beans
				"classpath*:META-INF/spring/testGeneDataApplicationContext.xml", // and this is for gene data importer beans
				"classpath*:META-INF/spring/testProteinDataApplicationContext.xml", // and this is for protein data importer beans
				"classpath*:META-INF/spring/testTRCshRNADataApplicationContext.xml", // and this is for trc-shRNA data importer beans
				"classpath*:META-INF/spring/testTissueSampleDataApplicationContext.xml", // and this is for tissue sample data importer beans
				"classpath*:META-INF/spring/testControlledVocabularyApplicationContext.xml", // and this is for controlled vocabulary importer beans
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

        // animal model
        jobExecution = executeJob("animalModelImporterJob");
		assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(1, dashboardDao.countEntities(AnimalModel.class).intValue());
		List<AnimalModel> models = dashboardDao.findAnimalModelByName("[FVB/N x SPRET/Ei] x FVB/N");
		assertEquals(1, models.size());
		assertEquals("10090", models.iterator().next().getOrganism().getTaxonomyId());

        // import some cell line data
        jobExecution = executeJob("cellLineDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        assertEquals(3, dashboardDao.countEntities(CellSample.class).intValue());
        List<CellSample> cellSamples = dashboardDao.findCellSampleByAnnoSource("COSMIC (Sanger)");
        assertEquals(3, cellSamples.size());

        cellSamples = dashboardDao.findCellSampleByAnnoType("primary_site");
        assertEquals(3, cellSamples.size());

        cellSamples = dashboardDao.findCellSampleByAnnoName("acute_lymphoblastic_B_cell_leukaemia");
        assertEquals(1, cellSamples.size());
        CellSample cellSample = (CellSample)cellSamples.iterator().next();
        assertEquals(8, cellSample.getAnnotations().size());
        assertEquals(2, cellSample.getSynonyms().size());
        
        Annotation annotation = cellSample.getAnnotations().iterator().next();
        cellSamples = dashboardDao.findCellSampleByAnnotation(annotation);
        assertEquals(1, cellSamples.size());
        assertEquals(cellSample, cellSamples.iterator().next());

        List<Subject> cellSampleSubjects = dashboardDao.findSubjectsBySynonym("5637", true);
        assertEquals(1, cellSampleSubjects.size());
        cellSample = (CellSample)cellSampleSubjects.iterator().next();
        assertEquals("M", cellSample.getGender());

        // import some compound data
        jobExecution = executeJob("compoundDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        assertEquals(10, dashboardDao.countEntities(Compound.class).intValue());
        List<Subject> compoundSubjects =
            dashboardDao.findSubjectsByXref(CompoundsFieldSetMapper.BROAD_COMPOUND_DATABASE, "411739");
        assertEquals(1, compoundSubjects.size());
        List<Compound> compounds = dashboardDao.findCompoundsBySmilesNotation("CCCCCCCCC1OC(=O)C(=C)C1C(O)=O");
        assertEquals(1, compounds.size());
        assertEquals(3, compounds.iterator().next().getSynonyms().size());
        List<Subject> compoundSubjectsWithImage =
            dashboardDao.findSubjectsByXref(CompoundsFieldSetMapper.COMPOUND_IMAGE_DATABASE,
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
        List<Transcript> transcripts = dashboardDao.findTranscriptsByRefseqId("NM_003404");
        assertEquals(1, transcripts.size());

        // import some shrna
        jobExecution = executeJob("TRCshRNADataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        assertEquals(1, dashboardDao.countEntities(ShRna.class).intValue());
        List<Subject> shRNASubjects = dashboardDao.findSubjectsByXref("BROAD_SHRNA", "TRCN0000000001");
        assertEquals(1, shRNASubjects.size());
        ShRna shRNA = (ShRna)shRNASubjects.get(0);
        assertEquals("CCCTGCCAAACAAGCTAATAT", shRNA.getDisplayName());
        assertEquals("CCCTGCCAAACAAGCTAATAT", shRNA.getTargetSequence());

        // import some tissue sample data
        jobExecution = executeJob("tissueSampleDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        assertEquals(2, dashboardDao.countEntities(TissueSample.class).intValue());
        List<TissueSample> tissueSamples = dashboardDao.findTissueSampleByName("neoplasm by morphology");
        assertEquals(1, tissueSamples.size());
        TissueSample tissueSample = tissueSamples.get(0);
        assertEquals(1, tissueSample.getSynonyms().size());
        assertEquals(2, tissueSample.getXrefs().size());

        // import controlled vocabulary
        jobExecution = executeJob("controlledVocabularyImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        // we get some subject/observed subject roles
        assertEquals(21, dashboardDao.countEntities(SubjectRole.class).intValue());
        assertEquals(119, dashboardDao.countEntities(ObservedSubjectRole.class).intValue());
        assertTrue(dashboardDao.findObservedSubjectRole("broad_cpd_sens_lineage_enrich", "compound_name") != null);
        // we get some evidence/observed evidence roles
        assertEquals(10, dashboardDao.countEntities(EvidenceRole.class).intValue());
        assertEquals(271, dashboardDao.countEntities(ObservedEvidenceRole.class).intValue());
        assertTrue(dashboardDao.findObservedEvidenceRole("broad_cpd_sens_lineage_enrich", "cell_line_subset") != null);
        // we get observation template data
        assertEquals(36, dashboardDao.countEntities(ObservationTemplate.class).intValue());
        ObservationTemplate observationTemplate = dashboardDao.findObservationTemplateByName("broad_cpd_sens_lineage_enrich");
        assertNotNull(observationTemplate);
        assertFalse(observationTemplate.getIsSubmissionStory());
        assertEquals(0, observationTemplate.getSubmissionStoryRank().intValue());
        observationTemplate = dashboardDao.findObservationTemplateByName("broad_beta-catenin_navitoclax");
        assertNotNull(observationTemplate);
        assertFalse(observationTemplate.getIsSubmissionStory());
        assertEquals(0, observationTemplate.getSubmissionStoryRank().intValue());
    }

	private JobExecution executeJob(String jobName) throws Exception {

		JobParametersBuilder builder = new JobParametersBuilder();
		Job job = (Job) appContext.getBean(jobName);
        return jobLauncher.run(job, builder.toJobParameters());
	}
}
