package gov.nih.nci.ctd2.dashboard.util.cnkb;

public class InteractionParticipant{
 
	private final String geneId;    
    private final String geneName;     
   
   
    public InteractionParticipant(String geneId, String geneName) {
        this.geneId = geneId; 
        this.geneName = geneName;        
             
    }
    
    public String getGeneId() {
        return geneId;
    }

    public String getGeneName() {
        return geneName;
    }
    
}
