package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.Index;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.*;

@Entity
@Proxy(proxyClass= DashboardEntity.class)
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
@DynamicInsert
@Table(name = "dashboard_entity",
        indexes = { @Index(name = "entityNameIdx", columnList = "displayName" )
})
@Indexed
public class DashboardEntityImpl implements DashboardEntity {
    private static final long serialVersionUID = 7796821976089294032L;
	public final static String FIELD_DISPLAYNAME = "keyword";
    public final static String FIELD_DISPLAYNAME_WS = "keywordWS";
    public final static String FIELD_DISPLAYNAME_UT = "keywordUT";

    private Integer id;
    private String displayName;

    @FullTextField(name = FIELD_DISPLAYNAME, searchable = Searchable.YES, projectable = Projectable.YES)
    @FullTextField(name = FIELD_DISPLAYNAME_WS, searchable = Searchable.YES, projectable = Projectable.YES, analyzer = "ctd2analyzer")
    @GenericField(name = FIELD_DISPLAYNAME_UT, searchable = Searchable.YES)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardEntityImpl that = (DashboardEntityImpl) o;
        if(this.getId() == null || that.getId() == null)
            return super.equals(o);

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
