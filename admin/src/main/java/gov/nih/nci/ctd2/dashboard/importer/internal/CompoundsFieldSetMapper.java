package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Component("compoundsMapper")
public class CompoundsFieldSetMapper implements FieldSetMapper<Compound> {

	public static final String BROAD_COMPOUND_DATABASE = "BROAD_COMPOUND";
	
	private static final String CPD_ID = "CPD_ID";
	private static final String	CPD_PRIMARY_NAME = "CPD_PRIMARY_NAME";
	private static final String	SMILES = "SMILES";

    @Autowired
    private DashboardFactory dashboardFactory;

	public Compound mapFieldSet(FieldSet fieldSet) throws BindException {

		String compoundId = fieldSet.readString(CPD_ID);
		String primaryName = fieldSet.readString(CPD_PRIMARY_NAME);
		String smiles = fieldSet.readString(SMILES);

		Compound compound = dashboardFactory.create(Compound.class);
        compound.setDisplayName(primaryName);
		compound.setSmilesNotation(smiles);
		// create synonym back to self
		Synonym synonym = dashboardFactory.create(Synonym.class);
		synonym.setDisplayName(primaryName);
		compound.getSynonyms().add(synonym);
		// create xref back to broad
		Xref xref = dashboardFactory.create(Xref.class);
		xref.setDatabaseId(compoundId);
		xref.setDatabaseName(BROAD_COMPOUND_DATABASE);
		compound.getXrefs().add(xref);

		return compound;
	}
}
