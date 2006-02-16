package Composestar.RuntimeCore.Utils;

/**
 * represents a method binding
 */
public class MethodBinding {
    private String name;
    private Object ref;
    
    /**
     * @param name
     * @param ref
     * @roseuid 41160762011E
     */
    public MethodBinding(String name, Object ref) {
		this.name = name;
		this.ref = ref;
    }
    
    /**
     * @roseuid 4116071F00BE
     */
    public MethodBinding() {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 4116071F00C8
     */
    public String getName() {
		return(name);     
    }
    
    /**
     * @param value
     * @roseuid 4116071F00D2
     */
    public void setName(String value) {
		name = value;     
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 4116071F00D4
     */
    public Object getRef() {
		return(ref);     
    }
    
    /**
     * @param value
     * @roseuid 4116071F00D5
     */
    public void setRef(Object value) {
		ref = value;     
    }
}
