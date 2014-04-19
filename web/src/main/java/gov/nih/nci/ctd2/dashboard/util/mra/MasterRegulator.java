package gov.nih.nci.ctd2.dashboard.util.mra;

import java.util.ArrayList;
import java.util.List;

public class MasterRegulator {
	private Long entrezId;
	private String geneSymbol;
	private double score;
	private String daColor;
	private String deColor;
	private int deRank = 0;
	private int dataRowCount;

	private List<MraTargetBarcode> mraTargets = new ArrayList<MraTargetBarcode>();

	public long getEntrezId() {
		return this.entrezId;
	}

	public void setEntrezId(long entrezId) {
		this.entrezId = entrezId;
	}

	public String getGeneSymbol() {
		return this.geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	public double getScore() {
		return this.score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getDaColor() {
		return this.daColor;
	}

	public void setDaColor(String daColor) {
		this.daColor = daColor;
	}

	public String getDeColor() {
		return this.deColor;
	}

	public void setDeColor(String deColor) {
		this.deColor = deColor;
	}

	public int getDeRank() {
		return this.deRank;
	}

	public void setDeRank(int deRank) {
		this.deRank = deRank;
	}

	public int getDataRowCount() {
		return this.dataRowCount;
	}

	public void setDataRowCount(int dataRowCount) {
		this.dataRowCount = dataRowCount;
	}

	public List<MraTargetBarcode> getMraTargets() {
		return this.mraTargets;
	}

	public void setMraTargets(List<MraTargetBarcode> mraTargets) {
		this.mraTargets = mraTargets;
	}

	public void addMraTarget(MraTargetBarcode mraTarget) {
		this.mraTargets.add(mraTarget);
	}

}
