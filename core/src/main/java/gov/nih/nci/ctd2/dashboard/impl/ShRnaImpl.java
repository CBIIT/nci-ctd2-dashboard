package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ShRna;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = ShRna.class)
@Table(name = "shrna")
@Indexed
public class ShRnaImpl extends SubjectWithOrganismImpl implements ShRna {
    private String targetSequence;
    private Transcript transcript;
    private String type;
    private String reagentName;

    @Column(length = 2048, nullable = false)
    public String getTargetSequence() {
        return targetSequence;
    }

    public void setTargetSequence(String targetSequence) {
        this.targetSequence = targetSequence;
    }

    @ManyToOne(targetEntity = TranscriptImpl.class)
    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }

    @Column(length = 5, nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(length = 255, nullable = false)
    @Index(name = "reagent_idx")
    public String getReagentName() {
        return reagentName;
    }

    public void setReagentName(String reagentName) {
        this.reagentName = reagentName;
    }
}
