package gov.nih.nci.ctd2.dashboard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum Hierarchy {
    DISEASE_CONTEXT("disease_context_hierarchy.txt"), EXPERIMENTAL_EVIDENCE("experimental_evidence_hierarchy.txt");

    final private Map<Integer, int[]> map;

    private Hierarchy(String filename) {
        map = new HashMap<Integer, int[]>();
        if (filename.length() == 0) // TODO
            return;
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
