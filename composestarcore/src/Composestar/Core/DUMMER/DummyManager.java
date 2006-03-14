package Composestar.Core.DUMMER;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.*;
import Composestar.Core.Master.*;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.Config.*;
import Composestar.Utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

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
			ArrayList sources = p.getSources();
			Iterator sourceIt = sources.iterator();
			while( sourceIt.hasNext() ) {
				
				DummyEmitter emitter = p.getLanguage().getEmitter();
				
				//create target of source 
				Source source = (Source)sourceIt.next();
				String dummyPath = config.getPathSettings().getPath("Dummy");
				String target = "";
				try {
					target = FileUtils.createOutputFilename(p.getProperty("basePath"),"obj/"+dummyPath,source.getFileName());
				}
				catch(Exception e) {
					throw new ModuleException("Error while creating targetfile of dummy: "+e.getMessage(),"DUMMER");
				}
								
				String targetPath = target.substring(0,target.lastIndexOf("/")+1);
				File fileDir = new File(targetPath);
				BufferedWriter bw;		
				//create dir(s)
				try {
					if(!fileDir.exists())	{
						fileDir.mkdirs();
					}
					bw = new BufferedWriter(new FileWriter(target));
				}
				catch(IOException io) {
					throw new ModuleException("ERROR while creating dummy directory: "+io.getMessage(),"DUMMER");
				}
				//create dummy
				emitter.createDummy(source,bw);

				//add dummy to source
				source.setDummy(target);
			}
			
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
