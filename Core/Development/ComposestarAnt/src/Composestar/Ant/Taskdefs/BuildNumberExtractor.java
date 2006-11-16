/**
 * 
 */
package Composestar.Ant.Taskdefs;

import org.apache.tools.ant.Task;

/**
 * @author Michiel Hendriks
 *
 */
public class BuildNumberExtractor extends Task
{	
	protected String label = null;

	protected String property = null;
	
	protected String syntax = "build.#";

	/**
	 * 
	 */
	public BuildNumberExtractor()
	{
	
	}
	
    /**
     * Sets the string attribute of the BuildNumberExtractor object
     *
     * @param string  The new string value
     */
    public void setLabel(String name) {
        label = name;
    }

    /**
     * Sets the property attribute of the BuildNumberExtractor object
     *
     * @param name  The new property value
     */
    public void setProperty(String name) {
        property = name;
    }
    
    public void setSyntax(String name) {
    	syntax = name;
    }
    
    /** Description of the Method */
    public void execute() {
    	String before = syntax.substring(0, syntax.indexOf('#'));
    	String after = syntax.substring(syntax.indexOf('#')+1);    	
    	String result = label.substring(before.length(), label.length()-after.length());
    	getProject().setProperty(property, result);
    }
}
