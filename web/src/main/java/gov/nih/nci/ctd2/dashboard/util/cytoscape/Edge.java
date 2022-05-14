package gov.nih.nci.ctd2.dashboard.util.cytoscape;

import java.util.HashMap;
import java.util.Map;

public class Edge {
    public static final String ID = "id";
    public static final String SOURCE = "source";
    public static final String TARGET = "target";
    public static final String WEIGHT = "weight";
    public static final String COLOR = "color";

    private Map<String, Object> data = new HashMap<String, Object>();

    public Map<String, Object> getData() {
        return data;
    }

    public void setProperty(String property, Object value) {
        data.put(property, value);
    }

    public Object getProperty(String property) {
        return data.get(property);
    }
}
