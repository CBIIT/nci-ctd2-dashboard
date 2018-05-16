package gov.nih.nci.ctd2.dashboard.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

public class SubjectResponse {
    public final String clazz, name;
    public final String[] synonyms, roles;
    public final XRefItem[] xref;

    public final ObservationCount observation_count;
    public final ObservationItem[] observations;

    public static class Filter {
        public final int limit;
        public final List<String> rolesIncluded, centerIncluded;
        public final Integer[] tiersIncluded;

        public Filter(final int limit, final List<String> rolesIncluded, final List<String> centerIncluded,
                final Integer[] tiersIncluded) {
            this.limit = limit;
            this.rolesIncluded = rolesIncluded;
            this.centerIncluded = centerIncluded;
            this.tiersIncluded = tiersIncluded;
        }
    }

    public static Filter createFilter(String center, String role, String tiers, String maximum) {
        int limit = 0;
        if (maximum != null && maximum.trim().length() > 0) {
            try {
                limit = Integer.parseInt(maximum.trim());
            } catch (NumberFormatException e) {
                // no-op
            }
        }

        List<String> rolesIncluded = new ArrayList<String>();
        if (role.trim().length() > 0) {
            String[] rls = role.trim().toLowerCase().split(",");
            rolesIncluded.addAll(Arrays.asList(rls));
        }

        List<String> centerIncluded = new ArrayList<String>();
        if (center.trim().length() > 0) {
            String[] ctr = center.trim().toLowerCase().split(",");
            centerIncluded.addAll(Arrays.asList(ctr));
        }

        Integer[] tiersIncluded = { 1, 2, 3 };
        if (tiers.trim().length() > 0) {
            try {
                String[] tierStrings = tiers.split(",");
                Integer[] tt = new Integer[tierStrings.length];
                for (int index = 0; index < tierStrings.length; index++) {
                    tt[index] = Integer.parseInt(tierStrings[index]);
                }
                tiersIncluded = tt;
            } catch (NumberFormatException e) {
                // e.printStackTrace();
                tiersIncluded = new Integer[0];
            }
        }

        return new Filter(limit, rolesIncluded, centerIncluded, tiersIncluded);
    }

    public static SubjectResponse createInstance(final Subject subject, final Filter filter, DashboardDao dashboardDao,
            WebServiceUtil webServiceUtil) {

        List<? extends DashboardEntity> observations = webServiceUtil.getObservations(subject, filter);

        if (filter.limit > 0 && filter.limit < observations.size()) {
            observations = observations.subList(0, filter.limit);
        }

        ObservationItem[] obvs = new ObservationItem[observations.size()];
        Set<String> roles = new TreeSet<String>();
        int[] tierCount = new int[3];
        for (int i = 0; i < observations.size(); i++) {
            Observation observation = (Observation) observations.get(i);
            obvs[i] = new ObservationItem(observation, dashboardDao);
            int tier = observation.getSubmission().getObservationTemplate().getTier();
            assert tier > 0 && tier < 3;
            tierCount[tier - 1]++;

            List<ObservedSubject> observedSubjects = dashboardDao.findObservedSubjectByObservation(observation);
            for (ObservedSubject os : observedSubjects) {
                if (os.getSubject().equals(subject)) {
                    String rl = os.getObservedSubjectRole().getSubjectRole().getDisplayName();
                    roles.add(rl);
                }
            }
        }
        SubjectResponse subjectResponse = new SubjectResponse(subject, obvs, roles.toArray(new String[0]), tierCount);
        return subjectResponse;
    }

    public SubjectResponse(Subject subject, ObservationItem[] observations, String[] roles, int[] tierCount) {
        clazz = SubjectItem.simpleClassName.get( subject.getClass().getSimpleName().replace("Impl", "") );

        this.name = subject.getDisplayName();
        this.synonyms = getSynomyms(subject);
        this.xref = getXRefs(subject);

        this.roles = roles;

        assert tierCount.length == 3;
        observation_count = new ObservationCount(tierCount[0], tierCount[1], tierCount[2]);
        this.observations = observations;
    }

    private static String[] getSynomyms(Subject subject) {
        Synonym[] synonyms = subject.getSynonyms().toArray(new Synonym[0]);
        String[] synms = new String[synonyms.length];
        for (int k = 0; k < synonyms.length; k++) {
            synms[k] = synonyms[k].getDisplayName();
        }
        return synms;
    }

    private static XRefItem[] getXRefs(Subject subject) {
        Xref[] xrefs = subject.getXrefs().toArray(new Xref[0]);
        XRefItem[] apiXrefs = new XRefItem[xrefs.length];
        for (int k = 0; k < xrefs.length; k++) {
            apiXrefs[k] = new XRefItem(xrefs[k].getDatabaseName(), xrefs[k].getDatabaseId());
        }
        return apiXrefs;
    }

    public static class ObservationCount {
        public final int tier1, tier2, tier3;

        public ObservationCount(int t1, int t2, int t3) {
            tier1 = t1;
            tier2 = t2;
            tier3 = t3;
        }
    }
}
