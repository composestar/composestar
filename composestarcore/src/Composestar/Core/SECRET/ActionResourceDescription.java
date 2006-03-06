//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\ActionResourceDescription.java

package Composestar.Core.SECRET;


public class ActionResourceDescription {
    private String action;
    private String resource;
    private String operation;
    
    /**
     * @roseuid 405072FF02EE
     */
    public ActionResourceDescription() {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 4050728801F5
     */
    public String getAction() {
    	return this.action;     
    }
    
    /**
     * @param action
     * @roseuid 405072940165
     */
    public void setAction(String action) {
    	this.action = action;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 4050729D0101
     */
    public String getResource() {
    	return this.resource;     
    }
    
    /**
     * @param resource
     * @roseuid 405072A601A7
     */
    public void setResource(String resource) {
    	this.resource = resource;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 405072B40196
     */
    public String getOperation() {
     	return this.operation;     
    }
    
    /**
     * @param operation
     * @roseuid 405072C6014B
     */
    public void setOperation(String operation) {
    	this.operation = operation;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 405072E301D7
     */
    public String toString() {
    	return "ARD{/" + this.action + "/" + this.resource + "/" + this.operation + "}";
    }
}
