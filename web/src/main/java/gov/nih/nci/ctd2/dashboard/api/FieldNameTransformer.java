package gov.nih.nci.ctd2.dashboard.api;

import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;

public class FieldNameTransformer extends AbstractTransformer {
    private String transformedFieldName;

    public FieldNameTransformer(String transformedFieldName) {
        this.transformedFieldName = transformedFieldName;
    }

    public void transform(Object object) {
        boolean setContext = false;

        TypeContext typeContext = getContext().peekTypeContext();

        // Write comma before starting to write field name if this
        // isn't first property that is being transformed
        if (!typeContext.isFirst())
            getContext().writeComma();

        typeContext.setFirst(false);

        getContext().writeName(getTransformedFieldName());
        getContext().writeQuoted(object.toString());

        if (setContext) {
            getContext().writeCloseObject();
        }
    }

    /***
     * TRUE tells the JSONContext that this class will be handling the writing of
     * our property name by itself.
     */
    @Override
    public Boolean isInline() {
        return Boolean.TRUE;
    }

    public String getTransformedFieldName() {
        return this.transformedFieldName;
    }
}
