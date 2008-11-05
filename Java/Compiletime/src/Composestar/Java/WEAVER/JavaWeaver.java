package Composestar.Java.WEAVER;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Config.Project;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH2.DefaultInnerDispatchNames;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Java.COMP.CStarJavaCompiler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Weaver starting point
 * 
 * @see Composestar.Java.WEAVER.JavaWeaver#run(CommonResources)
 */
@ComposestarModule(ID = ModuleNames.WEAVER, dependsOn = { ComposestarModule.DEPEND_ALL })
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

	protected Repository repos;

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
		repos = resources.repository();

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
		// Iterator<CompiledImplementation> it =
		// repos.getAllInstancesOf(CompiledImplementation.class);
		// while (it.hasNext())
		// {
		// CompiledImplementation ci = it.next();
		// String className = ci.getClassName();
		// if (className != null)
		// {
		// hd.addAfterInstantationInterception(className);
		// }
		// }
		// Iterator<CpsConcern> it2 = repos.getAllInstancesOf(CpsConcern.class);
		// while (it2.hasNext())
		// {
		// CpsConcern c = it2.next();
		// Object impl = c.getDynObject("IMPLEMENTATION");
		// if (impl != null)
		// {
		// PrimitiveConcern pc = (PrimitiveConcern) impl;
		// hd.addAfterInstantationInterception(pc.getFullyQualifiedName());
		// }
		// }
		for (Concern c : repos.getAll(Concern.class))
		{
			if (c.getSuperimposed() != null)
			{
				hd.addAfterInstantationInterception(c.getFullyQualifiedName());
			}
		}
	}

	public void getCastInterceptions()
	{
		Set<String> qns = new HashSet<String>();
		for (Concern c : repos.getAll(Concern.class))
		{
			boolean castConcern = false;
			if (c.getSuperimposed() != null)
			{
				List<ImposedFilterModule> fmo = c.getSuperimposed().getFilterModuleOrder();
				for (ImposedFilterModule fmsi : fmo)
				{
					FilterModule fm = fmsi.getFilterModule();
					for (FilterModuleVariable fmvar : fm.getVariables())
					{
						if (!(fmvar instanceof Internal))
						{
							continue;
						}
						Internal internal = (Internal) fmvar;
						String internalQN = internal.getTypeReference().getReference().getFullName();
						castConcern = true;
						if (!qns.contains(internalQN))
						{
							qns.add(internalQN);
							hd.addCastInterception(internalQN);
						}
					}
				}
			}
			if (castConcern && !qns.contains(c.getFullyQualifiedName()))
			{
				qns.add(c.getFullyQualifiedName());
				hd.addCastInterception(c.getFullyQualifiedName());
			}
		}

	}

	public void getMethodInterceptions(CommonResources resources) throws ModuleException
	{
		for (Concern c : repos.getAll(Concern.class))
		{
			if (c.getSuperimposed() == null)
			{
				continue;
			}
			List<ImposedFilterModule> list = c.getSuperimposed().getFilterModuleOrder();
			if (!list.isEmpty())
			{
				if (hasInputFilters(list))
				{
					logger.debug(" method calls to " + c.getFullyQualifiedName() + " added to hook dictionary...");
					hd.addIncomingMethodInterception(c.getFullyQualifiedName());
				}
				if (hasOutputFilters(list))
				{
					logger.debug(" method calls from " + c.getFullyQualifiedName() + " added to hook dictionary...");
					hd.addOutgoingMethodInterception(c.getFullyQualifiedName());
				}
			}
		}
	}

	private boolean hasInputFilters(List<ImposedFilterModule> iterFilterModules)
	{
		for (ImposedFilterModule fmsi : iterFilterModules)
		{
			FilterModule fm = fmsi.getFilterModule();
			if (fm.getInputFilterExpression() != null)
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasOutputFilters(List<ImposedFilterModule> iterFilterModules)
	{
		for (ImposedFilterModule fmsi : iterFilterModules)
		{
			FilterModule fm = fmsi.getFilterModule();
			if (DefaultInnerDispatchNames.FILTER_MODULE.equals(fm.getFullyQualifiedName()))
			{
				continue;
			}
			if (fm.getOutputFilterExpression() != null)
			{
				return true;
			}
		}
		return false;
	}
}
