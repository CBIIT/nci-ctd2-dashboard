package gov.nih.nci.ctd2.dashboard.importer.internal;


import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Component("xrefMapper")
public class XrefFieldSetMapper implements FieldSetMapper<XrefData> {

	private static final String CLASS = "subject_class";
	private static final String NAME = "subject_name";
	private static final String	DB_NAME = "database_name";
	private static final String	DB_ID = "database_id";

	private static final Log log = LogFactory.getLog(XrefFieldSetMapper.class);
	
    @Autowired
    private DashboardFactory dashboardFactory;

    @Autowired
	private DashboardDao dashboardDao;

	public XrefData mapFieldSet(FieldSet fieldSet) throws BindException {

		String subjectClass = fieldSet.readString(CLASS);
		String subjectName = fieldSet.readString(NAME);
		String databaseName = fieldSet.readString(DB_NAME);
		String databaseId = fieldSet.readString(DB_ID);

		Xref xref = dashboardFactory.create(Xref.class);
		xref.setDatabaseName(databaseName);
		xref.setDatabaseId(databaseId);
		
		List<? extends Subject> subjects = findSubject(subjectClass, subjectName);
		if (subjects == null || subjects.size() == 0){
			log.warn("Subject not found: "+subjectName+" ("+subjectClass+")");
		}
		return new XrefData(xref, subjects);
	}
	
	private List<? extends Subject> findSubject(String subjectClass, String name) {
		switch (subjectClass) {
		case "animal_model": 
			return dashboardDao.findAnimalModelByName(name);
		case "cell_sample": 
			return dashboardDao.findCellLineByName(name);
		case "compound": 
			return dashboardDao.findCompoundsByName(name);
		case "gene": 
			return dashboardDao.findGenesBySymbol(name);
		case "protein": 
			return dashboardDao.findProteinsByUniprotId(name);
		case "shrna": 
			return dashboardDao.findSiRNAByTargetSequence(name);
		case "tissue_sample": 
			return dashboardDao.findTissueSampleByName(name);
		case "transcript": 
			return dashboardDao.findTranscriptsByRefseqId(name);
		}
		return null;
	}
}
