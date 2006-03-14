//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\AssemblyTransformer.java

package Composestar.DotNET.TYM.SignatureTransformer;

import Composestar.Core.TYM.SignatureTransformer.*;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;


import java.util.Iterator;

public class DotNETSignatureTransformer implements SignatureTransformer {
    
    /**
     * @roseuid 40F632500138
     */
    public DotNETSignatureTransformer() {
     
    }
    
    /**
     * @param resources
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 40F632500157
     */
    public void run(CommonResources resources) throws ModuleException {
     	
		boolean signaturesmodified = ((Boolean)(resources.getResource("signaturesmodified"))).booleanValue();
		if( signaturesmodified )
		{
			// only run ASTRA when SIGN found ADDED or REMOVED methods
			DataStore ds = DataStore.instance();
			ILCodeParser codeParser = new ILCodeParser();
			Iterator iterator = ds.getAllInstancesOf(Concern.class);
			while(iterator.hasNext())
			{
				Concern concern = (Concern) iterator.next();
				codeParser.addConcern(concern);    
			}
     	
			try
			{
				
				String assemblies = Configuration.instance().getModuleSettings().getModule("ILICIT").getProperty("assemblies");
				String[] assemblyArray = assemblies.split(",");
				for( int i = 0; i < assemblyArray.length; i++ )
				{
					codeParser.setAssemblyName(assemblyArray[i]);
					codeParser.run();
				}
			}
			catch(ModifierException me)
			{
				me.printStackTrace();
			}
		}
		else 
		{
			Debug.out (Debug.MODE_INFORMATION, "ASTRA", "No need to transform assemblies");
		}
    }
}
