package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.ArrayList;
import java.util.List;

public class InteractomeInfo extends CnkbObject {

	private static final long serialVersionUID = -6965453484961096930L;

	private List<String> interactomeList = new ArrayList<String>();	 
	private List<VersionDescriptor> versionDescriptorList = new ArrayList<VersionDescriptor>();
	private String description = null;
	 

	public List<String> getInteractomeList() {
		return this.interactomeList;
	}

	public void setInteractomeList(List<String> interactomeList) {
		this.interactomeList = interactomeList;
	}
	
	

	public List<VersionDescriptor> getVersionDescriptorList() {
		return this.versionDescriptorList;
	}

	public void setVersionDescriptorList(
			List<VersionDescriptor> versionDescriptorList) {
		this.versionDescriptorList = versionDescriptorList;
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
	
	
	public boolean addVersionDescriptor(VersionDescriptor versionDescriptor) {
		return getVersionDescriptorList().add(versionDescriptor);
	}

}
