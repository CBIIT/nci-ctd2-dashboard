package gov.nih.nci.ctd2.dashboard.util;

import flexjson.transformer.AbstractTransformer;

public class ImplTransformer extends AbstractTransformer {
    @Override
    public void transform(Object object) {
        assert object instanceof Class;
        getContext().writeQuoted(((Class) object).getSimpleName().replace("Impl", ""));
    }
}
