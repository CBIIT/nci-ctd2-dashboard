package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Component("compoundSMILESMapper")
public class CompoundSMILESFieldSetMapper implements FieldSetMapper<Compound> {

    @Autowired
    private DashboardDao dashboardDao;

	public Compound mapFieldSet(FieldSet fieldSet) throws BindException {
		// find compound by xref (broad)
		//Xref broadXref = DashboardDao::getEntityByXref(Xref xref)
        Compound compound =  (Compound)dashboardDao.getEntityById(Compound.class, fieldSet.readInt(0));
		if (compound != null) {
			compound.setSmilesNotation(fieldSet.readString(1));
		}
		return compound;
	}
}
