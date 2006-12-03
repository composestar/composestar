/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.COPPER;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import antlr.ANTLRException;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.debug.misc.ASTFrame;

/**
 * Parses the cps file and builds the parse tree
 */
public class CPSFileParser
{
	private CpsParser parser;

	public void parseCpsFileWithName(String filename) throws ModuleException
	{
		this.readCpsFile(filename);
		this.parseCpsFile(filename);

		COPPER.setParser(parser);
		COPPER.setParseTree((CommonAST) parser.getAST());

		Debug.out(Debug.MODE_DEBUG, COPPER.MODULE_NAME, "Parse tree: " + parser.getAST().toStringTree());
		if (COPPER.isShowtree())
		{
			System.out.println(COPPER.getParseTree().toStringList() + "\n\n");
			ASTFrame frame = new ASTFrame(filename, COPPER.getParseTree());
			frame.setSize(800, 600);
			frame.setVisible(true);
		}
	}

	public void readCpsFile(String file) throws ModuleException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuffer sb = new StringBuffer();

			String line = reader.readLine();
			while (line != null)
			{
				sb.append(line).append("\r\n");
				line = reader.readLine();
			}

			COPPER.setCpscontents(sb.toString());
		}
		catch (IOException ioe)
		{
			throw new ModuleException("The specified file '" + file + "' could not be found or read!", COPPER.MODULE_NAME);
		}
		finally
		{
			FileUtils.close(reader);
		}
	}

	public void parseCpsFile(String file) throws ModuleException
	{
		try
		{
			StringReader sr = new StringReader(COPPER.getCpscontents());
			CpsPosLexer lexer = new CpsPosLexer(sr);
			parser = new CpsParser(lexer);
			parser.getASTFactory().setASTNodeClass(CpsAST.class);
			// parser.setASTNodeClass("Composestar.Core.COPPER.CpsAST");
			parser.concern();
		}
		catch (RecognitionException e)
		{
			throw new ModuleException("Syntax error in concern file: " + e, COPPER.MODULE_NAME, file, e.getLine());
		}
		catch (ANTLRException e)
		{
			throw new ModuleException("Syntax error in concern file: " + e, COPPER.MODULE_NAME, file, 0);
		}
	}
}
