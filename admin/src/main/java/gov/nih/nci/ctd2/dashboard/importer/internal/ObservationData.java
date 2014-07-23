package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import java.util.Set;

public class ObservationData {

	protected Observation observation;
	protected Set<DashboardEntity> evidence;
	protected Set<DashboardEntity> observedEntities;

	public ObservationData(Observation observation, Set<DashboardEntity> observedEntities, Set<DashboardEntity> evidence) {
		this.observation = observation;
		this.observedEntities = observedEntities;
		this.evidence = evidence;
	}
}
