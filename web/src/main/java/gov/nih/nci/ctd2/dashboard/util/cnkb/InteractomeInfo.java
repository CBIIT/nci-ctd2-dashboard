package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.ArrayList;
import java.util.List;

/* TODO
This class seems to be used a catch-all object for various different queries.
This should be replaced by some more specific, more concise objects.
*/
public class InteractomeInfo extends CnkbObject {

	private static final long serialVersionUID = -6965453484961096930L;

	private List<String> interactomeList = new ArrayList<String>();
	private String versionDescription = null;
	private String description = null;
	 

	public List<String> getInteractomeList() {
		return this.interactomeList;
	}

	public void setInteractomeList(List<String> interactomeList) {
		this.interactomeList = interactomeList;
	}
	
	

	public String getVersionDescriptor() {
		return this.versionDescription;
	}

	public void setVersionDescriptor(String versionDescriptor) {
		this.versionDescription = versionDescriptor;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean addInteractome(String interactome) {
		return getInteractomeList().add(interactome);
	}
	
	
}
