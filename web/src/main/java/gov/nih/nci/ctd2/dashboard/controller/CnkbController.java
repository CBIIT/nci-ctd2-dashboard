package gov.nih.nci.ctd2.dashboard.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.util.cnkb.CNKB;
import gov.nih.nci.ctd2.dashboard.util.cnkb.InteractionAndCount;
import gov.nih.nci.ctd2.dashboard.util.cnkb.InteractionDetail;
import gov.nih.nci.ctd2.dashboard.util.cnkb.InteractionParticipant;
import gov.nih.nci.ctd2.dashboard.util.cnkb.UnAuthenticatedException;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyNetwork;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.Element;

@Controller
@RequestMapping("/cnkb")
public class CnkbController {
	private static final Log log = LogFactory.getLog(CnkbController.class);

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
		List<InteractionAndCount> list = null;
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
	@RequestMapping(value = "interaction-total-number", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/text")
	public ResponseEntity<String> getInteractionResult(@RequestParam("interactome") String interactome,
			@RequestParam("selectedGenes") String selectedGenes) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		final CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		try {
			String latestVersion = interactionsConnection.getLatestVersionNumber(interactome);
			@SuppressWarnings("unchecked")
			List<String> selectedGenesList = (List<String>) new JSONDeserializer()
					.deserialize(selectedGenes);
			if (selectedGenesList != null) {
				int total = 0;
				for (String gene : selectedGenesList) {
					total += interactionsConnection
							.getInteractionsByGeneSymbol(gene.trim(), interactome, latestVersion).size();
				}
				return new ResponseEntity<String>(Integer.toString(total), headers, HttpStatus.OK);
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

		return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
	}

	@Transactional
	@RequestMapping(value = "network", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/json")
	public ResponseEntity<String> getCnkbCyNetwork(
			@RequestParam("interactome") String interactome,
			@RequestParam("selectedGenes") String selectedGenes,
			@RequestParam(value = "interactionLimit", required = false, defaultValue = "10000") int interactionLimit) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		CyNetwork cyNetwork = null;
		try {
			List<String> selectedGenesList = convertStringToList(selectedGenes);

			List<Element> edgeList = new ArrayList<Element>();
			if (selectedGenesList != null && selectedGenesList.size() != 0) {
				String latestVersion = interactionsConnection.getLatestVersionNumber(interactome);
				for (String gene : selectedGenesList) {
					List<InteractionDetail> interactionDetails = interactionsConnection
							.getInteractionsByGeneSymbol(gene.trim(), interactome, latestVersion);
					if (interactionDetails != null) {
						getEdgeList(interactionDetails, edgeList);
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
			@RequestParam(value = "all", required = false, defaultValue = "true") Boolean all,
			@RequestParam(value = "confidenceType", required = false, defaultValue = "1") int confidenceType,
			@RequestParam(value = "interactionLimit", required = false, defaultValue = "100") int limit,
			HttpServletResponse response) {

		CNKB interactionsConnection = CNKB.getInstance(getCnkbDataURL());
		String filename = "cnkbResult";

		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".sif\"");
		response.addHeader("Content-Transfer-Encoding", "binary");

		try {
			String version = interactionsConnection.getLatestVersionNumber(interactome);

			List<String> selectedGenesList = convertStringToList(selectedGenes);

			OutputStream outputStream = response.getOutputStream();

			Map<String, String> map = interactionsConnection.getInteractionTypeMap();
			List<InteractionDetail> interactionDetails = new ArrayList<InteractionDetail>();
			for (String gene : selectedGenesList) {
				interactionDetails
						.addAll(interactionsConnection.getInteractionsByGeneSymbol(gene.trim(), interactome, version));
			}
			if (!all && interactionDetails.size() > limit) {
				interactionDetails = filter(interactionDetails, confidenceType, limit);
			}
			for (String gene : selectedGenesList) {
				StringBuffer buf = new StringBuffer(
						gene + " " + map.get(interactionDetails.get(0).getInteractionType()));
				for (InteractionDetail interactionDetail : interactionDetails) {
					List<InteractionParticipant> pList = interactionDetail.getParticipantList();
					if (pList.get(0).getGeneName().equals(gene))
						buf.append(" " + pList.get(1).getGeneName());
					if (pList.get(1).getGeneName().equals(gene))
						buf.append(" " + pList.get(0).getGeneName());
				}
				buf.append("\n");
				outputStream.write(buf.toString().getBytes());
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

	private List<InteractionDetail> filter(List<InteractionDetail> all, int confidenceType, int limit) {
		all.sort(
				(InteractionDetail h1, InteractionDetail h2) -> -h1.getConfidenceValue(confidenceType)
						.compareTo(h2.getConfidenceValue(confidenceType)));
		return all.subList(0, limit);
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

	@Transactional
	@RequestMapping(value = "gene-detail", method = { RequestMethod.POST,
			RequestMethod.GET }, headers = "Accept=application/json")
	public ResponseEntity<String> getGeneDetail(
			@RequestParam("gene_symbol") String geneSymbol) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		List<Gene> genes = dashboardDao.findGenesBySymbol(geneSymbol);
		// only return human gene. Returning two genes with the same symbol, one for
		// human and one for mouse, does not make sense.
		Gene gene = null;
		for (Gene g : genes) {
			if (g.getOrganism().getTaxonomyId().equals("9606")) {
				gene = g;
				break;
			}
		}
		if (gene == null) {
			log.warn("gene symbol " + geneSymbol + " not found in database");
			return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
		}
		Set<Xref> xrefs = gene.getXrefs();
		String genecards = null;
		String dave = null;
		if (xrefs.size() > 1) {
			Xref x = xrefs.iterator().next();
			if (x.getDatabaseName().equals("GeneCards")) {
				genecards = x.getDatabaseId();
			}
			if (x.getDatabaseName().equals("Ensembl")) {
				dave = x.getDatabaseId();
			}
		}
		List<Protein> proteinByGene = dashboardDao.findProteinByGene(gene);
		String uniprot = null;
		if (proteinByGene.size() != 1) {
			log.warn("no single protein found for gene symbol " + geneSymbol + ": " + proteinByGene.size());
		} else {
			uniprot = proteinByGene.get(0).getUniprotId();
		}
		GeneDetail geneDetail = new GeneDetail(gene.getFullName(), gene.getEntrezGeneId(), genecards, dave,
				uniprot);
		JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
		return new ResponseEntity<String>(
				jsonSerializer.deepSerialize(geneDetail), headers, HttpStatus.OK);
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

	private void getEdgeList(List<InteractionDetail> interactionDetails, final List<Element> edgeList) {

		for (InteractionDetail interactionDetail : interactionDetails) {
			List<InteractionParticipant> participants = interactionDetail
					.getParticipantList();
			String interactionType = interactionDetail.getInteractionType();
			for (int i = 0; i < participants.size(); i++) {
				String sName = participants.get(i).getGeneName();
				Element source = Element.createNode(sName);
				for (int j = i + 1; j < participants.size(); j++) {
					String tName = participants.get(j).getGeneName();
					Element target = Element.createNode(tName);
					Element cyEdge = Element.createEdge(
							sName + "." + getInteractionTypeMap().get(interactionType) + "." + tName,
							sName,
							tName);
					cyEdge.setProperty(Element.COLOR, colorMap.get(interactionType));
					cyEdge.setProperty(Element.CONFIDENCES, interactionDetail.getConfidences());
					edgeList.add(cyEdge);
				}
			}
		}
	}

	private CyNetwork convertToCyNetwork(List<Element> edgeList, int interactionLimit, List<String> selectedGenesList) {

		CyNetwork cyNetwork = new CyNetwork();
		Set<String> interactionTypes = new HashSet<String>();

		for (int i = 0; i < edgeList.size(); i++) {
			if (i >= interactionLimit)
				break;
			cyNetwork.addEdge(edgeList.get(i));
			String interactionType = edgeList.get(i)
					.getProperty(Element.ID).toString().split("\\.")[1].trim();
			interactionTypes.add(getInteractionTypeMap().get(interactionType));
		}

		cyNetwork.setInteractions(new ArrayList<String>(interactionTypes));

		return cyNetwork;
	}

	@SuppressWarnings({ "unchecked" })
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
}
