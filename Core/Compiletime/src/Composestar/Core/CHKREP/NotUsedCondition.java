/*
* This file is part of Composestar project [http://composestar.sf.net].
* Copyright (C) 2005 University of Twente.
*
* Licensed under LGPL v2.1 or (at your option) any later version.
* [http://www.fsf.org/copyleft/lgpl.html]
*
* $Id$
*/
package Composestar.Core.CHKREP;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.Exception.ModuleException;

import java.util.*;

/**
 * @author DoornenbalD
 * Checks on not used Conditions.
 * Iterates on the FilerModules to get the Condtions and then checks
 * per condtion the input and output filters.
 */
public class NotUsedCondition implements BaseChecker {
	private DataStore ds;
	
	/**
	 * Performs the check. Calls isUsedInFilters for both input and output filters
	 */
	public boolean performCheck() {
		Iterator filterModuleIterator = ds.getAllInstancesOf(FilterModule.class);
		while(filterModuleIterator.hasNext()){
			FilterModule fm = (FilterModule) filterModuleIterator.next();
			Iterator ci = fm.getConditionIterator();
			while (ci.hasNext()){
				Condition c = (Condition) ci.next(); 
		
				boolean used = (isUsedInFilters(c, fm.getInputFilterIterator()) || isUsedInFilters(c, fm.getOutputFilterIterator()));
			
				if(!used){
					Debug.out(Debug.MODE_WARNING, "CHKREP", "Condition " + c.getName() + " is declared, but never used", c.getDescriptionFileName(), c.getDescriptionLineNumber());
				}
			}
		}	
		
		return true;
	}

	/* (non-Javadoc)
	 * @see Composestar.Core.CHKREP.BaseChecker#check(Composestar.Core.RepositoryImplementation.DataStore)
	 */
	public void check(DataStore newDs) throws ModuleException {
		ds = newDs;
		performCheck();
	}

	/*
	 * Does the check for a filter iterator, calls isUsedInConditionExpression
	 */
	private boolean isUsedInFilters(Condition c, Iterator fi){
		boolean used = false;
		
		if (fi != null){
			while(fi.hasNext()){
				Filter f = (Filter) fi.next();
				Iterator filterIterator = f.getFilterElementIterator();
				while(filterIterator.hasNext()){
					FilterElement fe = (FilterElement) filterIterator.next();
					ConditionExpression ce = fe.getConditionPart();
					if (ce != null && !used){
						used = isUsedInConditionExpression(c, ce);
					}
				}
			}
		}
		return used;
	}
	
	/**
	 * The recursive function to search in a condition expression.
	 * @param c  the condition to be found
	 * @param ce the condition were to to look 
	 * @return found (used)
	 */
	private boolean isUsedInConditionExpression(Condition c, ConditionExpression ce){
		boolean used = false;
		
		/*If it is a Literal then there is a condition name, match this 
		* with the Condition name and voila
		*/
		if(ce instanceof ConditionLiteral){
			ConditionLiteral cl = (ConditionLiteral) ce;
			if(cl.getCondition().getName().equals(c.getName())){
				used = true;
			}
		}
		
		//Checks on a Not
		if(ce instanceof Not){
			Not n = (Not) ce;
			used = isUsedInConditionExpression(c, n.getOperand());
		}
		
		// checks in an And
		if(ce instanceof And){
			And a = (And) ce;
			used = (isUsedInConditionExpression(c, a.getLeft()) || isUsedInConditionExpression(c, a.getRight()));
		}
		
		//copied from And
		if(ce instanceof Or){
			Or o = (Or) ce;
			used = (isUsedInConditionExpression(c, o.getLeft()) || isUsedInConditionExpression(c, o.getRight()));
		}
		
		return used;
	}
}
