package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("compoundNamesProcessor")
public class CompoundNamesProcessor implements ItemProcessor<CompoundData, Compound> {

	// the reference metadata worksheet can contain environment vars
	private static final Pattern PRIMARY_COMMON_TYPE = Pattern.compile("primary-common");
 
    @Override
    public Compound process(CompoundData compoundData) throws Exception {
		return (PRIMARY_COMMON_TYPE.matcher(compoundData.compoundType).find()) ? compoundData.compound : null;
	}
}