package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal; 
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

 
public class ResultSetlUtil {

	public static final String DEL = "|";

	public static final String REGEX_DEL = "\\|";

	public static final int SPLIT_ALL = -2;
	public static final String NULL_STR = "null";
	public static final BigDecimal NULL_BIGDECIMAL = new BigDecimal(0);

	private static String INTERACTIONS_SERVLET_URL = null;
	 
	private static int urlConnectionTimeout = 3000;

	private TreeMap<String, Integer> metaMap;
	private String[] row;
	private String decodedString;
	private BufferedReader in;

	public ResultSetlUtil(BufferedReader in) throws IOException {
		this.in = in;
		metaMap = new TreeMap<String, Integer>();

		// metadata
		next();

		processMetadata();
	}

	public static void setUrl(String aUrl) {
		INTERACTIONS_SERVLET_URL = aUrl;
	}

	public static String getUrl() {
		return INTERACTIONS_SERVLET_URL;
	}

	public static void setTimeout(int timeout) {
		urlConnectionTimeout = timeout;
	}

	// reconstruct metadata
	public void processMetadata() {
		if (row == null)
			return;
		for (int i = 0; i < row.length; i++) {
			metaMap.put(row[i], new Integer(i + 1));
		}
		return;
	}

	public int getColumNum(String name) {
		Integer ret = metaMap.get(name);
		if (ret != null)
			return ret.intValue();
		else
			return -1;
	}

	public double getDouble(String colmName) {
		int columNum = getColumNum(colmName);

		return getDouble(columNum);
	}

	public double getDouble(int colmNum) {
		double ret = 0;

		String tmp = getString(colmNum).trim();

		if (!tmp.equals(NULL_STR)) {
			ret = Double.valueOf(tmp).doubleValue();
		}

		return ret;
	}

	public BigDecimal getBigDecimal(String colmName) {
		int columNum = getColumNum(colmName);
		return getBigDecimal(columNum);
	}

	public BigDecimal getBigDecimal(int colmNum) {
		BigDecimal ret = NULL_BIGDECIMAL;

		String tmp = getString(colmNum).trim();
		if (!tmp.equals(NULL_STR)) {
			ret = new BigDecimal(tmp);
		}

		return ret;
	}

	public String getString(String colmName) {
		int coluNum = getColumNum(colmName);
		if (coluNum == -1)
			return null;
		return getString(coluNum);
		 
	}

	public String getString(int colmNum) {
		// get from row
		return row[colmNum - 1];
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

	public static HttpURLConnection getConnection(String url)
			throws IOException {
		URL aURL = new URL(url);
		HttpURLConnection aConnection = (HttpURLConnection) (aURL
				.openConnection());
		aConnection.setDoOutput(true);
		aConnection.setConnectTimeout(urlConnectionTimeout);
		return aConnection;
	}
	
	public static ResultSetlUtil executeQuery(String methodAndParams,
			String urlStr) throws IOException, UnAuthenticatedException {
	
		    return executeQueryWithUserInfo(methodAndParams, urlStr, null);
	}

	public static ResultSetlUtil executeQueryWithUserInfo(String methodAndParams,
			String urlStr, String userInfo) throws IOException, UnAuthenticatedException {

		HttpURLConnection aConnection = getConnection(urlStr);

		if (userInfo != null && userInfo.trim().length() != 0)
		{			
			aConnection.setRequestProperty("Authorization",
			                            "Basic " + DatatypeConverter.printBase64Binary(userInfo.getBytes()));			
		 
		}
		OutputStreamWriter out = new OutputStreamWriter(
				aConnection.getOutputStream());

		out.write(methodAndParams);
		out.close();

		// errors, exceptions

		int respCode = aConnection.getResponseCode();

		if (respCode == HttpURLConnection.HTTP_UNAUTHORIZED)
			throw new UnAuthenticatedException("server response code = "
					+ respCode);

		if ((respCode == HttpURLConnection.HTTP_BAD_REQUEST)
				|| (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {
			throw new IOException("server response code = " + respCode
					+ ", see server logs");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				aConnection.getInputStream()));

		ResultSetlUtil rs = new ResultSetlUtil(in);

		return rs;
	}
	
	
	
	

	// test
	public static void main(String[] args) {

		ResultSetlUtil rs = null;
		try {
			Properties iteractionsProp = new Properties();
			iteractionsProp.load(new FileInputStream(
					"conf/application.properties"));
			String interactionsServletUrl = iteractionsProp
					.getProperty("interactions_servlet_url");
			interactionsServletUrl = "http://localhost:8080/InteractionsServlet/InteractionsServlet";
			ResultSetlUtil.setUrl(interactionsServletUrl);
		 

			String aSQL = "getInteractionsByEntrezIdOrGeneSymbol" + DEL + "165" + DEL + "BCi"
					+ DEL + "1.0";
			int i = 165;

			aSQL = "getInteractionsByEntrezIdOrGeneSymbol" + DEL + i + DEL +"geneName"+ DEL +  "HGi_Sun" + DEL
					+ "1.0";

			rs = ResultSetlUtil.executeQuery(aSQL, INTERACTIONS_SERVLET_URL);

			while (rs.next()) {

				BigDecimal msid1 = rs.getBigDecimal("ms_id1");
				System.out.println("msid1 = " + msid1);

				BigDecimal msid2 = rs.getBigDecimal("ms_id2");
				System.out.println("msid2 = " + msid2);

				BigDecimal confidenceValue = rs
						.getBigDecimal("confidence_value");
				// double confidenceValue = rs.getDouble("confidence_value");
				System.out.println("confidence_value = " + confidenceValue);

				String interactionType = rs.getString("interaction_type");
				System.out.println("name = " + interactionType);
			}

		} catch (IOException ie) {
			// TODO Auto-generated catch block
			ie.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();

				} catch (Exception e) {
				}

			}
			System.exit(0);
		}
	}

}
