package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.CellLine;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.HashMap;

@Component("cellLineDataMapper")
public class CellLineDataFieldSetMapper implements FieldSetMapper<CellLine> {

	private static final String PRIMARY_NAME = "Name";
	private static final String	ACHILLES = "Achilles";
	private static final String	BSP = "BSP";
	private static final String	CCLE = "CCLE";
	private static final String	CTD2 = "CTD2";
	private static final String	PRISM = "PRISM";
	private static final String	SANGER = "Sanger";
	private static final String	SANGER_BARCODE = "Sanger_CELL_SAMPLE_BARCODE_Parsed";
	private static final String	TAXONOMY_ID = "taxonomy_id";

	private static final String NA = "NA";
	private static final String TISSUE_START = "_";
	private static final String CELL_LINE_DELIMITER = ";";

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

	private HashMap<String, Organism> organismsCache = new HashMap<String, Organism>();

	public CellLine mapFieldSet(FieldSet fieldSet) throws BindException {

		String primaryName = fieldSet.readString(PRIMARY_NAME);
		String achilles = fieldSet.readString(ACHILLES);
		String bsp = fieldSet.readString(BSP);
		String ccle = fieldSet.readString(CCLE);
		String ctd2 = fieldSet.readString(CTD2);
		String prism = fieldSet.readString(PRISM);
		String sanger = fieldSet.readString(SANGER);
		String sangerBarcode = fieldSet.readString(SANGER_BARCODE);
		String taxId = fieldSet.readString(TAXONOMY_ID);

		CellLine cellLine = dashboardFactory.create(CellLine.class);
		cellLine.setDisplayName(primaryName);
		// create synonym back to self
		setSynonym(cellLine, primaryName);
		// organism
		Organism organism = getOrganism(taxId);
		if (organism != null) cellLine.setOrganism(organism);
		// tissue
		String tissue = getTissue(ccle, sangerBarcode);
		if (tissue != null) cellLine.setTissue(tissue);
		// create other synonyms
		setSynonym(cellLine, achilles);
		setSynonym(cellLine, bsp);
		setSynonym(cellLine, ccle);
		setSynonym(cellLine, ctd2);
		setSynonym(cellLine, prism);
		setSynonym(cellLine, sanger);
		// xrefs
		setXref(cellLine, ACHILLES, achilles);
		setXref(cellLine, BSP, bsp);
		setXref(cellLine, CCLE, ccle);
		setXref(cellLine, CTD2, ctd2);
		setXref(cellLine, PRISM, prism);
		setXref(cellLine, SANGER, sanger);

		return cellLine;
	}

	private void setSynonym(CellLine cellLine, String cellLineName) {
		for (String potentialCellLineName : cellLineName.split(CELL_LINE_DELIMITER)) {
			if (!potentialCellLineName.equals(NA)) {
				boolean addSynonym = true;
				for (Synonym synonym : cellLine.getSynonyms()) {
					if (synonym.getDisplayName().equals(cellLineName)) {
						addSynonym = false;
						break;
					}
				}
				if (addSynonym) {
					Synonym synonym = dashboardFactory.create(Synonym.class);
					synonym.setDisplayName(potentialCellLineName);
					cellLine.getSynonyms().add(synonym);
				}
			}
		}
	}

	private void setXref(CellLine cellLine, String xRefDatabase, String xRefId) {
		for (String potentialxRefId : xRefId.split(CELL_LINE_DELIMITER)) {
			if (!potentialxRefId.equals(NA)) {
				Xref xref = dashboardFactory.create(Xref.class);
				xref.setDatabaseId(potentialxRefId);
				xref.setDatabaseName(xRefDatabase);
				cellLine.getXrefs().add(xref);
			}
		}
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

	// rule: look at ccle column for tissue, if not found, look at sangerBarcode column
	private String getTissue(String ccle, String sangerBarcode) {
		String tissue = null;
		if (!ccle.equals(NA) && ccle.contains(TISSUE_START)) {
			tissue = ccle.substring(ccle.indexOf(TISSUE_START)+1);
		}
		else if (!sangerBarcode.equals(NA) && sangerBarcode.contains(TISSUE_START)) {
			tissue = sangerBarcode.substring(sangerBarcode.indexOf(TISSUE_START)+1);
		}
		return (tissue.length() > 0) ? tissue : null;
	}
}
