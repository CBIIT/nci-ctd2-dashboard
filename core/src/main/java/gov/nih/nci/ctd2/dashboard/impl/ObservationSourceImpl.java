package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationSource;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = ObservationSource.class)
public class ObservationSourceImpl extends DashboardEntityImpl implements ObservationSource {
}
