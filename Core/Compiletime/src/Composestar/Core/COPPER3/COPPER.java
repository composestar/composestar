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

package Composestar.Core.COPPER3;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;

import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintFactory;
import Composestar.Core.CpsRepository2Impl.References.ReferenceManagerImpl;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.ConstraintFactoryImpl;
import Composestar.Core.EMBEX.EmbeddedSources;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.DevNullOutputStream;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * COPPER parses the concern sources and produces the repository entries that
 * will be used by other modules. During processing the so called syntactic
 * sugar is applied that expands the repository entries to their complete form.
 * Only the concern source syntax will be validated.
 * 
 * @author Michiel Hendriks
 */
public class COPPER implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COPPER);

	/**
	 * Number of parse errors
	 */
	protected int errorCnt;

	protected Repository repository;

	/**
	 * Defined filter types
	 */
	protected FilterTypeMapping filterTypes;

	/**
	 * The filter factory to use
	 */
	protected FilterTypeFactory filterTypeFactory;

	/**
	 * If true export the antlr tree to a .dot file. Useful for debugging.
	 */
	@ModuleSetting
	protected boolean debugExportDot;

	@ResourceManager
	protected EmbeddedSources embeddedSourceManager;

	/**
	 * Reference manager
	 */
	protected ReferenceManager refman;

	protected ConstraintFactory constraintFactory;

	public COPPER()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.COPPER;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.
	 * CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		repository = resources.repository();
		filterTypes = resources.get(FilterTypeMapping.RESOURCE_KEY);
		if (filterTypes == null)
		{
			filterTypes = new FilterTypeMapping(repository);
			resources.put(FilterTypeMapping.RESOURCE_KEY, filterTypes);
		}

		filterTypeFactory = resources.get(FilterTypeFactory.RESOURCE_KEY);
		if (filterTypeFactory == null)
		{
			filterTypeFactory = new FilterTypeFactory(repository);
			resources.put(FilterTypeFactory.RESOURCE_KEY, filterTypeFactory);
		}
		filterTypeFactory.setTypeMapping(filterTypes);

		refman = resources.get(ReferenceManager.RESOURCE_KEY);
		if (refman == null)
		{
			refman = new ReferenceManagerImpl();
			resources.put(ReferenceManager.RESOURCE_KEY, refman);
		}
		constraintFactory = new ConstraintFactoryImpl();

		errorCnt = 0;

		CPSTimer timer = CPSTimer.getTimer(ModuleNames.COPPER);
		for (File file : resources.configuration().getProject().getConcernFiles())
		{
			timer.start(file.toString());
			parseConcernFile(file);
			timer.stop();
		}

		if (errorCnt > 0)
		{
			logger.error(String.format("%d error(s) detected in the concern sources.", errorCnt));
			return ModuleReturnValue.ERROR;
		}
		return ModuleReturnValue.OK;
	}

	/**
	 * Parse a single CPS file
	 * 
	 * @param file
	 * @throws ModuleException
	 */
	protected void parseConcernFile(File file) throws ModuleException
	{
		// workaround for some ANTLRv3 debug output
		PrintStream oldErr = System.err;
		System.setErr(new PrintStream(new DevNullOutputStream()));
		try
		{

			int localErrCnt = 0;

			logger.info(String.format("Parsing file %s", file.toString()));
			CpsLexer lex;
			try
			{
				lex = new CpsLexer(new ANTLRFileStream(file.toString()));
			}
			catch (IOException e1)
			{
				throw new ModuleException(e1.getMessage(), ModuleNames.COPPER, e1);
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
				System.setErr(oldErr);
				throw new ModuleException(e.getMessage(), ModuleNames.COPPER, file.toString(), e.line, e);
			}
			localErrCnt += p.getErrorCnt();

			if (debugExportDot)
			{
				ASTExporter.dotExport(new File(file + ".dot"), rootNode);
			}

			if (localErrCnt > 0)
			{
				logger.error(String.format("%s contains %d error(s)", file.toString(), localErrCnt));
				return;
			}

			logger.debug("Walking AST");
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(rootNode);
			nodes.setTokenStream(tokens);
			CpsTreeWalker w = new CpsTreeWalker(nodes);
			w.setSourceFile(file);
			w.setRepository(repository);
			w.setFilterTypeMapping(filterTypes);
			w.setFilterFactory(filterTypeFactory);
			w.setEmbeddedSourceManager(embeddedSourceManager);
			w.setReferenceManager(refman);
			w.setConstraintFactory(constraintFactory);
			try
			{
				w.concern();
			}
			catch (RecognitionException e)
			{
				System.setErr(oldErr);
				throw new ModuleException(e.getMessage(), ModuleNames.COPPER, file.toString(), e.line,
						e.charPositionInLine, e);
			}
			System.setErr(oldErr);

			localErrCnt += w.getErrorCnt();
			errorCnt += localErrCnt;

			if (localErrCnt > 0)
			{
				logger.error(String.format("%s contains %d error(s)", file.toString(), localErrCnt));
			}
		}
		finally
		{
			System.setErr(oldErr);
		}
	}
}
