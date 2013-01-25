package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ShRna;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = ShRna.class)
public class ShRnaImpl extends SubjectImpl implements ShRna {
    private String targetSequence;
    private Transcript transcript;

    @Column(length = 2048, nullable = false)
    public String getTargetSequence() {
        return targetSequence;
    }

    public void setTargetSequence(String targetSequence) {
        this.targetSequence = targetSequence;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }
}
