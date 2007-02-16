/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH;

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElementAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPartAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatternAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SignatureMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPartAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.VoidFilterCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.VoidFilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * @author Staijen
 */
public class InnerDispatcher
{

	private static FilterModuleReference innerDispatchReference;

	private InnerDispatcher()
	{}

	public static FilterModuleReference getInnerDispatchReference()
	{
		if (innerDispatchReference == null)
		{
			innerDispatchReference = InnerDispatcher.createInnerDispatchReference();
		}
		return innerDispatchReference;
	}

	private static FilterModule createInnerDispatchFilterModule()
	{
		// create a conern
		CpsConcern cc = new CpsConcern();
		cc.setName("CpsDefaultInnerDispatchConcern");

		// create filtermodule
		FilterModuleAST fm = new FilterModuleAST();
		cc.addFilterModuleAST(fm);
		fm.setParent(cc);
		fm.setName("CpsDefaultInnerDispatchFilterModule");

		// add the filter to the filtermodule
		fm.addInputFilter(createInnerDispatchFilter(cc, fm, "CpsDefaultInnerInputDispatchFilter"));
		// TODO: it should be a "send" filter, but not present for the runtime
		//fm.addOutputFilter(createInnerDispatchFilter(cc, fm, "CpsDefaultInnerOutputDispatchFilter"));

		// add concern and filtermodule to the datastore
		DataStore.instance().addObject(cc);
		DataStore.instance().addObject(fm);

		// create the instances
		FilterModule fmInstance = new FilterModule(fm, new Vector(), "0");
		DataStore.instance().addObject(fmInstance);

		// return the filtermodule
		return fmInstance;
	}

	private static FilterAST createInnerDispatchFilter(CpsConcern cc, FilterModuleAST fm, String name)
	{
		// create a filter
		FilterAST f = new FilterAST();
		f.setParent(fm);
		f.setName(name);

		// create the filtertype and set it in the filter (typeImplementation as
		// well)
		FilterType filterType = FilterType.getFilterType("Dispatch");
		ConcernReference typeImplementation = new ConcernReference();
		// typeImplementation.setName(filterType.getType());
		// typeImplementation.setRef(filterType);
		// typeImplementation.setResolved(true);
		// f.setTypeImplementation(typeImplementation);
		f.setFilterType(filterType);
		f.setRightOperator(new VoidFilterCompOper());

		// create a filterelement
		FilterElementAST fe = new FilterElementAST();
		f.addFilterElement(fe);
		fe.setConditionPart(new True());
		fe.setEnableOperatorType(new EnableOperator());
		fe.setRightOperator(new VoidFilterElementCompOper());

		// create the matchingpattern
		Target mtarget = new Target();
		mtarget.setName(Target.INNER);
		DeclaredObjectReference dor = new DeclaredObjectReference();
		dor.setName(Target.INNER);
		dor.setConcern(cc.getName());
		mtarget.setRef(dor);
		dor.setFilterModule(fm.getName());
		MessageSelectorAST selector = new MessageSelectorAST();
		selector.setName("*");
		MatchingPatternAST mpattern = new MatchingPatternAST();
		MatchingPartAST mpart = new MatchingPartAST();
		mpart.setTarget(mtarget);
		mpart.setSelector(selector);
		mpart.setMatchType(new SignatureMatchingType());
		mpattern.addMatchingPart(mpart);

		SubstitutionPartAST spart = new SubstitutionPartAST();
		Target starget = new Target();
		starget.setName(Target.INNER);

		DeclaredObjectReference subdor = new DeclaredObjectReference();
		subdor.setName(Target.INNER);
		subdor.setConcern(cc.getName());
		starget.setRef(subdor);
		subdor.setFilterModule(fm.getName());

		spart.setTarget(starget);
		spart.setSelector(selector);
		mpattern.addSubstitutionPart(spart);

		// fe.addMatchingPattern(mpattern);
		fe.setMatchingPattern(mpattern);

		return f;
	}

	private static FilterModuleReference createInnerDispatchReference()
	{
		FilterModule fm = InnerDispatcher.createInnerDispatchFilterModule();
		FilterModuleReference fmr = new FilterModuleReference();
		fmr.setName(fm.getName());
		fmr.setConcern(((CpsConcern) fm.getParent()).getName());
		fmr.setRef(fm);
		fmr.setResolved(true);
		return fmr;
	}
}
