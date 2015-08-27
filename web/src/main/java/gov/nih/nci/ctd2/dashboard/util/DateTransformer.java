package gov.nih.nci.ctd2.dashboard.util;

import flexjson.transformer.AbstractTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTransformer extends AbstractTransformer {
    @Override
    public void transform(Object object) {
        assert object instanceof Date;
        Date date = (Date) object;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, 20yy");
        getContext().writeQuoted(dateFormat.format(date));
    }
}
