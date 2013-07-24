package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Synonym;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = Synonym.class)
@Table(name = "synonym")
@Indexed
public class SynonymImpl extends DashboardEntityImpl implements Synonym {
}
