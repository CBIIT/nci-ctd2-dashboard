package gov.nih.nci.ctd2.dashboard.util.cytoscape;

public class CyInteraction {
	private String type;
	private String color;

	public CyInteraction(String type, String color)
	{
		this.type = type;
		this.color = color;
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
