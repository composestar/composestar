/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 * 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: WrappedAST.java,v 1.1 2006/09/04 08:12:15 johantewinkel Exp $
 */
package Composestar.C.wrapper.parsing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import Composestar.C.wrapper.ExitPoint;
import Composestar.C.wrapper.Function;
import Composestar.C.wrapper.GlobalIntroductionPoint;
import Composestar.C.wrapper.HeaderIntroductionPoint;
import Composestar.C.wrapper.Struct;
import Composestar.C.wrapper.utils.StaticVariableReplacer;
import Composestar.C.wrapper.utils.WrappingUtil;
import antlr.RecognitionException;

/**
 * Created by IntelliJ IDEA. User: ByelasH Date: 20-dec-2004 Time: 11:17:45 To
 * change this template use File | Settings | File Templates.
 */
public class WrappedAST
{

	private static final int FOR_REMOVAL = -999;

	private static final int BEFORE = 0;

	private static final int AFTER = 1;

	private GnuCEmitter emitter = null;

	private TNode node = null;

	private PreprocessorInfoChannel infoChannel = null;

	public GlobalIntroductionPoint introductionPoint = null;

	public HeaderIntroductionPoint headerintroductionPoint = null;

	private Vector allNodes = new Vector();

	private Vector functions = new Vector();

	private Vector commentKeepers = new Vector();

	private String filename = null;

	private WrappingUtil wrappingUtil = new WrappingUtil();

	public WrappedAST(TNode node, PreprocessorInfoChannel infoChannel, Vector allNodes, Vector functions,
			Vector commentKeepers, GlobalIntroductionPoint intropoint, HeaderIntroductionPoint headerpoint)
	{
		this.node = node;
		this.introductionPoint = intropoint;
		this.headerintroductionPoint = headerpoint;

		this.infoChannel = infoChannel;
		this.allNodes = allNodes;
		this.functions = functions;
		this.commentKeepers = commentKeepers;
		emitter = new GnuCEmitter(infoChannel);
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	private void fillVector(TNode node, Vector vec)
	{
		vec.addElement(node);
		if (node.getNumberOfChildren() > 0)
		{
			fillVector((TNode) node.getFirstChild(), vec);
		}
		if (node.getNextSibling() != null)
		{
			fillVector((TNode) node.getNextSibling(), vec);
		}
	}

	// do not consider node siblings
	private void fillVectorDeep(TNode node, Vector vec)
	{
		vec.addElement(node);
		if (node.getNumberOfChildren() > 0)
		{
			fillVectorD((TNode) node.getFirstChild(), vec);
		}
	}

	// suplementary method for fillVectorDeep
	private void fillVectorD(TNode node, Vector vec)
	{
		vec.addElement(node);
		if (node.getNumberOfChildren() > 0)
		{
			fillVectorD((TNode) node.getFirstChild(), vec);
		}
		if (node.getNextSibling() != null)
		{
			fillVectorD((TNode) node.getNextSibling(), vec);
		}
	}

	private void fillVectorDeepSpecial(TNode node, Vector vec, int type)
	{
		if (node.getType() == type)
		{
			vec.addElement(node);
		}
		if (node.getNumberOfChildren() > 0)
		{
			fillVectorDSpecial((TNode) node.getFirstChild(), vec, type);
		}
	}

	private void fillVectorDSpecial(TNode node, Vector vec, int type)
	{
		if (node.getType() == type)
		{
			vec.addElement(node);
		}

		if (node.getNumberOfChildren() > 0)
		{
			fillVectorDSpecial((TNode) node.getFirstChild(), vec, type);
		}
		if (node.getNextSibling() != null)
		{
			fillVectorDSpecial((TNode) node.getNextSibling(), vec, type);
		}
	}

	public void printAllNodes()
	{
		System.out.println("--------------------------------------");
		for (int i = 0; i < allNodes.size(); i++)
		{
			TNode node = (TNode) allNodes.elementAt(i);
			System.out.print(" > " + node.getText());
			System.out.print("\t  type: " + GnuCParser._tokenNames[node.getType()]);
			// System.out.print("\t # : " + node.getTokenNumber());
			// System.out.print("\t line: " + node.getLineNum());
			// System.out.print("\t #chi: " + node.getNumberOfChildren());
			// System.out.print("\t scope: " + node.getScope());
			// if(node.isSource()) System.out.print("\t is Source: ");
			System.out.println("");
		}
		System.out.println("--------------------------------------");
	}

	public void emite(String filename) throws FileNotFoundException, RecognitionException
	{
		smartIncreaseNumberInfoChannel();
		emitter.setAdvancedOutput(filename);
		emitter.setASTNodeClass(Composestar.C.wrapper.parsing.TNode.class.getName());
		emitter.translationUnit(node);
	}

	public void emite() throws RecognitionException
	{
		smartIncreaseNumberInfoChannel();
		emitter.setCurrentOutputToScreen();
		emitter.translationUnit(node);
	}

	public void emiteToFile(String filename) throws FileNotFoundException, RecognitionException
	{
		smartIncreaseNumberInfoChannel();
		emitter.setCurrentOutputToFile(filename);
		emitter.translationUnit(node);
	}

	public void printTreeElements()
	{
		System.out.print(" > " + node.getText() + "\t  type: " + node.getType() + "  line: " + node.getLineNum()
				+ "  children# " + node.getNumberOfChildren());

		Enumeration keys = (node).getAttributesTable().keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			if (key.equalsIgnoreCase("tokenNumber"))
			{
				System.out.print(" " + key + ":" + (node).getAttribute(key));
			}
			if (key.equalsIgnoreCase("source"))
			{
				System.out.print("  --isSource");
			}
			if (key.equalsIgnoreCase("scopeName"))
			{
				System.out.print("   scope :" + (node).getAttribute(key));
			}
		}
		System.out.println("");

		if (node.getNumberOfChildren() > 0)
		{
			TNode firstChild = (TNode) node.getFirstChild();
			printNextElements(firstChild);
		}
		if (node.getNextSibling() != null)
		{
			TNode firstSibling = (TNode) node.getNextSibling();
			printNextElements(firstSibling);
		}
	}

