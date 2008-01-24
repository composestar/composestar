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
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.apache.log4j.BasicConfigurator;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.FILTH.SyntacticOrderingConstraint;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * @author Michiel Hendriks
 */
public class CpsParserTest extends TestCase
{
	protected File[] files;

	public CpsParserTest()
	{
		BasicConfigurator.configure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		DataMap.setDataMapClass(DataMapImpl.class);

		URL url = CpsParserTest.class.getResource("examples");
		File exampleDir = new File(url.toURI());
		if (!exampleDir.isDirectory())
		{
			System.err.println("No CPS example files");
			files = new File[0];
			return;
		}
		files = exampleDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.getName().endsWith(".cps");
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		files = null;
	}

	private int testCpsFile(File file)
	{
		System.out.println("Parsing file " + file.toString());

		DataStore ds = new DataStore();
		DataStore.setInstance(ds);

		CpsLexer lex;
		try
		{
			lex = new CpsLexer(new ANTLRFileStream(file.toString()));
		}
		catch (IOException e)
		{
			fail(String.format("CpsLexer construction for \"%s\" failed: %s", file.toString(), e.getMessage()));
			return -1;
		}
		CommonTokenStream tokens = new CommonTokenStream(lex);
		CpsParser parser = new CpsParser(tokens);
		Tree rootNode;
		try
		{
			rootNode = (Tree) parser.concern().getTree();
		}
		catch (RecognitionException e)
		{
			fail(String.format("Parsing of \"%s\" failed: %s", file.toString(), e.getMessage()));
			return -1;
		}

		CommonTreeNodeStream nodes = new CommonTreeNodeStream(rootNode);
		nodes.setTokenStream(tokens);
		CpsTreeWalker w = new CpsTreeWalker(nodes);
		w.setSourceFile(file.toString());
		w.setOrderingConstraints(new HashMap<String, SyntacticOrderingConstraint>());
		w.setFilterTypeMapping(new FilterTypeMapping());
		DefaultFilterFactory fact = new DefaultFilterFactory(w.filterTypes, ds);
		fact.setAllowLegacyCustomFilters(true);
		w.setFilterFactory(fact);
		try
		{
			w.concern();
		}
		catch (RecognitionException e)
		{
			fail(String.format("Parsing of \"%s\" failed: %s", file.toString(), e.getMessage()));
		}

		return parser.getErrorCnt() + w.getErrorCnt();
	}

	public void testCorrect()
	{
		for (File file : files)
		{
			if (!file.getName().matches("^correct.*"))
			{
				continue;
			}
			assertEquals(String.format("correct file %s produced parse errors", file.toString()), 0, testCpsFile(file));
		}
	}

	public void testWrong()
	{
		for (File file : files)
		{
			if (!file.getName().matches("^wrong.*"))
			{
				continue;
			}
			assertTrue(String.format("wrong file %s did not produce parse errors", file.toString()),
					testCpsFile(file) > 0);
		}
	}
}
