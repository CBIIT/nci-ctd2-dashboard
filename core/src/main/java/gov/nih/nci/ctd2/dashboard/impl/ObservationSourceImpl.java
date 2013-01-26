package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationSource;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = ObservationSource.class)
@Table(name = "observation_source")
public class ObservationSourceImpl extends DashboardEntityImpl implements ObservationSource {
}
