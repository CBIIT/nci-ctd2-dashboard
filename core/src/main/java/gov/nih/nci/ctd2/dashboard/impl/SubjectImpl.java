package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Proxy(proxyClass = Subject.class)
@Table(name = "subject")
@Indexed
public class SubjectImpl extends DashboardEntityImpl implements Subject {
    public final static String FIELD_SYNONYM = "synonym";
    public final static String FIELD_SYNONYM_UT = "synonymUT";

    private Set<Synonym> synonyms = new LinkedHashSet<Synonym>();
    private Set<Xref> xrefs = new LinkedHashSet<Xref>();
    private Integer score = 0;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = SynonymImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "subject_synonym_map")
    public Set<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    @Fields({
            @Field(name = FIELD_SYNONYM, index = org.hibernate.search.annotations.Index.TOKENIZED, store = Store.YES),
            @Field(name = FIELD_SYNONYM_UT, index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    })
    @Transient
    public String getSynoynmStrings() {
        StringBuilder builder = new StringBuilder();
        for (Synonym synonym : getSynonyms()) {
            builder.append(synonym.getDisplayName()).append(" ");
        }
        return builder.toString();
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
