package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.UrlEvidence;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = UrlEvidence.class)
@Table(name = "url_evidence")
public class UrlEvidenceImpl extends EvidenceImpl implements UrlEvidence {
    private String url;

    @Column(length = 2048)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
