/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.Repository;

public class FiltersHandler extends DefaultHandler
{
	// String filter = "";
	// boolean accept = false;

	public ConfigParser theCkretFilterXMLParser;

	ConfigParser returnhandler;

	XMLReader parser;

	/**
	 * @param handler
	 * @param parser
	 * @param sr
	 * @roseuid 405026C5011E
	 * @param repository
	 */
	public FiltersHandler(ConfigParser handler, XMLReader inparser, Repository repository)
	{
		returnhandler = handler;
		parser = inparser;

	}

	/**
	 * @param uri
	 * @param local_name
	 * @param raw_name
	 * @param amap
	 * @throws org.xml.sax.SAXException
	 * @roseuid 405026C5013D
	 */
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (raw_name.equalsIgnoreCase("filter"))
		{
			String type = amap.getValue("type");
			// System.err.println("Reading filter " + type);
			// fad = repository.getDescription(type);
		}
		else if (raw_name.equalsIgnoreCase("accept"))
		{
			// System.err.println("Accept action for " + fad.getFilterType() + "
			// is " + amap.getValue("action"));
			// fad.setAction(amap.getValue("action"), true);

		}
		else if (raw_name.equalsIgnoreCase("reject"))
		{
			// fad.setAction(amap.getValue("action"), false);
			// System.err.println("Reject action for " + fad.getFilterType() + "
			// is " + amap.getValue("action"));
		}
	}

	/**
	 * @param uri
	 * @param local_name
	 * @param raw_name
	 * @roseuid 405026C501CB
	 */
	public void endElement(String uri, String local_name, String raw_name)
	{
		if (raw_name.equalsIgnoreCase("filters"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}
	}
}
