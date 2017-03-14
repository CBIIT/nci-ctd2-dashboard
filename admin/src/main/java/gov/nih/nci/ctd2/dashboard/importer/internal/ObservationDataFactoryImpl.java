package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.importer.ObservationDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Method;

public class ObservationDataFactoryImpl implements ObservationDataFactory {

	private static final Log log = LogFactory.getLog(ObservationDataFactoryImpl.class);

	private static final String DASHBOARD_SUBMISSION_URL = "/#/submission/";	
    private static final String DASHBOARD_OBSERVATION_URL = "/#/observation/";
    //private static final Pattern LINKBACK_URL_EVIDENCE_REGEX = Pattern.compile("tier._evidence");
    // submission_name:col_name_1=col_val_1&col_name_2=col_val_2
    private static final Pattern LINKBACK_URL_REGEX = Pattern.compile("([\\w\\-]+):([\\w\\-&=]+)");

    private class LinkbackURL {
        public String submissionName;
        public HashMap<String,String> columnValuePairs = new HashMap<String,String>();
    }

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	private DashboardDao dashboardDao;

	// cache for fast lookup and prevention of duplicate role records
    private HashMap<String, Subject> subjectCache = new HashMap<String, Subject>();
    private HashMap<String, ObservedSubjectRole> observedSubjectRoleCache = new HashMap<String, ObservedSubjectRole>();
    private HashMap<String, ObservedEvidenceRole> observedEvidenceRoleCache = new HashMap<String, ObservedEvidenceRole>();

	@Override
	public Submission createSubmission(String submissionName, Date submissionDate, String observationTemplateName) {
		Submission submission = dashboardFactory.create(Submission.class);
        submission.setDisplayName(submissionName);
		submission.setSubmissionDate(submissionDate);
		ObservationTemplate observationTemplate  = dashboardDao.findObservationTemplateByName(observationTemplateName);
		if (observationTemplate != null) {
			submission.setObservationTemplate(observationTemplate);
		}
		return submission;
	}

	private List<Subject> filterEntities(List<Subject> dashboardEntities, Class<? extends Subject> filterClass) {
		List<Subject> filteredList = new ArrayList<Subject>();
		for (Subject dashboardEntity : dashboardEntities) {
			if(filterClass.isInstance(dashboardEntity)) {
				filteredList.add(dashboardEntity);
			}
		}
		return filteredList;
	}

