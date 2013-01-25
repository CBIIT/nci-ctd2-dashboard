package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Proxy(proxyClass = Subject.class)
public class SubjectImpl extends DashboardEntityImpl implements Subject {
    private Set<Synonym> synonyms = new HashSet<Synonym>();

    @OneToMany
    public Set<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms) {
        this.synonyms = synonyms;
    }
}
