package Composestar.Core.EMBEX;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Implementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Projects;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * Class responsible for extracting embedded sources and storing them
 */
public class EMBEX implements CTCommonModule
{
	private String tempPath = "";
	private String embeddedDir = "";
	private String embeddedPath = "";

	public EMBEX() 
	{     	
    }
    
	/**
	 * Iterates over cps concerns and calls saveToFile for embedded sources found
	 * Creates directory for embedded sources  
	 */
	public void run(CommonResources resources) throws ModuleException 
	{
		DataStore ds = DataStore.instance();
		Configuration config = Configuration.instance();
		Projects allProjects = config.getProjects();

		Iterator cpsConcernIter = ds.getAllInstancesOf(CpsConcern.class);

		// fetch temppath
		tempPath = config.getPathSettings().getPath("Base");
		if (tempPath == null) 
			throw new ModuleException("Error in configuration file: no such property Base", "EMBEX");
		
		// fetch embedded sources directory
		embeddedDir = Configuration.instance().getPathSettings().getPath("EmbeddedSources");
		if (embeddedDir == null || embeddedDir.length() == 0 )
			throw new ModuleException( "Error in configuration file: No such property EmbeddedSources", "EMBEX");

		// create directory for embedded code
		embeddedPath = tempPath + embeddedDir;
		File embeddedDir = new File(embeddedPath);
		if (embeddedDir.exists())
		{
			// bad news
			String msg = "Cannot create directory '" + embeddedPath + "' for embedded sources. Directory exists! Removing files in directory";
			Debug.out(Debug.MODE_WARNING,"EMBEX",msg);	
			embeddedDir.delete();
		}
		
		// iterate over all cps concerns
		while (cpsConcernIter.hasNext()) 
		{
			// fetch implementation
			CpsConcern cps = (CpsConcern)cpsConcernIter.next();
			Implementation imp = cps.getImplementation();

			if (imp instanceof Source)
			{
				if (!embeddedDir.exists())
					embeddedDir.mkdir();

				// fetch embedded source and save
				Source src = (Source)imp;
				String language = src.getLanguage();
				
				Debug.out(Debug.MODE_DEBUG,"EMBEX","Found embedded source: "+src.getClassName());
				Debug.out(Debug.MODE_DEBUG,"EMBEX","\tLanguage: "+language);
				Debug.out(Debug.MODE_DEBUG,"EMBEX","\tFile: "+src.getSourceFile());
				
				List languageProjects = allProjects.getProjectsByLanguage(language);
				if (languageProjects == null)
					throw new ModuleException("No projects for language " + language, "EMBEX");
				
				Iterator lpIter = languageProjects.iterator();
				if (!lpIter.hasNext())
					throw new ModuleException("There is no project to add the embedded source to, the embedded code: "+src.className+" is added to the first project of type: "+src.language, "EMBEX");
				else
				{
					Project prj = (Project)lpIter.next();
					Debug.out(Debug.MODE_DEBUG,"EMBEX","Adding embedded code to project: "+prj.getProperty("name"));
					
					Composestar.Core.Master.Config.Source source = new Composestar.Core.Master.Config.Source();
					source.setFileName(embeddedPath+src.getSourceFile());
					prj.addSource(source);
					
					TypeSource tsource = new TypeSource();
					tsource.setFileName(embeddedPath+src.getSourceFile());
					tsource.setName(src.getClassName());
					prj.addTypeSource(tsource);
					
					this.saveToFile(src,resources);
				}
			}
		}
	}

	/**
	 * Stores the embedded source in a new file
	 */
	private void saveToFile(Source src,CommonResources resources) throws ModuleException
	{
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(embeddedPath+src.getSourceFile()));
			bw.write(src.getSource());
			bw.close();
		}
		catch (IOException e) {
			throw new ModuleException("ERROR while trying to save embedded source!:" + e.getMessage() , "EMBEX");
		}
	}
}

