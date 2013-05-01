package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass= CellSample.class)
@Table(name = "cell_sample")
@Indexed
public class CellSampleImpl extends SubjectWithOrganismImpl implements CellSample {
    public final static String FIELD_LINEAGE = "lineage";

    private String lineage;

    @Field(name=FIELD_LINEAGE, index = Index.TOKENIZED)
    @Column(length = 128, nullable = true)
    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }
}
