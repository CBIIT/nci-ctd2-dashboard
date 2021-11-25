package gov.nih.nci.ctd2.dashboard.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import gov.nih.nci.ctd2.dashboard.model.TissueSample;

@Entity
@Table(name = "tissue_sample")
@Proxy(proxyClass = TissueSample.class)
@Indexed
public class TissueSampleImpl extends SubjectImpl implements TissueSample {
    private static final long serialVersionUID = 1L;

    private int code;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }
}
