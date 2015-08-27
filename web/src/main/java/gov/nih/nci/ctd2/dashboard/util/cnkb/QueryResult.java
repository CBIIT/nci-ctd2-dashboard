package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.ArrayList; 
import java.util.List;
 

/**
 * All CNKB results for ONE queried marker. It is uniquely identified by the markerLabel.
 * 
 * This is based on the class from geWorkbench core that has the same name.
 * Dependency on bison types is removed; name is kept to avoid too much immediate change of other code.
 */
public class QueryResult extends CnkbObject {

	private static final long serialVersionUID = -4163326138016520667L;

	private List<String> interactionTypeList = new ArrayList<String>();
	private List<CellularNetWorkElementInformation> cnkbElementList= new ArrayList<CellularNetWorkElementInformation>();	 
	private float threshold = -1;
 
	
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

	public void setThreshold(float threshold) {
			this.threshold = threshold; 
	}
 
	float getThreshold()
	{
		return this.threshold;
	} 
	
	public boolean addCnkbElement(CellularNetWorkElementInformation c) {
		return getCnkbElementList().add(c);
	}

	
	
}
