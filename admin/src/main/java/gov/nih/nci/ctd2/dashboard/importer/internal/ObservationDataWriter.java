package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component("observationDataWriter")
public class ObservationDataWriter implements ItemWriter<ObservationData> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(ObservationDataWriter.class);
 
	public void write(List<? extends ObservationData> items) throws Exception {
		for (ObservationData observationData : items) {

			// check for existence
			SubmissionCenter submissionCenter =
				dashboardDao.findSubmissionCenterByName(observationData.observation.getSubmission().
														getSubmissionCenter().getDisplayName());
			if (submissionCenter == null) {
				dashboardDao.save(observationData.observation.getSubmission().getSubmissionCenter());
				submissionCenter = observationData.observation.getSubmission().getSubmissionCenter();
			}
			log.info("Storing observation for submission center: " + submissionCenter.getDisplayName());
			dashboardDao.save(observationData.observation.getSubmission());
			dashboardDao.save(observationData.observation);
			for (DashboardEntity evidence : observationData.evidence) {
				dashboardDao.save(evidence);
			}
			for (DashboardEntity observedEntities : observationData.observedEntities) {
				dashboardDao.save(observedEntities);
			}
		}
	}
}
