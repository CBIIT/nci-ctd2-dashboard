package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass= CellSample.class)
@Table(name = "cell_sample")
public class CellSampleImpl extends SubjectWithOrganismImpl implements CellSample {
	private String lineage;

    @Column(length = 128, nullable = true)
    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }
}
