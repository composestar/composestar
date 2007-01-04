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

import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Utils.Debug;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class DotNETMaster extends Master
{
	/**
	 * Compose* main function. Creates the Master object and invokes the run
	 * method.
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
		main(DotNETMaster.class, args);
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
}
