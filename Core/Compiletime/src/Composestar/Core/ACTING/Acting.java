/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */package Composestar.Core.ACTING;

import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

import java.util.Iterator;
import java.util.LinkedList;

import Composestar.Core.FIRE.*;

import Composestar.Core.FILTH.*;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElementReference;
import Composestar.Core.Exception.ModuleException;

/**
 * Entrypoint for this tool. Will call a sequence of subtools:
 * 
 * -AttributeHarvester
 * -AttributeCollector
 * -Analyser
 * 
 * The class will also contain the interface to be used by other tools.
 */
public class Acting implements CTCommonModule {
    
    private FILTHService filthService;
    
    /**
     * @roseuid 40AB496D0333
     */
    public Acting() {
     
    }
	
	protected Node getFIRETree(Concern c)
	{
	   if( c.getDynObject("FIRETREE") != null )
	   	  return (Node) c.getDynObject("FIRETREE");

	   // create new node
	   FilterModuleOrder fmo = this.getSingleOrder(c);
	   FilterReasoningEngine fire = new FilterReasoningEngine(new LinkedList(fmo.orderAsList()));
	   fire.run();
	   Node node = fire.getTree();
	 	
	   c.addDynObject("FIRETREE", node);
	 	
	   return node;
	}
    
	/**
	 * @pre Assumes there is an SIinfo otherwise no singleorder is required
     * @param concern
	 */
    public FilterModuleOrder getSingleOrder(Concern concern)
    {
    	FilterModuleOrder order = (FilterModuleOrder) concern.getDynObject("SingleOrder");
    	if( order == null )
    	{
			//System.err.println("Forcing singleorder calculation for " + concern.getQualifiedName());
			//invoking filth
			filthService.getMultipleOrder(concern);
			order = (FilterModuleOrder) concern.getDynObject("SingleOrder");
    	}
    	return order;	
    }
    
    /**
     * @param resources
     * @throws Composestar.Core.Exception.ModuleException
     * @roseuid 40AB49550018
     */
    public void run(CommonResources resources) throws ModuleException {
    	
    	this.filthService = FILTHService.getInstance(resources);
    	
    	/*
    	new AttributeHarvester().run(resources);
	    new AttributeCollector().run(resources);
	    new AttributeAnalyser().run(resources);
	    */
	    
	    DataStore ds = DataStore.instance();
	    
	    Concern concern;
	    FilterModuleOrder filterModuleOrder;
	    FilterModule filterModule;
	    Filter filter;
	    FilterElement filterElement;
	    MatchingPattern matchingPattern;
	    for( Iterator i = ds.getAllInstancesOf(Concern.class); i.hasNext(); )
	    {
	    	concern = (Concern) i.next();
			
			if( concern.getQualifiedName().startsWith("System."))
				continue;
			
			filterModuleOrder = (FilterModuleOrder) concern.getDynObject("SingleOrder");
	    	if( filterModuleOrder != null )
	    	{
	    		for( Iterator fmi = filterModuleOrder.orderAsList().iterator(); fmi.hasNext(); )
	    		{
					filterModule = (FilterModule) ds.getObjectByID((String) fmi.next());
	    			for( Iterator ifi = filterModule.getInputFilterIterator(); ifi.hasNext(); )
	    			{
	    				filter = (Filter) ifi.next();
	    				if( filter.getFilterType().getType().equals("Meta"))
	    				{
	    					//System.err.println("UMF: " + concern.getQualifiedName());
	    					for( Iterator fei = filter.getFilterElementIterator(); fei.hasNext(); )
	    					{
	    						filterElement = (FilterElement) fei.next();
	    						for( Iterator mpi = filterElement.getMatchingPatternIterator(); mpi.hasNext(); )
	    						{
	    							matchingPattern = (MatchingPattern) mpi.next();
	    							Iterator sps = matchingPattern.getSubstitutionPartsIterator();
	    							while( sps.hasNext() ) {
		    							Target target = ((SubstitutionPart)sps.next()).getTarget();
		    							if( target.getRef() instanceof DeclaredObjectReference )
		    							{
		    								DeclaredObjectReference dor = (DeclaredObjectReference) target.getRef();
											if( dor.getResolved() )
											{
												//System.err.println("ACT Method: " + dor.getRef().getType().getQualifiedName() + "." + matchingPattern.getSubstitutionPart().getSelector().getName());
												Concern act = dor.getRef().getType().getRef();
												if( act.getDynObject("superImpInfo") !=  null )
												{
													this.getFIRETree(act);
													//System.err.println("Nodetree created for this ACT " + act.getQualifiedName());
												}
												else
												{
													//System.err.println("Simple act, using scenario's directly (" + act.getQualifiedName() + ")");
												}
											}											
		    							}
		    							else
		    							{
		    								//System.err.println("ACTING: Unsupported meta usage (static?)");
		    							}
	    							}
	    						}
	    					}
	    				}	    					
	    			}
	    		}
	    	}
	    }
	    
	    
	    
    }
    
    /**
     * Returns <FullyQualifiedClassName>.<methodName> of the method that called in 
     * this metafilter
     * @param meta
     * @return java.lang.String
     * @roseuid 40C861AE027D
     */
    public static String getActingFilterType(Filter meta) {
		String retval = "";
		FilterModule fm = (FilterModule) meta.getParent();
		SubstitutionPart sp = (SubstitutionPart) meta.getFilterElement(0).getMatchingPattern().getSubstitutionParts().firstElement();
		FilterModuleElementReference fmer = sp.getTarget().getRef();
		Target t = sp.getTarget();
		retval += sp.getSelector().getName();
		
		if( fm.getName().equals(fmer.getFilterModule()))
		{
			java.util.Iterator iterator;
			boolean found = false;
			for( iterator = fm.getInternalIterator(); iterator.hasNext() && !found; )
			{
				Internal internal = (Internal) iterator.next();
				if( internal.getName().equals(t.getName()) )
				{
					found = true;
					retval = internal.getType().getName();
				}
			}
			for( iterator = fm.getExternalIterator(); iterator.hasNext() && !found; )
			{
				External external = (External) iterator.next();
				if( external.getName().equals(t.getName()) )
				{
					found = true;
					retval = external.getType().getName();
				}
			}
			if( !found )
			// didn't find anything, returning "Meta" 
			return meta.getFilterType().getType();
		}
		retval += '.' + sp.getSelector().getName();
		return retval;     
    }
}
