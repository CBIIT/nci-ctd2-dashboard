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

	public static final String CBIO_PORTAL = "CBIO_PORTAL";
	public static final String BROAD_CELL_LINE_DATABASE = "BROAD_CELL_LINE";

	private static final String	CELL_SAMPLE_ID = "cell_sample_id";
	private static final String	CELL_NAME_TYPE_ID = "cell_name_type_id";
	private static final String	CELL_SAMPLE_NAME = "cell_sample_name";

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineNameTypeMap")
	private HashMap<String,String> cellLineNameTypeMap;

    @Autowired
	@Qualifier("cellSampleMap")
	private HashMap<String,CellSample> cellSampleMap;

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		String cellSampleId = fieldSet.readString(CELL_SAMPLE_ID);
		String cellNameTypeId = fieldSet.readString(CELL_NAME_TYPE_ID);
		String cellSampleName = fieldSet.readString(CELL_SAMPLE_NAME);
		// this map was populated in cellLineSampleStep
		CellSample cellSample = cellSampleMap.get(cellSampleId);
		if (cellSample != null) {
			if (cellSampleName.length() > 0) {
				// we are not concerned about creating dup synonyms
				// across cell samples - the cost in terms of lookup
				// to prevent this is not worth the benefit
				if (createSynonym(cellSample, cellSampleName)) {
					Synonym synonym = dashboardFactory.create(Synonym.class);
					synonym.setDisplayName(cellSampleName);
					// cell line name types are ordered by priority in the cell sample name file
					// if this is our first synonym, it has the highest priority -
					// in which case we should use it as the cell sample display name
					if (cellSample.getSynonyms().isEmpty()) cellSample.setDisplayName(cellSampleName);
					cellSample.getSynonyms().add(synonym);
				}
			}
			// create xref
			String cellNameType = cellLineNameTypeMap.get(cellNameTypeId);
			if (cellNameType != null) {
				Xref xref = dashboardFactory.create(Xref.class);
				xref.setDatabaseId(cellSampleName);
				xref.setDatabaseName(cellNameType);
				cellSample.getXrefs().add(xref);
				// add xref to cbio portal - temp hack until CUTLL1 is in cBio, skip
				if (cellNameType.equalsIgnoreCase("ccle") && !cellSampleName.contains("CUTLL1")) {
					xref = dashboardFactory.create(Xref.class);
					xref.setDatabaseId(cellSampleName);
					xref.setDatabaseName(CBIO_PORTAL);
					cellSample.getXrefs().add(xref);
				}
			}
		}
		return cellSample;
	}

	private boolean createSynonym(CellSample cellSample, String cellSampleName) {
		for (Synonym synonym : cellSample.getSynonyms()) {
			if (synonym.getDisplayName().equals(cellSampleName)) return false;
		}
		return true;
	}
}
