package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;

@Component("controlledVocabularyPerColumnWriter")
public class ControlledVocabularyPerColumnWriter implements ItemWriter<ControlledVocabulary> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(ControlledVocabularyPerColumnWriter.class);

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;

	private ArrayList<DashboardEntity> entities;
	private HashSet<DashboardEntity> entityCache;

    public void write(List<? extends ControlledVocabulary> items) throws Exception {

		if (entityCache == null) entityCache = new HashSet<DashboardEntity>();
		if (entities == null) entities = new ArrayList<DashboardEntity>();

		for (ControlledVocabulary controlledVocabulary : items) {
			String observedRoleName = "";
			if (controlledVocabulary.observedRole instanceof ObservedSubjectRole) {
				observedRoleName = ((ObservedSubjectRole)controlledVocabulary.observedRole).getColumnName();
			}
			else if (controlledVocabulary.observedRole instanceof ObservedEvidenceRole) {
				observedRoleName = ((ObservedEvidenceRole)controlledVocabulary.observedRole).getColumnName();
			}
			log.info("Storing Observed Role: " + observedRoleName);

            if(!entityCache.contains(controlledVocabulary.role)) {
                entityCache.add(controlledVocabulary.role);
                entities.add(controlledVocabulary.role);
            }

            ObservationTemplate ot = (ObservationTemplate)controlledVocabulary.observationTemplate;
            if (!entityCache.contains(ot.getSubmissionCenter())) {
                SubmissionCenter submissionCenter =
                    dashboardDao.findSubmissionCenterByName(ot.getSubmissionCenter().getDisplayName());
                if (submissionCenter == null) {
                    entities.add(ot.getSubmissionCenter());
                }
                entityCache.add(ot.getSubmissionCenter());
            }

            if(!entityCache.contains(controlledVocabulary.observationTemplate)) {
                entityCache.add(controlledVocabulary.observationTemplate);
                entities.add(controlledVocabulary.observationTemplate);
            }

            if(!entityCache.contains(controlledVocabulary.observedRole)) {
                entityCache.add(controlledVocabulary.observedRole);
                entities.add(controlledVocabulary.observedRole);
            }
		}

        dashboardDao.batchSave(entities, batchSize);
	}
}
