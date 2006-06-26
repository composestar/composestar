package Composestar.DotNET.SEMTEX;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LAMA.DotNETType;

import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;

import java.util.*;

/**
 * Calls the SemanticAnalyser with the just created assemblies and the SemanticComposestarPlugin.
 * The result will be stored in the semtex.xml file.
 * @author Michiel van Oudheusden
 *
 */
public class DotNETSemTexRunner implements CTCommonModule {

	private INCRE incre;
	
    public DotNETSemTexRunner() {
		this.incre = INCRE.instance();
	} 
   	
	/* 
	 * Calls the SemanticExtractor to generate the semtex.xml file
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException {
		
		String assemblyList = Configuration.instance().getModuleSettings().getModule("ILICIT").getProperty("assemblies"); 
		ArrayList dependencyList = Configuration.instance().getProjects().getDependencies();  
		String tempFolder = Configuration.instance().getPathSettings().getPath("Base");
		
		Configuration config = Configuration.instance();
		
		// ouputpath:
		String outputpath = config.getProjects().getProperty("OuputPath");
		
		String arg = "";
		
		Iterator it = ((ArrayList)DataStore.instance().getObjectByID("BuiltLibs")).iterator();
		while(it.hasNext())
		{
			arg += "\"" + (String)it.next() + "\"" + " ";
		}
		
		arg += " /plugin:\"" + Configuration.instance().getPathSettings().getPath("Composestar") + "binaries/SemanticComposestarPlugins.dll\"";
		arg += " /exportFilename:\"" + Configuration.instance().getPathSettings().getPath("Base") + "obj/semtex.xml\"";
		
		Debug.out(Debug.MODE_DEBUG,"SEMTEX","Arguments for DotNet SemTex: "+arg);
		String semTexAnalyser =  Configuration.instance().getPathSettings().getPath("Composestar") + "binaries/SemanticExtractorConsole.exe" ;
		java.io.File semTexFile = new java.io.File(semTexAnalyser);
		if( !semTexFile.exists() )
		{
			Debug.out(Debug.MODE_WARNING , "SEMTEX", "SemTex Analyzer not found on it's expected location: " + semTexAnalyser + ". Semantic Analyzing will be skipped.");
			Debug.out(Debug.MODE_INFORMATION , "SEMTEX", "Semantic Analyzer cannot be executed because of missing files. See http://janus.cs.utwente.nl:8000/twiki/bin/view/Composer/SemanticAnalyser for more information.");
		}
		else
		{
			CommandLineExecutor exec = new CommandLineExecutor();
			Configuration.instance().getPathSettings().getPath("Composestar");
			String cmd =  "\"" + semTexAnalyser + "\" "  + arg;
			
			Debug.out(Debug.MODE_DEBUG,"SEMTEX","Calling SemTex Analyzer...");
			
			int result = exec.exec( "call " + cmd );
			
			if( result != 0 )
			{
				throw new ModuleException("SemTex Analyzer failed with error: " + exec.outputError(),"SEMTEX");
			}
		}
		Debug.out(Debug.MODE_DEBUG,"SEMTEX","SemTex Analyzer completed.");
	}
		
}
