package gov.nih.nci.ctd2.dashboard;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.importer.internal.SampleImporter;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DashboardAdminMain {
    private static final Log log = LogFactory.getLog(DashboardAdminMain.class);
    private static final String helpText = DashboardAdminMain.class.getSimpleName();

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/applicationContext.xml", // This is for DAO/Dashboard Model
                "classpath*:META-INF/spring/adminApplicationContext.xml" // This is for admin-related beans
        );

        // These two should not be exposed in the main method, but were put here
        // to show how we can access beans from the core module
        final DashboardDao dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");
        final DashboardFactory dashboardFactory = (DashboardFactory) appContext.getBean("dashboardFactory");

        final CommandLineParser parser = new GnuParser();
        Options gnuOptions = new Options();
        gnuOptions
                .addOption("h", "help", false, "shows this help document and quits.")
                .addOption("s", "sample-data", false, "imports sample data.")
        ;

        // Here goes the parsing attempt
        try {
            CommandLine commandLine = parser.parse(gnuOptions, args);

            if( commandLine.getOptions().length == 0 ) {
                // Here goes help message about running admin
                throw new ParseException("Nothing to do!");
            }

            if( commandLine.hasOption("h") ) {
                printHelpAndExit(gnuOptions, 0);
            }

            if( commandLine.hasOption("s") ) {
                log.info("Running sample importer...");
                // This is just for demonstration purposes
                SampleImporter sampleImporter = (SampleImporter) appContext.getBean("sampleImporter");
                sampleImporter.run();
            }

            log.info("All done.");
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
}
