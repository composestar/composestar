package Composestar.Java.WEAVER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FILTH.FILTHService;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.WEAVER.WEAVER;
import Composestar.Utils.Debug;

/**
 * Weaver starting point
 * 
 * @see Composestar.Java.WEAVER.JavaWeaver#run(CommonResources)
 */
public class JavaWeaver implements WEAVER
{

	/**
	 * Constructor
	 */
	public JavaWeaver()
	{

	}

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

		// local variables
		ClassWeaver c;
		Project p;
		ArrayList deps;
		Iterator depsIt;
		String dependency;

		Configuration config = Configuration.instance();
		List projects = config.getProjects().getProjects();
		Iterator projIt = projects.iterator();
		while (projIt.hasNext())
		{
			c = new ClassWeaver();

			// add classpaths
			p = (Project) projIt.next();
			deps = p.getDependencies();
			depsIt = deps.iterator();
			while (depsIt.hasNext())
			{
				dependency = ((Dependency) depsIt.next()).getFileName();
				c.addClasspath(dependency);
			}
			c.addClasspath(p.getProperty("buildPath"));

			// weave
			c.weave(p);
		}
	}

	public void createHookDictionary(CommonResources resources)
	{
		getMethodInterceptions(resources);
		getCastInterceptions();
		getAfterInstantationInterceptions();
	}

	public void getAfterInstantationInterceptions()
	{
	/*
	 * DataStore ds = DataStore.instance(); HookDictionary hd =
	 * HookDictionary.instance(); Iterator it =
	 * ds.getAllInstancesOf(CompiledImplementation.class); while (it.hasNext()) {
	 * CompiledImplementation ci = (CompiledImplementation)it.next(); String
	 * className = ci.getClassName(); if (className != null) {
	 * hd.addAfterInstantationInterception(className); } } it =
	 * ds.getAllInstancesOf(CpsConcern.class); while (it.hasNext()) { CpsConcern
	 * c = (CpsConcern)it.next(); Object impl =
	 * c.getDynObject("IMPLEMENTATION"); if (impl != null) { PrimitiveConcern pc =
	 * (PrimitiveConcern)impl;
	 * hd.addAfterInstantationInterception(pc.getQualifiedName()); } } it =
	 * ds.getAllInstancesOf(Concern.class); while (it.hasNext()) { Concern c =
	 * (Concern)it.next(); if (c.getDynObject("superImpInfo") != null && !(c
	 * instanceof CpsConcern)) {
	 * hd.addAfterInstantationInterception(c.getQualifiedName()); } }
	 */
	}

	
	public void getCastInterceptions()
	{
	/*
	 * DataStore ds = DataStore.instance(); HookDictionary hd =
	 * HookDictionary.instance(); Set qns = new HashSet(); Iterator iterConcerns =
	 * ds.getAllInstancesOf(Concern.class); while (iterConcerns.hasNext()) {
	 * Concern c = (Concern) iterConcerns.next(); boolean castConcern = false;
	 * if (c.getDynObject("SingleOrder") != null) { FilterModuleOrder fmo =
	 * (FilterModuleOrder) c.getDynObject("SingleOrder"); Iterator
	 * iterFilterModules = fmo.orderAsList().iterator(); while
	 * (iterFilterModules.hasNext()) { String fmn = (String)
	 * iterFilterModules.next(); FilterModule fm = (FilterModule)
	 * ds.getObjectByID(fmn); Iterator iterInternals = fm.getInternalIterator();
	 * while (iterInternals.hasNext()) { Internal internal = (Internal)
	 * iterInternals.next(); String internalQN =
	 * internal.getType().getQualifiedName(); castConcern = true; if
	 * (!qns.contains(internalQN)) { qns.add(internalQN);
	 * hd.addCastInterception(internalQN); } } } } if (castConcern &&
	 * !qns.contains(c.getQualifiedName())) { qns.add(c.getQualifiedName());
	 * hd.addCastInterception(c.getQualifiedName()); } }
	 */
	}

	public void getMethodInterceptions(CommonResources resources)
	{
		FILTHService filthservice = FILTHService.getInstance(resources);

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();
			List list = filthservice.getOrder(c);
			if (!list.isEmpty())
			{
				Debug.out(Debug.MODE_DEBUG, "WEAVER", " method calls to " + c.getQualifiedName()
						+ " added to hook dictionary...");
				HookDictionary.instance().addMethodInterception(c.getQualifiedName());
			}
		}
	}

	public void main(String[] args)
	{

	}
}
