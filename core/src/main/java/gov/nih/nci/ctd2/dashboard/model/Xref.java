package gov.nih.nci.ctd2.dashboard.model;

public interface Xref extends DashboardEntity {
    public String getDatabaseId();
    public void setDatabaseId(String databaseId);
    public String getDatabaseName();
    public void setDatabaseName(String databaseName);
}
