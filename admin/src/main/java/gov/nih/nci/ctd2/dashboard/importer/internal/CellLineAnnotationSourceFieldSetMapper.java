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

@Component("cellLineAnnotationSourceMapper")
public class CellLineAnnotationSourceFieldSetMapper implements FieldSetMapper<CellSample> {

	private static final String CELL_ANNO_SOURCE_ID = "cell_anno_source_id";
	private static final String CELL_ANNO_SOURCE = "cell_anno_source";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineAnnotationSourceMap")
	private HashMap<String,String> cellLineAnnotationSourceMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		cellLineAnnotationSourceMap.put(fieldSet.readString(CELL_ANNO_SOURCE_ID),
                                        fieldSet.readString(CELL_ANNO_SOURCE));

		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(CellSample.class);
	}
}
