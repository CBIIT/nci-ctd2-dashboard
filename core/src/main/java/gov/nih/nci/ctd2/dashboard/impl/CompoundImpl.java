package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = Compound.class)
@Table(name = "compound")
public class CompoundImpl extends SubjectImpl implements Compound {
    private String smilesNotation;

    @Column(length = 2048, nullable = true)
    public String getSmilesNotation() {
        return smilesNotation;
    }

    public void setSmilesNotation(String smilesNotation) {
        this.smilesNotation = smilesNotation;
    }
}
