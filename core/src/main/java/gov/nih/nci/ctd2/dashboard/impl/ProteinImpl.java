package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = Protein.class)
@Table(name = "protein")
public class ProteinImpl extends SubjectImpl implements Protein {
    private String uniprotId;
    private Transcript transcript;

    @Column(length = 64, nullable = false)
    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    @ManyToOne(targetEntity = TranscriptImpl.class)
    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }
}
