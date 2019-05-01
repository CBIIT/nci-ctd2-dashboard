package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.StableURL;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component("observationDataWriter")
public class ObservationDataWriter implements ItemWriter<ObservationData> {

    private volatile int counter = 0;

    @Autowired
    private DashboardDao dashboardDao;

    private static final Log log = LogFactory.getLog(ObservationDataWriter.class);

    private HashMap<String, Submission> submissionCache = new HashMap<String, Submission>();

    @Autowired
    @Qualifier("batchSize")
    private Integer batchSize;

    private Map<String, Integer> observationIndex = new HashMap<String, Integer>();

    public void write(List<? extends ObservationData> items) throws Exception {
        log.debug("submission counter=" + ++counter + "; number of observations=" + items.size());
        ArrayList<DashboardEntity> entities = new ArrayList<DashboardEntity>();

        StableURL stableURL = new StableURL();
        for (ObservationData observationData : items) {
            Observation observation = observationData.observation;
            Submission submission = observation.getSubmission();
            String submissionCacheKey = ObservationDataFieldSetMapper.getSubmissionCacheKey(submission);
            String submissionName = submission.getDisplayName();
            if (!submissionCache.containsKey(submissionCacheKey)) {
                submission.setStableURL(stableURL.createURLWithPrefix("submission", submissionName));
                entities.add(submission);
                if (observationIndex.get(submissionName) == null) {
                    observationIndex.put(submissionName, 0);
                }
                submissionCache.put(submissionCacheKey, submission);
            } else {
                observation.setSubmission(submissionCache.get(submissionCacheKey));
            }
            int index = observationIndex.get(submissionName);
            observationIndex.put(submissionName, observationIndex.get(submissionName) + 1);
            observation.setStableURL(stableURL.createURLWithPrefix("observation", submissionName) + "-" + index);
            entities.add(observation);
            entities.addAll(observationData.evidence);
            entities.addAll(observationData.observedEntities);
        }

        /* Including different kinds of things in one collection is most likely not to be a good idea.
        entities include: (1) observations, (2) evidences, (3) observed subjects and evidences,
        and (4) submission (only one expected, and should be saved before all other things */
        dashboardDao.batchSave(entities, 1000); // smaller batch size helps to prevent org.hibernate.PessimisticLockException
        log.debug("ObservationData written " + entities.size());
    }
}
