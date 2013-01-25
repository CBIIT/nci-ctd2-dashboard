package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Proxy(proxyClass= DashboardEntity.class)
@Inheritance(strategy = InheritanceType.JOINED)
@org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true)
public class DashboardEntityImpl implements DashboardEntity {
    private Integer id;
    private String displayName;

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
}
