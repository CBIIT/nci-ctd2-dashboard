package gov.nih.nci.ctd2.dashboard.api;

import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;

public class SubjectItem {
    public final String clazz, role, description, name;
    public final String[] synonyms;
    public final XRefItem[] xref;

    public SubjectItem(ObservedSubject observedSubject, String[] synonyms, XRefItem[] xref) {
        gov.nih.nci.ctd2.dashboard.model.Subject subject = observedSubject.getSubject();
        clazz = subject.getClass().getSimpleName().replace("Impl", "");
        this.role = observedSubject.getObservedSubjectRole().getSubjectRole().getDisplayName();
        this.description = observedSubject.getObservedSubjectRole().getDisplayText();
        this.name = subject.getDisplayName();
        this.synonyms = synonyms;
        this.xref = xref;
    }
}
