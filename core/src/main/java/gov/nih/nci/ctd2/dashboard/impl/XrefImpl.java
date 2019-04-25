package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import org.hibernate.annotations.Proxy;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "xref")
@Proxy(proxyClass = Xref.class)
public class XrefImpl extends DashboardEntityImpl implements Xref {
    private static final long serialVersionUID = -4725584706231465381L;
    private String databaseId;
    private String databaseName;

    @Column(length = 128, nullable = false)
    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    @Column(length = 32, nullable = false)
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseName, databaseId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XrefImpl) {
            XrefImpl xref = (XrefImpl) obj;
            if (databaseName.equals(xref.databaseName) && databaseId.equals(xref.databaseId))
                return true;
        }
        return false;
    }
}
