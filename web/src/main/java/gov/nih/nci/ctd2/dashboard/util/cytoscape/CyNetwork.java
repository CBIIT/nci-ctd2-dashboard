package gov.nih.nci.ctd2.dashboard.util.cytoscape;

import java.util.ArrayList;
import java.util.List;

public class CyNetwork {
    List<Element> edges = new ArrayList<Element>();
    List<CyInteraction> interactions = new ArrayList<CyInteraction>();

    public List<Element> getEdges() {
        return edges;
    }

    public void setEdges(List<Element> edges) {
        this.edges = edges;
    }

    public List<CyInteraction> getInteractions() {
        return this.interactions;
    }

    public void setInteractions(List<CyInteraction> interactions) {
        this.interactions = interactions;
    }

    public boolean addEdge(Element edge) {
        return getEdges().add(edge);
    }
}
