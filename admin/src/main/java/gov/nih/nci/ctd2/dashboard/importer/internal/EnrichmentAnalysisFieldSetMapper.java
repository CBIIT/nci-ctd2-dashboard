package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.ObservationDataFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Date;
import java.util.HashSet;

@Component("enrichmentAnalysisMapper")
public class EnrichmentAnalysisFieldSetMapper implements FieldSetMapper<ObservationData> {

	private static final Log log = LogFactory.getLog(EnrichmentAnalysisFieldSetMapper.class);

	private static final String SUBMISSION_CENTER = "SUBMISSION_CENTER";
	private static final String	SUBMISSION_DATE = "SUBMISSION_DATE";
	private static final String TEMPLATE_NAME = "TEMPLATE_NAME";
	private static final String CELL_LINE_SUBSET = "CELL_LINE_SUBSET";
	private static final String CELL_LINE_EXCLUSION = "CELL_LINE_EXCLUSION";
	private static final String FEATURE_DATASET = "FEATURE_DATASET";
	private static final String COMPOUND_NAME = "COMPOUND_NAME";
	private static final String ENTREZ_GENE_ID = "ENTREZ_GENE_ID";
	private static final String LINEAGE = "LINEAGE";
	private static final String ENRICHED_FEATURE = "ENRICHED_FEATURE";
	private static final String NUMBER_OF_CELL_LINES = "NUMBER_OF_CELL_LINES";
	private static final String NUMBER_OF_MUTANT_CELL_LINES = "NUMBER_OF_MUTANT_CELL_LINES";
	private static final String ENRICHMENT_DIRECTION = "ENRICHMENT_DIRECTION";
	private static final String LOG_Q_VALUE_SCORE = "LOG_Q_VALUE_SCORE";
	private static final String RESP_IMAGE_PATH = "RESP_IMAGE_PATH";
	private static final String FEAT_IMAGE_PATH = "FEAT_IMAGE_PATH";
	private static final String NCI_PORTAL = "NCI_PORTAL";
	private static final String GCT_PATH = "GCT_PATH";

	@Autowired
	private ObservationDataFactory observationDataFactory;

    @Autowired
    private DashboardFactory dashboardFactory;

	public ObservationData mapFieldSet(FieldSet fieldSet) throws BindException {

		// create submission
		Submission submission = observationDataFactory.createSubmission(fieldSet.readString(SUBMISSION_CENTER),
																		fieldSet.readDate(SUBMISSION_DATE, "mm/DD/yyyy"),
																		fieldSet.readString(TEMPLATE_NAME));

		// create observation
		Observation observation = dashboardFactory.create(Observation.class);
		observation.setSubmission(submission);

		// these will contain all observed entities / evidence we will persist
		HashSet<DashboardEntity> evidenceSet = new HashSet<DashboardEntity>();
		HashSet<DashboardEntity> observedEntitiesSet = new HashSet<DashboardEntity>();

		// create observed subjects
		try {
			ObservedSubject observedCompound = 
				observationDataFactory.createObservedSubject(fieldSet.readString(COMPOUND_NAME),
															 COMPOUND_NAME, observation, "findCompoundsByName");
			observedEntitiesSet.add(observedCompound);

			ObservedSubject observedGene =
				observationDataFactory.createObservedSubject(fieldSet.readString(ENTREZ_GENE_ID),
															 ENTREZ_GENE_ID, observation, "findGenesByEntrezId");
			//////////////////
			// add lineage here
			//////////////////

			observedEntitiesSet.add(observedGene);
		}
		catch (Exception e) {
			log.info("Exception thrown processing observation data row, skipping row: " + e.getMessage());
			return new ObservationData(null, null, null);
		}

		// create observed evidence
		ObservedEvidence cellLineSubsetEvidence =
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(CELL_LINE_SUBSET),
															   CELL_LINE_SUBSET, observation);
		observedEntitiesSet.add(cellLineSubsetEvidence);
		if (cellLineSubsetEvidence.getEvidence() != null) evidenceSet.add(cellLineSubsetEvidence.getEvidence());

