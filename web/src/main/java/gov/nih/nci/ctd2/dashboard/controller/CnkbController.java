package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.util.cnkb.CNKB;
import gov.nih.nci.ctd2.dashboard.util.cnkb.CellularNetWorkElementInformation;
import gov.nih.nci.ctd2.dashboard.util.cnkb.InteractionDetail;
import gov.nih.nci.ctd2.dashboard.util.cnkb.InteractionParticipant;
import gov.nih.nci.ctd2.dashboard.util.cnkb.QueryResult;
import gov.nih.nci.ctd2.dashboard.util.cnkb.UnAuthenticatedException;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyEdge;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyElement;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyInteraction;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyNetwork;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyNode;

@Controller
@RequestMapping("/cnkb")
public class CnkbController {

	@Autowired
	private DashboardDao dashboardDao;

	@Autowired
	@Qualifier("cnkbDataURL")
	private String cnkbDataURL = "";

	public String getCnkbDataURL() {
		return cnkbDataURL;
	}

	public void setCnkbDataURL(String cnkbDataURL) {
		this.cnkbDataURL = cnkbDataURL;
	}

	private static Map<String, String> colorMap = new HashMap<String, String>();
	static {
		colorMap.put("protein-dna", "cyan");
		colorMap.put("protein-protein", "orange");
		colorMap.put("modulator-TF", "steelblue");
		colorMap.put("miRNA-mRNA", "darkred");
		colorMap.put("physical association", "navy");
		colorMap.put("protein-rna", "pink");
		colorMap.put("dna-dna", "brown");
		colorMap.put("rna-rna", "purple");
		colorMap.put("reaction->compound", "gray");
		colorMap.put("compund->reaction", "blue");
		colorMap.put("genetic lethal relationship", "black");
		colorMap.put("protein-metabolite", "green");
		colorMap.put("metabolite-protein", "magenta");
		colorMap.put("protein->reaction", "yellow");
		colorMap.put("rna-rna-miRNA", "olive");

	}

