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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;

import weavec.ast.PreprocDirective;
import weavec.ast.PreprocessorInfoChannel;
import weavec.ast.TNode;
import weavec.ast.TNodeFactory;
import weavec.cmodel.declaration.FunctionDeclaration;
import weavec.cmodel.declaration.ModuleDeclaration;
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
import Composestar.Core.LAMA.ParameterInfo;
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
 * CwC Weaver. Weaves the filtercode (from INLINE) into the correct methods. It
 * does not actually use the WeaveC weaver engine, but in employs the same
 * methods (i.e. modifying the Antlr AST)
 * 
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

	/**
	 * The scope where composestar.h is loaded. This is needed for the generated
	 * code
	 */
	protected BlockScope rootScope;

	/**
	 * Location to the ComposeStar.h file in the "woven" directory
	 */
	protected File cshFile;

	/**
	 * The AST of the ComposeStar.H file
	 */
	protected TNode composestarHAst;

	/**
	 * The preprocessor channel of the parsed composestar.h
	 */
	protected PreprocessorInfoChannel cshPIC;

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

		Project p = resources.configuration().getProject();
		File outputDir = new File(p.getIntermediate(), "woven");
		if (!outputDir.isDirectory() && !outputDir.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create target directory for preprocessing: %s",
					outputDir.toString()), MODULE_NAME);
		}

		cshFile = FileUtils.relocateFile(p.getBase(), new File("ComposeStar.h"), outputDir);
		loadComposeStarH();
		copyComposeStarC(FileUtils.relocateFile(p.getBase(), new File("ComposeStar.c"), outputDir));

		// inject filter code
		Iterator<Concern> concernIterator = resources.repository().getAllInstancesOf(Concern.class);
		while (concernIterator.hasNext())
		{
			Concern concern = concernIterator.next();
			processConcern(concern);
		}

		// emit updated C files
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
				PreprocessorInfoChannel ppic = weavecResc.getPreprocessorInfoChannel(tunit);

				// insert the preprocessor data from the header file
				// for (Entry<Integer, ArrayList<Object>> entry :
				// cshPIC.objects.entrySet())
				// {
				// for (Object o : entry.getValue())
				// {
				// ppic.addLineForTokenNumber(o, entry.getKey());
				// }
				// }

				CEmitter emitter = new CEmitter(ppic, output);
				emitter.setASTNodeClass(TNode.class.getName());
				emitter.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));
				// emitter.tracings = System.err;
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

	protected InputStream getComposeStarFile(boolean header)
	{
		String filename = "ComposeStar.h";
		if (!header)
		{
			filename = "ComposeStar.c";
		}
		return CwCWeaver.class.getResourceAsStream(filename);
	}

	/**
	 * Load ComposeStar.h; this is used to get the global scope so that the
	 * JoinPointContext is known when generating the filter code
	 * 
	 * @throws ModuleException
	 */
	protected void loadComposeStarH() throws ModuleException
	{
		logger.info("Copying ComposeStar.h to output directory");
		InputStream cshstream = getComposeStarFile(true);

		// save a copy in the "woven" directory, which will be used in the
		// #include directive
		if (!cshFile.getParentFile().exists() && !cshFile.getParentFile().mkdirs())
		{
			throw new ModuleException(String.format("Unable to create parent directories for: %s", cshFile.toString()),
					MODULE_NAME);
		}

		if (cshFile.exists())
		{
			cshFile.delete();
		}
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(cshFile);
		}
		catch (FileNotFoundException e1)
		{
			throw new ModuleException(e1.toString(), MODULE_NAME, e1);
		}
		try
		{
			FileUtils.copy(cshstream, fos);
		}
		catch (IOException e1)
		{
			throw new ModuleException(e1.toString(), MODULE_NAME, e1);
		}

		// re-get the stream
		cshstream = getComposeStarFile(true);

		AspectCLexer lexer = new AspectCLexer(cshstream);
		lexer.setSource("ComposeStar.h");
		lexer.newPreprocessorInfoChannel();
		lexer.setTokenNumber(weavecResc.getTokenNumber());
		lexer.yybegin(AspectCLexer.C);
		AspectCParser cparser = new AspectCParser(lexer);
		cparser.setASTNodeClass(TNode.class.getName());
		cparser.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));
		cparser.setSource("ComposeStar.h");
		try
		{
			TranslationUnitResult csh = cparser.cfile("_COMPOSESTAR_H_");
			rootScope = csh.getRootScope();
			composestarHAst = csh.getAST();
			cshPIC = lexer.getPreprocessorInfoChannel();
		}
		catch (RecognitionException e)
		{
			logger.error(e, e);
		}
		catch (TokenStreamException e)
		{
			logger.error(e, e);
		}
	}

	protected void copyComposeStarC(File dest) throws ModuleException
	{
		logger.info("Copying ComposeStar.c to output directory");
		InputStream cshstream = getComposeStarFile(false);

		// save a copy in the "woven" directory, which will be used in the
		// #include directive
		if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs())
		{
			throw new ModuleException(String.format("Unable to create parent directories for: %s", dest.toString()),
					MODULE_NAME);
		}

		if (dest.exists())
		{
			dest.delete();
		}
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(dest);
		}
		catch (FileNotFoundException e1)
		{
			throw new ModuleException(e1.toString(), MODULE_NAME, e1);
		}
		try
		{
			FileUtils.copy(cshstream, fos);
		}
		catch (IOException e1)
		{
			throw new ModuleException(e1.toString(), MODULE_NAME, e1);
		}
	}

	protected void processConcern(Concern concern)
	{
		if (!(concern.getPlatformRepresentation() instanceof CwCFile))
		{
			return;
		}

		if (concern.getDynObject(SIinfo.DATAMAP_KEY) == null)
		{
			return;
		}

		logger.info(String.format("Weaving concern %s", concern.getQualifiedName()));

		CwCFile type = (CwCFile) concern.getPlatformRepresentation();

		boolean containsFilterCode = false;

		Signature sig = concern.getSignature();
		List<CwCFunctionInfo> functions = sig.getMethods(MethodWrapper.NORMAL);
		for (CwCFunctionInfo func : functions)
		{
			// look up the "real" function
			List<ParameterInfo> pis = func.getParameters();
			String[] params = new String[pis.size()];
			for (int i = 0; i < pis.size(); i++)
			{
				params[i] = pis.get(i).getParameterTypeString();
			}
			CwCFunctionInfo realFunc = (CwCFunctionInfo) type.getMethod(func.getName(), params);
			if (realFunc == null)
			{
				logger.error(String.format("Unable to find the method %s.%s(%s)", concern.getQualifiedName(), func
						.getName(), Arrays.toString(params)));
				continue;
			}
			FilterCode filterCode = inlinerRes.getInputFilterCode(func);
			if (filterCode != null)
			{
				containsFilterCode = true;
				processFilterCode(rootScope, realFunc, filterCode);
			}
			// TODO: call to other methods, how? This information isn't
			// harvested from the C file in the first place.
		}

		// TODO: process added signatures, added signatures can't be processed
		// using the same method as NORMAL because they don't have actual
		// function declarations in the code. They need to be added.

		if (containsFilterCode)
		{
			injectComposestarHInclude(type.getModuleDeclaration());
		}
	}

	/**
	 * Inject the filter code at the beginning of the method implementation.
	 * 
	 * @param rootScope
	 * @param func
	 * @param fc
	 */
	protected void processFilterCode(BlockScope rootScope, CwCFunctionInfo func, FilterCode fc)
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
			LabelScope labelScope = ScopeConstructor.getLabelScope();
			AnnotationScope annotationScope = new AnnotationScopeImpl();
			cparser.compoundStatement(ScopeConstructor.getScope(rootScope), labelScope, annotationScope);
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
		functionAST.doubleLink();
		TNode bodyAST = functionAST.getLastSibling();
		TNode newBodyAST = createTNode(ACGrammarTokenTypes.NCompoundStatement, "{", bodyAST);
		bodyAST.doubleLink();
		bodyAST.removeSelf();
		setMetaInfo(fcAst, RecursionMode.FOREST, -1, null, bodyAST.getTokenNumber());
		fcAst.doubleLink();
		newBodyAST.addChild(fcAst);
		newBodyAST.addChild(bodyAST);
		newBodyAST.addChild(createTNode(ACGrammarTokenTypes.RCURLY, "}", bodyAST.getLastChild()));
		functionAST.getLastSibling().addSibling(newBodyAST);
	}

	/**
	 * Inserts an #include directive just before the first function declaration
	 * 
	 * @param modDecl
	 */
	protected void injectComposestarHInclude(ModuleDeclaration modDecl)
	{
		// find injection AST
		TNode weaveNode = null;
		modDecl.getAST().doubleLink();

		for (FunctionDeclaration func : modDecl.getFunctions())
		{
			if (!func.isIncluded())
			{
				// the function's AST points to the "name" part, it should go up
				// 2 levels to get to the declaration part
				weaveNode = func.getAST().getParent().getParent();
				break;
			}
		}

		while (weaveNode.getTokenNumber() == -1)
		{
			weaveNode = weaveNode.getFirstChild();
		}

		PreprocDirective incdirective = new PreprocDirective(String.format("#include \"%s\"", cshFile.toString()
				.replace("\\", "\\\\")), 1);

		for (TranslationUnitResult tunit : weavecResc.translationUnitResults())
		{
			if (tunit.getModuleDeclaration() == modDecl)
			{
				PreprocessorInfoChannel ppic = weavecResc.getPreprocessorInfoChannel(tunit);
				ppic.addLineForTokenNumber(incdirective, weaveNode.getTokenNumber() - 1);
				return;
			}
		}
		logger.error("Unable to find location to inject the ComposeStar.h include directive");
	}

	/**
	 * Injects the contents of the composestar.h file before the first function
	 * declaration. NOTE: BROKEN
	 * 
	 * @param modDecl
	 */
	protected void injectComposestarH(ModuleDeclaration modDecl)
	{
		TNode cshStart = TNodeFactory.getInstance().dupTree(composestarHAst);
		TNode cshEnd = cshStart;

		TNode nxtSib = composestarHAst.getNextSibling();
		while (nxtSib != null)
		{
			TNode currentAST = TNodeFactory.getInstance().dupTree(nxtSib);
			cshEnd.addSibling(currentAST);
			cshEnd = currentAST;
			nxtSib = nxtSib.getNextSibling();
		}

		modDecl.getAST().doubleLink();

		// find injection AST
		TNode weaveNode = null;

		for (FunctionDeclaration func : modDecl.getFunctions())
		{
			if (!func.isIncluded())
			{
				// the function's AST points to the "name" part, it should go up
				// 2 levels to get to the declaration part
				weaveNode = func.getAST().getParent().getParent();
				break;
			}
		}

		// weave node points to the first function, this should be the
		// just before the first function

		TNode search = modDecl.getAST();
		while (search != null)
		{
			if (search.getNextSibling() == weaveNode)
			{
				weaveNode = search;
				break;
			}
			search = search.getNextSibling();
		}

		TNode afterNodes = weaveNode.getNextSibling();
		weaveNode.setNextSibling(cshStart);
		cshEnd.addSibling(afterNodes);
	}

	//
	// The following comes from WeaveC's WeaveUnit
	//

	/**
	 * Create a new TNode of the given type with the provided text.
	 */
	protected TNode createTNode(int type, String text, TNode metaInfoFrom)
	{
		TNode node = TNodeFactory.getInstance().create(type, text);
		setMetaInfo(node, RecursionMode.NODE, metaInfoFrom.getLineNum(), metaInfoFrom.getSource(), metaInfoFrom
				.getTokenNumber());
		return node;
	}

	enum RecursionMode
	{
		NODE, TREE, FOREST;
	}

	/**
	 * Sets meta info on the provide node(s). The meta info is important during
	 * the emitting phase.
	 * 
	 * @param node
	 * @param mode
	 * @param lineNum
	 * @param source
	 * @param tokenNumber
	 */
	protected void setMetaInfo(TNode node, RecursionMode mode, int lineNum, String source, int tokenNumber)
	{
		switch (mode)
		{
			case NODE:
				if (lineNum != -1)
				{
					node.setLineNum(lineNum);
				}
				if (source != null)
				{
					node.setSource(source);
				}
				if (tokenNumber != -1)
				{
					node.setTokenNumber(tokenNumber);
				}
				break;
			case TREE:
				setMetaInfo(node, RecursionMode.NODE, lineNum, source, tokenNumber);
				setMetaInfo(node.getFirstChild(), RecursionMode.FOREST, lineNum, source, tokenNumber);
				break;
			case FOREST:
				while (node != null)
				{
					setMetaInfo(node, RecursionMode.TREE, lineNum, source, tokenNumber);
					node = node.getNextSibling();
				}
				break;
		}
	}
}
