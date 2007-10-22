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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.CKRET;
import Composestar.Core.CKRET.Repository;
import Composestar.Utils.Debug;

public class ConfigParser extends DefaultHandler
{
	private Repository repository;

	public FiltersHandler theCkretFiltersXMLHandler;

	private XMLReader parser;

	public void parse(File filename, Repository inrepository)
	{
		try
		{
			parse(new BufferedInputStream(new FileInputStream(filename)), inrepository);
		}
		catch (SAXException e)
		{
			Debug.out(Debug.MODE_WARNING, CKRET.MODULE_NAME, "Error parsing " + filename + ": " + e.getMessage());
		}
		catch (Exception e)
		{
			Debug.out(Debug.MODE_WARNING, CKRET.MODULE_NAME, "Error parsing " + filename + ": " + e.getMessage());
			Debug.out(Debug.MODE_DEBUG, CKRET.MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
		}
	}

	public void parse(InputStream stream, Repository inrepository) throws ParserConfigurationException, SAXException,
			IOException
	{
		repository = inrepository;
		SAXParser saxparser = SAXParserFactory.newInstance().newSAXParser();
		parser = saxparser.getXMLReader();
		saxparser.parse(new InputSource(stream), this);
	}

	/**
	 * @param uri
	 * @param local_name
	 * @param raw_name
	 * @param amap
	 * @throws org.xml.sax.SAXException
	 * @roseuid 405026C60073
	 */
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (raw_name.equalsIgnoreCase("filters"))
		{
			// System.err.println("Found filters section...");
			FiltersHandler sfxmlhandler = new FiltersHandler(this, parser, this.repository);
			this.parser.setContentHandler(sfxmlhandler);
		}
		else if (raw_name.equalsIgnoreCase("actions"))
		{
			// System.err.println("Found actions section...");
			ActionsHandler saxmlhandler = new ActionsHandler(this, parser, this.repository);
			this.parser.setContentHandler(saxmlhandler);
		}
		else if (raw_name.equalsIgnoreCase("constraints"))
		{
			// System.err.println("Found constraints section...");
			ConstraintsHandler nonconflicthandler = new ConstraintsHandler(this, parser, this.repository);
			this.parser.setContentHandler(nonconflicthandler);
		}
		else if (raw_name.equalsIgnoreCase("resources"))
		{
			// System.err.println("Found resources section...");
			ResourceHandler nonconflicthandler = new ResourceHandler(this, parser, this.repository);
			this.parser.setContentHandler(nonconflicthandler);
		}
	}

	/**
	 * @param uri
	 * @param local_name
	 * @param raw_name
	 * @throws org.xml.sax.SAXException
	 * @roseuid 405026C6010E
	 */
	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{}

	/**
	 * @roseuid 405026C6018C
	 */
	public void startDocument()
	{}

	/**
	 * @roseuid 405026C6019B
	 */
	public void endDocument()
	{}
}
