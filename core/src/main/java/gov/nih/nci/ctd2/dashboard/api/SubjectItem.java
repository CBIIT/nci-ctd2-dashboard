package gov.nih.nci.ctd2.dashboard.api;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "subject_item")
public class SubjectItem implements Serializable {

    private static final long serialVersionUID = 4332875482830599641L;

    @Column(length = 10240)
    private String description;

    private String clazz, role;
    private String name, columnName;
    public String uri;

    @Column(length = 10240, columnDefinition = "BLOB")
    private String[] synonyms;

    @Lob
    @Column(length = 10240, columnDefinition = "BLOB")
    private XRefItem[] xref;

    @Id
    @GeneratedValue
    private Long id;

    public SubjectItem() {
    }

    public XRefItem[] getXref() {
        return xref;
    }

    public void setXref(XRefItem[] xref) {
        this.xref = xref;
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public SubjectItem(String stableURL, String role, String description, String name, String[] synonyms, XRefItem[] xref,
            String columnName) {
        String clazz = stableURL.substring(0, stableURL.indexOf("/"));
        this.setClazz(clazz);
        this.setRole(role);
        this.setDescription(description);
        this.setName(name);
        this.setSynonyms(synonyms);
        this.setXref(xref);
        this.setColumnName(columnName);
        this.uri = stableURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
