package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.HashMap;

@Component("cellLineNameMapper")
public class CellLineNameFieldSetMapper implements FieldSetMapper<CellSample> {

	public static final String BROAD_CELL_LINE_DATABASE = "BROAD_CELL_LINE";

	private static final String	CELL_SAMPLE_NAME_ID = "CELL_SAMPLE_NAME_ID";
	private static final String	CELL_SAMPLE_ID = "CELL_SAMPLE_ID";
	private static final String	CELL_NAME_TYPE_ID = "CELL_NAME_TYPE_ID";
	private static final String	CELL_SAMPLE_NAME = "CELL_SAMPLE_NAME";

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineNameTypeMap")
	private HashMap<String,String> cellLineNameTypeMap;

    private HashMap<String, CellSample> cellSampleMap = new HashMap<String, CellSample>();
    private HashMap<String, Synonym> synonymMap = new HashMap<String, Synonym>();

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		String cellSampleId = fieldSet.readString(CELL_SAMPLE_ID);
		String cellNameTypeId = fieldSet.readString(CELL_NAME_TYPE_ID);
		String cellSampleName = fieldSet.readString(CELL_SAMPLE_NAME);

		// find cell line by xref (broad)
		CellSample cellSample = cellSampleMap.get(cellSampleId);
		if (cellSample == null) {
			List<Subject> cellSamples =
				dashboardDao.findSubjectsByXref(CellLineSampleFieldSetMapper.BROAD_CELL_LINE_DATABASE,
												cellSampleId);
			if (cellSamples.size() == 1) {
				cellSample = (CellSample)cellSamples.iterator().next();
				cellSampleMap.put(cellSampleId, cellSample);
			}
		}
		if (cellSample != null) {
			if (cellSampleName.length() > 0) {
				// create synonym
				Synonym synonym  = synonymMap.get(cellSampleName);
				if (synonym == null) {
					synonym = dashboardFactory.create(Synonym.class);
					synonym.setDisplayName(cellSampleName);
					synonymMap.put(cellSampleName, synonym);
				}
				cellSample.getSynonyms().add(synonym);
			}
			// create xref
			// [0] is name type, [1] is name type priority
			String[] cellNameTypePair = cellLineNameTypeMap.get(cellNameTypeId)
				.split(CellLineNameTypeFieldSetMapper.CELL_NAME_TYPE_MAP_DELIMITER);
			if (cellNameTypePair.length == 2) {
				Xref xref = dashboardFactory.create(Xref.class);
				xref.setDatabaseId(cellSampleName);
				xref.setDatabaseName(cellNameTypePair[0]);
				cellSample.getXrefs().add(xref);
			}

		}
		return cellSample;
	}
}
