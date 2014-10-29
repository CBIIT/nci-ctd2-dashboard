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

@Component("tissueSampleSynonymsDataMapper")
public class TissueSampleSynonymsDataFieldSetMapper implements FieldSetMapper<TissueSample> {

	public static final String NCI_THESAURUS_DATABASE = "NCI_THESAURUS";

	private static final String	NCI_THESAURUS_CODE = "nci_thesaurus_code";
	private static final String	SYNONYM = "synonym";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("tissueSampleMap")
	private HashMap<String,TissueSample> tissueSampleMap;       

	public TissueSample mapFieldSet(FieldSet fieldSet) throws BindException {

        String nciThesaurusCode = fieldSet.readString(NCI_THESAURUS_CODE);
        TissueSample tissueSample = tissueSampleMap.get(nciThesaurusCode);
        if (tissueSample != null) {
            Synonym synonym = dashboardFactory.create(Synonym.class);
            synonym.setDisplayName(fieldSet.readString(SYNONYM));
            tissueSample.getSynonyms().add(synonym);
        }

        return tissueSample;
	}
}
