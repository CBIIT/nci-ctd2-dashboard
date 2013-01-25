package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationType;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = ObservationType.class)
public class ObservationTypeImpl extends DashboardEntityImpl implements ObservationType {
}
