//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\ResourceDescription.java

package Composestar.Core.SECRET;

import java.util.ArrayList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Composestar.Core.SECRET.cat,v 1.15 2004/03/11 15:05:07 pascal_durr 
 * Exp $
 */
public class ResourceDescription {
    private String ok_regex;
    
    /**
     * @roseuid 4047481D0205
     */
    public ResourceDescription() {
     
    }
    
    /**
     * @param alphabet
     * @param regex
     * @roseuid 4047479003C7
     */
    public ResourceDescription(ArrayList alphabet, String regex) {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 40474745038F
     */
    public String getAlphabet() {
    	return new String("");     
    }
    
    /**
     * @param alpha
     * @roseuid 4047475D00F2
     */
    public void setAlphabet(String alpha) {     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 4047476D01CF
     */
    public String getOKRegex() {
    	return this.ok_regex;     
    }
    
    /**
     * @param regex
     * @roseuid 4047478301C2
     */
    public void setOKRegex(String regex) {
    	this.ok_regex = regex;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 404C7E18026E
     */
    public String toString() {
     	return new String("Resource description: "+this.ok_regex);     
    }
}
