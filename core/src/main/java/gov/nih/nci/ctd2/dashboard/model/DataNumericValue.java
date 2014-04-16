package gov.nih.nci.ctd2.dashboard.model;

public interface DataNumericValue extends Evidence {
    Number getNumericValue();
	void setNumericValue(Number numericValue);
    String getUnit();
    void setUnit(String unit);
}
