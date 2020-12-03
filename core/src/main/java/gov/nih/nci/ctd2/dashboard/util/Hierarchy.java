package gov.nih.nci.ctd2.dashboard.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum Hierarchy {
    DISEASE_CONTEXT("disease_context_hierarchy.txt"), EXPERIMENTAL_EVIDENCE("");

    final private Map<Integer, int[]> map;

    private Hierarchy(String filename) {
        map = new HashMap<Integer, int[]>();
        if (filename.length() == 0) // TODO
            return;
        try {
            URI txtFileUri = Hierarchy.class.getClassLoader().getResource(filename).toURI();

            // Path path = Path.of(txtFileUri)
            /*
             * this following approach is necessary only because all resrouce files will be
             * inside the jar file.
             */
            final String[] array = txtFileUri.toString().split("!");
            final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), new HashMap<>());
            final Path path = fs.getPath(array[1]);

            String content = Files.readString(path, Charset.defaultCharset());
            fs.close();
            content.lines().forEach(line -> {
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
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // just in case we care of the full code with the leading 'C' for some reason
    public String[] getDiseaseContextChildrenFullCode(String parent) {
        Integer p = Integer.valueOf(parent.substring(1));
        return Stream.of(map.get(p)).map(x -> "C" + x).toArray(String[]::new);
    }

    public int[] getChildrenCode(int parent) {
        int[] children = map.get(parent);
        if (children == null)
            return new int[0];
        return children;
    }
}
