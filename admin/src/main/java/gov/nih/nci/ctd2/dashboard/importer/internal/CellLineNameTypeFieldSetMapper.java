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

	private static final String CELL_NAME_TYPE_ID = "cell_name_type_id";
	private static final String CELL_NAME_TYPE = "cell_name_type";
	//private static final String CELL_NAME_TYPE_PRIORITY = "cell_name_type_priority";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineNameTypeMap")
	private HashMap<String,String> cellLineNameTypeMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		cellLineNameTypeMap.put(fieldSet.readString(CELL_NAME_TYPE_ID),
								fieldSet.readString(CELL_NAME_TYPE));

		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(CellSample.class);
	}
}
