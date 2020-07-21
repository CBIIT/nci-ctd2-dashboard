package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.GeneType;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;

@Component("geneDataMapper")
public class GeneDataFieldSetMapper implements FieldSetMapper<Gene> {

	public static final String NCBI_GENE_DATABASE = "NCBI_GENE";

	@Autowired
	private DashboardFactory dashboardFactory;

	@Autowired
	private DashboardDao dashboardDao;

	private HashMap<String, Organism> organismMap = new HashMap<String, Organism>();
	private Map<String, GeneType> geneTypeMap = new HashMap<String, GeneType>();

	public Gene mapFieldSet(FieldSet fieldSet) throws BindException {

		Gene gene = dashboardFactory.create(Gene.class);
		String entrezGeneId = fieldSet.readString(1);
		gene.setEntrezGeneId(entrezGeneId);
		gene.setDisplayName(fieldSet.readString(2));
		// create synonym back to self
		Synonym synonym = dashboardFactory.create(Synonym.class);
		synonym.setDisplayName(fieldSet.readString(2));
		gene.getSynonyms().add(synonym);
		// create xref back to ncbi
		Xref xref = dashboardFactory.create(Xref.class);
		xref.setDatabaseId(entrezGeneId);
		xref.setDatabaseName(NCBI_GENE_DATABASE);
		gene.getXrefs().add(xref);
		for (String synonymName : fieldSet.readString(4).split("\\|")) {
			synonym = dashboardFactory.create(Synonym.class);
			synonym.setDisplayName(synonymName);
			gene.getSynonyms().add(synonym);
		}
		for (String synonymName : fieldSet.readString(13).split("\\|")) {
			synonym = dashboardFactory.create(Synonym.class);
			synonym.setDisplayName(synonymName);
			gene.getSynonyms().add(synonym);
		}
		// hgnc parsing
		for (String dbXrefs : fieldSet.readString(5).split("\\|")) {
			String[] parts = dbXrefs.split("\\:");
			if (parts[0].equals("HGNC")) {
				gene.setHGNCId(parts[1]);
				break;
			}
		}
		// set organism
		String taxonomyId = fieldSet.readString(0);
		Organism organism = organismMap.get(taxonomyId);
		if (organism == null) {
			List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId(taxonomyId);
			if (organisms.size() == 1)
				organism = organisms.get(0);
			organismMap.put(taxonomyId, organism);
		}
		if (organism != null)
			gene.setOrganism(organism);

		String map_location = fieldSet.readString(7);
		gene.setMapLocation(map_location);

		String description = fieldSet.readString(8);
		gene.setFullName(description);

		String type_of_gene = fieldSet.readString(9);
		GeneType geneType = geneTypeMap.get(type_of_gene);
		if (geneType == null) {
			geneType = dashboardFactory.create(GeneType.class);
			geneType.setDisplayName(type_of_gene);
			geneTypeMap.put(type_of_gene, geneType);
			dashboardDao.save(geneType);
		}
		gene.setGeneType(geneType);

		return gene;
	}
}
