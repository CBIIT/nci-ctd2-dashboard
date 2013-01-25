package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Synonym;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = Synonym.class)
public class SynonymImpl extends DashboardEntityImpl implements Synonym {
}
