package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Component("emptyItemWriter")
public class EmptyItemWriter implements ItemWriter<DashboardEntity> {
	// this writer intentionally does nothing. it exists only because it is required by org.springframework.batch.core.step.item.SimpleStepFactoryBean
	public void write(List<? extends DashboardEntity> items) throws Exception {}
}
