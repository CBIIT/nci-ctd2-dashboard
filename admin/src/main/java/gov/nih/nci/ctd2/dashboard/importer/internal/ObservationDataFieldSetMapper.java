package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.importer.ObservationDataFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

@Component("observationDataMapper")
public class ObservationDataFieldSetMapper implements FieldSetMapper<ObservationData> {

	private static final Log log = LogFactory.getLog(ObservationDataFieldSetMapper.class);

	private static final String MAP_DELIMITER = ":";
	private static final int ROLE_TYPE_INDEX = 0;
	private static final int SUBJECT_QUERY_METHOD_INDEX = 1;
	private static final int FIELDSET_METHOD_INDEX = 1;
	private static final int OBSERVATION_DATA_FACTORY_METHOD_INDEX = 2;

	private static final String SUBMISSION_NAME = "submission_name";
	private static final String	SUBMISSION_DATE = "submission_date";
	private static final String TEMPLATE_NAME = "template_name";

	public static final String XREF_DELIMITER = "-";
	public static final SimpleDateFormat TEMPLATE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	@Autowired
	private ObservationDataFactory observationDataFactory;

    @Autowired
    private DashboardFactory dashboardFactory;

	@Autowired
	@Qualifier("observationTemplateMap")
	private	HashMap<String, String> observationTemplateMap;

	private HashMap<String, Submission> submissionCache = new HashMap<String, Submission>();

	/* Used by ObservationDataWriter */
	public static String getSubmissionCacheKey(Submission submission) {
		return submission.getDisplayName() + 
			ObservationDataFieldSetMapper.TEMPLATE_DATE_FORMAT.format(submission.getSubmissionDate()) +
			submission.getObservationTemplate().getDisplayName();
	}

	public ObservationData mapFieldSet(FieldSet fieldSet) throws BindException {

		String templateName = fieldSet.readString(TEMPLATE_NAME);
		Date submissionDate = fieldSet.readDate(SUBMISSION_DATE, "yyyy.MM.dd");
		String submissionName = fieldSet.readString(SUBMISSION_NAME);

		// create submission - if cache key changes, update getSubmissionCacheKey() defined above
		String submissionCacheKey = submissionName + TEMPLATE_DATE_FORMAT.format(submissionDate) + templateName;
		Submission submission = submissionCache.get(submissionCacheKey);
		if (submission == null) {
			submission = observationDataFactory.createSubmission(submissionName,
																 submissionDate,
																 templateName);
			submissionCache.put(submissionCacheKey, submission);
		}
		// create observation
		Observation observation = dashboardFactory.create(Observation.class);
		observation.setSubmission(submission);

		// these will contain all observed entities / evidence we will persist
		LinkedHashSet<DashboardEntity> evidenceSet = new LinkedHashSet<DashboardEntity>();
		LinkedHashSet<DashboardEntity> observedEntitiesSet = new LinkedHashSet<DashboardEntity>();

		for (String columnName : fieldSet.getNames()) {
			if (columnName.equals(SUBMISSION_DATE) ||
				columnName.equals(TEMPLATE_NAME)) continue;
			try {
				String mapKey = templateName + MAP_DELIMITER + columnName;
				if (!observationTemplateMap.containsKey(mapKey)) continue;
				String[] mapValues = observationTemplateMap.get(mapKey).split(MAP_DELIMITER);
				if (mapValues[ROLE_TYPE_INDEX].equals("subject")) {
					String subjectValue = fieldSet.readString(columnName);
					if (subjectValue.length() == 0) continue; // motivated by gene symbol vs gene id column usage
					ObservedSubject observedSubject = 
						observationDataFactory.createObservedSubject(subjectValue, columnName, templateName,
																	 observation, mapValues[SUBJECT_QUERY_METHOD_INDEX]);
					if (observedSubject.getSubject() == null) {
						// cannot find underlying subject behind
						// the observed subject, log and bail
						log.info("Cannot find subject: " + templateName + ", " + columnName + ", " + subjectValue);
						return new ObservationData(null, null, null);
					}
					observedEntitiesSet.add(observedSubject);
				}
				else {
					Method observationDataFactoryMethod =
						observationDataFactory.getClass().getMethod(mapValues[OBSERVATION_DATA_FACTORY_METHOD_INDEX],
																	mapValues[OBSERVATION_DATA_FACTORY_METHOD_INDEX]
																	.contains("Numeric") ? Number.class : String.class,
																	String.class, String.class, Observation.class);
					Method fieldSetMethod =
						fieldSet.getClass().getMethod(mapValues[FIELDSET_METHOD_INDEX], String.class);
					// create observed evidence
					ObservedEvidence observedEvidence =
						(ObservedEvidence)observationDataFactoryMethod.invoke(observationDataFactory,
																			  fieldSetMethod.invoke(fieldSet, columnName),
																			  columnName, templateName, observation);
					if (observedEvidence.getObservedEvidenceRole() == null) {
						log.info("Cannot find observed evidence role via column: " + templateName + ", " + columnName);
						return new ObservationData(null, null, null);
					}
					observedEntitiesSet.add(observedEvidence);
					if (observedEvidence.getEvidence() != null) evidenceSet.add(observedEvidence.getEvidence());
				}
			}
			catch (Exception e) {
				log.info("Exception thrown processing observation data row, skipping row: " + e.getMessage());
				return new ObservationData(null, null, null);
			}
		}
		return new ObservationData(observation, observedEntitiesSet, evidenceSet);
	}
}
