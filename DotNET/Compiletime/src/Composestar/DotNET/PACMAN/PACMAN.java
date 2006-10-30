package Composestar.DotNET.PACMAN;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;

/**
 * PACMAN - PArtial Class MANager for C# 2.0
 * 
 * Creates partial classes to extend existing classes with additional methods.
 * The set of additional methods is generated by SIGN.
 *
 * @author Marcus Klimstra
 */
public class PACMAN implements CTCommonModule
{
	private static final String MODULE_NAME = "PACMAN";
	
	private Configuration m_config;
	private DataStore m_dataStore;
	
	public PACMAN()
	{
		m_config = Configuration.instance();
		m_dataStore = DataStore.instance();
	}
	
	/**
	 * Determines which types have been expanded with additional methods,
	 * then delegates the generation procedure for the partial classes to
	 * the PartialClassEmitter.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		try {
			String outputDir = m_config.getPathSettings().getPath("Base");
			File objPath = new File(outputDir, "obj");
			File target = new File(objPath, "PartialClasses.cs");
			
			PartialClassEmitter pce = new PartialClassEmitter(target);
			collectExpandedTypes(pce);
			pce.emit();
		}
		catch (IOException e) {
			throw new ModuleException("Unable to generate partial classes: " + e.getMessage(), MODULE_NAME);
		}
	}
	
	private void collectExpandedTypes(PartialClassEmitter pce)
	{
		Iterator concernIt = m_dataStore.getAllInstancesOf(Concern.class);
		while (concernIt.hasNext())
		{
			Concern concern = (Concern)concernIt.next();
			PlatformRepresentation pr = concern.getPlatformRepresentation();
			
			if (pr != null && pr instanceof DotNETType)
			{
				DotNETType dnt = (DotNETType)pr;
				
				Signature sig = concern.getSignature();
				List methods = sig.getMethods(MethodWrapper.ADDED);				
				
				Iterator methIt = methods.iterator();
				while (methIt.hasNext())
				{
					DotNETMethodInfo mi = (DotNETMethodInfo)methIt.next();
					pce.addMethod(dnt, mi);
				}
			}
		}
	}
}
