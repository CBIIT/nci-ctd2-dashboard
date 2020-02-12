package gov.nih.nci.ctd2.dashboard.importer.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;

@Component("ecotermDataMapper")
public class ECOTermDataFieldSetMapper implements FieldSetMapper<ECOTerm> {

    private static final Log log = LogFactory.getLog(ECOTermDataFieldSetMapper.class);

    @Autowired
    private DashboardFactory dashboardFactory;

    public ECOTerm mapFieldSet(FieldSet fieldSet) throws BindException {

        String name = fieldSet.readString("name");
        String code = fieldSet.readString("code");
        String definition = fieldSet.readString("definition");
        String synonyms = fieldSet.readString("synonyms");
        log.debug("synonyms: " + synonyms);

        ECOTerm ecoterm = dashboardFactory.create(ECOTerm.class);
        ecoterm.setDisplayName(name);
        ecoterm.setCode(code);
        ecoterm.setDefinition(definition);
        ecoterm.setSynonyms(synonyms);

        return ecoterm;
    }
}
