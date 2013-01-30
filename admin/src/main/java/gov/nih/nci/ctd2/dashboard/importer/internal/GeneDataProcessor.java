package gov.nih.nci.ctd2.dashboard.importer.internal;

import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("geneDataProcessor")
public class GeneDataProcessor implements ItemProcessor<GeneData, GeneData> {

	// the reference metadata worksheet can contain environment vars
	private static final Pattern DESIRED_ORGANISMS = Pattern.compile("(9606|10090)");
 
    @Override
    public GeneData process(GeneData geneData) throws Exception {
		return (DESIRED_ORGANISMS.matcher(geneData.organism.getTaxonomyId()).find()) ? geneData : null;
	}
}