package gov.nih.nci.ctd2.dashboard.importer.internal;


import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Component("animalModelMapper")
public class AnimalModelFieldSetMapper implements FieldSetMapper<AnimalModel> {

	private static final String NAME = "animal_model_name";
	private static final String	TAXONOMY_ID = "taxonomy_id";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	private DashboardDao dashboardDao;

    private HashMap<String, Organism> organismMap = new HashMap<String, Organism>();

	public AnimalModel mapFieldSet(FieldSet fieldSet) throws BindException {

		String name = fieldSet.readString(NAME);
		String taxId = fieldSet.readString(TAXONOMY_ID);

		AnimalModel animalModel = dashboardFactory.create(AnimalModel.class);
		animalModel.setDisplayName(name);

        Organism organism = organismMap.get(taxId);
        if (organism == null) {
			List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId(taxId);
			if (organisms.size() == 1) organism = organisms.get(0);
            organismMap.put(taxId, organism);
        }
		if (organism != null) animalModel.setOrganism(organism);
		return animalModel;
	}
}