	@Transactional
	@RequestMapping(value = "interactome-list", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/text")
	public ResponseEntity<String> getInteractomeList() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		final CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		List<String> list = null;
		try {
			list = interactionsConnection
					.getNciDatasetAndInteractioCount();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnAuthenticatedException e) {
			e.printStackTrace();
		}
		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(list), headers,
				HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = "interactome-descriptions", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/text")
	public ResponseEntity<String> getInteractomeDescriptions(@RequestParam("interactome") String interactome) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		final CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		String[] descriptions = new String[2];
		try {
			descriptions[0] = interactionsConnection
					.getInteractomeDescription(interactome);
			descriptions[1] = interactionsConnection
					.getVersionDescriptor(interactome);
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnAuthenticatedException e) {
			e.printStackTrace();
		}
		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(descriptions), headers,
				HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = "interaction-result", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/text")
	public ResponseEntity<String> getInteractionResult(@RequestParam("interactome") String interactome,
			@RequestParam("selectedGenes") String selectedGenes) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		final CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		QueryResult queryResult = new QueryResult();
		try {
			String latestVersion = interactionsConnection.getLatestVersionNumber(interactome);
			List<String> interactionTypes = interactionsConnection
					.getInteractionTypesByInteractomeVersion(interactome,
							latestVersion);
			queryResult.setInteractionTypeList(interactionTypes);

			List<InteractionDetail> interactionDetails = null;
			Short confidenceType = null;
			@SuppressWarnings("unchecked")
			List<String> selectedGenesList = (List<String>) new JSONDeserializer()
					.deserialize(selectedGenes);
			if (selectedGenesList != null && selectedGenesList.size() != 0) {

				for (String gene : selectedGenesList) {
					CellularNetWorkElementInformation c = new CellularNetWorkElementInformation(
							gene.trim());
					interactionDetails = interactionsConnection
							.getInteractionsByGeneSymbol(gene.trim(),
									interactome, latestVersion);
					if (confidenceType == null
							&& interactionDetails != null
							&& interactionDetails.size() > 0)
						confidenceType = interactionDetails.get(0)
								.getConfidenceTypes().get(0);
					for (int i = 0; i < interactionTypes.size(); i++) {
						c.addInteractionNum(getInteractionNumber(
								interactionDetails,
								interactionTypes.get(i), confidenceType));
					}

					queryResult.addCnkbElement(c);
				}

			}
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnAuthenticatedException e) {
			e.printStackTrace();
		}
		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(queryResult), headers,
				HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = "interaction-throttle", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/text")
	public ResponseEntity<String> getCnkbObject(
			@RequestParam("interactome") String interactome,
			@RequestParam("selectedGenes") String selectedGenes,
			@RequestParam("interactionLimit") int interactionLimit) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		final CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		QueryResult queryResult = new QueryResult();
		try {
			String latestVersion = interactionsConnection.getLatestVersionNumber(interactome);
			List<InteractionDetail> interactionDetails = null;
			List<String> selectedGenesList = convertStringToList(selectedGenes);
			List<Float> confidentList = new ArrayList<Float>();
			Short confidenceType = null;
			if (selectedGenesList != null && selectedGenesList.size() != 0) {
				for (String gene : selectedGenesList) {
					interactionDetails = interactionsConnection
							.getInteractionsByGeneSymbolAndLimit(
									gene.trim(), interactome, latestVersion,
									interactionLimit);
					if (interactionDetails != null) {
						if (confidenceType == null
								&& interactionDetails.size() > 0)
							confidenceType = interactionDetails.get(0)
									.getConfidenceTypes().get(0);
						for (InteractionDetail interactionDetail : interactionDetails)
							confidentList.add(interactionDetail
									.getConfidenceValue(confidenceType));
					}
				}
				// sort genes by value
				Collections.sort(confidentList, new Comparator<Float>() {
					public int compare(Float f1, Float f2) {
						return f2.compareTo(f1);
					}
				});
				if (confidentList.size() > interactionLimit)
					queryResult.setThreshold(confidentList
							.get(interactionLimit));
				else if (confidentList.size() > 0)
					queryResult.setThreshold(confidentList
							.get(confidentList.size() - 1));
			}
		} catch (UnAuthenticatedException uae) {
			uae.printStackTrace();
		} catch (ConnectException e1) {
			e1.printStackTrace();
		} catch (SocketTimeoutException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(queryResult), headers,
				HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = "network", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/json")
	public ResponseEntity<String> getCnkbCyNetwork(
			@RequestParam("interactome") String interactome,
			@RequestParam("selectedGenes") String selectedGenes,
			@RequestParam("interactionLimit") int interactionLimit,
			@RequestParam("throttle") String throttle) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		CyNetwork cyNetwork = null;
		try {

			List<InteractionDetail> interactionDetails = null;
			List<String> selectedGenesList = convertStringToList(selectedGenes);
			float throttleValue = 0;
			try {
				throttleValue = Float.valueOf(throttle);
			} catch (NumberFormatException ne) {
				// set throttleValue = 0;
			}

			List<CyEdge> edgeList = new ArrayList<CyEdge>();
			Short confidenceType = null;
			if (selectedGenesList != null && selectedGenesList.size() != 0) {
				String latestVersion = interactionsConnection.getLatestVersionNumber(interactome);
				for (String gene : selectedGenesList) {
					interactionDetails = interactionsConnection
							.getInteractionsByGeneSymbolAndLimit(gene.trim(),
									interactome, latestVersion, interactionLimit);
					if (interactionDetails != null) {
						if (confidenceType == null
								&& interactionDetails.size() > 0)
							confidenceType = interactionDetails.get(0)
									.getConfidenceTypes().get(0);
						getCyEdgeList(interactionDetails, throttleValue,
								confidenceType, edgeList);
					}
				}
			}

			if (edgeList != null && edgeList.size() > 0) {
				cyNetwork = convertToCyNetwork(edgeList, interactionLimit, selectedGenesList);
			}

		} catch (UnAuthenticatedException uae) {
			uae.printStackTrace();
		} catch (ConnectException e1) {
			e1.printStackTrace();
		} catch (SocketTimeoutException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(cyNetwork), headers, HttpStatus.OK);

	}

	@Transactional
	@RequestMapping(value = "download", method = { RequestMethod.POST })
	public void downloadCnkbResult(
			@RequestParam("interactome") String interactome,
			@RequestParam("selectedGenes") String selectedGenes,
			@RequestParam("interactionLimit") int interactionLimit,
			@RequestParam("throttle") String throttle,
			HttpServletResponse response) {

		CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		String filename = "cnkbResult";

		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=\""
				+ filename + ".sif\"");
		response.addHeader("Content-Transfer-Encoding", "binary");

		try {

			QueryResult cnkbObject = new QueryResult();
			String version = interactionsConnection.getLatestVersionNumber(interactome);
			List<String> interactionTypes = interactionsConnection
					.getInteractionTypesByInteractomeVersion(interactome,
							version);
			((QueryResult) cnkbObject).setInteractionTypeList(interactionTypes);
			List<InteractionDetail> interactionDetails = null;

			List<String> selectedGenesList = convertStringToList(selectedGenes);

			OutputStream outputStream = response.getOutputStream();

			if (selectedGenesList.size() != 0) {
				Short confidenceType = null;
				HashMap<String, String> map = interactionsConnection
						.getInteractionTypeMap();
				for (String gene : selectedGenesList) {
					interactionDetails = interactionsConnection
							.getInteractionsByGeneSymbol(gene.trim(),
									interactome, version);
					if (interactionDetails == null
							|| interactionDetails.size() == 0)
						continue;
					if (confidenceType == null)
						confidenceType = interactionDetails.get(0)
								.getConfidenceTypes().get(0);
					for (int i = 0; i < interactionTypes.size(); i++) {
						List<InteractionDetail> interactionDetailList = getSelectedInteractions(
								interactionDetails, interactionTypes.get(i),
								confidenceType);
						if (interactionDetailList.size() == 0)
							continue;
						StringBuffer buf = new StringBuffer(gene + " "
								+ map.get(interactionTypes.get(i)));
						for (InteractionDetail interactionDetail : interactionDetailList) {
							List<InteractionParticipant> pList = interactionDetail
									.getParticipantList();
							for (int j = 0; j < pList.size(); j++) {
								if (!pList.get(j).getGeneName().equals(gene))
									buf.append(" " + pList.get(j).getGeneName());
							}

						}
						buf.append("\n");
						outputStream.write(buf.toString().getBytes());
					}

				}

			}
			outputStream.close();
		} catch (UnAuthenticatedException uae) {
			uae.printStackTrace();
		} catch (ConnectException e1) {
			e1.printStackTrace();
		} catch (SocketTimeoutException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Transactional
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public ResponseEntity<String> getCnkbCyNetwork(
			@RequestParam("sifData") String data) {

		HashMap<String, String> interactionTypeMap = getInteractionTypeMap();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		HashSet<String> nodeNames = new HashSet<String>();
		HashSet<String> interactionTypes = new HashSet<String>();
		CyNetwork cyNetwork = new CyNetwork();
		boolean validGFormat = true;
		String lines[] = data.split("\n");

		for (int i = 0; i < lines.length; i++) {
			String tokens[] = lines[i].split("\\s+");
			if (tokens.length < 3)
				continue;
			String source = tokens[0].trim();

			String interactionType = tokens[1].trim();
			for (int j = 2; j < tokens.length; j++) {
				String target = tokens[j].trim();
				CyEdge cyEdge = new CyEdge();
				cyEdge.setProperty(CyElement.ID, source + "." + interactionType
						+ "." + target);
				cyEdge.setProperty(CyElement.SOURCE, source);
				cyEdge.setProperty(CyElement.TARGET, target);
				cyEdge.setProperty(CyElement.COLOR,
						colorMap.get(interactionTypeMap.get(interactionType)));
				if (colorMap.get(interactionTypeMap.get(interactionType)) == null) {
					validGFormat = false;
					break;
				}
				cyNetwork.addEdge(cyEdge);
				interactionTypes.add(interactionTypeMap.get(interactionType));
				nodeNames.add(source);
				nodeNames.add(target);

				if (nodeNames.size() > 500) {
					String message = "The number of nodes to be displayed is limited to 500.";
					return new ResponseEntity<String>(
							jsonSerializer.deepSerialize(message), headers,
							HttpStatus.OK);

				}

			}
		}

		if (validGFormat == false) {
			String message = "your file is not sif format.";
			return new ResponseEntity<String>(
					jsonSerializer.deepSerialize(message), headers,
					HttpStatus.OK);

		}

		for (String nodeName : nodeNames) {
			CyNode cyNode = new CyNode();
			cyNode.setProperty(CyElement.ID, nodeName);
			cyNetwork.addNode(cyNode);
		}

		List<CyInteraction> cyInteractions = new ArrayList<CyInteraction>();
		for (String interactionType : interactionTypes) {
			cyInteractions.add(new CyInteraction(interactionType, colorMap
					.get(interactionType)));
		}
		cyNetwork.setInteractions(cyInteractions);

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(cyNetwork), headers, HttpStatus.OK);

	}

	@Transactional
	@RequestMapping(value = "validation", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/json")
	public ResponseEntity<String> getInvalidGeneSymbols(
			@RequestParam("geneSymbols") String geneSymbols) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		List<String> invalidGenes = getInvalidNames(geneSymbols);

		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");

		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(invalidGenes), headers, HttpStatus.OK);

	}

