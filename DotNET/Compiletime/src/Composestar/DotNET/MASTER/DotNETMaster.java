/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.MASTER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class DotNETMaster extends Master
{
	public DotNETMaster(String[] args)
	{
		super(args);
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public void run()
	{
		try
		{
			long beginTime = System.currentTimeMillis();
			
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Creating datastore...");
			DataStore.instance();

			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.run(resources);

			// execute enabled modules one by one
			Iterator modulesIter = incre.getModules();
			while (modulesIter.hasNext())
			{
				Module m = (Module) modulesIter.next();
				m.execute(resources);
			}

			incre.getReporter().close();

			// display total time elapsed
			long total = System.currentTimeMillis() - beginTime;
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Total time: " + total + "ms");

			// display number of warnings
			if (Debug.willLog(Debug.MODE_WARNING))
			{
				Debug.outWarnings();
			}
		}
		catch (ModuleException e)
		{
			String error = e.getMessage();
			String filename = e.getErrorLocationFilename();
			int lineNumber = e.getErrorLocationLineNumber();
			
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			if (filename == null || "".equals(filename))
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			}
			else
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, filename, lineNumber);
			}

			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + Debug.stackTrace(e));
			System.exit(ECOMPILE);
		}
		catch (Exception e)
		{
			String error = e.getMessage();
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Internal compiler error: " + error);
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
			System.exit(EFAIL);
		}
	}

	// not used
	public void saveModifiedConfigurationKeys(CommonResources resources)
	{
		List builtAssemblies = (List) resources.getResource("BuiltAssemblies");
		List configLines = new ArrayList();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(configfile));
			String line = br.readLine();
			while (line != null)
			{
				if (line.startsWith("BuiltAssemblies="))
				{
					line = "BuiltAssemblies=" + builtAssemblies.size();
					configLines.add(line);
					for (int i = 0; i < builtAssemblies.size(); i++)
					{
						Object temp = builtAssemblies.get(i);
						if (temp != null)
						{
							configLines.add("BuiltAssembly" + i + "=" + temp.toString());
						}
					}
				}
				else
				{
					configLines.add(line);
				}

				line = br.readLine();
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new FileWriter(configfile));
			Iterator iterLines = configLines.iterator();
			while (iterLines.hasNext())
			{
				line = (String) iterLines.next();
				bw.write(line + "\n");
			}
			bw.close();
		}
		catch (IOException e)
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Unable to update configuration file '" + configfile + "'!");
		}
	}

	/**
	 * Compose* main function. Creates the Master object and invokes the run method.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarDotNET.jar <config file>");
			return;
		}

		String arg = args[0].toLowerCase();
		if (arg.equals("-v") || arg.equals("--version"))
		{
			Version.reportVersion(System.out);
			return;
		}
		
		Master master = new DotNETMaster(args);
		try
		{
			master.loadConfiguration();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(ECONFIG);
		}

		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, Version.getTitle() + " " + Version.getVersionString());
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Compiled on "+Version.getCompileDate().toString());
		master.run();
	}
}
