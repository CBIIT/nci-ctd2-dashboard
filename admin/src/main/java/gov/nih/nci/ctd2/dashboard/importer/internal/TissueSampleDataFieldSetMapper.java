package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
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

	public static final String NCI_THESAURUS_DATABASE = "NCI_THESAURUS";
	public static final String NCI_PARENT_THESAURUS_DATABASE = "NCI_PARENT_THESAURUS";

	private static final String TISSUE_SAMPLE_NAME = "tissue_sample_name";
	private static final String	TISSUE_SAMPLE_LINEAGE = "tissue_sample_lineage";
	private static final String	NCI_THESAURUS_CODE = "nci_thesaurus_code";
	private static final String	PARENTS = "parents";

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
        // create xref to NCI thesaurus
        String nciThesaurusCode = fieldSet.readString(NCI_THESAURUS_CODE);
        if (!nciThesaurusCode.isEmpty()) {
            addXrefToSample(tissueSample, nciThesaurusCode, NCI_THESAURUS_DATABASE);
        }
        // create xref to NCI thesaurus (parent)
        nciThesaurusCode = fieldSet.readString(PARENTS);
        if (!nciThesaurusCode.isEmpty()) {
            addXrefToSample(tissueSample, nciThesaurusCode, NCI_PARENT_THESAURUS_DATABASE);
        }

        return tissueSample;
	}

    private void addXrefToSample(TissueSample tissueSample, String id, String database)
    {
        Xref xref = dashboardFactory.create(Xref.class);
        xref.setDatabaseId(id);
        xref.setDatabaseName(database);
        tissueSample.getXrefs().add(xref);
    }
}
