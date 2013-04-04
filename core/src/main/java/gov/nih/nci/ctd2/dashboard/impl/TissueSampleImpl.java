package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.TissueSample;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tissue_sample")
@Proxy(proxyClass = TissueSample.class)
public class TissueSampleImpl extends SubjectImpl implements TissueSample {
	private String lineage;

    @Column(length = 128, nullable = true)
    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }
}
