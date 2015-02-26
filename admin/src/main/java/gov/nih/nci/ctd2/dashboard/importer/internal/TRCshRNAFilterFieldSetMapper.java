package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.ShRna;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;

@Component("TRCshRNAFilterMapper")
public class TRCshRNAFilterFieldSetMapper implements FieldSetMapper<ShRna> {

    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	@Qualifier("TRCshRNAFilterMap")
	private HashMap<String,String> tRCshRNAFilterMap;

	public ShRna mapFieldSet(FieldSet fieldSet) throws BindException {
		tRCshRNAFilterMap.put(fieldSet.readString(0), fieldSet.readString(0));
		// if we don't return something, spring batch will think EOF has been reached
		return dashboardFactory.create(ShRna.class);
	}
}
