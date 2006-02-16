package Composestar.Core.CORE;
import java.util.Iterator;
import java.util.LinkedList;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE.DepthFirstIterator;
import Composestar.Core.FIRE.FilterReasoningEngine;
import Composestar.Core.FIRE.Node;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.Exception.ModuleException;

public class CORE implements CTCommonModule 
{
	protected DataStore datastore;
	protected Node fireTree = null;
	
	public void run(CommonResources resources) throws ModuleException
	{
	 	datastore = DataStore.instance();
    	
		// Get all the concerns
		Iterator conIter = datastore.getAllInstancesOf(Concern.class);
		
		while( conIter.hasNext() )
		{
			Concern concern = (Concern)conIter.next();
		
			if (concern.getDynObject("superImpInfo") != null) 
				printAllInfo(concern);
		
		}
	}
	
	public void printAllInfo(Concern c)
	{
		Debug.out(Debug.MODE_DEBUG, "CORE", "Checking concern:   o/ ");
		Debug.out(Debug.MODE_DEBUG, "CORE", "Checking concern:  /|   ... " + c.getName()+ " ...");
		Debug.out(Debug.MODE_DEBUG, "CORE", "Checking concern:  / \\ ");
		
		FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject("SingleOrder");
		LinkedList filterModules = new LinkedList(fmo.orderAsList());
		
		LinkedList filters = getFilterList(filterModules);
		//printInputFilters(filters);
		
	 	FilterReasoningEngine fire = new FilterReasoningEngine(filterModules);
	 	fire.run();
	 	fireTree = fire.getTree();
	 	
	 	//System.out.println (fireTree.toTreeString());
	 	
	 	printInputFilters(filters);
	 	printDeadCode(filters);
	}
	
	protected LinkedList getFilterList(LinkedList filterModules)
	{
		LinkedList ll = new LinkedList();
		
		Iterator itr = filterModules.iterator();
		while (itr.hasNext())
		{
			String name = (String) itr.next();
			
			FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID(name);
			Iterator ifItr = fm.inputFilters.iterator();
			
			while (ifItr.hasNext())
			{
				Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f = 
					(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter) ifItr.next();
		
				ll.add(f);
			}	
		}
		
		return ll;
	}
	
	public void printInputFilters(LinkedList filters)
	{
		Iterator itr = filters.iterator();
		int i = 0;
		while (itr.hasNext())
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f =
				(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter) itr.next();
			
			Debug.out(Debug.MODE_WARNING, "CORE", "["+ ++i + "] " + f.getName());
			//System.out.println (f.getName());
		}
	}
	
	public void printDeadCode(LinkedList filters)
	{
		Object [] dead = filters.toArray();
		int size = dead.length;
		
		DepthFirstIterator itr = (DepthFirstIterator) fireTree.getIterator(DepthFirstIterator.class);
	
		//  Every filter is dead!
		while (!itr.isDone())
		{
			Node n = itr.getNode();
			
			int foundFilter = n.getFilterNumber();
					
			if (foundFilter <= size && foundFilter > 0)
			{
				// Until proven otherwise.
				// Raise undead!
				if (dead[foundFilter - 1] != null) dead[foundFilter - 1] = null;
			}
			
			itr.next();
		}
		
		// Are there any dead filters?
		boolean isDead = false;
		for (int i = 0; i < size; i++)
		{
			if (dead[i] != null) 
			{
				isDead = true;
				break;
			}
		}
		
		if (isDead)
		{
			Debug.out(Debug.MODE_WARNING, "CORE", "");
			Debug.out(Debug.MODE_WARNING, "CORE", "Found dead or useless filters :(");
			Debug.out(Debug.MODE_WARNING, "CORE", "");
			Debug.out(Debug.MODE_WARNING, "CORE", "Complete filter set:");			
			printInputFilters(filters);
			Debug.out(Debug.MODE_WARNING, "CORE", "");
			Debug.out(Debug.MODE_WARNING, "CORE", "Dead filters in the filter set above are:");
		}
	
		for (int i = 0; i < size; i++)
		{
			if (dead[i] != null)
			{
				Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f =
					(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter) dead[i];
				
				Debug.out(Debug.MODE_WARNING, "CORE", "Filter [" + (i + 1) + "]: " + f.getName());
			}
			
		}
		
		
	}
		
}


