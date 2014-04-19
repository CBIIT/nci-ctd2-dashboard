package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.text.SimpleDateFormat;

@Component("observationDataWriter")
public class ObservationDataWriter implements ItemWriter<ObservationData> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(ObservationDataWriter.class);

	private HashMap<String, Submission> submissionCache = new HashMap<String, Submission>();

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;

    public void write(List<? extends ObservationData> items) throws Exception {
        ArrayList<DashboardEntity> entities = new ArrayList<DashboardEntity>();

		for (ObservationData observationData : items) {
			Submission submission = observationData.observation.getSubmission();
			String submissionCacheKey = ObservationDataFieldSetMapper.getSubmissionCacheKey(submission);
			if (!submissionCache.containsKey(submissionCacheKey)) {
				SubmissionCenter submissionCenter =
					dashboardDao.findSubmissionCenterByName(submission.getSubmissionCenter().getDisplayName());
				if (submissionCenter == null)
                    entities.add(submission.getSubmissionCenter());
                entities.add(submission);
				submissionCache.put(submissionCacheKey, submission);
			}
            entities.add(observationData.observation);
            entities.addAll(observationData.evidence);
            entities.addAll(observationData.observedEntities);
		}

        dashboardDao.batchSave(entities, batchSize);
	}
}
