package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component("compoundSMILESMapper")
public class CompoundSMILESFieldSetMapper implements FieldSetMapper<Compound> {

    @Autowired
    private DashboardDao dashboardDao;

	public Compound mapFieldSet(FieldSet fieldSet) throws BindException {
		// find compound by xref (broad)
		List<Subject> compounds =
			dashboardDao.findSubjectsByXref(CompoundNamesFieldSetMapper.BROAD_COMPOUND_DATABASE,
											fieldSet.readString(0));
		if (compounds.size() == 1) {
			Compound compound = (Compound)compounds.iterator().next();
			compound.setSmilesNotation(fieldSet.readString(1));
			return compound;
		}
		else {
			return null;
		}
	}
}
