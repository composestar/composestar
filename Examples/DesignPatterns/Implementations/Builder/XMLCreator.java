/**
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is ca.ubc.cs.spl.aspectPatterns. 
 * 
 * The Initial Developers of the Original Code are Jan Hannemann and Gregor Kiczales.
 * 
 * For more details and the latest version of the original code, please see: 
 * http://www.cs.ubc.ca/labs/spl/projects/aodps.html
 * 
 * Contributor(s): Stephan Huttenhuis.
 * 
 * The modified version of the code is part of the Compose* project which can be found at: 
 * http://composestar.sf.net
 * 
 */
package Composestar.Patterns.Builder;

/** @attribute Composestar.Patterns.Builder.Annotations.ConcreteBuilder() */
public class XMLCreator extends Creator {

    protected String type = null;
    protected String attribute = null;

    public void processType(String newType) {
        representation = "<"+newType+">\n";
        type = newType;
    }
    
    public void processAttribute(String newAttribute) {
        if (attribute != null) {
            representation += ("</" + attribute + ">\n");
        }
        representation += ("\t<" + newAttribute + ">");
        this.attribute = newAttribute;
    }
    
    public void processValue(String newValue) {
        representation += (newValue);
    }
    
    public String getRepresentation() {
        if (attribute != null) {
            representation += ("</" + attribute + ">\n");
            attribute = null;
        }
        if (type != null) {
            representation += ("</" + type + ">\n");
            type = null;
        }
        return representation;
    }

}  
