package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Widget;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = Widget.class)
@Table(name = "widget")
public class WidgetImpl extends DashboardEntityImpl implements Widget {
}
