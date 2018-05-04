package gov.nih.nci.ctd2.dashboard.api;

import flexjson.transformer.AbstractTransformer;

public class ExcludeTransformer extends AbstractTransformer {
    @Override
    public Boolean isInline() {
        return true;
    }

    @Override
    public void transform(Object object) {
        // Do nothing, null objects are not serialized.
        return;
    }
}