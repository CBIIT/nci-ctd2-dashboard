package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component("controlledVocabularyWriter")
public class ControlledVocabularyWriter implements ItemWriter<ControlledVocabulary> {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(ControlledVocabularyWriter.class);
 
	public void write(List<? extends ControlledVocabulary> items) throws Exception {
		for (ControlledVocabulary controlledVocabulary : items) {
			String observedRoleName = "";
			if (controlledVocabulary.observedRole instanceof ObservedSubjectRole) {
				observedRoleName = ((ObservedSubjectRole)controlledVocabulary.observedRole).getColumnName();
			}
			else if (controlledVocabulary.observedRole instanceof ObservedEvidenceRole) {
				observedRoleName = ((ObservedEvidenceRole)controlledVocabulary.observedRole).getColumnName();
			}
			log.info("Storing Observed Role: " + observedRoleName);
			dashboardDao.save(controlledVocabulary.role);
			dashboardDao.save(controlledVocabulary.observedRole);
		}
	}
}
