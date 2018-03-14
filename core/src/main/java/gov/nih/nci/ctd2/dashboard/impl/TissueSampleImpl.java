package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.TissueSample;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tissue_sample")
@Proxy(proxyClass = TissueSample.class)
@Indexed
public class TissueSampleImpl extends SubjectImpl implements TissueSample {
    private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(TissueSampleImpl.class);

	public final static String FIELD_LINEAGE = "lineage";

    private String lineage;

    @Field(name=FIELD_LINEAGE, index = Index.YES)
    @Column(length = 128, nullable = true)
    public String getLineage() {
        return lineage;
    }

    public void setLineage(String lineage) {
        this.lineage = lineage;
    }
}
