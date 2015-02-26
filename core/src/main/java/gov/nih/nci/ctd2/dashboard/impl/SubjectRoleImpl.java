package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.SubjectRole;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = SubjectRole.class)
@Table(name = "subject_role")
public class SubjectRoleImpl extends DashboardEntityImpl implements SubjectRole {
}
