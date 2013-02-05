package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.LabelEvidence;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = LabelEvidence.class)
@Table(name = "label_evidence")
public class LabelEvidenceImpl extends EvidenceImpl implements LabelEvidence {
}
