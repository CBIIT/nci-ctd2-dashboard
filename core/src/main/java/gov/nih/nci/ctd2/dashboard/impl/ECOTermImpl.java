package gov.nih.nci.ctd2.dashboard.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Indexed;

import gov.nih.nci.ctd2.dashboard.model.ECOTerm;

@Entity
@Proxy(proxyClass = ECOTerm.class)
@Table(name = "ecoterm")
@Indexed
public class ECOTermImpl extends DashboardEntityImpl implements ECOTerm {

    private static final long serialVersionUID = 1L;

    private String stableURL = "";
    private String code = "";
    private String definition = "";
    private String synonyms = "";

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

    @Column(length = 512)
    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public String toString() {
        return "[ECO term] name: " + getDisplayName() + ", code: " + code + ", definition: " + definition;
    }

    @Override
    public Boolean containsTerm(String term) {
        if (this.getDisplayName().toLowerCase().contains(term))
            return true;
        if (code.toLowerCase().contains(term.replace("_", ":")))
            return true;
        for (String synonym : synonyms.split("\\|")) {
            if (synonym.contains(term))
                return true;
        }
        return false;
    }
}
