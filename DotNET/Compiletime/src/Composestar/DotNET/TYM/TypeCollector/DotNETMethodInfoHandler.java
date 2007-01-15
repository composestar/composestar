//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\DotNETMethodInfoHandler.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\DotNETMethodInfoHandler.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.TYM.TypeCollector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETParameterInfo;

/**
 * Top level method handler. Handles all elements for a method except the
 * parameter.
 */
public class DotNETMethodInfoHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	private DotNETTypeHandler returnHandler;

	private DotNETMethodInfo methodInfo;

	private String lastCharData;

	/**
	 * @param theMethodInfo
	 * @param theParser
	 * @param typeHandler
	 * @roseuid 40502BDF03CB
	 */
	public DotNETMethodInfoHandler(DotNETMethodInfo theMethodInfo, XMLReader theParser, DotNETTypeHandler typeHandler)
	{
		parser = theParser;
		returnHandler = typeHandler;
		methodInfo = theMethodInfo;
	}

	/**
	 * @param namespaceURI
	 * @param localName
	 * @param rawName
	 * @param atts
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE00029
	 */
	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
			throws SAXException
	{
		lastCharData = null;
		if ("ParameterInfo".equals(rawName))
		{
			DotNETParameterInfo paramInfo = new DotNETParameterInfo();
			String fullName = atts.getValue("name");
			if (fullName != null)
			{
				paramInfo.setName(fullName);
			}
			else
			{
				throw new SAXNotRecognizedException("ParamInfo must have a name attribute");
			}

			DotNETParameterInfoHandler parameterHandler = new DotNETParameterInfoHandler(paramInfo, parser, this);
			methodInfo.addParameter(paramInfo);
			parser.setContentHandler(parameterHandler);
		}
	}

	/**
	 * @param namespaceURI
	 * @param localName
	 * @param rawName
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE000F1
	 */
	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException
	{
		// <!ELEMENT CallingConvention (#PCDATA)>
		if ("CallingConvention".equals(rawName))
		{
			methodInfo.setCallingConvention(Integer.parseInt(lastCharData));
		}
		// <!ELEMENT IsAbstract (#PCDATA)>
		else if ("IsAbstract".equals(rawName))
		{
			methodInfo.setIsAbstract(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsAssembly (#PCDATA)>
		else if ("IsAssembly".equals(rawName))
		{
			methodInfo.setIsAssembly(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsConstructor (#PCDATA)>
		else if ("IsConstructor".equals(rawName))
		{
			methodInfo.setIsConstructor(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsFamily (#PCDATA)>
		else if ("IsFamily".equals(rawName))
		{
			methodInfo.setIsFamily(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsFamilyAndAssembly (#PCDATA)>
		else if ("IsFamilyAndAssembly".equals(rawName))
		{
			methodInfo.setIsFamilyAndAssembly(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsFamilyOrAssembly (#PCDATA)>
		else if ("IsFamilyOrAssembly".equals(rawName))
		{
			methodInfo.setIsFamilyOrAssembly(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsFinal (#PCDATA)>
		else if ("IsFinal".equals(rawName))
		{
			methodInfo.setIsFinal(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsHideBySig (#PCDATA)>
		else if ("IsHideBySig".equals(rawName))
		{
			methodInfo.setIsHideBySig(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsPrivate (#PCDATA)>
		else if ("IsPrivate".equals(rawName))
		{
			// TODO: Is*P*rivate
			methodInfo.setIsprivate(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsPublic (#PCDATA)>
		else if ("IsPublic".equals(rawName))
		{
			methodInfo.setIsPublic(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsStatic (#PCDATA)>
		else if ("IsStatic".equals(rawName))
		{
			methodInfo.setIsStatic(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT IsVirtual (#PCDATA)>
		else if ("IsVirtual".equals(rawName))
		{
			methodInfo.setIsVirtual(Boolean.valueOf(lastCharData));
		}
		// <!ELEMENT HashCode (#PCDATA)>
		else if ("HashCode".equals(rawName))
		{
			methodInfo.setHashCode(Integer.parseInt(lastCharData));
		}
		// <!ELEMENT ReturnType (#PCDATA)>
		else if ("ReturnType".equals(rawName))
		{
			methodInfo.setReturnType(lastCharData);
		}
		else if ("IsDeclaredHere".equals(rawName))
		{
			methodInfo.setIsDeclaredHere(Boolean.valueOf(lastCharData));
		}
		else if ("MethodAttributes".equals(rawName))
		{
			// Ignore this type
		}
		// end of method
		else if ("MethodInfo".equals(rawName))
		{
			// end of this element. Pass back control to old handler
			parser.setContentHandler(returnHandler);
		}
		else
		{
			throw new SAXNotRecognizedException("Unknown type " + rawName + " in DotNETMethodInfoHandler.endElement");
		}
	}

	/**
	 * @param text
	 * @param start
	 * @param length
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE0019C
	 */
	public void characters(char[] text, int start, int length) throws SAXException
	{
		if (lastCharData == null)
		{
			lastCharData = new String(text, start, length);
		}
		else
		{
			lastCharData += new String(text, start, length);
		}
	}
}
