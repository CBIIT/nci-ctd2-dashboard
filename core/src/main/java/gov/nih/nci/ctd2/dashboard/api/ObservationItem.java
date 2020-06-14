package gov.nih.nci.ctd2.dashboard.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

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
}