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
package Composestar.CwC.WEAVER;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;

import weavec.ast.TNode;
import weavec.ast.TNodeFactory;
import weavec.cmodel.scope.AnnotationScope;
import weavec.cmodel.scope.BlockScope;
import weavec.cmodel.scope.LabelScope;
import weavec.cmodel.util.AnnotationScopeImpl;
import weavec.cmodel.util.ScopeConstructor;
import weavec.emitter.CEmitter;
import weavec.grammar.TranslationUnitResult;
import weavec.parser.ACGrammarTokenTypes;
import weavec.parser.AspectCLexer;
import weavec.parser.AspectCParser;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Config.Project;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INLINE.CodeGen.CodeGenerator;
import Composestar.Core.INLINE.CodeGen.DispatchActionCodeGen;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.SIinfo;
import Composestar.Core.WEAVER.WEAVER;
import Composestar.CwC.INLINE.CodeGen.CCodeGenerator;
import Composestar.CwC.LAMA.CwCFile;
import Composestar.CwC.LAMA.CwCFunctionInfo;
import Composestar.CwC.TYM.WeaveCResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Logging.OutputStreamRedirector;
import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * @author Michiel Hendriks
 */
public class CwCWeaver implements WEAVER
{
	public static final String MODULE_NAME = "WEAVER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	@ResourceManager
	protected WeaveCResources weavecResc;

	@ResourceManager
	protected InlinerResources inlinerRes;

	protected CodeGenerator<String> codeGen;

	public CwCWeaver()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		codeGen = new CCodeGenerator();
		codeGen.register(new DispatchActionCodeGen(inlinerRes));
		// TODO: add all code gens

		Iterator<Concern> concernIterator = resources.repository().getAllInstancesOf(Concern.class);
		while (concernIterator.hasNext())
		{
			Concern concern = concernIterator.next();
			processConcern(concern);
		}
		Project p = resources.configuration().getProject();
		File outputDir = new File(p.getIntermediate(), "woven");
		if (!outputDir.isDirectory() && !outputDir.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create target directory for preprocessing: %s",
					outputDir.toString()), MODULE_NAME);
		}
		for (TranslationUnitResult tunit : weavecResc.translationUnitResults())
		{
			File target = FileUtils.relocateFile(p.getBase(), weavecResc.getSource(tunit).getFile(), outputDir);
			if (!target.getParentFile().exists() && !target.getParentFile().mkdirs())
			{
				throw new ModuleException(String.format("Unable to create parent directories for: %s", target
						.toString()), MODULE_NAME);
			}
			FileWriter output;
			try
			{
				output = new FileWriter(target);
				CEmitter emitter = new CEmitter(weavecResc.getPreprocessorInfoChannel(tunit), output);
				emitter.setASTNodeClass(TNode.class.getName());
				emitter.translationUnit(tunit.getModuleDeclaration().getAST());
				output.close();
			}
			catch (IOException e)
			{
				logger.error(e.getMessage(), e);
				continue;
			}
			catch (RecognitionException e)
			{
				logger.error(e.getMessage(), e);
				continue;
			}
		}
	}

	protected void processConcern(Concern concern)
	{
		CwCFile type = (CwCFile) concern.getPlatformRepresentation();
		if (type == null)
		{
			return;
		}

		if (concern.getDynObject(SIinfo.DATAMAP_KEY) == null)
		{
			return;
		}

		logger.info(String.format("Weaving concern %s", concern.getQualifiedName()));

		Signature sig = concern.getSignature();
		List<CwCFunctionInfo> functions = sig.getMethods(MethodWrapper.NORMAL + MethodWrapper.ADDED);
		for (CwCFunctionInfo func : functions)
		{
			FilterCode filterCode = inlinerRes.getInputFilterCode(func);
			if (filterCode != null)
			{
				processFilterCode(func, filterCode);
			}
			// TODO: call to other methods
		}
	}

	protected void processFilterCode(CwCFunctionInfo func, FilterCode fc)
	{
		// generate ANSI-C code
		Reader ccode = new StringReader(codeGen.generate(fc, func, inlinerRes.getMethodId(func)));

		// parse ANSI-C code to AST
		// parser.compoundStatement(...)
		// -> must be: { ... }

		AspectCLexer lexer = new AspectCLexer(ccode);
		lexer.yybegin(AspectCLexer.C);

		AspectCParser cparser = new AspectCParser(lexer);
		cparser.setASTNodeClass(TNode.class.getName());
		cparser.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));

		TNode fcAst = null;
		try
		{
			BlockScope blockScope = ScopeConstructor.getRootScope();
			LabelScope labelScope = ScopeConstructor.getLabelScope();
			AnnotationScope annotationScope = new AnnotationScopeImpl();
			cparser.compoundStatement(blockScope, labelScope, annotationScope);
			// getAST should return the returnAst, which contains the result of
			// the previous method execution
			fcAst = (TNode) cparser.getAST();
		}
		catch (RecognitionException e)
		{
			logger.error(new LogMessage(e.getMessage(), "", e.getLine(), e.getColumn()), e);
			return;
		}
		catch (TokenStreamException e)
		{
			logger.error(e, e);
			return;
		}

		// inject AST into function node
		TNode functionAST = func.getFunctionDeclaration().getAST();
		TNode bodyAST = functionAST.getLastSibling();
		TNode newBodyAST = createTNode(ACGrammarTokenTypes.NCompoundStatement, "{", bodyAST);
		bodyAST.doubleLink();
		bodyAST.removeSelf();
		newBodyAST.addChild(fcAst);
		newBodyAST.addChild(bodyAST);
		newBodyAST.addChild(createTNode(ACGrammarTokenTypes.RCURLY, "}", bodyAST.getLastChild()));
		functionAST.getLastSibling().addSibling(newBodyAST);
	}

	protected TNode createTNode(int type, String text, TNode ref)
	{
		return TNodeFactory.getInstance().create(type, text);
	}
}
