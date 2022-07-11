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

@Component("relatedCompoundsWriter")
public class RelatedCompoundsWriter implements ItemWriter<String[]> {

    @Autowired
    private DashboardDao dashboardDao;

    private static final Log log = LogFactory.getLog(RelatedCompoundsWriter.class);

    public void write(List<? extends String[]> items) throws Exception {
        final Pattern pattern = Pattern.compile("GeneID:\\d+");
        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        int count = 0;
        int not_match = 0;

        for (String[] x : items) {
            String cpd_id = x[0];
            String annotation_id = x[2];
            Matcher matcher = pattern.matcher(annotation_id);
            if (matcher.matches()) {
                Integer gene_id = Integer.valueOf(annotation_id.substring(7));
                Set<Integer> s = map.get(gene_id);
                if (s == null) {
                    s = new HashSet<Integer>();
                    map.put(gene_id, s);
                }
                s.add(Integer.valueOf(cpd_id));
                count++;
            } else {
                // log.debug("not matching 'GeneID:' - " + annotation_id);
                not_match++;
            }
        }
        log.debug("not matched rows " + not_match);
        log.debug("total gene id rows " + count);
        log.debug("unique gene ids " + map.size());
        int gene_id_count = 0;
        int total_rows = 0;
        List<Integer[]> list = new ArrayList<Integer[]>();
        for (Integer gene_id : map.keySet()) {
            Set<Integer> set = map.get(gene_id);
            if (set.size() == 1)
                continue; // no related

            for (Integer cpd_id : set) {
                list.add(new Integer[] { gene_id, cpd_id });
                total_rows++;
            }
            gene_id_count++;
        }
        log.debug("number of gene id used " + gene_id_count);
        log.debug("total rows to database: " + total_rows);
        dashboardDao.storeRelatedCompounds(list);
        log.debug("RelatedCompoundsWriter called. size=" + items.size());
    }
}
