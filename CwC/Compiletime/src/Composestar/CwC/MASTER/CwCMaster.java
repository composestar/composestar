/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.CwC.MASTER;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.CustomFilter;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.CwC.Filters.FilterLoader;
import Composestar.Utils.CmdLineParser;

/**
 * @author Michiel Hendriks
 */
public class CwCMaster extends Master
{
	protected String intermediateDir;

	protected List<String> cfiles, cpsfiles;

	protected CmdLineParser.StringListOption customFilters;

	@Override
	protected boolean loadConfiguration() throws Exception
	{
		if (cfiles.size() == 0)
		{
			if (!super.loadConfiguration())
			{
				return false;
			}
		}
		else
		{
			// create the repository and common resources
			resources = new CommonResources();
			resources.setRepository(DataStore.instance());
			resources.put(RESOURCE_CONFIGFILE, configFilename);
			BuildConfig config = new BuildConfig();
			Project proj = config.getNewProject();

			proj.setBase(System.getProperty("user.dir"));
			proj.setIntermediate(intermediateDir);
			proj.setPlatform("CwC");
			proj.setLanguage("C");
			proj.setName(proj.getBase().getName());
			for (String file : cfiles)
			{
				Source s = new Source();
				s.setFile(new File(file));
				proj.addSource(s);
			}
			for (String file : cpsfiles)
			{
				proj.addConcern(file);
			}

			resources.setConfiguration(config);
			resources.setPathResolver(getPathResolver());
			if (!debugOverride)
			{
				setLogLevel(config.getSetting("buildDebugLevel"));
			}
			ModuleInfoManager.getInstance().setBuildConfig(config);

			for (Entry<String, String> override : settingsOverride.entrySet())
			{
				config.addSetting(override.getKey(), override.getValue());
			}

			if (customFilters != null && customFilters.isSet())
			{
				for (String cfspec : customFilters.getValue())
				{
					CustomFilter cf = new CustomFilter();
					cf.setLibrary(cfspec);
					config.getFilters().add(cf);
				}
			}
		}
		FilterLoader loader = new FilterLoader();
		loader.load(resources);
		return true;
	}

	@Override
	public boolean processCmdArgs(String[] args)
	{
		super.addCmdLineOptions();
		CmdLineParser.SwitchOption notPPed = new CmdLineParser.SwitchOption('N', "notpreprocessed");
		notPPed.setDescription("The input files have not been preprocessed yet. "
				+ "This is identical to providing the switch: -D PreCOMP.preprocessed=false");
		parser.addOption(notPPed);
		CmdLineParser.StringOption intermed = new CmdLineParser.StringOption('i', "intermediate");
		intermed.setHelpValue("directory");
		intermed.setDescription("The directory where the intermediate files should be stored.");
		parser.addOption(intermed);
		CmdLineParser.StringListOption fileList = new CmdLineParser.StringListOption();
		fileList
				.setDescription("You can either provide a single BuildConfiguration.xml or a list of files. "
						+ "When a list of files is given all files ending with .cps are considered concern files, "
						+ "and all other files will be considered C source files. "
						+ "If no files are given Compose* will try to load the file BuildConfiguration.xml in the current directory.");
		fileList.setHelpValue("file");
		parser.setDefaultOption(fileList);
		customFilters = new CmdLineParser.StringListOption('F', "custom-filter");
		customFilters.setDescription("Include custom filters. The spec is a combination of an url "
				+ "of the package to load followed by a pound (#) and the fully qualified "
				+ "name of a class that implements the CustomCwCFilters class.");
		fileList.setHelpValue("spec");
		parser.addOption(customFilters);

		parser.parse(args);
		if (!procCmdLineOptions())
		{
			return false;
		}

		if (notPPed.isSet() && notPPed.getValue())
		{
			settingsOverride.put("PreCOMP.preprocessed", "false");
		}
		if (intermed.isSet())
		{
			intermediateDir = intermed.getValue();
		}
		cfiles = new ArrayList<String>();
		cpsfiles = new ArrayList<String>();
		if (fileList.isSet())
		{
			configFilename = null;
		}
		for (String file : fileList.getValue())
		{
			String locfile = file.toLowerCase();
			if (locfile.endsWith(".xml"))
			{
				// we assume it's the buildconfig
				configFilename = file;
			}
			else if (locfile.endsWith(".cps"))
			{
				cpsfiles.add(file);
			}
			else
			{
				// assume it's a source file
				cfiles.add(file);
			}
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		main(CwCMaster.class, "ComposestarCwC.jar", args);
	}
}
