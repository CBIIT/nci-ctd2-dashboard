package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {

	private List<String> interactionTypeList = new ArrayList<String>();
	private List<CellularNetWorkElementInformation> cnkbElementList = new ArrayList<CellularNetWorkElementInformation>();

	public List<String> getInteractionTypeList() {
		return this.interactionTypeList;
	}

	public void setInteractionTypeList(List<String> interactionTypeList) {
		this.interactionTypeList = interactionTypeList;
	}

	public List<CellularNetWorkElementInformation> getCnkbElementList() {
		return this.cnkbElementList;
	}

	public void setCnkbElementList(
			List<CellularNetWorkElementInformation> cnkbElementList) {
		this.cnkbElementList = cnkbElementList;
	}

	public boolean addCnkbElement(CellularNetWorkElementInformation c) {
		return getCnkbElementList().add(c);
	}
}
