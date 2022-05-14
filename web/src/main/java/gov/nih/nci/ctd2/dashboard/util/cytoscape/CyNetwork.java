package gov.nih.nci.ctd2.dashboard.util.cytoscape;

import java.util.ArrayList;
import java.util.List;

public class CyNetwork {
    List<CyNode> nodes = new ArrayList<CyNode>();
    List<Edge> edges = new ArrayList<Edge>();
    List<CyInteraction> interactions =  new ArrayList<CyInteraction>();

    public List<CyNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<CyNode> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    
    public List<CyInteraction> getInteractions() {
        return this.interactions;
    }  
    
    public void setInteractions(List<CyInteraction> interactions) {
        this.interactions = interactions;
    }
    
    public boolean addEdge(Edge edge) {
        return getEdges().add(edge);
    }

    public boolean addNode(CyNode node) {
        return getNodes().add(node);
    }
}
