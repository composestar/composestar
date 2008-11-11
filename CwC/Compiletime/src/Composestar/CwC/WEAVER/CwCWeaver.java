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
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;

import weavec.ast.LineObject;
import weavec.ast.PreprocDirective;
import weavec.ast.PreprocessorInfoChannel;
import weavec.ast.TNode;
import weavec.ast.TNodeFactory;
import weavec.cmodel.declaration.FunctionDeclaration;
import weavec.cmodel.declaration.ModuleDeclaration;
import weavec.cmodel.declaration.StorageClass;
import weavec.cmodel.scope.AnnotationScope;
import weavec.cmodel.scope.BlockScope;
import weavec.cmodel.scope.CNamespaceKind;
import weavec.cmodel.scope.LabelScope;
import weavec.cmodel.util.AnnotationScopeImpl;
import weavec.cmodel.util.CDeclarations;
import weavec.cmodel.util.ScopeConstructor;
import weavec.emitter.CEmitter;
import weavec.grammar.TranslationUnitResult;
import weavec.parser.ACGrammarTokenTypes;
import weavec.parser.AspectCLexer;
import weavec.parser.AspectCParser;
import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Config.Project;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INLINE.CodeGen.AdviceActionCodeGen;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Signatures.MethodRelation;
import Composestar.Core.LAMA.Signatures.Signature;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.CwC.Filters.FilterLoader;
import Composestar.CwC.INLINE.CodeGen.CCodeGenerator;
import Composestar.CwC.INLINE.CodeGen.CDispatchActionCodeGen;
import Composestar.CwC.LAMA.CwCCallToOtherMethod;
import Composestar.CwC.LAMA.CwCFile;
import Composestar.CwC.LAMA.CwCFunctionInfo;
import Composestar.CwC.TYM.WeaveCResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Logging.OutputStreamRedirector;
import Composestar.Utils.Perf.CPSTimer;
import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * CwC Weaver. Weaves the filtercode (from INLINE) into the correct methods. It
 * does not actually use the WeaveC weaver engine, but in employs the same
 * methods (i.e. modifying the Antlr AST)
 * 
 * @author Michiel Hendriks
 */
