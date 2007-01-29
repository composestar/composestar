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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.FileUtils;
import antlr.ANTLRException;
import antlr.CommonAST;
import antlr.RecognitionException;

/**
 * Parses the cps file and builds the parse tree
 */
public class CPSFileParser
{
	public ParseResult parse(String filename) throws ModuleException
	{
		ParseResult pr = innerParse(filename);

		if (pr.startPos != -1)
			pr.embeddedCode = extractEmbeddedCode(filename, pr.startPos);
		
		return pr;
	}
	
	private ParseResult innerParse(String filename) throws ModuleException
	{
		Reader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(filename));
			
			CpsPosLexer lexer = new CpsPosLexer(reader);
			CpsParser parser = new CpsParser(lexer);
			parser.getASTFactory().setASTNodeClass(CpsAST.class);
			parser.concern();
			
			ParseResult pr = new ParseResult();
			pr.filename = filename;
			pr.ast = (CommonAST) parser.getAST();
			pr.startPos = parser.startPos;
			return pr;
		}
		catch (FileNotFoundException e)
		{
			throw new ModuleException(
					"Could not open '" + filename + "': " + e.getMessage(), COPPER.MODULE_NAME);
		}
		catch (RecognitionException e)
		{
			throw new ModuleException(
					"Syntax error in concern file: " + e, COPPER.MODULE_NAME, filename, e.getLine());
		}
		catch (ANTLRException e)
		{
			throw new ModuleException(
					"Syntax error in concern file: " + e, COPPER.MODULE_NAME, filename, 0);
		}
		finally
		{
			FileUtils.close(reader);
		}
	}

	private String extractEmbeddedCode(String filename, int startPos) throws ModuleException
	{
		Reader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(filename));
			StringWriter sw = new StringWriter();
			
			// skip until embedded code starts
			int toSkip = startPos - 1;
			while (toSkip > 0)
				toSkip -= reader.skip(startPos);
			
			// copy the rest
			FileUtils.copy(reader, sw);

			// trim and return the result
			return trimEmbeddedCode(sw.toString());
		}
		catch (FileNotFoundException e)
		{
			throw new ModuleException(
					"Could not open '" + filename + "': " + e.getMessage(), COPPER.MODULE_NAME);
		}
		catch (IOException e)
		{
			throw new ModuleException(
					"Error reading from '" + filename + "': " + e.getMessage(), COPPER.MODULE_NAME);
		}
		finally
		{
			FileUtils.close(reader);
		}
	}
	
	private String trimEmbeddedCode(String source) throws ModuleException
	{
		// find closing tag of concern
		int endpos = source.lastIndexOf('}');
		if (endpos > 0)
		{
			// find closing tag of implementation by
			endpos = source.lastIndexOf('}', endpos - 1);
		}

		if (endpos <= 0)
		{
			throw new ModuleException(
					"Expecting closing '}' at end of cps file and after embedded source",
					COPPER.MODULE_NAME);
		}

		return source.substring(0, endpos);
	}
}
