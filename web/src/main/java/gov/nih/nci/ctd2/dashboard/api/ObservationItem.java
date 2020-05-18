package gov.nih.nci.ctd2.dashboard.api;

import java.util.List;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.Submission;

public class ObservationItem {
    public final String observation_summary;
    public final SubjectItem[] subject_list;
    public final EvidenceItem[] evidence_list;

    public ObservationItem(Submission submission, SubjectItem[] subject_list, EvidenceItem[] evidence_list) {
        this.observation_summary = submission.getObservationTemplate().getObservationSummary();
        this.subject_list = subject_list;
        this.evidence_list = evidence_list;
    }

    public ObservationItem(final Observation observation, final DashboardDao dashboardDao) {
        List<SubjectItem> subjects = dashboardDao.getObservedSubjectInfo(observation.getId());
        this.subject_list = subjects.toArray(new SubjectItem[0]);
        List<EvidenceItem> evidences = dashboardDao.getObservedEvidenceInfo(observation.getId()); // TESTING

        Submission submission = observation.getSubmission();
        this.observation_summary = replaceValues(submission.getObservationTemplate().getObservationSummary(), subjects,
                evidences);
        this.evidence_list = evidences.toArray(new EvidenceItem[0]);
    }

    private static String replaceValues(String summary, List<SubjectItem> subjects, List<EvidenceItem> evidences) {
        for (SubjectItem s : subjects) {
            summary = summary.replace("<" + s.columnName + ">", s.name);
        }
        for (EvidenceItem e : evidences) {
            summary = summary.replace("<" + e.getColumnName() + ">", e.getEvidenceName());
        }
        return summary;
    }
}
