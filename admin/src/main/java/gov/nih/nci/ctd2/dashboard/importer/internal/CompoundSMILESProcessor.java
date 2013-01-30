package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("compoundSMILESProcessor")
public class CompoundSMILESProcessor implements ItemProcessor<Compound, Compound> {

    @Override
    public Compound process(Compound compound) throws Exception {
		return (compound != null) ? compound : null;
	}
}