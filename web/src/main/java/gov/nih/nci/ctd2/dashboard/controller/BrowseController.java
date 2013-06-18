package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.*;
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

import java.util.*;

@Controller
@RequestMapping("/browse")
public class BrowseController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value="{type}/{c}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
    public ResponseEntity<String> browseByCharacter(@PathVariable String type, @PathVariable String c) {
        type = type.toLowerCase().trim();
        c = c.toLowerCase().trim();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        List<DashboardEntity> matches = new ArrayList<DashboardEntity>();
        List<? extends DashboardEntity> entities;
        if(type.equals("target")) {
            entities = dashboardDao.browseTargets(c);
        } else if (type.equals("compound")) {
            entities = dashboardDao.browseCompounds(c);
        } else {
            entities = new ArrayList<DashboardEntity>();
        }

        for (DashboardEntity entity : entities) {
            assert entities instanceof Subject;

            if(entity.getDisplayName().toLowerCase().startsWith(c)
                    && !dashboardDao.findObservedSubjectBySubject((Subject) entity).isEmpty()) {
                matches.add(entity);
            }
        }

        Collections.sort(matches, new Comparator<Object>() {
            @Override
            public int compare(Object o, Object o1) {
                assert o instanceof DashboardEntity && o1 instanceof DashboardEntity;
                return ((DashboardEntity) o).getDisplayName().compareTo(((DashboardEntity) o1).getDisplayName());
            }
        });

        JSONSerializer jsonSerializer = new JSONSerializer()
                .transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class)
                ;
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(matches),
                headers,
                HttpStatus.OK
        );
    }
}