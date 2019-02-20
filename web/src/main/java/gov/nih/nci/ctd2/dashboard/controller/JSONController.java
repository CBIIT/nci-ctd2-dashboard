package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Controller
@RequestMapping("/get")
public class JSONController {
    private static final Log log = LogFactory.getLog(JSONController.class);
    @Autowired
    private DashboardDao dashboardDao;

    private static Set<String> typesWithStableURL = new HashSet<String>();
    static {
        Collections.addAll(typesWithStableURL, new String[] { "center", "animal-model", "cell-sample", "compound",
                "protein", "rna", "tissue", "transcript", "submission", "observation", "observedevidence" });
    }

    /*
     * gene needs a separate method because it asks for different number of
     * parameters
     */
    @Transactional
    @RequestMapping(value = "{type}/{species}/{symbol}", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getGeneInJson(@PathVariable String type, @PathVariable String species,
            @PathVariable String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (!type.equalsIgnoreCase("gene")) {
            log.info("query with wrong type");
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        List<Gene> genes = dashboardDao.findGenesBySymbol(symbol);
        Gene gene = null;
        for (Gene g : genes) {
            if (g.getOrganism().getDisplayName().toLowerCase().startsWith(species)) {
                gene = g;
                break;
            }
        }

        if (gene == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } else {
            JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                    .transform(new DateTransformer(), Date.class);
            return new ResponseEntity<String>(jsonSerializer.deepSerialize(gene), headers, HttpStatus.OK);
        }
    }

    @Transactional
    @RequestMapping(value = "{type}/{id}", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getEntityInJson(@PathVariable String type, @PathVariable String id) {
        DashboardEntity entityById = null;

        Class<? extends DashboardEntity> clazz = Subject.class;
        if (type.equalsIgnoreCase("subject")) {
            clazz = Subject.class;
        } else if (type.equals("observedsubject")) {
            clazz = ObservedSubject.class;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        log.debug("JSONController " + type + " " + id);
        if (typesWithStableURL.contains(type)) {
            String stableURL = type + "/" + id;
            if (type.equals("observedevidence"))
                stableURL = "mra/" + id;
            entityById = dashboardDao.getEntityByStableURL(type, stableURL);
        } else {
            entityById = dashboardDao.getEntityById(clazz, Integer.parseInt(id));
        }
        if (entityById == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(entityById), headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "homepage-text", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> getHomepageText() {

        HomepageText t = pullText();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        JSONSerializer jsonSerializer = new JSONSerializer();
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(t), headers, HttpStatus.OK);
    }

    static public class HomepageText {
        public String description = "<ul><li>enables users to explore data integrated from multiple CTD2 Centers</li>"
                + "<li>contains data from multiple experimental and computational approaches</li>"
                + "<li>addresses key biomedical subjects (e.g., genes, compounds, disease contexts)</li>"
                + "<li>explicitly captures the roles (e.g., biomarker, target, oncogene) subjects take in experiments</li>"
                + "<li>provides validation strength in terms of evidence \"<a href='http://www.ncbi.nlm.nih.gov/pubmed/27401613' target='_blank' data-toggle='tooltip' id='tierTooltip'>Tier</a>.\"</li></ul>";
    }

    private static HomepageText pullText() {
        HomepageText t = new HomepageText();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            URLConnection urlConnection = new java.net.URL("https://ocg.cancer.gov/dashboard-export.xml").openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            Document doc = dBuilder.parse(inputStream);

            inputStream.close();
            NodeList nList = doc.getElementsByTagName("main-description");
            org.w3c.dom.Node item = nList.item(0);
            String description = item.getTextContent();

            // style requirement
            description = description.replaceAll("<ul>", "<ul style='padding-left:0'>");
            // insert attributes to create the tooltip
            description = description.replaceAll("(<a [^>]*)(>Tiers</a>)", "$1 target='_blank' data-toggle='tooltip' id='tierTooltip'$2");
            t.description = description;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return t;
    }
}
