package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationReference;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = ObservationReference.class)
public class ObservationReferenceImpl extends DashboardEntityImpl implements ObservationReference {
}
