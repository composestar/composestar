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
import java.util.List;

import Composestar.Core.CKRET.Config.ConfigParser;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * 
 */
public class Repository
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CKRET.MODULE_NAME);

	private static Repository instance;

	public static final String CKRET_CONFIG = "filterdesc.xml";

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
			logger.info("Using filter specification in " + ckretconfigfile);
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
				logger.warn("Error parsing interal config: " + e.getMessage(), e);
			}
		}

	}
}
