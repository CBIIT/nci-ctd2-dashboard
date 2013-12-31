package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.Annotation;
import gov.nih.nci.ctd2.dashboard.model.Organism;
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
import java.util.HashSet;

@Component("cellLineSampleMapper")
public class CellLineSampleFieldSetMapper implements FieldSetMapper<CellSample> {

	private static final String CELL_SAMPLE_ID = "cell_sample_id";
	private static final String	TAXONOMY_ID = "taxonomy_id";
	private static final String GENDER = "gender";

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("cellLineAnnotationSampleMap")
	private HashMap<String,HashSet> cellLineAnnotationSampleMap;

    @Autowired
	@Qualifier("cellSampleMap")
	private HashMap<String,CellSample> cellSampleMap;

	private HashMap<String, Organism> organismsCache = new HashMap<String, Organism>();

	public CellSample mapFieldSet(FieldSet fieldSet) throws BindException {

		String cellSampleId = fieldSet.readString(CELL_SAMPLE_ID);
		String taxonomyId = fieldSet.readString(TAXONOMY_ID);
        String gender = fieldSet.readString(GENDER);

		CellSample cellSample = dashboardFactory.create(CellSample.class);
		// display name will be set in next step
		Organism organism = getOrganism(taxonomyId);
		if (organism != null) cellSample.setOrganism(organism);
        if (gender != null && gender.length() > 0) cellSample.setGender(gender);
        if (cellLineAnnotationSampleMap.containsKey(cellSampleId)) {
            cellSample.setAnnotations(cellLineAnnotationSampleMap.get(cellSampleId));
        }
        else {
            cellSample.setAnnotations(new HashSet<Annotation>());
        }
		// optimization - avoid persisting CellSamples
		// - place in map and pass to cellLineNameStep
		cellSampleMap.put(cellSampleId,cellSample);
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
