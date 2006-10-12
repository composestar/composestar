//Source file: F:\\composestar\\src\\Composestar\\core\\CKRET\\CkretFilterInformationHarvestor.java

package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;

public class FilterInfo {
    private ArrayList conditions = new ArrayList();
    private boolean targetmatching = false;
    private boolean selectormatching = false;
    private ArrayList substitutions = new ArrayList();
    
    /**
     * @roseuid 40D8440101EB
     */
    public FilterInfo(Filter filter) {
     	this.run(filter);
    }
    
	public List getReadOperations()
	{
		List l = new ArrayList();
		if( targetmatching )
			l.add(new Operation("read","target"));
		if( selectormatching )
			l.add(new Operation("read","selector"));
		for( int i = 0; i < conditions.size(); i++ )
			l.add(new Operation("read", conditions.get(i).toString()));
		return l;
	}

    
   
    /**
     * @param cond_part
     * @param increase
     * @roseuid 40D841900341
     */
    public void collectConditions(ConditionExpression cond_part, boolean increase) {    	
    	if(cond_part instanceof True) //TODO: why?
    	{
    		//this.conditions.add("*");
    	}
    	else if(cond_part instanceof ConditionVariable) // Only name so get it!
    	{
    		ConditionVariable cl = (ConditionVariable)cond_part;
    		String conditionname = cl.getCondition().getRef().getQualifiedName();
    		if(increase)
    		{
    			this.conditions.add(conditionname);
    		}
    		else
    		{
    			this.conditions.remove(conditionname);
    		}
    	}
    	else if(cond_part instanceof UnaryOperator)
    	{
    		UnaryOperator cl = (UnaryOperator) cond_part;
    		this.collectConditions(cl.getOperand(),false);
    	}
    	else if(cond_part instanceof BinaryOperator)
    	{
    		BinaryOperator bo = (BinaryOperator) cond_part;
    		this.collectConditions(bo.getLeft(),true);
    		this.collectConditions(bo.getRight(),true);
    	}     
    }
    
    /**
     * @param matchingpart
     * @roseuid 40D841C001A5
     */
    public void collectMacthingSpecification(Vector matchingparts) {
    	Iterator mpi = new CPSIterator( matchingparts );
    	while( mpi.hasNext() ) {
    		MatchingPart matchingpart = (MatchingPart) mpi.next();
	    	String target = matchingpart.getTarget().getName();
	    	if(!target.equals("*")) // Something to be done the target is read!
	    	{
	    		this.targetmatching = true;
	    	}
	    	String selector = matchingpart.getSelector().getName();
	    	if(!selector.equals("*")) // Something to be done the selector is read!
	    	{
	    		this.selectormatching = true;
	    	}     
	    }
    }
    
    /**
     * @param subspart
     * @roseuid 40D843320234
     */
    public void collectSubstitutionSpecification(Vector subsparts) {
    	if(subsparts.isEmpty() )
    		return;
    	boolean foundTarget = false, foundSelector=false;
    	
    	Iterator spi = new CPSIterator( subsparts );
    	while( spi.hasNext() ) {
    		SubstitutionPart subspart = (SubstitutionPart) spi.next();
    		
	    	String target = subspart.getTarget().getName();
	    	if(!target.equals("*")) // Something to be done the target is read!
	    	{
	    		foundTarget = true;
	    	}
	    	String selector = subspart.getSelector().getName();
	    	if(!selector.equals("*")) // Something to be done the selector is read!
	    	{
	    		foundSelector = true;
	    	}
	    	CPSIterator argiterator = (CPSIterator)subspart.getSelector().getParameterTypeIterator();
	    	while(argiterator.hasNext())
	    	{
	    		this.substitutions.add(((ConcernReference)argiterator.next()).getRef().getName());
	    	}
    	}
    	
    	if( foundTarget ) this.substitutions.add("message.target;");
    	if( foundSelector ) this.substitutions.add("message.selector");
    }
    
    /**
     * @param filter
     * @roseuid 40D846D5011A
     */
    private void run(Filter filter) {
		Iterator iterator = filter.getFilterElementIterator();
		while(iterator.hasNext())
		{
			FilterElement fe = (FilterElement)iterator.next();
			this.collectConditions(fe.getConditionPart(), true);
			CPSIterator matchiterator = (CPSIterator)fe.getMatchingPatternIterator();
			while(matchiterator.hasNext())
			{
				MatchingPattern mp = (MatchingPattern)matchiterator.next();
				Vector mpart = mp.getMatchingParts();
				this.collectMacthingSpecification(mpart);
				Vector spart = mp.getSubstitutionParts();
				this.collectSubstitutionSpecification(spart);
			}
		}
    }
    
}
