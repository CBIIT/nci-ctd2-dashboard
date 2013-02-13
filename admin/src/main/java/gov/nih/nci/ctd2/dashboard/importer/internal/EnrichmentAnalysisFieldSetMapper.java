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

	@Autowired
	private ObservationDataFactory observationDataFactory;

    @Autowired
    private DashboardFactory dashboardFactory;

	public ObservationData mapFieldSet(FieldSet fieldSet) throws BindException {

		String submissionCenterName = fieldSet.readString(0).toLowerCase();
		Date submissionDate = fieldSet.readDate(1, "mm/DD/yyyy");
		String templateName = fieldSet.readString(2).toLowerCase();
		String cellLineSubset = fieldSet.readString(3).toLowerCase();
		String cellLineExclusion = fieldSet.readString(4).toLowerCase();
		String featureDataset = fieldSet.readString(5).toLowerCase();
		String compoundName = fieldSet.readString(6).toLowerCase();
		String geneId = fieldSet.readString(7).toLowerCase();
		String lineage = fieldSet.readString(8).toLowerCase();
		String enrichedFeature = fieldSet.readString(9).toLowerCase();
		Integer numberOfCellLines = fieldSet.readInt(10);
		Integer numberOfMutantCellLines = fieldSet.readInt(11);
		String enrichmentDirection = fieldSet.readString(12).toLowerCase();
		Double logQValueScore = fieldSet.readDouble(13);
		String respImagePath = fieldSet.readString(14).toLowerCase();
		String featImagePath = fieldSet.readString(15).toLowerCase();
		String compoundStructurePath = fieldSet.readString(16).toLowerCase();
		String nciPortalPath = fieldSet.readString(17).toLowerCase();
		String gctPath = fieldSet.readString(18).toLowerCase();

		// create submission
		Submission submission = observationDataFactory.createSubmission(submissionCenterName, submissionDate, templateName);

		// create observation
		Observation observation = dashboardFactory.create(Observation.class);
		observation.setSubmission(submission);

		// these will contain all observed entities / evidence we will persist
		HashSet<DashboardEntity> evidenceSet = new HashSet<DashboardEntity>();
		HashSet<DashboardEntity> observedEntitiesSet = new HashSet<DashboardEntity>();

		// create observed subjects
		try {
			ObservedSubject observedCompound = 
				observationDataFactory.createObservedSubject(compoundName, fieldSet.getNames()[3],
															 observation, "findCompoundsByName");
			observedEntitiesSet.add(observedCompound);
			ObservedSubject observedGene =
				observationDataFactory.createObservedSubject(geneId, fieldSet.getNames()[7],
															 observation, "findGenesByEntrezId");
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
			observationDataFactory.createObservedLabelEvidence(cellLineSubset, fieldSet.getNames()[3], observation);
		observedEntitiesSet.add(cellLineSubsetEvidence);
		if (cellLineSubsetEvidence.getEvidence() != null) evidenceSet.add(cellLineSubsetEvidence.getEvidence());

		ObservedEvidence cellLineExclusionEvidence =
			observationDataFactory.createObservedLabelEvidence(cellLineExclusion, fieldSet.getNames()[4], observation);
		observedEntitiesSet.add(cellLineExclusionEvidence);
		if (cellLineExclusionEvidence.getEvidence() != null) evidenceSet.add(cellLineExclusionEvidence.getEvidence());

		ObservedEvidence featureDatasetEvidence =
			observationDataFactory.createObservedLabelEvidence(featureDataset, fieldSet.getNames()[5], observation);
		observedEntitiesSet.add(featureDatasetEvidence);
		if (featureDatasetEvidence.getEvidence() != null) evidenceSet.add(featureDatasetEvidence.getEvidence());

		ObservedEvidence numberOfCellLinesEvidence = 
			observationDataFactory.createObservedNumericEvidence(numberOfCellLines, fieldSet.getNames()[10], observation);
		observedEntitiesSet.add(numberOfCellLinesEvidence);
		if (numberOfCellLinesEvidence.getEvidence() != null) evidenceSet.add(numberOfCellLinesEvidence.getEvidence());

		ObservedEvidence numberOfMutantCellLinesEvidence = 
			observationDataFactory.createObservedNumericEvidence(numberOfMutantCellLines, fieldSet.getNames()[11], observation);
		observedEntitiesSet.add(numberOfMutantCellLinesEvidence);
		if (numberOfMutantCellLinesEvidence.getEvidence() != null) evidenceSet.add(numberOfMutantCellLinesEvidence.getEvidence());

		ObservedEvidence enrichmentDirectionEvidence = 
			observationDataFactory.createObservedLabelEvidence(enrichmentDirection, fieldSet.getNames()[12], observation);
		observedEntitiesSet.add(enrichmentDirectionEvidence);
		if (enrichmentDirectionEvidence.getEvidence() != null) evidenceSet.add(enrichmentDirectionEvidence.getEvidence());

		ObservedEvidence logQValueScoreEvidence = 
			observationDataFactory.createObservedNumericEvidence(logQValueScore, fieldSet.getNames()[13], observation);
		observedEntitiesSet.add(logQValueScoreEvidence);
		if (logQValueScoreEvidence.getEvidence() != null) evidenceSet.add(logQValueScoreEvidence.getEvidence());

		ObservedEvidence respImagePathEvidence = 
			observationDataFactory.createObservedFileEvidence(respImagePath, "image", fieldSet.getNames()[14], observation);
		observedEntitiesSet.add(respImagePathEvidence);
		if (respImagePathEvidence.getEvidence() != null) evidenceSet.add(respImagePathEvidence.getEvidence());

		ObservedEvidence featImagePathEvidence = 
			observationDataFactory.createObservedFileEvidence(featImagePath, "image", fieldSet.getNames()[15], observation);
		observedEntitiesSet.add(featImagePathEvidence);
		if (featImagePathEvidence.getEvidence() != null) evidenceSet.add(featImagePathEvidence.getEvidence());

		// should go into compound data file?
		ObservedEvidence compoundStructurePathEvidence = 
			observationDataFactory.createObservedFileEvidence(compoundStructurePath, "image", fieldSet.getNames()[16], observation);
		observedEntitiesSet.add(compoundStructurePathEvidence);
		if (compoundStructurePathEvidence.getEvidence() != null) evidenceSet.add(compoundStructurePathEvidence.getEvidence());

		ObservedEvidence nciPortalPathEvidence = 
			observationDataFactory.createObservedUrlEvidence(nciPortalPath, fieldSet.getNames()[17], observation);
		observedEntitiesSet.add(nciPortalPathEvidence);
		if (nciPortalPathEvidence.getEvidence() != null) evidenceSet.add(nciPortalPathEvidence.getEvidence());

		ObservedEvidence gctPathEvidence = 
			observationDataFactory.createObservedFileEvidence(gctPath, "gct", fieldSet.getNames()[18], observation);
		observedEntitiesSet.add(gctPathEvidence);
		if (gctPathEvidence.getEvidence() != null) evidenceSet.add(gctPathEvidence.getEvidence());

		return new ObservationData(observation, observedEntitiesSet, evidenceSet);
	}
}
