package gov.nih.nci.ctd2.dashboard.api;

import java.io.Serializable;

public class XRefItem implements Serializable {
    private static final long serialVersionUID = 5390596880948240407L;

    public final String source, id;

    public XRefItem(String s, String i) {
        source = s;
        id = i;
    }
}
