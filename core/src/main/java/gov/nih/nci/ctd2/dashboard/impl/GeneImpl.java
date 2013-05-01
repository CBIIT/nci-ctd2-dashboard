package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass= Gene.class)
@Table(name = "gene")
@org.hibernate.annotations.Table(
        appliesTo = "gene",
        indexes = { @Index(name = "geneHgncIdx", columnNames = { "hgncId" })
        })
@Indexed
public class GeneImpl extends SubjectWithOrganismImpl implements Gene {
    public final static String FIELD_ENTREZID = "entrezid";
    public final static String FIELD_HGNCID = "hgncid";

    private String entrezGeneId;
	private String hgncId;

    @Field(name=FIELD_ENTREZID, index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Column(length = 32, nullable = false, unique = true)
    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    @Field(name=FIELD_HGNCID, index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Column(length = 32, nullable = true)
    public String getHGNCId() {
        return hgncId;
    }

    public void setHGNCId(String hgncId) {
        this.hgncId = hgncId;
    }
}
