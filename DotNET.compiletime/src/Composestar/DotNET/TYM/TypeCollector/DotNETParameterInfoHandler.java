//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\DotNETParameterInfoHandler.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\DotNETParameterInfoHandler.java

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

import Composestar.DotNET.LAMA.DotNETParameterInfo;

/**
 * Handles parameters.
 */
public class DotNETParameterInfoHandler extends DefaultHandler implements ContentHandler
{
	XMLReader Parser;

	private DotNETMethodInfoHandler ReturnHandler;

	private DotNETParameterInfo ParamInfo;

	private String LastCharData;

	/**
	 * @param info
	 * @param parser
	 * @param typeHandler
	 * @roseuid 40502BE10111
	 */
	public DotNETParameterInfoHandler(DotNETParameterInfo info, XMLReader parser, DotNETMethodInfoHandler typeHandler)
	{
		Parser = parser;
		ReturnHandler = typeHandler;
		ParamInfo = info;
	}

	/**
	 * @param namespaceURI
	 * @param localName
	 * @param rawName
	 * @param atts
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE10157
	 */
	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
			throws SAXException
	{
		LastCharData = null;
	}

	/**
	 * @param namespaceURI
	 * @param localName
	 * @param rawName
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE1021F
	 */
	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException
	{
		// <!ELEMENT IsIn (#PCDATA)>
		if ("IsIn".equals(rawName))
		{
			// TODO: SetIs"In"
			ParamInfo.setIsln(Boolean.valueOf(LastCharData));
		}
		// <!ELEMENT IsLcid (#PCDATA)>
		else if ("IsLcid".equals(rawName))
		{
			ParamInfo.setIsLcid(Boolean.valueOf(LastCharData));
		}
		// <!ELEMENT IsOptional (#PCDATA)>
		else if ("IsOptional".equals(rawName))
		{
			ParamInfo.setIsOptional(Boolean.valueOf(LastCharData));
		}
		// <!ELEMENT IsOut (#PCDATA)>
		else if ("IsOut".equals(rawName))
		{
			ParamInfo.setIsOut(Boolean.valueOf(LastCharData));
		}
		// <!ELEMENT IsRetval (#PCDATA)>
		else if ("IsRetval".equals(rawName))
		{
			ParamInfo.setIsRetVal(Boolean.valueOf(LastCharData));
		}
		// <!ELEMENT ParameterType (#PCDATA)>
		else if ("ParameterType".equals(rawName))
		{
			ParamInfo.setParameterType(LastCharData);
		}
		// <!ELEMENT Position (#PCDATA)>
		else if ("Position".equals(rawName))
		{
			ParamInfo.setPosition(Integer.parseInt(LastCharData));
		}
		// <!ELEMENT HashCode (#PCDATA)>
		else if ("HashCode".equals(rawName))
		{
			ParamInfo.setHashCode(Integer.parseInt(LastCharData));
		}
		else if ("ParameterInfo".equals(rawName))
		{
			// end of this element. Pass back control to old handler
			Parser.setContentHandler(ReturnHandler);
		}
		else
		{
			throw new SAXNotRecognizedException("Unkown type " + rawName + " in DotNETParameterInfoHandler.endElement");
		}
	}

	/**
	 * @param text
	 * @param start
	 * @param length
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40502BE102C9
	 */
	public void characters(char[] text, int start, int length) throws SAXException
	{
		if (LastCharData == null)
		{
			LastCharData = new String(text, start, length);
		}
		else
		{
			LastCharData += new String(text, start, length);
		}
	}
}
