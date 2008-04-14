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

package Composestar.CwC.TYM.Collector;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import weavec.ast.TNode;
import weavec.cmodel.declaration.Annotation;
import weavec.cmodel.declaration.AnnotationInstance;
import weavec.cmodel.declaration.AnnotationValue;
import weavec.cmodel.declaration.Declaration;
import weavec.cmodel.declaration.FunctionDeclaration;
import weavec.cmodel.declaration.ModuleDeclaration;
import weavec.cmodel.declaration.ObjectDeclaration;
import weavec.cmodel.declaration.TypeDeclaration;
import weavec.cmodel.scope.AnnotationNamespaceKind;
import weavec.cmodel.scope.CNamespaceKind;
import weavec.cmodel.type.AnnotationType;
import weavec.cmodel.type.CType;
import weavec.cmodel.type.FunctionType;
import weavec.grammar.TranslationUnitResult;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.CwC.LAMA.CwCAnnotation;
import Composestar.CwC.LAMA.CwCAnnotationType;
import Composestar.CwC.LAMA.CwCCallToOtherMethod;
import Composestar.CwC.LAMA.CwCFile;
import Composestar.CwC.LAMA.CwCFunctionInfo;
import Composestar.CwC.LAMA.CwCFunctionInfoCTOMStub;
import Composestar.CwC.LAMA.CwCParameterInfo;
import Composestar.CwC.LAMA.CwCType;
import Composestar.CwC.LAMA.CwCVariable;
import Composestar.CwC.TYM.WeaveCResources;
import Composestar.Utils.Logging.CPSLogger;
import antlr.collections.AST;

/**
 * Creates the LAMA wrapper classes for the WeaveC Language Model
 * 
 * @author Michiel Hendriks
 */
public class LangModelConverter implements CTCommonModule
{
	public static final String MODULE_NAME = "Collector";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	@ResourceManager
	protected WeaveCResources weavecRes;

	protected Map<CType, CwCType> typeMapping;

	protected Map<String, CwCType> stringTypeMapping;

	protected Map<AnnotationType, CwCAnnotationType> annotTypeMapping;

	protected Map<String, CwCAnnotationType> stringAnnotTypeMapping;

	protected Map<FunctionDeclaration, CwCFunctionInfoCTOMStub> ctomStubs;

	protected CTypeToStringConverter converter;

	protected UnitRegister register;

	protected int duplicateTypeCtr;

	// used for resolving CTOM stubs
	protected Set<CwCCallToOtherMethod> ctoms;

	// used for resolving CTOM stubs
	protected Set<CwCFunctionInfo> cfucns;

	public LangModelConverter()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		typeMapping = new HashMap<CType, CwCType>();
		stringTypeMapping = new HashMap<String, CwCType>();
		annotTypeMapping = new HashMap<AnnotationType, CwCAnnotationType>();
		stringAnnotTypeMapping = new HashMap<String, CwCAnnotationType>();
		ctomStubs = new HashMap<FunctionDeclaration, CwCFunctionInfoCTOMStub>();
		ctoms = new HashSet<CwCCallToOtherMethod>();
		cfucns = new HashSet<CwCFunctionInfo>();
		duplicateTypeCtr = 0;

		register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
		if (register == null)
		{
			register = new UnitRegister();
			resources.put(UnitRegister.RESOURCE_KEY, register);
		}
		converter = new CTypeToStringConverter();

		logger.info("Phase 1: Collecting types for caching");
		for (TranslationUnitResult tunit : weavecRes.translationUnitResults())
		{
			Iterator<Declaration> it = tunit.getRootScope().getNamespace(CNamespaceKind.OBJECT).iterator();
			while (it.hasNext())
			{
				Declaration d = it.next();
				if (d instanceof TypeDeclaration)
				{
					createCwCType((TypeDeclaration) d);
				}
			}

			EnumSet<AnnotationNamespaceKind> anks = EnumSet.allOf(AnnotationNamespaceKind.class);
			anks.remove(AnnotationNamespaceKind.ANNOTATION_TAG);
			for (AnnotationNamespaceKind ank : anks)
			{
				Iterator<Declaration> atit = tunit.getAnnotationScope().getNamespace(ank).iterator();
				while (atit.hasNext())
				{
					Declaration d = atit.next();
					if (d instanceof Annotation)
					{
						createCwCAnnotationType((Annotation) d);
					}
				}
			}
		}
		logger.info("Phase 2: Collecting language model");
		for (TranslationUnitResult tunit : weavecRes.translationUnitResults())
		{
			createCwCFile(tunit);
		}

