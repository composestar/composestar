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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CKRET.Config.ConfigParser;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
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

	protected void init() throws ModuleException
	{
		// CommonResources resources = (CommonResources)
		// DataStore.instance().getObjectByID(Master.RESOURCES_KEY);
		String tempFolder = Configuration.instance().getPathSettings().getPath("Base");
		String ckretconfigfile = tempFolder + CKRET_CONFIG;
		if (!(new File(ckretconfigfile).exists()))
		{
			ckretconfigfile = Configuration.instance().getPathSettings().getPath("Composestar") + CKRET_CONFIG;
			if (!(new File(ckretconfigfile).exists()))
			{
				throw new ModuleException("Filter specification (" + CKRET_CONFIG + ") not found.", CKRET.MODULE_NAME);
			}
		}
		INCRE.instance().addConfiguration("CKRETConfigFile", ckretconfigfile);

		Debug.out(Debug.MODE_INFORMATION, CKRET.MODULE_NAME, "Using filter specification in " + ckretconfigfile);

		ConfigParser parser = new ConfigParser();
		parser.parse(ckretconfigfile, this);

	}
}
