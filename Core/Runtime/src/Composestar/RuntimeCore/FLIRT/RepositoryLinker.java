/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 */
package Composestar.RuntimeCore.FLIRT;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.Utils.CPSIterator;

import java.util.*;

public class RepositoryLinker 
{
	public DataStore dataStore;
	public static HashMap filterModuleReferenceMap = new HashMap();
	//private FilterElementRuntime previous_fer = null;
	private FilterRuntime previous_fr = null;
	private FilterElementCompositionOperatorRuntime previous_operator = null;
    
	public RepositoryLinker(DataStore dataStore)
	{
		this.dataStore = dataStore;
	}
    
	public void link() throws Exception 
	{
		Iterator concernIter = this.dataStore.getAllInstancesOf(CpsConcern.class);
		while(concernIter.hasNext())
		{
			CpsConcern cc = (CpsConcern)concernIter.next();
			Iterator filterModuleIter = cc.getFilterModuleIterator();
			while(filterModuleIter.hasNext())
			{
				String fullname = cc.getName();
				FilterModule fm = (FilterModule)filterModuleIter.next();
				fullname = fullname + '.' +fm.getName();
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"Repository Linker","Adding filtermodule '"+fm.getName()+"' from '"+fm+"'.");
				RepositoryLinker.filterModuleReferenceMap.put(fullname,linkFilterModule(fm));
			}
		}
	}

	private FilterModuleRuntime linkFilterModule(FilterModule filterModule)throws Exception
	{
		FilterModuleRuntime filterModuleRuntime;
		//FILTERMODULE: create runtime filtermodule, link the compiletime filtermodules to it, 
		//and add runtime filter module into compiletime datastore
		filterModuleRuntime = new FilterModuleRuntime();
		filterModuleRuntime.setReference(filterModule);
		dataStore.addObject(filterModuleRuntime);

		//iterate over all filters and link them
		Iterator filterIter = filterModule.getInputFilterIterator();
		while(filterIter.hasNext())
			linkFilter((Filter)filterIter.next(),filterModuleRuntime, true);
		filterIter = filterModule.getOutputFilterIterator();
		while(filterIter.hasNext())	
			linkFilter((Filter)filterIter.next(),filterModuleRuntime, false);
		
		filterModule.addDynObject("RuntimeReference",filterModuleRuntime);
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"Repository Linker", "Linking '"+filterModule+"' with '"+filterModuleRuntime+"'.");
		
		return filterModuleRuntime;
	}
	
	private void linkFilter(Filter filter, FilterModuleRuntime filterModuleRuntime, boolean input )throws Exception
	{
		FilterRuntime filterRuntime;
					
		//INPUTFILTER: create a runtime input filter, link the compiletime input filter to it,
		//and add the runtime input filter to the runtime filtermodule
		filterRuntime = new FilterRuntime();
		filterRuntime.setReference(filter);
		if( input )
			filterModuleRuntime.addInputFilter(filterRuntime);
		else
			filterModuleRuntime.addOutputFilter(filterRuntime);
		
		//Set the previous element so that the linking occurs ok
		this.previous_fr = filterRuntime;

		//link right argument
		linkRightArgument(filter.getRightOperator(),filterRuntime);

		//link the filter type
		linkFilterType(filter.getFilterType(),filterRuntime);	
	
		//link filter elements
		Iterator filterElementIterator = filter.getFilterElementIterator();
		while(filterElementIterator.hasNext())
		{
			linkFilterElement((FilterElement)filterElementIterator.next(),filterRuntime);
		}
		
	}

	private void linkRightArgument(FilterCompOper filterCompOper, FilterRuntime filterRuntime)
	{
		FilterCompositionOperatorRuntime filterCompositionOperatorRuntime;

		//RIGHTARGUMENT: create runtime filter composition operator, link the compiletime 
		//filter composition operator to it, and add the runtime right argument to the runtime filter
		if(filterCompOper instanceof VoidFilterCompOper)
			filterCompositionOperatorRuntime = new VoidFilterCompositionOperatorRuntime(this.previous_fr);
		else
			filterCompositionOperatorRuntime = new SeqFilterCompositionOperatorRuntime(this.previous_fr);
		filterCompositionOperatorRuntime.setReference(filterCompOper);
		filterRuntime.rightArgument = filterCompositionOperatorRuntime;
		filterCompositionOperatorRuntime.rightOperator = filterRuntime;
	}

	private void linkFilterType(FilterType filterType, FilterRuntime filterRuntime) throws Exception
	{
		// Create a .NET specific FilterFactory to support custom filter types.
		FilterFactory filter = FilterFactory.getInstance();
		FilterTypeRuntime filterTypeRuntime = filter.getFilterTypeFor(filterType);

		if(filterTypeRuntime == null)
			throw new Exception("Unexpected filter type encountered at runtime. Edit RepositoryLinker class to include this new type: "+ filterType );
		
		filterRuntime.theFilterTypeRuntime = filterTypeRuntime;
	}

	private void linkFilterElement(FilterElement filterElement, FilterRuntime filterRuntime) throws Exception
	{
		FilterElementRuntime filterElementRuntime;

		//FILTERELEMENT:create runtime filter element, link the compiletime 
		//filter element to it, and add the runtime filter element to the runtime filter
		filterElementRuntime = new FilterElementRuntime();
		filterElementRuntime.setReference(filterElement);
		filterRuntime.addFilterElement(filterElementRuntime);
		filterElementRuntime.theFilter = filterRuntime;

		if(this.previous_operator != null)
		{
			this.previous_operator.rightArgument = filterElementRuntime;
		}

		//link right operator
		linkRightOperator(filterElement.getRightOperator(), filterElementRuntime);
		
		//link matching patterns
		Iterator matchingPatternIter = filterElement.getMatchingPatternIterator();
		while(matchingPatternIter.hasNext())
			linkMatchingPattern((MatchingPattern)matchingPatternIter.next(),filterElementRuntime);

		//link the enable operator type 
		linkEnableOperatorType(filterElement.getEnableOperatorType(), filterElementRuntime);

		//link condition part
		linkConditionPart(filterElement.getConditionPart(), filterElementRuntime);
	}		
	
	private void linkRightOperator(FilterElementCompOper rightOperator, FilterElementRuntime filterElementRuntime)
	{
		FilterElementCompositionOperatorRuntime filterElementCompositionOperatorRuntime;

		//RIGHTOPERATOR: create runtime filter element composition operator, link the compiletime 
		//filter element composition operator to it, and add the runtime filter element composition operator 
		//to the runtime filter element.
		if(rightOperator instanceof VoidFilterElementCompOper)
			filterElementCompositionOperatorRuntime = new VoidFilterElementCompositionOperatorRuntime(filterElementRuntime);
		else
			filterElementCompositionOperatorRuntime = new CorFilterElementCompositionOperatorRuntime(filterElementRuntime);

		filterElementCompositionOperatorRuntime.setReference(rightOperator);
		filterElementRuntime.rightOperator = filterElementCompositionOperatorRuntime;
		//filterElementCompositionOperatorRuntime.rightArgument = filterElementRuntime;
		this.previous_operator = filterElementCompositionOperatorRuntime;
	}

	private void linkMatchingPattern(MatchingPattern matchingPattern, FilterElementRuntime filterElementRuntime)
	{
		MatchingPatternRuntime matchingPatternRuntime;
		//MATCHINGPATTERN:create runtime matching pattern, link the compiletime 
		//matching pattern to it, and add the runtime matching pattern to the runtime filter element
		matchingPatternRuntime = new MatchingPatternRuntime();
		matchingPatternRuntime.setReference(matchingPattern);
		filterElementRuntime.matchingPatterns.add(matchingPatternRuntime);
		matchingPatternRuntime.theFilterElement = filterElementRuntime;

		//link the matching part 
		linkMatchingParts(matchingPattern.getMatchingPartsIterator(), matchingPatternRuntime);

		//link the substitution part
		if(matchingPattern.getSubstitutionParts().size() != 0 )
		{
			linkSubstitutionParts(matchingPattern.getSubstitutionPartsIterator(), matchingPatternRuntime);
		}
		else
		{ // If no substitution part was found will create it based on the matching part so <inner.*> == <inner.*>inner.*
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"Repository Linker","Creating substitution part...");
			Vector matchparts = matchingPattern.getMatchingParts();
			Vector subparts = new Vector();
			for( int i = 0; i < matchparts.size(); i++ )
			{
				SubstitutionPart subpart = new SubstitutionPart();
				subpart.setTarget(((MatchingPart)matchparts.elementAt(i)).getTarget());
				subpart.setSelector(((MatchingPart)matchparts.elementAt(i)).getSelector());
				subpart.setParent(matchingPattern);
				subparts.addElement( subpart );
			}
			linkSubstitutionParts(new CPSIterator(subparts), matchingPatternRuntime);
		}
	}

	private void linkMatchingParts( Iterator matchingParts, MatchingPatternRuntime matchingPatternRuntime ) 
	{
		while( matchingParts.hasNext() )
		{
			MatchingPart matchingPart = (MatchingPart) matchingParts.next();
			linkMatchingPart( matchingPart, matchingPatternRuntime );
		}
	}
	
	private void linkMatchingPart(MatchingPart matchingPart, MatchingPatternRuntime  matchingPatternRuntime)
	{
		MatchingPartRuntime matchingPartRuntime;
		//MATCHINGPART:create runtime matching part, link the compiletime 
		//matching part to it, and add the runtime matching part to the runtime matching pattern
		matchingPartRuntime = new MatchingPartRuntime();
		matchingPartRuntime.setReference(matchingPart);
		matchingPatternRuntime.addMatchingPart( matchingPartRuntime );

		//link matching type
		linkMatchingType(matchingPart.getMatchType(), matchingPartRuntime);

		//link selector
		linkSelector(matchingPart.getSelector(), matchingPartRuntime);

		//link target
		linkTarget(matchingPart.getTarget(), matchingPartRuntime);
	}

	private void linkMatchingType(MatchingType matchingType, MatchingPartRuntime matchingPartRuntime)
	{
		MatchingTypeRuntime matchingTypeRuntime;

		//THEMATCHINGTYPERUNTIME: create runtime matching type, link the compiletime 
		//matching type to it, and add the runtime matching type to the runtime matching part
		if(matchingType instanceof SignatureMatchingType)
			matchingTypeRuntime = new SignatureMatchingRuntime();
		else
			matchingTypeRuntime = new NameMatchingRuntime();
		matchingTypeRuntime.setReference(matchingType);
		matchingPartRuntime.theMatchingTypeRuntime = matchingTypeRuntime;
		matchingPartRuntime.theMatchingTypeRuntime.parentMatchingPart = matchingPartRuntime;
	}

	private void linkSelector(MessageSelector messageSelector, MatchingPartRuntime matchingPartRuntime)
	{
		SelectorRuntime selectorRuntime;
		//THESELECTORRUNTIME:create runtime selector, link the compiletime 
		//selector to it, and add the runtime selector to the runtime matching part
		selectorRuntime = new SelectorRuntime();
		selectorRuntime.setReference(messageSelector);
		matchingPartRuntime.theSelectorRuntime = selectorRuntime;
	}
	
	private void linkTarget(Target messageTarget, MatchingPartRuntime matchingPartRuntime)
	{
		TargetRuntime targetRuntime;
		//THETARGETRUNTIME:create runtime target, link the compiletime 
		//target to it, and add the runtime target to the runtime matching part
		targetRuntime = new TargetRuntime();
		targetRuntime.setReference(messageTarget);
		matchingPartRuntime.theTargetRuntime = targetRuntime;
	}
	
	private void linkSubstitutionParts( Iterator substitutionParts, MatchingPatternRuntime matchingPatternRuntime ) 
	{
		while( substitutionParts.hasNext() )
		{
			SubstitutionPart substitutionPart = (SubstitutionPart) substitutionParts.next();
			if( substitutionPart.getTarget() != null 
				&& substitutionPart.getSelector() != null
				&& !substitutionPart.getTarget().toString().equals("")
				&& !substitutionPart.getTarget().toString().equals("") ) 
			{
				linkSubstitutionPart( substitutionPart, matchingPatternRuntime );
			}
		}
	}

	private void linkSubstitutionPart(SubstitutionPart substitutionPart, MatchingPatternRuntime  matchingPatternRuntime)
	{
		SubstitutionPartRuntime substitutionPartRuntime;
		//SUBSTITUTIONPART:create runtime substitution part, link the compiletime 
		//substitution part to it, and add the runtime substitution part to the runtime matching pattern
		substitutionPartRuntime = new SubstitutionPartRuntime();
		substitutionPartRuntime.setReference(substitutionPart);
		matchingPatternRuntime.addSubstitutionPart( substitutionPartRuntime );

		if(substitutionPart != null)
		{
			//link selector
			linkSelector(substitutionPart.getSelector(), substitutionPartRuntime);
			//link target
			linkTarget(substitutionPart.getTarget(), substitutionPartRuntime);
		}
	}

	private void linkSelector(MessageSelector messageSelector, SubstitutionPartRuntime substitutionPartRuntime)
	{
		SelectorRuntime selectorRuntime;
		//THESELECTORRUNTIME:create runtime selector, link the compiletime 
		//selector to it, and add the runtime selector to the runtime matching part
		selectorRuntime = new SelectorRuntime();
		selectorRuntime.setReference(messageSelector);
		substitutionPartRuntime.theSelectorRuntime = selectorRuntime;
	}
	
	private void linkTarget(Target messageTarget, SubstitutionPartRuntime substitutionPartRuntime)
	{
		TargetRuntime targetRuntime;
		//THETARGETRUNTIME:create runtime target, link the compiletime 
		//target to it, and add the runtime target to the runtime matching part
		targetRuntime = new TargetRuntime();
		targetRuntime.setReference(messageTarget);
		substitutionPartRuntime.theTargetRuntime = targetRuntime;
	}

	private void linkEnableOperatorType(EnableOperatorType enableOperatorType, FilterElementRuntime filterElementRuntime)
	{

		EnableOperatorTypeRuntime enableOperatorTypeRuntime;

		//THEENABLEOPERATORTYPERUNTIME: create runtime enable opeator type, link the compiletime 
		//runtime enable opeator type to it, and add the runtime runtime enable opeator type to the runtime matching filter element
		if(enableOperatorType instanceof EnableOperator)
			enableOperatorTypeRuntime = new EnableOperatorRuntime();
		else
			enableOperatorTypeRuntime = new DisableOperatorRuntime();
		enableOperatorTypeRuntime.setReference(enableOperatorType);
		filterElementRuntime.theEnableOperatorTypeRuntime = enableOperatorTypeRuntime;		
	}
	
	/**
	 * Creates an instance of ConditionExpressionRuntime for the given ConditionExpression
     * @param conditionExpression
     */
	private ConditionExpressionRuntime conditionRuntimeFactory(ConditionExpression conditionExpression) throws Exception
	{
		ConditionExpressionRuntime conditionExpressionRuntime;

		if(conditionExpression instanceof And)
		{
			conditionExpressionRuntime = new AndRuntime();
		}
		else if(conditionExpression instanceof ConditionVariable)
		{
			conditionExpressionRuntime = new ConditionVariableRuntime();
		}
		else if(conditionExpression instanceof Not)
		{
			conditionExpressionRuntime = new NotRuntime();
		}
		else if(conditionExpression instanceof True)
		{
			conditionExpressionRuntime = new TrueRuntime();
		}
		else if(conditionExpression instanceof False)
		{
			conditionExpressionRuntime = new FalseRuntime();
		}
		else if(conditionExpression instanceof Or)
		{
			conditionExpressionRuntime = new OrRuntime();
		}
		else
		{
			throw new Exception("Unknown ConditionExpression class: "+conditionExpression.getClass().getName());
		}

		if (conditionExpression instanceof BinaryOperator)
		{
			linkLeft(((And) conditionExpression).getLeft(),(BinaryOperatorRuntime) conditionExpressionRuntime);
			linkRight(((And) conditionExpression).getLeft(),(BinaryOperatorRuntime) conditionExpressionRuntime);
		}
		else if(conditionExpression instanceof UnaryOperator)
		{
			linkOperand(((UnaryOperator) conditionExpression).getOperand(),(UnaryOperatorRuntime) conditionExpressionRuntime);
		}

		return conditionExpressionRuntime;
	}

	private void linkConditionPart(ConditionExpression conditionExpression, FilterElementRuntime filterElementRuntime) throws Exception
	{
		ConditionExpressionRuntime conditionExpressionRuntime = conditionRuntimeFactory(conditionExpression);

		//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"Repository Linker", "***** LinkOperand: "+(conditionExpression instanceof Not));
		
		conditionExpressionRuntime.setReference(conditionExpression);
		filterElementRuntime.conditionpart = conditionExpressionRuntime;	
	}

	private void linkLeft(ConditionExpression conditionExpression, BinaryOperatorRuntime boRuntime) throws Exception
	{
		ConditionExpressionRuntime conditionExpressionRuntime = conditionRuntimeFactory(conditionExpression);

		conditionExpressionRuntime.setReference(conditionExpression);
		boRuntime.left = conditionExpressionRuntime;
		
	}

	private void linkRight(ConditionExpression conditionExpression, BinaryOperatorRuntime boRuntime) throws Exception
	{
		ConditionExpressionRuntime conditionExpressionRuntime = conditionRuntimeFactory(conditionExpression);

		conditionExpressionRuntime.setReference(conditionExpression);
		boRuntime.right = conditionExpressionRuntime;
	}
	
	private void linkOperand(ConditionExpression conditionExpression, UnaryOperatorRuntime uoRuntime) throws Exception
	{
		ConditionExpressionRuntime conditionExpressionRuntime = conditionRuntimeFactory(conditionExpression);

		conditionExpressionRuntime.setReference(conditionExpression);
		uoRuntime.operand = conditionExpressionRuntime;
	}
}