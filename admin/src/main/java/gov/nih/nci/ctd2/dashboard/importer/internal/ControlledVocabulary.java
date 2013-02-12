package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

public class ControlledVocabulary {

	protected DashboardEntity role;
	protected DashboardEntity observedRole;
	protected DashboardEntity observationTemplate;

	public ControlledVocabulary(DashboardEntity observationTemplate, DashboardEntity role, DashboardEntity observedRole) {
		this.role = role;
		this.observedRole = observedRole;
		this.observationTemplate = observationTemplate;
	}
}
