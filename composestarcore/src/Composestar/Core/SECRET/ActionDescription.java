//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\ActionDescription.java

package Composestar.Core.SECRET;


/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * 
 * This is the action description used internally by the resources.
 */
public class ActionDescription {
    private String actor;
    private String action;
    
    /**
     * @roseuid 40459C8C0268
     */
    public ActionDescription() {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 40459C270355
     */
    public String getActor() {
    	return this.actor;     
    }
    
    /**
     * @param name
     * @roseuid 40459C300191
     */
    public void setActor(String name) {
    	this.actor = name;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 40459C370347
     */
    public String getAction() {
     	return this.action;     
    }
    
    /**
     * @param action
     * @roseuid 40459C430387
     */
    public void setAction(String action) {
    	this.action = action;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 404C7E210172
     */
    public String toString() {
    	return "ActionDescription[" + this.action + "] is done by: " + this.actor;
    }
}