		ObservedEvidence cellLineExclusionEvidence =
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(CELL_LINE_EXCLUSION),
															   CELL_LINE_EXCLUSION, observation);
		observedEntitiesSet.add(cellLineExclusionEvidence);
		if (cellLineExclusionEvidence.getEvidence() != null) evidenceSet.add(cellLineExclusionEvidence.getEvidence());

		ObservedEvidence featureDatasetEvidence =
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(FEATURE_DATASET),
															   FEATURE_DATASET, observation);
		observedEntitiesSet.add(featureDatasetEvidence);
		if (featureDatasetEvidence.getEvidence() != null) evidenceSet.add(featureDatasetEvidence.getEvidence());

		ObservedEvidence enrichedFeatureEvidence =
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(ENRICHED_FEATURE),
															   ENRICHED_FEATURE, observation);
		observedEntitiesSet.add(enrichedFeatureEvidence);
		if (enrichedFeatureEvidence.getEvidence() != null) evidenceSet.add(enrichedFeatureEvidence.getEvidence());

		ObservedEvidence numberOfCellLinesEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readInt(NUMBER_OF_CELL_LINES),
																 NUMBER_OF_CELL_LINES, observation);
		observedEntitiesSet.add(numberOfCellLinesEvidence);
		if (numberOfCellLinesEvidence.getEvidence() != null) evidenceSet.add(numberOfCellLinesEvidence.getEvidence());

		ObservedEvidence numberOfMutantCellLinesEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readInt(NUMBER_OF_MUTANT_CELL_LINES),
																 NUMBER_OF_MUTANT_CELL_LINES, observation);
		observedEntitiesSet.add(numberOfMutantCellLinesEvidence);
		if (numberOfMutantCellLinesEvidence.getEvidence() != null) evidenceSet.add(numberOfMutantCellLinesEvidence.getEvidence());

		ObservedEvidence enrichmentDirectionEvidence = 
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(ENRICHMENT_DIRECTION),
															   ENRICHMENT_DIRECTION, observation);
		observedEntitiesSet.add(enrichmentDirectionEvidence);
		if (enrichmentDirectionEvidence.getEvidence() != null) evidenceSet.add(enrichmentDirectionEvidence.getEvidence());

		ObservedEvidence logQValueScoreEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(LOG_Q_VALUE_SCORE),
																 LOG_Q_VALUE_SCORE, observation);
		observedEntitiesSet.add(logQValueScoreEvidence);
		if (logQValueScoreEvidence.getEvidence() != null) evidenceSet.add(logQValueScoreEvidence.getEvidence());

		ObservedEvidence respImagePathEvidence = 
			observationDataFactory.createObservedFileEvidence(fieldSet.readString(RESP_IMAGE_PATH),
															  RESP_IMAGE_PATH, observation);
		observedEntitiesSet.add(respImagePathEvidence);
		if (respImagePathEvidence.getEvidence() != null) evidenceSet.add(respImagePathEvidence.getEvidence());

		ObservedEvidence featImagePathEvidence = 
			observationDataFactory.createObservedFileEvidence(fieldSet.readString(FEAT_IMAGE_PATH),
															  FEAT_IMAGE_PATH, observation);
		observedEntitiesSet.add(featImagePathEvidence);
		if (featImagePathEvidence.getEvidence() != null) evidenceSet.add(featImagePathEvidence.getEvidence());

		ObservedEvidence nciPortalPathEvidence = 
			observationDataFactory.createObservedUrlEvidence(fieldSet.readString(NCI_PORTAL),
															 NCI_PORTAL, observation);
		observedEntitiesSet.add(nciPortalPathEvidence);
		if (nciPortalPathEvidence.getEvidence() != null) evidenceSet.add(nciPortalPathEvidence.getEvidence());

		ObservedEvidence gctPathEvidence = 
			observationDataFactory.createObservedFileEvidence(fieldSet.readString(GCT_PATH),
															  GCT_PATH, observation);
		observedEntitiesSet.add(gctPathEvidence);
		if (gctPathEvidence.getEvidence() != null) evidenceSet.add(gctPathEvidence.getEvidence());

		return new ObservationData(observation, observedEntitiesSet, evidenceSet);
	}
}
