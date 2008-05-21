package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ProgramElementReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * This is a Facade for the SANE module; this is the module that traverses all
 * the concerns in an application and resolves superimpositions.
 */
@ComposestarModule(ID = ModuleNames.SANE, dependsOn = { ModuleNames.COPPER })
public class SANE implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SANE);

	public SANE()
	{}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		DataStore repository = resources.repository();
		Iterator<CpsConcern> cpsConcernIter = repository.getAllInstancesOf(CpsConcern.class);

		// now iterate over the CpsConcerns again and resolve the bindings
		while (cpsConcernIter.hasNext())
		{
			CpsConcern c = cpsConcernIter.next();

			// first resolve the selectors for this entity
			if (c.getSuperImposition() != null)
			{
				Iterator<SelectorDefinition> selIter = c.getSuperImposition().getSelectorIterator();
				while (selIter.hasNext())
				{
					selIter.next().interpret();
				}
			}

			logger.debug("Resolving bindings for concern '" + c.getName() + "'");
			bindFilterModules(c);
			// bindMethods(c);
			// bindConditions(c);
		}
		return ModuleReturnValue.Ok;
	}

	/**
	 * Generates all possible sets of bindings (each represented by
	 * FilterModSIinfo).
	 * 
	 * @param concern
	 */
	protected void bindFilterModules(CpsConcern concern) throws ModuleException
	{
		if (concern.getSuperImposition() != null)
		{
			Iterator<FilterModuleBinding> fmBindingIter = concern.getSuperImposition().getFilterModuleBindingIterator();
			while (fmBindingIter.hasNext())
			{
				FilterModuleBinding fmBinding = fmBindingIter.next();
				SelectorReference selRef = fmBinding.getSelector();
				SelectorDefinition sel = selRef.getRef();

				// all concerns that a selector defines
				Vector<Reference> selConcerns = (Vector<Reference>) sel.getDynObject(SelectorDefinition.INTREP_KEY);
				if (selConcerns == null)
				{
					throw new ModuleException("Selector has not been interpreted!", ModuleNames.SANE);
				}

				// iterate over all concerns in the selector;
				// for each concern, add all the s.i.'ed filtermodules
				for (Reference ref : selConcerns)
				{
					if (!(ref instanceof ConcernReference))
					{
						if (ref instanceof ProgramElementReference)
						{
							ProgramElement pe = ((ProgramElementReference) ref).getRef();
							throw new ModuleException("Filtermodules can only be superimposed on concerns/classes. "
									+ "Selector " + sel.getName() + " matched a " + pe.getUnitType() + " with name '"
									+ pe.getUnitName() + "'.", ModuleNames.SANE, sel);
						}
						else
						{
							throw new ModuleException("Selector " + sel.getName()
									+ " matched an unexpected unit type. " + "Reference is of type " + ref.getClass()
									+ ".", ModuleNames.SANE, sel);
						}
					}

					Concern siConcern = ((ConcernReference) ref).getRef();
					if (siConcern == null)
					{
						throw new ModuleException("Selector " + sel.getName() + " matched null.", ModuleNames.SANE, sel);
					}
					else
					{
						SIinfo siInfo = (SIinfo) siConcern.getDynObject(SIinfo.DATAMAP_KEY);

						// if this is not available yet, create it,
						// including
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

	// /**
	// * Generates all possible sets of bindings (each represented by
	// * MethodSIinfo).
	// *
	// * @param concern
	// * @deprecated
	// */
	// @Deprecated
	// protected void bindMethods(CpsConcern concern) throws ModuleException
	// {
	// if (concern.getSuperImposition() != null)
	// {
	// Iterator<MethodBinding> methodBindingIter =
	// concern.getSuperImposition().getMethodBindingIterator();
	// while (methodBindingIter.hasNext())
	// {
	// MethodBinding methodBinding = methodBindingIter.next();
	// SelectorReference selRef = methodBinding.getSelector();
	// SelectorDefinition sel = selRef.getRef();
	// Vector<ConcernReference> selConcerns = (Vector<ConcernReference>) sel
	// .getDynObject(SelectorDefinition.INTREP_KEY);
	// if (selConcerns == null)
	// {
	// throw new ModuleException("Selector has not been interpreted!",
	// MODULE_NAME);
	// }
	//
	// // iterate over all concerns in the selector;
	// // for each concern, add all the s.i.'ed filtermodules
	// Iterator<ConcernReference> selConcIter = selConcerns.iterator();
	// while (selConcIter.hasNext())
	// {
	// Concern siConcern = selConcIter.next().getRef();
	// SIinfo siInfo = (SIinfo) siConcern.getDynObject(SIinfo.DATAMAP_KEY);
	// // if this is not available yet, create it, including one
	// // alternative.
	// if (siInfo == null)
	// {
	// siInfo = new SIinfo();
	// siConcern.addDynObject(SIinfo.DATAMAP_KEY, siInfo);
	// }
	// siInfo.addMethodsAt(methodBinding.getMethodIterator(), 1);
	// }
	// }
	// }
	// }
	//
	// /**
	// * Generates all possible sets of bindings (each represented by
	// * CondtionsSIinfo).
	// *
	// * @param concern
	// * @deprecated
	// */
	// @Deprecated
	// protected void bindConditions(CpsConcern concern) throws ModuleException
	// {
	// if (concern.getSuperImposition() != null)
	// {
	// Iterator<ConditionBinding> condBindingIter =
	// concern.getSuperImposition().getConditionBindingIterator();
	// while (condBindingIter.hasNext())
	// {
	// ConditionBinding condBinding = condBindingIter.next();
	// SelectorReference selRef = condBinding.getSelector();
	// SelectorDefinition sel = selRef.getRef();
	// Vector<ConcernReference> selConcerns = (Vector<ConcernReference>) sel
	// .getDynObject(SelectorDefinition.INTREP_KEY);
	// if (selConcerns == null)
	// {
	// throw new ModuleException("Selector has not been interpreted!",
	// MODULE_NAME);
	// }
	//
	// // iterate over all concerns in the selector;
	// // for each concern, add all the s.i.'ed filtermodules
	// Iterator<ConcernReference> selConcIter = selConcerns.iterator();
	// while (selConcIter.hasNext())
	// {
	// Concern siConcern = selConcIter.next().getRef();
	// SIinfo siInfo = (SIinfo) siConcern.getDynObject(SIinfo.DATAMAP_KEY);
	// // if this is not available yet, create it, including one
	// // alternative.
	// if (siInfo == null)
	// {
	// siInfo = new SIinfo();
	// siConcern.addDynObject(SIinfo.DATAMAP_KEY, siInfo);
	// }
	// siInfo.addCondsAt(condBinding.getConditionIterator(), 1);
	// }
	// }
	// }
	// }
}
