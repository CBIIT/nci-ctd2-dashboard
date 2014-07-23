package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.AnimalModel;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass = AnimalModel.class)
@Table(name = "animal_model")
@Indexed
public class AnimalModelImpl extends SubjectWithOrganismImpl implements AnimalModel {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
