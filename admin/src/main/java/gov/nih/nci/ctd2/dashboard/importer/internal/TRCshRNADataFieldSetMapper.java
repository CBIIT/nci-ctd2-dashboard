package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
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
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@Component("TRCshRNADataMapper")
public class TRCshRNADataFieldSetMapper implements FieldSetMapper<ShRna> {

	public static final String BROAD_SHRNA_DATABASE = "BROAD_SHRNA";
	private static final String MISSING_ENTRY = "EMPTY";
	private static final String REFSEQ_DELIMITER = ",";

	private static final int CLONE_ID_COL_INDEX = 4;
	private static final int TARGET_SEQ_COL_INDEX = 5;
	private static final int CLONE_NAME_COL_INDEX = 8;
	private static final int TAX_ID_COL_INDEX = 11;
	private static final int TRANSCRIPT_ID_COL_INDEX = 10;
    private static final int ALT_TRANSCRIPT_ID_COL_INDEX = 15;

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	private DashboardDao dashboardDao;

    @Autowired
	@Qualifier("TRCshRNAFilterMap")
	private HashMap<String,String> tRCshRNAFilterMap;

    private HashMap<String, Organism> organismMap = new HashMap<String, Organism>();
    private HashMap<String, Transcript> transcriptMap = new HashMap<String, Transcript>();

	public ShRna mapFieldSet(FieldSet fieldSet) throws BindException {

        ShRna shRNA = dashboardFactory.create(ShRna.class);
        shRNA.setType("shrna");

		// sanity check
		String cloneId = fieldSet.readString(CLONE_ID_COL_INDEX);
		if (cloneId.equalsIgnoreCase(MISSING_ENTRY)) return shRNA;

		// only process records that exist in our map
		if (!tRCshRNAFilterMap.isEmpty() && !tRCshRNAFilterMap.containsKey(cloneId)) return shRNA;

        shRNA.setDisplayName(fieldSet.readString(TARGET_SEQ_COL_INDEX));
        shRNA.setReagentName(cloneId);
		// create synonym back to self
		Synonym synonym = dashboardFactory.create(Synonym.class);
		synonym.setDisplayName(fieldSet.readString(CLONE_NAME_COL_INDEX));
		shRNA.getSynonyms().add(synonym);
		// create xref back to broad
		Xref xref = dashboardFactory.create(Xref.class);
		xref.setDatabaseId(cloneId);
		xref.setDatabaseName(BROAD_SHRNA_DATABASE);
		shRNA.getXrefs().add(xref);
		// set target seq
		shRNA.setTargetSequence(fieldSet.readString(TARGET_SEQ_COL_INDEX));
		// set organism
        String taxonomyId = fieldSet.readString(TAX_ID_COL_INDEX);
        Organism organism = organismMap.get(taxonomyId);
        if (organism == null) {
			List<Organism> organisms = dashboardDao.findOrganismByTaxonomyId(taxonomyId);
			if (organisms.size() == 1) organism = organisms.get(0);
            organismMap.put(taxonomyId, organism);
        }
		if (organism != null) shRNA.setOrganism(organism);
		// set transcript
		Transcript transcript = getTranscript(fieldSet.readString(TRANSCRIPT_ID_COL_INDEX));
		if (transcript != null) {
            shRNA.setTranscript(transcript);
        }
        else {
		    transcript = getTranscript(fieldSet.readString(ALT_TRANSCRIPT_ID_COL_INDEX));
            if (transcript != null) shRNA.setTranscript(transcript);
        }

        return shRNA;
	}

	private Transcript getTranscript(String columnEntry) {
        
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
				transcriptMap.put(transcriptId, transcripts.get(0));
				return transcripts.get(0);
			}
		}

		// made it here
		return null;
	}
}
