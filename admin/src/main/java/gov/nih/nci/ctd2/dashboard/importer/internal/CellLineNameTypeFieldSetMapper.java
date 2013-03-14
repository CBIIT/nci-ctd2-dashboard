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

@Component("cellLineNameTypeMapper")
public class CellLineNameTypeFieldSetMapper implements FieldSetMapper<CellSample> {

	public static final String CELL_NAME_TYPE_MAP_DELIMITER = ":";

	private static final String CELL_NAME_TYPE_ID = "CELL_NAME_TYPE_ID";
	private static final String CELL_NAME_TYPE = "CELL_NAME_TYPE";
	private static final String CELL_NAME_TYPE_PRIORITY = "CELL_NAME_TYPE_PRIORITY";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineNameTypeMap")
	private HashMap<String,String> cellLineNameTypeMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		cellLineNameTypeMap.put(fieldSet.readString(CELL_NAME_TYPE_ID),
								fieldSet.readString(CELL_NAME_TYPE) +
								CELL_NAME_TYPE_MAP_DELIMITER +
								fieldSet.readString(CELL_NAME_TYPE_PRIORITY));

		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(CellSample.class);
	}
}
