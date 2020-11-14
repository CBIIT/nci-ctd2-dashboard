package gov.nih.nci.ctd2.dashboard.model;

public interface ECOTerm extends DashboardEntity, HasStableURL {
    public String getCode();

    public void setCode(String code);

    public String getDefinition();

    public void setDefinition(String definition);

    public String getSynonyms();

    public void setSynonyms(String synonyms);

    /* Whether the name, or the code, or the synonyms contain a given term. */
    public Boolean containsTerm(String term);
}
