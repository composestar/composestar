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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.SyntacticOrderingConstraint;
import Composestar.Core.FILTH2.ConstraintSpecification;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.DevNullOutputStream;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * CPS parser and repository constructor.
 * 
 * @author Michiel Hendriks
 */
public class COPPER implements CTCommonModule
{
	public static final String MODULE_NAME = "COPPER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * Number of parse errors
	 */
	protected int errorCnt;

	/**
	 * Map that contains the filter module ordering constraints. It is added to
	 * the common resources.
	 */
	protected Map<String, SyntacticOrderingConstraint> orderingconstraints;

	/**
	 * The constraint specification for FILTH2
	 */
	protected ConstraintSpecification constraintSpec;

	/**
	 * Defined filter types
	 */
	protected FilterTypeMapping filterTypes;

	/**
	 * The filter factory to use
	 */
	protected DefaultFilterFactory filterFactory;

	public COPPER()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		filterTypes = resources.get(FilterTypeMapping.RESOURCE_KEY);
		if (filterTypes == null)
		{
			filterTypes = new FilterTypeMapping(resources.repository());
			resources.put(FilterTypeMapping.RESOURCE_KEY, filterTypes);
		}
		filterFactory = resources.get(DefaultFilterFactory.RESOURCE_KEY);
		if (filterFactory == null)
		{
			filterFactory = new DefaultFilterFactory(filterTypes, resources.repository());
			resources.put(DefaultFilterFactory.RESOURCE_KEY, filterFactory);
		}
		else
		{
			filterFactory.setTypeMapping(filterTypes);
		}

		errorCnt = 0;

		orderingconstraints = new HashMap<String, SyntacticOrderingConstraint>();
		resources.put(SyntacticOrderingConstraint.FILTER_ORDERING_SPEC, orderingconstraints);

		constraintSpec = new ConstraintSpecification();
		resources.put(ConstraintSpecification.RESOURCE_KEY, constraintSpec);

		CPSTimer timer = CPSTimer.getTimer(MODULE_NAME);
		for (File file : resources.configuration().getProject().getConcernFiles())
		{
			timer.start(file.toString());
			parseConcernFile(file);
			timer.stop();
		}

		if (errorCnt > 0)
		{
			throw new ModuleException(String.format("%d error(s) detected in the concern sources.", errorCnt),
					MODULE_NAME);
		}
		return ModuleReturnValue.Ok;
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
				System.setErr(oldErr);
				throw new ModuleException(e.getMessage(), MODULE_NAME, file.toString(), e.line, e);
			}
			localErrCnt += p.getErrorCnt();

			if (localErrCnt > 0)
			{
				logger.error(String.format("%s contains %d error(s)", file.toString(), localErrCnt));
				return;
			}

			logger.debug("Walking AST");
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(rootNode);
			nodes.setTokenStream(tokens);
			CpsTreeWalker w = new CpsTreeWalker(nodes);
			w.setSourceFile(file.toString());
			w.setOrderingConstraints(orderingconstraints);
			w.setConstraintSpec(constraintSpec);
			w.setFilterTypeMapping(filterTypes);
			w.setFilterFactory(filterFactory);
			try
			{
				w.concern();
			}
			catch (RecognitionException e)
			{
				System.setErr(oldErr);
				throw new ModuleException(e.getMessage(), MODULE_NAME, file.toString(), e.line, e.charPositionInLine, e);
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

	/**
	 * For debugging
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
		DataMap.setDataMapClass(DataMapImpl.class);
		COPPER c = new COPPER();
		DataStore ds = DataStore.instance();
		c.filterTypes = new FilterTypeMapping(ds);
		c.filterFactory = new DefaultFilterFactory(c.filterTypes, ds);
		c.filterFactory.setAllowLegacyCustomFilters(true);
		try
		{
			c.parseConcernFile(new File(args[0]));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
