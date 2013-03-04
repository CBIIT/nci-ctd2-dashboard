package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Component("geneDataProcessor")
public class GeneDataProcessor implements ItemProcessor<Gene, Gene> {

    @Override
    public Gene process(Gene gene) throws Exception {
		return (gene.getOrganism() == null) ? null : gene;
	}
}
