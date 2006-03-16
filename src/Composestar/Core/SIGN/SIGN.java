package Composestar.Core.SIGN;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: SIGN.java,v 1.8 2006/03/14 12:53:54 pascal_durr Exp $
 * 
**/

import Composestar.Core.FILTH.FilterModuleOrder;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;

import Composestar.Core.LAMA.*;

import Composestar.Core.FIRE.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import Composestar.Core.RepositoryImplementation.TypedDeclaration;

import Composestar.Utils.Debug;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.Exception.ModuleException;

public class SIGN implements CTCommonModule 
{
	 private HashMap treeMap = new HashMap();
	 protected DataStore datastore = DataStore.instance();
	 private INCRE incre;
	
	 private ArrayList noFilters = new ArrayList();
	 private HashMap toBeProcessed = new HashMap();
	 private HashMap alreadyProcessed = new HashMap(); 		

	 public SIGN()
	 {
		this.incre = INCRE.instance();
	 }
	 
	 public void run(CommonResources resources) throws ModuleException
	 {
		 if(incre.isModuleInc("SIGN"))
		 {// SIGN is incremental
		    
			 // Strategy: Group the concerns into three groups
			 // 1. concerns with one or more modified 'SIGN dependencies' => toBeProcessed concerns
			 // 2. concerns with unmodified dependencies => alreadyProcessed concerns
			 // 3. concerns with no filters superimposed => noFilters concerns

			 // Iterate over all the concerns
			 Iterator conIter = datastore.getAllInstancesOf(Concern.class);
			 while( conIter.hasNext() )
			 {
				 Concern concern = (Concern)conIter.next();
				 
				 if (concern.getDynObject("superImpInfo") != null){
					 // check if the 'SIGN-dependencies' have changed
					 if(incre.isProcessedByModule(concern,"SIGN")){
						// dependencies not modified => add temporarily to 'alreadyProcessed'
						this.alreadyProcessed.put(concern.getUniqueID(),concern); 
					 }
					 else{
						// dependencies modified => add to 'toBeProcessed'
						this.toBeProcessed.put(concern.getUniqueID(),concern);
					 }
				 }
				 else{
					 // no filters superimposed => add to 'noFilters'
					 this.noFilters.add(concern);
				 }
			 }

			 // Double-check the concerns added to alreadyProcessed
			 // Check the internals and externals of the concerns. Are they already processed by SIGN?!
			 // If NOT => signature may be modified => add concern to 'toBeProcessed' instead
			
			 boolean modified = true;
			 while(modified)
			 {
				 modified = false;
				 Iterator concIter = this.alreadyProcessed.values().iterator();
				 while( concIter.hasNext() )
				 {	
					 // iterate over concerns in alreadyProcessed list
					 Concern concern = (Concern)concIter.next();
					 
					 Iterator filterModulesItr = getSIFilterModuleIterator(concern); 
					 while (filterModulesItr.hasNext())
					 {
						 FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)filterModulesItr.next());
						 Iterator itr = fm.getInternalIterator();
						 Iterator extr = fm.getExternalIterator();	
						 
						 while (itr.hasNext() || extr.hasNext())
						 {	
							 // Get internal/external
							 TypedDeclaration typedDecl;
							 if(itr.hasNext())
								typedDecl = (TypedDeclaration)itr.next();
							 else
								typedDecl = (TypedDeclaration)extr.next();

							 Concern foundconcern = typedDecl.getType().getRef();	
							 if (foundconcern != null)
							 {
								 if(foundconcern.getDynObject("superImpInfo") != null)
								 {
									 // check if internal/external is in alreadyProcessed
									 if(this.alreadyProcessed.containsKey(foundconcern.getUniqueID())){}
									 else 
									 {
										 // remove concern from alreadyProcessed and put in toBeProcessed
										 this.alreadyProcessed.remove(concern.getUniqueID());
										 this.toBeProcessed.put(concern.getUniqueID(),concern);	
										 concIter = this.alreadyProcessed.values().iterator();
										 modified = true; // others concerns may depend on this => repeat loop
									 }			
								 }	
								 else 
								 {
									 // check the dependencies of the internal/external
									 //System.out.println("Checking internal/external "+foundconcern.getUniqueID()+" for "+concern.getUniqueID());	
									 if(!incre.isProcessedByModule(foundconcern,"SIGN"))
									 {
										 // internal/external has one or more modified dependencies =>
										 // remove concern from alreadyProcessed and add to toBeProcessed
										 this.alreadyProcessed.remove(concern.getUniqueID());
										 this.toBeProcessed.put(concern.getUniqueID(),concern);	
										 concIter = this.alreadyProcessed.values().iterator();
										 modified = true; // others concerns may depend on this => repeat loop
									 }
								 }
							 }
						 }// end of internals iteration
					 }
				 }// end of concerns iteration
			 }

