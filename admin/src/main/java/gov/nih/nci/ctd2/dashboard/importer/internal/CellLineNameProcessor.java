package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;

@Component("cellLineNameProcessor")
public class CellLineNameProcessor implements ItemProcessor<CellSample, CellSample> {

    @Autowired
	@Qualifier("cellLineNameTypeMap")
	private HashMap<String,String> cellLineNameTypeMap;

    @Override
    public CellSample process(CellSample cellSample) throws Exception {
		int priority = Integer.MAX_VALUE;
		for (Xref xRef : cellSample.getXrefs()) {
			int thisXrefPriority = getPriority(xRef.getDatabaseName());
			if (thisXrefPriority < priority) {
				cellSample.setDisplayName(xRef.getDatabaseId());
				priority = thisXrefPriority;
			}
		}
		return cellSample;
	}

	private int getPriority(String cellNameType) {
		for (String key : cellLineNameTypeMap.keySet()) {
			// [0] is name type, [1] is name type priority
			String[] cellNameTypePair = cellLineNameTypeMap.get(key)
				.split(CellLineNameTypeFieldSetMapper.CELL_NAME_TYPE_MAP_DELIMITER);
			if (cellNameTypePair.length == 2 &&
				cellNameTypePair[0].equals(cellNameType)) return new Integer(cellNameTypePair[1]);
		}
		return Integer.MAX_VALUE;
	}
}
