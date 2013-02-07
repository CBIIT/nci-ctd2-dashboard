package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Proxy(proxyClass = Transcript.class)
@Table(name = "transcript")
public class TranscriptImpl extends SubjectWithOrganismImpl implements Transcript {
    private String refseqId;
    private Gene gene;

    @Column(length = 64, nullable = false, unique = true)
    public String getRefseqId() {
        return refseqId;
    }

    public void setRefseqId(String refseqId) {
        this.refseqId = refseqId;
    }

    @ManyToOne(targetEntity = GeneImpl.class)
    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}