@ComposestarModule(ID = ModuleNames.WEAVER, dependsOn = { ModuleNames.INLINE })
public class CwCWeaver implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.WEAVER);

	@ResourceManager
	protected WeaveCResources weavecResc;

	@ResourceManager
	protected InlinerResources inlinerRes;

	protected CommonResources resources;

	protected CCodeGenerator codeGen;

	protected Project currentProject;

	protected File outputDir;

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

	protected Set<String> extraDeps;

	protected CPSTimer timer;

	/**
	 * Used to look up unknown methods
	 * 
	 * @see {@link CwCWeaver#findMethodByName(String)}
	 */
	protected Map<String, MethodInfo> methodLookup;

	/**
	 * If true, remove preprocessor directives from &gt;built-in&lt;
	 */
	@ModuleSetting(ID = "undef", isAdvanced = true)
	protected boolean ppRemoveBuiltins = true;

	/**
	 * If true, remove preprocessor directives from <command line>
	 */
	@ModuleSetting(ID = "undef-cmdline", isAdvanced = true)
	protected boolean ppRemoveCommandline = false;

	public CwCWeaver()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resc) throws ModuleException
	{
		timer = CPSTimer.getTimer(ModuleNames.WEAVER);
		resources = resc;

		codeGen = new CCodeGenerator();
		codeGen.register(new CDispatchActionCodeGen(inlinerRes));
		// codeGen.register(new CErrorActionCodeGen(inlinerRes));
		codeGen.register(new AdviceActionCodeGen(inlinerRes));

		FilterLoader filterLoader = resources.get(FilterLoader.RESOURCE_KEY);
		if (filterLoader != null)
		{
			for (FilterActionCodeGenerator<String> facg : filterLoader.getCodeGenerators())
			{
				facg.setInlinerResources(inlinerRes);
				codeGen.register(facg);
			}
		}

		// TODO: do something with the additional dependencies
		extraDeps = new HashSet<String>();

		currentProject = resources.configuration().getProject();
		outputDir = new File(currentProject.getIntermediate(), "woven");
		if (!outputDir.isDirectory() && !outputDir.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create target directory for preprocessing: %s",
					outputDir.toString()), ModuleNames.WEAVER);
		}

		cshFile = FileUtils.relocateFile(currentProject.getBase(), new File("ComposeStar.h"), outputDir);
		loadComposeStarH();
		copyComposeStarC(FileUtils.relocateFile(currentProject.getBase(), new File("ComposeStar.c"), outputDir));

		// inject filter code
		for (Concern concern : resources.repository().getAll(Concern.class))
		{
			processConcern(concern);
		}

		// emit updated C files
		for (TranslationUnitResult tunit : weavecResc.translationUnitResults())
		{
			File target = FileUtils.relocateFile(currentProject.getBase(), weavecResc.getSource(tunit).getFile(),
					outputDir);
			if (!target.getParentFile().exists() && !target.getParentFile().mkdirs())
			{
				throw new ModuleException(String.format("Unable to create parent directories for: %s", target
						.toString()), ModuleNames.WEAVER);
			}
			FileWriter output;
			try
			{
				timer.start("Emitting file " + target.toString());
				output = new FileWriter(target);
				PreprocessorInfoChannel ppic = weavecResc.getPreprocessorInfoChannel(tunit);

				if (ppRemoveBuiltins || ppRemoveCommandline)
				{
					cleanPreprocessorInfoChannel(ppic);
				}

				CEmitter emitter = new CEmitter(ppic, output);
				emitter.setASTNodeClass(TNode.class.getName());
				emitter.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));
				// emitter.tracings = System.err;
				emitter.translationUnit(tunit.getModuleDeclaration().getAST());
				output.close();
				timer.stop();
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
		// TODO return error when weaving failed somewhere
		return ModuleReturnValue.OK;
	}

	protected void cleanPreprocessorInfoChannel(PreprocessorInfoChannel ppic)
	{
		boolean removeItems = false;
		for (List<Object> objs : ppic.objects.values())
		{
			Iterator<Object> oit = objs.iterator();
			while (oit.hasNext())
			{
				Object o = oit.next();
				if (o instanceof LineObject)
				{
					LineObject lo = (LineObject) o;
					if (ppRemoveBuiltins && lo.getSource().equals("<built-in>"))
					{
						removeItems = true;
						oit.remove();
						continue;
					}
					if (ppRemoveCommandline && lo.getSource().equals("<command line>"))
					{
						oit.remove();
						removeItems = true;
						continue;
					}
					removeItems = false;
				}
				if (removeItems)
				{
					oit.remove();
				}
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
		// re-get the stream
		InputStream cshstream = getComposeStarFile(true);
		timer.start("Parsing ComposeStar.h");

		AspectCLexer lexer = new AspectCLexer(cshstream);
		lexer.setSource(cshFile.getName());
		lexer.newPreprocessorInfoChannel();
		lexer.setTokenNumber(weavecResc.getTokenNumber());
		lexer.yybegin(AspectCLexer.C);
		AspectCParser cparser = new AspectCParser(lexer);
		cparser.setASTNodeClass(TNode.class.getName());
		cparser.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));
		cparser.setSource(cshFile.getName());
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
		timer.stop();

		logger.info("Copying ComposeStar.h to output directory");
		// re-get the stream
		cshstream = getComposeStarFile(true);

		// save a copy in the "woven" directory, which will be used in the
		// #include directive
		if (!cshFile.getParentFile().exists() && !cshFile.getParentFile().mkdirs())
		{
			throw new ModuleException(String.format("Unable to create parent directories for: %s", cshFile.toString()),
					ModuleNames.WEAVER);
		}

		if (cshFile.exists())
		{
			if (!cshFile.delete())
			{
				logger.error(String.format("Unable to delete the file \"%s\". Existing file might be outdated.",
						cshFile.toString()));
				return;
			}
		}
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(cshFile);
		}
		catch (FileNotFoundException e1)
		{
			throw new ModuleException(e1.toString(), ModuleNames.WEAVER, e1);
		}
		try
		{
			FileUtils.copy(cshstream, fos);
		}
		catch (IOException e1)
		{
			throw new ModuleException(e1.toString(), ModuleNames.WEAVER, e1);
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
					ModuleNames.WEAVER);
		}

		if (dest.exists())
		{
			if (!dest.delete())
			{
				logger.error(String.format("Unable to delete the file \"%s\". Existing file might be outdated.", dest
						.toString()));
				return;
			}
		}
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(dest);
		}
		catch (FileNotFoundException e1)
		{
			throw new ModuleException(e1.toString(), ModuleNames.WEAVER, e1);
		}
		try
		{
			FileUtils.copy(cshstream, fos);
		}
		catch (IOException e1)
		{
			throw new ModuleException(e1.toString(), ModuleNames.WEAVER, e1);
		}
	}

	protected void processConcern(Concern concern)
	{
		if (concern.getTypeReference() == null)
		{
			return;
		}
		if (!(concern.getTypeReference().getReference() instanceof CwCFile))
		{
			return;
		}

		if (concern.getSuperimposed() == null)
		{
			return;
		}

		logger.info(String.format("Weaving concern %s", concern.getFullyQualifiedName()));
		timer.start("Weaving concern " + concern.getName());

		CwCFile type = (CwCFile) concern.getTypeReference().getReference();

		TranslationUnitResult tunit = null;
		for (TranslationUnitResult ttu : weavecResc.translationUnitResults())
		{
			if (ttu.getModuleDeclaration() == type.getModuleDeclaration())
			{
				tunit = ttu;
				break;
			}
		}
		if (tunit == null)
		{
			logger.error(String.format("Concern '%s' does not have a translation unit associated with it.", concern
					.getFullyQualifiedName()));
			return;
		}

		boolean containsFilterCode = false;

		Set<String> imports = new HashSet<String>();
		HeaderFileGenerator extraFuncDecls = new HeaderFileGenerator(tunit);
		codeGen.setHeaderGenerator(extraFuncDecls);

		Signature sig = type.getSignature();
		for (MethodInfo method : sig.getMethods(MethodRelation.NORMAL))
		{
			CwCFunctionInfo func = (CwCFunctionInfo) method;
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
				logger.error(String.format("Unable to find the method %s.%s(%s)", concern.getFullyQualifiedName(), func
						.getName(), Arrays.toString(params)));
				continue;
			}
			FilterCode filterCode = inlinerRes.getInputFilterCode(func);
			if (filterCode != null)
			{
				logger.info(String.format("Weaving function %s.%s", concern.getFullyQualifiedName(), func.getName()),
						realFunc);
				containsFilterCode = true;
				processFilterCode(realFunc, filterCode, imports);
			}

			for (CallToOtherMethod ctom : realFunc.getCallsToOtherMethods())
			{
				// check if this method was added, in which case the call to
				// other method is not set
				MethodInfo mi = ctom.getCalledMethod();
				if (mi == null)
				{
					mi = findMethodByName(ctom.getMethodName());
					if (mi == null)
					{
						logger.warn(String.format("Call to unknown method %s", ctom.getMethodName()), realFunc);
					}
					else
					{
						ctom.setCalledMethod(mi);
						extraFuncDecls.addMethod(mi);
					}
				}

				filterCode = inlinerRes.getOutputFilterCode(ctom);
				if (filterCode != null)
				{
					logger.info(String.format("Weaving call to function %s from %s.%s", ctom.getMethodName(), concern
							.getFullyQualifiedName(), func.getName()), realFunc);
					containsFilterCode = true;
					processOutputFilterCode((CwCCallToOtherMethod) ctom, func, filterCode, imports);
				}
			}
		}

		for (MethodInfo method : sig.getMethods(MethodRelation.ADDED))
		{
			CwCFunctionInfo func = (CwCFunctionInfo) method;
			createAddedFunctionAST(func, type);
			FilterCode filterCode = inlinerRes.getInputFilterCode(func);
			if (filterCode != null)
			{
				logger.info(String.format("Weaving function %s.%s", concern.getFullyQualifiedName(), func.getName()));
				containsFilterCode = true;
				processFilterCode(func, filterCode, imports);
			}
		}

		if (containsFilterCode)
		{
			imports.add("\"" + cshFile.toString() + "\"");
			// injectComposestarHInclude(type.getModuleDeclaration());
		}
		if (extraFuncDecls.hasMethods())
		{
			File extraH = new File(outputDir, String.format("cstarxt__%s.h", type.getFullName()));
			try
			{
				extraFuncDecls.generateHeader(new FileWriter(extraH, false));
				imports.add("\"" + extraH.toString() + "\"");
			}
			catch (IOException e)
			{
				logger.error(e, e);
			}
		}
		if (imports.size() > 0)
		{
			injectIncludeDirectives(tunit, imports);
		}

		timer.stop();
	}

	/**
	 * Find a method by name. It will only try to find added methods. Existing
	 * methods should already be properly linked in the CallToOtherMethod
	 * 
	 * @param name
	 * @return
	 */
	protected MethodInfo findMethodByName(String name)
	{
		logger.debug(String.format("Looking up function by name %s", name));
		if (methodLookup == null)
		{
			methodLookup = new HashMap<String, MethodInfo>();
			for (Concern concern : resources.repository().getAll(Concern.class))
			{
				if (concern.getTypeReference() == null)
				{
					continue;
				}
				if (concern.getTypeReference().getReference() == null)
				{
					continue;
				}
				Signature sign = concern.getTypeReference().getReference().getSignature();
				if (sign != null)
				{
					for (MethodInfo mi : (Collection<MethodInfo>) sign.getMethods(MethodRelation.ADDED))
					{
						methodLookup.put(mi.getName(), mi);
					}
				}
			}
		}
		return methodLookup.get(name);
	}

	/**
	 * 
	 */
	protected TNode generateCodeAST(String stringcode, CwCFunctionInfo func, Set<String> imports)
	{
		// generate ANSI-C code

		// Note: stringcode is used for error reporting when there's an
		// exception
		if (logger.isTraceEnabled())
		{
			logger.trace(stringcode);
		}
		Reader ccode = new StringReader(stringcode);

		Set<String> limp = codeGen.getDependencies();
		if (limp != null)
		{
			extraDeps.addAll(limp);
			limp = null;
		}

		limp = codeGen.getImports();
		if (limp != null)
		{
			imports.addAll(limp);
			// limp = null;
		}

		// parse ANSI-C code to AST
		// parser.compoundStatement(...)
		// -> must be: { ... }

		AspectCLexer lexer = new AspectCLexer(ccode);
		lexer.yybegin(AspectCLexer.C);

		AspectCParser cparser = new AspectCParser(lexer);
		cparser.setASTNodeClass(TNode.class.getName());
		cparser.errors = new PrintStream(new OutputStreamRedirector(logger, Level.ERROR));

		try
		{
			LabelScope labelScope = ScopeConstructor.getLabelScope();
			AnnotationScope annotationScope = new AnnotationScopeImpl();
			cparser.compoundStatement(ScopeConstructor.getScope(new CompositeScope((BlockScope) func
					.getFunctionDeclaration().getScope(), rootScope)), labelScope, annotationScope);
			// getAST should return the returnAst, which contains the result of
			// the previous method execution
			return (TNode) cparser.getAST();
		}
		catch (RecognitionException e)
		{
			logger.debug(stringcode);
			logger.error(new LogMessage(e.getMessage(), "", e.getLine(), e.getColumn()), e);
			return null;
		}
		catch (TokenStreamException e)
		{
			logger.error(e, e);
			return null;
		}
	}

	/**
	 * Inject the filter code at the beginning of the method implementation.
	 * 
	 * @param rootScope
	 * @param func
	 * @param fc
	 */
	protected void processFilterCode(CwCFunctionInfo func, FilterCode fc, Set<String> imports)
	{
		TNode fcAst = generateCodeAST(codeGen.generate(fc, func, inlinerRes.getMethodId(func)), func, imports);
		if (fcAst == null)
		{
			return;
		}

		// inject AST into function node
		TNode functionAST = func.getFunctionDeclaration().getAST();
		functionAST.doubleLink();
		TNode bodyAST = functionAST.getLastSibling();
		TNode newBodyAST = createTNode(ACGrammarTokenTypes.NCompoundStatement, "{", bodyAST);
		bodyAST.doubleLink();
		bodyAST.removeSelf();
		setMetaInfo(fcAst, RecursionMode.FOREST, -1, "<Composestar/CwC/INLINE#" + func.parent().getFullName() + "."
				+ func.getName() + ">", bodyAST.getTokenNumber());
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
	protected void injectIncludeDirectives(TranslationUnitResult tunit, Set<String> incfiles)
	{
		ModuleDeclaration modDecl = tunit.getModuleDeclaration();
		// find injection AST
		TNode weaveNode = null;

		for (FunctionDeclaration func : modDecl.getFunctions())
		{
			if (!func.isIncluded())
			{
				// the function's AST points to the "name" part, it should go up
				// 2 levels to get to the declaration part
				weaveNode = func.getBaseTypeAST();// .getParent().getParent();
				if (weaveNode == null)
				{
					weaveNode = func.getAST();
				}
				break;
			}
		}

		if (weaveNode == null)
		{
			// no functions -> no inlining possible
			logger.error(String.format("No location to inject include directives found, not functions in this file"));
			return;
		}

		while (weaveNode.getTokenNumber() == -1)
		{
			weaveNode = weaveNode.getFirstChild();
		}

		PreprocessorInfoChannel ppic = weavecResc.getPreprocessorInfoChannel(tunit);
		for (String incfile : incfiles)
		{
			if (!incfile.startsWith("\"") && !incfile.startsWith("<"))
			{
				logger.error(String.format("Invalid include file, must start with \" or <: %s", incfile));
				continue;
			}
			PreprocDirective incdirective = new PreprocDirective(String.format("#include %s", incfile), 0);
			ppic.addLineForTokenNumber(incdirective, weaveNode.getTokenNumber() - 1);
		}
		return;
	}

	/**
	 * @param ctom call to a given method
	 * @param func called from within this method
	 * @param fc the generated filter code
	 * @param imports
	 */
	protected void processOutputFilterCode(CwCCallToOtherMethod ctom, CwCFunctionInfo func, FilterCode fc,
			Set<String> imports)
	{
		String code = codeGen.generate(fc, ctom, inlinerRes.getMethodId(ctom.getCalledMethod()), func, inlinerRes
				.getMethodId(func));
		logger.debug(code);
		TNode fcAst = generateCodeAST(code, func, imports);
		if (fcAst == null)
		{
			return;
		}
	}

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
			default:
		}
	}

	/**
	 * Creates the WeaveC data structure for this added function.
	 * 
	 * @param func
	 */
	protected void createAddedFunctionAST(CwCFunctionInfo func, CwCFile type)
	{
		TNode fast = TNodeFactory.getInstance().dupTree(func.getFunctionDeclaration().getAST());
		TNode bast = TNodeFactory.getInstance().dupTree(func.getFunctionDeclaration().getBaseTypeAST());

		fast.getFirstChild().setText(func.getName());
		TNode bodyAST = TNodeFactory.getInstance().create(ACGrammarTokenTypes.NCompoundStatement, "{");
		if (codeGen.hasReturnValue(func))
		{
			// yes I know this is extremely dirty, but it works
			TNode returnVal = TNodeFactory.getInstance().create(ACGrammarTokenTypes.LITERAL_return,
					String.format("%s retval; return retval", func.getReturnTypeString()));
			returnVal.addChild(TNodeFactory.getInstance().create(ACGrammarTokenTypes.SEMI, ";"));
			bodyAST.addChild(returnVal);
		}
		bodyAST.addChild(TNodeFactory.getInstance().create(ACGrammarTokenTypes.RCURLY, "}"));

		TNode fdecl = TNodeFactory.getInstance().create(ACGrammarTokenTypes.NDeclaration);
		fdecl.addChild(fast);
		fdecl.addChild(bodyAST);

		TNode nfast = TNodeFactory.getInstance().create(ACGrammarTokenTypes.NTypedDeclaration);
		nfast.addChild(bast);
		nfast.addChild(fdecl);
		nfast.doubleLink();

		setMetaInfo(nfast, RecursionMode.FOREST, 1, String.format("<Composestar/CwC/SIGN/Added#%s.%s>", func.parent()
				.getFullName(), func.getName()), -1);

		FunctionDeclaration olddecl = func.getFunctionDeclaration();
		FunctionDeclaration newdecl = (FunctionDeclaration) CDeclarations.getFunctionOrTypedef(func.getName(), olddecl
				.getType(), (EnumSet<StorageClass>) olddecl.getStorageClasses(), false);
		newdecl.setAST(fast);
		newdecl.setBaseTypeAST(bast);
		func.setFunctionDeclaration(newdecl);
		((BlockScope) olddecl.getScope()).addDeclaration(CNamespaceKind.OBJECT, newdecl);
		type.getModuleDeclaration().getAST().getLastSibling().addSibling(nfast);
	}
}
