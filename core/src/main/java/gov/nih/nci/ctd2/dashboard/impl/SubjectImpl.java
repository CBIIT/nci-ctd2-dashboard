package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;

import java.util.HashSet;
import java.util.Set;

public class SubjectImpl extends DashboardEntityImpl implements Subject {
    private Set<Synonym> synonyms = new HashSet<Synonym>();

    public Set<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms) {
        this.synonyms = synonyms;
    }
}
