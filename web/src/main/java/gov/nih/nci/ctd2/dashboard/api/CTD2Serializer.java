package gov.nih.nci.ctd2.dashboard.api;

import java.util.Date;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.util.DateTransformer;
import gov.nih.nci.ctd2.dashboard.util.ImplTransformer;

public class CTD2Serializer {
    static public JSONSerializer createJSONSerializer() {
        JSONSerializer jsonSerializer = new JSONSerializer().exclude("observation_count.class").exclude("xref.class")
                .exclude("observations.subject_list.xref.class").transform(new ImplTransformer(), Class.class)
                .transform(new DateTransformer(), Date.class).transform(new FieldNameTransformer("class"), "clazz")
                .transform(new FieldNameTransformer("class"), "observations.subject_list.clazz")
                .transform(new FieldNameTransformer("class"), "observations.evidence_list.clazz")
                .transform(new ExcludeTransformer(), void.class).exclude("class").exclude("observations.class");
        return jsonSerializer;
    }
}