	private void printNextElements(TNode node)
	{
		System.out.print(" > " + node.getText() + "\t  type: " + node.getType() + "  line: " + node.getLineNum()
				+ "  children# " + node.getNumberOfChildren());

		Enumeration keys = (node).getAttributesTable().keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			if (key.equalsIgnoreCase("tokenNumber"))
			{
				System.out.print(" " + key + ":" + (node).getAttribute(key));
			}
			if (key.equalsIgnoreCase("source"))
			{
				System.out.print("  --isSource");
			}
			if (key.equalsIgnoreCase("scopeName"))
			{
				System.out.print("   scope :" + (node).getAttribute(key));
			}
		}
		System.out.println("");

		if (node.getNumberOfChildren() > 0)
		{
			TNode firstChild = (TNode) node.getFirstChild();
			printNextElements(firstChild);
		}
		if (node.getNextSibling() != null)
		{
			TNode firstSibling = (TNode) node.getNextSibling();
			printNextElements(firstSibling);
		}
	}

	public void printTree()
	{
		TNode.printTree(node);
	}

	public void printNodeDeep(TNode n)
	{
		TNode.printTree(n);
	}

	public void printPreprocessorInfo()
	{
		System.out.println(" max token number: " + infoChannel.getMaxTokenNumber());
		Hashtable lineLists = infoChannel.getHashtableLine();
		for (int i = 0; i <= infoChannel.getMaxTokenNumber(); i++)
		{
			Integer inti = i;

			if (lineLists.containsKey(inti))
			{
				System.out.print(" # " + inti + " : ");
				Vector tokenLineVector = (Vector) lineLists.get(inti);
				if (tokenLineVector != null)
				{
					Enumeration tokenLines = tokenLineVector.elements();
					while (tokenLines.hasMoreElements())
					{
						System.out.println("  " + inti + " " + tokenLines.nextElement());
					}
				}
			}
			// else
			// System.out.println(" null ");
		}
		System.out.println("");
	}

	public void printXML() throws IOException
	{
		System.out.println("XML ---------------------");
		Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
		node.xmlSerialize(out);
		out.flush();
		System.out.println("/end XML ----------------");
	}

	private void insertAsFirstChild(TNode bNode, TNode iNode)
	{
		int tokenNumber = bNode.getTokenNumber();
		int lineNumber = bNode.getLineNum();

		Vector vecINode = new Vector();
		fillVector(iNode, vecINode);
		int insertionNumber = getMaxTokenNumber(vecINode);

		// System.out.println(" numbers: " + vecINode.size() + " & " +
		// insertionNumber);

		increaseNumberNewNode(vecINode, tokenNumber, lineNumber + 1);
		// todo here 1 - is a increment for lines - change later
		increaseNumberMainNode(insertionNumber, tokenNumber, 1);

		// increaseNumberInfoChannel(insertionNumber, tokenNumber);

		TNode firstChild = (TNode) bNode.getFirstChild();

		bNode.setFirstChild(iNode);
		iNode.setParent(bNode);

		if (bNode.getNumberOfChildren() > 0)
		{
			iNode.setNextSibling(firstChild);
			firstChild.setPreviousNode(iNode);
			firstChild.setParent(null);
		}
		updateVector(vecINode);
	}

	private int getMaxTokenNumber(Vector vecINode)
	{
		int result = -1;
		for (int i = 0; i < vecINode.size(); i++)
		{
			TNode node = (TNode) vecINode.elementAt(i);
			Integer n = (Integer) node.getAttribute(TNode.TOKEN_NUMBER);
			if (n != null)
			{
				int number = n;
				if (number > result)
				{
					result = number;
				}
			}
		}
		return result;
	}

	private void updateVector(Vector vecINode)
	{
		for (int i = 0; i < vecINode.size(); i++)
		{
			TNode tnode = (TNode) vecINode.elementAt(i);
			allNodes.addElement(tnode);
		}
	}

	private void updateVector(TNode nnn)
	{
		Vector vec = new Vector();
		fillVector(nnn, vec);

		for (int i = 0; i < vec.size(); i++)
		{
			TNode tnode = (TNode) vec.elementAt(i);
			allNodes.addElement(tnode);
		}
	}

	private void updateVectorDeep(TNode nnn)
	{
		Vector vec = new Vector();
		fillVectorDeep(nnn, vec);

		for (int i = 0; i < vec.size(); i++)
		{
			TNode tnode = (TNode) vec.elementAt(i);
			allNodes.addElement(tnode);
		}
	}

	private void increaseNumberNewNode(Vector vecINode, int tokenNumber, int lineNumber)
	{
		for (int i = 0; i < vecINode.size(); i++)
		{
			TNode tNode = (TNode) vecINode.elementAt(i);
			// if(tNode.getLineNum()> 0)
			tNode.setLineNum(lineNumber);
			int oldNumber = tNode.getTokenNumber();
			if (oldNumber != -1)
			{
				tNode.setTokenNumber(oldNumber + tokenNumber);
			}
		}
		// printElements(vecINode);
	}

	private void increaseNumberNewNode(TNode iNode, int tokenNumber, int lineNumber)
	{
		Vector vecINode = new Vector();
		fillVector(iNode, vecINode);

		for (int i = 0; i < vecINode.size(); i++)
		{
			TNode tNode = (TNode) vecINode.elementAt(i);

			if (tNode.getLineNum() > 0)
			{
				tNode.setLineNum(tNode.getLineNum() + lineNumber);
			}
			else
			{
				tNode.setLineNum(lineNumber);
			}

			int oldNumber = tNode.getTokenNumber();
			if (oldNumber != -1)
			{
				tNode.setTokenNumber(oldNumber + tokenNumber);
			}
		}
		// printElements(vecINode);
	}

	private void increaseNumberNewNodeDeep(TNode iNode, int tokenNumber, int lineNumber)
	{
		Vector vecINode = new Vector();
		fillVectorDeep(iNode, vecINode);

		for (int i = 0; i < vecINode.size(); i++)
		{
			TNode tNode = (TNode) vecINode.elementAt(i);

			if (tNode.getLineNum() > 0)
			{
				tNode.setLineNum(tNode.getLineNum() + lineNumber);
			}
			else
			{
				tNode.setLineNum(lineNumber);
			}
			int oldNumber = tNode.getTokenNumber();
			if (oldNumber != -1)
			{
				tNode.setTokenNumber(oldNumber + tokenNumber);
			}
		}
	}

	private void printElements(Vector vec)
	{
		System.out.println(" elements:  -------------------------" + vec.size());
		for (int i = 0; i < vec.size(); i++)
		{
			TNode node = (TNode) vec.elementAt(i);
			System.out.print(" > " + node.getText() + "\t  type: " + node.getType() + "  line: " + node.getLineNum()
					+ "  children# " + node.getNumberOfChildren());

			Enumeration keys = (node).getAttributesTable().keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				if (key.equalsIgnoreCase(TNode.TOKEN_NUMBER))
				{
					System.out.print(" " + key + ":" + (node).getAttribute(key));
				}
				if (key.equalsIgnoreCase(TNode.IS_SOURCE))
				{
					System.out.print("  --isSource");
				}
				if (key.equalsIgnoreCase(TNode.SCOPE_NAME))
				{
					System.out.print("   scope :" + (node).getAttribute(key));
				}
				else
				{
					System.out.print(" " + key);
				}
			}
			System.out.println("");
		}
		System.out.println("x------------------------------------");
	}

	private void increaseNumberInfoChannel(int iNumber, int tokenNumber)
	{
		if (iNumber > 0)
		{
			int maxTokenNumber = infoChannel.getMaxTokenNumber() + iNumber;
			infoChannel.setMaxTokenNumber(maxTokenNumber);
		}

		Hashtable lineLists = infoChannel.getHashtableLine();
		Hashtable newHashtable = (Hashtable) lineLists.clone();

		for (int i = 0; i <= infoChannel.getMaxTokenNumber(); i++)
		{
			Integer inti = i;
			if (lineLists.containsKey(inti))
			{
				Vector tokenLineVector = (Vector) lineLists.get(inti);
				if (tokenLineVector != null && i > tokenNumber)
				{
					newHashtable.remove(new Integer(i));
					newHashtable.put(i + iNumber, tokenLineVector);
				}
			}
		}
		infoChannel.setHashtable(newHashtable);
		// if(iNumber < 0)
		// {
		// int maxTokenNumber = infoChannel.getMaxTokenNumber() + iNumber;
		// infoChannel.setMaxTokenNumber(maxTokenNumber);
		// }
	}

	private void smartIncreaseNumberInfoChannel()
	{
		int maxTokenNumber = findMaxInfoChannel();
		if (maxTokenNumber > 0)
		{
			infoChannel.setMaxTokenNumber(maxTokenNumber);

			Hashtable lineLists = infoChannel.getHashtableLine();
			Hashtable newHashtable = (Hashtable) lineLists.clone();
			// from 1
			for (int i = 1; i <= infoChannel.getMaxTokenNumber(); i++)
			{
				Integer inti = i;
				if (lineLists.containsKey(inti))
				{
					Vector tokenLineVector = (Vector) lineLists.get(inti);
					if (tokenLineVector != null)
					{
						newHashtable.remove(new Integer(i));
						int newNumber = findNewNumberForInfoChannel(i);
						newHashtable.put(newNumber, tokenLineVector);
					}
				}
			}
			infoChannel.setHashtable(newHashtable);
		}
	}

	// used in smartIncreaseNumberInfoChannel()
	private int findNewNumberForInfoChannel(int i)
	{
		for (int j = 0; j < commentKeepers.size(); j++)
		{
			TNode tNode = (TNode) commentKeepers.elementAt(j);
			if (tNode.getOldComment() == i)
			{
				return tNode.getTokenNumber();
			}
		}
		return -1;
	}

	// used in smartIncreaseNumberInfoChannel()
	private int findMaxInfoChannel()
	{
		int max = -1;
		for (int i = 0; i < commentKeepers.size(); i++)
		{
			TNode tNode = (TNode) commentKeepers.elementAt(i);
			int cur = tNode.getTokenNumber();
			if (cur > max)
			{
				max = cur;
			}
		}
		return max;
	}

	//
	private void increaseNumberMainNode(int incNumber, int fromNumber, int lineInc)
	{
		for (int i = 0; i < allNodes.size(); i++)
		{
			TNode tNode = (TNode) allNodes.elementAt(i);
			int curNumber = tNode.getTokenNumber();
			if (curNumber > fromNumber)
			{
				tNode.setTokenNumber(curNumber + incNumber);
				// if(tNode.getLineNum() > 0)
				tNode.setLineNum(tNode.getLineNum() + lineInc);
			}
		}
	}

	// 1 node - to insert after, 2 - will be inserted
	private void insertAfterNode(TNode bNode, TNode iNode)
	{
		int tokenNumber = -100;
		int lineNumber = -100;
		TNode nextSibling = (TNode) bNode.getNextSibling();

		tokenNumber = nextSibling.getTokenNumber();
		lineNumber = nextSibling.getLineNum();

		if (tokenNumber == -1) // node without token number
		{
			tokenNumber = findMinTokenNumber(nextSibling.deepCopy());
		}

		// System.out.println("token # : " + tokenNumber + " line # " +
		// lineNumber );

		Vector vecINode = new Vector();
		fillVector(iNode, vecINode);

		// int insertionNumber = vecINode.size();
		int insertionNumber = getMaxTokenNumber(vecINode);

		increaseNumberNewNode(vecINode, tokenNumber - 1, lineNumber);

		// todo 1 is a incremental for lines - change later
		increaseNumberMainNode(insertionNumber, tokenNumber - 1, 1);

		// increaseNumberInfoChannel(insertionNumber, tokenNumber-1);

		bNode.setNextSibling(iNode);
		iNode.setPreviousNode(bNode);

		iNode.setNextSibling(nextSibling);
		nextSibling.setPreviousNode(iNode);

		updateVector(vecINode);
	}

	// 1 node - to insert after, 2 - will be inserted
	private void insertBeforeNode(TNode beforeNode, TNode newNode)
	{
		int tokenNumber = -100;
		int lineNumber = -100;
		TNode nextSibling = (TNode) beforeNode.getNextSibling();

		tokenNumber = nextSibling.getTokenNumber();
		lineNumber = nextSibling.getLineNum();

		if (tokenNumber == -1) // node without token number
		{
			tokenNumber = findMinTokenNumber(nextSibling.deepCopy());
		}

		// System.out.println("token # : " + tokenNumber + " line # " +
		// lineNumber );

		Vector vecINode = new Vector();
		fillVector(newNode, vecINode);

		// int insertionNumber = vecINode.size();
		int insertionNumber = getMaxTokenNumber(vecINode);

		increaseNumberNewNode(vecINode, tokenNumber - 1, lineNumber);

		// todo 1 is a incremental for lines - change later
		increaseNumberMainNode(insertionNumber, tokenNumber - 1, 1);

		// increaseNumberInfoChannel(insertionNumber, tokenNumber-1);

		TNode origbeforeNode = beforeNode.getPreviousNode();
		origbeforeNode.setNextSibling(newNode);
		newNode.setPreviousNode(origbeforeNode);

		newNode.setNextSibling(beforeNode);
		beforeNode.setPreviousNode(newNode);

		updateVector(vecINode);
	}

	private int findMinTokenNumber(TNode n)
	{
		Vector v = new Vector();
		fillVector(n, v);
		int min = 2147483647;
		for (int i = 0; i < v.size(); i++)
		{
			int number = ((TNode) v.elementAt(i)).getTokenNumber();
			if (number > 0 && number < min)
			{
				min = number;
			}
		}
		return min;
	}

	private int findMinTokenNumberDeep(TNode n)
	{
		Vector v = new Vector();
		fillVectorDeep(n, v);
		int min = 2147483647;
		for (int i = 0; i < v.size(); i++)
		{
			int number = ((TNode) v.elementAt(i)).getTokenNumber();
			if (number > 0 && number < min)
			{
				min = number;
			}
		}
		return min;
	}

	private int findMaxTokenNumber(TNode nnn)
	{
		int result = -1;
		Vector v = new Vector();
		fillVector(nnn, v);

		for (int i = 0; i < v.size(); i++)
		{
			TNode node = (TNode) v.elementAt(i);
			Integer n = (Integer) node.getAttribute(TNode.TOKEN_NUMBER);
			if (n != null)
			{
				int number = n;
				if (number > result)
				{
					result = number;
				}
			}
		}
		return result;
	}

	private int findMaxTokenNumberDeep(TNode nnn)
	{
		int result = -1;
		Vector v = new Vector();
		fillVectorDeep(nnn, v);

		for (int i = 0; i < v.size(); i++)
		{
			TNode node = (TNode) v.elementAt(i);
			Integer n = (Integer) node.getAttribute(TNode.TOKEN_NUMBER);
			if (n != null)
			{
				int number = n;
				if (number > result)
				{
					result = number;
				}
			}
		}
		return result;
	}

	public void printTreeToFile(String s) throws FileNotFoundException
	{
		TNode.printTreeToFile(node, s);
	}

	public void printFunctions()
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			System.out.println(" " + function.getName());
		}
	}

	public void weaveEntryFunctions(String string)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			{
				weavetoFunctionEntry(function, string);
			}
		}
	}

	public void weaveEntryFunction(Function function, String string)
	{
		weavetoFunctionEntry(function, string);
	}

	public void weaveGlobalIntroduction(String code, GlobalIntroductionPoint gip)
	{
		TNode iNode = wrappingUtil.prepareNodeForIntroduction(StaticVariableReplacer.replaceStaticVariables(code,
				this.filename));
		TNode previous = gip.getNode().getPreviousNode();
		// System.out.println("Previous NODE:
		// "+GnuCParser._tokenNames[previous.getType()]);
		this.insertBeforeNode(previous, iNode);
	}

	public void weaveHeaderIntroduction(String code, HeaderIntroductionPoint hip)
	{
		hip.getNode().addComment(code);
		hip.getNode().HEADER = true;
	}

	public void weaveStructureIntroduction(String code, Struct struct)
	{
		TNode[] iNodes = wrappingUtil.prepareNodeForStructureIntroduction(StaticVariableReplacer
				.replaceStaticVariables(code, this.filename));

		// System.out.println("Struct code:
		// "+StaticVariableReplacer.replaceStaticVariables(code,
		// this.filename));

		TNode node = struct.getNode().firstChildOfType(GnuCTokenTypes.LCURLY);
		TNode node1 = (TNode) node.getNextSibling();

		node.setNextSibling(iNodes[0]);
		iNodes[0].setPreviousNode(node);
		iNodes[0].setNextSibling(iNodes[1]);
		iNodes[1].setPreviousNode(iNodes[0]);
		iNodes[1].setNextSibling(node1);
		node1.setPreviousNode(iNodes[1]);
	}

	public void weaveFunctionIntroduction(Function function, String code)
	{
		String text = StaticVariableReplacer.replaceStaticVariables(code, function);
		TNode iNode = wrappingUtil.prepareNode(text);
		TNode node113 = function.getToInsertBeforeNode();

		if (node113.getType() == GnuCTokenTypes.NCompoundStatement)
		{
			insertAsFirstChild(node113, iNode);
		}
	}

	private void weavetoFunctionEntry(Function function, String string)
	{
		TNode iNode = wrappingUtil.prepareNodeWithSiblings(StaticVariableReplacer.replaceStaticVariables(string,
				function));
		TNode node113 = function.getToInsertBeforeNode();
		TNode firstChild = (TNode) node113.getFirstChild();
		if (firstChild.getType() != GnuCTokenTypes.NDeclaration)
		{
			insertAsFirstChild(function.getToInsertBeforeNode(), iNode);
		}
		else
		{
			while (firstChild.getNextSibling().getType() == GnuCTokenTypes.NDeclaration)
			{
				firstChild = (TNode) firstChild.getNextSibling();
			}
			insertAfterNode(firstChild, iNode);
		}
	}

	public void weaveExitFunctions(String string)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			insertAfterFunExecutionBody(function, string);
		}
	}

	public void weaveExitFunction(String funName, String string)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			if (function.getName().equalsIgnoreCase(funName))
			{
				insertAfterFunExecutionBody(function, string);
			}
		}
	}

	public void weaveExitFunction(Function function, String string)
	{
		insertAfterFunExecutionBody(function, string);
	}

	private void insertAfterFunExecutionBody(Function function, String string)
	{
		// TNode.printTree(function.mainNode);
		// ASTFrame astf = new ASTFrame("Hallo",function.mainNode);
		// astf.setSize(400,400);
		// astf.setVisible(true);
		// Vector nodes = reconsiderAfterNodes(function.mainNode);
		for (int i = 0; i < function.getNumberExitPoints(); i++)
		{
			TNode iNode = wrappingUtil.prepareNodeWithSiblings(StaticVariableReplacer.replaceStaticVariables(string,
					function));
			ExitPoint exitPoint = function.getExitPoint(i);
			TNode exitNode = exitPoint.getNode();
			// TNode.printTree(exitNode);
			// System.out.println("Parent: "+exitNode.getParent());
			// System.out.println("Previous: "+exitNode.getPreviousNode());
			// System.out.println("Next: "+exitNode.getNextSibling());
			// System.out.println("Child: "+exitNode.getFirstChild());

			// case when we add just to the end of function (no return - no
			// exit)
			if (exitPoint.getExitType() == ExitPoint.END)
			{
				prepareNodeEndCase(iNode, exitNode);
				int old_max_line = exitNode.getLineNum();
				int old_max_token = exitNode.getTokenNumber();

				int new_max_line = findMaxLineNumber(iNode);
				int new_max_token = findMaxTokenNumber(iNode);

				int lineInc = new_max_line - old_max_line;
				int tokenInc = new_max_token - old_max_token;

				increaseNumberMainNode(tokenInc + 1, old_max_token - 1, lineInc);

				// increaseNumberInfoChannel(tokenInc, old_max_token);

				TNode prev = exitNode.getPreviousNode();

				if (prev != null)
				{
					prev.setNextSibling(iNode);
					iNode.setPreviousNode(prev);
				}
				else
				{
					TNode parent = exitNode.getParent();
					parent.setFirstChild(iNode);
					iNode.setParent(parent);
					exitNode.setParent(null);
				}

				iNode.setNextSibling(exitNode);
				exitNode.setPreviousNode(iNode);
				updateVectorDeep(iNode);
			}
			else
			{
				// insertBeforeWithBrackets(iNode, exitNode);
				int old_max_line = findMaxLineNumberDeep(exitNode);
				int old_max_token = findMaxTokenNumberDeep(exitNode);

				TNode copyExitNode = exitNode.deepCopy();
				exitPoint.setNode(copyExitNode);
				prepareCommentsKeeping(copyExitNode);
				prepareNodeWithBracketsBefore(iNode, copyExitNode);
				// printNodeDeep(iNode);

				TNode prev = exitNode.getPreviousNode();
				// System.out.println("Previous node: "+prev);
				TNode next = (TNode) exitNode.getNextSibling();

				// increasing numbers
				int new_max_line = findMaxLineNumber(iNode);
				int new_max_token = findMaxTokenNumber(iNode);

				int lineInc = new_max_line - old_max_line;
				int tokenInc = new_max_token - old_max_token;

				// System.out.println(">> " + old_max_token + " >> " +
				// new_max_token);

				increaseNumberMainNode(tokenInc, old_max_token, lineInc);

				// increaseNumberInfoChannel(tokenInc, old_max_token);

				// put token numbers to -999 for removal
				markTokensRemoveDeep(exitNode);

				if (prev != null)
				{
					prev.setNextSibling(iNode);
					iNode.setPreviousNode(prev);
				}
				else
				{
					TNode parent = exitNode.getParent();
					parent.setFirstChild(iNode);
					iNode.setParent(parent);
				}

				if (next != null)
				{
					iNode.setNextSibling(next);
					next.setPreviousNode(iNode);
				}
				updateVectorDeep(iNode);
				removeTokens();
			}
			// System.out.print("exit node after: ");
			// printOneNode(exitPoint.getNode());
		}
		// System.out.println("-------------------------");
	}

	private void insertBeforeWithBrackets(TNode iNode, TNode tNode)
	{
		int old_max_line = findMaxLineNumberDeep(tNode);
		int old_max_token = findMaxTokenNumberDeep(tNode);

		TNode copyNode = tNode.deepCopy();
		prepareCommentsKeeping(copyNode);
		prepareNodeWithBracketsBefore(iNode, copyNode);

		TNode prev = tNode.getPreviousNode();
		TNode next = (TNode) tNode.getNextSibling();

		// increasing numbers
		int new_max_line = findMaxLineNumber(iNode);
		int new_max_token = findMaxTokenNumber(iNode);

		int lineInc = new_max_line - old_max_line;
		int tokenInc = new_max_token - old_max_token;

		increaseNumberMainNode(tokenInc, old_max_token, lineInc);

		markTokensRemoveDeep(tNode);

		if (prev != null)
		{
			prev.setNextSibling(iNode);
			iNode.setPreviousNode(prev);
		}
		else
		{
			TNode parent = tNode.getParent();
			parent.setFirstChild(iNode);
			iNode.setParent(parent);
		}

		if (next != null)
		{
			iNode.setNextSibling(next);
			next.setPreviousNode(iNode);
		}

		updateVectorDeep(iNode);
		removeTokens();
	}

	// this method checks is a node a comments keeper and add to comments
	// keepers array!!!
	private void prepareCommentsKeeping(TNode copyExitNode)
	{
		Vector v = new Vector();
		fillVector(copyExitNode, v);
		for (int i = 0; i < v.size(); i++)
		{

			TNode tNode = (TNode) v.elementAt(i);
			// System.out.println(" !!! comments tests " +
			// tNode.getTokenNumber());
			if (tNode.getOldComment() > 0)
			{
				commentKeepers.addElement(tNode);
			}
		}
	}

	private void removeTokens()
	{
		for (int i = 0; i < allNodes.size(); i++)
		{
			TNode tNode = (TNode) allNodes.elementAt(i);
			if (tNode.getTokenNumber() == WrappedAST.FOR_REMOVAL)
			{
				allNodes.removeElementAt(i);
			}
		}

		for (int i = 0; i < commentKeepers.size(); i++)
		{
			TNode tNode = (TNode) commentKeepers.elementAt(i);
			if (tNode.getTokenNumber() == WrappedAST.FOR_REMOVAL)
			{
				commentKeepers.removeElementAt(i);
			}
		}
	}

	private void markTokensRemoveDeep(TNode exitNode)
	{
		Vector v = new Vector();
		fillVectorDeep(exitNode, v);

		for (int i = 0; i < v.size(); i++)
		{
			TNode tNode = (TNode) v.elementAt(i);
			tNode.setTokenNumber(WrappedAST.FOR_REMOVAL);
		}
	}

	private void markTokensSpecialSubFun(TNode node)
	{
		TNode lastBracket = node.getLastSibling();
		int goodNumber = lastBracket.getTokenNumber();
		lastBracket.setTokenNumber(-998);
		Vector v = new Vector();
		fillVector(node, v);

		for (int i = 0; i < v.size(); i++)
		{
			TNode tNode = (TNode) v.elementAt(i);
			if (tNode.getTokenNumber() != -998)
			{
				tNode.setTokenNumber(WrappedAST.FOR_REMOVAL);
			}
			else
			{
				tNode.setTokenNumber(goodNumber);
			}
		}
	}

	private void prepareNodeEndCase(TNode iNode, TNode exitNode)
	{
		int min = exitNode.getTokenNumber();
		int line = exitNode.getLineNum();
		increaseNumberNewNode(iNode, min, line);
	}

	private void prepareNodeWithBracketsBefore(TNode iNode, TNode copyExitNode)
	{
		int min = findMinTokenNumber(copyExitNode);
		int line = copyExitNode.getLineNum();

		int insertionNumber = iNode.firstChildOfType(GnuCTokenTypes.RCURLY).getTokenNumber();

		int newLineNumber = findMaxLineNumber(iNode);

		increaseNumberNewNode(iNode, min, line);

		increaseNumberNewNode(copyExitNode, insertionNumber, newLineNumber + 1);

		// change token numbers of the rest of compound statement
		newLineNumber = findMaxLineNumber(copyExitNode);
		iNode.firstChildOfType(GnuCTokenTypes.RCURLY).setTokenNumber(findMaxTokenNumber(copyExitNode) + 1);
		iNode.firstChildOfType(GnuCTokenTypes.RCURLY).setLineNum(newLineNumber);// + 1

		TNode lastNode = iNode.firstChildOfType(GnuCTokenTypes.RCURLY);
		TNode prevNode = lastNode.getPreviousNode();
		prevNode.setNextSibling(copyExitNode);
		copyExitNode.setPreviousNode(prevNode);
		copyExitNode.setNextSibling(lastNode);
		lastNode.setPreviousNode(copyExitNode);
	}

	private int findMaxLineNumber(TNode iNode)
	{
		int result = -1;
		Vector v = new Vector();
		fillVector(iNode, v);

		for (int i = 0; i < v.size(); i++)
		{
			int number = ((TNode) v.elementAt(i)).getLineNum();
			if (number > result)
			{
				result = number;
			}
		}
		return result;
	}

	private int findMaxLineNumberDeep(TNode iNode)
	{
		int result = -1;
		Vector v = new Vector();
		fillVectorDeep(iNode, v);

		for (int i = 0; i < v.size(); i++)
		{
			int number = ((TNode) v.elementAt(i)).getLineNum();
			if (number > result)
			{
				result = number;
			}
		}
		return result;
	}

	// just for testing - later remove
	public void testFunctions()
	{
		for (int i = 0; i < functions.size(); i++)
		{
			((Function) functions.elementAt(i)).testFunction();
		}
	}

	public void substituteFunctionBody(String functionName, String code)
	{
		Function function = getFunction(functionName);
		TNode node113 = function.getToInsertBeforeNode();
		TNode oldChild = (TNode) node113.getFirstChild();
		// System.out.println(" code " + code);
		TNode iNode = wrappingUtil.prepareNodeWithSiblings(StaticVariableReplacer
				.replaceStaticVariables(code, function));

		int n113line = node113.getLineNum();
		int n113token = node113.getTokenNumber();

		increaseNumberNewNode(iNode, n113token, n113line + 1);

		int maxNewline = findMaxLineNumber(iNode);
		int maxNewtoken = findMaxTokenNumber(iNode);

		TNode closingBracket = node113.firstChildOfType(GnuCTokenTypes.RCURLY);

		int maxOldline = closingBracket.getLineNum();
		int maxOldtoken = closingBracket.getTokenNumber();

		increaseNumberMainNode((maxNewtoken - maxOldtoken + 1), maxOldtoken - 1, maxNewline - maxOldline + 1);

		// increaseNumberInfoChannel(maxNewtoken - maxOldtoken + 1, maxOldtoken
		// -1);

		node113.setFirstChild(iNode);
		iNode.setParent(node113);

		iNode = iNode.getLastSibling();

		iNode.setNextSibling(closingBracket);
		closingBracket.setPreviousNode(iNode);

		markTokensSpecialSubFun(oldChild);
		removeTokens();
		updateVector(iNode);

		Vector vecExitPoints = reconsiderAfterNodes(function.getMainNode());
		function.setReturnPoints(vecExitPoints);
		// function.testFunction();
	}

	private Function getFunction(String functionName)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			if (function.getName().equalsIgnoreCase(functionName))
			{
				return function;
			}
		}
		return null;
	}

	public void removeFunctionBody(String functionName)
	{
		Function function = getFunction(functionName);
		if (function != null)
		{
			TNode mainNode = function.getMainNode();
			int minToken = findMinTokenNumberDeep(mainNode);
			int maxToken = findMaxTokenNumberDeep(mainNode);

			int minLine = mainNode.getLineNum();
			int maxLine = findMaxLineNumberDeep(mainNode);

			markTokensRemoveDeep(mainNode);
			removeTokens();

			increaseNumberMainNode(minToken - maxToken - 1, minToken, minLine - maxLine - 1);

			// mainNode.removeChildren();
			mainNode.removeSelf();
			TNode prev = mainNode.getPreviousNode();
			TNode next = (TNode) mainNode.getNextSibling();
			if (prev == null)
			{
				// we are removing first function
				node = next;
				next.setPreviousNode(null);
			}
			else
			{
				prev.setNextSibling(next);
				if (next != null)
				{
					next.setPreviousNode(prev);
				}
			}
			functions.remove(function);
		}
		else
		{
			// System.out.println("no such function");
		}
	}

	public void testCommentKeepers()
	{
		for (int i = 0; i < commentKeepers.size(); i++)
		{
			TNode tNode = (TNode) commentKeepers.elementAt(i);
			System.out.println(" old comment : " + tNode.getOldComment() + " number " + tNode.getTokenNumber());
		}
	}

	private Vector reconsiderAfterNodes(TNode mainNode)
	{
		Vector returnPoints = new Vector();
		TNode node113 = mainNode.firstChildOfType(113);

		// lookingForNumberOfReturns(tNode);
		Vector v = new Vector();
		fillVector(node113, v);

		for (int i = 0; i < v.size(); i++)
		{
			TNode node = (TNode) v.elementAt(i);

			// for return statements
			if (node.getType() == GnuCTokenTypes.LITERAL_return)
			{
				ExitPoint exitPoint = null;
				if (node.numberOfChildren() > 0)
				{
					if (node.getFirstChild().getType() == GnuCTokenTypes.ID)
					{
						String valueID = node.getFirstChild().getText();
						exitPoint = new ExitPoint(valueID, node, ExitPoint.RETURN);
					}
					else
					{
						exitPoint = new ExitPoint(node, ExitPoint.RETURN);
					}
				}
				else
				{
					exitPoint = new ExitPoint(node, ExitPoint.RETURN);
				}
				returnPoints.addElement(exitPoint);
			}
			// for exit statement
			// else if(node.getType() == 109 &&
			// ((TNode)node.getFirstChild()).getType() == 118 &&
			// ((TNode)node.getFirstChild().getFirstChild()).getType() == 27 &&
			// ((TNode)node.getFirstChild().getFirstChild()).getText().equalsIgnoreCase("exit"))
			// {
			// ExitPoint exitPoint = new ExitPoint(node, ExitPoint.EXIT);
			// returnPoints.addElement(exitPoint);
			// }
		}

		// also here we need to check about simple end of the function
		TNode ret = node113.firstChildOfType(GnuCTokenTypes.LITERAL_return);
		if (ret != null && ret.getNextSibling().getType() == GnuCTokenTypes.RCURLY)
		{
			// not needed to add simple exit
		}
		else
		{
			ExitPoint exitPoint = new ExitPoint(node113.firstChildOfType(GnuCTokenTypes.RCURLY), ExitPoint.END);
			returnPoints.addElement(exitPoint);
		}
		return returnPoints;
	}

	public void weaveFunctionCallBefore(String functionName, String code, String callFunctionName)
	{
		Function function = getFunction(functionName);
		weaveFunctionCall(function, code, callFunctionName, WrappedAST.BEFORE);
	}

	public void weaveGlobalFunctionCallBefore(String code, String callFunctionName)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			weaveFunctionCall(function, code, callFunctionName, WrappedAST.BEFORE);
		}
	}

	private void weaveFunctionCall(Function function, String code, String callFunctionName, int type)
	{
		// System.out.println(" >>> function call");
		TNode main = function.getMainNode();
		Vector nodes109 = new Vector();
		fillVectorDeepSpecial(main, nodes109, 109);
		// System.out.println("109 size : " + nodes109.size());
		for (int i = 0; i < nodes109.size(); i++)
		{
			TNode node109 = (TNode) nodes109.elementAt(i);
			Vector nodes118 = new Vector();
			fillVectorDeepSpecial(node109, nodes118, 118);
			// System.out.println(" 118 size : " + nodes118.size());
			for (int j = 0; j < nodes118.size(); j++)
			{
				TNode node118 = (TNode) nodes118.elementAt(j);
				if (node118.getFirstChild().getText().equalsIgnoreCase(callFunctionName))
				{
					// System.out.println("!! text : " + callFunctionName);
					TNode iNode = null;
					if (code != null)
					{
						iNode = wrappingUtil.prepareNodeWithBrackets(StaticVariableReplacer.replaceStaticVariables(
								code, function));
					}
					if (type == WrappedAST.BEFORE)
					{
						insertBeforeWithBrackets(iNode, node109);
					}
					else if (type == WrappedAST.AFTER)
					{
						insertAfterWithBrackets(iNode, node109);
					}
				}
			}
		}
	}

	private void insertAfterWithBrackets(TNode iNode, TNode tNode)
	{
		int old_max_line = findMaxLineNumberDeep(tNode);
		int old_max_token = findMaxTokenNumberDeep(tNode);

		TNode copyNode = tNode.deepCopy();
		prepareCommentsKeeping(copyNode);
		prepareNodeWithBracketsAfter(iNode, copyNode);

		TNode prev = tNode.getPreviousNode();
		TNode next = (TNode) tNode.getNextSibling();

		// increasing numbers
		int new_max_line = findMaxLineNumber(iNode);
		int new_max_token = findMaxTokenNumber(iNode);

		int lineInc = new_max_line - old_max_line;
		int tokenInc = new_max_token - old_max_token;

		increaseNumberMainNode(tokenInc, old_max_token, lineInc);

		markTokensRemoveDeep(tNode);

		if (prev != null)
		{
			prev.setNextSibling(iNode);
			iNode.setPreviousNode(prev);
		}
		else
		{
			TNode parent = tNode.getParent();
			parent.setFirstChild(iNode);
			iNode.setParent(parent);
		}

		if (next != null)
		{
			iNode.setNextSibling(next);
			next.setPreviousNode(iNode);
		}

		updateVectorDeep(iNode);
		removeTokens();
	}

	private void prepareNodeWithBracketsAfter(TNode iNode, TNode copyNode)
	{
		int min = findMinTokenNumber(copyNode);
		int line = copyNode.getLineNum();

		int insertionNumber = iNode.firstChildOfType(8).getTokenNumber();

		int newLineNumber = findMaxLineNumber(iNode);

		increaseNumberNewNode(iNode, min, line);

		increaseNumberNewNode(copyNode, insertionNumber, newLineNumber + 1);

		// change token numbers of the rest of compound statement
		newLineNumber = findMaxLineNumber(copyNode);
		iNode.firstChildOfType(GnuCTokenTypes.RCURLY).setTokenNumber(findMaxTokenNumber(copyNode) + 1);
		iNode.firstChildOfType(GnuCTokenTypes.RCURLY).setLineNum(newLineNumber);// + 1

		TNode node8 = iNode.firstChildOfType(GnuCTokenTypes.RCURLY);
		TNode lastNode = node8.getPreviousNode();
		TNode prev = null;

		if (lastNode.getPreviousNode() != null)
		{
			prev = lastNode.getPreviousNode();
			prev.setNextSibling(copyNode);
			copyNode.setPreviousNode(prev);
		}
		else
		{
			prev = lastNode.getParent();
			prev.setFirstChild(copyNode);
			copyNode.setParent(prev);
		}
		// some stupid number rearragement -- don't try to understand !!! writen
		// once !!!
		{
			int iMaxLast = findMaxTokenNumberDeep(lastNode);
			int iMinLast = findMinTokenNumberDeep(lastNode);
			int iLineMaxLast = findMaxLineNumberDeep(lastNode);
			int ilineMinLast = lastNode.getLineNum();

			int lineDiff1 = ilineMinLast - iLineMaxLast - 1;
			int diff1 = iMinLast - iMaxLast - 2;

			int iMaxCopy = findMaxTokenNumberDeep(copyNode);
			int iMinCopy = findMinTokenNumberDeep(copyNode);
			int iLineMaxCopy = findMaxLineNumberDeep(copyNode);
			int ilineMinCopy = copyNode.getLineNum();

			int lineDiff2 = iLineMaxCopy - ilineMinCopy + 1;
			int diff2 = iMaxCopy - iMinCopy + 1;

			increaseNumberNewNodeDeep(copyNode, diff1, lineDiff1);
			increaseNumberNewNodeDeep(lastNode, diff2, lineDiff2);

		}
		copyNode.setNextSibling(lastNode);
		lastNode.setPreviousNode(copyNode);
	}

	public void weaveFunctionCallAfter(String functionName, String code, String callFunctionName)
	{
		Function function = getFunction(functionName);
		weaveFunctionCall(function, code, callFunctionName, WrappedAST.AFTER);
	}

	public void weaveGlobalFunctionCallAfter(String code, String callFunctionName)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			Function function = (Function) functions.elementAt(i);
			weaveFunctionCall(function, code, callFunctionName, WrappedAST.AFTER);
		}
	}
}
