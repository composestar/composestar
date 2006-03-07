//Source file: F:\\composestar\\src\\Composestar\\core\\SECRET\\SecretFilterInformationHarvestor.java

package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.List;

import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
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
    	if(cond_part instanceof True)
    	{
    		//this.conditions.add("*");
    	}
    	else if(cond_part.isLiteral()) // Only name so get it!
    	{
    		ConditionLiteral cl = (ConditionLiteral)cond_part;
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
    	else if(cond_part.isUnary()) // Now only NOT but in the future??? We need to fix this
    	{
    		if(cond_part instanceof Not)
    		{
    			Not cl = (Not)cond_part;
    			this.collectConditions(cl.getOperand(),false);
    		}
    	}
    	else if(cond_part.isBinary())
    	{
    		if(cond_part instanceof Or)
    		{
    			Or or = (Or)cond_part;
    			this.collectConditions(or.getLeft(),true);
    			this.collectConditions(or.getRight(),true);
    		}
    		else if(cond_part instanceof And)
    		{
    			And and = (And)cond_part;
    			this.collectConditions(and.getLeft(),true);
    			this.collectConditions(and.getRight(),true);
    		}
    	}     
    }
    
    /**
     * @param matchingpart
     * @roseuid 40D841C001A5
     */
    public void collectMacthingSpecification(MatchingPart matchingpart) {
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
    
    /**
     * @param subspart
     * @roseuid 40D843320234
     */
    public void collectSubstitutionSpecification(SubstitutionPart subspart) {
    	if( subspart == null )
    		return;
    	
    	String target = subspart.getTarget().getName();
    	if(!target.equals("*")) // Something to be done the target is read!
    	{
    		this.substitutions.add("message.target;");
    	}
    	String selector = subspart.getSelector().getName();
    	if(!selector.equals("*")) // Something to be done the selector is read!
    	{
    		this.substitutions.add("message.selector");
    	}
    	CPSIterator argiterator = (CPSIterator)subspart.getSelector().getParameterTypeIterator();
    	while(argiterator.hasNext())
    	{
    		this.substitutions.add(((ConcernReference)argiterator.next()).getRef().getName());
    	}     
    }
    
    /**
     * @param filter
     * @roseuid 40D846D5011A
     */
    private void run(Filter filter) {
		CPSIterator iterator = (CPSIterator)filter.getFilterElementIterator();
		while(iterator.hasNext())
		{
			FilterElement fe = (FilterElement)iterator.next();
			this.collectConditions(fe.getConditionPart(), true);
			CPSIterator matchiterator = (CPSIterator)fe.getMatchingPatternIterator();
			while(matchiterator.hasNext())
			{
				MatchingPattern mp = (MatchingPattern)matchiterator.next();
				MatchingPart mpart = mp.getMatchingPart();
				this.collectMacthingSpecification(mpart);
				SubstitutionPart spart = mp.getSubstitutionPart();
				this.collectSubstitutionSpecification(spart);
			}
		}
    }
    
}
