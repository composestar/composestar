/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.LAMA.MethodInfo;

public class ContextInstruction extends Instruction{
    private String type;
    private MethodInfo method;
    private boolean enabled;

    public ContextInstruction( String type, MethodInfo method ){
	this.type = type;
	this.method = method;
    }

    /**
     * @return the method
     */
    public MethodInfo getMethod(){
	return method;
    }

    /**
     * @return the type
     */
    public String getType(){
	return type;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled(){
	return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled){
	this.enabled = enabled;
    }
}
