package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
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

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/findProteinFromGene")
public class FindProteinFromGeneController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> browseByCharacter(@PathVariable Integer id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Gene gene = dashboardDao.getEntityById(Gene.class, id);
        List<Protein> proteinByGene = dashboardDao.findProteinByGene(gene);

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                ;
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(proteinByGene),
                headers,
                HttpStatus.OK
        );
    }
}
