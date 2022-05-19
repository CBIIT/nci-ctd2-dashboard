package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;

import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyNetwork;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.Element;
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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import gov.nih.nci.ctd2.dashboard.util.mra.MasterRegulator;
import gov.nih.nci.ctd2.dashboard.util.mra.MraTargetBarcode;

@Controller
@RequestMapping("/mra-data")
public class MraController {

	private static final String WEIGHT = "weight";

	@Autowired
	@Qualifier("allowedProxyHosts")
	private String allowedProxyHosts = "";

	private static Map<String, String> shapeMap = new HashMap<String, String>();
	static {
		shapeMap.put("K", "rectangle");
		shapeMap.put("TF", "ellipse");
		shapeMap.put("P", "hexagon");
		shapeMap.put("none", "triangle");
	}
 
	public String getAllowedProxyHosts() {
		return allowedProxyHosts;
	}

	public void setAllowedProxyHosts(String allowedProxyHosts) {
		this.allowedProxyHosts = allowedProxyHosts;
	}

	@Transactional
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, headers = "Accept=application/json")
	public ResponseEntity<String> convertMRAtoJSON(
			@RequestParam("url") String url,
			@RequestParam("dataType") String dataType,
			@RequestParam("filterBy") String filterBy,
			@RequestParam("nodeNumLimit") int nodeNumLimit,
			@RequestParam("throttle") String throttle) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		CyNetwork cyNetwork = null;
		List<MasterRegulator> masterRegulators = null;
		Float throttleValue = null;

		if (isURLValid(url)) {
			URLConnection urlConnection = null;
			try {			 
				urlConnection = new URL(url).openConnection();
				InputStream inputStream = urlConnection.getInputStream();
				Scanner scanner = new Scanner(inputStream);
				if (dataType != null && dataType.trim().equals("cytoscape"))
					cyNetwork = convertToCyNetwork(scanner, filterBy, nodeNumLimit, throttle);
				else if (dataType != null && dataType.trim().equals("mra"))
					masterRegulators = convertToMasterRegulator(scanner);
				else
					throttleValue = getThrottleValue(scanner, filterBy, nodeNumLimit);							 
				inputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
		if (dataType != null && dataType.trim().equals("cytoscape")) {
			return new ResponseEntity<String>(
					jsonSerializer.deepSerialize(cyNetwork), headers,
					HttpStatus.OK);
		} else if (dataType != null && dataType.trim().equals("mra")) {
			return new ResponseEntity<String>(
					jsonSerializer.deepSerialize(masterRegulators), headers,
					HttpStatus.OK

			);
		} else {
			// System.out.println(jsonSerializer.deepSerialize(throttleValue));
			return new ResponseEntity<String>(
					jsonSerializer.deepSerialize(throttleValue), headers,
					HttpStatus.OK

			);
		}
	}

	private boolean isURLValid(String url) {
		
		String[] hosts = allowedProxyHosts.split(",", -1);	 
		for (String host : hosts)
			if (url.toLowerCase().startsWith(host.toLowerCase()))
				return true;

		return false;
	}

	private List<MasterRegulator> convertToMasterRegulator(Scanner scanner) {
		List<MasterRegulator> masterRegulators = new ArrayList<MasterRegulator>();
		MasterRegulator masterRegulator = null;
		int totalNumberOfMarkers = 0;
		String scoreType = null;
		double absMaxMraScore = 0;
		double absMaxDeScore = 0;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty())
				continue;
			if (line.contains("!Series_total_number_of_markers")) {
				totalNumberOfMarkers = getIntValue(line);
			} else if (line.contains("!Series_mra_score_type")) {
				scoreType = getStringValue(line);

			} else if (line.contains("!Series_abs_max_da_score_observed")) {
				absMaxMraScore = getDoubleValue(line);

			} else if (line.contains("!Series_abs_max_de_score_observed")) {
				absMaxDeScore = getDoubleValue(line);

			} else if (line.contains("^MRA_ENTREZ_ID")) {
				masterRegulator = new MasterRegulator();
				masterRegulator.setEntrezId(getIntValue(line));
			} else if (line.contains("!mra_gene_symbol")) {
				masterRegulator.setGeneSymbol(getStringValue(line));
			} else if (line.contains("!mra_score")) {
				double score = getDoubleValue(line);
				masterRegulator.setScore(score);
				if (scoreType.equals("NES") && absMaxMraScore != 0)
					masterRegulator.setDaColor(calculateColor(
							absMaxMraScore, score));
			} else if (line.contains("!mra_de_rank")) {
				masterRegulator.setDeRank(getIntValue(line));
			} else if (line.contains("!mra_de")) {
				if (absMaxDeScore != 0)
					masterRegulator.setDeColor(calculateColor(
							absMaxDeScore, getDoubleValue(line)));

			} else if (line.contains("!mra_data_row_count")) {
				masterRegulator.setDataRowCount(getIntValue(line));
			} else if (line.contains("!target_table_begin")) {
				line = scanner.nextLine(); // skip header
				ArrayList<HashMap<Integer, Integer>> lm = new ArrayList<HashMap<Integer, Integer>>();
				lm.add(0, new HashMap<Integer, Integer>()); // SC>=0
				lm.add(1, new HashMap<Integer, Integer>()); // SC<0
				int[] maxcopy = new int[2];
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if (line.contains("!target_table_end")) {
						List<MraTargetBarcode> mraTargets = masterRegulator
								.getMraTargets();
						for (MraTargetBarcode mraTargetBarcode : mraTargets) {
							int arrayIndex = mraTargetBarcode.getArrayIndex();
							int position = mraTargetBarcode.getPosition();
							int ColorIndex = 255
									* lm.get(arrayIndex).get(position)
									/ maxcopy[arrayIndex];
							mraTargetBarcode.setColorIndex(ColorIndex);
						}
						masterRegulators.add(masterRegulator);
						break;
					}
					MraTargetBarcode mraTarget = getMraTargetBarcode(line,
							totalNumberOfMarkers, lm, maxcopy);
					masterRegulator.addMraTarget(mraTarget);
				}

			}

		}

		return masterRegulators;

	}

	private CyNetwork convertToCyNetwork(Scanner scanner, String filterBy, int nodeNumLimit, 
			String throttle) {

		Object[] edgeNodeList = getEdgeNodeList(scanner, filterBy, nodeNumLimit, throttle);
		CyNetwork cyNetwork = new CyNetwork();
		@SuppressWarnings("unchecked")
		List<Element> edgeList = (List<Element>) edgeNodeList[0];
		@SuppressWarnings("unchecked")
		Map<String, Element> nodeList = (Map<String, Element>) edgeNodeList[1];

		if (edgeList == null || edgeList.size() == 0)
			return null;
		// sort genes by value
		Collections.sort(edgeList, new Comparator<Element>() {
			public int compare(Element e1, Element e2) {
				return ((Float) e1.getProperty(WEIGHT))
						.compareTo((Float) e2.getProperty(WEIGHT));
			}
		});

		float minValue = getMinValue(edgeList, nodeNumLimit);
		float maxValue = (Float) edgeList.get(edgeList.size() - 1)
				.getProperty(WEIGHT);
		float divisor = getDivisorValue(maxValue, minValue);
		HashSet<String> nodeNames = new HashSet<String>();
		for (int i = 1; i <= edgeList.size(); i++) {
			if (nodeNames.size() >= nodeNumLimit)
				break;
			int index = edgeList.size() - i;
			float confValue = Float.valueOf(edgeList.get(index)
					.getProperty(WEIGHT).toString());
			if (divisor != 0)
				edgeList.get(index).setProperty(WEIGHT,
						(int) ((confValue - minValue) / divisor));
			else
				edgeList.get(index).setProperty(WEIGHT, 50);
			cyNetwork.addEdge(edgeList.get(index));
			String sourceId = (String) edgeList.get(index)
					.getProperty(Element.SOURCE);
			String targetId = (String) edgeList.get(index)
					.getProperty(Element.TARGET);
			if (!nodeNames.contains(sourceId)) {
				cyNetwork.addNode(nodeList.get(sourceId));
				nodeNames.add(sourceId);
			}
			if (!nodeNames.contains(targetId)) {
				cyNetwork.addNode(nodeList.get(targetId));
				nodeNames.add(targetId);
			}

		}

		return cyNetwork;
	}

	private Float getThrottleValue(Scanner scanner, String filterBy, 
			int nodeNumLimit) {

		Object[] edgeNodeList = getEdgeNodeList(scanner, filterBy, nodeNumLimit, null);

		@SuppressWarnings("unchecked")
		List<Element> edgeList = (List<Element>) edgeNodeList[0];

		// sort genes by value
		Collections.sort(edgeList, new Comparator<Element>() {
			public int compare(Element e1, Element e2) {
				return ((Float) e1.getProperty(WEIGHT))
						.compareTo((Float) e2.getProperty(WEIGHT));
			}
		});

		if (edgeList == null || edgeList.size() == 0)
			return null;
		float minValue = getMinValue(edgeList, nodeNumLimit);
		return minValue;
	}

	private Object[] getEdgeNodeList(Scanner scanner, String filterBy,
			int  nodeNumLimit, String throttle) {
		double absMaxDeScore = 0;
		Object[] edgeNodeList = new Object[2];
		List<Element> edgeList = new ArrayList<Element>();
		Map<String, Element> nodeList = new HashMap<String, Element>();

		List<String> filters = new ArrayList<String>();
		if (filterBy != null && !filterBy.trim().equals("")) {
			String[] tokens = filterBy.split(",");
			for (String token : tokens)
				filters.add(token.trim());
		}

		float throttleVal = 0;
		if (throttle != null && throttle.trim().length() > 0)
			throttleVal = Float.valueOf(throttle);

		Element source = null;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty())
				continue;
			if (line.contains("!Series_abs_max_de_score_observed")) {
				absMaxDeScore = getDoubleValue(line);
				continue;
			}
			if (line.contains("^MRA_ENTREZ_ID")) {
				String entrezId = getStringValue(line);
				if (filters.contains(entrezId)) {
					source = new Element();
				} else
					source = null;
			}
			if (source == null)
				continue;

			if (line.contains("!mra_gene_symbol")) {
				String geneSymbol = getStringValue(line);
				source.setProperty(Element.ID, geneSymbol);
			} else if (line.contains("!mra_gene_type")) {
				source.setProperty(Element.SHAPE,
						shapeMap.get(getStringValue(line)));
			} else if (line.contains("!mra_de")
					&& !line.contains("!mra_de_rank")) {
				if (absMaxDeScore != 0) {

					source.setProperty(Element.COLOR,
							calculateColor(absMaxDeScore, getDoubleValue(line)));

				}
			} else if (line.contains("!target_table_begin")) {
				line = scanner.nextLine();// skip header
				// cyNetwork.addNode(source);
				String sourceId = (String) source.getProperty(Element.ID);
				nodeList.put(sourceId, source);
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if (line.contains("!target_table_end"))
						break;
					String tokens[] = line.trim().split("\t");
					float confValue = Float.valueOf(tokens[3]);
					if (confValue < throttleVal)
						continue;
					Element cyEdge = new Element();
					Element target = new Element();

					assert tokens.length == 7;

					cyEdge.setProperty(Element.ID, sourceId + "." + tokens[1]);
					cyEdge.setProperty(Element.SOURCE, sourceId);
					cyEdge.setProperty(Element.TARGET, tokens[1]);
					cyEdge.setProperty(WEIGHT, confValue);
					// cyNetwork.addEdge(cyEdge);
					edgeList.add(cyEdge);
					if (!nodeList.keySet().contains(tokens[1])) {
						target.setProperty(Element.ID, tokens[1]);
						target.setProperty(Element.SHAPE,
								shapeMap.get(tokens[2]));
						target.setProperty(
								Element.COLOR,
								calculateColor(absMaxDeScore, Double.valueOf(
										tokens[4])));
						nodeList.put(tokens[1], target);
					}
				}
			} // end !target_table_begin

		}// end while
		edgeNodeList[0] = edgeList;
		edgeNodeList[1] = nodeList;

		return edgeNodeList;

	}

	private float getMinValue(List<Element> edgeList, int nodeNumLimit) {
		HashSet<String> nodeNames = new HashSet<String>();
		int index = 0;
		for (int i = 1; i <= edgeList.size(); i++) {
			if (nodeNames.size() > nodeNumLimit)
				break;
			index = edgeList.size() - i;
			String sourceId = (String) edgeList.get(index)
					.getProperty(Element.SOURCE);
			String targetId = (String) edgeList.get(index)
					.getProperty(Element.TARGET);

			nodeNames.add(sourceId);
			nodeNames.add(targetId);

		}
		return Float.valueOf(edgeList.get(index).getProperty(WEIGHT)
				.toString());
	}

	private int getIntValue(String line) {
		String tokens[] = line.trim().split("=");
		assert tokens.length == 2;
		return Integer.valueOf(tokens[1].trim()).intValue();
	}

	private double getDoubleValue(String line) {
		String tokens[] = line.trim().split("=");
		assert tokens.length == 2;
		return Double.valueOf(tokens[1].trim()).doubleValue();
	}

	private String getStringValue(String line) {
		String tokens[] = line.trim().split("=");
		assert tokens.length == 2;
		return tokens[1].trim();
	}

	private MraTargetBarcode getMraTargetBarcode(String line,
			int totalMarkerNumber, ArrayList<HashMap<Integer, Integer>> lm,
			int[] maxcopy) {
		MraTargetBarcode mraTargetBarcode = null;
		String tokens[] = line.trim().split("\t");
		assert tokens.length == 7;
		mraTargetBarcode = new MraTargetBarcode();
		mraTargetBarcode.setEntrezId(Long.valueOf(tokens[0].trim()));
		double spearmanCor = Double.valueOf(tokens[6]).doubleValue();
		mraTargetBarcode.setArrayIndex(spearmanCor > 0 ? 0 : 1);
		int position = (int) 400 * Integer.valueOf(tokens[5]).intValue()
				/ totalMarkerNumber;
		mraTargetBarcode.setPosition(position);
		int arrayindex = spearmanCor >= 0 ? 0 : 1;
		HashMap<Integer, Integer> hm = lm.get(arrayindex);
		Integer copy = hm.get(position);
		copy = copy == null ? 1 : (copy + 1);
		hm.put(position, copy);
		if (maxcopy[arrayindex] < copy)
			maxcopy[arrayindex] = copy;
		return mraTargetBarcode;
	}

	private String calculateColor(double absMaxValue, double value) {

		int colorindex = 0;
		if (absMaxValue != 0)
			colorindex = (int) (255 * value / absMaxValue);

		if (colorindex < 0) {
			colorindex = Math.abs(colorindex);
			return "rgb(" + (255 - colorindex) + ", " + (255 - colorindex)
					+ ", 255)";
		} else
			return "rgb(255, " + (255 - colorindex) + ", " + (255 - colorindex)
					+ ")";
	}
	 
	private float getDivisorValue(float maxValue, float minValue) {
		float divisor = (float) (maxValue - minValue) / 100;

		return divisor;
	}

	// test
	public static void main(String[] args) {

		MraController mraController = new MraController();
		String dataUrl = "http://localhost:8080/ctd2-dashboard/submissions/MRA_Combine-gbm-filtered.txt";
	   //  mraController.convertMRAtoJSON(dataUrl, "mra", "", "");

		mraController.convertMRAtoJSON(dataUrl, "cytoscape", "2355,1051", 1000, "");
		//mraController.convertMRAtoJSON(dataUrl, "throttle", "", 150, null);

	}
}
