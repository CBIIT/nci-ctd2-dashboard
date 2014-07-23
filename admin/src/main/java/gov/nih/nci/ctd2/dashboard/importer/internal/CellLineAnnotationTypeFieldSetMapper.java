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

@Component("cellLineAnnotationTypeMapper")
public class CellLineAnnotationTypeFieldSetMapper implements FieldSetMapper<CellSample> {

	private static final String CELL_ANNO_TYPE_ID = "cell_anno_type_id";
	private static final String CELL_ANNO_TYPE = "cell_anno_type";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineAnnotationTypeMap")
	private HashMap<String,String> cellLineAnnotationTypeMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		cellLineAnnotationTypeMap.put(fieldSet.readString(CELL_ANNO_TYPE_ID),
                                      fieldSet.readString(CELL_ANNO_TYPE));

		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(CellSample.class);
	}
}
