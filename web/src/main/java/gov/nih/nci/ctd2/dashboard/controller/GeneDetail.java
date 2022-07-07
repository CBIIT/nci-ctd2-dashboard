package gov.nih.nci.ctd2.dashboard.controller;

import java.lang.ref.Reference;

/* Although there are already similar and overlapped data models, this one is particularly for implementing efficiently the network detail feature. */
public class GeneDetail {
    public final String geneName;
    public final References references;

    public GeneDetail(String geneName, String entrez, String genecards, String dave, String uniprot) {
        this.geneName = geneName;
        this.references = new References(entrez, genecards, dave, uniprot);
    }

    static public class References {
        public String entrez, genecards, dave, uniprot;

        public References(String entez, String genecards, String dave, String uniprot) {
            this.entrez = entez;
            this.genecards = genecards;
            this.dave = dave;
            this.uniprot = uniprot;
        }
    }
}
