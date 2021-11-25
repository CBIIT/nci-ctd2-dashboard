package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtract;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ObjectPath;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyValue;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtraction;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Proxy(proxyClass = Subject.class)
@Table(name = "subject")
@Indexed
public class SubjectImpl extends DashboardEntityImpl implements Subject {
    private static final long serialVersionUID = 1L;
    public final static String FIELD_SYNONYM = "synonym";
    public final static String FIELD_SYNONYM_WS = "synonymWS";
    public final static String FIELD_SYNONYM_UT = "synonymUT";

    private Set<Synonym> synonyms = new LinkedHashSet<Synonym>();
    private Set<Xref> xrefs = new LinkedHashSet<Xref>();
    private String stableURL = "";

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = SynonymImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "subject_synonym_map")
    public Set<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    @FullTextField(name = FIELD_SYNONYM, searchable = Searchable.YES, projectable = Projectable.YES)
    @FullTextField(name = FIELD_SYNONYM_WS, searchable = Searchable.YES, projectable = Projectable.YES, analyzer = "ctd2analyzer")
    @GenericField(name = FIELD_SYNONYM_UT, searchable = Searchable.YES)
    @Transient
    @IndexingDependency(
        derivedFrom =  @ObjectPath(@PropertyValue(propertyName = "synonyms")),
        extraction = @ContainerExtraction(extract = ContainerExtract.NO)
    )
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

    @Override
    public String getStableURL() {
        return stableURL;
    }

    @Override
    public void setStableURL(String stableURL) {
        this.stableURL = stableURL;
    }
}
