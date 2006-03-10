//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeHarvester\\HarvestRunner.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeHarvester\\HarvestRunner.java

package Composestar.DotNET.TYM.TypeHarvester;

import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.Core.INCRE.Dependency;
import Composestar.Core.INCRE.INCRE;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Core.Exception.ModuleException;

import java.util.*;

/**
 * This Module is responsible for running the .NET assembly harvester.
 */
public class DotNETHarvestRunner implements HarvestRunner {
    
    /**
     * @roseuid 4056E99103CC
     */
	private INCRE incre;
	public static Vector unmodifiedDLLs = new Vector();

    public DotNETHarvestRunner() {
		this.incre = INCRE.instance();	
    } 

    public String checkDLL(String dllName) throws ModuleException
	{
    	if(!dllName.equals("")){
    		if(incre.isProcessedByModule(dllName.replace('\"',' ').trim(),"HARVESTER"))
    		{
    			if(dllName.indexOf("dummies")==-1)
    			{
					unmodifiedDLLs.add(dllName);
					dllName = "!" + dllName;
    			}
    		}
    	}
		return dllName;
	}
    
    /**
     * The run function of each module is called in the same order as the  modules 
     * where added to the Master.
     * @param resources The resources objects contains the common resources availabe 
     * e.g the Repository.
     * @throws Composestar.core.Exception.ModuleException If a ModuleException is 
     * thrown the Master will stop its activity immediately.
     * @roseuid 4056E8BC03B6
     */
    public void run(CommonResources resources) throws ModuleException {
    	String assemblyList = Configuration.instance().moduleSettings.getModule("ILICIT").getProperty("assemblies"); 
		ArrayList dependencyList = Configuration.instance().projects.getDependencies();  
		String tempFolder = Configuration.instance().pathSettings.getPath("Temp");
		
		
		if( assemblyList == null )
			throw new ModuleException("TYM TypeHarvester needs \"ProjectConfiguration.Assemblies\" which is missing.");
		if( dependencyList == null )
			throw new ModuleException("TYM TypeHarvester needs \"ProjectConfiguration.Dependencies\" which is missing.");
		
		String arg = "";

		Iterator it = dependencyList.iterator();
		while(it.hasNext()) 
		{
			Dependency dependency = (Dependency)it.next();
			String name = dependency.getName();
			if( name.indexOf("Microsoft.NET/Framework/") == -1 )
			{
				name = checkDLL(name);
                arg += name + " ";
			}
		}

		String[] assemblyPaths = assemblyList.split(",");
		for( int i = 0; i < assemblyPaths.length; i++ ) {  
			String asm = assemblyPaths[i];
			asm = checkDLL(asm);
			arg += "\"" + asm + "\"" + " ";
			// Add additional dll's (from compiled inner's) and list of classes that we still need.
      	}
      	
      	
		resources.addResource("unmodifiedDLLs",unmodifiedDLLs);
		Debug.out(Debug.MODE_DEBUG,"TYM","Arguments for TYM Harvester: "+arg);
		String typeHarvester =  Configuration.instance().pathSettings.getPath("Composestar") + "binaries\\TypeHarvester.exe" ;
		java.io.File typeHarvesterFile = new java.io.File(typeHarvester);
		if( !typeHarvesterFile.exists() )
		{
			throw new ModuleException("TypeHarvester not found on it's expected location: " + typeHarvester);
		}

      	CommandLineExecutor exec = new CommandLineExecutor();
      	Configuration.instance().pathSettings.getPath("Composestar");
      	String cmd =  "\"" + typeHarvester + "\"" + " \"" + tempFolder + "\" " + arg;
      	int result = exec.exec( "call " + cmd );
      	if( result != 0 )
      	{
      		throw new ModuleException("TypeHarvester failed with error: " + exec.outputError(),"TYM");
      	}
    }
}
