package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.TissueSample;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Component("tissueSampleDataMapper")
public class TissueSampleDataFieldSetMapper implements FieldSetMapper<TissueSample> {

	private static final String TISSUE_SAMPLE_NAME = "tissue_sample_name";
	private static final String	TISSUE_SAMPLE_LINEAGE = "tissue_sample_lineage";

    @Autowired
    private DashboardFactory dashboardFactory;

	public TissueSample mapFieldSet(FieldSet fieldSet) throws BindException {

        TissueSample tissueSample = dashboardFactory.create(TissueSample.class);
		tissueSample.setDisplayName(fieldSet.readString(TISSUE_SAMPLE_NAME));
		tissueSample.setLineage(fieldSet.readString(TISSUE_SAMPLE_LINEAGE));
		// create a synonym to self
		Synonym synonym = dashboardFactory.create(Synonym.class);
		synonym.setDisplayName(fieldSet.readString(TISSUE_SAMPLE_NAME));
		tissueSample.getSynonyms().add(synonym);

        return tissueSample;
	}
}
