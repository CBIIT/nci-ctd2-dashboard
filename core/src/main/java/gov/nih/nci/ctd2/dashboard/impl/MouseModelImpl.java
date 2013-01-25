package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.MouseModel;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;

@Entity
@Proxy(proxyClass = MouseModel.class)
public class MouseModelImpl extends SubjectImpl implements MouseModel {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
