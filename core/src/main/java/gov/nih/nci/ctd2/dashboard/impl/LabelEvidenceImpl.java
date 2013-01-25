package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.LabelEvidence;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = LabelEvidence.class)
public class LabelEvidenceImpl extends EvidenceImpl implements LabelEvidence {
}
