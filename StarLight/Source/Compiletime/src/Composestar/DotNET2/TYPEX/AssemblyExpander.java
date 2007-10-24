package Composestar.DotNET2.TYPEX;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.DotNET2.MASTER.StarLightMaster;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;

import composestar.dotNET2.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET2.tym.entities.AssemblyConfig;
import composestar.dotNET2.tym.entities.ExpandedAssembly;
import composestar.dotNET2.tym.entities.ExpandedAssemblyDocument;

class AssemblyExpander
{
	private File baseDir;

	private Map<String, AssemblyConfig> assemblyConfigs;

	public AssemblyExpander(CommonResources resources)
	{
		baseDir = new File(resources.configuration().getProject().getIntermediate(), "Starlight");

		assemblyConfigs = new HashMap<String, AssemblyConfig>();
		ArrayOfAssemblyConfig acs = StarLightMaster.getConfigContainer().getAssemblies();
		for (AssemblyConfig ac : acs.getAssemblyConfigList())
		{
			assemblyConfigs.put(ac.getName(), ac);
		}
	}

	public void process(Collection<ExpandedAssembly> assemblies) throws ModuleException
	{
		if (assemblies.size() == 0)
		{
			return;
		}

		writeExpansionSpecs(assemblies);
		invokeExpander();
	}

	private void writeExpansionSpecs(Collection<ExpandedAssembly> assemblies) throws ModuleException
	{
		for (ExpandedAssembly ea : assemblies)
		{
			AssemblyConfig ac = assemblyConfigs.get(ea.getName());

			ExpandedAssemblyDocument doc = ExpandedAssemblyDocument.Factory.newInstance();
			doc.setExpandedAssembly(ea);

			File file = new File(baseDir, ac.getId() + "_expandspec.xml");
			ac.setExpansionSpecificationFile(file.getAbsolutePath());

			OutputStream os = null;
			try
			{
				os = new FileOutputStream(file);
				doc.save(os);
			}
			catch (IOException e)
			{
				throw new ModuleException("IOException while writing expansion spec '" + file + "': " + e.getMessage(),
						TYPEX.MODULE_NAME);
			}
			finally
			{
				FileUtils.close(os);
			}
		}

		try
		{
			StarLightMaster.storeConfigContainer();
		}
		catch (IOException e)
		{
			throw new ModuleException("IOException while writing configuration: " + e.getMessage(), TYPEX.MODULE_NAME);
		}
	}

	private void invokeExpander() throws ModuleException
	{
		List<String> cmd = new ArrayList<String>();
		cmd.add(getExecutable());
		cmd.add(StarLightMaster.getConfigFileName());

		Debug.out(Debug.MODE_DEBUG, TYPEX.MODULE_NAME, "Command line: " + StringUtils.join(cmd));

		CommandLineExecutor cle = new CommandLineExecutor();
		int result = cle.exec(cmd);

		if (result != 0)
		{
			Debug.out(Debug.MODE_ERROR, TYPEX.MODULE_NAME, "Output from SigExpander: " + cle.outputNormal());
			throw new ModuleException("SigExpander failed with error " + result, TYPEX.MODULE_NAME);
		}
	}

	private String getExecutable() throws ModuleException
	{
		// File baseDir = new File(pathSettings.getPath("Composestar"),
		// "CoreElements");
		File baseDir = new File("C:\\CPS\\StarLight\\Source\\SigExpander\\bin\\Debug");
		File exe = new File(baseDir, "Composestar.StarLight.SigExpander.exe");

		if (!exe.exists())
		{
			throw new RuntimeException("Executable does not exist: " + exe);
		}

		return exe.getAbsolutePath();
	}
}