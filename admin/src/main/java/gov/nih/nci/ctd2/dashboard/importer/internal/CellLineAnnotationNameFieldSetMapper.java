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

@Component("cellLineAnnotationNameMapper")
public class CellLineAnnotationNameFieldSetMapper implements FieldSetMapper<CellSample> {

	private static final String CELL_ANNO_NAME_ID = "cell_anno_name_id";
	private static final String CELL_ANNO_NAME = "cell_anno_name";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineAnnotationNameMap")
	private HashMap<String,String> cellLineAnnotationNameMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		cellLineAnnotationNameMap.put(fieldSet.readString(CELL_ANNO_NAME_ID),
                                      fieldSet.readString(CELL_ANNO_NAME));

		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(CellSample.class);
	}
}
