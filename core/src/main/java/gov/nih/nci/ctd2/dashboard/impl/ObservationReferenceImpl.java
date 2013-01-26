package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationReference;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = ObservationReference.class)
@Table(name = "observation_reference")
public class ObservationReferenceImpl extends DashboardEntityImpl implements ObservationReference {
}
