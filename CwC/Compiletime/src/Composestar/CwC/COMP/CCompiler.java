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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Config.CompilerAction;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Compiler for C
 * 
 * @author Michiel Hendriks
 */
public class CCompiler implements LangCompiler
{
	public static final String MODULE_NAME = "COMP";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected SourceCompiler compConfig;

	protected CommonResources resources;

	protected ModuleInfo moduleInfo;

	public void setCompilerConfig(SourceCompiler compilerConfig)
	{
		compConfig = compilerConfig;
	}

	public void setCommonResources(CommonResources resc)
	{
		resources = resc;
		moduleInfo = ModuleInfoManager.get(MODULE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.COMP.LangCompiler#compileDummies(Composestar.Core.Config.Project,
	 *      java.util.Set)
	 */
	public void compileDummies(Project p, Set<Source> sources) throws CompilerException
	{
		File ppOutput = new File(p.getIntermediate(), "preprocess");
		if (!ppOutput.isDirectory() && !ppOutput.mkdirs())
		{
			throw new CompilerException(String.format("Unable to create target directory for preprocessing: %s",
					ppOutput.toString()));
		}

		CompilerAction action = compConfig.getAction("preprocess");
		if (action == null)
		{
			throw new CompilerException(String.format("Missing preprocess action for language: %s", p.getLanguage()));
		}

		for (Source source : sources)
		{
			File sourceFile = source.getFile();
			Set<File> files = new HashSet<File>();
			files.add(sourceFile);

			if (!sourceFile.exists())
			{
				logger.warn(String.format("Source file does not exist: %s", sourceFile.toString()));
				continue;
			}
			File target = FileUtils.relocateFile(p.getBase(), sourceFile, ppOutput);
			if (!target.getParentFile().exists() && !target.getParentFile().mkdirs())
			{
				throw new CompilerException(String.format("Unable to create parent directories for: %s", target
						.toString()));
			}
			source.setStub(target);

			Properties prop = new Properties();
			prop.put("OUT", target.toString());
			prop.put("includedir", p.getBase().toString());
			String args = moduleInfo.getSetting("args", "");
			if (args != null && args.trim().length() > 0)
			{
				prop.put("args", args);
			}

			String[] cmdline = action.getCmdLine(p, files, prop);
			logger.debug(Arrays.toString(cmdline));
			CommandLineExecutor cmdExec = new CommandLineExecutor();
			cmdExec.setWorkingDir(p.getBase());
			int result = cmdExec.exec(cmdline);
			String compilerOutput = cmdExec.outputError();

			if (result != 0)
			{
				// there was an error
				StringTokenizer st = new StringTokenizer(compilerOutput, "\n");
				String lastToken = null;
				while (st.hasMoreTokens())
				{
					lastToken = st.nextToken();
					logger.error(lastToken);
				}
				throw new CompilerException("Errors during preprocessing.");
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.COMP.LangCompiler#compileSources(Composestar.Core.Config.Project,
	 *      java.util.Set)
	 */
	public void compileSources(Project p, Set<Source> sources) throws CompilerException, ModuleException
	{
	// TODO Auto-generated method stub

	}

}
