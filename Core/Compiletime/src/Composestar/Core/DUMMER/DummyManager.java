package Composestar.Core.DUMMER;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Utils.FileUtils;

public class DummyManager implements CTCommonModule
{
	public static final String MODULE_NAME = "DUMMER";
	
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
		List sources = project.getSources();
		List outputFilenames = new ArrayList(sources.size());
		
		File baseDir = new File(project.getBasePath());
		File dummyDir = new File(baseDir, "obj/" + dummyPath);

		// Make sure the directory exists
		dummyDir.mkdirs();

		Iterator sourceIt = sources.iterator();
		while (sourceIt.hasNext())
		{
			Source source = (Source)sourceIt.next();			
			try {
				File sourceFile = new File(source.getFileName());				
				String target = FileUtils.createOutputFilename(baseDir.getAbsolutePath(), "/obj/" + dummyPath, sourceFile.getAbsolutePath());
				String targetPath = FileUtils.getDirectoryPart(target);
				FileUtils.createFullPath(targetPath); // Make sure the directory exists
				
				File dummyFile = new File(target);
				String absolutePath = dummyFile.getAbsolutePath();
				outputFilenames.add(absolutePath);
				source.setDummy(absolutePath);
			}
			catch (Exception e) {
				throw new ModuleException("Error while creating targetfile of dummy: "+e.getMessage(),MODULE_NAME);
			}
		}
		
		// Create all dummies in one go.
		DummyEmitter emitter = project.getLanguage().getEmitter();
		emitter.createDummies(project, sources, outputFilenames);

		// compile dummies
		try {
			LangCompiler comp = project.getLanguage().compilerSettings.getCompiler();
			comp.compileDummies(project);
		}
		catch (CompilerException e) {
			e.printStackTrace();
			throw new ModuleException("Cannot compile dummies: "+e.getMessage(),MODULE_NAME);
		}
	}
}
