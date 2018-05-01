package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

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

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/submission")
public class SubmissionAPI {
    private static final Log log = LogFactory.getLog(SubmissionAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private WebServiceUtil webServiceUtil;

    @Transactional
    @RequestMapping(value = "{id}", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getSubmission(@PathVariable String id,
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
        Submission submission = dashboardDao.getEntityByStableURL("submission", "submission/" + id);
        List<? extends DashboardEntity> observations = webServiceUtil.getDashboardEntities("observation",
                submission.getId());
        if (limit > 0 && limit < observations.size()) {
            observations = observations.subList(0, limit);
        }
        APIObservation[] obvs = new APIObservation[observations.size()];
        for (int i = 0; i < observations.size(); i++) {
            List<ObservedSubject> subjects = dashboardDao
                    .findObservedSubjectByObservation((Observation) observations.get(i));
            List<ObservedEvidence> evidences = dashboardDao
                    .findObservedEvidenceByObservation((Observation) observations.get(i));
            Subject[] subjs = new Subject[subjects.size()];
            for (int j = 0; j < subjects.size(); j++) {
                ObservedSubject observedSubject = subjects.get(j);
                Synonym[] synonyms = observedSubject.getSubject().getSynonyms().toArray(new Synonym[0]);
                String[] synms = new String[synonyms.length];
                for (int k = 0; k < synonyms.length; k++) {
                    synms[k] = synonyms[k].getDisplayName();
                }
                Xref[] xrefs = observedSubject.getSubject().getXrefs().toArray(new Xref[0]);
                APIXRef[] apiXrefs = new APIXRef[xrefs.length];
                for (int k = 0; k < xrefs.length; k++) {
                    apiXrefs[k] = new APIXRef(xrefs[k].getDatabaseName(), xrefs[k].getDatabaseId());
                }
                subjs[j] = new Subject(observedSubject, synms, apiXrefs);
            }
            Evidence[] evds = new Evidence[evidences.size()];
            for (int j = 0; j < evidences.size(); j++) {
                ObservedEvidence observedEvidence = evidences.get(j);
                evds[j] = new Evidence(observedEvidence);
            }
            obvs[i] = new APIObservation(submission, subjs, evds);
        }
        APISubmission apiSubmission = new APISubmission(submission, obvs);

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                .transform(new FieldNameTransformer("class"), "observations.subject_list.clazz")
                .transform(new FieldNameTransformer("class"), "observations.evidence_list.clazz");
        String json = "{}";
        try {
            json = jsonSerializer.exclude("class").exclude("observations.class").deepSerialize(apiSubmission);
        } catch (Exception e) {
            json = "{'Exception': '" + e.getMessage() + "'}";
            e.printStackTrace();
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }

    private static class FieldNameTransformer extends AbstractTransformer {
        private String transformedFieldName;

        public FieldNameTransformer(String transformedFieldName) {
            this.transformedFieldName = transformedFieldName;
        }

        public void transform(Object object) {
            boolean setContext = false;

            TypeContext typeContext = getContext().peekTypeContext();

            // Write comma before starting to write field name if this
            // isn't first property that is being transformed
            if (!typeContext.isFirst())
                getContext().writeComma();

            typeContext.setFirst(false);

            getContext().writeName(getTransformedFieldName());
            getContext().writeQuoted(object.toString());

            if (setContext) {
                getContext().writeCloseObject();
            }
        }

        /***
         * TRUE tells the JSONContext that this class will be handling the writing of
         * our property name by itself.
         */
        @Override
        public Boolean isInline() {
            return Boolean.TRUE;
        }

        public String getTransformedFieldName() {
            return this.transformedFieldName;
        }
    }

    public static class APISubmission {
        public final String submission_center, submission_name;
        public final Date submisstion_date;
        public final Integer tier;
        public final String project, submission_description, story_title;
        public final Integer observation_count;
        public final APIObservation[] observations;

        public APISubmission(Submission submission, APIObservation[] observations) {
            ObservationTemplate template = submission.getObservationTemplate();
            this.submission_center = template.getSubmissionCenter().getDisplayName();
            this.submission_name = submission.getDisplayName();
            this.submisstion_date = submission.getSubmissionDate();
            this.tier = template.getTier();
            this.project = template.getProject();
            this.submission_description = template.getDescription();

            String st = null;
            if (template.getIsSubmissionStory())
                st = template.getDescription();
            this.story_title = st;

            this.observation_count = observations.length;
            this.observations = observations;
        }
    }

    public static class APIObservation {
        public final String submission_id, observation_summary;
        public final Subject[] subject_list;
        public final Evidence[] evidence_list;

        public APIObservation(Submission submission, Subject[] subject_list, Evidence[] evidence_list) {
            this.submission_id = submission.getId().toString();
            this.observation_summary = submission.getObservationTemplate().getObservationSummary();
            this.subject_list = subject_list;
            this.evidence_list = evidence_list;
        }
    }

    public static class Subject {
        public final String clazz, role, description, name;
        public final String[] synonyms;
        public final APIXRef[] xref;

        public Subject(ObservedSubject observedSubject, String[] synonyms, APIXRef[] xref) {
            gov.nih.nci.ctd2.dashboard.model.Subject subject = observedSubject.getSubject();
            clazz = subject.getClass().getSimpleName().replace("Impl", "");
            this.role = observedSubject.getObservedSubjectRole().getSubjectRole().getDisplayName();
            this.description = observedSubject.getObservedSubjectRole().getDisplayText();
            this.name = subject.getDisplayName();
            this.synonyms = synonyms;
            this.xref = xref;
        }
    }

    public static class Evidence {
        public final String clazz, type, description, value, units, mime_type;

        public Evidence(ObservedEvidence observedEvidence) {
            gov.nih.nci.ctd2.dashboard.model.Evidence evidence = observedEvidence.getEvidence();
            clazz = evidence.getClass().getSimpleName().replace("Impl", "");
            this.type = observedEvidence.getObservedEvidenceRole().getEvidenceRole().getDisplayName();
            this.description = observedEvidence.getObservedEvidenceRole().getDisplayText();

            String value = null, units = null, mime_type = null;
            if (evidence instanceof DataNumericValue) {
                DataNumericValue dnv = (DataNumericValue) evidence;
                value = dnv.getNumericValue().toString();
                units = dnv.getUnit();
            } else if (evidence instanceof FileEvidence) {
                FileEvidence fe = (FileEvidence) evidence;
                value = fe.getFileName();
                mime_type = fe.getMimeType();
            }

            this.value = value;
            this.units = units;
            this.mime_type = mime_type;
        }
    }

    public static class APIXRef {
        public final String source, id;

        public APIXRef(String s, String i) {
            source = s;
            id = i;
        }
    }
}
