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
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Repository {

	private static Repository instance;

	public static final String CKRET_CONFIG = "filterdesc.xml";

	private Map actions;
	private Map filters;
	
	private List constraints;

	public static Repository instance()
	{
		if( instance == null )
		{
			instance = new Repository();
		}
		return instance;
	}

	private Repository()
	{
		actions = new HashMap();
		filters = new HashMap();
		constraints = new ArrayList();
	}
	
	public void addConstraint(Constraint constraint)
	{
		this.constraints.add(constraint);
	}
	
	public List getConstraints()
	{
		return this.constraints;
	}
	
	protected void init() throws ModuleException
	{
		actions.put("meta", new MetaFilterAction());
				
		//CommonResources resources = (CommonResources) DataStore.instance().getObjectByID(Master.RESOURCES_KEY);
		String tempFolder = Configuration.instance().getPathSettings().getPath("Base");
		String ckretconfigfile = tempFolder + CKRET_CONFIG;
		if( !(new File(ckretconfigfile).exists()))
		{
			ckretconfigfile = Configuration.instance().getPathSettings().getPath("Composestar") + CKRET_CONFIG;
			if( !(new File(ckretconfigfile).exists()))
			{
				throw new ModuleException("Filter specification (" + CKRET_CONFIG + ") not found.","CKRET");
			}
		}	
		INCRE.instance().addConfiguration("CKRETConfigFile",ckretconfigfile);
														
		Debug.out(Debug.MODE_INFORMATION,"CKRET","Using filter specification in " + ckretconfigfile);
		
		ConfigParser parser = new ConfigParser();
		parser.parse(ckretconfigfile, this);
		     
	}
	
	public FilterAction getAction(String name)
	{
		FilterAction action = (FilterAction) actions.get(name);
		if( action == null )
		{
			action = new FilterAction(name);
			actions.put(name, action);
		}
		return action;
	}
	
	public FilterActionDescription getDescription(String type)
	{
		FilterActionDescription fad = (FilterActionDescription) filters.get(type);
		if( fad == null )
		{
			fad = new FilterActionDescription(type);
			filters.put(type, fad);
		}
		return fad;
	}
	
	/**
	 * Todo make this dynamic (xml) or have FIRE pass the FilterActions
	 * @param filter
	 * @param accept
	 * @return
	 */
	public static FilterAction getAction(Filter filter, boolean accept)
	{
		String type = filter.getFilterType().getType();
		if(type.equalsIgnoreCase("Custom"))
			type = filter.getFilterType().getName();
		Repository repository = Repository.instance;
		return repository.getAction(repository.getDescription(type).getAction(accept));
	}
	
}
