package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Synonym;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = Synonym.class)
@Table(name = "synonym")
@Indexed
public class SynonymImpl extends DashboardEntityImpl implements Synonym {

    private static final long serialVersionUID = -4843391059437576165L;
}
