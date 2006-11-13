/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.XMLSpecification;

/**
 * @author nagyist
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ArgumentFilter extends XMLFilterImpl
{
	protected String left;

	protected String right;

	private boolean sleft;

	private boolean sright;

	public ArgumentFilter()
	{}

	public ArgumentFilter(XMLReader parent)
	{
		super(parent);
	}

	public String getLeft()
	{
		return left;
	}

	public String getRight()
	{
		return right;
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		// System.out.println(localName);

		if ("left".equals(localName))
		{
			sleft = true;
			sright = false;
		}
		if ("right".equals(localName))
		{
			sleft = false;
			sright = true;
		}

		super.startElement(uri, localName, qName, atts);
	}

	/**
	 * Filter the Namespace URI for end-element events.
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		sleft = false;
		sright = false;
		super.endElement(uri, localName, qName);
	}

	public void characters(char buf[], int offset, int len) throws SAXException
	{
		String s = new String(buf, offset, len);
		// System.out.println("CHAR "+s+" "+sleft);
		if (sleft && !sright)
		{
			left = s;
		}
		if (sright && !sleft)
		{
			right = s;
		}

	}

}
