package gov.nih.nci.ctd2.dashboard.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;
import gov.nih.nci.ctd2.dashboard.model.HasStableURL;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.ShRna;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.SubjectWithOrganism;
import gov.nih.nci.ctd2.dashboard.model.Xref;

public class SubjectResult {
    final public String subjectName;
    final public String organismName;
    final public String className;
    final public Integer observationCount;
    final public Integer centerCount;
    public Integer matchNumber;
    final public Integer id;
    final public String stableURL;
    final public Set<String> synonyms;
    final public Set<String> roles;
    final public Set<Xref> xrefs; /* only for Compound */
    final public String type; /* only for ShRna */

    public SubjectResult(DashboardEntity entity, Integer observationCount, Integer centerCount, Integer matchNumber, Set<String> roles) {
        this.observationCount = observationCount;
        this.centerCount = centerCount;
        this.matchNumber = matchNumber;
        this.roles = roles;
        this.id = entity.getId();
        this.subjectName = entity.getDisplayName();
        this.className = entity.getClass().getSimpleName().replace("Impl", "");
        if (entity instanceof HasStableURL) {
            this.stableURL = ((HasStableURL) entity).getStableURL();
        } else {
            this.stableURL = null;
        }
        String organismName = null;
        Set<Xref> xrefs = null;
        String type = null;
        if (entity instanceof Subject) {
            this.synonyms = ((Subject) entity).getSynonyms().stream().map(s -> s.getDisplayName())
                    .collect(Collectors.toSet());
            if (entity instanceof Compound) { /* only necesary for Compound */
                xrefs = ((Subject) entity).getXrefs();
            }
            if (entity instanceof ShRna) {
                type = ((ShRna) entity).getType();
            }
            if (entity instanceof SubjectWithOrganism) {
                Organism organism = ((SubjectWithOrganism) entity).getOrganism();
                if (organism != null)
                    organismName = organism.getDisplayName();
            }
        } else if (entity instanceof ECOTerm) {
            String str = ((ECOTerm) entity).getSynonyms();
            String[] synonyms = new String[0];
            if (!str.trim().isEmpty()) {
                synonyms = str.split("\\|");
            }
            this.synonyms = new HashSet<String>(Arrays.asList(synonyms));
        } else {
            this.synonyms = null;
        }
        this.organismName = organismName;
        this.xrefs = xrefs;
        this.type = type;
    }

    public int getMatchNumber() {
        if (matchNumber == null)
            return 0;
        else
            return matchNumber;
    }

    public int getObservationCount() {
        if (observationCount == null)
            return 0;
        else
            return observationCount;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubjectResult))
            return false;

        SubjectResult e = (SubjectResult) o;
        if (e.id.equals(this.id))
            return true;
        else
            return false;
    }
}