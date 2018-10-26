package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.HashMap;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.model.Synonym;

@Component("compoundSynonymsMapper")
public class CompoundSynonymsFieldSetMapper implements FieldSetMapper<Compound> {

	private static final String CPD_ID = "CPD_ID";
	private static final String	CPD_NAME = "CPD_NAME";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("compoundMap")
	private HashMap<String,Compound> compoundMap;

	public Compound mapFieldSet(FieldSet fieldSet) throws BindException {

		String compoundId = fieldSet.readString(CPD_ID);
		String compoundName = fieldSet.readString(CPD_NAME);

		// find compound by xref (broad)
		Compound compound = compoundMap.get(compoundId);
		if (compound != null) {
			Synonym synonym = dashboardFactory.create(Synonym.class);
			synonym.setDisplayName(compoundName);
			compound.getSynonyms().add(synonym);
		}
		return compound;
	}
}