	@Override
	public ObservedSubject createObservedSubject(String subjectValue, String columnName, String templateName,
												 Observation observation, String daoFindQueryName) throws Exception {
		ObservedSubject observedSubject = dashboardFactory.create(ObservedSubject.class);
		observedSubject.setDisplayName(subjectValue);
		observedSubject.setObservation(observation);
		Subject subject = subjectCache.get(subjectValue);
		if (subject == null) {
			List<Subject> dashboardEntities = null;
			if (daoFindQueryName.equals("findSubjectsBySynonym")) {
				Method method = dashboardDao.getClass().getMethod(daoFindQueryName, String.class, Boolean.TYPE);
				dashboardEntities = (List<Subject>)method.invoke(dashboardDao, subjectValue, true);
			}
			else if (daoFindQueryName.startsWith("findSubjectsByXref")) {
				String[] parts = daoFindQueryName.split(ObservationDataFieldSetMapper.XREF_DELIMITER);
				Method method = dashboardDao.getClass().getMethod(parts[0], String.class, String.class);
				dashboardEntities = (List<Subject>)method.invoke(dashboardDao, parts[1], subjectValue);
			}
			else {
				Method method = dashboardDao.getClass().getMethod(daoFindQueryName, String.class);
				dashboardEntities = (List<Subject>)method.invoke(dashboardDao, subjectValue);
				// if we've searched for gene by symbol and come up empty, try by synonym
				if (dashboardEntities.isEmpty() && daoFindQueryName.equals("findGenesBySymbol")) {
					method = dashboardDao.getClass().getMethod("findSubjectsBySynonym", String.class, Boolean.TYPE);
					dashboardEntities = filterEntities((List<Subject>) method.invoke(dashboardDao, subjectValue, true), Gene.class);
				}
				// if we've searched for gene by synonym and come up empty, try entrez id
				if (dashboardEntities.isEmpty() && daoFindQueryName.equals("findGenesBySymbol")) {
					method = dashboardDao.getClass().getMethod("findGenesByEntrezId", String.class);
					dashboardEntities = filterEntities((List<Subject>)method.invoke(dashboardDao, subjectValue), Gene.class);
				}
				// if we've searched for tissue sample by name and come up empty, try by synonym
				if (dashboardEntities.isEmpty() && daoFindQueryName.equals("findTissueSampleByName")) {
					method = dashboardDao.getClass().getMethod("findSubjectsBySynonym", String.class, Boolean.TYPE);
					dashboardEntities = filterEntities((List<Subject>)method.invoke(dashboardDao, subjectValue, true), TissueSample.class);
				}
				// if we've searched for drug/compound by name and come up empty, try by synonym
				if (dashboardEntities.isEmpty() && daoFindQueryName.equals("findCompoundsByName")) {
					method = dashboardDao.getClass().getMethod("findSubjectsBySynonym", String.class, Boolean.TYPE);
					dashboardEntities = filterEntities((List<Subject>)method.invoke(dashboardDao, subjectValue, true), Compound.class);
				}
				// if we've searched for drug/compound by name and come up empty, try by synonym
				if (dashboardEntities.isEmpty() && daoFindQueryName.equals("findCellLineByName")) {
					method = dashboardDao.getClass().getMethod("findSubjectsBySynonym", String.class, Boolean.TYPE);
					dashboardEntities = filterEntities((List<Subject>)method.invoke(dashboardDao, subjectValue, true), CellSample.class);
				}
			}
			if (dashboardEntities.size() > 0) {
				for (Subject returnedSubject : dashboardEntities) {
					if (returnedSubject.getDisplayName().equals(subjectValue)) {
						subject = returnedSubject;
						break;
					}
				}
				subject = (subject == null) ? dashboardEntities.iterator().next() : subject;
				subjectCache.put(subjectValue, subject);
			}
		}
		if (subject != null) observedSubject.setSubject(subject);
		String observedSubjectRoleCacheKey = templateName+columnName;
		ObservedSubjectRole observedSubjectRole = observedSubjectRoleCache.get(observedSubjectRoleCacheKey);
		if (observedSubjectRole == null) {
			observedSubjectRole = dashboardDao.findObservedSubjectRole(templateName, columnName);
			if (observedSubjectRole != null) {
				observedSubjectRoleCache.put(observedSubjectRoleCacheKey, observedSubjectRole);
			}
		}
		if (observedSubjectRole != null) observedSubject.setObservedSubjectRole(observedSubjectRole);
		return observedSubject;
	}

	@Override
	public ObservedEvidence createObservedLabelEvidence(String evidenceValue, String columnName,
														String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		Evidence evidence = dashboardFactory.create(LabelEvidence.class);
		evidence.setDisplayName(evidenceValue);
		observedEvidence.setEvidence(evidence);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		return observedEvidence;
	}

