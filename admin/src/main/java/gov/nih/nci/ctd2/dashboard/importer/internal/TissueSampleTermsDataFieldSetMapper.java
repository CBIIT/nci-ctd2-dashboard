package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Component("tissueSampleTermsDataMapper")
public class TissueSampleTermsDataFieldSetMapper implements FieldSetMapper<TissueSample> {

	public static final String NCI_THESAURUS_DATABASE = "NCI_THESAURUS";
	public static final String NCI_PARENT_THESAURUS_DATABASE = "NCI_PARENT_THESAURUS";

	private static final String TISSUE_SAMPLE_NAME = "display_name";
	private static final String	NCI_THESAURUS_CODE = "nci_thesaurus_code";
	private static final String	PARENTS = "parents";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("tissueSampleMap")
	private HashMap<String,TissueSample> tissueSampleMap;

	public TissueSample mapFieldSet(FieldSet fieldSet) throws BindException {

        TissueSample tissueSample = dashboardFactory.create(TissueSample.class);
		tissueSample.setDisplayName(fieldSet.readString(TISSUE_SAMPLE_NAME));
        // create xref to NCI thesaurus
        String nciThesaurusCode = fieldSet.readString(NCI_THESAURUS_CODE);
        if (!nciThesaurusCode.isEmpty()) {
            addXrefToSample(tissueSample, nciThesaurusCode, NCI_THESAURUS_DATABASE);
        }
        // create xref to NCI thesaurus (parent)
        String parents = fieldSet.readString(PARENTS);
        if (!parents.isEmpty()) {
            for (String parentThesaurusCode : parents.split(";")) {
                addXrefToSample(tissueSample, parentThesaurusCode, NCI_PARENT_THESAURUS_DATABASE);
            }
        }
        tissueSampleMap.put(nciThesaurusCode, tissueSample);
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
