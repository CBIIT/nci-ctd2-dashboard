package gov.nih.nci.ctd2.dashboard.model;

public interface ECOTerm extends DashboardEntity, HasStableURL {
    public String getCode();

    public void setCode(String code);

    public String getDefinition();

    public void setDefinition(String definition);

    public String getSynonyms();

    public void setSynonyms(String synonyms);
}
