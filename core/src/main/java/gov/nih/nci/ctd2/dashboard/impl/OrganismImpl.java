package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Proxy(proxyClass = Organism.class)
@Table(name = "organism")
public class OrganismImpl extends SubjectImpl implements Organism {
    private String taxonomyId;
    private Gene gene;

    @Column(length = 32, nullable = false)
    public String getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(String taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    @ManyToOne(targetEntity = GeneImpl.class)
    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}
