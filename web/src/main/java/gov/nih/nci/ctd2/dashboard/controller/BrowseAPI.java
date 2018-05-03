package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.api.FieldNameTransformer;
import gov.nih.nci.ctd2.dashboard.api.ObservationItem;
import gov.nih.nci.ctd2.dashboard.api.XRefItem;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

@Controller
@RequestMapping("/api/browse")
public class BrowseAPI {
    private static final Log log = LogFactory.getLog(BrowseAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private WebServiceUtil webServiceUtil;

    @Transactional
    @RequestMapping(value = "{subjectClass}/{subjectName}", method = {
            RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubmission(@PathVariable String subjectClass, @PathVariable String subjectName,
            @RequestParam(value = "center", required = false, defaultValue = "") String center,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "tier", required = false, defaultValue = "") String tiers,
            @RequestParam(value = "maximum", required = false, defaultValue = "") String maximum) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        int limit = 0;
        if (maximum != null && maximum.trim().length() > 0) {
            try {
                limit = Integer.parseInt(maximum.trim());
            } catch (NumberFormatException e) {
                // no-op
            }
        }

        log.debug("subjectClass:" + subjectClass);
        log.debug("subjectName:" + subjectName);
        log.debug("center:" + center);
        log.debug("role:" + role);
        log.debug("tiers:" + tiers);
        log.debug("maximum:" + maximum);

        Subject subject = null;
        if (subjectClass.equalsIgnoreCase("gene")) {
            List<Gene> genes = dashboardDao.findGenesBySymbol(subjectName);
            if (genes.size() > 0)
                subject = genes.get(0);
        } else {
            subject = dashboardDao.getEntityByStableURL(subjectClass, subjectClass + "/" + subjectName);
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

        List<? extends DashboardEntity> observations = webServiceUtil.getObservations(subject, role,
                Arrays.asList(tiersIncluded));

        if (limit > 0 && limit < observations.size()) {
            observations = observations.subList(0, limit);
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
                    roles.contains(rl);
                }
            }
        }
        SubjectBrowse subjectBrowse = new SubjectBrowse(subject, obvs, roles.toArray(new String[0]), tierCount);

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class).transform(new FieldNameTransformer("class"), "clazz")
                .transform(new FieldNameTransformer("class"), "observations.subject_list.clazz")
                .transform(new FieldNameTransformer("class"), "observations.evidence_list.clazz");
        String json = "{}";
        try {
            json = jsonSerializer.exclude("class").exclude("observations.class").deepSerialize(subjectBrowse);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    public static class SubjectBrowse {
        public final String clazz, name;
        public final String[] synonyms, roles;
        public final XRefItem[] xref;

        public final ObservationCount observation_count;
        public final ObservationItem[] observations;

        public SubjectBrowse(Subject subject, ObservationItem[] observations, String[] roles, int[] tierCount) {
            clazz = subject.getClass().getSimpleName().replace("Impl", "");

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
