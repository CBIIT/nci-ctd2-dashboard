package gov.nih.nci.ctd2.dashboard;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Evidence;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidence;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.model.UrlEvidence;

// this is written to faciliate testing new code.
public class DashboardAdminUtil {
    private static final Log log = LogFactory.getLog(DashboardAdminUtil.class);

    private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
            "classpath*:META-INF/spring/applicationContext.xml", // This is for DAO/Dashboard Model

            //"classpath*:META-INF/spring/animalModelApplicationContext.xml", // This is for gene data importer beans
            "classpath*:META-INF/spring/cellLineDataApplicationContext.xml", // This is for cell line data importer beans
            "classpath*:META-INF/spring/compoundDataApplicationContext.xml", // This is for compound data importer beans
            "classpath*:META-INF/spring/geneDataApplicationContext.xml", // This is for gene data importer beans
            "classpath*:META-INF/spring/proteinDataApplicationContext.xml", // This is for compound data importer beans
            "classpath*:META-INF/spring/TRCshRNADataApplicationContext.xml", // and this is for trc-shRNA data importer beans
            "classpath*:META-INF/spring/siRNADataApplicationContext.xml", // and this is for siRNA reagents data importer beans
            "classpath*:META-INF/spring/tissueSampleDataApplicationContext.xml", // This is for cell line data importer beans
            "classpath*:META-INF/spring/controlledVocabularyApplicationContext.xml", // This is for controlled vocabulary importer beans
            "classpath*:META-INF/spring/observationDataApplicationContext.xml", // This is for observation data importer beans
            "classpath*:META-INF/spring/taxonomyDataApplicationContext.xml" // This is for taxonomy data importer beans
    );

    public static void main(String[] args) {
        System.out.println("hi there");
        if (args.length > 0 && args[0].equals("d"))
            deleteObservationData();
        else
            debug();
    }

    // test accessing the database outside the regular workflow
    private static void debug() {
        DashboardDao dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");

        List<ObservedSubject> observedSubject = dashboardDao.findEntities(ObservedSubject.class);
        log.debug("observed subject count=" + observedSubject.size());
        int count = 0;
        for (ObservedSubject x : observedSubject) {
            log.debug(x.getDisplayName());
            count++;
            if (count > 20)
                break;
        }
        List<ObservedEvidence> observedEvidence = dashboardDao.findEntities(ObservedEvidence.class);
        log.debug("observed evidence count=" + observedEvidence.size());
        count = 0;
        for (ObservedEvidence x : observedEvidence) {
            if (!(x.getEvidence() instanceof UrlEvidence)) {
                continue;
            }
            log.debug(x.getDisplayName());
            count++;
            if (count > 20)
                break;
        }
        List<Evidence> evidence = dashboardDao.findEntities(Evidence.class);
        log.debug("evidence count=" + evidence.size());
        count = 0;
        for (Evidence x : evidence) {
            if (!(x instanceof UrlEvidence)) {
                continue;
            }
            log.debug(x.getDisplayName());
            count++;
            if (count > 100)
                break;
        }
        List<Observation> observation = dashboardDao.findEntities(Observation.class);
        log.debug("observation count=" + observation.size());
        count = 0;
        for (Observation x : observation) {
            log.debug(x.getStableURL());
            count++;
            if (count > 20)
                break;
        }

    }

    // find all observed subject, observed evidence, evidence, and observation. delete them
    static private void deleteObservationData() {
        DashboardDao dashboardDao = (DashboardDao) appContext.getBean("dashboardDao");

        List<ObservedSubject> observedSubject = dashboardDao.findEntities(ObservedSubject.class);
        for (ObservedSubject x : observedSubject) {
            dashboardDao.delete(x);
        }
        List<ObservedEvidence> observedEvidence = dashboardDao.findEntities(ObservedEvidence.class);
        for (ObservedEvidence x : observedEvidence) {
            dashboardDao.delete(x);
        }
        List<Evidence> evidence = dashboardDao.findEntities(Evidence.class);
        for (Evidence x : evidence) {
            dashboardDao.delete(x);
        }
        List<Observation> observation = dashboardDao.findEntities(Observation.class);
        for (Observation x : observation) {
            dashboardDao.delete(x);
        }
        log.debug("all observation data deleted.");
        List<Submission> submission = dashboardDao.findEntities(Submission.class);
        for (Submission x : submission) {
            dashboardDao.delete(x);
        }
        log.debug("all submission data deleted.");

    }
}
