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

package Composestar.CwC.COMP;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.Config.Language;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Feed the C source files to the preprocessor
 * 
 * @author Michiel Hendriks
 */
public class PreProcess implements CTCommonModule
{
	public static final String MODULE_NAME = CCompiler.MODULE_NAME + ".PreProcess";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public PreProcess()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		ModuleInfo mi = ModuleInfoManager.get(PreProcess.class);
		if (mi.getSetting("preprocessed", false))
		{
			logger.info("Already preprocessed, skipping " + MODULE_NAME);
			return;
		}
		Project proj = resources.configuration().getProject();
		Language lang = proj.getPlatform().getLanguage(proj.getLanguage());
		if (lang == null)
		{
			logger.error(String.format("Language not found in the current platform: %s", proj.getLanguage()));
			throw new ModuleException("Unable to get compiler for the project language", MODULE_NAME);
		}
		SourceCompiler comp = lang.getCompiler();
		if (comp == null)
		{
			logger.error(String.format("No compiler for the language: %s", proj.getLanguage()));
			throw new ModuleException("Unable to get compiler for the project language", MODULE_NAME);
		}
		try
		{
			comp.getCompiler().setCommonResources(resources);
			comp.getCompiler().compileDummies(proj, proj.getSources());
		}
		catch (CompilerException e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME, e);
		}
	}
}
