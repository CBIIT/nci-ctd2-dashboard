package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Xref;

@Component("xrefDataWriter")
public class XrefDataWriter implements ItemWriter<XrefData> {

    @Autowired
    private DashboardDao dashboardDao;

    public void write(List<? extends XrefData> items) throws Exception {
        Map<Integer, Subject> all = new HashMap<Integer, Subject>();
        for (XrefData item : items) {
            List<? extends Subject> subjects = item.subjects;
            if (subjects == null || subjects.size() == 0) {
                continue;
            }

            Xref xref = item.xref;
            for (Subject subject : subjects) {
                Integer subjectId = subject.getId();
                Subject x = all.get(subjectId);
                if (x == null) {
                    x = subject;
                    all.put(subjectId, x);
                }
                x.getXrefs().add(xref);
            }
        }
        dashboardDao.batchMerge(all.values());
    }
}
