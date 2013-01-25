package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ValueType;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = ValueType.class)
public class ValueTypeImpl extends DashboardEntityImpl implements ValueType {
    private String unit;

    @Column(length = 32)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
