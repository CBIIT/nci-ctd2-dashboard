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

@Component("signalingAnalysisMapper")
public class SignalingAnalysisFieldSetMapper implements FieldSetMapper<ObservationData> {

	private static final Log log = LogFactory.getLog(SignalingAnalysisFieldSetMapper.class);

	private static final String SUBMISSION_CENTER = "SUBMISSION_CENTER";
	private static final String	SUBMISSION_DATE = "SUBMISSION_DATE";
	private static final String TEMPLATE_NAME = "TEMPLATE_NAME";
	private static final String AFFY_PROBESET_ID = "AFFY_PROBESET_ID";
	private static final String ENTREZ_GENE_ID = "ENTREZ_GENE_ID";
	private static final String CELL_LINEAGE_TISSUE = "CELL_LINEAGE_TISSUE";
	private static final String CELL_LINEAGE_BLOOD_TYPE = "CELL_LINEAGE_BLOOD_TYPE";
	private static final String LOG2_FOLD_CHANGE = "LOG2_FOLD_CHANGE";
	private static final String AVG_EXPR = "AVG_EXPR";
	private static final String Z_SCORE = "Z_SCORE";
	private static final String P_VALUE = "P_VALUE";
	private static final String ADJ_P_VALUE = "ADJ_P_VALUE";
	private static final String FUNC_TYPE = "FUNC_TYPE";
	private static final String SET_SIZE = "SET_SIZE";
	private static final String GSEA_ENRICH_SCORE = "GSEA_ENRICH_SCORE";
	private static final String GSEA_P_VALUE = "GSEA_P_VALUE";
	private static final String GSEA_FDR = "GSEA_FDR";

	@Autowired
	private ObservationDataFactory observationDataFactory;

    @Autowired
    private DashboardFactory dashboardFactory;

	public ObservationData mapFieldSet(FieldSet fieldSet) throws BindException {

		String templateName = fieldSet.readString(TEMPLATE_NAME);

		// create submission
		Submission submission = observationDataFactory.createSubmission(fieldSet.readString(SUBMISSION_CENTER),
																		fieldSet.readDate(SUBMISSION_DATE, "mm/DD/yyyy"),
																		templateName);

		// create observation
		Observation observation = dashboardFactory.create(Observation.class);
		observation.setSubmission(submission);

		// these will contain all observed entities / evidence we will persist
		HashSet<DashboardEntity> evidenceSet = new HashSet<DashboardEntity>();
		HashSet<DashboardEntity> observedEntitiesSet = new HashSet<DashboardEntity>();

		// create observed subjects
		try {
			ObservedSubject observedGene =
				observationDataFactory.createObservedSubject(fieldSet.readString(ENTREZ_GENE_ID),
															 ENTREZ_GENE_ID, templateName, observation, "findGenesByEntrezId");
			observedEntitiesSet.add(observedGene);
			
			////////////////////////////////////////
			// add tissue & blood type lineage here
			///////////////////////////////////////
		}
		catch (Exception e) {
			log.info("Exception thrown processing observation data row, skipping row: " + e.getMessage());
			return new ObservationData(null, null, null);
		}

		// create observed evidence
		ObservedEvidence affyProbeSetEvidence =
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(AFFY_PROBESET_ID),
															   AFFY_PROBESET_ID, templateName, observation);
		observedEntitiesSet.add(affyProbeSetEvidence);
		if (affyProbeSetEvidence.getEvidence() != null) evidenceSet.add(affyProbeSetEvidence.getEvidence());

		ObservedEvidence log2FoldChangeEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(LOG2_FOLD_CHANGE),
																 LOG2_FOLD_CHANGE, templateName, observation);
		observedEntitiesSet.add(log2FoldChangeEvidence);
		if (log2FoldChangeEvidence.getEvidence() != null) evidenceSet.add(log2FoldChangeEvidence.getEvidence());

		ObservedEvidence avgExprEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(AVG_EXPR),
																 AVG_EXPR, templateName, observation);
		observedEntitiesSet.add(avgExprEvidence);
		if (avgExprEvidence.getEvidence() != null) evidenceSet.add(avgExprEvidence.getEvidence());

		ObservedEvidence zScoreEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(Z_SCORE),
																 Z_SCORE, templateName, observation);
		observedEntitiesSet.add(zScoreEvidence);
		if (zScoreEvidence.getEvidence() != null) evidenceSet.add(zScoreEvidence.getEvidence());

		ObservedEvidence pValueEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(P_VALUE),
																 P_VALUE, templateName, observation);
		observedEntitiesSet.add(pValueEvidence);
		if (pValueEvidence.getEvidence() != null) evidenceSet.add(pValueEvidence.getEvidence());

		ObservedEvidence adjPValueEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(ADJ_P_VALUE),
																 ADJ_P_VALUE, templateName, observation);
		observedEntitiesSet.add(adjPValueEvidence);
		if (adjPValueEvidence.getEvidence() != null) evidenceSet.add(adjPValueEvidence.getEvidence());

		ObservedEvidence funcTypeEvidence =
			observationDataFactory.createObservedLabelEvidence(fieldSet.readString(FUNC_TYPE),
															   FUNC_TYPE, templateName, observation);
		observedEntitiesSet.add(funcTypeEvidence);
		if (funcTypeEvidence.getEvidence() != null) evidenceSet.add(funcTypeEvidence.getEvidence());

		ObservedEvidence setSizeEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readInt(SET_SIZE),
																 SET_SIZE, templateName, observation);
		observedEntitiesSet.add(setSizeEvidence);
		if (setSizeEvidence.getEvidence() != null) evidenceSet.add(setSizeEvidence.getEvidence());

		ObservedEvidence gseaEnrichScoreEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(GSEA_ENRICH_SCORE),
																 GSEA_ENRICH_SCORE, templateName, observation);
		observedEntitiesSet.add(gseaEnrichScoreEvidence);
		if (gseaEnrichScoreEvidence.getEvidence() != null) evidenceSet.add(gseaEnrichScoreEvidence.getEvidence());

		ObservedEvidence gseaPValueEvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(GSEA_P_VALUE),
																 GSEA_P_VALUE, templateName, observation);
		observedEntitiesSet.add(gseaPValueEvidence);
		if (gseaPValueEvidence.getEvidence() != null) evidenceSet.add(gseaPValueEvidence.getEvidence());

		ObservedEvidence gseaFDREvidence = 
			observationDataFactory.createObservedNumericEvidence(fieldSet.readDouble(GSEA_FDR),
																 GSEA_FDR, templateName, observation);
		observedEntitiesSet.add(gseaFDREvidence);
		if (gseaFDREvidence.getEvidence() != null) evidenceSet.add(gseaFDREvidence.getEvidence());

		return new ObservationData(observation, observedEntitiesSet, evidenceSet);
	}
}
