package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.ArrayList;
import java.util.List; 

public class CellularNetWorkElementInformation {

	private String geneName = null;
	private List<Integer> interactionNumlist = new ArrayList<Integer>();
	
	public CellularNetWorkElementInformation(String geneName) 
	{		this.geneName = geneName;	 
 
	}

	public String getGeneName() {
		return this.geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}
	
	
	public List<Integer> getInteractionNumlist() {
		return this.interactionNumlist;
	}

	public void setInteractionNumlist(
			List<Integer> interactionNumlist) {
		this.interactionNumlist = interactionNumlist;
	}
	
	public boolean addInteractionNum(int number) {
		return getInteractionNumlist().add(number);
	}	
 

}
