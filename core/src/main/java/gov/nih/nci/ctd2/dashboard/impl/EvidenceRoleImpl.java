package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.EvidenceRole;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "evidence_role")
@Proxy(proxyClass = EvidenceRole.class)
public class EvidenceRoleImpl extends DashboardEntityImpl implements EvidenceRole {
}
