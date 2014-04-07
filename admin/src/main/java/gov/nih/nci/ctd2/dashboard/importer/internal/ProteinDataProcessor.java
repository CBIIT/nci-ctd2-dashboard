package gov.nih.nci.ctd2.dashboard.importer.internal;

import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Component("proteinDataProcessor")
    public class ProteinDataProcessor implements ItemProcessor<ProteinData, ProteinData> {

    @Override
    public ProteinData process(ProteinData proteinData) throws Exception {
		return (proteinData.protein.getOrganism() == null) ? null : proteinData;
    }
}
