package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;

@Component("cellLineLineageMapper")
public class CellLineLineageFieldSetMapper implements FieldSetMapper<CellSample> {

	private static final String SITE_INDEX = "site_index";
	private static final String CELL_SAMPLE_LINEAGE = "cell_sample_lineage";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineLineageMap")
	private HashMap<String,String> cellLineLineageMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		cellLineLineageMap.put(fieldSet.readString(SITE_INDEX),
							   fieldSet.readString(CELL_SAMPLE_LINEAGE));

		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(CellSample.class);
	}
}
