package gov.nih.nci.ctd2.dashboard.util.cnkb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/* This is based on InteractionsConnectionImpl in CNKB (interactions) component
 * to fix the issue of dependency on 'current dataset' */
/**
 * The class to query CNKB database via servlet.
 * 
 */
public class CNKB {

	private static final Log logger = LogFactory.getLog(CNKB.class);

	private HashMap<String, String> interactionTypeMap = null;

	private static class Constants {
		static String DEL = "|";
	};

	static private CNKB instance = null;

	private CNKB() {
		
	};

	public static CNKB getInstance(String interactionsServletUrl) {
		if (instance == null)
		{
			instance = new CNKB();
			ResultSetlUtil.setUrl(interactionsServletUrl);
		}
		return instance;
	}

	// query only by one gene
	public List<InteractionDetail> getInteractionsByGeneSymbol(
			String geneSymbol, String context, String version)
			throws UnAuthenticatedException, ConnectException,
			SocketTimeoutException, IOException {

		 return getInteractionsByGeneSymbolAndLimit(geneSymbol, context, version, null);
	}

	public List<String> getInteractionsSifFormat(String context,
			String version, String interactionType, String presentBy)
			throws UnAuthenticatedException, ConnectException,
			SocketTimeoutException, IOException {

		List<String> arrayList = new ArrayList<String>();

		String methodAndParams = "getInteractionsSifFormat" + Constants.DEL
				+ context + Constants.DEL + version + Constants.DEL
				+ interactionType + Constants.DEL + presentBy;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		String sifLine = null;
		while (rs.next()) {
			try {
				sifLine = rs.getString("sif format data");
				arrayList.add(sifLine);
			} catch (NullPointerException npe) {
				if (logger.isErrorEnabled()) {
					logger.error("db row is dropped because a NullPointerException");
				}
			}
		}
		rs.close();

		return arrayList;
	}

	public List<String> getInteractionsAdjFormat(String context,
			String version, String interactionType, String presentBy)
			throws UnAuthenticatedException, ConnectException,
			SocketTimeoutException, IOException {

		List<String> arrayList = new ArrayList<String>();

		String methodAndParams = "getInteractionsAdjFormat" + Constants.DEL
				+ context + Constants.DEL + version + Constants.DEL
				+ interactionType + Constants.DEL + presentBy;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		String adjLine = null;
		while (rs.next()) {
			try {
				adjLine = rs.getString("adj format data");
				arrayList.add(adjLine);
			} catch (NullPointerException npe) {
				if (logger.isErrorEnabled()) {
					logger.error("db row is dropped because a NullPointerException");
				}
			}
		}
		rs.close();

		return arrayList;
	}

	public HashMap<String, String> getInteractionTypeMap()
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {

		if (interactionTypeMap != null)
			return interactionTypeMap;
		interactionTypeMap = new HashMap<String, String>();
		String methodAndParams = "getInteractionTypes";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			String interactionType = rs.getString("interaction_type").trim();
			String short_name = rs.getString("short_name").trim();

			interactionTypeMap.put(interactionType, short_name);
			interactionTypeMap.put(short_name, interactionType);
		}
		rs.close();

