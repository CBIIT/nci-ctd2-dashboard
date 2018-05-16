package gov.nih.nci.ctd2.dashboard.api;

import java.util.HashMap;
import java.util.Map;

import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;

public class SubjectItem {
    public final String clazz, role, description, name;
    public final String[] synonyms;
    public final XRefItem[] xref;

    public SubjectItem(ObservedSubject observedSubject, String[] synonyms, XRefItem[] xref) {
        gov.nih.nci.ctd2.dashboard.model.Subject subject = observedSubject.getSubject();
        clazz = simpleClassName.get( subject.getClass().getSimpleName().replace("Impl", "") );
        this.role = observedSubject.getObservedSubjectRole().getSubjectRole().getDisplayName();
        this.description = observedSubject.getObservedSubjectRole().getDisplayText();
        this.name = subject.getDisplayName();
        this.synonyms = synonyms;
        this.xref = xref;
    }

    final static Map<String, String> simpleClassName = new HashMap<String, String>();
    static {
        simpleClassName.put("AnimalModel", "animal-model");
        simpleClassName.put("CellSample", "cell-sample");
        simpleClassName.put("Compound", "compound");
        simpleClassName.put("Gene", "gene");
        simpleClassName.put("Protein", "protein");
        simpleClassName.put("ShRna", "rna");
        simpleClassName.put("TissueSample", "tissue");
        simpleClassName.put("Transcript", "transcript");
    }
}
