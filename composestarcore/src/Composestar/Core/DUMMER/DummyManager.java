package Composestar.Core.DUMMER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Utils.FileUtils;

public class DummyManager implements CTCommonModule {

	public DummyManager() {
		
	}
	
	public void run(CommonResources resources) throws ModuleException {
		
		/** create dummies */
		
		Configuration config = Configuration.instance();
		ArrayList projects = config.getProjects().getProjects();
		Iterator projIt = projects.iterator();
		
		while( projIt.hasNext() ) {
			Project p = (Project)projIt.next();
			DummyEmitter emitter = p.getLanguage().getEmitter();
			ArrayList sources = p.getSources();
			Vector outputFilenames = new Vector(sources.size());
			
			Iterator sourceIt = sources.iterator();
			while( sourceIt.hasNext() ) {				
				
				//create target of source 
				Source source = (Source)sourceIt.next();
				String dummyPath = config.getPathSettings().getPath("Dummy");
				try {
					String target = FileUtils.createOutputFilename(p.getProperty("basePath"),"obj/"+dummyPath,source.getFileName());
					String targetPath = FileUtils.getDirectoryPart(target);
					FileUtils.createFullPath(targetPath); // Make sure the directory exists

					outputFilenames.add(target);
					source.setDummy(target);
				}
				catch(Exception e) {
					throw new ModuleException("Error while creating targetfile of dummy: "+e.getMessage(),"DUMMER");
				}
				//emitter.createDummy(source,target);
			}
			// Create all the dummies in one go.
			emitter.createDummies(sources, outputFilenames);
			
			/** compile dummies */
			LangCompiler comp = (LangCompiler)p.getLanguage().compilerSettings.getCompiler();
			try {
				comp.compileDummies(p);
			}
			catch(Exception e) {
				throw new ModuleException("CANNOT compile dummies: "+e.getMessage(),"DUMMER");
			}
		}
	}	
		
	/**
	 * For testing purposes
	 * @param args
	 */
	public static void main(String[] args) throws ModuleException{
		
	}
}
