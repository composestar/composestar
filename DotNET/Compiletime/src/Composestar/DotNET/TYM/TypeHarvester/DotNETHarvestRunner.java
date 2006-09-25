package Composestar.DotNET.TYM.TypeHarvester;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;

/**
 * This Module is responsible for running the .NET assembly harvester.
 */
public class DotNETHarvestRunner implements HarvestRunner
{
	private INCRE incre;
	private Configuration config;
	private List skippedAssemblies;

	public DotNETHarvestRunner()
	{
		incre = INCRE.instance();
		config = Configuration.instance();
		skippedAssemblies = new ArrayList();
	} 

	/**
	 * FIXME: describe what this method really does, and rename it appropriately. 
	 * side-effect is currently unclear.
	 */
	private String checkDLL(String dllName) throws ModuleException
	{
		if (!dllName.equals(""))
		{
			String fixedDllName = FileUtils.unquote(dllName);
			if (incre.isProcessedByModule(fixedDllName,"HARVESTER"))
			{
				skippedAssemblies.add(dllName);
				return "!" + dllName;
			}
		}
		return dllName;
	}

	/**
	 * Returns all assemblies indirectly used by the harvester to harvest all types from the specified assembly.
	 * e.g: When the harvester loads assembly ComposestarRuntimeInterpreter.dll, 
	 * it indirectly harvest types from other assemblies e.g ComposestarFilterDebugger.dll
	 * 
	 * Used by INCRE
	 */
	public List externalAssemblies(String asm)
	{
		List externals = new ArrayList();
		
		Iterator typeIt = incre.getHistoryTypes().iterator();
		while (typeIt.hasNext())
		{
			DotNETType type = (DotNETType)typeIt.next();
			if (type.fromDLL.equalsIgnoreCase(asm))
			{
				String extAsm = type.Module.fullyQualifiedName();
				if (!externals.contains(extAsm))
					externals.add(extAsm);
			}
		}

		return externals;
	}

	/**
	 * Returns a list containing all assemblies harvested earlier
	 * e.g input for harvester is "A.dll B.dll C.dll"
	 * then the list returned is 
	 * 		for input A.dll => []
	 * 		for input B.dll => [A.dll]
	 * 		for input C.dll => [A.dll,B.dll] 
	 * 
	 * Used by INCRE
	 */
	public List prevAssemblies(String asm)
	{
		List assemblies = new ArrayList();
		
		List dependencies = config.getProjects().getDependencies();  
		Iterator depIt = dependencies.iterator();
		while (depIt.hasNext()) 
		{
			Dependency dependency = (Dependency)depIt.next();
			String name = dependency.getFileName();
			if (name.equalsIgnoreCase(asm))
				return assemblies;

			// FIXME: assumption about the environment
			if (name.indexOf("Microsoft.NET/Framework/") == -1)
				assemblies.add(name);
		}

		List dummies = config.getProjects().getCompiledDummies();
		Iterator dumIt = dummies.iterator();
		while (dumIt.hasNext()) 
		{
			String name = (String)dumIt.next();
			assemblies.add(name);
		}

		return assemblies;
	}

	/**
	 * Returns a string of all assemblies harvested earlier
	 * e.g input for harvester is "A.dll B.dll C.dll"
	 * then return string for 
	 * 		input A.dll => ""
	 * 		input B.dll => "A.dll"
	 * 		input C.dll => "A.dll B.dll" 
	 * 
	 * Used by INCRE
	 */
	public String prevInput(String asm)
	{
		String input = incre.getConfiguration("HarvesterInput");

		int index = input.indexOf(asm);
		if (index > 0)
			return input.substring(0,index);

		return "";
	}

	/**
	 * The run function of each module is called in the same order as the  modules 
	 * where added to the Master.
	 * @param resources The resources objects contains the common resources availabe e.g the Repository.
	 * @throws ModuleException If a ModuleException is thrown the Master will stop its activity immediately.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		resources.addResource("skippedAssemblies", skippedAssemblies); // yes, we can add this immediately

		List dummies = config.getProjects().getCompiledDummies();
		List dependencies = config.getProjects().getDependencies();  

		if (dummies == null)
			throw new ModuleException("TYM TypeHarvester needs compiled dummies which is missing.");

		if (dependencies == null)
			throw new ModuleException("TYM TypeHarvester needs 'ProjectConfiguration.Dependencies' which is missing.");

		List cmdItems = new ArrayList();
		cmdItems.add(getExecutable());
		cmdItems.add(config.getPathSettings().getPath("Base"));

		Iterator depIt = dependencies.iterator();
		while (depIt.hasNext())
		{
			Dependency dependency = (Dependency)depIt.next();
			String name = dependency.getFileName();			

			// FIXME: this makes an assumption on the location of the framework 
			if (name.indexOf("Microsoft.NET/Framework/") == -1)
			{
				name = checkDLL(name); // FIXME: checkDLL has unclear side-effect
				cmdItems.add(name);
			}
		}

		Iterator dumIt = dummies.iterator();
		while (dumIt.hasNext()) 
		{
			String name = (String)dumIt.next();
			name = checkDLL(name); // FIXME: checkDLL has unclear side-effect
			cmdItems.add(name);
		}

		// FIXME: clarify this TODO 
		// TODO: (?) Add additional dll's (from compiled inner's) and list of classes that we still need (?)

		Debug.out(Debug.MODE_DEBUG,"TYM","Command for TYM Harvester: " + StringUtils.join(cmdItems));		

		CommandLineExecutor cle = new CommandLineExecutor();
		int result = cle.exec(cmdItems);
		
		Debug.parseLog(cle.outputNormal());
		
		if (result != 0)
			throw new ModuleException("TypeHarvester failed");
	}

	private String getExecutable() throws ModuleException
	{
		Configuration config = Configuration.instance();

		File cs = new File(config.getPathSettings().getPath("Composestar"));
		File exe = new File(cs, "binaries/TypeHarvester.exe");
		if (!exe.exists())
			throw new ModuleException("TypeHarvester not found on it's expected location: " + exe);

		return exe.getAbsolutePath();
	}
}
