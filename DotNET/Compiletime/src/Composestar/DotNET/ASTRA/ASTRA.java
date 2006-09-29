package Composestar.DotNET.ASTRA;

import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * ASTRA applies the signature changes determined by SIGN to the dummies assembly.
 */
public class ASTRA implements CTCommonModule
{
	public ASTRA()
	{
	}

	public void run(CommonResources resources) throws ModuleException
	{
		// only run ASTRA when SIGN found ADDED or REMOVED methods
		if (resources.getBoolean("signaturesmodified"))
		{
			ILCodeParser codeParser = new ILCodeParser();

			DataStore ds = DataStore.instance();
			Iterator iterator = ds.getAllInstancesOf(Concern.class);
			while (iterator.hasNext())
			{
				Concern concern = (Concern)iterator.next();
				codeParser.addConcern(concern);
			}

			try
			{
				Configuration config = Configuration.instance();
				List dummies = config.getProjects().getCompiledDummies();
				Iterator dumIt = dummies.iterator();
				while (dumIt.hasNext()) 
				{
					String name = (String)dumIt.next();
					codeParser.setAssemblyName(name);
					codeParser.run();
				}

			/*	TODO: can this be removed?
				String assemblies = config.getModuleSettings().getModule("ILICIT").getProperty("assemblies");
				String[] assemblyArray = assemblies.split(",");
				for( int i = 0; i < assemblyArray.length; i++ )
				{
					codeParser.setAssemblyName(assemblyArray[i]);
					codeParser.run();
				} */
			}
			catch (ModifierException e)
			{
				throw new ModuleException(e.getMessage(), "ASTRA");
			}
		}
		else 
			Debug.out(Debug.MODE_INFORMATION,"ASTRA","No need to transform assemblies");
	}
}
