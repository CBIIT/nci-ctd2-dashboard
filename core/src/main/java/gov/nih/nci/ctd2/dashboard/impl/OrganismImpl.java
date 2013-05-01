package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;

@Entity
@Proxy(proxyClass = Organism.class)
@Table(name = "organism")
public class OrganismImpl extends DashboardEntityImpl implements Organism {
    public final static String FIELD_TAXID = "taxid";

    private String taxonomyId;

    @Field(name=FIELD_TAXID, index = Index.TOKENIZED)
    @Column(length = 32, nullable = false)
    public String getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(String taxonomyId) {
        this.taxonomyId = taxonomyId;
    }
}
