package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import java.util.Set;

public class ProteinData {

	protected Protein protein;
	protected Set<Transcript> transcripts;
	protected Set<Organism> organisms;

	public ProteinData(Protein protein, Set<Transcript> transcripts, Set<Organism> organisms) {
		this.protein = protein;
		this.transcripts = transcripts;
		this.organisms = organisms;
	}
}
