package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DataNumericValue;
import gov.nih.nci.ctd2.dashboard.model.ValueType;

public class DataNumericValueImpl extends EvidenceImpl implements DataNumericValue {
    private Number numericValue;
    private ValueType valueType;

    public Number getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Number numericValue) {
        this.numericValue = numericValue;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}
