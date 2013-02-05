package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value="{keyword}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> getSearchResultsInJson(@PathVariable String keyword) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        // Do not allow search with really genetic keywords
        // This is to prevent unnecessary server loads
        if(keyword.length() < 3)
            return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);

        List<Subject> subjectsBySynonym = dashboardDao.findSubjectsBySynonym(keyword, false);

        JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class);
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(subjectsBySynonym),
                headers,
                HttpStatus.OK
        );
    }

}