		return interactionTypeMap;
	}

	public HashMap<String, String> getInteractionEvidenceMap()
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {
		HashMap<String, String> map = new HashMap<String, String>();

		String methodAndParams = "getInteractionEvidences";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			String evidenceDesc = rs.getString("description");
			String evidenceId = rs.getString("id");

			map.put(evidenceId, evidenceDesc);
			map.put(evidenceDesc, evidenceId);
		}
		rs.close();

		return map;
	}

	public HashMap<String, String> getConfidenceTypeMap()
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {
		HashMap<String, String> map = new HashMap<String, String>();

		String methodAndParams = "getConfidenceTypes";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			String confidenceType = rs.getString("name").trim();
			String id = rs.getString("id").trim();

			map.put(confidenceType, id);
			map.put(id, confidenceType);
		}
		rs.close();

		return map;
	}

	public List<String> getInteractionTypes() throws ConnectException,
			SocketTimeoutException, IOException, UnAuthenticatedException {
		List<String> arrayList = new ArrayList<String>();

		String methodAndParams = "getInteractionTypes";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			String interactionType = rs.getString("interaction_type").trim();

			arrayList.add(interactionType);
		}
		rs.close();

		return arrayList;
	}

	public List<String> getInteractionTypesByInteractomeVersion(String context,
			String version) throws ConnectException, SocketTimeoutException,
			IOException, UnAuthenticatedException {
		List<String> arrayList = new ArrayList<String>();

		String methodAndParams = "getInteractionTypesByInteractomeVersion"
				+ Constants.DEL + context + Constants.DEL + version;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			String interactionType = rs.getString("interaction_type").trim();

			arrayList.add(interactionType);
		}
		rs.close();

		return arrayList;
	}

	public String getInteractomeDescription(String interactomeName)
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {

		String interactomeDesc = null;

		String methodAndParams = "getInteractomeDescription" + Constants.DEL
				+ interactomeName;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());
		while (rs.next()) {
			interactomeDesc = rs.getString("description").trim();
			break;
		}
		rs.close();

		return interactomeDesc;
	}

	public ArrayList<String> getDatasetAndInteractioCount()
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {
		ArrayList<String> arrayList = new ArrayList<String>();

		String datasetName = null;
		int interactionCount = 0;

		String methodAndParams = "getDatasetNames";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			datasetName = rs.getString("name").trim();
			interactionCount = (int) rs.getDouble("interaction_count");
			arrayList.add(datasetName + " (" + interactionCount
					+ " interactions)");
		}
		rs.close();

		return arrayList;
	}

	public ArrayList<String> getNciDatasetAndInteractioCount()
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {
		ArrayList<String> arrayList = new ArrayList<String>();

		String datasetName = null;
		int interactionCount = 0;

		String methodAndParams = "getNciDatasetNames";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		while (rs.next()) {

			datasetName = rs.getString("name").trim();
			interactionCount = (int) rs.getDouble("interaction_count");
			arrayList.add(datasetName + " (" + interactionCount
					+ " interactions)");
		}
		rs.close();

		return arrayList;
	}

	public List<VersionDescriptor> getVersionDescriptor(String interactomeName)
			throws ConnectException, SocketTimeoutException, IOException,
			UnAuthenticatedException {
		List<VersionDescriptor> arrayList = new ArrayList<VersionDescriptor>();

		String methodAndParams = "getVersionDescriptor" + Constants.DEL
				+ interactomeName;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());
		while (rs.next()) {
			String version = rs.getString("version").trim();
			if (version.equalsIgnoreCase("DEL"))
				continue;
			String value = rs.getString("authentication_yn").trim();
			boolean needAuthentication = false;
			if (value.equalsIgnoreCase("Y")) {
				needAuthentication = true;
			}
			String versionDesc = rs.getString("description").trim();
			VersionDescriptor vd = new VersionDescriptor(version,
					needAuthentication, versionDesc);
			arrayList.add(vd);
		}
		rs.close();

		return arrayList;
	}

	public double getThrottleValue(String geneSymbols, String context,
			String version, int rowLimit) throws UnAuthenticatedException,
			ConnectException, SocketTimeoutException, IOException {

		String methodAndParams = "getThrottleValue" + Constants.DEL
				+ geneSymbols + Constants.DEL + context + Constants.DEL
				+ version + Constants.DEL + rowLimit;

		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams,
				ResultSetlUtil.getUrl());

		double throttle = 0;
		while (rs.next()) {
			throttle = rs.getDouble("confidence_value");
			break;
		}
		rs.close();

		return throttle;
	}

	// could query by a list of gene, but limit by number of rows
	public List<InteractionDetail> getInteractionsByGenesAndLimit(
			String geneSymbols, String context, String version, int limit,
			String userInfo) throws UnAuthenticatedException, ConnectException,
			SocketTimeoutException, IOException {

		List<InteractionDetail> arrayList = new ArrayList<InteractionDetail>();

		String marker_geneName = geneSymbols;

		String methodAndParams = "getInteractionsByGenesAndLimit"
				+ Constants.DEL + marker_geneName + Constants.DEL + context
				+ Constants.DEL + version + Constants.DEL + limit;

		ResultSetlUtil rs = ResultSetlUtil.executeQueryWithUserInfo(
				methodAndParams, ResultSetlUtil.getUrl(), userInfo);

		String previousInteractionId = null;

		InteractionDetail interactionDetail = null;
		while (rs.next()) {
			try {
				String msid2 = rs.getString("primary_accession");
				String geneName2 = rs.getString("gene_symbol");

				if (geneName2 == null || geneName2.trim().equals("")
						|| geneName2.trim().equals("null"))
					geneName2 = msid2;
				String interactionType = rs.getString("interaction_type")
						.trim();
				String interactionId = rs.getString("interaction_id");
				Short evidenceId = 0;
				if (rs.getString("evidence_id") != null
						&& !rs.getString("evidence_id").trim().equals("null")) {
					evidenceId = new Short(rs.getString("evidence_id"));
				}

				if (previousInteractionId == null
						|| !previousInteractionId.equals(interactionId)) {
					if (interactionDetail != null) {
						if (!interactionDetail.getParticipantGeneList()
								.contains("null"))
							arrayList.add(interactionDetail);
						interactionDetail = null;
					}
					previousInteractionId = interactionId;

				}

				if (interactionDetail == null) {
					interactionDetail = new InteractionDetail(
							new InteractionParticipant(msid2, geneName2),
							interactionType, evidenceId);
					float confidenceValue = 1.0f;
					try {
						confidenceValue = new Float(
								rs.getDouble("confidence_value"));
					} catch (NumberFormatException nfe) {
						logger.info("there is no confidence value for this row. Default it to 1.");
					}
					short confidenceType = 0;
					try {
						confidenceType = new Short(rs.getString(
								"confidence_type").trim());
					} catch (NumberFormatException nfe) {
						logger.info("there is no confidence value for this row. Default it to 0.");
					}
					interactionDetail.addConfidence(confidenceValue,
							confidenceType);
					String otherConfidenceValues = rs
							.getString("other_confidence_values");
					String otherConfidenceTypes = rs
							.getString("other_confidence_types");
					if (!otherConfidenceValues.equals("null")) {
						String[] values = otherConfidenceValues.split(";");
						String[] types = otherConfidenceTypes.split(";");

						for (int i = 0; i < values.length; i++)
							interactionDetail.addConfidence(
									new Float(values[i]), new Short(types[i]));

					}
				} else {
					interactionDetail
							.addParticipant(new InteractionParticipant(msid2,
									geneName2));
				}

			} catch (NullPointerException npe) {
				logger.error("db row is dropped because a NullPointerException");

			} catch (NumberFormatException nfe) {
				logger.error("db row is dropped because a NumberFormatExceptio");
			}
		}

		if (interactionDetail != null) {
			arrayList.add(interactionDetail);
			interactionDetail = null;
		}
		rs.close();

		return arrayList;
	}

	
	public List<InteractionDetail> getInteractionsByGeneSymbolAndLimit(
			String geneSymbol, String context, String version, Integer limit) throws UnAuthenticatedException, ConnectException,
			SocketTimeoutException, IOException {

		List<InteractionDetail> arrayList = new ArrayList<InteractionDetail>();
        Set<String> edgeIdList = new HashSet<String>();
		String marker_geneName = geneSymbol;

		String methodAndParams = "getInteractionsByGeneSymbolAndLimit"
				+ Constants.DEL + marker_geneName + Constants.DEL + context
				+ Constants.DEL + version;
		
		if (limit != null )
			methodAndParams = methodAndParams +  Constants.DEL + limit;
		 
		ResultSetlUtil rs = ResultSetlUtil.executeQueryWithUserInfo(
				methodAndParams, ResultSetlUtil.getUrl(), null);

		String previousInteractionId = null;

		InteractionDetail interactionDetail = null;
		while (rs.next()) {
			try {
				String msid2 = rs.getString("primary_accession");
				String geneName2 = rs.getString("gene_symbol");

				if (geneName2 == null || geneName2.trim().equals("")
						|| geneName2.trim().equals("null"))
					geneName2 = msid2;
				String interactionType = rs.getString("interaction_type")
						.trim();
				String interactionId = rs.getString("interaction_id");
				Short evidenceId = 0;
				if (rs.getString("evidence_id") != null
						&& !rs.getString("evidence_id").trim().equals("null")) {
					evidenceId = new Short(rs.getString("evidence_id"));
				}

				if (previousInteractionId == null
						|| !previousInteractionId.equals(interactionId)) {
					if (interactionDetail != null) {
						if (!interactionDetail.getParticipantGeneList()
								.contains("null"))
						{
							String edgeName = getEdgeName(interactionDetail, geneSymbol);
							if ( !edgeIdList.contains(edgeName) )
							{
								edgeIdList.add(edgeName);
								arrayList.add(interactionDetail);
							}
							
						}
						interactionDetail = null;
					}
					previousInteractionId = interactionId;

				}

				if (interactionDetail == null) {
					interactionDetail = new InteractionDetail(
							new InteractionParticipant(msid2, geneName2),
							interactionType, evidenceId);
					float confidenceValue = 1.0f;
					try {
						confidenceValue = new Float(
								rs.getDouble("confidence_value"));
					} catch (NumberFormatException nfe) {
						logger.info("there is no confidence value for this row. Default it to 1.");
					}
					short confidenceType = 0;
					try {
						confidenceType = new Short(rs.getString(
								"confidence_type").trim());
					} catch (NumberFormatException nfe) {
						logger.info("there is no confidence value for this row. Default it to 0.");
					}
					interactionDetail.addConfidence(confidenceValue,
							confidenceType);
					String otherConfidenceValues = rs
							.getString("other_confidence_values");
					String otherConfidenceTypes = rs
							.getString("other_confidence_types");
					if (!otherConfidenceValues.equals("null")) {
						String[] values = otherConfidenceValues.split(";");
						String[] types = otherConfidenceTypes.split(";");

						for (int i = 0; i < values.length; i++)
							interactionDetail.addConfidence(
									new Float(values[i]), new Short(types[i]));

					}
				} else {
					interactionDetail
							.addParticipant(new InteractionParticipant(msid2,
									geneName2));
				}

			} catch (NullPointerException npe) {
				logger.error("db row is dropped because a NullPointerException");

			} catch (NumberFormatException nfe) {
				logger.error("db row is dropped because a NumberFormatExceptio");
			}
		}

		if (interactionDetail != null && !interactionDetail.getParticipantGeneList()
				.contains("null")) {
			String edgeName = getEdgeName(interactionDetail, geneSymbol);
			if ( !edgeIdList.contains(edgeName) )
			{
				edgeIdList.add(edgeName);
				arrayList.add(interactionDetail);
			}
			 
			interactionDetail = null;
		}
		rs.close();

		return arrayList;
	}
	
	private String getEdgeName(InteractionDetail d, String geneSymbol)
	{
		String edgeName = null;
		List<InteractionParticipant> participants = d
				.getParticipantList(); 
		String interactionType = d.getInteractionType();
		edgeName = geneSymbol + "." + interactionType;
		for (int i = 0; i < participants.size(); i++)  
		{
		     if (!participants.get(i).getGeneName().equals(geneSymbol))
			    edgeName += "."+ participants.get(i).getGeneName();
		}
		
		return edgeName;
	}
}
