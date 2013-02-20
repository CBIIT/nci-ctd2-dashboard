package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;

public class CompoundData {

	protected Compound compound;
	protected String compoundType;

	public CompoundData(Compound compound, String compoundType) {
		this.compound = compound;
		this.compoundType = compoundType;
	}
}