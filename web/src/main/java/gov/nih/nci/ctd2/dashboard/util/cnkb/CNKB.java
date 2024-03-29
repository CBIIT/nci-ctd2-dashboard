package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		if (instance == null) {
			instance = new CNKB();
			ResultSetlUtil.setUrl(interactionsServletUrl);
		}
		return instance;
	}

	public HashMap<String, String> getInteractionTypeMap()
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {

		if (interactionTypeMap != null)
			return interactionTypeMap;
		interactionTypeMap = new HashMap<String, String>();
		String methodAndParams = "getInteractionTypes";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

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
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		HashMap<String, String> map = new HashMap<String, String>();

		String methodAndParams = "getInteractionEvidences";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

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
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		HashMap<String, String> map = new HashMap<String, String>();

		String methodAndParams = "getConfidenceTypes";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

		while (rs.next()) {

			String confidenceType = rs.getString("name").trim();
			String id = rs.getString("id").trim();

			map.put(confidenceType, id);
			map.put(id, confidenceType);
		}
		rs.close();

		return map;
	}

	public List<String> getInteractionTypes()
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		List<String> arrayList = new ArrayList<String>();

		String methodAndParams = "getInteractionTypes";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

		while (rs.next()) {

			String interactionType = rs.getString("interaction_type").trim();

			arrayList.add(interactionType);
		}
		rs.close();

		return arrayList;
	}

	public List<String> getInteractionTypesByInteractomeVersion(String context, String version)
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		List<String> arrayList = new ArrayList<String>();

		String methodAndParams = "getInteractionTypesByInteractomeVersion" + Constants.DEL + context + Constants.DEL
				+ version;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

		while (rs.next()) {

			String interactionType = rs.getString("interaction_type").trim();

			arrayList.add(interactionType);
		}
		rs.close();

		return arrayList;
	}

	public String getInteractomeDescription(String interactomeName)
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {

		String interactomeDesc = null;

		String methodAndParams = "getInteractomeDescription" + Constants.DEL + interactomeName;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);
		while (rs.next()) {
			interactomeDesc = rs.getString("description").trim();
			break;
		}
		rs.close();

		return interactomeDesc;
	}

	public ArrayList<String> getDatasetAndInteractioCount()
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		ArrayList<String> arrayList = new ArrayList<String>();

		String datasetName = null;
		int interactionCount = 0;

		String methodAndParams = "getDatasetNames";
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

		while (rs.next()) {

			datasetName = rs.getString("name").trim();
			interactionCount = (int) rs.getDouble("interaction_count");
			arrayList.add(datasetName + " (" + interactionCount + " interactions)");
		}
		rs.close();

		return arrayList;
	}

	public Set<String> interactionNames = null;

	public List<InteractionAndCount> getNciDatasetAndInteractioCount()
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		interactionNames = new HashSet<String>();
		List<InteractionAndCount> arrayList = new ArrayList<InteractionAndCount>();
		ResultSetlUtil rs = ResultSetlUtil.executeQuery("getNciDatasetNames");
		while (rs.next()) {
			String datasetName = rs.getString("name").trim();
			interactionNames.add(datasetName);
			arrayList
					.add(new InteractionAndCount(datasetName, (int) rs.getDouble("interaction_count")));
		}
		rs.close();
		return arrayList;
	}

	// "only the most recent version of each interactome will be supported" - the
	// requirement document
	// The only interactome that has more than one version is HGi_TCGA. There are
	// about 34 interactome totally.
	public String getVersionDescriptor(String interactomeName)
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		String latestVersionDescription = null;
		float versionValue = 0;

		String methodAndParams = "getVersionDescriptor" + Constants.DEL + interactomeName;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);
		while (rs.next()) {
			String version = rs.getString("version").trim();
			if (version.equalsIgnoreCase("DEL"))
				continue;
			String value = rs.getString("authentication_yn").trim();
			if (value.equalsIgnoreCase("Y")) {
				continue;
			}
			String versionDesc = rs.getString("description").trim();
			float v = Float.parseFloat(version);
			if (v > versionValue) {
				versionValue = v;
				latestVersionDescription = versionDesc;
			}
		}
		rs.close();

		return latestVersionDescription;
	}

	/* get the latest version number for an interactome */
	public String getLatestVersionNumber(String interactomeName)
			throws ConnectException, SocketTimeoutException, IOException, UnAuthenticatedException {
		String latestVersion = null;
		float versionValue = 0;

		String methodAndParams = "getVersionDescriptor" + Constants.DEL + interactomeName;
		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);
		while (rs.next()) {
			String version = rs.getString("version").trim();
			if (version.equalsIgnoreCase("DEL"))
				continue;
			String value = rs.getString("authentication_yn").trim();
			if (value.equalsIgnoreCase("Y")) {
				continue;
			}
			float v = Float.parseFloat(version);
			if (v > versionValue) {
				versionValue = v;
				latestVersion = version;
			}
		}
		rs.close();

		return latestVersion;
	}

	public List<InteractionDetail> getInteractionsByGeneSymbol(String geneSymbol, String context, String version)
			throws UnAuthenticatedException, ConnectException, SocketTimeoutException, IOException {

		List<InteractionDetail> arrayList = new ArrayList<InteractionDetail>();
		Set<String> edgeIdList = new HashSet<String>();
		String marker_geneName = geneSymbol;

		String methodAndParams = "getInteractionsByGeneSymbolAndLimit" + Constants.DEL + marker_geneName + Constants.DEL
				+ context + Constants.DEL + version;

		ResultSetlUtil rs = ResultSetlUtil.executeQuery(methodAndParams);

		String previousInteractionId = null;

		InteractionDetail interactionDetail = null;
		while (rs.next()) {
			try {
				String msid2 = rs.getString("primary_accession");
				String geneName2 = rs.getString("gene_symbol");

				if (geneName2 == null || geneName2.trim().equals("") || geneName2.trim().equals("null"))
					geneName2 = msid2;
				String interactionType = rs.getString("interaction_type").trim();
				String interactionId = rs.getString("interaction_id");

				if (previousInteractionId == null || !previousInteractionId.equals(interactionId)) {
					if (interactionDetail != null) {
						if (!interactionDetail.getParticipantGeneList().contains("null")) {
							String edgeName = getEdgeName(interactionDetail, geneSymbol);
							if (!edgeIdList.contains(edgeName)) {
								edgeIdList.add(edgeName);
								if (interactionDetail.getParticipantList().size() > 1)
									arrayList.add(interactionDetail);
							}
						}
						interactionDetail = null;
					}
					previousInteractionId = interactionId;
				}

				if (interactionDetail == null) {
					interactionDetail = new InteractionDetail(new InteractionParticipant(msid2, geneName2),
							interactionType);
					float confidenceValue = 1.0f;
					try {
						confidenceValue = (float) rs.getDouble("confidence_value");
					} catch (NumberFormatException nfe) {
						logger.info("there is no confidence value for this row. Default it to 1.");
					}
					short confidenceType = 0;
					try {
						confidenceType = Short.valueOf(rs.getString("confidence_type").trim());
						if (confidenceType == 6) // mode of action
							confidenceValue = Math.abs(confidenceValue);
					} catch (NumberFormatException nfe) {
						logger.info("there is no confidence value for this row. Default it to 0.");
					}
					interactionDetail.addConfidence(confidenceValue, confidenceType);
					String otherConfidenceValues = rs.getString("other_confidence_values");
					String otherConfidenceTypes = rs.getString("other_confidence_types");
					if (!otherConfidenceValues.equals("null")) {
						String[] values = otherConfidenceValues.split(";");
						String[] types = otherConfidenceTypes.split(";");

						for (int i = 0; i < values.length; i++) {
							short type = Short.valueOf(types[i]);
							float value = Float.valueOf(values[i]);
							if (type == 6) // mode of action
								value = Math.abs(value);
							interactionDetail.addConfidence(value, type);
						}
					}
				} else {
					interactionDetail.addParticipant(new InteractionParticipant(msid2, geneName2));
				}

			} catch (NullPointerException npe) {
				logger.error("db row is dropped because a NullPointerException");

			} catch (NumberFormatException nfe) {
				logger.error("db row is dropped because a NumberFormatException");
				logger.error("query string: " + methodAndParams);
			}
		}

		if (interactionDetail != null && !interactionDetail.getParticipantGeneList().contains("null")) {
			String edgeName = getEdgeName(interactionDetail, geneSymbol);
			if (!edgeIdList.contains(edgeName)) {
				edgeIdList.add(edgeName);
				if (interactionDetail.getParticipantList().size() > 1)
					arrayList.add(interactionDetail);
			}

			interactionDetail = null;
		}
		rs.close();

		return arrayList;
	}

	private String getEdgeName(InteractionDetail d, String geneSymbol) {
		String edgeName = null;
		List<InteractionParticipant> participants = d.getParticipantList();
		String interactionType = d.getInteractionType();
		edgeName = geneSymbol + "." + interactionType;
		for (int i = 0; i < participants.size(); i++) {
			if (!participants.get(i).getGeneName().equals(geneSymbol))
				edgeName += "." + participants.get(i).getGeneName();
		}

		return edgeName;
	}
}
