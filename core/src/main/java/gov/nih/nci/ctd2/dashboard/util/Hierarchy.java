package gov.nih.nci.ctd2.dashboard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Hierarchy {
    DISEASE_CONTEXT("disease_context_hierarchy.txt"), EXPERIMENTAL_EVIDENCE("experimental_evidence_hierarchy.txt");

    private static final Log log = LogFactory.getLog(Hierarchy.class);

    final private Map<Integer, int[]> map;

    private Hierarchy(String filename) {
        map = new HashMap<Integer, int[]>();
        InputStream inputStream = Hierarchy.class.getClassLoader().getResourceAsStream(filename);
        try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] x = line.split(" ");
                int child = Integer.parseInt(x[0]);
                int parent = Integer.parseInt(x[1]);
                int[] children = map.get(parent);
                if (children == null) {
                    children = new int[0];
                }
                int[] new_children = new int[children.length + 1];
                System.arraycopy(children, 0, new_children, 0, children.length);
                new_children[children.length] = child;
                map.put(parent, new_children);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return super.toString() + ": parent number " + map.size() + "; total children "
                + map.values().stream().reduce(0, (total, x) -> total + x.length, Integer::sum);
    }

    // export as a tree structure
    public Node getTree(Map<Integer, Integer> observations) {
        Map<Integer, Node> nodes = new HashMap<Integer, Node>();
        for (Integer key : map.keySet()) {
            int[] id_children = map.get(key);
            List<Node> children = new ArrayList<Node>();
            for (int child : id_children) {
                children.add(new Node(String.valueOf(child)));
            }
            nodes.put(key, new Node(String.valueOf(key), children));
        }

        Set<Node> to_be_removed = new HashSet<Node>();

        for (Integer key : nodes.keySet()) {
            final Node node = nodes.get(key);
            final List<Node> nodes_to_remove = new ArrayList<Node>();
            final List<Node> nodes_to_add = new ArrayList<Node>();
            for (Node child : node.children) {
                int child_id = Integer.valueOf(child.name);
                Node x = nodes.get(child_id);
                if (x != null && !to_be_removed.contains(x)) {
                    nodes_to_remove.add(child);
                    nodes_to_add.add(x);
                    to_be_removed.add(x);
                }
            }
            for (Node x : nodes_to_remove) {
                node.children.remove(x);
            }
            for (Node x : nodes_to_add) {
                node.children.add(x);
            }
        }

        Collection<Node> top_nodes = nodes.values();
        top_nodes.removeAll(to_be_removed);
        Node tree = new Node("root", new ArrayList<Node>(top_nodes));

        if (observations != null) {
            int c = 0;
            for (Node child : tree.children) {
                setCount(child, observations);
                c += child.observations;
            }
            tree.observations = c;
            filter(tree);
        }
        if (tree.children.size() == 1) { /* avoid unnecessary root node. it is in fact the case of evidence type */
            tree = tree.children.get(0);
        }

        log.debug("top level: " + tree.children.size());
        log.debug("total nodes: " + tree.totalSize());
        return tree;
    }

    static private void setCount(Node node, Map<Integer, Integer> observations) {
        int c = 0;
        Integer x = observations.get(Integer.parseInt(node.name));
        if (x != null) {
            c = x;
        }
        for (Node child : node.children) {
            setCount(child, observations);
            c += child.observations;
        }
        node.observations = c;
    }

    static private void filter(Node node) {
        List<Node> to_remove = new ArrayList<Node>();
        for (Node child : node.children) {
            filter(child);
            if (child.observations == 0) {
                to_remove.add(child);
            }
        }
        for (Node x : to_remove) {
            node.children.remove(x);
        }
    }

    // create a flat map for looking up all descendants quickly
    public Map<Integer, List<Integer>> flatten(final List<Integer> observed) {
        final Map<Integer, List<Integer>> flatMap = new HashMap<Integer, List<Integer>>();
        for (int key : map.keySet()) {
            // *searched* set is parent-specific. we do need to search even if it has been
            // searched for another parent
            final Set<Integer> searched = new HashSet<Integer>();
            final List<Integer> observedDescendants = new ArrayList<Integer>();
            observed(key, observedDescendants, observed, searched);
            if (observedDescendants.size() > 0)
                flatMap.put(key, observedDescendants);
        }
        return flatMap;
    }

    private void observed(int x, final List<Integer> observedDescendants, final List<Integer> allObserved,
            final Set<Integer> searched) {
        if (searched.contains(x)) {
            return;
        }
        searched.add(x);
        if (allObserved.contains(x)) {
            observedDescendants.add(x);
        }
        int[] v = map.get(x);
        if (v == null) { // x is a leaf
            return;
        }
        for (int a : v) {
            observed(a, observedDescendants, allObserved, searched);
        }
    }

    // just in case we care of the full code with the leading 'C' for some reason
    public String[] getDiseaseContextChildrenFullCode(String parent) {
        Integer p = Integer.valueOf(parent.substring(1));
        return Stream.of(map.get(p)).map(x -> "C" + x).toArray(String[]::new);
    }

    // in case we need the full code with the leading 'ECO:' plus 7 digits
    public String[] getExperimentalEvidenceChildrenFullCode(String parent) {
        Integer p = Integer.valueOf(parent.substring(4));
        return Stream.of(map.get(p)).map(x -> String.format("ECO:%07d", x)).toArray(String[]::new);
    }

    public int[] getChildrenCode(int parent) {
        int[] children = map.get(parent);
        if (children == null)
            return new int[0];
        return children;
    }
}
