package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

public class ResultSetlUtil {
    private static final String REGEX_DEL = "\\|";
    private static final int SPLIT_ALL = -2;
    private static final String NULL_STR = "null";

    private static String INTERACTIONS_SERVLET_URL = null;

    private final static int urlConnectionTimeout = 3000;

    private final TreeMap<String, Integer> metaMap;
    private String[] row;
    private String decodedString;
    private final BufferedReader in;

    public ResultSetlUtil(final BufferedReader in) throws IOException {
        this.in = in;
        metaMap = new TreeMap<String, Integer>();

        // metadata
        next();

        if (row == null)
            return;
        for (int i = 0; i < row.length; i++) {
            metaMap.put(row[i], new Integer(i + 1));
        }
    }

    public static void setUrl(final String aUrl) {
        INTERACTIONS_SERVLET_URL = aUrl;
    }

    private int getColumNum(final String name) {
        final Integer ret = metaMap.get(name);
        if (ret != null)
            return ret.intValue();
        else
            return -1;
    }

    public double getDouble(final String colmName) {
        final int columNum = getColumNum(colmName);

        double ret = 0;

        final String tmp = row[columNum - 1].trim();

        if (!tmp.equals(NULL_STR)) {
            ret = Double.valueOf(tmp).doubleValue();
        }

        return ret;
    }

    public String getString(final String colmName) {
        final int coluNum = getColumNum(colmName);
        if (coluNum == -1)
            return null;
        return row[coluNum - 1];
    }

    public boolean next() throws IOException {
        boolean ret = false;
        decodedString = in.readLine();

        if (decodedString != null && !decodedString.trim().equals("")) {
            row = decodedString.split(REGEX_DEL, SPLIT_ALL);
            ret = true;
        }

        return ret;
    }

    public void close() throws IOException {
        in.close();
    }

    public static ResultSetlUtil executeQuery(final String methodAndParams)
            throws IOException, UnAuthenticatedException {

        return executeQueryWithUserInfo(methodAndParams, null);
    }

    public static ResultSetlUtil executeQueryWithUserInfo(final String methodAndParams, final String userInfo)
            throws IOException, UnAuthenticatedException {

        final URL aURL = new URL(INTERACTIONS_SERVLET_URL);
        final HttpURLConnection aConnection = (HttpURLConnection) (aURL.openConnection());
        aConnection.setDoOutput(true);
        aConnection.setConnectTimeout(urlConnectionTimeout);

        if (userInfo != null && userInfo.trim().length() != 0) {
            aConnection.setRequestProperty("Authorization",
                    "Basic " + DatatypeConverter.printBase64Binary(userInfo.getBytes()));

        }
        final OutputStreamWriter out = new OutputStreamWriter(aConnection.getOutputStream());

        out.write(methodAndParams);
        out.close();

        // errors, exceptions
        final int respCode = aConnection.getResponseCode();

        if (respCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            throw new UnAuthenticatedException("server response code = " + respCode);

        if ((respCode == HttpURLConnection.HTTP_BAD_REQUEST) || (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {
            throw new IOException("server response code = " + respCode + ", see server logs");
        }

        final BufferedReader in = new BufferedReader(new InputStreamReader(aConnection.getInputStream()));

        final ResultSetlUtil rs = new ResultSetlUtil(in);

        return rs;
    }
}
