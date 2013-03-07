package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;

@Component("taxonomyDataMapper")
public class TaxonomyDataFieldSetMapper implements FieldSetMapper<Organism> {

	private static final String NAME = "name";
	private static final String	TAXONOMY_ID = "taxonomy_id";

    @Autowired
    private DashboardFactory dashboardFactory;

	public Organism mapFieldSet(FieldSet fieldSet) throws BindException {

		String name = fieldSet.readString(NAME);
		String taxId = fieldSet.readString(TAXONOMY_ID);

		Organism organism = dashboardFactory.create(Organism.class);
		organism.setDisplayName(name);
		organism.setTaxonomyId(taxId);

		return organism;
	}
}
