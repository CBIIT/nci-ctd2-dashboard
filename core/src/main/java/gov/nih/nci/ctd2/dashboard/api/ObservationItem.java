package gov.nih.nci.ctd2.dashboard.api;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.Submission;

@Entity
@Table(name = "observation_item", indexes = @Index(name = "submission_id", columnList = "submission_id", unique = false))
public class ObservationItem {
    private Integer submission_id;

    @Column(length = 1024)
    public String observation_summary;
    @Column(length = 102400)
    public SubjectItem[] subject_list;
    @Column(length = 102400)
    public EvidenceItem[] evidence_list;

    @Id
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(final Integer sid) {
        this.submission_id = sid;
    }

    public ObservationItem() {
    }

    public ObservationItem(final Observation observation, final DashboardDao dashboardDao) {
        final List<SubjectItem> subjects = dashboardDao.getObservedSubjectInfo(observation.getId());
        this.subject_list = subjects.toArray(new SubjectItem[0]);
        final List<EvidenceItem> evidences = dashboardDao.getObservedEvidenceInfo(observation.getId());

        final Submission submission = observation.getSubmission();
        this.observation_summary = replaceValues(submission.getObservationTemplate().getObservationSummary(), subjects,
                evidences);
        this.evidence_list = evidences.toArray(new EvidenceItem[0]);
    }

    private static String replaceValues(String summary, final List<SubjectItem> subjects,
            final List<EvidenceItem> evidences) {
        for (final SubjectItem s : subjects) {
            summary = summary.replace("<" + s.getColumnName() + ">", s.getName());
        }
        for (final EvidenceItem e : evidences) {
            summary = summary.replace("<" + e.getColumnName() + ">", e.getEvidenceName());
        }
        return summary;
    }
}
