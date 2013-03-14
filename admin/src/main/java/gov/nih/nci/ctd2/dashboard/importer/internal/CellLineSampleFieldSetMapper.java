package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.HashMap;

@Component("cellLineSampleMapper")
public class CellLineSampleFieldSetMapper implements FieldSetMapper<CellSample> {

	public static final String BROAD_CELL_LINE_DATABASE = "BROAD_CELL_LINE";

	private static final String CELL_SAMPLE_ID = "CELL_SAMPLE_ID";
	private static final String	CELL_LINEAGE = "CELL_LINEAGE";
	private static final String	TAXONOMY_ID = "TAXONOMY_ID";

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

	private HashMap<String, Organism> organismsCache = new HashMap<String, Organism>();

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		String cellSampleId = fieldSet.readString(CELL_SAMPLE_ID);
		String cellLineage = fieldSet.readString(CELL_LINEAGE);
		String taxonomyId = fieldSet.readString(TAXONOMY_ID);

		CellSample cellSample = dashboardFactory.create(CellSample.class);
		// display name will be set in next step
		Organism organism = getOrganism(taxonomyId);
		if (organism != null) cellSample.setOrganism(organism);
		if (cellLineage.length() > 0) cellSample.setLineage(cellLineage);
		Xref xref = dashboardFactory.create(Xref.class);
		xref.setDatabaseId(cellSampleId);
		xref.setDatabaseName(BROAD_CELL_LINE_DATABASE);
		cellSample.getXrefs().add(xref);

		return cellSample;
	}

	private Organism getOrganism(String taxonomyId) {
		Organism toReturn = null;
		toReturn = organismsCache.get(taxonomyId);
		if (toReturn == null) {
			List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId(taxonomyId);
			if (organisms.size() == 1) {
				toReturn = organisms.get(0);
				organismsCache.put(taxonomyId, toReturn);
			}
		}
		return toReturn;
	}
}
