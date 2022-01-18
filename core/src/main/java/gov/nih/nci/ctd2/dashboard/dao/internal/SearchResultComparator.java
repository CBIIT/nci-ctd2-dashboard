package gov.nih.nci.ctd2.dashboard.dao.internal;

import java.util.Comparator;

import gov.nih.nci.ctd2.dashboard.util.SubjectResult;

/* ranking rules: first, compare match numbers; if same, compare observation numbers. For both comparisons, larger number is ranked higher. */
public class SearchResultComparator implements Comparator<SubjectResult> {

    @Override
    public int compare(SubjectResult e0, SubjectResult e1) {
        int m0 = e0.getMatchNumber();
        int m1 = e1.getMatchNumber();
        if (m0 != m1) {
            return m1 - m0;
        }
        return e1.getObservationCount() - e0.getObservationCount();
    }

}
