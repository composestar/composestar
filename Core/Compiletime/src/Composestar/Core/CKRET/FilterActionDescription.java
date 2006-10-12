//Source file: H:\\composestar\\src\\Composestar\\core\\CKRET\\FilterActionDescription.java

package Composestar.Core.CKRET;


/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: Composestar.Core.CKRET.cat,v 1.14 2004/03/11 12:25:52 pascal_durr 
 * Exp $
 * This class represents a an action that a filter can do. It contains the action 
 * and if that action is done with an accept or reject action.
 */
public class FilterActionDescription {

    private String acceptAction;
	private String rejectAction;
    
    private String filtertype;
    
    
    /**
     * @param filtertype
     */
    public FilterActionDescription(String filtertype) {
     	this.filtertype = filtertype;
    }
    
    /**
     * Gets the name of the action.
     * @return String The name of the action
     * @param accept
     */
    public String getAction(boolean accept) {
     	return (accept? this.acceptAction: this.rejectAction);     
    }
    
    /**
     * Sets the name of this action.
     * @param name String The name to be set
     * @param action
     * @param accept
     */
    public void setAction(String action, boolean accept) {
    	if( accept )
    		this.acceptAction = action;
    	else
    		this.rejectAction = action;     
    }
    
   
    /**
     * @return java.lang.String
     */
    public String getFilterType() {
     	return this.filtertype;     
    }
 }
