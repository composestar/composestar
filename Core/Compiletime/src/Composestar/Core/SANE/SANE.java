package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ProgramElementReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.ConditionBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.MethodBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.CPSIterator;
import Composestar.Utils.Debug;

/**
 * This is a Facade for the SANE module; this is the module that traverses all
 * the concerns in an application and resolves superimpositions.
 */
public class SANE implements CTCommonModule
{
	private static final String MODULE_NAME = "SANE";

	public SANE()
	{}

	/**
	 * Iterates over all instances of CpsConcern in the iterator (i.e.
	 * repository), and creates and attaches an SIinfo instance, then calls
	 * SIInfo.resolveSI().
	 */
	protected static void extractAllConcerns(Iterator entityIt)
	{
		// iterate over all CpsConcerns in the repository:
		while (entityIt.hasNext())
		{
			CpsConcern c = (CpsConcern) entityIt.next();
			// first resolve the selectors for this entity
			if (c.getSuperImposition() != null)
			{
				Iterator selIter = c.getSuperImposition().getSelectorIterator();
				while (selIter.hasNext())
				{
					((SelectorDefinition) selIter.next()).interpret();
				}
			}
		}
	}

	public void run(CommonResources resources) throws ModuleException
	{
		DataStore repository = DataStore.instance();
		Iterator cpsConcernIter = repository.getAllInstancesOf(CpsConcern.class);

		// first resolve all selectors
		extractAllConcerns(cpsConcernIter);

		// now do the bindings
		cpsConcernIter = repository.getAllInstancesOf(CpsConcern.class);

		// now iterate over the CpsConcerns again and resolve the bindings
		while (cpsConcernIter.hasNext())
		{
			CpsConcern c = (CpsConcern) cpsConcernIter.next();
			Debug.out(Debug.MODE_DEBUG, "SANE", "Resolving bindings for concern '" + c.getName() + "'");
			bindFilterModules(c);
			bindMethods(c);
			bindConditions(c);
		}
	}

	/**
	 * Returns an instance of SIinfo associated with the argument concern.
	 * 
	 * @param concern
	 * @return Composestar.Core.SANE.SIinfo
	 * @roseuid 405A0C100376
	 */
	public static SIinfo getSIinfo(Concern concern)
	{
		// FIXME: unused; can be removed.
		return (SIinfo) concern.getDynObject(SIinfo.DATAMAP_KEY);
	}

	/**
	 * Generates all possible sets of bindings (each represented by
	 * FilterModSIinfo).
	 * 
	 * @param concern
	 * @roseuid 40597FA9001D
	 */
	protected static void bindFilterModules(CpsConcern concern) throws ModuleException
	{
		if (concern.getSuperImposition() != null)
		{
			Iterator fmBindingIter = concern.getSuperImposition().getFilterModuleBindingIterator();
			while (fmBindingIter.hasNext())
			{
				FilterModuleBinding fmBinding = (FilterModuleBinding) fmBindingIter.next();
				SelectorReference selRef = fmBinding.getSelector();
				SelectorDefinition sel = selRef.getRef();

				// all concerns that a selector defines
				Vector selConcerns = (Vector) sel.getDynObject("interpretation");
				if (selConcerns == null)
				{
					throw new ModuleException("Selector has not been interpreted!", MODULE_NAME);
				}

				// iterate over all concerns in the selector;
				// for each concern, add all the s.i.'ed filtermodules
				Iterator selConcIter = selConcerns.iterator();
				while (selConcIter.hasNext())
				{
					Reference ref = (Reference) selConcIter.next();
					if (!(ref instanceof ConcernReference))
					{
						if (ref instanceof ProgramElementReference)
						{
							ProgramElement pe = ((ProgramElementReference) ref).getRef();
							throw new ModuleException("Filtermodules can only be superimposed on concerns/classes. "
									+ "Selector " + sel.getName() + " matched a " + pe.getUnitType() + " with name '"
									+ pe.getUnitName() + "'.", MODULE_NAME, sel);
						}
						else
						{
							throw new ModuleException("Selector " + sel.getName()
									+ " matched an unexpected unit type. " + "Reference is of type " + ref.getClass()
									+ ".", MODULE_NAME, sel);
						}
					}

					Concern siConcern = ((ConcernReference) ref).getRef();
					if (siConcern == null)
					{
						throw new ModuleException("Selector " + sel.getName() + " matched null.", MODULE_NAME, sel);
					}
					else
					{
						SIinfo siInfo = (SIinfo) siConcern.getDynObject(SIinfo.DATAMAP_KEY);

						// if this is not available yet, create it, including
						// one alternative.
						if (siInfo == null)
						{
							siInfo = new SIinfo();
							siConcern.addDynObject(SIinfo.DATAMAP_KEY, siInfo);
						}

						siInfo.addFMsAt(fmBinding.getFilterModuleIterator(), fmBinding.getFilterModuleCondition(), 0);
					}
				}
			}
		}
	}

