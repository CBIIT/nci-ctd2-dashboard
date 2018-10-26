package gov.nih.nci.ctd2.dashboard.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import gov.nih.nci.ctd2.dashboard.model.EvidenceRole;

@Entity
@Table(name = "evidence_role")
@Proxy(proxyClass = EvidenceRole.class)
public class EvidenceRoleImpl extends DashboardEntityImpl implements EvidenceRole {

    private static final long serialVersionUID = 2902159165303232573L;
}
