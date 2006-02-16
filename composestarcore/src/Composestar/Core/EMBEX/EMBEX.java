package Composestar.Core.EMBEX;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.*;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

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
		tempPath = resources.ProjectConfiguration.getProperty( "TempFolder", "ERROR" );
		if( tempPath.equals( "ERROR" ) ) 
		{
			throw new ModuleException( "Error in configuration file: No such property TempFolder" );
		}
		
		//fetch embedded sources directory
		embeddedDir = resources.ProjectConfiguration.getProperty( "EmbeddedSourcesFolder", "ERROR" );
		if( embeddedDir.equals( "ERROR" ) ) 
		{
			throw new ModuleException( "Error in configuration file: No such property EmbeddedSourcesFolder" );
		}

		// create directory for embedded code
		embeddedPath = tempPath + embeddedDir;
		File fileDir = new File(embeddedPath);
		if(fileDir.exists())
		{
			// bad news
			throw new ModuleException( "Cannot create directory for embedded sources. Directory exists!", "EMBEX");	
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
				this.saveToFile(src,resources);
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

