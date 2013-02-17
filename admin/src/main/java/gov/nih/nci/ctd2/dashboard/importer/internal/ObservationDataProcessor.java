package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

@Component("observationDataProcessor")
public class ObservationDataProcessor implements ItemProcessor<ObservationData, ObservationData> {

    @Override
    public ObservationData process(ObservationData observationData) throws Exception {
		return (observationData.observation == null) ? null : observationData;
	}
}