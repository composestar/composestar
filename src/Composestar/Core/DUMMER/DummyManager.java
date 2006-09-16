package Composestar.Core.DUMMER;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;

public class DummyManager implements CTCommonModule
{
	public DummyManager() 
	{		
	}
	
	public void run(CommonResources resources) throws ModuleException
	{
		createDummies();
	}
	
	private void createDummies() throws ModuleException
	{
		Configuration config = Configuration.instance();
		String dummyPath = config.getPathSettings().getPath("Dummy");
		
		List projects = config.getProjects().getProjects();
		Iterator projIt = projects.iterator();		
		while (projIt.hasNext())
		{
			Project project = (Project)projIt.next();
			createProjectDummies(dummyPath, project);
		}
	}

	private void createProjectDummies(String dummyPath, Project project) throws ModuleException
	{
		DummyEmitter emitter = project.getLanguage().getEmitter();
		List sources = project.getSources();
		List outputFilenames = new ArrayList(sources.size());
		
		String basePath = project.getProperty("basePath");
		File base = new File(basePath);
		File dummyDir = new File(base, "obj/" + dummyPath);

		// Make sure the directory exists
		dummyDir.mkdirs();

		Iterator sourceIt = sources.iterator();
		while (sourceIt.hasNext())
		{				
			Source source = (Source)sourceIt.next();			
			try {
				File sourceFile = new File(source.getFileName());				
				File dummyFile = new File(dummyDir, sourceFile.getName());
				
				String absolutePath = dummyFile.getAbsolutePath();
				outputFilenames.add(absolutePath);
				source.setDummy(absolutePath);
			}
			catch (Exception e) {
				throw new ModuleException("Error while creating targetfile of dummy: "+e.getMessage(),"DUMMER");
			}
		}
		
		// Create all dummies in one go.
		emitter.createDummies(project, sources, outputFilenames);
		
		// compile dummies
		LangCompiler comp = project.getLanguage().compilerSettings.getCompiler();
		try {
			comp.compileDummies(project);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ModuleException("Cannot compile dummies: "+e.getMessage(),"DUMMER");
		}
	}
}
