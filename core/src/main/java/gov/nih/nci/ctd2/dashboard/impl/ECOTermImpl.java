package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.ECOTerm;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.Attributes.Name;

@Entity
@Proxy(proxyClass = ECOTerm.class)
@Table(name = "ecoterm")
@Indexed
public class ECOTermImpl extends DashboardEntityImpl implements ECOTerm {

    private static final long serialVersionUID = 1L;

    private String stableURL = "";
    private String code = "";
    private String definition = "";

    @Override
    public String getStableURL() {
        return stableURL;
    }

    @Override
    public void setStableURL(String stableURL) {
        this.stableURL = stableURL;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 512)
    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "[ECO term] name: " + getDisplayName() + ", code: " + code + ", definition: " + definition;
    }
}
