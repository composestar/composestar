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
 * $Id: AdviceFactory.java,v 1.3 2005/11/21 16:20:03 pascal_durr Exp $
 */
package Composestar.C.specification;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import Composestar.C.wrapper.utils.GeneralUtils;

public class AdviceFactory
{
    public Advice createAdvice(Element element)
    {
    	String id = element.getAttribute("id");
    	String type = element.getAttribute("type");
    	int ttype = GeneralUtils.getTypeOfProgramPoint(type);
    	if(ttype == 0)
    	{
    		System.out.println("Undefined join point type: "+type);
    		System.exit(-1);
    	}
    	int priority = 0;
    	try
    	{
    		priority = Integer.valueOf(element.getAttribute("priority")).intValue();
    	}
    	catch(Exception e)
    	{
    		priority = 0;
    	}
    	
    	Advice advice = new Advice();
    	advice.setId(id);
    	advice.setType(ttype);
    	advice.setPriority(priority);
    	parseCode(advice, element);
        return advice;
    }

    private void parseCode(Advice advice, Element element)
    {
        NodeList nodeList = element.getElementsByTagName("code");
        if (nodeList.getLength() > 0)
        {
            NodeList nList = nodeList.item(0).getChildNodes();
            if (nList != null)
            {
                for (int ii = 0; ii < nList.getLength(); ii++)
                {
                    Node n = nList.item(ii);
                    if (n.getNodeName().equalsIgnoreCase("#cdata-section"))
                    {
                        String code = n.getNodeValue();
                        advice.setCode(code);
                    }
                }
            }
        }
    }
}
