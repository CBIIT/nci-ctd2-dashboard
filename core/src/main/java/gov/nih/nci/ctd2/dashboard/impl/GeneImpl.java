package gov.nih.nci.ctd2.dashboard.impl;

import com.sun.istack.internal.NotNull;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Proxy(proxyClass= Gene.class)
public class GeneImpl extends SubjectImpl implements Gene {
    private String entrezGeneId;

    @NotNull
    @Column(length = 32)
    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }
}
