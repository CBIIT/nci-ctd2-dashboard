package gov.nih.nci.ctd2.dashboard;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.importer.internal.SampleImporter;
import gov.nih.nci.ctd2.dashboard.util.APIDataBuilder;
import gov.nih.nci.ctd2.dashboard.util.ExportBuilder;
import gov.nih.nci.ctd2.dashboard.util.OverallSummary;
import gov.nih.nci.ctd2.dashboard.util.SubjectScorer;

public class DashboardAdminMain {
    private static final Log log = LogFactory.getLog(DashboardAdminMain.class);
    private static final String helpText = DashboardAdminMain.class.getSimpleName();

    private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
            "classpath*:META-INF/spring/applicationContext.xml", // This is for DAO/Dashboard Model
            "classpath*:META-INF/spring/adminApplicationContext.xml", // This is for admin-related beans
            "classpath*:META-INF/spring/animalModelApplicationContext.xml", // This is for gene data importer beans
            "classpath*:META-INF/spring/cellLineDataApplicationContext.xml", // cell line data importer beans
            "classpath*:META-INF/spring/compoundDataApplicationContext.xml", // This is for compound data importer beans
            "classpath*:META-INF/spring/geneDataApplicationContext.xml", // This is for gene data importer beans
            "classpath*:META-INF/spring/proteinDataApplicationContext.xml", // This is for compound data importer beans
            "classpath*:META-INF/spring/TRCshRNADataApplicationContext.xml", // trc-shRNA data importer beans
            "classpath*:META-INF/spring/siRNADataApplicationContext.xml", // siRNA reagents data importer beans
            "classpath*:META-INF/spring/tissueSampleDataApplicationContext.xml", // cell line data importer beans
            "classpath*:META-INF/spring/controlledVocabularyApplicationContext.xml", // controlled vocab importer beans
            "classpath*:META-INF/spring/observationDataApplicationContext.xml", // observation data importer beans
            "classpath*:META-INF/spring/taxonomyDataApplicationContext.xml", // This is for taxonomy data importer beans
            "classpath*:META-INF/spring/ecotermDataApplicationContext.xml", // This is for ECO term data importer beans
            "classpath*:META-INF/spring/relatedCompoundsContext.xml", // this is for related compounds
            "classpath*:META-INF/spring/xrefApplicationContext.xml" // this is for xref importer beans
    );

    @Transactional
    public static void main(String[] args) {

        final CommandLineParser parser = new GnuParser();
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
                .addOption("am", "animal-model-data", false, "imports animal model data.")
                .addOption("cl", "cell-line-data", false, "imports cell line data.")
                .addOption("cp", "compound-data", false, "imports compound data.")
                .addOption("e", "eco-term", false, "import ECO terms.")
                .addOption("g", "gene-data", false, "imports gene data.")
                .addOption("p", "protein-data", false, "imports protein data.")
                .addOption("r", "rank-subjects", false,
                        "prioritize and rank the subjects according to the observation data.")
                .addOption("sh", "shrna-data", false, "imports shrna data.")
                .addOption("si", "sirna-data", false, "imports sirna data.")
                .addOption("ts", "tissue-sample-data", false, "imports tissue sample data.")
                .addOption("cv", "controlled-vocabulary", false, "imports the dashboard controlled vocabulary.")
                .addOption("o", "observation-data", false, "imports dashboard observation data.")
                .addOption("s", "sample-data", false, "imports sample data.")
                .addOption("t", "taxonomy-data", false, "imports organism data.")
                .addOption("i", "index", false, "creates lucene index.")
                .addOption("rc", "related-compounds", false, "store related compounds.")
                .addOption("x", "prepare-export", false, "prepare master export file.");

        // Here goes the parsing attempt
        try {
            CommandLine commandLine = parser.parse(gnuOptions, args);

            if (commandLine.getOptions().length == 0) {
                // Here goes help message about running admin
                throw new ParseException("Nothing to do!");
            }

            if (commandLine.hasOption("h")) {
                printHelpAndExit(gnuOptions, 0);
            }

            if (commandLine.hasOption("am")) {
                launchJob("animalModelImporterJob");
            }

            if (commandLine.hasOption("cl")) {
                launchJob("cellLineDataImporterJob");
            }

            if (commandLine.hasOption("cp")) {
                launchJob("compoundDataImporterJob");
            }

            if (commandLine.hasOption("g")) {
                launchJob("geneDataImporterJob");
            }

            if (commandLine.hasOption("p")) {
                launchJob("proteinDataImporterJob");
            }

            if (commandLine.hasOption("sh")) {
                launchJob("TRCshRNADataImporterJob");
            }

            if (commandLine.hasOption("si")) {
                launchJob("siRNADataImporterJob");
            }

            if (commandLine.hasOption("ts")) {
                launchJob("tissueSampleDataImporterJob");
            }

            if (commandLine.hasOption("cv")) {
                launchJob("controlledVocabularyImporterJob");
            }

            if (commandLine.hasOption("o")) {
                launchJob("observationDataImporterJob");
                String dataURL = (String) appContext.getBean("dataURL");
                APIDataBuilder b = (APIDataBuilder) appContext.getBean("apiDataBuilder");
                b.prepareData(dataURL);
            }

            if (commandLine.hasOption("x")) {
                String downloadFileLocation = (String) appContext.getBean("downloadFileLocation");
                Boolean zipExport = (Boolean) appContext.getBean("zipExport");
                ExportBuilder e = (ExportBuilder) appContext.getBean("exportBuilder");
                e.prepareData(downloadFileLocation, zipExport);
            }

            if (commandLine.hasOption("s")) {
                log.info("Running sample importer...");
                // This is just for demonstration purposes
                SampleImporter sampleImporter = (SampleImporter) appContext.getBean("sampleImporter");
                sampleImporter.run();
            }

            if (commandLine.hasOption("t")) {
                launchJob("taxonomyDataImporterJob");
            }

            if (commandLine.hasOption("e")) {
                launchJob("ecotermDataImporterJob");
            }

            if (commandLine.hasOption("r")) {
                SubjectScorer subjectScorer = (SubjectScorer) appContext.getBean("subjectScorer");
                subjectScorer.scoreAllRoles();
                OverallSummary overallSummary = (OverallSummary) appContext.getBean("overallSummary");
                overallSummary.summarize();
            }

            if (commandLine.hasOption("i")) {
                DashboardDao dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");
                dashboardDao.cleanIndex((Integer) appContext.getBean("indexBatchSize"));
            }

            if (commandLine.hasOption("rc")) {
                launchJob("relatedCompoundsJob");
            }

            log.info("All done.");
            System.exit(0);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelpAndExit(gnuOptions, -1);
        }
    }

    private static void printHelpAndExit(Options gnuOptions, int exitStatus) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(helpText, gnuOptions);
        System.exit(exitStatus);
    }

    private static void launchJob(String jobName) {
        log.info("launchJob: jobName:" + jobName);
        try {
            Job job = (Job) appContext.getBean(jobName);
            JobLauncher jobLauncher = (JobLauncher) appContext.getBean("jobLauncher");
            JobParametersBuilder builder = new JobParametersBuilder();
            JobExecution jobExecution = jobLauncher.run(job, builder.toJobParameters());
            log.info("launchJob: exit code: " + jobExecution.getExitStatus().getExitCode());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
