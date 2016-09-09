package gov.nih.nci.ctd2.dashboard.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.util.WebServiceUtil;

@Controller
@RequestMapping("/list")
public class ListController {
    @Autowired
    private WebServiceUtil webServiceUtil;

    @Autowired
    @Qualifier("maxNumberOfEntities")
    private Integer maxNumberOfEntities = 100;

    public WebServiceUtil getWebServiceUtil() {
        return webServiceUtil;
    }

    public void setWebServiceUtil(WebServiceUtil webServiceUtil) {
        this.webServiceUtil = webServiceUtil;
    }

    public Integer getMaxNumberOfEntities() {
        return maxNumberOfEntities;
    }

    public void setMaxNumberOfEntities(Integer maxNumberOfEntities) {
        this.maxNumberOfEntities = maxNumberOfEntities;
    }

    @Transactional
    @RequestMapping(value="{type}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getSearchResultsInJson(
            @PathVariable String type,
            @RequestParam("filterBy") Integer filterBy,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "tier", required = false, defaultValue = "0") Integer tier,
            @RequestParam(value = "getAll", required = false, defaultValue = "false") Boolean getAll
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<? extends DashboardEntity> entities = null;
        if("observation".equals(type)) { // different logic for observation list
            entities = webServiceUtil.getObservationsPerRoleTier(filterBy, role, tier);
        } else if("template".equals(type)) { // template cannot be cached
            entities = webServiceUtil.getTemplates(filterBy);
        } else {
            entities = webServiceUtil.getDashboardEntities(type, filterBy);
        }
        if(!getAll && entities.size() > getMaxNumberOfEntities()) {
            entities = entities.subList(0, getMaxNumberOfEntities());
        }

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
        ;

        String s = null;
        if("template".equals(type)) {
            s = jsonSerializer.deepSerialize(entities);
        } else {
            s = jsonSerializer.serialize(entities);
        }
        return new ResponseEntity<String>(
                s,
                headers,
                HttpStatus.OK
        );
    }

    @Transactional
    @RequestMapping(value="similar/{submissionId}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getSimilarSubmissionsInJson(@PathVariable Integer submissionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                ;
        List<Submission> submissions = webServiceUtil.getSimilarSubmissions(submissionId);

        return new ResponseEntity<String>(
                jsonSerializer.serialize(submissions),
                headers,
                HttpStatus.OK
        );
    }

}
