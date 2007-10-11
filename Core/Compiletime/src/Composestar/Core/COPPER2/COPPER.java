/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.COPPER2;

import java.io.File;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.apache.log4j.Level;

import Composestar.Core.CpsProgramRepository.Legacy.LegacyFilterTypes;
import Composestar.Core.Exception.CpsSemanticException;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.Utils.Logging.CPSLogger;

/**
 * CPS parser and repository constructor.
 * 
 * @author Michiel Hendriks
 */
public class COPPER implements CTCommonModule
{
	public static final String MODULE_NAME = "COPPER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public COPPER()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		INCRE incre = INCRE.instance();

		if (LegacyFilterTypes.useLegacyFilterTypes)
		{
			LegacyFilterTypes.addLegacyFilterTypes();
		}

		for (File file : resources.configuration().getProject().getConcernFiles())
		{
			INCRETimer runTimer = incre.getReporter().openProcess(MODULE_NAME, file.toString(), INCRETimer.TYPE_NORMAL);
			parseConcernFile(file);
			runTimer.stop();
		}
	}

	protected void parseConcernFile(File file) throws ModuleException
	{
		logger.info(String.format("Parsing file %s", file.toString()));
		CpsLexer lex;
		try
		{
			lex = new CpsLexer(new ANTLRFileStream(file.toString()));
		}
		catch (IOException e1)
		{
			throw new ModuleException(e1.getMessage(), MODULE_NAME, e1);
		}
		CommonTokenStream tokens = new CommonTokenStream(lex);
		CpsParser p = new CpsParser(tokens);
		p.setSourceFile(file.toString());
		Tree rootNode;
		try
		{
			rootNode = (Tree) p.concern().getTree();
		}
		catch (RecognitionException e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME, file.toString(), e.line, e);
		}

		logger.debug("Walking AST");
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(rootNode);
		nodes.setTokenStream(tokens);
		CpsTreeWalker w = new CpsTreeWalker(nodes);
		w.setSourceFile(file.toString());
		w.setEmbeddedSourceLoc(p.getEmbeddedSourceLoc());
		try
		{
			w.concern();
		}
		catch (CpsSemanticException se)
		{
			// TODO: hanlde more gracefully by using RecognitionException's?
			throw new ModuleException(se.getMessage(), MODULE_NAME, se.getFilename(), se.getLineNumber(), se
					.getLinePosition(), se);
		}
		catch (RecognitionException e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME, file.toString(), e.line, e.charPositionInLine, e);
		}
	}

	public static void main(String[] args)
	{
		// BasicConfigurator.configure();
		CPSLogger.getRootLogger().setLevel(Level.DEBUG);
		DataMap.setDataMapClass(DataMapImpl.class);
		LegacyFilterTypes.useLegacyFilterTypes = true;
		LegacyFilterTypes.addLegacyFilterTypes();
		COPPER c = new COPPER();
		try
		{
			c.parseConcernFile(new File(args[0]));
		}
		catch (ModuleException e)
		{
			e.printStackTrace();
		}
	}
}
