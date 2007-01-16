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
 * $Id: FunctionFactory.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.wrapper;

import java.util.HashMap;
import java.util.Vector;

import Composestar.C.wrapper.parsing.GnuCTokenTypes;
import Composestar.C.wrapper.parsing.TNode;

/**
 * Created by IntelliJ IDEA. User: ByelasH Date: 10-jan-2004 Time: 9:45:39 To
 * change this template use File | Settings | File Templates.
 */
public class FunctionFactory
{
	TNode node = null;

	boolean fVoid = false; // is the function void or not

	boolean noInput = false; // is the function without input parameters

	private HashMap params = new HashMap();

	Vector vecReturns = null; // vector of nodes of 43 type (return)

	private String name;

	public Function createFunction(TNode mainNode, String filename)
	{
		// should think maybe to change next booleans
		fVoid = false;
		noInput = false;

		this.node = mainNode;
		vecReturns = new Vector();

		name = parseName();

		ParameterType returnParameter = parseReturnParameterType();
		Vector inParameters = parseInputParameters();

		parseTraceSpec(inParameters, filename);

		TNode toInsertBefore = parseBeforeNode();
		Vector vecReturnPoints = parseAfterNodes();

		Function function = new Function(name, returnParameter, inParameters, mainNode, toInsertBefore,
				vecReturnPoints, filename);
		if (fVoid)
		{
			function.setVoid();
		}
		if (noInput)
		{
			function.setNoInputParameters();
		}
		return function;
	}

