package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Component("compoundNamesMapper")
public class CompoundNamesFieldSetMapper implements FieldSetMapper<CompoundData> {

    @Autowired
    private DashboardFactory dashboardFactory;

	public CompoundData mapFieldSet(FieldSet fieldSet) throws BindException {
		Compound compound = dashboardFactory.create(Compound.class);
        compound.setDisplayName(fieldSet.readString(1));
		// create xref back to broad
		String xrefId = fieldSet.readInt(0);
		return new CompoundData(compound, fieldSet.readString(2));
	}
}
