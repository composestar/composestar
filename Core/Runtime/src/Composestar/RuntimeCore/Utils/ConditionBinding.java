package Composestar.RuntimeCore.Utils;

/**
 * Summary description for ConditionBinding.
 */
public class ConditionBinding {
    private String name;
    private Object ref;
    
    /**
     * @param _name
     * @param _ref
     * @roseuid 4116071F017D
     */
    public ConditionBinding(String _name, Object _ref) {
		name = _name;
		ref = _ref;     
    }
    
    /**
     * @roseuid 4116071F017C
     */
    public ConditionBinding() {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 4116071F0186
     */
    public String getName() {
		return(name);     
    }
    
    /**
     * @param value
     * @roseuid 4116071F0187
     */
    public void setName(String value) {
		name = value;     
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 4116071F0191
     */
    public Object getRef() {
		return(ref);     
    }
    
    /**
     * @param value
     * @roseuid 4116071F0192
     */
    public void setRef(Object value) {
		ref = value;     
    }
}
