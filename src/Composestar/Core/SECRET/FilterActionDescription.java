//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\FilterActionDescription.java

package Composestar.Core.SECRET;


/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: Composestar.Core.SECRET.cat,v 1.14 2004/03/11 12:25:52 pascal_durr 
 * Exp $
 * This class represents a an action that a filter can do. It contains the action 
 * and if that action is done with an accept or reject action.
 */
public class FilterActionDescription {
    private boolean ACCEPT_REJECT;
    public static final boolean ACCEPT = true;
    public static final boolean REJECT = false;
    private String action;
    private String filtertype;
    
    /**
     * @roseuid 40505FA901F3
     */
    public FilterActionDescription() {
     
    }
    
    /**
     * Creates a new FilterAction with the specified name and action.
     * @param name String The name to set
     * @param action boolean The action to set
     * @param filtertype
     * @param accept
     * @roseuid 4045D46802A6
     */
    public FilterActionDescription(String action, String filtertype, boolean accept) {
    	this.action = action;
    	this.filtertype = filtertype;
    	this.ACCEPT_REJECT = accept;     
    }
    
    /**
     * Gets the name of the action.
     * @return String The name of the action
     * @roseuid 404599BE0218
     */
    public String getAction() {
     	return this.action;     
    }
    
    /**
     * Sets the name of this action.
     * @param name String The name to be set
     * @param action
     * @roseuid 404599CF02E3
     */
    public void setAction(String action) {
    	this.action = action;     
    }
    
    /**
     * Sets the behavior that triggered is action, an accept or a reject.
     * @param action boolean The value to set
     * @param accept
     * @roseuid 404599D801AA
     */
    public void setAcceptEject(boolean accept) {
    	this.ACCEPT_REJECT = accept;     
    }
    
    /**
     * Gets the type of action.
     * @return boolean The action
     * @roseuid 404599E60033
     */
    public boolean getAceptReject() {
    	return this.ACCEPT_REJECT;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 40505F7C02BE
     */
    public String getFilterType() {
     	return this.filtertype;     
    }
    
    /**
     * @param filtertype
     * @roseuid 40505F8403B8
     */
    public void setFilterType(String filtertype) {
    	this.filtertype = filtertype;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 404C7DF9014A
     */
    public String toString() {
     	return new String("/"+this.action+"/"+this.filtertype+"/"+this.ACCEPT_REJECT);     
    }
}