	@Override
	public ObservedEvidence createObservedNumericEvidence(Number evidenceValue, String columnName,
														  String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(String.valueOf(evidenceValue));
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(DataNumericValue.class);
        evidence.setDisplayName(String.valueOf(evidenceValue));
		((DataNumericValue)evidence).setNumericValue(evidenceValue);
		if (observedEvidenceRole != null && observedEvidenceRole.getAttribute().length() > 0) {
			((DataNumericValue)evidence).setUnit(observedEvidenceRole.getAttribute());
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}

	@Override
	public ObservedEvidence createObservedFileEvidence(String evidenceValue, String columnName,
													   String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(FileEvidence.class);
        evidence.setDisplayName(String.valueOf(evidenceValue));
		File file = new File(evidenceValue);

		((FileEvidence)evidence).setFileName(file.getName());
		((FileEvidence)evidence).setFilePath(file.getPath());
		if (observedEvidenceRole != null && observedEvidenceRole.getAttribute().length() > 0) {
			((FileEvidence)evidence).setMimeType(observedEvidenceRole.getAttribute());
		}
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}


	@Override
	public ObservedEvidence createObservedUrlEvidence(String evidenceValue, String columnName,
													  String templateName, Observation observation) {
		ObservedEvidence observedEvidence = dashboardFactory.create(ObservedEvidence.class);
		observedEvidence.setDisplayName(evidenceValue);
		observedEvidence.setObservation(observation);
		ObservedEvidenceRole observedEvidenceRole = getObservedEvidenceRole(templateName, columnName);
		if (observedEvidenceRole != null) observedEvidence.setObservedEvidenceRole(observedEvidenceRole);
		Evidence evidence = dashboardFactory.create(UrlEvidence.class);
        evidence.setDisplayName(String.valueOf(evidenceValue));
		((UrlEvidence)evidence).setUrl(getEvidenceURL(columnName, evidenceValue));
		observedEvidence.setEvidence(evidence);
		return observedEvidence;
	}

	private ObservedEvidenceRole getObservedEvidenceRole(String templateName, String columnName) {
		String observedEvidenceRoleCacheKey = templateName+columnName;
		ObservedEvidenceRole observedEvidenceRole = observedEvidenceRoleCache.get(observedEvidenceRoleCacheKey);
		if (observedEvidenceRole == null) {
			observedEvidenceRole = dashboardDao.findObservedEvidenceRole(templateName, columnName);
			if (observedEvidenceRole != null) {
				observedEvidenceRoleCache.put(observedEvidenceRoleCacheKey, observedEvidenceRole);
			}
		}
		return observedEvidenceRole;
	}

    private String getEvidenceURL(String columnName, String evidenceValue) {

        String evidenceURL = null;
        
        //Matcher linkbackURLEvidenceMatcher = LINKBACK_URL_EVIDENCE_REGEX.matcher(columnName);
        // is this a linkback
        //if (linkbackURLEvidenceMatcher.find()) {
        Matcher linkbackURLMatcher = LINKBACK_URL_REGEX.matcher(evidenceValue);
	    // is this a linkback to an observation
        if (linkbackToObservation(linkbackURLMatcher)) {
            LinkbackURL linkbackURL = getLinkbackURL(linkbackURLMatcher);
            Submission submission = dashboardDao.findSubmissionByName(linkbackURL.submissionName);
            evidenceURL = getLinkbackURL(linkbackURL.columnValuePairs,
                                         getObservations(submission));
        }
	    // is this a linkback to submission
    	else if (linkbackToSubmission(evidenceValue)) {
	    	Submission submission = dashboardDao.findSubmissionByName(evidenceValue);
	        evidenceURL = DASHBOARD_SUBMISSION_URL + submission.getId();
	    }
        //}
        
        return (evidenceURL == null) ? evidenceValue : evidenceURL;
    }

    private LinkbackURL getLinkbackURL(Matcher linkbackURLMatcher) {

        LinkbackURL linkbackURL = new LinkbackURL();
        linkbackURL.submissionName = linkbackURLMatcher.group(1);

        // each linkback component is of the form &column_name=column_value, e.g. compound=navitoclax
        String[] linkbackComponents = linkbackURLMatcher.group(2).contains("&") ?
            linkbackURLMatcher.group(2).split("&") : new String[]{linkbackURLMatcher.group(2)};

        for (String linkbackComponent : linkbackComponents) {
            String[] columnValuePair = linkbackComponent.split("=");
            if (columnValuePair.length == 2) {
                linkbackURL.columnValuePairs.put(columnValuePair[0], columnValuePair[1]);
            }
        }

        return linkbackURL;
    }

    private boolean linkbackToObservation(Matcher linkbackURLMatcher)
    {
		return (linkbackURLMatcher.find() && linkbackURLMatcher.groupCount() == 2);
    }

    private boolean linkbackToSubmission(String evidenceValue)
    {
    	Submission submission = dashboardDao.findSubmissionByName(evidenceValue);
    	return (submission != null);
    }

    private List<Observation> getObservations(Submission submission) {
        return (submission != null) ?
            dashboardDao.findObservationsBySubmission(submission) : new ArrayList<Observation>();
    }

    private String getLinkbackURL(HashMap<String,String> columnValuePairs, List<Observation> observations) {

        String url = null;
        for (Observation observation : observations) {
            int match = 0;
            for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectByObservation(observation)) {
                //Subject subject = observedSubject.getSubject();
                ObservedSubjectRole observedSubjectRole = observedSubject.getObservedSubjectRole();
                if (columnValuePairs.containsKey(observedSubjectRole.getColumnName()) &&
                    columnValuePairs.get(observedSubjectRole.getColumnName()).equals(observedSubject.getDisplayName())) {
                    match += 1;
                }
            }
            if (match == columnValuePairs.size()) {
                url = DASHBOARD_OBSERVATION_URL + observation.getId();
                break;
            }
        }

        return url;
    }
}
