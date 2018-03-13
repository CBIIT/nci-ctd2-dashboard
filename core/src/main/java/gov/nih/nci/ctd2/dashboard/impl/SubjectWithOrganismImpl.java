package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.SubjectWithOrganism;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subject_with_organism")
@Proxy(proxyClass = SubjectWithOrganism.class)
@Indexed
public class SubjectWithOrganismImpl extends SubjectImpl implements SubjectWithOrganism {
    private static final long serialVersionUID = -6851675098845211440L;
    private Organism organism;

    @ManyToOne(targetEntity = OrganismImpl.class)
    public Organism getOrganism() {
        return organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    @Override
    public void setStableURL(String dummy) {
        // this no-op method needs to exist for flexjson.JSONSerializer to do the right thing for CellSample.
    }
}
