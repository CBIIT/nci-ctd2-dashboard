package gov.nih.nci.ctd2.dashboard.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import gov.nih.nci.ctd2.dashboard.model.Organism;

@Entity
@Proxy(proxyClass = Organism.class)
@Table(name = "organism")
public class OrganismImpl extends DashboardEntityImpl implements Organism {
    private static final long serialVersionUID = -3857505527383982701L;

    public final static String FIELD_TAXID = "taxid";

    private String taxonomyId;

    @FullTextField(name=FIELD_TAXID, searchable = Searchable.YES)
    @Column(length = 32, nullable = false)
    public String getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(String taxonomyId) {
        this.taxonomyId = taxonomyId;
    }
}
