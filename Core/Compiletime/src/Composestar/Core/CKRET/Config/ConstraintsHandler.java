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

import Composestar.Core.CKRET.Constraint;
import Composestar.Core.CKRET.Repository;
import Composestar.Core.FIRE2.util.regex.PatternParseException;

public class ConstraintsHandler extends DefaultHandler
{
	private Repository repository;

	private ConfigParser returnhandler;

	private XMLReader parser;

	public ConstraintsHandler(ConfigParser handler, XMLReader inparser, Repository inrepository)
	{
		returnhandler = handler;
		parser = inparser;
		repository = inrepository;
	}

	/**
	 * @param uri
	 * @param local_name
	 * @param raw_name
	 * @param amap
	 * @throws org.xml.sax.SAXException
	 * @roseuid 405026C7013E
	 */
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		try
		{
			if (raw_name.equalsIgnoreCase("conflict"))
			{
				String resource = amap.getValue("resource");
				String pattern = amap.getValue("pattern");
				String message = amap.getValue("message");

				repository.addConstraint(new Constraint(resource, pattern, message, Constraint.CONFLICT));
			}
			else if (raw_name.equalsIgnoreCase("require"))
			{
				String resource = amap.getValue("resource");
				String pattern = amap.getValue("pattern");
				String message = amap.getValue("message");

				repository.addConstraint(new Constraint(resource, pattern, message, Constraint.REQUIREMENT));
			}
		}
		catch (PatternParseException e)
		{
			throw new SAXException("Error in " + local_name + " pattern: " + e.getMessage());
		}
	}

	/**
	 * @param uri
	 * @param local_name
	 * @param raw_name
	 * @roseuid 405026C701DA
	 */
	public void endElement(String uri, String local_name, String raw_name)
	{
		if (raw_name.equalsIgnoreCase("constraints"))
		{
			this.parser.setContentHandler(this.returnhandler);
		}
	}
}
