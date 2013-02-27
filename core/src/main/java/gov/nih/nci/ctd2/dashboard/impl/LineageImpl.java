package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Lineage;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "lineage")
@Proxy(proxyClass = Lineage.class)
public class LineageImpl extends SubjectImpl implements Lineage {
}
