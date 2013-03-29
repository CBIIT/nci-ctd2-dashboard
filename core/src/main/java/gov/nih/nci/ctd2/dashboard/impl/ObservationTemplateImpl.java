package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Proxy(proxyClass= ObservationTemplate.class)
@Table(name = "observation_template")
public class ObservationTemplateImpl extends DashboardEntityImpl implements ObservationTemplate {
    private String description;
	private String observationSummary;
    private Integer tier = 0;

    @Column(length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 1024)
    public String getObservationSummary() {
        return observationSummary;
    }

    public void setObservationSummary(String observationSummary) {
        this.observationSummary = observationSummary;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }
}
