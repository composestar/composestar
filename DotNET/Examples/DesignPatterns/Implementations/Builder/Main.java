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

public class Main {

    public static void main(String[] args) {
		System.out.println("Creating builder1");
        Creator builder1 = new TextCreator();
		
		System.out.println("Creating builder2");
        Creator builder2 = new XMLCreator();

		System.out.println("calling build on builder1");
		builder1.build();
		System.out.println("calling build on builder2");
		builder2.build();
		System.out.println(builder1.getRepresentation());
		System.out.println(builder2.getRepresentation());
    }
}