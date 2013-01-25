package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

@Entity
@Proxy(proxyClass = Transcript.class)
public class TranscriptImpl extends SubjectImpl implements Transcript {
    private String refseqId;
    private Gene gene;

    @Column(length = 64, nullable = false)
    public String getRefseqId() {
        return refseqId;
    }

    public void setRefseqId(String refseqId) {
        this.refseqId = refseqId;
    }

    @Column(nullable = false)
    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}
