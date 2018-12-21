package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.ArrayList;
import java.util.List;

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
        List<Subject> all = new ArrayList<Subject>();
        for (XrefData item : items) {
            List<? extends Subject> subjects = item.subjects;
            if (subjects == null || subjects.size() == 0) {
                continue;
            }

            Xref xref = item.xref;
            for (Subject subject : subjects) {
                subject.getXrefs().add(xref);
            }
            all.addAll(subjects);
        }
        dashboardDao.batchMerge(all);
    }
}
