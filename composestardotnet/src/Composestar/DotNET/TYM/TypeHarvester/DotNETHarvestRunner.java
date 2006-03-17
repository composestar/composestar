//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeHarvester\\HarvestRunner.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeHarvester\\HarvestRunner.java

package Composestar.DotNET.TYM.TypeHarvester;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.DotNET.LAMA.DotNETType;

import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;

import java.util.*;

/**
 * This Module is responsible for running the .NET assembly harvester.
 */
public class DotNETHarvestRunner implements HarvestRunner {
    
    /**
     * @roseuid 4056E99103CC
     */
	private INCRE incre;
	public ArrayList skippedAssemblies;
	
    public DotNETHarvestRunner() {
		this.incre = INCRE.instance();
		skippedAssemblies = new ArrayList();
	} 

    public String checkDLL(String dllName) throws ModuleException
	{
    	if(!dllName.equals("")){
    		if(incre.isProcessedByModule(dllName.replace('\"',' ').trim(),"HARVESTER"))
    		{
    			skippedAssemblies.add(dllName);
    			return "!"+dllName;
    		}
    	}
		return dllName;
	}
   
    /**
     * Returns all assemblies indirectly used by the harvester 
     * 	to harvest all types from the assembly (param asm)
     * e.g: When the harvester loads assembly composestarruntimeinterpreter, 
     * it indirectly harvest types from other assemblies e.g composestarfilterdebugger.dll
     * 
     * @param lib
     * @return
     */
    public ArrayList externalAssemblies(String asm)
    {
    	ArrayList externals = new ArrayList();
    	INCRE incre = INCRE.instance();
    	Iterator types = incre.getHistoryTypes().iterator();
    	while(types.hasNext()){
    		DotNETType type = (DotNETType)types.next();
    		if(type.fromDLL.equalsIgnoreCase(asm)){
    			String extAsm = type.Module.fullyQualifiedName();
    			if(!externals.contains(extAsm))
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
     * @param asm
     * @return ArrayList
     */
    public ArrayList prevAssemblies(String asm)
    {
    	ArrayList assemblies = new ArrayList();
    	ArrayList dependencyList = Configuration.instance().getProjects().getDependencies();  
    	Iterator it = dependencyList.iterator();
    	
    	while(it.hasNext()) 
		{
			Dependency dependency = (Dependency)it.next();
			String name = dependency.getFileName();
			if(name.equalsIgnoreCase(asm))
				return assemblies;
						
			if( name.indexOf("Microsoft.NET/Framework/") == -1 )
				assemblies.add(name);
		}
    	
		// TODO: use getCompiledDummies of DUMMER instead of ugly ilicit setting
		String dummies = Configuration.instance().getModuleSettings().getModule("ILICIT").getProperty("assemblies");
		assemblies.add(dummies); 
		return assemblies;
    }
    
    /**
     * Returns a string of all assemblies harvested earlier
     * e.g input for harvester is "A.dll B.dll C.dll"
     * then return string for 
     * 		input A.dll => ""
     * 		input B.dll => "A.dll"
     * 		input C.dll => "A.dll B.dll" 
     * @param asm
     * @return String
     */
    public String prevInput(String asm)
    {
    	INCRE incre = INCRE.instance();
    	String harvesterinput = incre.getConfiguration("HarvesterInput");
    	int index = harvesterinput.indexOf(asm);
    	if(index>0){
    		String previnput = harvesterinput.substring(0,index);
    		return previnput;
    	}
    	
    	return "";
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
    	String assemblyList = Configuration.instance().getModuleSettings().getModule("ILICIT").getProperty("assemblies"); 
		ArrayList dependencyList = Configuration.instance().getProjects().getDependencies();  
		String tempFolder = Configuration.instance().getPathSettings().getPath("Base");
		
		
		if( assemblyList == null )
			throw new ModuleException("TYM TypeHarvester needs \"ProjectConfiguration.Assemblies\" which is missing.");
		if( dependencyList == null )
			throw new ModuleException("TYM TypeHarvester needs \"ProjectConfiguration.Dependencies\" which is missing.");
		
		String arg = "";

		Iterator it = dependencyList.iterator();
		while(it.hasNext()) 
		{
			Dependency dependency = (Dependency)it.next();
			String name = dependency.getFileName();
			if( name.indexOf("Microsoft.NET/Framework/") == -1 )
			{
				name = checkDLL(name);
                arg += "\"" +name + "\"" + " ";
			}
		}

		String[] assemblyPaths = assemblyList.split(",");
		for( int i = 0; i < assemblyPaths.length; i++ ) {  
			String asm = assemblyPaths[i];
			asm = checkDLL(asm);
			arg += "\"" + asm + "\"" + " ";
			// Add additional dll's (from compiled inner's) and list of classes that we still need.
      	}
      	
		resources.addResource("skippedAssemblies", skippedAssemblies);
		Debug.out(Debug.MODE_DEBUG,"TYM","Arguments for TYM Harvester: "+arg);
		String typeHarvester =  Configuration.instance().getPathSettings().getPath("Composestar") + "binaries/TypeHarvester.exe" ;
		java.io.File typeHarvesterFile = new java.io.File(typeHarvester);
		if( !typeHarvesterFile.exists() )
		{
			throw new ModuleException("TypeHarvester not found on it's expected location: " + typeHarvester);
		}

      	CommandLineExecutor exec = new CommandLineExecutor();
      	Configuration.instance().getPathSettings().getPath("Composestar");
      	String cmd =  "\"" + typeHarvester + "\"" + " \"" + tempFolder + "\" " + arg;
      	int result = exec.exec( "call " + cmd );
      	if( result != 0 )
      	{
      		throw new ModuleException("TypeHarvester failed with error: " + exec.outputError(),"TYM");
      	}
    }
    
    
}
