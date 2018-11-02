package gov.nih.nci.ctd2.dashboard.model;

import java.util.Set;

public interface Subject extends DashboardEntity, HasStableURL {
    public Set<Synonym> getSynonyms();
    public void setSynonyms(Set<Synonym> synonyms);
    public Set<Xref> getXrefs();
    public void setXrefs(Set<Xref> xrefs);
}