	private float getDivisorValue(float maxValue, float minValue) {
		float divisor = (float) (maxValue - minValue) / 100;

		return divisor;
	}

	private HashMap<String, String> getInteractionTypeMap() {
		HashMap<String, String> map = null;
		try {
			map = CNKB.getInstance(getCnkbDataURL()).getInteractionTypeMap();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnAuthenticatedException e) {
			e.printStackTrace();
		}

		return map;
	}

	private int getInteractionNumber(
			List<InteractionDetail> interactionDetails, String interactionType,
			Short confidenceType) {
		int count = 0;
		if (interactionDetails != null && interactionDetails.size() > 0) {
			for (int i = 0; i < interactionDetails.size(); i++) {
				InteractionDetail interactionDetail = interactionDetails.get(i);
				if (interactionDetail != null
						&& interactionType.equals(interactionDetail
								.getInteractionType())
						&& interactionDetail.getConfidenceTypes().contains(
								confidenceType)) {
					count++;
				}

			}
		}
		return count;
	}

	public ArrayList<InteractionDetail> getSelectedInteractions(
			List<InteractionDetail> interactionDetails, String interactionType,
			short selectedConfidenceType) {
		ArrayList<InteractionDetail> arrayList = new ArrayList<InteractionDetail>();
		if (interactionDetails != null && interactionDetails.size() > 0) {
			for (int i = 0; i < interactionDetails.size(); i++) {
				InteractionDetail interactionDetail = interactionDetails.get(i);

				if (interactionType.equals(interactionDetail
						.getInteractionType())
						&& interactionDetail
								.getConfidenceValue(selectedConfidenceType) != null) {
					arrayList.add(interactionDetail);

				}

			}
		}
		return arrayList;
	}

