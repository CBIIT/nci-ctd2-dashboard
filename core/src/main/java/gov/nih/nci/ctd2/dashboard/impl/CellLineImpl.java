package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.CellLine;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Proxy(proxyClass= CellLine.class)
@Table(name = "cell_line")
public class CellLineImpl extends SubjectWithOrganismImpl implements CellLine {
	private String tissue;

    @Column(length = 128, nullable = true)
    public String getTissue() {
        return tissue;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }
}
