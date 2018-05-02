package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;
import java.util.List;

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
import gov.nih.nci.ctd2.dashboard.model.Subject;
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
            @RequestParam(value = "tier", required = false, defaultValue = "0") String tiers,
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
        Integer tier1 = 0;
        try {
            tier1 = Integer.parseInt(tiers.split(",")[0]); // TODO test for now
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        List<? extends DashboardEntity> observations = webServiceUtil.getObservations(subject, role, tier1);

        if (limit > 0 && limit < observations.size()) {
            observations = observations.subList(0, limit);
        }

        ObservationItem[] obvs = new ObservationItem[observations.size()];
        for (int i = 0; i < observations.size(); i++) {
            obvs[i] = new ObservationItem((Observation) observations.get(i), dashboardDao);
        }
        SubjectBrowse subjectBrowse = new SubjectBrowse(subject, obvs);

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

        public final ObservationItem[] observations;

        public SubjectBrowse(Subject subject, ObservationItem[] observations) {
            clazz = subject.getClass().getSimpleName().replace("Impl", "");

            this.name = subject.getDisplayName();
            // TODO
            this.synonyms = null;
            this.roles = null;
            this.xref = null;
            this.observations = observations;
        }
    }
}
