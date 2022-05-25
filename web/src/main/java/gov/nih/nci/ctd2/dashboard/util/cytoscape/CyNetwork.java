package gov.nih.nci.ctd2.dashboard.util.cytoscape;

import java.util.ArrayList;
import java.util.List;

public class CyNetwork {
    List<Element> edges = new ArrayList<Element>();
    List<String> interactionTypes = new ArrayList<String>();

    public List<Element> getEdges() {
        return edges;
    }

    public void setEdges(List<Element> edges) {
        this.edges = edges;
    }

    public List<String> getInteractionTypes() {
        return this.interactionTypes;
    }

    public void setInteractions(List<String> interactionTypes) {
        this.interactionTypes = interactionTypes;
    }

    public boolean addEdge(Element edge) {
        return getEdges().add(edge);
    }
}
