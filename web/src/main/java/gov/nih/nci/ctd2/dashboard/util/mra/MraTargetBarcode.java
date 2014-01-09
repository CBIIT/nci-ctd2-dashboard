package gov.nih.nci.ctd2.dashboard.util.mra;

public class MraTargetBarcode {
	private long entrezId;
	private int position;
	private int colorIndex;
	private int arrayIndex;

	public long getEntrezId() {
		return this.entrezId;
	}

	public void setEntrezId(long entrezId) {
		this.entrezId = entrezId;
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public int getArrayIndex() {
		return this.arrayIndex;
	}

	public void setArrayIndex(int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

}
