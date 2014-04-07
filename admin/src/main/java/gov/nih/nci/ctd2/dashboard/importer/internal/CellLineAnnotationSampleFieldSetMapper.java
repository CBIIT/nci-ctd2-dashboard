package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Annotation;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.HashSet;

@Component("cellLineAnnotationSampleMapper")
public class CellLineAnnotationSampleFieldSetMapper implements FieldSetMapper<Annotation> {

	private static final String CELL_SAMPLE_ID = "cell_sample_id";
	private static final String CELL_ANNO_NAME_ID = "cell_anno_name_id";
	private static final String CELL_ANNO_TYPE_ID = "cell_anno_type_id";
	private static final String CELL_ANNO_SOURCE_ID = "cell_anno_source_id";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineAnnotationTypeMap")
	private HashMap<String,String> cellLineAnnotationTypeMap;

    @Autowired
	@Qualifier("cellLineAnnotationNameMap")
	private HashMap<String,String> cellLineAnnotationNameMap;

    @Autowired
	@Qualifier("cellLineAnnotationSourceMap")
	private HashMap<String,String> cellLineAnnotationSourceMap;

    @Autowired
	@Qualifier("cellLineAnnotationSampleMap")
	private HashMap<String,HashSet> cellLineAnnotationSampleMap;

	public Annotation mapFieldSet(FieldSet fieldSet) throws BindException {

        String cellSampleId = fieldSet.readString(CELL_SAMPLE_ID);
        if (!cellLineAnnotationSampleMap.containsKey(cellSampleId)) {
            cellLineAnnotationSampleMap.put(cellSampleId, new HashSet<Annotation>());
        }

        Annotation annotation = dashboardFactory.create(Annotation.class);

        if (cellLineAnnotationNameMap.containsKey(fieldSet.readString(CELL_ANNO_NAME_ID))) {
            annotation.setDisplayName(cellLineAnnotationNameMap.get(fieldSet.readString(CELL_ANNO_NAME_ID)));
        }

        if (cellLineAnnotationTypeMap.containsKey(fieldSet.readString(CELL_ANNO_TYPE_ID))) {
            annotation.setType(cellLineAnnotationTypeMap.get(fieldSet.readString(CELL_ANNO_TYPE_ID)));
        }

        if (cellLineAnnotationSourceMap.containsKey(fieldSet.readString(CELL_ANNO_SOURCE_ID))) {
            annotation.setSource(cellLineAnnotationSourceMap.get(fieldSet.readString(CELL_ANNO_SOURCE_ID)));
        }

        cellLineAnnotationSampleMap.get(cellSampleId).add(annotation);

		// if we don't return something, spring batch will think EOF has been reached
		return annotation;
	}
}
