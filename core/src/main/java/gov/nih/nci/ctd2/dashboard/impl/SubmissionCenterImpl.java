package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass= SubmissionCenter.class)
@Table(name = "submission_center")
public class SubmissionCenterImpl extends DashboardEntityImpl implements SubmissionCenter {
}
