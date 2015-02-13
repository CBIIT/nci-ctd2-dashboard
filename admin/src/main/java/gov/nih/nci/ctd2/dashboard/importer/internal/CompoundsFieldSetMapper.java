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
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.HashMap;

@Component("compoundsMapper")
public class CompoundsFieldSetMapper implements FieldSetMapper<Compound> {

	public static final String BROAD_COMPOUND_DATABASE = "BROAD_COMPOUND";
	public static final String COMPOUND_IMAGE_DATABASE = "IMAGE";
	public static final String PUBCHEM_DATABASE = "PUBCHEM";
    public static final String CAS_DATABASE = "CAS";
    
	private static final String CPD_ID = "CPD_ID";
	private static final String	CPD_PRIMARY_NAME = "CPD_PRIMARY_NAME";
	private static final String	SMILES = "SMILES";
	private static final String	STRUCTURE_FILE = "STRUCTURE_FILE";
    private static final String	PUBCHEM_CID = "PUBCHEM_CID";
    private static final String	CAS_NUMBER = "CAS_NUMBER";

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("compoundMap")
	private HashMap<String,Compound> compoundMap;

	public Compound mapFieldSet(FieldSet fieldSet) throws BindException {

		String compoundId = fieldSet.readString(CPD_ID);
		String primaryName = fieldSet.readString(CPD_PRIMARY_NAME);
		String smiles = fieldSet.readString(SMILES);
		String structureFile = fieldSet.readString(STRUCTURE_FILE);
        String pubchemCid = fieldSet.readString(PUBCHEM_CID);
        String casNumber = fieldSet.readString(CAS_NUMBER);
        
		Compound compound = dashboardFactory.create(Compound.class);
        compound.setDisplayName(primaryName);
		compound.setSmilesNotation(smiles);
		// create synonym back to self
		Synonym synonym = dashboardFactory.create(Synonym.class);
		synonym.setDisplayName(primaryName);
		compound.getSynonyms().add(synonym);
		// create xref back to broad
		Xref compoundXref = dashboardFactory.create(Xref.class);
		compoundXref.setDatabaseId(compoundId);
		compoundXref.setDatabaseName(BROAD_COMPOUND_DATABASE);
		compound.getXrefs().add(compoundXref);
		// create xref to structure file
		Xref imageXref = dashboardFactory.create(Xref.class);
		imageXref.setDatabaseId(structureFile);
		imageXref.setDatabaseName(COMPOUND_IMAGE_DATABASE);
		compound.getXrefs().add(imageXref);
        // create xref to pubchem
		if (pubchemCid != null && !pubchemCid.isEmpty()) {
            Xref pubchemXref = dashboardFactory.create(Xref.class);
		    pubchemXref.setDatabaseId(pubchemCid);
		    pubchemXref.setDatabaseName(PUBCHEM_DATABASE);
		    compound.getXrefs().add(pubchemXref);
        }
        // create xref to cas
        if (casNumber != null && !casNumber.isEmpty()) {
		    Xref casXref = dashboardFactory.create(Xref.class);
		    casXref.setDatabaseId(casNumber);
		    casXref.setDatabaseName(CAS_DATABASE);
		    compound.getXrefs().add(casXref);
        }
		// optimization - avoid persisting Compounds
		// - place in map and pass to compoundSynonymsStep
		compoundMap.put(compoundId, compound);
		return compound;
	}
}
