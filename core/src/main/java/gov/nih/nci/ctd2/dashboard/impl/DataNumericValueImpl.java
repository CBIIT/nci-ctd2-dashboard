package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = DataNumericValue.class)
@Table(name = "data_numeric_value")
public class DataNumericValueImpl extends EvidenceImpl implements DataNumericValue {
    private Number numericValue;
    private String unit;

    @Column(nullable = false)
    public Number getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Number numericValue) {
        this.numericValue = numericValue;
    }

    @Column(length = 32)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
