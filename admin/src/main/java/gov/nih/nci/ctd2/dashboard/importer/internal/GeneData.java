package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Organism;

public class GeneData {

	protected Gene gene;
	protected Organism organism;
	protected boolean saveOrganism;

	public GeneData(Gene gene, Organism organism, boolean saveOrganism) {
		this.gene = gene;
		this.organism = organism;
		this.saveOrganism = saveOrganism;
	}
}