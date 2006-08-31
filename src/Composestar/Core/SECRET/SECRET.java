//Source file: F:\\composestar\\src\\Composestar\\core\\SECRET\\SECRET.java

//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\SECRET.java

package Composestar.Core.SECRET;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE.ActionNode;
import Composestar.Core.FIRE.DepthFirstIterator;
import Composestar.Core.FIRE.FilterReasoningEngine;
import Composestar.Core.FIRE.Node;
import Composestar.Core.FIRE.PathIterator;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SECRET.filterxmlparser.SecretFilterXMLParser;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * 
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Composestar.Core.SECRET.cat,v 1.15 2004/03/11 15:05:07 pascal_durr Exp 
 * $
 * 
 * This class is the main entry point of the application. It is called from the 
 * master to activate the analysis
 */
public class SECRET implements CTCommonModule {

	private DataStore datastore;
	public SecretFilterXMLParser xmlparser;
	public AbstractVM avm;

	public SecretFilterInformationHarvestor theSecretFilterInformationHarvestor;

	/**
	 * @roseuid 403F17660308
	 */
	public SECRET() {
		datastore = DataStore.instance();
	}

	public void run(CommonResources resources) throws ModuleException {
		Iterator conIter = datastore.getAllInstancesOf(Concern.class);

		while (conIter.hasNext()) {
			Concern concern = (Concern) conIter.next();

			if (concern.getDynObject("superImpInfo") != null) {
				System.err.println(
					"(SECRET) Checking concern " + concern.getQualifiedName());
				this.checkConcern(concern);
			}
		}
	}

	protected List getFilterList(List filterModules)
	{
		List list = new ArrayList();
		
		Iterator itr = filterModules.iterator();
		while (itr.hasNext())
		{
			String name = (String) itr.next();
		
			FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID(name);
			Iterator ifItr = fm.getInputFilterIterator();
		
			while (ifItr.hasNext())
			{
				Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f = (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter) ifItr.next();
				list.add(f);
			}	
		}
		
		return list;
	}

	/**
	 * @param resources
	 * @throws Composestar.Core.Exception.ModuleException
	 */
	public void checkConcern(Concern concern) throws ModuleException {
		// Get the datastore and extract the properties
		datastore = DataStore.instance();
	 	
	 	FilterModuleOrder singleOrder = (FilterModuleOrder) concern.getDynObject("SingleOrder");
		
		
		if( singleOrder != null )
		{
			System.err.println("(SECRET) Checking order " + SECRET.printFilterModuleOrder(singleOrder));
			FilterReasoningEngine fire = new FilterReasoningEngine(new LinkedList(singleOrder.orderAsList()));
			fire.run();
			Node fireTree = fire.getTree();
			//fireTree.removeVoidNodes();
			checkOrder( concern, getFilterList(singleOrder.orderAsList()), fireTree);
		}
	}

