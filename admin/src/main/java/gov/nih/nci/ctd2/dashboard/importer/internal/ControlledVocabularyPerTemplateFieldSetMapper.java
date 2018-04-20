package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

@Component("controlledVocabularyPerTemplateMapper")
public class ControlledVocabularyPerTemplateFieldSetMapper implements FieldSetMapper<ObservationTemplate> {

	private static final String TEMPLATE_TIER = "observation_tier";
	private static final String TEMPLATE_NAME = "template_name";
	private static final String OBSERVATION_SUMMARY = "observation_summary";
	private static final String TEMPLATE_DESCRIPTION = "template_description";
	private static final String SUBMISSION_NAME = "submission_name";
	private static final String SUBMISSION_DESCRIPTION = "submission_description";
    private static final String PROJECT = "project";
	private static final String SUBMISSION_STORY = "submission_story";
	private static final String SUBMISSION_STORY_RANK = "submission_story_rank";
	private static final String PRINCIPAL_INVESTIGATOR = "principal_investigator";
	private static final String SUBMISSION_CENTER = "submission_center";

	@Autowired
	private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("observationTemplateMap")
	private HashMap<String,ObservationTemplate> observationTemplateMap;

	private HashMap<String, SubmissionCenter> submissionCenterCache = new HashMap<String, SubmissionCenter>();

	public ObservationTemplate mapFieldSet(FieldSet fieldSet) throws BindException {

		ObservationTemplate observationTemplate = dashboardFactory.create(ObservationTemplate.class);
		observationTemplate.setTier(fieldSet.readInt(TEMPLATE_TIER));
		observationTemplate.setDisplayName(fieldSet.readString(TEMPLATE_NAME));
		observationTemplate.setObservationSummary(fieldSet.readString(OBSERVATION_SUMMARY));
		observationTemplate.setDescription(fieldSet.readString(TEMPLATE_DESCRIPTION));
		observationTemplate.setSubmissionName(fieldSet.readString(SUBMISSION_NAME));
		observationTemplate.setSubmissionDescription(fieldSet.readString(SUBMISSION_DESCRIPTION));
        observationTemplate.setProject(fieldSet.readString(PROJECT));
		observationTemplate.setIsSubmissionStory(fieldSet.readBoolean(SUBMISSION_STORY, "TRUE"));
		observationTemplate.setSubmissionStoryRank(fieldSet.readInt(SUBMISSION_STORY_RANK));
		observationTemplate.setPrincipalInvestigator(fieldSet.readString(PRINCIPAL_INVESTIGATOR));

		String submissionCenterName = fieldSet.readString(SUBMISSION_CENTER);
		SubmissionCenter submissionCenter = submissionCenterCache.get(submissionCenterName);
		if (submissionCenter == null) {
			submissionCenter = dashboardDao.findSubmissionCenterByName(submissionCenterName);
			if (submissionCenter == null) {
				submissionCenter = dashboardFactory.create(SubmissionCenter.class);
				submissionCenter.setDisplayName(submissionCenterName);
				String shortCenterNames = shortCenterNameMap.get(submissionCenterName);
				if(shortCenterNames==null) shortCenterNames = "";
				submissionCenter.setStableURL("center/"+shortCenterNames.toLowerCase());
			}
			submissionCenterCache.put(submissionCenterName, submissionCenter);
		}
		observationTemplate.setSubmissionCenter(submissionCenter);

		observationTemplateMap.put(fieldSet.readString(TEMPLATE_NAME), observationTemplate);

		return observationTemplate;
	}

	final private static Map<String, String> shortCenterNameMap = new HashMap<String, String>();
	static {
		shortCenterNameMap.put("Broad Institute", "Broad");
		shortCenterNameMap.put("Cold Spring Harbor Laboratory", "CSHL");
		shortCenterNameMap.put("Columbia University", "Columbia");
		shortCenterNameMap.put("Dana-Farber Cancer Institute", "DFCI");
		shortCenterNameMap.put("Emory University", "Emory");
		shortCenterNameMap.put("Fred Hutchinson Cancer Research Center (1)", "FHCRC1");
		shortCenterNameMap.put("Fred Hutchinson Cancer Research Center (2)", "FHCRC2");
		shortCenterNameMap.put("Johns Hopkins University", "JHU");
		shortCenterNameMap.put("Oregon Health and Science University", "OHSU");
		shortCenterNameMap.put("Stanford University", "Stanford");
		shortCenterNameMap.put("Translational Genomics Research Institute", "TGen");
		shortCenterNameMap.put("University of California San Diego", "UCSD");
		shortCenterNameMap.put("University of California San Francisco (1)", "UCSF1");
		shortCenterNameMap.put("University of California San Francisco (2)", "UCSF2");
		shortCenterNameMap.put("University of Texas MD Anderson Cancer Center", "UTMDACC");
		shortCenterNameMap.put("University of Texas Southwestern Medical Center", "UTSW");
	}
}
