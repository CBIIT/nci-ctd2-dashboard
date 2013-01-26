package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Evidence;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = Evidence.class)
@Table(name = "evidence")
public class EvidenceImpl extends DashboardEntityImpl implements Evidence {
}
