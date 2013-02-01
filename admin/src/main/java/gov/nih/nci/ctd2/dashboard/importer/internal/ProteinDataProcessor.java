package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Protein;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("proteinDataProcessor")
    public class ProteinDataProcessor implements ItemProcessor<ProteinData, ProteinData> {

    @Override
    public ProteinData process(ProteinData proteinData) throws Exception {
        return (GeneDataProcessor.DESIRED_ORGANISMS.matcher(proteinData.taxonomyId).find()) ? proteinData : null;
    }
}
