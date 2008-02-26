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

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import weavec.ast.TNode;
import weavec.ast.TNodeFactory;
import weavec.cmodel.declaration.Declaration;
import weavec.cmodel.declaration.FunctionDeclaration;
import weavec.cmodel.scope.CNamespaceKind;
import weavec.emitter.CEmitter;
import weavec.grammar.TranslationUnitResult;
import weavec.parser.ACGrammarTokenTypes;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.CwC.INLINE.CodeGen.AbstractHeaderFileGenerator;
import Composestar.CwC.LAMA.CwCFunctionInfo;
import antlr.RecognitionException;

/**
 * Implementation of the AbstractHeaderFileGenerator that relies on the WevaeC
 * data
 * 
 * @author Michiel Hendriks
 */
public class HeaderFileGenerator extends AbstractHeaderFileGenerator
{
	protected TranslationUnitResult tunit;

	protected CEmitter emitter;

	public HeaderFileGenerator(TranslationUnitResult unit)
	{
		super();
		tunit = unit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.INLINE.CodeGen.AbstractHeaderFileGenerator#addMethod(Composestar.Core.LAMA.MethodInfo)
	 */
	@Override
	public void addMethod(MethodInfo mi)
	{
		if (!(mi instanceof CwCFunctionInfo))
		{
			return;
		}
		Collection<Declaration> decls = tunit.getRootScope().getAll(CNamespaceKind.OBJECT, mi.getName());
		for (Declaration decl : decls)
		{
			if (decl instanceof FunctionDeclaration)
			{
				if (((FunctionDeclaration) decl).getBodyScope() == null)
				{
					// function has been defined (hopefully before it is used)
					return;
				}
			}
		}
		// function has not be defined (could be implemented, but not defined)
		super.addMethod(mi);

	}

	@Override
	public boolean generateHeader(FileWriter target)
	{
		StringWriter sw = new StringWriter();
		emitter = new CEmitter(sw);
		emitter.setASTNodeClass(TNode.class.getName());
		for (MethodInfo mi : methods)
		{
			if (!createMethodDeclaration(mi))
			{
				return false;
			}
		}
		try
		{
			target.write(sw.getBuffer().toString());
			target.flush();
			target.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	protected boolean createMethodDeclaration(MethodInfo mi)
	{
		if (!(mi instanceof CwCFunctionInfo))
		{
			return true;
		}
		CwCFunctionInfo cwcfunc = (CwCFunctionInfo) mi;
		TNode nd = TNodeFactory.getInstance().dupTree(cwcfunc.getFunctionDeclaration().getBaseTypeAST());
		try
		{
			emitter.declarationSpecifiers(nd);
		}
		catch (RecognitionException e)
		{
			return false;
		}
		nd = TNodeFactory.getInstance().dupTree(cwcfunc.getFunctionDeclaration().getBaseTypeAST().getNextSibling());
		try
		{
			nd.doubleLink();
			// method name
			nd.getFirstChild().getFirstChild().setText(mi.getName());

			// remove implementation
			TNode body = nd.getFirstChild().getLastSibling();
			body.removeSelf();

			body = TNodeFactory.getInstance().create(ACGrammarTokenTypes.NInitDecl, "");
			body.setLineNum(nd.getFirstChild().getLineNum());
			body.setSource(nd.getFirstChild().getSource());
			nd.addChild(body);

			body = TNodeFactory.getInstance().create(ACGrammarTokenTypes.SEMI, ";");
			body.setLineNum(nd.getFirstChild().getLineNum());
			body.setSource(nd.getFirstChild().getSource());
			nd.addChild(body);
			// emit
			emitter.declaratorInitList(nd);
		}
		catch (RecognitionException e)
		{
			return false;
		}
		return true;
	}
}
