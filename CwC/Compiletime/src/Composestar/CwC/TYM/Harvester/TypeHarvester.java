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

package Composestar.CwC.TYM.Harvester;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.log4j.Level;

import weavec.aspectc.acmodel.AnnotationDescriptor;
import weavec.aspectc.acmodel.CTypeDescriptor;
import weavec.ast.TNode;
import weavec.grammar.AnonymousIdentifier;
import weavec.grammar.TranslationUnitResult;
import weavec.parser.AspectCLexer;
import weavec.parser.AspectCParser;
import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.CwC.TYM.WeaveCResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Logging.OutputStreamRedirector;
import Composestar.Utils.Perf.CPSTimer;
import antlr.RecognitionException;
import antlr.TokenStreamException;

// TODO: check for duplication of type information
/**
 * Uses WeaveC to harvest type information from the preprocessed C files.
 * 
 * @author Michiel Hendriks
 */
// @ComposestarModule(ID = ModuleNames.HARVESTER)
public class TypeHarvester implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.HARVESTER);

	@ResourceManager
	protected WeaveCResources weavecRes;

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.HARVESTER;
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		initWeaveC();
		for (Source src : resources.configuration().getProject().getSources())
		{
			harvest(src);
		}
		return ModuleReturnValue.OK;
	}

	protected void initWeaveC()
	{
		AnonymousIdentifier.reset();
		TNode.setTokenVocabulary("ACGrammarTokenTypes");
		// AdviceTemplateFunction.setcounter(new Integer(0));
		// BindingEntity.setcounter(new Integer(0));
		CTypeDescriptor.setcounter(Integer.valueOf(0));
		CTypeDescriptor.ctdescriptors().clear();
		AnnotationDescriptor.setcounter(Integer.valueOf(0));
		AnnotationDescriptor.ctdescriptors().clear();
	}

	protected void harvest(Source source)
	{
		File sourceFile = source.getStub();
		String moduleName = FileUtils.removeExtension(source.getRawFile().toString());

		logger.info(String.format("Harvesting from file %s", sourceFile));
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.HARVESTER, sourceFile.toString());
		try
		{
			try
			{
				InputStream is = new BufferedInputStream(new FileInputStream(sourceFile));

				AspectCLexer lexer = new AspectCLexer(is);
				lexer.setSource(sourceFile.toString());
				lexer.newPreprocessorInfoChannel();
				lexer.setTokenNumber(weavecRes.getTokenNumber());
				lexer.yybegin(AspectCLexer.C);

				// ACGrammarLexer lexer = new ACGrammarLexer(is);
				// lexer.setTokenObjectClass(CToken.class.getName());
				// lexer.initialize(sourceFile.toString());
				// lexer.setAnnotationsLexer();

				AspectCParser cparser = new AspectCParser(lexer);
				cparser.setASTNodeClass(TNode.class.getName());
				cparser.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));
				// cparser.tracing = new PrintStream(new
				// OutputStreamRedirector(logger, Level.TRACE));
				// cparser.diagnostics = cparser.tracing;
				// cparser.verbosity = 2;

				cparser.setSource(sourceFile.toString());
				try
				{
					TranslationUnitResult tunit = cparser.cfile(moduleName);
					weavecRes.addTranslationUnitResult(tunit);
					weavecRes.addPreprocessorInfoChannel(tunit, lexer.getPreprocessorInfoChannel());
					weavecRes.addSourceMapping(tunit, source);
					weavecRes.setTokenNumber(lexer.getTokenNumber());
				}
				catch (RecognitionException e)
				{
					logger.error(new LogMessage(e.getMessage(), (new File(e.getFilename())).toString(), e.getLine(), e
							.getColumn()), e);
				}
				catch (TokenStreamException e)
				{
					logger.error(e, e);
				}
			}
			catch (FileNotFoundException e)
			{
				logger.error(e, e);
			}
		}
		finally
		{
			timer.stop();
		}
	}
}
