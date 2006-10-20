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
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Projects;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * Class responsible for extracting embedded sources and storing them
 */
public class EMBEX implements CTCommonModule
{
	public static final String MODULE_NAME = "EMBEX";

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
		PathSettings ps = config.getPathSettings();
		Projects allProjects = config.getProjects();

		// fetch temppath
		String basePath = ps.getPath("Base");
		if (basePath == null || basePath.length() == 0) 
			throw new ModuleException("Error in configuration file: no path Base", MODULE_NAME);

		// create directory for embedded code
		File embeddedDir = new File(basePath, "obj/embedded/");
		embeddedDir.mkdirs();

		// iterate over all cps concerns
		Iterator concernIt = ds.getAllInstancesOf(CpsConcern.class);
		while (concernIt.hasNext()) 
		{
			// fetch implementation
			CpsConcern cps = (CpsConcern)concernIt.next();
			Implementation imp = cps.getImplementation();

			if (imp instanceof Source)
			{
				// fetch embedded source and save
				Source sourceCode = (Source)imp;
				String language = sourceCode.getLanguage();
				File target = new File(embeddedDir, sourceCode.getSourceFile());

				Debug.out(Debug.MODE_DEBUG,MODULE_NAME,"Found embedded source: "+sourceCode.getClassName());
				Debug.out(Debug.MODE_DEBUG,MODULE_NAME,"\tLanguage: "+language);
				Debug.out(Debug.MODE_DEBUG,MODULE_NAME,"\tFile: "+sourceCode.getSourceFile());

				List languageProjects = allProjects.getProjectsByLanguage(language);
				if (languageProjects == null)
					throw new ModuleException("No projects for language " + language, MODULE_NAME);

				Iterator lpIter = languageProjects.iterator();
				if (!lpIter.hasNext())
					throw new ModuleException("There is no project to add the embedded source to, the embedded code: "+sourceCode.className+" is added to the first project of type: "+sourceCode.language, MODULE_NAME);
				else
				{
					Project prj = (Project)lpIter.next();
					Debug.out(Debug.MODE_DEBUG,MODULE_NAME,"Adding embedded code to project: " + prj.getName());

					Composestar.Core.Master.Config.Source source = new Composestar.Core.Master.Config.Source();
					source.setFileName(target.getAbsolutePath());
					prj.addSource(source);

					TypeSource ts = new TypeSource();
					ts.setFileName(target.getAbsolutePath());
					ts.setName(sourceCode.getClassName());
					prj.addTypeSource(ts);

					this.saveToFile(sourceCode, target);
				}
			}
		}
	}

	/**
	 * Stores the embedded source in a new file.
	 */
	private void saveToFile(Source src, File target) throws ModuleException
	{
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(target));
			bw.write(src.getSource());
		}
		catch (IOException e) {
			throw new ModuleException("Could not save embedded source:" + e.getMessage() , MODULE_NAME);
		}
		finally {
			FileUtils.close(bw);
		}
	}
}


