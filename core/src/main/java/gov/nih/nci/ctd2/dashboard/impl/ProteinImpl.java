package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = Protein.class)
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

    @Column(nullable = false)
    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }
}
