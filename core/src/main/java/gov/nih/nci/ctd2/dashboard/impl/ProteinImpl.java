package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Proxy;
import javax.persistence.*;

import java.util.Set;
import java.util.HashSet;

@Entity
@Proxy(proxyClass = Protein.class)
@Table(name = "protein")
public class ProteinImpl extends SubjectWithOrganismImpl implements Protein {
    private String uniprotId;
    private Set<Transcript> transcripts = new HashSet<Transcript>();

    @Column(length = 64, nullable = false, unique = true)
    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    @ManyToMany(targetEntity = TranscriptImpl.class)
    @JoinTable(name = "protein_transcript_map")
    public Set<Transcript> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(Set<Transcript> transcripts) {
        this.transcripts = transcripts;
    }
}
