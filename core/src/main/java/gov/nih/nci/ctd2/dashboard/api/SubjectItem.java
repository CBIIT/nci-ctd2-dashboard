package gov.nih.nci.ctd2.dashboard.api;

public class SubjectItem {
    public final String clazz, role, description, name;
    public final String[] synonyms;
    public final XRefItem[] xref;
    public String columnName;

    public SubjectItem(String clazz, String role, String description, String name, String[] synonyms, XRefItem[] xref,
            String columnName) {
        this.clazz = clazz;
        this.role = role;
        this.description = description;
        this.name = name;
        this.synonyms = synonyms;
        this.xref = xref;
        this.columnName = columnName;
    }
}
