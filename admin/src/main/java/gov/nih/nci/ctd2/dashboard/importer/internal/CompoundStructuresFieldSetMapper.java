package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.HashMap;

@Component("compoundStructuresMapper")
public class CompoundStructuresFieldSetMapper implements FieldSetMapper<Compound> {

	public static final String COMPOUND_IMAGE_DATABASE = "IMAGE";

	private static final String CPD_ID = "CPD_ID";
	private static final String	CPD_PRIMARY_NAME = "CPD_PRIMARY_NAME";
	private static final String	STRUCTURE_FILE = "STRUCTURE_FILE";

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private DashboardFactory dashboardFactory;

    private HashMap<String, Compound> compoundMap = new HashMap<String, Compound>();

	public Compound mapFieldSet(FieldSet fieldSet) throws BindException {

		String compoundId = fieldSet.readString(CPD_ID);
		String compoundName = fieldSet.readString(CPD_PRIMARY_NAME);
		String structureFile = fieldSet.readString(STRUCTURE_FILE);

		// find compound by xref (broad)
		Compound compound = compoundMap.get(compoundId);
		if (compound == null) {
			List<Subject> compounds =
				dashboardDao.findSubjectsByXref(CompoundsFieldSetMapper.BROAD_COMPOUND_DATABASE,
												compoundId);
			if (compounds.size() == 1) {
				compound = (Compound)compounds.iterator().next();
				compoundMap.put(compoundId, compound);
			}
		}
		if (compound != null) {
			Xref xref = dashboardFactory.create(Xref.class);
			xref.setDatabaseId(structureFile);
			xref.setDatabaseName(COMPOUND_IMAGE_DATABASE);
			compound.getXrefs().add(xref);
		}
		return compound;
	}
}
