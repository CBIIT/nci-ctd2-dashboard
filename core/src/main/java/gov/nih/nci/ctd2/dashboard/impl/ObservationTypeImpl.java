package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationType;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = ObservationType.class)
@Table(name = "observation_type")
public class ObservationTypeImpl extends DashboardEntityImpl implements ObservationType {
}
