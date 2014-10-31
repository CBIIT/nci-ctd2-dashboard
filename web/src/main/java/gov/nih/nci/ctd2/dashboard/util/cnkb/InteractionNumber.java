package gov.nih.nci.ctd2.dashboard.util.cnkb;

public class InteractionNumber{
  
    private String interactionType = null; 
    private int number = 0;
   
    public InteractionNumber(String interactionType) {
        this.interactionType = interactionType;         
    }
    
    public String getInteractionType() {
        return this.interactionType;
    }

    public int getNumber() {
        return this.number;
    }
    
    public void increase()
    {
    	number++;
    }
    
}