	/**
	 * Generates all possible sets of bindings (each represented by
	 * MethodSIinfo).
	 * 
	 * @param concern
	 * @roseuid 405981390144
	 */
	protected static void bindMethods(CpsConcern concern) throws ModuleException
	{
		Iterator methodBindingIter;
		MethodBinding methodBinding;
		SelectorReference selRef;
		SelectorDefinition sel;
		Vector selConcerns; // all concerns that a selector defines
		CPSIterator selConcIter; // iterator over "
		// CPSIterator methodIter; // Iterator over method references
		SIinfo siInfo;
		Concern siConcern; // used to refer to a superimposed concern
		if (concern.getSuperImposition() != null)
		{
			methodBindingIter = concern.getSuperImposition().getMethodBindingIterator();
			while (methodBindingIter.hasNext())
			{
				methodBinding = (MethodBinding) methodBindingIter.next();
				selRef = methodBinding.getSelector();
				sel = selRef.getRef();
				selConcerns = (Vector) sel.getDynObject("interpretation");
				if (selConcerns == null)
				{
					throw new ModuleException("Selector has not been interpreted!", MODULE_NAME);
				}

				// iterate over all concerns in the selector;
				// for each concern, add all the s.i.'ed filtermodules
				selConcIter = new CPSIterator(selConcerns);
				while (selConcIter.hasNext())
				{
					siConcern = ((ConcernReference) selConcIter.next()).getRef();
					siInfo = (SIinfo) siConcern.getDynObject(SIinfo.DATAMAP_KEY);
					// if this is not available yet, create it, including one
					// alternative.
					if (siInfo == null)
					{
						siInfo = new SIinfo();
						siConcern.addDynObject(SIinfo.DATAMAP_KEY, siInfo);
					}
					siInfo.addMethodsAt(methodBinding.getMethodIterator(), 1);
				} // for
			} // for
		} // if
	} // bindFilterModules()

	/**
	 * Generates all possible sets of bindings (each represented by
	 * CondtionsSIinfo).
	 * 
	 * @param concern
	 * @roseuid 4059814001B2
	 */
	protected static void bindConditions(CpsConcern concern) throws ModuleException
	{
		// get an iterator over all the condition bindings;
		// if there is no superimposition, get an iterator over an empty vector
		Iterator condBindingIter;
		ConditionBinding condBinding;
		SelectorReference selRef;
		SelectorDefinition sel;
		Vector selConcerns; // all concerns that a selector defines
		CPSIterator selConcIter; // iterator over "
		// Iterator methodIter; // Iterator over method references
		SIinfo siInfo;
		Concern siConcern; // used to refer to a superimposed concern
		if (concern.getSuperImposition() != null)
		{
			condBindingIter = concern.getSuperImposition().getConditionBindingIterator();
			while (condBindingIter.hasNext())
			{
				condBinding = (ConditionBinding) condBindingIter.next();
				selRef = condBinding.getSelector();
				sel = selRef.getRef();
				selConcerns = (Vector) sel.getDynObject("interpretation");
				if (selConcerns == null)
				{
					throw new ModuleException("Selector has not been interpreted!", MODULE_NAME);
				}

				// iterate over all concerns in the selector;
				// for each concern, add all the s.i.'ed filtermodules
				selConcIter = new CPSIterator(selConcerns);
				while (selConcIter.hasNext())
				{
					siConcern = ((ConcernReference) selConcIter.next()).getRef();
					siInfo = (SIinfo) siConcern.getDynObject(SIinfo.DATAMAP_KEY);
					// if this is not available yet, create it, including one
					// alternative.
					if (siInfo == null)
					{
						siInfo = new SIinfo();
						siConcern.addDynObject(SIinfo.DATAMAP_KEY, siInfo);
					}
					siInfo.addCondsAt(condBinding.getConditionIterator(), 1);
				} // for
			} // for
		} // if
	} // bindFilterModules()
}