		logger.info("Phase 3: Creating primitive types");
		for (Type type : register.getTypeMap().values())
		{
			Concern pc = null;
			Object o = resources.repository().getObjectByID(type.getFullName());
			if (o instanceof CpsConcern)
			{
				pc = (Concern) o;
				if (pc.getPlatformRepresentation() != null)
				{
					type.setParentConcern(pc);
					logger.error(String.format("CpsConcern %s is already bound to a platform representation", pc
							.getQualifiedName()));
					continue;
				}
			}
			if (pc == null)
			{
				pc = new PrimitiveConcern();
				pc.setName(type.getFullName());
				resources.repository().addObject(type.getFullName(), pc);
			}
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
		}
		logger.info("Phase 4: Resolve call to other methods");
		resolveCTOMs();

		if (duplicateTypeCtr > 0)
		{
			logger.debug(String.format("Detected %d duplicate type declarations", duplicateTypeCtr));
		}
	}

	/**
	 * Find a CwCType for a given CType (create it if missing).
	 * 
	 * @param ctype
	 * @return
	 */
	protected CwCType resolveCwCType(CType ctype)
	{
		CwCType result;
		result = typeMapping.get(ctype);
		if (result == null)
		{
			result = new CwCType(ctype);
			result.setName(converter.convert(ctype));
			result.setFullName(result.getName());
			typeMapping.put(ctype, result);
		}
		return result;
	}

	protected void createCwCType(TypeDeclaration typeDecl)
	{
		if (stringTypeMapping.containsKey(typeDecl.getName()))
		{
			typeMapping.put(typeDecl.getType(), stringTypeMapping.get(typeDecl.getName()));
			logger.trace(String.format("Type %s already contains a registered declaration", typeDecl.getName()));
			duplicateTypeCtr++;
			return;
		}
		logger.debug(String.format("Creating LAMA Type for %s", typeDecl.getName()));
		CwCType cwcType = new CwCType(typeDecl);
		cwcType.setName(converter.convert(typeDecl.getType()));
		cwcType.setFullName(cwcType.getName());

		register.registerLanguageUnit(cwcType);
		stringTypeMapping.put(typeDecl.getName(), cwcType);
		typeMapping.put(typeDecl.getType(), cwcType);
	}

	protected CwCAnnotationType resolveCwCAnnotationType(AnnotationType annotType)
	{
		CwCAnnotationType result;
		result = annotTypeMapping.get(annotType);
		// if (result == null)
		// {
		//
		// result = createCwCAnnotationType(annotType.getDeclaration());
		// if (result != null)
		// {
		// annotTypeMapping.put(annotType, result);
		// }
		//
		// }
		return result;
	}

	protected CwCAnnotationType createCwCAnnotationType(Annotation annot)
	{
		if (stringTypeMapping.containsKey(annot.getName()))
		{
			annotTypeMapping.put(annot.getType(), stringAnnotTypeMapping.get(annot.getName()));
			logger
					.trace(String.format("Annotation Type %s already contains a registered declaration", annot
							.getName()));
			return null;
		}
		logger.debug(String.format("Creating LAMA Annotation Type for %s", annot.getName()));
		CwCAnnotationType cwcType = new CwCAnnotationType(annot);

		register.registerLanguageUnit(cwcType);
		stringAnnotTypeMapping.put(annot.getName(), cwcType);
		annotTypeMapping.put(annot.getType(), cwcType);
		return cwcType;
	}

	protected void createCwCFile(TranslationUnitResult tunit)
	{
		ModuleDeclaration modDecl = tunit.getModuleDeclaration();
		logger.debug(String.format("Collecting file (module) %s", modDecl.getName()));
		File fileName = new File(modDecl.getName());
		String namespace = "";
		if (fileName.getParentFile() != null)
		{
			namespace = fileName.getParentFile().toString().replace(File.separatorChar, '.');
		}

		CwCFile cwcfile = new CwCFile(modDecl);
		cwcfile.setName(fileName.getName());
		cwcfile.setFullName(fileName.toString().replace(File.separatorChar, '.'));
		cwcfile.setNamespace(namespace);
		register.registerLanguageUnit(cwcfile);
		procAnnotations(cwcfile, modDecl);

		for (FunctionDeclaration funcDecl : modDecl.getFunctions())
		{
			if (funcDecl.isIncluded())
			{
				continue;
			}
			logger.debug(String.format("Collecting function %s.%s", cwcfile.getFullName(), funcDecl.getName()));
			FunctionType ftype = (FunctionType) funcDecl.getType();

			CwCFunctionInfo cwcfunc = new CwCFunctionInfo(funcDecl);
			cwcfunc.setName(funcDecl.getName());
			cwcfunc.setReturnType(resolveCwCType(funcDecl.getReturnType()));
			cwcfunc.setReturnType(converter.convert(funcDecl.getReturnType()));
			cwcfunc.setVarArgs(ftype.hasVarArgs());

			cwcfunc.setParent(cwcfile);
			cwcfile.addMethod(cwcfunc);
			register.registerLanguageUnit(cwcfunc);
			cfucns.add(cwcfunc);
			procAnnotations(cwcfunc, funcDecl);

			for (Declaration d : funcDecl.getParameterScope().getNamespace(CNamespaceKind.OBJECT))
			{
				ObjectDeclaration od = (ObjectDeclaration) d;
				CwCParameterInfo cwcparm = new CwCParameterInfo(od);
				cwcparm.setName(d.getName());
				cwcparm.setParameterType(resolveCwCType(od.getType()));
				cwcparm.setParameterType(converter.convert(od.getType()));

				cwcparm.setParent(cwcfunc);
				cwcfunc.addParameter(cwcparm);
				register.registerLanguageUnit(cwcparm);
				procAnnotations(cwcparm, od);
			}

			// this is required for output filters but also for signature
			// extention
			procCallToOtherMethods(tunit, cwcfunc, funcDecl);
		}

		for (ObjectDeclaration objDecl : modDecl.getVariables())
		{
			logger.debug(String.format("Collecting variable %s.%s", cwcfile.getFullName(), objDecl.getName()));

			CwCVariable cwcvar = new CwCVariable(objDecl);
			cwcvar.setName(objDecl.getName());
			cwcvar.setFieldType(resolveCwCType(objDecl.getType()));
			cwcvar.setFieldType(converter.convert(objDecl.getType()));

			cwcvar.setParent(cwcfile);
			cwcfile.addField(cwcvar);
			register.registerLanguageUnit(cwcvar);
			procAnnotations(cwcvar, objDecl);
		}
	}

	protected void procAnnotations(ProgramElement target, Declaration source)
	{
		for (AnnotationInstance annot : source.getAnnotations())
		{
			CwCAnnotation cwcannot = new CwCAnnotation(annot);
			// cwcannot.setStringValue(annot.getAttributeValues().toString());
			for (Entry<String, AnnotationValue> entry : annot.getAttributeValues().entrySet())
			{
				cwcannot.setValue(entry.getKey(), entry.getValue().toString());
			}
			cwcannot.register(resolveCwCAnnotationType(annot.getDeclaration().getType()), target);
		}
	}

	protected void procCallToOtherMethods(TranslationUnitResult tunit, CwCFunctionInfo cwcfunc,
			FunctionDeclaration funcDecl)
	{
		TNode ast = funcDecl.getAST();
		procCTOMAstWalker(tunit, cwcfunc, ast.getNextSibling());
	}

	protected void procCTOMAstWalker(TranslationUnitResult tunit, CwCFunctionInfo cwcfunc, AST ast)
	{
		for (AST sibling = ast; sibling != null; sibling = sibling.getNextSibling())
		{
			if (sibling.getNextSibling() != null
					&& sibling.getNextSibling().getType() == weavec.parser.ACGrammarLexerTokenTypes.NFunctionCallArgs)
			{
				procCTOMAddCall(tunit, cwcfunc, sibling);
			}
			if (sibling.getFirstChild() != null)
			{
				// TODO could be changed to use a queue instead of recursion
				procCTOMAstWalker(tunit, cwcfunc, sibling.getFirstChild());
			}
		}
	}

	protected CwCFunctionInfoCTOMStub getCTOMStub(FunctionDeclaration funcDecl)
	{
		if (ctomStubs.containsKey(funcDecl))
		{
			return ctomStubs.get(funcDecl);
		}
		FunctionType ftype = (FunctionType) funcDecl.getType();
		CwCFunctionInfoCTOMStub calledMethod = new CwCFunctionInfoCTOMStub(funcDecl);
		calledMethod.setName(funcDecl.getName());
		calledMethod.setReturnType(resolveCwCType(funcDecl.getReturnType()));
		calledMethod.setReturnType(converter.convert(funcDecl.getReturnType()));
		calledMethod.setVarArgs(ftype.hasVarArgs());

		if (funcDecl.getParameterScope() != null)
		{
			for (Declaration d : funcDecl.getParameterScope().getNamespace(CNamespaceKind.OBJECT))
			{
				ObjectDeclaration od = (ObjectDeclaration) d;
				CwCParameterInfo cwcparm = new CwCParameterInfo(od);
				cwcparm.setName(d.getName());
				cwcparm.setParameterType(resolveCwCType(od.getType()));
				cwcparm.setParameterType(converter.convert(od.getType()));

				cwcparm.setParent(calledMethod);
				calledMethod.addParameter(cwcparm);
			}
		}
		ctomStubs.put(funcDecl, calledMethod);
		return calledMethod;
	}

	protected void procCTOMAddCall(TranslationUnitResult tunit, CwCFunctionInfo cwcfunc, AST ast)
	{
		logger.debug(String.format("Found call to %s from %s", ast.getText(), cwcfunc.getName()));
		CwCCallToOtherMethod ctom = new CwCCallToOtherMethod();
		ctom.setMethodName(ast.getText());
		ctom.setASTNode(ast);
		Declaration decl = tunit.getRootScope().get(CNamespaceKind.OBJECT, ast.getText());
		if (decl instanceof FunctionDeclaration)
		{
			ctom.setCalledMethod(getCTOMStub((FunctionDeclaration) decl));
		}
		cwcfunc.getCallsToOtherMethods().add(ctom);
		ctoms.add(ctom);
	}

	protected void resolveCTOMs()
	{
		Map<CwCFunctionInfoCTOMStub, CwCFunctionInfo> lookup = new HashMap<CwCFunctionInfoCTOMStub, CwCFunctionInfo>();
		for (CwCCallToOtherMethod ctom : ctoms)
		{
			if (!(ctom.getCalledMethod() instanceof CwCFunctionInfoCTOMStub))
			{
				continue;
			}
			if (lookup.containsKey(ctom.getCalledMethod()))
			{
				ctom.setCalledMethod(lookup.get(ctom.getCalledMethod()));
				continue;
			}
			CwCFunctionInfo func = null;
			CwCFunctionInfoCTOMStub stub = (CwCFunctionInfoCTOMStub) ctom.getCalledMethod();
			for (CwCFunctionInfo tf : cfucns)
			{
				if (stub.checkEquals(tf))
				{
					func = tf;
					break;
				}
			}
			if (func != null)
			{
				lookup.put(stub, func);
				ctom.setCalledMethod(func);
			}
			else
			{
				logger.debug("Unable to find actual declaration of method " + stub.getName());
			}
		}
	}
}
