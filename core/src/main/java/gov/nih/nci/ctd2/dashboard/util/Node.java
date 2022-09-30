package gov.nih.nci.ctd2.dashboard.util;

import java.util.ArrayList;
import java.util.List;

/* tree node for hierarchy */
public class Node {
    public final String name;
    public final List<Node> children;
    public int observations;
    public String label;

    public Node(String name, List<Node> children) {
        this.name = name;
        this.children = children;
    }

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<Node>();
    }

    /* total number of nodes, include this node itself and all the decedents */
    public int totalSize() {
        int i = 1;
        for (Node x : children) {
            i += x.totalSize();
        }
        return i;
    }
}
