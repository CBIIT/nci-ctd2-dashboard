package gov.nih.nci.ctd2.dashboard.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.EcoBrowse;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;

@Controller
@RequestMapping("/eco")
public class EcoController {
    private static final Log log = LogFactory.getLog(EcoController.class);

    @Autowired
    private DashboardDao dashboardDao;

    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(final DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    @Transactional
    @RequestMapping(value = "browse", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> browse() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        final List<EcoBrowse> list = new ArrayList<EcoBrowse>();

        Collections.sort(list, new Comparator<EcoBrowse>() {
            @Override
            public int compare(final EcoBrowse o1, final EcoBrowse o2) {
                final int i = o2.getScore() - o1.getScore();
                if (i == 0) {
                    final int j = o2.getMaxTier() - o1.getMaxTier();
                    if (j == 0) {
                        return o2.getNumberOfObservations() - o1.getNumberOfObservations();
                    } else {
                        return j;
                    }
                } else {
                    return i;
                }
            }
        });

        final JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(list), headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "term/{id}", method = { RequestMethod.GET,
            RequestMethod.POST }, headers = "Accept=application/json")
    public ResponseEntity<String> term(@PathVariable final String id) {
        log.debug("request received by EcoController for " + id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        final ECOTerm ecoterm = dashboardDao.getEntityByStableURL("eco", "eco/" + id);
        log.debug(ecoterm);
        final JSONSerializer jsonSerializer = new JSONSerializer().transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class);
        return new ResponseEntity<String>(jsonSerializer.deepSerialize(ecoterm), headers, HttpStatus.OK);
    }
}