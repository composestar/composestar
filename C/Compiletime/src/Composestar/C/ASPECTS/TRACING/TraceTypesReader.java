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
 * $Id: TraceTypesReader.java,v 1.1 2006/09/01 15:31:20 johantewinkel Exp $
 */
package Composestar.C.ASPECTS.TRACING;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TraceTypesReader
{
	public static void main(String[] args)
	{
		TraceTypesReader ttr = new TraceTypesReader();
		ttr.readTraceTypes(args[0]);
	}

	public void readTraceTypes(String filename)
	{
		System.out.print("Reading trace types from: " + filename + "(");

		TraceTypesDB ttdb = TraceTypesDB.instance();

		Document doc = parseXmlFile(filename, false);
		// Element root = doc.getDocumentElement();

		NodeList types = doc.getElementsByTagName("type");

		for (int i = 0; i < types.getLength(); i++)
		{
			Element element = (Element) types.item(i);
			TraceableType tt = parseType(element);
			ttdb.addTraceType(tt);
		}
		System.out.println(ttdb.getNumberOfTraceTypes() + ")");
	}

	public TraceableType parseType(Element element)
	{
		TraceableType tt = new TraceableType();
		tt.setType(element.getAttribute("id"));
		NodeList nodelist = element.getElementsByTagName("input");
		NodeList nList = nodelist.item(0).getChildNodes();
		if (nList != null)
		{
			for (int ii = 0; ii < nList.getLength(); ii++)
			{
				Node n = nList.item(ii);
				if (n.getNodeName().equalsIgnoreCase("#cdata-section"))
				{
					String str = n.getNodeValue();
					// System.out.println("Input: "+str);
					tt.setInTraceString(str);
				}
			}
		}
		nodelist = element.getElementsByTagName("output");
		nList = nodelist.item(0).getChildNodes();
		if (nList != null)
		{
			for (int ii = 0; ii < nList.getLength(); ii++)
			{
				Node n = nList.item(ii);
				if (n.getNodeName().equalsIgnoreCase("#cdata-section"))
				{
					String str = n.getNodeValue();
					// System.out.println("Onput: "+str);
					tt.setOutTraceString(str);
				}
			}
		}
		return tt;
	}

	public static Document parseXmlFile(String filename, boolean validating)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validating);

			Document doc = factory.newDocumentBuilder().parse(new File(filename));
			return doc;
		}
		catch (SAXException e)
		{
			System.out.println(e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			System.out.println(e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		return null;
	}
}