			 /* At this point the concerns have been grouped into three groups
			  * Group noFilters: the concerns without filters superimposed so they keep their original signatures
			  * Group alreadyProcessed: the concerns that have been processed in an earlier compilation run.
			  * Their signatures can be copied from the history repository. They don't need to be processed again! 
			  * Group toBeProcessed: concerns to be processed by SIGN. Their signatures could be modified!
			  */	

		 }
		 else 
		 {// SIGN is not incremental
			 
			 // Group concerns into two groups
			 // 1. concerns without filters superimposed 
			 // 2. concerns with filters superimposed => 'toBeProcessed' concerns
			
			 Iterator conIter = datastore.getAllInstancesOf(Concern.class);
			 while( conIter.hasNext() )
			 {
				 Concern concern = (Concern)conIter.next();
				 if (concern.getDynObject("superImpInfo") != null)
					 this.toBeProcessed.put(concern.getUniqueID(),concern);
				 else 
					 this.noFilters.add(concern);
			 }
		 }

		 Debug.out (Debug.MODE_DEBUG, "SIGN", "INCRE has recognized "+this.alreadyProcessed.size()+" concerns to be skipped!");
		 Debug.out (Debug.MODE_INFORMATION, "SIGN", "Number of signatures to be (re)calculated: "+this.toBeProcessed.size());

		 
		 INCRETimer maximizing = incre.getReporter().openProcess("SIGN","Maximizing signatures",INCRETimer.TYPE_INCREMENTAL);	
		 maximizeSignatures(); // for 'toBeProcessed' and 'noFilters' concerns only
		 maximizing.stop();	
		 
		 INCRETimer calcsignatures = incre.getReporter().openProcess("SIGN","Calculating signatures",INCRETimer.TYPE_INCREMENTAL);	
		 signatureCalculation(); // for 'toBeProcessed' concerns only
		 calcsignatures.stop();	

		 INCRETimer marksignatures = incre.getReporter().openProcess("SIGN","Marking signatures",INCRETimer.TYPE_INCREMENTAL);	
		 markSignatures(); // for all concerns
		 marksignatures.stop();	
	
		 selectorNameConversion(); // look for selector name conversion when using Dispatch-filter
		 
		 INCRETimer printsignatures = incre.getReporter().openProcess("SIGN","Printing signatures",INCRETimer.TYPE_INCREMENTAL);	
		 printConcernMethods(resources); // for all concerns
		 printsignatures.stop();			
	  }
	 

	 public void maximizeSignatures()
	 {
       	
	    // Original signature for all concerns without filters
		Iterator conIter = this.noFilters.iterator();
		
		while( conIter.hasNext() )
		{
			Concern concern = (Concern)conIter.next();
			//System.out.println("Original signature for "+concern.getName());
			originalSignature(concern);	
		}	
		
		//Initialize maximum-signature calculation for all 'toBeProcessed' concerns
		conIter = this.toBeProcessed.values().iterator();
		while( conIter.hasNext() )
		{
			Concern concern = (Concern)conIter.next();
			maxSignatureInit(concern);
		}
		
		boolean modified;
		do
		{
			
			modified = false;
			//conIter = datastore.getAllInstancesOf(Concern.class);
			conIter = this.toBeProcessed.values().iterator();

			while (conIter.hasNext())
			{
				Concern concern = (Concern) conIter.next();
				if (concern.getSignature().getStatus() != Signature.SOLVED) maxSignature(concern);
				
				if (concern.getSignature().getStatus() == Signature.RECALCMAXSIG)
					modified = true;			
			}			
		} while (modified);
	 }

	 protected void maxSignatureInit(Concern c)
	 {
		Signature signature = getSignature(c);
		LinkedList dnmi = getMethodList(c);
		

		// Add all (usr src) methods to the signature with status unknown.
		for (int i = 0; i < dnmi.size(); i++)
			signature.add ((MethodInfo)dnmi.get(i), MethodWrapper.UNKNOWN);

		// We check the internals and externals of each super imposed filtermodule. 
		Iterator filterModulesItr = getSIFilterModuleIterator(c); 
		while (filterModulesItr.hasNext())
		{
			//FilterModule fm = ((FilterModuleReference) filterModulesItr.next()).getRef();
			// I cast a spell on you.
			FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)filterModulesItr.next());
			
			// Add the methods of the super imposed internals.
			Iterator internalItr = fm.getInternalIterator(); 
			addMethodsToSignature(signature, internalItr);
	
			// Add the methods of the super imposed externals. 		
			Iterator externalItr = fm.getExternalIterator(); 
			addMethodsToSignature(signature, externalItr);
		}	
		
		// Signature must be recalculated
		signature.setStatus(Signature.RECALCMAXSIG);
	 }
	 
	 protected void maxSignature(Concern c)
	 {
		Signature signature = getSignature(c);
		signature.setStatus(Signature.UNSOLVED);
		
		// Recheck the internals  
		Iterator filterModulesItr = getSIFilterModuleIterator(c); 
		while (filterModulesItr.hasNext())
		{
			FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)filterModulesItr.next());
		
			// Get the signature s from each concern. 
			Iterator itr = fm.getInternalIterator();
			while (itr.hasNext())
			{
				TypedDeclaration typedDecl = (TypedDeclaration)itr.next();
				Concern foundConcern = typedDecl.getType().getRef();	
				if (foundConcern != null)
				{
					Signature s = foundConcern.getSignature();
					
					// Do we rely on modified signatures? 
					if (s.getStatus() == Signature.RECALCMAXSIG)
					{
						Iterator methodItr = s.getMethods().iterator();
						
						while (methodItr.hasNext())
						{
							 MethodInfo method = (MethodInfo) methodItr.next();
							
							 // Do we have this signature?
							 if (!signature.hasMethod(method))
							 {
							 	// No, so add this one.
							 	signature.add(method, MethodWrapper.UNKNOWN);
							 	
							 	// Hey, we updated our own signature. Maybe other signatures depend on us.  
							 	signature.setStatus(Signature.RECALCMAXSIG);
							 }
						}						
					}
				}
			}
		}	
	 }

	 
	public void originalSignature(Concern c)
	{
		Signature signature = getSignature(c);
		LinkedList dnmi = getMethodList(c);
	
		for (int i = 0; i < dnmi.size(); i++)
		{
			if((MethodInfo)dnmi.get(i)!=null)
			{
					signature.add ((MethodInfo)dnmi.get(i), MethodWrapper.NORMAL);
			}
		}
	 
		signature.setStatus(Signature.SOLVED);
		c.setSignature(signature);
		String qn = c.getQualifiedName();
		DataStore.instance().getObjectByID(qn);
	 }

	 protected Signature getSignature(Concern c)
	 {
		Signature signature = c.getSignature();
		if (signature == null) 
		{
			signature = new Signature();
			c.setSignature(signature);
		}
		
		return signature;
	 }

	 protected LinkedList getMethodList(Concern c)
	 {
		
		Type dt = (Type) c.getPlatformRepresentation();
		if (dt == null) return new LinkedList();
		
		return new LinkedList(dt.getMethods());
	 }
	 
	 protected Iterator getSIFilterModuleIterator (Concern c)
	 {
	 	// Get the Super Imposed Filter module information. We take the first filter module
		// alternative to obtain this information.
	 	/*
		SIinfo sinfo = (SIinfo)c.getDynObject("superImpInfo");
		FilterModSIinfo finfo = (FilterModSIinfo)(sinfo.getFilterModSIAlts()).get(0);
		return finfo.getIter();
		*/
		FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject("SingleOrder");
		return fmo.orderAsList().iterator();
	 }
	 
	 public void maximizeSignature(Concern c)
	 {
		Signature signature = getSignature(c);
		LinkedList dnmi = getMethodList(c);

		// Add all (usr src) methods to the signature with status unknown.
		for (int i = 0; i < dnmi.size(); i++)
			signature.add ((MethodInfo)dnmi.get(i), MethodWrapper.UNKNOWN);

		// We check the internals and externals of each super imposed filtermodule. 
		Iterator filterModulesItr = getSIFilterModuleIterator(c); 
		while (filterModulesItr.hasNext())
		{
			//FilterModule fm = ((FilterModuleReference) filterModulesItr.next()).getRef();
			// I cast a spell on you.
			FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)filterModulesItr.next());
		
			// Add the methods of the super imposed internals.
			Iterator internalItr = fm.getInternalIterator(); 
			addMethodsToSignature(signature, internalItr);
	
			// Add the methods of the super imposed externals. 		
			Iterator externalItr = fm.getExternalIterator(); 
			addMethodsToSignature(signature, externalItr);
		}	
		
		// Set the status of the Signature to UNSOLVED
		signature.setStatus(Signature.UNSOLVED);
	 }
	 
	 public void addMethodsToSignature(Signature signature, Iterator itr)
	 {
		while (itr.hasNext())
		{
			TypedDeclaration typedDecl = (TypedDeclaration)itr.next();
			Concern foundConcern = typedDecl.getType().getRef();
		
			if (foundConcern != null)
			{
				LinkedList methods = getMethodList(foundConcern);

				// Add all (imposed) methods to the signature with status unknown.
				for (int i = 0; i < methods.size(); i++)
					signature.add ((MethodInfo)methods.get(i), MethodWrapper.UNKNOWN);
			}
		}
	 }
	 
	 
	 public void signatureCalculation()
	 {
	    // Get all the concerns
	    // Iterator concernItr = (DataStore.instance()).getAllInstancesOf(Concern.class);
	    Iterator concernItr = this.toBeProcessed.values().iterator();

		while( concernItr.hasNext() )
		{
			Concern concern = (Concern)concernItr.next();			
			Signature signature = getSignature(concern);
			
			boolean modified = true;
			
			while (signature.getStatus() == Signature.UNSOLVED)
			{
				if (!modified) 
				{
					System.err.println("Unsolvable cycle detected in concern " + concern.toString());
					System.exit(1);
				}
				
				modified = solveConcern(concern);
			}
		}
	 }
	 
	 
	 public boolean solveConcern(Concern c)
	 {
	 	boolean modified = false;
	 
		Signature signature = c.getSignature();
	 	signature.setStatus(Signature.SOLVING);
	 
	 	HashSet dependencies = new HashSet();

 		Node fireTree = getFIRETree(c);
	 	
	 	Iterator wrappers = signature.getMethodWrapperIterator();
	 	while (wrappers.hasNext())
	 	{
	 		MethodWrapper mw = (MethodWrapper) wrappers.next();
	 		
	 		// Is this method already solved?
	 		if (mw.getRelationType() == MethodWrapper.UNKNOWN)
	 		{
	 			String method = mw.getMethodInfo().name();
	 			switch (methodIsExecutable(dependencies, fireTree, c.getName(), method, c.getQualifiedName()))
	 			{
	 				case FilterReasoningEngine.TRUE: 
	 					mw.setRelationType(MethodWrapper.ADDED);
	 					modified = true;
	 					break;
	 				
	 				case FilterReasoningEngine.FALSE: 
	 					mw.setRelationType(MethodWrapper.REMOVED);
	 					modified = true;
	 					break;
	 				default:
	 					break;
	 			}
	 		}
	 	}

	 	if (dependencies.size() == 0)
	 	{
	 		signature.setStatus(Signature.SOLVED);
	 		return true;
	 	}

	 	Iterator depItr = dependencies.iterator();
	 	while (depItr.hasNext())
	 	{
	 		Concern depConcern = (Concern) depItr.next();
	 		
	 		if (depConcern.getSignature().getStatus() != Signature.SOLVING)
	 		{
	 			modified |= solveConcern(depConcern);	 			
	 		}
	 	}
	 	
	 	return modified;	
	 }
	 
	 protected Node getFIRETree(Concern c)
	 {
	 	
	 	if (treeMap.containsKey(c.getName()))
	 		return (Node) treeMap.get(c.getName());

	 	// create new node
	 	FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject("SingleOrder");
	 	FilterReasoningEngine fire = new FilterReasoningEngine(new LinkedList(fmo.orderAsList()));
	 	fire.run();
	 	Node node = fire.getTree();
	 	
	 	treeMap.put(c.getName(), node);
	 	//Debug.out(Debug.MODE_DEBUG, "Sign", node.toTreeString());
	 	
	 	return node;
	 	//}
	 }
	 
	 // ============================================ //
	 // All code and no sleep makes Ray a dull boy [The Shining] 
	 protected int methodIsExecutable(HashSet dependencies, Node fireTree, String target, String selector, String concernName)
	 {
	 	ActionNode startWith = new ActionNode();
	 	startWith.setFIREInfo(fireTree.getFIREInfo()); //Copy FIRE info.
	 	startWith.setSelector(selector);
	 	startWith.setTarget(target);
	 	
	 	FilterActionNode match = new FilterActionNode ("Dispatch");
	 	//VoidNode match = new VoidNode();
	 	//match.addChild(new FilterActionNode ("Dispatch"));
	 	//match.addChild(new FilterActionNode ("Meta"));
	 	
	 	startWith.setPerfectMatch(true);
	 	startWith.setConditionCheck(false);
	 	
	 	return fireTree.exists(dependencies, startWith, match, concernName);
	  }
	 
	 /*
	  * A selector name X in the matching part can be dispatched to a selector name Y in the subtitution part.
	  * This means that selector name converion is applied (this occurs only with name matching with Dispatch filter).
	  * This means a signature must be added based on information in the inputfilters.
	  * 
	  * If the selector name is different in the matching part and the substitution part then
	  * the method signature is not part of an internal or external and probably not of the inner.
	  * The signature of the internal or external is taken and the selector name is changed.
	  * 
	  * This is done for all concerns with superimposed filtermodules
	  * 
	  */
	 protected void selectorNameConversion() throws ModuleException{
		 
		 //put all concerns with filtermodules in a map
		 HashMap withFilter = new HashMap();
		 withFilter.putAll(this.alreadyProcessed);
		 withFilter.putAll(this.toBeProcessed);
		 Iterator conIter = withFilter.values().iterator();
		 
		 //iterate over the concerns
		 while( conIter.hasNext() )
		 {
			Concern concern = (Concern)conIter.next();
			Signature signature = concern.getSignature();
			Iterator filterModulesItr = getSIFilterModuleIterator(concern);

			//iterate over filtermodules
			while(filterModulesItr.hasNext()){
				FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)filterModulesItr.next());
				Iterator inputItr = fm.getInputFilterIterator();
				
			 	//iterate over inputfilters
				while(inputItr.hasNext()){
					Filter inputfilter = (Filter) inputItr.next();
					
					//only Dispatch filters should be converted
					if(inputfilter.getFilterType().getType().equals("Dispatch")){
						Iterator elementIter = inputfilter.getFilterElementIterator();
						
						//iterate over filter elements
						while(elementIter.hasNext()){
							FilterElement element = (FilterElement) elementIter.next();
							Iterator matchingItr = element.getMatchingPatternIterator();
							
							//iterate over matching patterns
							while(matchingItr.hasNext()){
								MatchingPattern pattern = (MatchingPattern) matchingItr.next();
								
								//get matching and substitution part
								MatchingPart matching = pattern.getMatchingPart();
								SubstitutionPart substitution = pattern.getSubstitutionPart();
								
								//both matching and substitution must be present
								if(matching!= null && substitution!=null){
									//get matching and substitution selector
									MessageSelector matchingSelector = matching.getSelector();
									MessageSelector substitutionSelector = substitution.getSelector();
									
									//get matching and substitution selector names
									String matchingSelectorName = matchingSelector.getName();
									String substitutionSelectorName = substitutionSelector.getName();
									
									String substitutionTargetName = substitution.getTarget().getName();
									
									//only add signature if selector names are different
									if(!matchingSelectorName.equals(substitutionSelectorName)){
										
										//the method must not be implemented in the inner already
										if(!signature.hasMethod(matchingSelectorName)){	
											
											//retrieve internal and external
											Iterator internalIter = fm.getInternalIterator();
											Iterator externalIter = fm.getExternalIterator();
											TypedDeclaration internalOrExternal=null;
											while(internalIter.hasNext() || externalIter.hasNext()){
												if(internalIter.hasNext())
													internalOrExternal = (TypedDeclaration)internalIter.next();
												 else
													internalOrExternal = (TypedDeclaration)externalIter.next();
												if(internalOrExternal.getName().equals(substitutionTargetName)){
													break;
												}
											}
											Concern foundConcern = internalOrExternal.getType().getRef();
											if (foundConcern != null){
												//retrieve methodinfo from internal
												LinkedList methods = getMethodList(foundConcern);
												MethodInfo mi;
												for (int i = 0; i < methods.size(); i++){
													mi = (MethodInfo)methods.get(i);
													if(mi.Name.equals(substitutionSelectorName)){
														
														//clone methodinfo an use a different selector name and parent
														MethodInfo miClone;
														try{
															//inner is the parent
															Type innerObject = (Type) concern.getPlatformRepresentation();
															miClone = mi.getClone(matchingSelectorName, innerObject);
														}
														catch(Exception e){
															throw new ModuleException(e.getMessage());
														}
														signature.add (miClone, MethodWrapper.ADDED);
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
			}
		}
	 }
	 
	 public void printConcernMethods(CommonResources resources)
	 {
    	boolean signaturesmodified = false;
		datastore = DataStore.instance();
    	
		// Get all the concerns
		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		
		while( conIter.hasNext() )
		{
			Concern concern = (Concern)conIter.next();
				
			Signature st = concern.getSignature();
			if (st != null && concern.getDynObject("superImpInfo") != null)
			{
				Debug.out (Debug.MODE_INFORMATION, "Sign", "\tSignature for concern: "+concern.getQualifiedName());
				
				// Show them your goodies.
				Iterator itr = (st.getMethodWrappers()).iterator();
				
				while (itr.hasNext())
				{
					MethodWrapper mw = (MethodWrapper) itr.next();
					if(mw.getRelationType()==MethodWrapper.REMOVED || mw.getRelationType()==MethodWrapper.ADDED)
							signaturesmodified = true;
					
					String relation = "?";
					if(mw.getRelationType()==MethodWrapper.ADDED) relation = "added";
					if(mw.getRelationType()==MethodWrapper.REMOVED) relation = "removed";
					if(mw.getRelationType()==MethodWrapper.NORMAL) relation = "kept";
					
					//TODO: remove this, needed for demo!
					if(!Configuration.instance().getProperty("Platform").equalsIgnoreCase("c"))
					{
						String returntype = mw.theMethodInfo.returnType().Name ;
						
						String parameters = "";
						Iterator itrpara = mw.theMethodInfo.getParameters().iterator();
						while (itrpara.hasNext())
						{
							ParameterInfo parainfo = (ParameterInfo) itrpara.next();
							if (parameters.equalsIgnoreCase("") && parainfo.parameterType() != null) {
								parameters = parainfo.parameterType().Name;
							}
							else {
								parameters = parameters + ", " + parainfo.Name;
							}
							
						}
						Debug.out (Debug.MODE_INFORMATION, "Sign", "\t\t[ " + relation +  " ]  (" + returntype + ") " + mw.getMethodInfo().name() + "(" + parameters + ")");
					}
				}
			}
		}
	 
		 resources.addResource("signaturesmodified", Boolean.valueOf(signaturesmodified));
	 }
	 
	 public void markSignatures()
	 {
		 
    	datastore = DataStore.instance();
	 	Iterator conIter = datastore.getAllInstancesOf(Concern.class);
	   
		while( conIter.hasNext() )
		{
			Concern concern = (Concern)conIter.next();
			
			if(!this.alreadyProcessed.containsKey(concern.getUniqueID()))
			{
				LinkedList dnmi = getMethodList(concern);
				Signature signature = concern.getSignature();
				
				for (int i = 0; i < dnmi.size(); i++)
				{
					MethodWrapper mw = signature.getMethodWrapper((MethodInfo)dnmi.get(i));
					
					if (mw.getRelationType() == MethodWrapper.ADDED)
					{
						mw.setRelationType(MethodWrapper.NORMAL);										
					}
				}
				
				List removed = signature.getMethodWrappers(MethodWrapper.REMOVED);
				Iterator removeItr = removed.iterator();
				while(removeItr.hasNext())
				{
					MethodWrapper mw = (MethodWrapper)removeItr.next();
					MethodInfo minfo = mw.getMethodInfo();
					if(!dnmi.contains(minfo)){
						// not in original signature
						signature.removeMethodWrapper(mw);
					}
				}
			}
			else 
			{
				// INCRE profit => Copy signature for 'alreadyProcessed' concerns
				Concern old = (Concern)incre.findHistoryObject(concern);
				if(old.theSignature!=null)
				{
					concern.theSignature = old.theSignature;
				}
			}
		}
	 }
}
