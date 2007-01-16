/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CMaster.java,v 1.1 2006/09/01 15:31:20 johantewinkel Exp $
 */

package Composestar.C.MASTER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class CMaster extends Master
{

	/**
	 * Compose* main function. Creates the Master object. Adds the desired
	 * modules and then calls run on each of them in the order that they where
	 * added.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarC.jar <config file>");
			return;
		}
		main(CMaster.class, args);
	}

	public void SaveModifiedConfigurationKeys(CommonResources resources)
	{
		ArrayList builtAssemblies = (ArrayList) resources.getResource("BuiltAssemblies");
		if (builtAssemblies == null)
		{
			builtAssemblies = new ArrayList();
		}
		String execAssembly = (String) DataStore.instance().getObjectByID("Executable");
		if (!builtAssemblies.contains(execAssembly))
		{
			/* add the executable */
			builtAssemblies.add(execAssembly);
		}
		java.util.ArrayList configlines = new java.util.ArrayList();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(configfile));
			String line = br.readLine();
			while (line != null)
			{
				if (line.startsWith("BuiltAssemblies="))
				{
					line = "BuiltAssemblies=" + builtAssemblies.size();
					configlines.add(line);
					for (int i = 0; i < builtAssemblies.size(); i++)
					{
						Object temp = builtAssemblies.get(i);
						if (temp != null)
						{
							configlines.add("BuiltAssembly" + i + "=" + temp.toString());
						}
					}
				}
				else
				{
					configlines.add(line);
				}
				line = br.readLine();
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new FileWriter(configfile));
			Iterator iterLines = configlines.iterator();
			for (Object configline : configlines)
			{
				line = (String) configline;
				bw.write(line + "\n");
			}
			bw.close();
		}
		catch (IOException e)
		{
			Debug.out(Debug.MODE_WARNING, "Master", "Unable to update configuration file '" + configfile + "'!");
		}

	}
}
