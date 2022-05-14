package gov.nih.nci.ctd2.dashboard.util.cytoscape;

import java.util.HashMap;
import java.util.Map;

/* data element for cytoscape. it can be either node or edge */
public class Element {
    public static final String ID = "id";
    public static final String SOURCE = "source";
    public static final String TARGET = "target";
    public static final String WEIGHT = "weight";
    public static final String COLOR = "color";
    public static final String SHAPE = "shape"; // only used in MRA view

    private Map<String, Object> data = new HashMap<String, Object>();

    public Map<String, Object> getData() {
        return data;
    }

    public static Element createNode(String id) {
        Element instance = new Element();
        instance.data.put(Element.ID, id);
        return instance;
    }

    public static Element createEdge(String id, String source, String target) {
        Element instance = new Element();
        instance.data.put(Element.ID, id);
        instance.data.put(SOURCE, source);
        instance.data.put(TARGET, target);
        return instance;
    }

    public void setProperty(String property, Object value) {
        data.put(property, value);
    }

    public Object getProperty(String property) {
        return data.get(property);
    }
}
