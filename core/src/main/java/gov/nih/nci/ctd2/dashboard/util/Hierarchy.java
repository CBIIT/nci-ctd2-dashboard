package gov.nih.nci.ctd2.dashboard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Hierarchy {
    DISEASE_CONTEXT("disease_context_hierarchy.txt"), EXPERIMENTAL_EVIDENCE("experimental_evidence_hierarchy.txt");

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

    // TODO test
    public Map<Integer, int[]> map() {
        return map;
    }

    /* return the top nodes, namely those who are not children of any other nodes */
    public List<Integer> topNodes() {
        List<Integer> tops = new ArrayList<Integer>();
        for (int key : map.keySet()) {
            boolean found = false;
            for (int x : map.keySet()) {
                int[] children = map.get(x);
                if (Arrays.stream(children).anyMatch(a -> a == key)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                tops.add(key);
            }
        }
        return tops;
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