	private List<String> convertStringToList(String str) {
		List<String> list = new ArrayList<String>();
		if (str != null && !str.trim().equals("")) {
			String[] tokens = str.split(",");
			for (String token : tokens)
				if (!token.trim().equalsIgnoreCase("on"))
					list.add(token.trim());
		}

		return list;
	}

	private void getCyEdgeList(List<InteractionDetail> interactionDetails,
			float throttle, Short confidenceType, List<CyEdge> edgeList) {
		CyNode source = null;
		CyNode target = null;

		for (InteractionDetail interactionDetail : interactionDetails) {

			if (!interactionDetail.getConfidenceTypes()
					.contains(confidenceType)
					|| interactionDetail.getConfidenceValue(confidenceType)
							.floatValue() < throttle)
				continue;
			List<InteractionParticipant> participants = interactionDetail
					.getParticipantList();
			String interactionType = interactionDetail.getInteractionType();
			for (int i = 0; i < participants.size(); i++) {
				source = new CyNode();
				String sName = participants.get(i).getGeneName();
				source.setProperty(CyElement.ID, sName);
				for (int j = i + 1; j < participants.size(); j++) {
					target = new CyNode();
					String tName = participants.get(j).getGeneName();
					target.setProperty(CyElement.ID, tName);
					CyEdge cyEdge = new CyEdge();
					cyEdge.setProperty(CyElement.ID, sName + "."
							+ getInteractionTypeMap().get(interactionType)
							+ "." + tName);
					cyEdge.setProperty(CyElement.SOURCE, sName);
					cyEdge.setProperty(CyElement.TARGET, tName);
					cyEdge.setProperty(CyElement.WEIGHT, interactionDetail
							.getConfidenceValue(confidenceType));
					cyEdge.setProperty(CyElement.COLOR,
							colorMap.get(interactionType));
					edgeList.add(cyEdge);

				}
			}

		}

	}

