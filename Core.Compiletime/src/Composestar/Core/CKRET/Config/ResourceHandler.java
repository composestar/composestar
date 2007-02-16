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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.Repository;
import Composestar.Core.CKRET.Resource;
import Composestar.Utils.StringConverter;

public class ResourceHandler extends DefaultHandler
{
	// String filter = "";
	// boolean accept = false;

	private static List resources = new ArrayList();

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
	public ResourceHandler(ConfigParser handler, XMLReader inparser, Repository repository)
	{
		returnhandler = handler;
		parser = inparser;
		// Repository repository1 = repository;

	}

	public static List getResources()
	{
		return resources;
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
		Resource resource;
		if (local_name.equalsIgnoreCase("resource"))
		{
			resource = new Resource(amap.getValue("name"));
			resources.add(resource);
			Iterator it = StringConverter.stringToStringList(amap.getValue("alphabet"), ",");
			while (it.hasNext())
			{
				resource.addToAlphabet((String) it.next());
			}
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
		if (local_name.equalsIgnoreCase("resources"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}
	}
}