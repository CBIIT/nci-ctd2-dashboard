package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.ArrayList;
import java.util.List;

public class InteractionSummary {

	private List<String> geneNames = null;
	private int totalNumber = 0;

	public InteractionSummary(List<String> geneNames, int totalNumber) {
		this.geneNames = geneNames;
		this.totalNumber = totalNumber;
	}

	public List<String> getGeneNames() {
		return geneNames;
	}

	public void setGeneNames(List<String> geneNames) {
		this.geneNames = geneNames;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
}
