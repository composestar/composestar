/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
 * 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: PointcutFactory.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.specification;

import org.w3c.dom.*;

import Composestar.C.wrapper.utils.GeneralUtils;

public class PointcutFactory
{
	public Pointcut createPointcut(Element element)
	{
		String id = element.getAttribute("id");
		Pointcut pointcut = new Pointcut(id);
		parseFunctions(pointcut, element);
		parseAdvices(pointcut,element);
		return pointcut;
	}

	private void parseFunctions(Pointcut pointcut, Element element)
    {
        NodeList nodeList = element.getElementsByTagName("elements");
        for(int i = 0; i < nodeList.getLength(); i++)
        {
        	Functions functions = new Functions();
        	Element e = (Element) nodeList.item(i);
        	
        	functions.setFile(e.getAttribute("files"));
        	functions.setData(e.getAttribute("data"));
        	int type = GeneralUtils.getTypeForProgramElement(e.getAttribute("identifier"));
        	if(type ==0)
        	{
        		System.out.println("Unknown program element type: "+e.getAttribute("type"));
        		System.exit(-1);
        	}
        	functions.setType(type);
        	functions.setParent(pointcut);
            
        	NodeList children = e.getElementsByTagName("returntype");
            if(children != null)
            {
	        	for(int j = 0; j < children.getLength(); j++)
	            {
	            	Element node = (Element)children.item(j);
	            	functions.setReturnType(node.getAttribute("type"));
	            	if(node.getAttribute("not").equalsIgnoreCase("true"))
	            		functions.setInvertReturnType(true);
	            	else
	            		functions.setInvertReturnType(false);
	            }
            }
        	
            children = e.getElementsByTagName("parameter");
            if(children != null)
            {
	        	for(int j = 0; j < children.getLength(); j++)
	            {
	            	Element node = (Element)children.item(j);
	            	XMLParameter xmlparam = new XMLParameter();
	            	xmlparam.setType(node.getAttribute("type"));
	            	xmlparam.setName(node.getAttribute("name"));
	            	if(node.getAttribute("not").equalsIgnoreCase("true"))
	            		xmlparam.setInvert(true);
	            	else
	            		xmlparam.setInvert(false);
	            	
	            	functions.addParameter(xmlparam);
	            }
            }
            pointcut.addFunctions(functions);
        }
    }
    
    private void parseAdvices(Pointcut pointcut, Element element)
    {
        NodeList nodeList = element.getElementsByTagName("advices");

        for(int i = 0; i < nodeList.getLength(); i++)
        {
        	Element e = (Element) nodeList.item(i);
        	NodeList children = e.getElementsByTagName("adviceapplication");
        	if(children != null)
        	{
	            for(int j = 0; j < children.getLength(); j++)
	            {
	            	Element node = (Element)children.item(j);
	            	String id = node.getAttribute("id");
	            	int type = GeneralUtils.getTypeOfAdvice(node.getAttribute("type"));
	            	if(type == 0)
	            	{
	            		System.out.println("Undefined advice appilcation type: "+node.getAttribute("type"));
	            		System.exit(-1);
	            	}
	            	AdviceApplication aa = new AdviceApplication();
	            	aa.setId(id);
	            	aa.setType(type);
	            	pointcut.addAdviceApplication(aa);
	            }
            }
        }
    }
}
