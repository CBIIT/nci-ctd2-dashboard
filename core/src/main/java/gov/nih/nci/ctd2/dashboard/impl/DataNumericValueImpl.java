package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import gov.nih.nci.ctd2.dashboard.model.ValueType;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = DataNumericValue.class)
public class DataNumericValueImpl extends EvidenceImpl implements DataNumericValue {
    private Number numericValue;
    private ValueType valueType;

    @Column(nullable = false)
    public Number getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Number numericValue) {
        this.numericValue = numericValue;
    }

    @Column(nullable = false)
    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}
