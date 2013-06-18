package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Proxy(proxyClass = Subject.class)
@Table(name = "subject")
@Indexed
public class SubjectImpl extends DashboardEntityImpl implements Subject {
    private Set<Synonym> synonyms = new LinkedHashSet<Synonym>();
    private Set<Xref> xrefs = new LinkedHashSet<Xref>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = SynonymImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "subject_synonym_map")
    public Set<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = XrefImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "subject_xref_map")
    public Set<Xref> getXrefs() {
        return xrefs;
    }

    public void setXrefs(Set<Xref> xrefs) {
        this.xrefs = xrefs;
    }
}
