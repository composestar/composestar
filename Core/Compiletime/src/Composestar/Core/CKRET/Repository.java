/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import Composestar.Core.CKRET.Config.ConfigParser;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;

/**
 * 
 */
public class Repository
{

	private static Repository instance;

	public static final String CKRET_CONFIG = "filterdesc.xml";

	private Map filters;

	private List<Constraint> constraints;

	public static Repository instance()
	{
		if (instance == null)
		{
			instance = new Repository();
		}
		return instance;
	}

	private Repository()
	{
		filters = new HashMap();
		constraints = new ArrayList<Constraint>();
	}

	public void addConstraint(Constraint constraint)
	{
		constraints.add(constraint);
	}

	public List<Constraint> getConstraints()
	{
		return constraints;
	}

	protected void init(CommonResources resources) throws ModuleException
	{
		// CommonResources resources = (CommonResources)
		// DataStore.instance().getObjectByID(Master.RESOURCES_KEY);
		File ckretconfigfile = new File(resources.configuration().getProject().getBase(), CKRET_CONFIG);
		if (!ckretconfigfile.exists())
		{
			ckretconfigfile = resources.getPathResolver().getResource(CKRET_CONFIG);
		}
		ConfigParser parser = new ConfigParser();
		if (ckretconfigfile != null)
		{
			INCRE.instance().addConfiguration("CKRETConfigFile", ckretconfigfile.toString());
			Debug.out(Debug.MODE_INFORMATION, CKRET.MODULE_NAME, "Using filter specification in " + ckretconfigfile);
			parser.parse(ckretconfigfile, this);
		}
		else
		{
			try
			{
				parser.parse(Repository.class.getResourceAsStream("/" + CKRET_CONFIG), this);
			}
			catch (Exception e)
			{
				Debug.out(Debug.MODE_WARNING, CKRET.MODULE_NAME, "Error parsing interal config: " + e.getMessage());
				Debug.out(Debug.MODE_DEBUG, CKRET.MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
			}
		}

	}
}
