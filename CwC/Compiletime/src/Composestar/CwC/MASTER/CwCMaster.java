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

import java.util.Map.Entry;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.CpsProgramRepository.Filters.FilterTypeNames;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.CmdLineParser;

/**
 * @author Michiel Hendriks
 */
public class CwCMaster extends Master
{
	protected String intermediateDir;

	@Override
	protected void loadConfiguration() throws Exception
	{
		if (configFilename != null)
		{
			super.loadConfiguration();
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
			// todo: add files

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
		}
		// FIXME this probably needs to be improved, how to load it properly?
		// where to get the custom filters from?
		DefaultFilterFactory filterFactory = new DefaultFilterFactory(resources.repository());
		String[] filters = { FilterTypeNames.DISPATCH, FilterTypeNames.SEND, FilterTypeNames.ERROR,
				FilterTypeNames.BEFORE, FilterTypeNames.AFTER, FilterTypeNames.SUBSTITUTION };
		filterFactory.createFilterTypes(filters);
		resources.put(DefaultFilterFactory.RESOURCE_KEY, filterFactory);
	}

	@Override
	public void processCmdArgs(String[] args)
	{
		CmdLineParser parser = new CmdLineParser();
		super.addCmdLineOptions(parser);
		CmdLineParser.SwitchOption ispped = new CmdLineParser.SwitchOption("preprocessed");
		parser.addOption(ispped);
		CmdLineParser.StringOption intermed = new CmdLineParser.StringOption('i', "intermediate");
		parser.addOption(intermed);
		parser.setDefaultOption(new CmdLineParser.StringListOption());

		parser.parse(args);
		procCmdLineOptions(parser);

		if (ispped.isSet() && ispped.getValue())
		{
			settingsOverride.put("PreCOMP.preprocessed", "true");
		}
		if (intermed.isSet())
		{
			intermediateDir = intermed.getValue();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarCwC.jar [options] <config file>");
			return;
		}
		main(CwCMaster.class, args);
	}

}
