/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

public abstract class Instruction{
    private Label label;
    
    public Instruction(){
	
    }
    
    public Instruction( Label label ){
	this.label = label;
    }
    
    /**
     * @return the label
     */
    public Label getLabel(){
	return label;
    }
}
