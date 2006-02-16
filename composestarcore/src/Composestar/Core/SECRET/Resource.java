//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\Resource.java

package Composestar.Core.SECRET;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Composestar.Core.SECRET.cat,v 1.15 2004/03/11 15:05:07 pascal_durr Exp 
 * $
 * 
 * The resource class represent the abstract resource that is used in the model!
 */
public class Resource {
    public ResourceDescription resourcedescription;
    
    /**
     * @roseuid 403F3E160143
     */
    public Resource() {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 403F4908020D
     */
    public String getName() {
     return null;
    }
    
    /**
     * @param name
     * @roseuid 403F49100085
     */
    public void setName(String name) {
     
    }
    
    /**
     * @return Composestar.Core.SECRET.ResourceDescription
     * @roseuid 403F491B00D2
     */
    public ResourceDescription getResourceDescription() {
     return null;
    }
    
    /**
     * @param regex
     * @roseuid 403F492C0208
     */
    public void setResourceDescription(ResourceDescription regex) {
     
    }
    
    /**
     * @param ad
     * @roseuid 404597E00092
     */
    public void addActor(ActionDescription ad) {
     
    }
    
    /**
     * @return boolean
     * @roseuid 404597F502D9
     */
    public boolean checkDependency() {
     return true;
    }
}
