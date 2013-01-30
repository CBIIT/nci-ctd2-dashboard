package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Organism;

public class GeneData {

	protected Gene gene;
	protected Organism organism;

	public GeneData(Gene gene, Organism organism) {
		this.gene = gene;
		this.organism = organism;
	}
}