	private Vector parseAfterNodes()
	{
		Vector returnPoints = new Vector();

		TNode node113 = node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);

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
			// System.out.println(node113.firstChildOfType(GnuCTokenTypes.RCURLY).getPreviousNode());
			returnPoints.addElement(exitPoint);
		}
		return returnPoints;
	}

	// ok ;)
	private TNode parseBeforeNode()
	{
		return node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);
	}

	// input good
	private Vector parseInputParameters()
	{
		Vector v = new Vector();
		TNode n = node.firstChildOfType(GnuCTokenTypes.NDeclarator).firstChildOfType(GnuCTokenTypes.NParameterTypeList);
		if (n.getNumberOfChildren() == 1)
		{
			// System.out.println(" >>>>> " + name + " - no input " );
			noInput = true;
			return null;
		}
		else
		{
			TNode node114 = n.firstChildOfType(GnuCTokenTypes.NParameterDeclaration);
			for (int i = 0; i < n.numberOfChildsOfType(GnuCTokenTypes.NParameterDeclaration); i++)// n.getNumberOfChilds()/2
			{
				TNode nnn = (TNode) node114.getFirstChild();
				int type = nnn.getType();
				if (type == GnuCTokenTypes.LITERAL_void && node114.getNumberOfChildren() == 1)
				{
					// void
					// System.out.println(" >>>>> " + name + " - void " +
					// node114.getNumberOfChildren());
					noInput = true;
					return null;
				}
				else
				{
					if (type == GnuCTokenTypes.NTypedefName)
					{
						// type defined
						String sss = nnn.firstChildOfType(GnuCTokenTypes.ID).getText();
						int iii[] = { type };
						TNode node100 = node114.firstChildOfType(GnuCTokenTypes.NDeclarator);
						String ID = node100.firstChildOfType(GnuCTokenTypes.ID).getText();
						Parameter p = new Parameter(iii, sss, ID);

						this.checkForPointer(node100, p);
						this.checkForArray(node100, p);

						v.add(p);

						this.params.put(ID, p);
					}
					else
					{
						int[] iii;
						if (node114.getNumberOfChildren() == 2)
						{
							iii = new int[1];
							iii[0] = type;
						}
						else
						{
							TNode child = (TNode) node114.getFirstChild();
							iii = new int[node114.getNumberOfChildren() - 1];
							for (int j = 0; j < node114.getNumberOfChildren() - 1; j++)
							{
								iii[j] = child.getType();
								child = (TNode) child.getNextSibling();
							}
						}
						Parameter p = new Parameter(iii);

						TNode node100 = node114.firstChildOfType(GnuCTokenTypes.NDeclarator);
						String ID = node100.firstChildOfType(GnuCTokenTypes.ID).getText();
						p.setValueID(ID);
						this.checkForPointer(node100, p);
						this.checkForArray(node100, p);
						v.add(p);
						this.params.put(ID, p);
					}
				}
				node114 = node114.firstSiblingOfType(GnuCTokenTypes.NParameterDeclaration);
			}
			return v;
		}
	}

	// return parameter good
	private ParameterType parseReturnParameterType()
	{
		ParameterType p = null;
		// 3 children - simple return
		// 2 children - fun()
		// >3 children - complex: static int
		int childrenNumber = node.numberOfChildren();
		if (childrenNumber == 2)
		{
			fVoid = true;
		}
		else if (childrenNumber == 3)
		{
			TNode n = (TNode) node.getFirstChild();
			int type = n.getType();
			if (type == GnuCTokenTypes.LITERAL_void)
			{
				fVoid = true;
			}
			else if (type == GnuCTokenTypes.NTypedefName)
			{
				int iii[] = { type };
				String sss = n.firstChildOfType(GnuCTokenTypes.ID).getText();
				p = new ParameterType(iii, sss);

			}
			else
			{
				int iii[] = { type };
				p = new Parameter(iii);
			}
		}
		else if (childrenNumber > 3)
		{
			int size = node.getNumberOfChildren() - 2;
			int iii[] = new int[size];
			String additionalType = null;
			TNode n = (TNode) node.getFirstChild();
			iii[0] = n.getType();
			for (int i = 1; i < size; i++)
			{
				n = (TNode) n.getNextSibling();
				int type = n.getType();
				iii[i] = type;
				if (type == GnuCTokenTypes.NTypedefName)
				{
					additionalType = n.firstChildOfType(GnuCTokenTypes.ID).getText();
				}
			}
			p = new ParameterType(iii, additionalType);
		}
		checkForPointer(node.firstChildOfType(GnuCTokenTypes.NDeclarator), p);
		checkForArray(node.firstChildOfType(GnuCTokenTypes.NDeclarator), p);
		return p;
	}

	public void checkForPointer(TNode node100, ParameterType p)
	{
		TNode tn = (TNode) node100.getFirstChild();
		// System.out.println(node100.getText());

		if (tn.getType() == GnuCTokenTypes.NPointerGroup)
		{
			TNode n = null;
			for (int i = 0; i < node100.getNumberOfChildren(); i++)
			{
				if (i == 0)
				{
					n = (TNode) tn.getFirstChild();
				}
				else if (n != null)
				{
					n = (TNode) n.getNextSibling();
				}
				if (n != null && n.getType() == GnuCTokenTypes.STAR)
				{
					/**
					 * let op als het hier fout gaat kan er wel eens
					 * message.c/.h in je dir staan*
					 */
					p.addPointerLevel();
				}
			}
		}
	}

	public void checkForArray(TNode node100, ParameterType p)
	{
		TNode n = null;
		for (int i = 0; i < node100.getNumberOfChildren(); i++)
		{
			if (i == 0)
			{
				n = (TNode) node100.getFirstChild();
			}
			else
			{
				n = (TNode) n.getNextSibling();
			}
			if (n != null && n.getType() == GnuCTokenTypes.LBRACKET)
			{
				p.addArrayLevel();
			}
		}
	}

	private void parseTraceSpec(Vector params, String filename)
	{
		TNode tracenode = node.firstChildOfType(GnuCTokenTypes.LITERAL___trace__);
		if (tracenode == null)
		{
			return;
		}
		TNode node = (TNode) tracenode.getFirstChild();
		for (int i = 0; i < tracenode.getNumberOfChildren(); i++)
		{
			if (i == 0)
			{
				node = (TNode) tracenode.getFirstChild();
			}
			else
			{
				node = (TNode) node.getNextSibling();
			}
			if (node != null && node.getType() == GnuCTokenTypes.ID)
			{
				// System.out.println("ID["+i+"]: "+node.getText());
				if (node.getText().equals("in"))
				{
					// System.out.print("Found in spec: ");
					node = (TNode) node.getNextSibling();
					if (node != null)
					{
						i++;
					}
					if (node.getType() == GnuCTokenTypes.LPAREN)
					{
						node = (TNode) node.getNextSibling();
						i++;
						while (node != null && node.getType() != GnuCTokenTypes.RPAREN)
						{
							if (node.getType() == GnuCTokenTypes.ID)
							{
								// System.out.print(node.getText()+"("+(Parameter)this.params.get(node.getText())+"),");
								Parameter p = (Parameter) this.params.get(node.getText());
								if (p != null)
								{
									p.setUsageType(Parameter.IN);
								}
							}
							node = (TNode) node.getNextSibling();
							if (node != null)
							{
								i++;
							}
						}
						// System.out.println();
					}
				}
				else if (node.getText().equals("out"))
				{
					// System.out.print("Found in spec: ");
					node = (TNode) node.getNextSibling();
					if (node != null)
					{
						i++;
					}
					if (node.getType() == GnuCTokenTypes.LPAREN)
					{
						node = (TNode) node.getNextSibling();
						i++;
						while (node != null && node.getType() != GnuCTokenTypes.RPAREN)
						{
							if (node.getType() == GnuCTokenTypes.ID)
							{
								// System.out.print(node.getText()+"("+(Parameter)this.params.get(node.getText())+"),");
								Parameter p = (Parameter) this.params.get(node.getText());
								if (p != null)
								{
									p.setUsageType(Parameter.OUT);
								}
							}

							node = (TNode) node.getNextSibling();
							if (node != null)
							{
								i++;
							}
						}
						// System.out.println();
					}
				}
				else if (node.getText().equals("inout"))
				{
					// System.out.print("Found out spec: ");
					node = (TNode) node.getNextSibling();
					if (node != null)
					{
						i++;
					}
					if (node.getType() == GnuCTokenTypes.LPAREN)
					{
						node = (TNode) node.getNextSibling();
						i++;
						while (node != null && node.getType() != GnuCTokenTypes.RPAREN)
						{
							if (node.getType() == GnuCTokenTypes.ID)
							{
								// System.out.print(node.getText()+"("+(Parameter)this.params.get(node.getText())+"),");
								Parameter p = (Parameter) this.params.get(node.getText());
								if (p != null)
								{
									p.setUsageType(Parameter.INOUT);
								}
							}

							node = (TNode) node.getNextSibling();
							if (node != null)
							{
								i++;
							}
						}
						// System.out.println();
					}
				}
				/***************************************************************
				 * This else statement was needed when we used the tracing to
				 * add annotations this has become superfluous else
				 * if(node.getText().equals("Semantics")) { node =
				 * (TNode)node.getNextSibling(); if(node != null) i++;
				 * if(node.getType() == GnuCTokenTypes.LPAREN) { node =
				 * (TNode)node.getNextSibling(); i++; while(node != null &&
				 * node.getType() != GnuCTokenTypes.RPAREN) { if(node.getType() ==
				 * GnuCTokenTypes.StringLiteral) { //System.out.println("found:
				 * __trace__(Semantics("+node.getText()+")) in :" + filename
				 * +"."+ name); out.println("<Attribute type=" + '"'
				 * +"Semantics" + '"' + " value="+ node.getText()+ " target="+
				 * '"'+"Method"+ '"'+ " location="+ '"' +filename + "." + name +
				 * '"'+ " />"); } node = (TNode)node.getNextSibling(); if(node !=
				 * null) i++; } } }
				 **************************************************************/
			}
		}
		// TNode.printTree(tracenode);
	}

	private String parseName()
	{
		TNode n = node.firstChildOfType(100).firstChildOfType(27);
		String name = n.getText();
		return name;
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

}
