package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = SubmissionCenter.class)
@Table(name = "submission_center")
public class SubmissionCenterImpl extends DashboardEntityImpl implements SubmissionCenter {

    private static final long serialVersionUID = 1692094829648428859L;
    private String stableURL;

    @Override
    public String getStableURL() {
        return stableURL;
    }

    @Override
    public void setStableURL(String stableURL) {
        this.stableURL = stableURL;
    }
}
