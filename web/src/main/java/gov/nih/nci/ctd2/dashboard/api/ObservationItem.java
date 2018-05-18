package gov.nih.nci.ctd2.dashboard.api;

import java.util.List;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidence;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;

public class ObservationItem {
    public final String submission_id, observation_summary;
    public final SubjectItem[] subject_list;
    public final EvidenceItem[] evidence_list;

    public ObservationItem(Submission submission, SubjectItem[] subject_list, EvidenceItem[] evidence_list) {
        this.submission_id = submission.getId().toString();
        this.observation_summary = submission.getObservationTemplate().getObservationSummary();
        this.subject_list = subject_list;
        this.evidence_list = evidence_list;
    }

    public ObservationItem(final Observation observation, final DashboardDao dashboardDao) {
        List<ObservedSubject> subjects = dashboardDao.findObservedSubjectByObservation(observation);
        List<ObservedEvidence> evidences = dashboardDao.findObservedEvidenceByObservation(observation);
        SubjectItem[] subjs = new SubjectItem[subjects.size()];
        for (int j = 0; j < subjects.size(); j++) {
            ObservedSubject observedSubject = subjects.get(j);
            Synonym[] synonyms = observedSubject.getSubject().getSynonyms().toArray(new Synonym[0]);
            String[] synms = new String[synonyms.length];
            for (int k = 0; k < synonyms.length; k++) {
                synms[k] = synonyms[k].getDisplayName();
            }
            Xref[] xrefs = observedSubject.getSubject().getXrefs().toArray(new Xref[0]);
            XRefItem[] apiXrefs = new XRefItem[xrefs.length];
            for (int k = 0; k < xrefs.length; k++) {
                apiXrefs[k] = new XRefItem(xrefs[k].getDatabaseName(), xrefs[k].getDatabaseId());
            }
            subjs[j] = new SubjectItem(observedSubject, synms, apiXrefs);
        }
        EvidenceItem[] evds = new EvidenceItem[evidences.size()];
        for (int j = 0; j < evidences.size(); j++) {
            ObservedEvidence observedEvidence = evidences.get(j);
            evds[j] = new EvidenceItem(observedEvidence);
        }

        Submission submission = observation.getSubmission();
        this.submission_id = submission.getStableURL().substring("submission/".length());
        this.observation_summary = submission.getObservationTemplate().getObservationSummary();
        this.subject_list = subjs;
        this.evidence_list = evds;
    }
}
