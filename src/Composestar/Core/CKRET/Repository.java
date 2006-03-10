/*
 * Created on Dec 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CKRET.Config.ConfigParser;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.Exception.ModuleException;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Repository {

	private static Repository instance;

	public static final String SECRET_CONFIG = "filterdesc.xml";

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
				
		CommonResources resources = (CommonResources) DataStore.instance().getObjectByID(Master.RESOURCES_KEY);
		String tempFolder = Configuration.instance().pathSettings.getPath("Temp");
		String secretconfigfile = tempFolder + SECRET_CONFIG;
		if( !(new File(secretconfigfile).exists()))
		{
			secretconfigfile = Configuration.instance().pathSettings.getPath("Composestar") + SECRET_CONFIG;
			if( !(new File(secretconfigfile).exists()))
			{
				throw new ModuleException("Filter specification (" + SECRET_CONFIG + ") not found.","SECRET");
			}
		}	
		//resources.ProjectConfiguration.put("SECRETConfigFile",secretconfigfile);	
										
		Debug.out(Debug.MODE_INFORMATION,"SECRET","Using filter specification in " + secretconfigfile);
		
		ConfigParser parser = new ConfigParser();
		parser.parse(secretconfigfile, this);
		     
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
