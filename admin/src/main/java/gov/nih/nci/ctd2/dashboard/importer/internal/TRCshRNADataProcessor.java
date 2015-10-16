package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.ShRna;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Component("TRCshRNADataProcessor")
public class TRCshRNADataProcessor implements ItemProcessor<ShRna, ShRna> {

    @Override
    public ShRna process(ShRna shRNA) throws Exception {
		if (shRNA == null) return null;
		if (shRNA.getOrganism() == null) return null;
        // allow null transcript relationship
		//if (shRNA.getTranscript() == null) return null;
		return shRNA;
	}
}
