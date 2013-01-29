package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Proxy(proxyClass = Subject.class)
@Table(name = "subject")
public class SubjectImpl extends DashboardEntityImpl implements Subject {
    private Set<Synonym> synonyms = new HashSet<Synonym>();

    @OneToMany(targetEntity = SynonymImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "subject_synonym_map")
    public Set<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms) {
        this.synonyms = synonyms;
    }
}
