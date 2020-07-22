package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.GeneType;
import org.hibernate.annotations.Proxy;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = GeneType.class)
@Table(name = "gene_type")
public class GeneTypeImpl extends DashboardEntityImpl implements GeneType {

    private static final long serialVersionUID = -2780523146377767629L;

}