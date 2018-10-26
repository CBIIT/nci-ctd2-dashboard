package gov.nih.nci.ctd2.dashboard.importer.internal;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component("observationDataProcessor")
public class ObservationDataProcessor implements ItemProcessor<ObservationData, ObservationData> {

    @Override
    public ObservationData process(ObservationData observationData) throws Exception {
		return (observationData.observation == null) ? null : observationData;
	}
}