//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\SECRETFilter.java

package Composestar.Core.SECRET;


/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Composestar.Core.SECRET.cat,v 1.8 2004/03/10 07:56:14 pascal_durr 
 * Exp $
 * 
 * Temp class to capture the filters!
 */
public class SECRETFilter {
    private String name;
    private String type;
    
    /**
     * @param name
     * @param type
     * @roseuid 404C813803A8
     */
    public SECRETFilter(String name, String type) {
    	this.name = name;
    	this.type = type;
    }
    
    /**
     * @roseuid 403F1A1201C1
     */
    public SECRETFilter() {
    	this.name="";
    	this.type="";
    }
    
    /**
     * @return java.lang.String
     * @roseuid 403F1A120103
     */
    public String getName() {
     	return this.name;
    }
    
    /**
     * @param name
     * @roseuid 403F1A120135
     */
    public void setName(String name) {
    	this.name = name;
    }
    
    /**
     * @return java.lang.String
     * @roseuid 403F1A12015D
     */
    public String getType() {
     	return this.type;
    }
    
    /**
     * @param type
     * @roseuid 403F1A120199
     */
    public void setType(String type) {
    	this.type = type;
    }
    
    /**
     * @return java.lang.String
     * @roseuid 404C81CC01BA
     */
    public String toString() {
     	return "Filter with name: " + this.name + " and with type: " + this.type;
    }
}