	private CyNetwork convertToCyNetwork(List<CyEdge> edgeList,
			int interactionLimit, List<String> selectedGenesList) {

		CyNetwork cyNetwork = new CyNetwork();
		Collections.sort(edgeList, new Comparator<CyEdge>() {
			public int compare(CyEdge e1, CyEdge e2) {
				return ((Float) e2.getData().get(CyElement.WEIGHT))
						.compareTo((Float) e1.getData().get(CyElement.WEIGHT));
			}
		});

		List<CyEdge> cyEdgeList = new ArrayList<CyEdge>();
		HashSet<String> nodeNames = new HashSet<String>();
		HashSet<String> interactionTypes = new HashSet<String>();

		for (int i = 0; i < edgeList.size(); i++) {
			if (i >= interactionLimit)
				break;
			cyEdgeList.add(edgeList.get(i));
			nodeNames.add(edgeList.get(i).getData().get(CyElement.SOURCE)
					.toString());
			nodeNames.add(edgeList.get(i).getData().get(CyElement.TARGET)
					.toString());
			String interactionType = edgeList.get(i).getData()
					.get(CyElement.ID).toString().split("\\.")[1].trim();
			interactionTypes.add(getInteractionTypeMap().get(interactionType));
		}

		float minValue = (Float) cyEdgeList.get(cyEdgeList.size() - 1)
				.getData().get(CyElement.WEIGHT);
		float maxValue = (Float) cyEdgeList.get(0).getData()
				.get(CyElement.WEIGHT);
		float divisor = getDivisorValue(maxValue, minValue);

		for (int i = 0; i < cyEdgeList.size(); i++) {

			float confValue = Float.valueOf(edgeList.get(i).getData()
					.get(CyElement.WEIGHT).toString());
			if (divisor != 0)
				edgeList.get(i).setProperty(CyElement.WEIGHT,
						(int) ((confValue - minValue) / divisor));
			else
				edgeList.get(i).setProperty(CyElement.WEIGHT, 50);
			cyNetwork.addEdge(edgeList.get(i));

		}

		for (String nodeName : nodeNames) {
			CyNode cyNode = new CyNode();
			cyNode.setProperty(CyElement.ID, nodeName);
			if (selectedGenesList.contains(nodeName))
				cyNode.setProperty(CyElement.COLOR, "yellow");
			else
				cyNode.setProperty(CyElement.COLOR, "#DDD");
			cyNetwork.addNode(cyNode);
		}

		List<CyInteraction> cyInteractions = new ArrayList<CyInteraction>();
		for (String interactionType : interactionTypes) {
			cyInteractions.add(new CyInteraction(interactionType, colorMap
					.get(interactionType)));
		}
		cyNetwork.setInteractions(cyInteractions);

		return cyNetwork;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> getInvalidNames(String geneSymbols) {
		List<String> invalidGenes = new ArrayList<String>();
		List<String> geneSymbolList = null;
		if (geneSymbols != null && geneSymbols.trim().length() > 0) {
			geneSymbolList = (List<String>) new JSONDeserializer()
					.deserialize(geneSymbols);
		}
		for (String gene : geneSymbolList) {
			if (gene != null && gene.trim().length() > 0) {
				List<Gene> genes = dashboardDao.findGenesBySymbol(gene);
				if (genes == null || genes.size() == 0) {
					invalidGenes.add(gene);
				}
			}
		}

		return invalidGenes;
	}

	// test
	public static void main(String[] args) {

		CnkbController cnknController = new CnkbController();
		cnknController.getCnkbCyNetwork("BCi", "NFIX, FOSL2", 100, "");
		// cnknController.convertCNKBtoJSON("interaction-result", "BCi", "1.0",
		// "NFIX, NCOA1", 100, "");

	}
}
