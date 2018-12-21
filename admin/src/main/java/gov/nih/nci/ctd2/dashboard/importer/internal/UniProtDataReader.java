package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import gov.nih.nci.ctd2.dashboard.model.Xref;

public class UniProtDataReader implements ItemReader<ProteinData> {

	public static final String UNIPROT_DATABASE = "UNIPROT";

	// identifiers for FieldSets coming into process()
    private static final String LINE_ID_IDENTIFICATION = "ID";
    private static final String LINE_ID_RECORD_NAME = "DE";
	private static final String LINE_ID_ACCESSION = "AC";
	private static final String LINE_ID_NCBI_ORGANISM_TAX = "OX";
	private static final String LINE_ID_DB_XREF = "DR";
    private static final String LINE_ID_RECORD_END = "//";

	// some delimiters & regexs used to process FieldSet strings
	private static final String ACCESSION_DELIMITER = ";";
	//private static final Pattern UNIPROT_ID_REGEX = Pattern.compile("(\\w+)_\\w+");
	private static final Pattern UNIPROT_NAME_REGEX = Pattern.compile("RecName: Full=(.*);");
	private static final Pattern NCBI_TAXONOMY_ID_REGEX = Pattern.compile("NCBI_TaxID=(\\w+)[;| ].*");
	private static final Pattern ENTREZ_REGEX = Pattern.compile("GeneID; (\\w+);");
	private static final Pattern REFSEQ_REGEX = Pattern.compile("RefSeq; (?:N|X)P_\\d+(?:\\.\\d+)?; ((?:N|X)M_\\d+)(?:\\.\\d+)?\\.");

    @Autowired
	private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

	// vars which keep state of current protein/record being processed
	private String geneId;
	private Protein protein;
	private String taxonomyId;
	private HashSet<String> refseqIds = new HashSet<String>();
	private boolean recordFinished;

	// these vars are used to return new organisms & transcripts to the writer
	private HashSet<Transcript> transcriptsToReturn = new HashSet<Transcript>();

	// vars to speed up lookup
	private HashMap<String, Gene> genesCache = new HashMap<String, Gene>();
	private HashMap<String, Organism> organismsCache = new HashMap<String, Organism>();
	private HashMap<String, Transcript> transcriptsCache = new HashMap<String, Transcript>();

	// this gets set for each row
	private ItemReader<FieldSet> fieldSetReader;
	public void setFieldSetReader(ItemReader<FieldSet> fieldSetReader) { this.fieldSetReader = fieldSetReader; }

	public ProteinData read() throws Exception {

		recordFinished = false;
		while (!recordFinished) {
			process(fieldSetReader.read());
		}
		if (protein == null) return null; // this would happen on eof
		Protein proteinToReturn = protein;
		protein = null;
		return new ProteinData(proteinToReturn, new HashSet<Transcript>(transcriptsToReturn));
	}

	private void process(FieldSet fieldSet) throws Exception {

		// end of file
		if (fieldSet == null) {
			recordFinished = true;
			return;
		}

		// grab line id and process accordingly
		String lineId = fieldSet.readString(0);
		if (lineId.equals(LINE_ID_IDENTIFICATION)) {
			resetState();
			protein = dashboardFactory.create(Protein.class);
			// use primary accession, not EntryName as ID - accession is more stable
			//Matcher idMatcher = UNIPROT_ID_REGEX.matcher(fieldSet.readString(1));
			//if (idMatcher.find()) protein.setUniprotId(idMatcher.group(1));
		}
		else if (lineId.equals(LINE_ID_RECORD_NAME) && fieldSet.getValues().length == 2) {
			Matcher recordNameMatcher = UNIPROT_NAME_REGEX.matcher(fieldSet.readString(1));
			if (recordNameMatcher.find() &&
				(protein.getDisplayName() == null ||
				 protein.getDisplayName().length() == 0)) {
				protein.setDisplayName(recordNameMatcher.group(1));
				// create synonym back to self
				Synonym synonym = dashboardFactory.create(Synonym.class);
				synonym.setDisplayName(recordNameMatcher.group(1));
				protein.getSynonyms().add(synonym);
			}
		}
		else if (lineId.equals(LINE_ID_ACCESSION) && fieldSet.getValues().length == 2) {
			for (String accession : fieldSet.readString(1).split(ACCESSION_DELIMITER)) {
				// if uniprotid is null, it hasn't been set yet,
				// use first (primary) accession as uniprotid
				if (protein.getUniprotId() == null) {
					protein.setUniprotId(accession.trim());
				}
				else {
					Xref xref = dashboardFactory.create(Xref.class);
					xref.setDatabaseId(accession.trim());
					xref.setDatabaseName(UNIPROT_DATABASE);
					protein.getXrefs().add(xref);
				}
			}
		}
		else if (lineId.equals(LINE_ID_NCBI_ORGANISM_TAX) && fieldSet.getValues().length == 2) {
			Matcher taxonomyIdMatcher = NCBI_TAXONOMY_ID_REGEX.matcher(fieldSet.readString(1));
			if (taxonomyIdMatcher.find()) taxonomyId = taxonomyIdMatcher.group(1);
		}
		else if (lineId.equals(LINE_ID_DB_XREF) && fieldSet.getValues().length == 2) {
			Matcher refSeqMatcher = REFSEQ_REGEX.matcher(fieldSet.readString(1));
			if (refSeqMatcher.find()) {
				refseqIds.add(refSeqMatcher.group(1));

			}
			else {
				Matcher entrezMatcher = ENTREZ_REGEX.matcher(fieldSet.readString(1));
				if (entrezMatcher.find()) geneId = entrezMatcher.group(1);
			}
		}
		else if (lineId.equals(LINE_ID_RECORD_END)) {
			Organism organism = findOrganism();
			if (organism != null) protein.setOrganism(organism);
			addTranscripts(organism);
			recordFinished = true;
		}
	}

	private void resetState() {
		geneId = "";
		taxonomyId = "";
		refseqIds.clear();
		transcriptsToReturn.clear();
	}

	private void addTranscripts(Organism organism) {
		if (!refseqIds.isEmpty()) {
			Gene gene = findGene();
			for (String refseqId : refseqIds) {
				Transcript transcript = transcriptsCache.get(refseqId);
				if (transcript == null) {
					transcript = dashboardFactory.create(Transcript.class);
					transcript.setRefseqId(refseqId);
					if (gene != null) transcript.setGene(gene);
					if (organism != null) transcript.setOrganism(organism);
					transcriptsToReturn.add(transcript);
					transcriptsCache.put(refseqId, transcript);
				}
				protein.getTranscripts().add(transcript);
			}
		}
	}

	private Gene findGene() {
		Gene toReturn = null;
		if (geneId.length() > 0) {
			toReturn = genesCache.get(geneId);
			if (toReturn == null) {
				List<Gene> genes = dashboardDao.findGenesByEntrezId(geneId);
				if (genes.size() == 1) {
					toReturn = genes.get(0);
					genesCache.put(geneId, toReturn);
				}
			}
		}
		return toReturn;
	}

	private Organism findOrganism() {
		Organism toReturn = null;
		if (taxonomyId.length() > 0) {
			toReturn = organismsCache.get(taxonomyId);
			if (toReturn == null) {
				List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId(taxonomyId);
				if (organisms.size() == 1) {
					toReturn = organisms.get(0);
					organismsCache.put(taxonomyId, toReturn);
				}
			}
		}
		return toReturn;
	}
}
