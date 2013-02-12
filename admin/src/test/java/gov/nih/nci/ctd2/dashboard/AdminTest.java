package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.importer.internal.CompoundNamesFieldSetMapper;

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
                "classpath*:META-INF/spring/testAdminApplicationContext.xml", // and this from the admin module
				"classpath*:META-INF/spring/testCompoundDataApplicationContext.xml", // and tis is for compound data importer beans
				"classpath*:META-INF/spring/testGeneDataApplicationContext.xml", // and this is for gene data importer beans
				"classpath*:META-INF/spring/testProteinDataApplicationContext.xml", // and this is for protein data importer beans
				"classpath*:META-INF/spring/testControlledVocabularyApplicationContext.xml" // and this is for controlled vocabulary importer beans
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

		// import some compound data
		jobExecution = executeJob("compoundDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(2, dashboardDao.countEntities(Compound.class).intValue());
		List<Subject> subjects = 
			dashboardDao.findSubjectsByXref(CompoundNamesFieldSetMapper.BROAD_COMPOUND_DATABASE, "31336");
		assertEquals(1, subjects.size());
		List<Compound> compounds = dashboardDao.findCompoundsBySmilesNotation("CC(C)Cc1ccc(cc1)[C@H](C)C(O)=O");
        assertEquals(1, compounds.size());

		// import some gene data
		jobExecution = executeJob("geneDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(12, dashboardDao.countEntities(Gene.class).intValue());
		List<Gene> genes = dashboardDao.findGenesByEntrezId("7529");
		assertEquals(1, genes.size());
		// we should get some organism records created
		assertEquals(2, dashboardDao.countEntities(Organism.class).intValue());
		List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId("10090");
		assertEquals(1, organisms.size());

		// import some protein data
		jobExecution = executeJob("proteinDataImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		assertEquals(8, dashboardDao.countEntities(Protein.class).intValue());
		List<Protein> proteins = dashboardDao.findProteinsByUniprotId("P31946");
		assertEquals(1, proteins.size());
		// some transcripts get created along with proteins
		assertEquals(14, dashboardDao.countEntities(Transcript.class).intValue());
		List<Transcript> transcripts = dashboardDao.findTranscriptsByRefseqId("NM_003404.3");
		assertEquals(1, transcripts.size());

		// import controlled vocabulary
		jobExecution = executeJob("controlledVocabularyImporterJob");
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		// we get some subject/observed subject roles
		assertEquals(3, dashboardDao.countEntities(SubjectRole.class).intValue());
		assertEquals(3, dashboardDao.countEntities(ObservedSubjectRole.class).intValue());
		List<ObservedSubjectRole> observedSubjectRoles = 
			dashboardDao.findObservedSubjectRoleByColumnName("compound_name");
		assertEquals(1, observedSubjectRoles.size());
		// we get some evidence/observed evidence roles
		assertEquals(4, dashboardDao.countEntities(EvidenceRole.class).intValue());
		assertEquals(12, dashboardDao.countEntities(ObservedEvidenceRole.class).intValue());
		List<ObservedEvidenceRole> observedEvidenceRoles = 
			dashboardDao.findObservedEvidenceRoleByColumnName("cell_line_subset");
		assertEquals(1, observedEvidenceRoles.size());
		// we get observation template data
		assertEquals(1, dashboardDao.countEntities(ObservationTemplate.class).intValue());
		List<ObservationTemplate> observationTemplates =
			dashboardDao.findObservationTemplateByName("enrichment_analysis");
		assertEquals(1, observationTemplates.size());
	}

	private JobExecution executeJob(String jobName) throws Exception {

		JobParametersBuilder builder = new JobParametersBuilder();
		Job job = (Job) appContext.getBean(jobName);
        return jobLauncher.run(job, builder.toJobParameters());
	}
}
