package gov.nih.nci.ctd2.dashboard.util.cytoscape;

import java.util.HashMap;
import java.util.Map;

public class CyElement {
    public static final String ID = "id";
    public static final String SOURCE = "source";
    public static final String TARGET = "target";

    Map<String, Object> data = new HashMap<String, Object>();

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Object setProperty(String property, Object value) {
        return getData().put(property, value);
    }
}