	public void checkOrder(Concern concern, List filters, Node fireTree) throws ModuleException
	{
		int pathcount = 0;
		
		for( int i = fireTree.numberOfChildren()-1; i >=0; i-- )
		{
			ActionNode childNode = (ActionNode) fireTree.getChild(i);
			if( childNode.getTarget().getName().compareTo("*") != 0 )
			{
				System.out.println("Removing branch with init " + childNode.getTarget().getName() + "." + childNode.getSelector().getName());
				fireTree.removeChild(i);
			}
			else
			{
				System.out.println("Leaving branch with init " + childNode.getTarget().getName() + "." + childNode.getSelector().getName());
			}
		}
		
		for( DepthFirstIterator dfi = (DepthFirstIterator) fireTree.getIterator(DepthFirstIterator.class); !dfi.isDone() ; )
		{
			Node n = dfi.getNode();
			if( !n.hasChildren() )
			{
				
				Vector v = new Vector();
				for( PathIterator pi = new PathIterator(dfi); !pi.isDone(); )
				{
					v.add(pi.getNode());
					pi.next();
				}
				Object[] path = v.toArray();
				
				System.out.println("==== PATH " + pathcount + "===========");
				for( int i = 0; i < path.length; i++ )
				{
					if( path[i] instanceof ActionNode )
						System.out.print("" + ((ActionNode) path[i]).getTarget().getName()+"."+((ActionNode) path[i]).getSelector().getName()+" >> ");
					System.out.print(" " + path[i]);
					if( path[i] instanceof ActionNode )
						System.out.print(" {" + ((ActionNode) path[i]).getFilterNumber()+"}");
					System.out.println("");
				}
				System.out.println("");
				
				pathcount++;
			}
			dfi.next();
		}
	}

	
	/*
	public void blaat()
	{
		List list = (List)resources.getResource("filters");
		ArrayList filtermodules = new ArrayList(list);
		filters = new ArrayList();
		for(int i=0; i<filtermodules.size(); i++)
		{
	 		FilterModuleReference fmr = (FilterModuleReference)filtermodules.get(i);
			FilterModule fm = fmr.getRef();
	 		Iterator it = fm.getInputFilterIterator();
	 		while(it.hasNext())
	 		{
	 			Filter filter = (Filter)it.next();
	 			filters.add(filter);
	 		}
		}
		
		if(debug) System.out.println("Filters: "+filters);
	 	
		java.util.StringTokenizer st = new java.util.StringTokenizer(secretoptions,"/",false); 
		ArrayList options = new ArrayList();
		while(st.hasMoreTokens()) { options.add(st.nextToken()); }
		
		SecretRepository sr = SecretRepository.instance();
		
		if(debug) System.out.println("");
		if(debug) System.out.println("Filters: "+filters);
	 	if(debug) System.out.println("AbstractVM created, init done...");
		HTMLReporter htmlrep = new HTMLReporter(filtermodules, props.getProperty("SECRET_output_html_file"),sr,(String)resources.getResource("currentconcern")); 
	 	htmlrep.initBuffer();
	
	 	AbstractVM avm = new AbstractVM(htmlrep);
		avm.init();
		
		HashSet resourcelist = new HashSet();
		for(int i=0; i<filters.size(); i++)
	 	{
	 		Filter filter = (Filter)filters.get(i);
	 		String type = filter.getFilterType().getType();
			
			// TODO: remove?
			if( type.equals("Meta") ) {
				type = Composestar.Core.ACTING.Acting.getActingFilterType(filter);
			}
			
	 		ArrayList actions = sr.getAllActionsForType(type);
	 		String tmp1 = filter.getName();
	 		for(int j=0; j<actions.size(); j++)
	 		{
	 			FilterActionDescription fa = (FilterActionDescription)actions.get(j);
	 			tmp1+=fa.getFilterType();
	 			ArrayList rsrcs = sr.getAllResourcesForAction(fa.getAction());
	 			for(int k=0; k<rsrcs.size(); k++)
	 			{
	 				ActionResourceDescription ar = (ActionResourceDescription)rsrcs.get(k);
	 				ActionDescription ad = new ActionDescription();
	 				ad.setAction(ar.getOperation());
	 				ad.setActor(filter.getName()+"."+fa.getAction());
	 				avm.addActionDescription(ar.getResource(),ad);
	 				ResourceDescription rd = new ResourceDescription();
	 				rd.setAlphabet("");
	 				rd.setOKRegex(sr.getConflictDefinitionForResource(ar.getResource()));
	 				avm.addResourceDescription(ar.getResource(),rd);
	 				resourcelist.add(ar.getResource());
	 			}
	 		}
	 	}
		//Add the optional elements
		for(int i=0; i<options.size(); i++)
		{
			String tmp = (String)options.get(i);
			ArrayList rsrcs = sr.getAllResourcesForAction(tmp);
	        for(int j=0; j<rsrcs.size(); j++)
	        {
	                ActionResourceDescription ar = (ActionResourceDescription)rsrcs.get(j);
	                ActionDescription ad = new ActionDescription();
	                ad.setAction(ar.getOperation());
	                ad.setActor(tmp);
	                avm.addActionDescription(ar.getResource(),ad);
	                ResourceDescription rd = new ResourceDescription();
	                rd.setAlphabet("");
	                rd.setOKRegex(sr.getConflictDefinitionForResource(ar.getResource()));
	                avm.addResourceDescription(ar.getResource(),rd);
	                resourcelist.add(ar.getResource());
	        }
		}
		this.analyze(resourcelist, avm);
		htmlrep.closeBuffer();
		htmlrep.dumpBuffer();     
	}
	*/

	/**
	 * @param resourcelist
	 * @param avm
	 * @roseuid 404F1142028A
	 */
	public void analyze(HashSet resourcelist, AbstractVM avm) {
		Iterator it = resourcelist.iterator();
		while (it.hasNext()) {
			String resource = (String) it.next();
			avm.checkResourceConflict(resource);
		}
		//avm.checkResourceConflict("selector");     
	}
	
	
	
	
	
	
	
	public static String printFilterModuleOrder(FilterModuleOrder fmo)
	{
		String ret = null;
		for( Iterator fmi = fmo.orderAsList().iterator(); fmi.hasNext(); )
		{
			if( ret == null )
				ret = "(" + fmi.next();
			else
				ret += "," + fmi.next();
		}
		ret += ")";
		return ret;
	}
	
}
