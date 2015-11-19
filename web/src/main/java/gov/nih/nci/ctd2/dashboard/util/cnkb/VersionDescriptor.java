package gov.nih.nci.ctd2.dashboard.util.cnkb;
 
public class VersionDescriptor extends CnkbObject{
    
	private static final long serialVersionUID = 4090417692163302222L;
	
	private final String version;    
    private final boolean requiresAuthentication;
    private final String versionDesc;
    

    public VersionDescriptor(final String version, final boolean requiresAuthentication, final String versionDesc) {
        this.version = version;
        this.requiresAuthentication = requiresAuthentication;
        this.versionDesc = versionDesc;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public boolean getRequiresAuthentication() {
        return requiresAuthentication;
    }
   
}
