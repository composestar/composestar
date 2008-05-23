package Composestar.DotNET.TYM.TypeHarvester;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.DotNET.BACO.DotNETBACO;
import Composestar.DotNET.COMP.DotNETCompiler;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * This Module is responsible for running the .NET assembly harvester.
 */
@ComposestarModule(ID = ModuleNames.HARVESTER)
public class DotNETHarvestRunner implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.HARVESTER);

	public DotNETHarvestRunner()
	{}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		File dummies = (File) resources.get(DotNETCompiler.DUMMY_ASSEMBLY);

		if (dummies == null)
		{
			throw new ModuleException("TYM TypeHarvester needs compiled dummies.", ModuleNames.HARVESTER);
		}

		List<String> cmdItems = new ArrayList<String>();
		cmdItems.add(getExecutable(resources));
		cmdItems.add(resources.configuration().getProject().getIntermediate().toString());

		for (File dep : resources.configuration().getProject().getFilesDependencies())
		{
			String name = dep.toString();
			if (!DotNETBACO.isSystemAssembly(name))
			{
				name = checkDLL(name); // FIXME: checkDLL has unclear
				// side-effect
				cmdItems.add(name);
			}
		}

		String name = dummies.toString();
		name = checkDLL(name); // FIXME: checkDLL has unclear side-effect
		cmdItems.add(name);

		// FIXME: clarify this TODO
		// TODO: (?) Add additional dll's (from compiled inner's) and list of
		// classes that we still need (?)

		logger.debug("Command for TYM Harvester: " + StringUtils.join(cmdItems));

		CommandLineExecutor cle = new CommandLineExecutor();
		int result;
		try
		{
			result = cle.exec(cmdItems);
		}
		catch (IOException e)
		{
			throw new ModuleException(e.getMessage(), ModuleNames.HARVESTER, e);
		}
		catch (InterruptedException e)
		{
			throw new ModuleException(e.getMessage(), ModuleNames.HARVESTER, e);
		}

		Debug.parseLog(cle.outputNormal());

		if (result != 0)
		{
			throw new ModuleException("TypeHarvester failed", ModuleNames.HARVESTER);
		}
		return ModuleReturnValue.Ok;
	}

	private String getExecutable(CommonResources resources) throws ModuleException
	{
		File exe = resources.getPathResolver().getResource("lib/TypeHarvester.exe");
		if (exe == null)
		{
			throw new ModuleException("TypeHarvester not found on it's expected location: "
					+ resources.getPathResolver().getResource("lib"), ModuleNames.HARVESTER);
		}

		return exe.getAbsolutePath();
	}

	/**
	 * FIXME: describe what this method really does, and rename it
	 * appropriately. side-effect is currently unclear.
	 */
	private String checkDLL(String dllName) throws ModuleException
	{
		return dllName;
		// if ("".equals(dllName))
		// {
		// throw new ModuleException("Invalid dll name: " + dllName,
		// MODULE_NAME);
		// }
		//
		// if (incre.isProcessedByModule(dllName, "HARVESTER"))
		// {
		// skippedAssemblies.add(dllName);
		// return "!" + dllName;
		// }
		// else
		// {
		// return dllName;
		// }
	}

	// /**
	// * Returns all assemblies indirectly used by the harvester to harvest all
	// * types from the specified assembly. e.g: When the harvester loads
	// assembly
	// * ComposestarRuntimeInterpreter.dll, it indirectly harvest types from
	// other
	// * assemblies e.g ComposestarFilterDebugger.dll Used by INCRE
	// */
	// public List externalAssemblies(String asm)
	// {
	// List externals = new ArrayList();
	//
	// Iterator typeIt = incre.getHistoryTypes().iterator();
	// while (typeIt.hasNext())
	// {
	// DotNETType type = (DotNETType) typeIt.next();
	// if (type.fromDLL.equalsIgnoreCase(asm))
	// {
	// String extAsm = type.Module.fullyQualifiedName();
	// if (!externals.contains(extAsm))
	// {
	// externals.add(extAsm);
	// }
	// }
	// }
	//
	// return externals;
	// }

	// /**
	// * Returns a list containing all assemblies harvested earlier e.g input
	// for
	// * harvester is "A.dll B.dll C.dll" then the list returned is for input
	// * A.dll => [] for input B.dll => [A.dll] for input C.dll => [A.dll,B.dll]
	// * Used by INCRE
	// */
	// public List prevAssemblies(String asm)
	// {
	// List assemblies = new ArrayList();
	//
	// List dependencies = config.getProjects().getDependencies();
	// Iterator depIt = dependencies.iterator();
	// while (depIt.hasNext())
	// {
	// Dependency dependency = (Dependency) depIt.next();
	// String name = dependency.getFileName();
	// if (name.equalsIgnoreCase(asm))
	// {
	// return assemblies;
	// }
	//
	// if (!DotNETBACO.isSystemAssembly(name))
	// {
	// assemblies.add(name);
	// }
	// }
	//
	// List dummies = config.getProjects().getCompiledDummies();
	// Iterator dumIt = dummies.iterator();
	// while (dumIt.hasNext())
	// {
	// String name = (String) dumIt.next();
	// assemblies.add(name);
	// }
	//
	// return assemblies;
	// }

	// /**
	// * Returns a string of all assemblies harvested earlier e.g input for
	// * harvester is "A.dll B.dll C.dll" then return string for input A.dll =>
	// ""
	// * input B.dll => "A.dll" input C.dll => "A.dll B.dll" Used by INCRE
	// */
	// public String prevInput(String asm)
	// {
	// String input = incre.getConfiguration("HarvesterInput");
	//
	// int index = input.indexOf(asm);
	// if (index > 0)
	// {
	// return input.substring(0, index);
	// }
	//
	// return "";
	// }
}
