package gov.nih.nci.ctd2.dashboard.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.util.ObservationURIsAndTiers;

/* API 2.0 */
public class SubjectResponse {
    private static final Log log = LogFactory.getLog(SubjectResponse.class);
    public final String clazz, name;
    public final String[] synonyms, roles;
    public final XRefItem[] xref;

    public final ObservationCount observation_count;
    public final String[] observations;

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

    private static SubjectResponse createInstance(final Subject subject, final Filter filter,
            DashboardDao dashboardDao) {

        int[] tierCount = new int[3];
        Set<Integer> set = new HashSet<Integer>();
        for (ObservedSubject observedSubject : dashboardDao.findObservedSubjectBySubject(subject)) {
            ObservedSubjectRole observedSubjectRole = observedSubject.getObservedSubjectRole();
            String subjectRole = observedSubjectRole.getSubjectRole().getDisplayName();
            if (filter.rolesIncluded.size() > 0 && !filter.rolesIncluded.contains(subjectRole))
                continue;

            ObservationTemplate observatinoTemplate = observedSubject.getObservation().getSubmission()
                    .getObservationTemplate();
            Integer observationTier = observatinoTemplate.getTier();
            String centerNameBrief = observatinoTemplate.getSubmissionCenter().getStableURL().substring(7);
            if (filter.centerIncluded.size() > 0 && !filter.centerIncluded.contains(centerNameBrief))
                continue;

            if ((Arrays.asList(filter.tiersIncluded).contains(observationTier))) {
                set.add(observedSubject.getObservation().getId());
                tierCount[observationTier.intValue() - 1]++;
                if (filter.limit > 0 && set.size() >= filter.limit) {
                    break;
                }
            }
        }
        List<ObservationItem> observations = dashboardDao.findObservationInfo(new ArrayList<Integer>(set));

        Set<String> roles = new TreeSet<String>();
        for (int i = 0; i < observations.size(); i++) {
            for (SubjectItem sub : observations.get(i).subject_list) {
                if (sub.getName().equals(subject.getDisplayName())) {
                    roles.add(sub.getRole());
                    break;
                }
            }
        }
        String[] uris = observations.stream().map(x -> x.uri).toArray(String[]::new);
        SubjectResponse subjectResponse = new SubjectResponse(subject, uris, roles.toArray(new String[0]), tierCount);
        return subjectResponse;
    }

    private SubjectResponse(Subject subject, String[] observations, String[] roles, int[] tierCount) {
        String stableURL = subject.getStableURL();
        this.clazz = stableURL.substring(0, stableURL.indexOf("/"));

        this.name = subject.getDisplayName();
        this.synonyms = getSynomyms(subject);
        this.xref = getXRefs(subject);

        this.roles = roles;

        assert tierCount.length == 3;
        observation_count = new ObservationCount(tierCount[0], tierCount[1], tierCount[2]);
        this.observations = observations;
    }

    public static SubjectResponse createInstance(final DashboardEntity x, final Filter filter,
            DashboardDao dashboardDao) {
        if (x instanceof Subject) {
            return createInstance((Subject) x, filter, dashboardDao);
        } else if (x instanceof ECOTerm) {
            ECOTerm ecoTerm = (ECOTerm) x;
            ObservationURIsAndTiers data = dashboardDao.ecoCode2ObservationURIsAndTiers(ecoTerm.getCode());
            return new SubjectResponse(ecoTerm, data);
        } else {
            log.error("unexpected subject type:" + x.getClass().getName());
            return null;
        }
    }

    private SubjectResponse(ECOTerm ecoTerm, ObservationURIsAndTiers data) {
        String stableURL = ecoTerm.getStableURL();
        this.clazz = stableURL.substring(0, stableURL.indexOf("/"));

        this.name = ecoTerm.getDisplayName();
        String synonyms = ecoTerm.getSynonyms().trim();
        if (synonyms.length() == 0) {
            this.synonyms = null;
        } else {
            this.synonyms = synonyms.split("\\|");
        }

        this.xref = null;
        this.roles = null;

        observation_count = new ObservationCount(data.tier1, data.tier2, data.tier3);
        this.observations = data.uris;
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
