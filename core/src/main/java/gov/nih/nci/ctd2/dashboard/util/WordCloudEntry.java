package gov.nih.nci.ctd2.dashboard.util;

public class WordCloudEntry {
    final public String key;
    final public int value;
    final public String url;
    final public String fullname;

    public WordCloudEntry(String name, int count, String stableURL, String fullname) {
        key = name;
        value = count;
        url = stableURL;
        this.fullname = fullname;
    }
}
