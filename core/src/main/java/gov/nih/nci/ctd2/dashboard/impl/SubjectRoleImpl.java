package gov.nih.nci.ctd2.dashboard.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import gov.nih.nci.ctd2.dashboard.model.SubjectRole;

@Entity
@Proxy(proxyClass = SubjectRole.class)
@Table(name = "subject_role")
public class SubjectRoleImpl extends DashboardEntityImpl implements SubjectRole {

    private static final long serialVersionUID = 876905171662921966L;
}
