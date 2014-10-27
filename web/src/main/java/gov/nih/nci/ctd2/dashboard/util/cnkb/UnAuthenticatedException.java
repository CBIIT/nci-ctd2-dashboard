package gov.nih.nci.ctd2.dashboard.util.cnkb;

 
public class UnAuthenticatedException extends Exception {
    
	private static final long serialVersionUID = 6379819293142168996L;

	// ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public UnAuthenticatedException() {
        super();
    }

    public UnAuthenticatedException(String message) {
        super(message);
    }

}