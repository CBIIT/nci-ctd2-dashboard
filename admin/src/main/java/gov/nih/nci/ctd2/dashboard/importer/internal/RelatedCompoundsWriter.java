package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Subject;

@Component("relatedCompoundsWriter")
public class RelatedCompoundsWriter implements ItemWriter<String[]> {

    @Autowired
    private DashboardDao dashboardDao;

    private static final Log log = LogFactory.getLog(RelatedCompoundsWriter.class);

    public void write(List<? extends String[]> items) throws Exception {
        final Pattern pattern = Pattern.compile("GeneID:\\d+");
        int count = 0;
        int not_match = 0;

        Map<Integer, Integer> compound_ids = new HashMap<Integer, Integer>();
        List<Integer[]> list = new ArrayList<Integer[]>();
        for (String[] x : items) {
            String cpd_id = x[0];
            Integer id = compound_ids.get(Integer.valueOf(cpd_id));
            if (id == null) {
                List<Subject> compounds = dashboardDao.findSubjectsByXref("BROAD_COMPOUND", cpd_id);
                if (compounds.size() != 1) {
                    log.warn("The number of compounds for CTRP ID " + cpd_id + "is " + compounds.size()
                            + ". 1 is expected.");
                    continue;
                }
                id = compounds.get(0).getId();
                compound_ids.put(Integer.valueOf(cpd_id), id);
            }
            String annotation_id = x[2];
            Matcher matcher = pattern.matcher(annotation_id);
            if (matcher.matches()) {
                Integer gene_id = Integer.valueOf(annotation_id.substring(7));
                list.add(new Integer[] { gene_id, id });
                count++;
            } else {
                // log.debug("not matching 'GeneID:' - " + annotation_id);
                not_match++;
            }
        }
        log.debug("not matched rows " + not_match);
        log.debug("total gene id rows " + count);
        dashboardDao.storeRelatedCompounds(list);
        log.debug("RelatedCompoundsWriter called. size=" + items.size());
    }
}