package gov.nih.nci.ctd2.dashboard.controller;

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
import gov.nih.nci.ctd2.dashboard.api.CTD2Serializer;
import gov.nih.nci.ctd2.dashboard.api.SubjectResponse;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.XrefImpl;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Xref;

/* API 2.0 */
@Controller
@RequestMapping("/api/browse")
public class BrowseAPI {
    private static final Log log = LogFactory.getLog(BrowseAPI.class);
    @Autowired
    private DashboardDao dashboardDao;

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

        SubjectResponse.Filter filter = SubjectResponse.createFilter(center, role, tiers, maximum);

        DashboardEntity subject = null;
        if (subjectClass.equalsIgnoreCase("evidence") || subjectClass.equalsIgnoreCase("eco")) {
            /* API spec asks for Evidence but stable URL uses eco */
            var obj = dashboardDao.getEntityByStableURL("eco", "eco/" + subjectName);
            if (obj instanceof ECOTerm) {
                subject = (ECOTerm) obj;
            } else {
                log.error("unexpected subject type:" + obj.getClass().getName());
            }
        } else if (subjectClass.equalsIgnoreCase("gene")) {
            List<Gene> genes = dashboardDao.findGenesBySymbol(subjectName);
            if (genes.size() > 0) {
                Gene gene = genes.get(0);
                List<Protein> p = dashboardDao.findProteinByGene(gene);
                Xref xref = new XrefImpl();
                xref.setDatabaseId(p.get(0).getUniprotId());
                xref.setDatabaseName("UniProt");
                gene.getXrefs().add(xref);
                subject = gene;
            }
        } else {
            var obj = dashboardDao.getEntityByStableURL(subjectClass, subjectClass + "/" + subjectName);
            if (obj instanceof Subject) {
                subject = (Subject) obj;
            } else {
                log.error("unexpected subject type: " + (obj == null ? null : obj.getClass().getName()));
            }
        }
        if (subject == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        SubjectResponse subjectResponse = SubjectResponse.createInstance(subject, filter, dashboardDao);

        log.debug("ready to serialize");
        JSONSerializer jsonSerializer = CTD2Serializer.createJSONSerializer();
        String json = "{}";
        try {
            json = jsonSerializer.deepSerialize(subjectResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(json, headers, HttpStatus.OK);
    }
}
