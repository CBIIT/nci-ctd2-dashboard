package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.ShRna;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.util.NaturalOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@Component("siRNADataMapper")
public class siRNADataFieldSetMapper implements FieldSetMapper<ShRna> {

	private static final String REFSEQ_DELIMITER = " ";

	private static final int REAGENT_NAME_COL_INDEX = 0;
	private static final int TARGET_SEQ_COL_INDEX = 1;
	private static final int TRANSCRIPT_ID_COL_INDEX = 2;
	private static final int TARGET_GENE_COL_INDEX = 3;
	private static final int ORGANISM_COL_INDEX = 4;

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	private DashboardDao dashboardDao;

    private HashMap<String, Organism> organismMap = new HashMap<String, Organism>();
    private HashMap<String, Transcript> transcriptMap = new HashMap<String, Transcript>();

	public ShRna mapFieldSet(FieldSet fieldSet) throws BindException {

        ShRna shRNA = dashboardFactory.create(ShRna.class);
        shRNA.setType("sirna");

        shRNA.setDisplayName(fieldSet.readString(REAGENT_NAME_COL_INDEX));
        shRNA.setReagentName(shRNA.getDisplayName());
        // create synonym back to self
		Synonym synonym = dashboardFactory.create(Synonym.class);
		synonym.setDisplayName(fieldSet.readString(REAGENT_NAME_COL_INDEX));
		shRNA.getSynonyms().add(synonym);
		// set target seq
		shRNA.setTargetSequence(fieldSet.readString(TARGET_SEQ_COL_INDEX));
		// set organism
	    if (fieldSet.readString(ORGANISM_COL_INDEX).equals("Homo sapiens")) {
	        Organism organism = organismMap.get("9606");
	        if (organism == null) {
				List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId("9606");
				if (organisms.size() == 1) organism = organisms.get(0);
	            organismMap.put("9606", organism);
	        }
			if (organism != null) shRNA.setOrganism(organism);
		}
		// set transcript
		Transcript transcript = getTranscript(fieldSet.readString(TARGET_GENE_COL_INDEX),
		                                      fieldSet.readString(TRANSCRIPT_ID_COL_INDEX));
		if (transcript != null) shRNA.setTranscript(transcript);

        return shRNA;
	}

	private Transcript getTranscript(String targetGene, String columnEntry) {
        
        // split individual RefSeq IDs and sort naturally to test more mature RefSeq IDs first
        // natural order sorts leading zeros and things like .2 < .10 properly
        // natural order sorts by length properly, i.e. "NM_02044" < "NM_001001556" 
        // natural order not only sorts NM < XM also lower IDs first (lower RefSeq IDs are more mature)
        String[] transcriptIds = columnEntry.split(REFSEQ_DELIMITER);
		Arrays.sort(transcriptIds, new NaturalOrderComparator());
        // strip off RefSeq versions
        for (int i = 0; i < transcriptIds.length; i++) {
            transcriptIds[i] = transcriptIds[i].replaceFirst("\\.\\d+$", "");
        }
		// first look in hashmap
		for (String transcriptId : transcriptIds) {
			if (transcriptMap.containsKey(transcriptId)) {
				return transcriptMap.get(transcriptId);
			}
		}
		// now look in database
		for (String transcriptId : transcriptIds) {
			List<Transcript> transcripts = dashboardDao.findTranscriptsByRefseqId(transcriptId);
			if (transcripts.size() == 1) {
				Transcript t = transcripts.get(0);
				if (t.getGene().getDisplayName().equals(targetGene)) {
					transcriptMap.put(transcriptId, t);
					return t;
				}
			}
		}

		// made it here
		return null;
	}
}
