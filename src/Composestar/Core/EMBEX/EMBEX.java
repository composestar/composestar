package Composestar.Core.EMBEX;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.*;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;

import java.util.Iterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class responsible for extracting and storing embedded sources to disk
 */
public class EMBEX implements CTCommonModule
{
	private String tempPath = "";
	private String embeddedDir = "";
	private String embeddedPath = "";

	/**
	 * @roseuid 41AB1C7600BB
	 */
	public EMBEX() 
	{
     	
    }
    
	/**
	 * Iterates over cps concerns and calls saveToFile for embedded sources found
	 * Creates directory for embedded sources  
	 * @param resources
	 * @throws Composestar.Core.Exception.ModuleException
	 * @roseuid 41AB189F01F4
	 */
	public void run(CommonResources resources) throws ModuleException 
	{
		DataStore ds = DataStore.instance();
		Iterator cpsConcernIter = ds.getAllInstancesOf(CpsConcern.class);

		// fetch temppath
		tempPath = Configuration.instance().getPathSettings().getPath( "Base");
		if( tempPath.equals( "ERROR" ) ) 
		{
			throw new ModuleException( "Error in configuration file: No such property TempFolder" );
		}
		
		//fetch embedded sources directory
		embeddedDir = Configuration.instance().getPathSettings().getPath( "EmbeddedSources");
		if( embeddedDir.equals( "" ) ) 
		{
			throw new ModuleException( "Error in configuration file: No such property EmbeddedSources" );
		}

		// create directory for embedded code
		embeddedPath = tempPath + embeddedDir;
		File fileDir = new File(embeddedPath);
		if(fileDir.exists())
		{
			// bad news
			Debug.out(Debug.MODE_WARNING,"EMBEX","Cannot create directory for embedded sources. Directory exists! Removing files in directory");	
			try
			{
				fileDir.delete();
			}
			catch(Exception e)
			{
			}
		}
		/*else 
		{ 
			// Only create the directory if it is really needed!
			if(cpsConcernIter.hasNext())
			{
				fileDir.mkdir();
			}
		}*/

		// iterate over all cps concerns
		for (; cpsConcernIter.hasNext();) 
		{
			// fetch implementation
			CpsConcern cps = (CpsConcern)cpsConcernIter.next();
			Implementation imp = cps.getImplementation();

			if(imp instanceof Source)
			{
				if(!fileDir.exists())
				{
					fileDir.mkdir();
				}
				// fetch embedded source and save
				Source src = (Source)imp;
				src.getLanguage();
				Debug.out(Debug.MODE_DEBUG,"EMBEX","Found embedded source: "+src.getClassName());
				Debug.out(Debug.MODE_DEBUG,"EMBEX","\tLanguage: "+src.getLanguage());
				Debug.out(Debug.MODE_DEBUG,"EMBEX","\tFile: "+src.getSourceFile());
				Configuration config = Configuration.instance();
				Iterator projectit = config.getProjects().getProjectsByLanguage(src.getLanguage()).iterator();
				if(!projectit.hasNext())
					throw new ModuleException("There is no project to add the embedded source to, the embedded code: "+src.className+" is added to the first project of type: "+src.language);
				else
				{
					Project prj = (Project)projectit.next();
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
	 * @param embedded source
	 * @param resource
	 * @param src
	 * @roseuid 41AB19D500AB
	 */
	private void saveToFile(Source src,CommonResources resources) throws ModuleException
	{
		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(embeddedPath+src.getSourceFile()));
			bw.write(src.getSource());
			bw.close();
		}
		catch(IOException e)
		{
			throw new ModuleException( "ERROR while trying to save embedded source!:\n" + e.getMessage() , "EMBEX");
		}
	}
}

