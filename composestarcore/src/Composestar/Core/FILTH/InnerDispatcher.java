/*
 * Created on 28-jul-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.FILTH;

import Composestar.Core.CpsProgramRepository.CpsConcern.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * @author Staijen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InnerDispatcher {

	private static FilterModuleReference innerDispatchReference;

	public static FilterModuleReference getInnerDispatchReference()
	{
		if( innerDispatchReference == null )
			innerDispatchReference = InnerDispatcher.createInnerDispatchReference();
		return innerDispatchReference;
	}
	
	private static FilterModule createInnerDispatchFilterModule()
	{
		// create a conern
		CpsConcern cc = new CpsConcern();
		cc.setName("CpsDefaultInnerDispatchConcern");
		
		
		// create filtermodule
		FilterModule fm = new FilterModule();
		cc.addFilterModule(fm);
		fm.setParent(cc);
		fm.setName("CpsDefaultInnerDispatchFilterModule");

		// create a filter
		Filter f = new Filter();
		f.setParent(fm);
		f.setName("CpsDefaultInnerDispatchFilter");

		// create the filtertype and set it in the filter (typeImplementation as well)
		FilterType filterType = new FilterType();
		filterType.setType("Dispatch");
		filterType.setName("Dispatch");
		ConcernReference typeImplementation = new ConcernReference();
		typeImplementation.setName(filterType.getName()); 
		typeImplementation.setRef(filterType);
		typeImplementation.setResolved(true);
		f.setTypeImplementation(typeImplementation);
		f.setFilterType(filterType);
		f.setRightOperator(new VoidFilterCompOper());
		
		// create a filterelement
		FilterElement fe = new FilterElement();
		f.addFilterElement(fe);
		fe.setConditionPart(new True());
		fe.setEnableOperatorType(new EnableOperator());
		fe.setRightOperator(new VoidFilterElementCompOper());
		
		// create the matchingpattern
		Target mtarget = new Target();
		mtarget.setName("inner");
		DeclaredObjectReference dor = new DeclaredObjectReference();
		dor.setName("inner");
		dor.setConcern(cc.getName());
		mtarget.setRef(dor);
		dor.setFilterModule(fm.getName());
		MessageSelector selector = new MessageSelector();
		selector.setName("*");
		MatchingPattern mpattern = new MatchingPattern();
		MatchingPart mpart = new MatchingPart();
		mpart.setTarget(mtarget);
		mpart.setSelector(selector);
		mpart.setMatchType(new SignatureMatchingType());
		mpattern.setMatchingPart(mpart);
		
		SubstitutionPart spart = new SubstitutionPart();
		Target starget = new Target();
		starget.setName("inner");
		
		DeclaredObjectReference subdor = new DeclaredObjectReference();
		subdor.setName("inner");
		subdor.setConcern(cc.getName());
		starget.setRef(subdor);
		subdor.setFilterModule(fm.getName());
		
		spart.setTarget(starget);
		spart.setSelector(selector);
		mpattern.setSubstitutionPart(spart);
		
		fe.addMatchingPattern(mpattern);
		
		// add the filter to the filtermodule
		fm.addInputFilter(f);

		// add concern and filtermodule to the datastore
		DataStore.instance().addObject(cc);
		DataStore.instance().addObject(fm);

		// return the filtermodule
		return fm;
		
	}
	
	private static FilterModuleReference createInnerDispatchReference()
	{
		FilterModule fm = InnerDispatcher.createInnerDispatchFilterModule();
		FilterModuleReference fmr = new FilterModuleReference();
		fmr.setName(fm.getName());
		fmr.setConcern(((CpsConcern)fm.getParent()).getName());
		fmr.setRef(fm);
		fmr.setResolved(true);
		return fmr;
	}
}
