package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;

@Entity
@Proxy(proxyClass= DashboardEntity.class)
@Inheritance(strategy = InheritanceType.JOINED)
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
@Table(name = "dashboard_entity")
@org.hibernate.annotations.Table(
        appliesTo = "dashboard_entity",
        indexes = { @Index(name = "entityNameIdx", columnNames = { "displayName" })
})
@Indexed
public class DashboardEntityImpl implements DashboardEntity {
    public final static String FIELD_DISPLAYNAME = "keyword";
    public final static String FIELD_DISPLAYNAME_UT = "keywordUT";

    private Integer id;
    private String displayName;

    @Fields({
        @Field(name = FIELD_DISPLAYNAME, index = org.hibernate.search.annotations.Index.TOKENIZED, store = Store.YES),
        @Field(name = FIELD_DISPLAYNAME_UT, index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    })
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
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
