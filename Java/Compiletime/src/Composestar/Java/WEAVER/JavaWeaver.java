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
import Composestar.Core.FILTH.FILTHService;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.SIinfo;
import Composestar.Core.WEAVER.WEAVER;
import Composestar.Java.COMP.CStarJavaCompiler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Weaver starting point
 * 
 * @see Composestar.Java.WEAVER.JavaWeaver#run(CommonResources)
 */
public class JavaWeaver implements WEAVER
{
	public static final String MODULE_NAME = "WEAVER";

	/**
	 * The Weaver output directory
	 */
	public static final String WEAVE_PATH = "weaver";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * key used to store information in the resources.
	 */
	public static final String WOVEN_CLASSES = "JavaWovenClasses";

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
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 * @see Composestar.Java.WEAVER.HookDictionary
	 * @see Composestar.Java.WEAVER.ClassWeaver
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		// create the hook dictionary...
		createHookDictionary(resources);

		Project project = resources.configuration().getProject();
		ClassWeaver c = new ClassWeaver(resources);
		for (File file : project.getFilesDependencies())
		{
			c.addClasspath(file);
		}
		c.addClasspath((File) resources.get(CStarJavaCompiler.SOURCE_OUT));
		// weave
		c.weave(project);

	}

	public void createHookDictionary(CommonResources resources) throws ModuleException
	{
		getMethodInterceptions(resources);
		getCastInterceptions();
		getAfterInstantationInterceptions();
	}

	public void getAfterInstantationInterceptions()
	{

		DataStore ds = DataStore.instance();
		HookDictionary hd = HookDictionary.instance();
		Iterator it = ds.getAllInstancesOf(CompiledImplementation.class);
		while (it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation) it.next();
			String className = ci.getClassName();
			if (className != null)
			{
				hd.addAfterInstantationInterception(className);
			}
		}
		it = ds.getAllInstancesOf(CpsConcern.class);
		while (it.hasNext())
		{
			CpsConcern c = (CpsConcern) it.next();
			Object impl = c.getDynObject("IMPLEMENTATION");
			if (impl != null)
			{
				PrimitiveConcern pc = (PrimitiveConcern) impl;
				hd.addAfterInstantationInterception(pc.getQualifiedName());
			}
		}
		it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext())
		{
			Concern c = (Concern) it.next();
			if (c.getDynObject(SIinfo.DATAMAP_KEY) != null && !(c instanceof CpsConcern))
			{
				hd.addAfterInstantationInterception(c.getQualifiedName());
			}
		}
	}

	public void getCastInterceptions()
	{

		DataStore ds = DataStore.instance();
		HookDictionary hd = HookDictionary.instance();
		Set qns = new HashSet();
		Iterator iterConcerns = ds.getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();
			boolean castConcern = false;
			if (c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null)
			{
				FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
				for (Object o : fmo.orderAsList())
				{
					String fmn = (String) o;
					FilterModule fm = (FilterModule) ds.getObjectByID(fmn);
					Iterator iterInternals = fm.getInternalIterator();
					while (iterInternals.hasNext())
					{
						Internal internal = (Internal) iterInternals.next();
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

		FILTHService filthservice = FILTHService.getInstance(resources);

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();
			List list = filthservice.getOrder(c);
			if (!list.isEmpty())
			{
				Iterator iterFilterModules = list.iterator();
				if (hasInputFilters(iterFilterModules))
				{
					logger.debug(" method calls to " + c.getQualifiedName() + " added to hook dictionary...");
					HookDictionary.instance().addIncomingMethodInterception(c.getQualifiedName());
				}
				if (hasOutputFilters(iterFilterModules))
				{
					logger.debug(" method calls from " + c.getQualifiedName() + " added to hook dictionary...");
					HookDictionary.instance().addOutgoingMethodInterception(c.getQualifiedName());
				}
			}
		}
	}

	private boolean hasInputFilters(Iterator iterFilterModules)
	{
		while (iterFilterModules.hasNext())
		{
			FilterModule fm = (FilterModule) DataStore.instance().getObjectByID((String) iterFilterModules.next());

			if (fm.getInputFilterIterator().hasNext())
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasOutputFilters(Iterator iterFilterModules)
	{
		while (iterFilterModules.hasNext())
		{
			FilterModule fm = (FilterModule) DataStore.instance().getObjectByID((String) iterFilterModules.next());

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
