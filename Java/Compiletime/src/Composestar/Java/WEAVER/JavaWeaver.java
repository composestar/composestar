package Composestar.Java.WEAVER;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.Config.Project;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Master.CTCommonModule.ModuleReturnValue;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Java.COMP.CStarJavaCompiler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Weaver starting point
 * 
 * @see Composestar.Java.WEAVER.JavaWeaver#run(CommonResources)
 */
public class JavaWeaver implements CTCommonModule
{
	/**
	 * The Weaver output directory
	 */
	public static final String WEAVE_PATH = "weaver";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.WEAVER);

	/**
	 * key used to store information in the resources.
	 */
	public static final String WOVEN_CLASSES = "JavaWovenClasses";

	protected HookDictionary hd;

	protected DataStore ds;

	/**
	 * Constructor
	 */
	public JavaWeaver()
	{}

	/**
	 * Run this module. This module does the following tasks:
	 * <p>
	 * 1. Filling the HookDictionary.
	 * <p>
	 * 2. Adding the Classpaths to the Classpool.
	 * <p>
	 * 3. Calling the ClassWeaver to weave the classes.
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 * @see Composestar.Java.WEAVER.HookDictionary
	 * @see Composestar.Java.WEAVER.ClassWeaver
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		hd = new HookDictionary();
		ds = resources.repository();

		// create the hook dictionary...
		createHookDictionary(resources);

		Project project = resources.configuration().getProject();
		ClassWeaver c = new ClassWeaver(resources, hd);
		for (File file : project.getFilesDependencies())
		{
			c.addClasspath(file);
		}
		c.addClasspath((File) resources.get(CStarJavaCompiler.SOURCE_OUT));
		// weave
		c.weave(project);
		return ModuleReturnValue.Ok;
	}

	public void createHookDictionary(CommonResources resources) throws ModuleException
	{
		getMethodInterceptions(resources);
		getCastInterceptions();
		getAfterInstantationInterceptions();
	}

	public void getAfterInstantationInterceptions()
	{
		Iterator<CompiledImplementation> it = ds.getAllInstancesOf(CompiledImplementation.class);
		while (it.hasNext())
		{
			CompiledImplementation ci = it.next();
			String className = ci.getClassName();
			if (className != null)
			{
				hd.addAfterInstantationInterception(className);
			}
		}
		Iterator<CpsConcern> it2 = ds.getAllInstancesOf(CpsConcern.class);
		while (it2.hasNext())
		{
			CpsConcern c = it2.next();
			Object impl = c.getDynObject("IMPLEMENTATION");
			if (impl != null)
			{
				PrimitiveConcern pc = (PrimitiveConcern) impl;
				hd.addAfterInstantationInterception(pc.getQualifiedName());
			}
		}
		Iterator<Concern> it3 = ds.getAllInstancesOf(Concern.class);
		while (it3.hasNext())
		{
			Concern c = it3.next();
			if (c.getDynObject(SIinfo.DATAMAP_KEY) != null && !(c instanceof CpsConcern))
			{
				hd.addAfterInstantationInterception(c.getQualifiedName());
			}
		}
	}

	public void getCastInterceptions()
	{
		Set<String> qns = new HashSet<String>();
		Iterator<Concern> iterConcerns = ds.getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = iterConcerns.next();
			boolean castConcern = false;
			if (c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null)
			{
				FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
				for (FilterModuleSuperImposition fmsi : (List<FilterModuleSuperImposition>) fmo.filterModuleSIList())
				{
					FilterModule fm = fmsi.getFilterModule().getRef();
					Iterator<Internal> iterInternals = fm.getInternalIterator();
					while (iterInternals.hasNext())
					{
						Internal internal = iterInternals.next();
						String internalQN = internal.getType().getQualifiedName();
						castConcern = true;
						if (!qns.contains(internalQN))
						{
							qns.add(internalQN);
							hd.addCastInterception(internalQN);
						}
					}
				}
			}
			if (castConcern && !qns.contains(c.getQualifiedName()))
			{
				qns.add(c.getQualifiedName());
				hd.addCastInterception(c.getQualifiedName());
			}
		}

	}

	public void getMethodInterceptions(CommonResources resources) throws ModuleException
	{
		Iterator<Concern> iterConcerns = ds.getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = iterConcerns.next();
			FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
			if (fmo == null)
			{
				continue;
			}
			List<FilterModuleSuperImposition> list = fmo.filterModuleSIList();
			if (!list.isEmpty())
			{
				if (hasInputFilters(list))
				{
					logger.debug(" method calls to " + c.getQualifiedName() + " added to hook dictionary...");
					hd.addIncomingMethodInterception(c.getQualifiedName());
				}
				if (hasOutputFilters(list))
				{
					logger.debug(" method calls from " + c.getQualifiedName() + " added to hook dictionary...");
					hd.addOutgoingMethodInterception(c.getQualifiedName());
				}
			}
		}
	}

	private boolean hasInputFilters(List<FilterModuleSuperImposition> iterFilterModules)
	{
		for (FilterModuleSuperImposition fmsi : iterFilterModules)
		{
			FilterModule fm = fmsi.getFilterModule().getRef();
			if (fm.getInputFilterIterator().hasNext())
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasOutputFilters(List<FilterModuleSuperImposition> iterFilterModules)
	{
		for (FilterModuleSuperImposition fmsi : iterFilterModules)
		{
			FilterModule fm = fmsi.getFilterModule().getRef();
			if (InnerDispatcher.isDefaultDispatch(fm))
			{
				continue;
			}
			if (!fm.getOutputFilters().isEmpty())
			{
				return true;
			}
		}
		return false;
	}
}
