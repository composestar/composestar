//Source file: F:\\composestar\\src\\Composestar\\core\\SECRET\\SecretRepository.java

//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\SECRET\\SecretRepository.java

//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\SecretRepository.java

package Composestar.Core.SECRET;

import java.util.HashMap;
import java.util.ArrayList;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SECRET.filterxmlparser.SecretFilterXMLParser;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: Composestar.Core.SECRET.cat,v 1.10 2004/03/10 10:51:11 pascal_durr Exp 
 * $
 */
public class SecretRepository implements CTCommonModule {
    
    /**
     * This map is a mapping between filter and which actions this filter can do, 
     * depending on the accept of reject action.
     */
    private ArrayList filterActionList = new ArrayList ();
    
    /**
     * This map is a mapping between an action and which resources are effected by 
     * this action, specified with the read and write operation
     */
    private ArrayList actionResourceList = new ArrayList ();
    
    /**
     * This map is a mapping between aa resource and the conflict pattern of this 
     * resource.
     */
    private HashMap conflictResourceMap = new HashMap ();
    private static SecretRepository instance;
    
    /**
     * Adds filteraction to the map.
     * @param name String The name of the action
     * @param fa FilterAction The action to be added.
     * @roseuid 4050679E00D5
     */
    public void addFilterActionDescription(FilterActionDescription fa) {
     	if(fa != null)
     	{
     		this.filterActionList.add(fa);
     	}     
    }
    
    /**
     * Add an ActionResource with a  name and the action it does on certain resources.
     * @param name String The name of the action
     * @param ar ActionResource the placeholder for the action data.
     * @param rad
     * @roseuid 4050679E0152
     */
    public void addActionResourceDescription(ActionResourceDescription rad) {
    	if(rad != null)
    	{
    		this.actionResourceList.add(rad);
    	}     
    }
    
    /**
     * Adds a conflct pattern to the specified resource
     * @param name String The name of the resource
     * @param pattern String The pattern of the conflict.
     * @param rd
     * @roseuid 4050679E01DF
     */
    public void addResourceDescription(String name, ResourceDescription rd) {
    	if(name != null && rd != null)
    	{
    		this.conflictResourceMap.put(name,rd);
    	}     
    }
    
    /**
     * Returns the conflict definition of the specified resource.
     * @param name String The name of the resource
     * @return String The conflict definition
     * @roseuid 4050679E023D
     */
    public String getConflictDefinitionForResource(String name) {
		if(name != null)
		{
			// TODO: is the return value right? maybe return the OKRegEx
			return ((ResourceDescription)this.conflictResourceMap.get(name)).toString();
		}
     	return null;     
    }
    
    /**
     * @param type
     * @return java.util.ArrayList
     * @roseuid 4050679E0327
     */
    public ArrayList getAllActionsForType(String type) {
		if(type != null)
		{
			ArrayList tmp = new ArrayList();
			for(java.util.Iterator it = this.filterActionList.iterator(); it.hasNext();)
			{
				FilterActionDescription fad = (FilterActionDescription)it.next();
				if(fad.getFilterType().equals(type))
				{
					tmp.add(fad);
				}
			}
			return(tmp);
		}
		return(null);     
    }
    
    /**
     * @param action
     * @return java.util.ArrayList
     * @roseuid 4050679E0394
     */
    public ArrayList getAllResourcesForAction(String action) {
		if(action != null)
		{
			ArrayList tmp = new ArrayList();
			for(java.util.Iterator it = this.actionResourceList.iterator(); it.hasNext();)
			{
				ActionResourceDescription ard = (ActionResourceDescription)it.next();
				if(ard.getAction().equals(action))
				{
					tmp.add(ard);
				}
			}
			return(tmp);
		}
		return(null);     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 4050759700C1
     */
    public String toString() {
    	String tmp = "**** SecretRepository dump:\n";
    	tmp+="FiltersActions: "+this.filterActionList+"\n";
    	tmp+="ActionResources: "+this.actionResourceList+"\n";
    	tmp+="Resources: "+this.conflictResourceMap;
    	return(tmp);     
    }
    
    /**
     * @param resources
     * @throws Composestar.Core.Exception.ModuleException
     * @roseuid 40BF0292014F
     */
    public void run(CommonResources resources) throws ModuleException {
		DataStore ds = DataStore.instance();
		java.util.Properties props = (java.util.Properties)ds.getObjectByID("config");
		String secretconfigfile = props.getProperty("SECRET_CONFIG");
		SecretFilterXMLParser xmlparser = new SecretFilterXMLParser();
		xmlparser.parse(secretconfigfile, this);     
    }
    
    /**
     * @return Composestar.Core.SECRET.SecretRepository
     * @roseuid 40BF02A100D2
     */
    public static SecretRepository instance() {
     	if( instance == null ) {
     		instance = new SecretRepository();
     	}
     	return instance;     
    }
    
    /**
     * @param filterType
     * @param filterName
     * @return ArrayList
     * @roseuid 40DB6108001C
     */
    public ArrayList getAllResourcesForFilter(String filterType, String filterName) {
    	ArrayList actions = getAllActionsForType(filterType);
 		String tmp1 = filterName;
 		for(int j=0; j<actions.size(); j++)
 		{
 			FilterActionDescription fa = (FilterActionDescription)actions.get(j);
 			tmp1+=fa.getFilterType();
 			ArrayList rsrcs = getAllResourcesForAction(fa.getAction());
 			for(int i=0; i<rsrcs.size(); i++)
 			{
 				//TODO: thingies
 			}
 		}
 		return null;
    }
    
    /**
     * @param filterName
     * @param filterType
     * @param resourceName
     * @param operation
     * @roseuid 40DB612600E8
     */
    public void addResourceOperationForFilter(String filterName, String filterType, String resourceName, String operation) {
     
    }
}
