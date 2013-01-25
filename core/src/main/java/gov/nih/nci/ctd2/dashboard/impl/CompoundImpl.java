package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.Compound;

public class CompoundImpl extends SubjectImpl implements Compound {
    private String smilesNotation;

    public String getSmilesNotation() {
        return smilesNotation;
    }

    public void setSmilesNotation(String smilesNotation) {
        this.smilesNotation = smilesNotation;
    }
}
