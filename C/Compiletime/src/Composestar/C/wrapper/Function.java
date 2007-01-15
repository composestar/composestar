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
 * $Id: Function.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.wrapper;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import Composestar.C.wrapper.parsing.TNode;
import Composestar.C.wrapper.utils.GeneralUtils;

/**
 * Created by IntelliJ IDEA. User: ByelasH Date: 23-dec-2004 Time: 10:14:11 To
 * change this template use File | Settings | File Templates.
 */
public class Function extends WeaveblePoint
{
	private String name = null;

	private ParameterType retParameter = null;

	private Vector returnPoints = new Vector(); // vector with ReturnPoints

	private Vector inParameters = new Vector(); // vector with Parameters

	private TNode toInsertBefore = null; // node113 of a function

	public TNode mainNode = null;

	private boolean bVoid = false;

	private boolean noInputParameters = false;

	public boolean isNoInputParameters()
	{
		return noInputParameters;
	}

	public void setNoInputParameters()
	{
		this.noInputParameters = true;
	}

	public int getNumberExitPoints()
	{
		return returnPoints.size();
	}

	public ExitPoint getExitPoint(int i)
	{
		return (ExitPoint) returnPoints.elementAt(i);
	}

	public TNode getMainNode()
	{
		return mainNode;
	}

	public TNode getToInsertBeforeNode()
	{
		return toInsertBefore;
	}

	public Function(String name, ParameterType retParameter, Vector inParameters, TNode mainNode,
			TNode toInsertBeforeNode, Vector returnPoints, String filename)
	{
		super(filename);
		this.name = name;
		this.retParameter = retParameter;

		this.inParameters = inParameters;
		this.mainNode = mainNode;
		this.toInsertBefore = toInsertBeforeNode;
		this.returnPoints = returnPoints;
	}

	public String getName()
	{
		return name;
	}

	public ParameterType getReturnParameter()
	{
		return retParameter;
	}

	public int getNumberOfInputs()
	{
		return inParameters.size();
	}

	public Parameter getInputParameter(int i)
	{
		return (Parameter) inParameters.elementAt(i);
	}

	public boolean hasNoParameters()
	{
		boolean retValue = false;
		if (this.inParameters == null)
		{
			retValue = true;
		}
		else if (this.inParameters.size() <= 0)
		{
			retValue = true;
		}
		return retValue;
	}

	public boolean isVoid()
	{
		return bVoid;
	}

	public void setVoid()
	{
		bVoid = true;
	}

	private void printOneNode(TNode node)
	{
		System.out.print(" > " + node.getText() + "\t  type: " + node.getType() + "  line: " + node.getLineNum()
				+ "  children# " + node.getNumberOfChildren());

		Enumeration keys = (node).getAttributesTable().keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			if (key.equalsIgnoreCase("tokenNumber"))
			{
				System.out.print(" " + key + ":"
						+ (node).getAttribute(key));
			}
			else if (key.equalsIgnoreCase("source"))
			{
				System.out.print("  --isSource");
			}
			else if (key.equalsIgnoreCase("scopeName"))
			{
				System.out.print("   scope :"
						+ (node).getAttribute(key));
			}
			else
			{
				System.out.print("key::" + key + " == " + (node).getAttribute(key));
			}
		}
		System.out.println("");

	}

	// testing - later remove
	public void testFunction()
	{
		System.out.println("----------- " + name);
		System.out.print(" before node ");
		printOneNode(toInsertBefore);

		System.out.println("number of return points: " + returnPoints.size());
		for (int i = 0; i < returnPoints.size(); i++)
		{
			TNode n = ((ExitPoint) returnPoints.elementAt(i)).getNode();
			printOneNode(n);
			if (((ExitPoint) returnPoints.elementAt(i)).isSimpleVarReturn())
			{
				System.out.println(" return value: "
						+ ((ExitPoint) returnPoints.elementAt(i)).getValueID());
			}
		}

		if (noInputParameters)
		{
			System.out.println("no input par");
		}
		else
		{
			for (int i = 0; i < inParameters.size(); i++)
			{
				Parameter parameter = (Parameter) inParameters.elementAt(i);
				System.out.print("input :  ");
				parameter.testParameter();
			}
		}
		if (bVoid)
		{
			System.out.println("void");
		}
		else
		{
			System.out.print("output  ");
			retParameter.testParameterType();
		}
		System.out.println("------------------------------");
	}

	public void setReturnPoints(Vector returnPoints)
	{
		this.returnPoints = returnPoints;
	}

	public boolean hasReturnType(String type)
	{
		if (this.retParameter == null)
		{
			return false;
		}
		if (type.equals("*"))
		{
			return true;
		}
		return this.matchType(this.retParameter, type);
	}

	public boolean matchType(ParameterType ptype, String type)
	{
		boolean found = true;
		Parameter param = Function.getParameterFromParameterString(type);
		if (param.getArrayLevel() != ptype.getArrayLevel())
		{
			return false;
		}
		if (param.getPointerLevel() != ptype.getPointerLevel())
		{
			return false;
		}
		if (param.getTypesLength() > ptype.getTypesLength())
		{
			return false;
		}

		for (int i = 0; i < param.getTypesLength(); i++)
		{
			boolean tmpfound = false;
			for (int j = 0; j < ptype.getTypesLength(); j++)
			{
				if (param.type[i] == ptype.type[j])
				{
					tmpfound = tmpfound | true;
				}
				else if (param.type[i] == 27 && ptype.type[j] == 98)
				{
					// can
																			// also
																			// be a
																			// typedef
																			// :(
					tmpfound = tmpfound | true;
				}

			}
			found = found & tmpfound;
		}
		return found;
	}

	public static Parameter getParameterFromParameterString(String param)
	{
		String theparam = param;
		Parameter parameter = new Parameter(new int[0]);
		// Fix pointer
		while (theparam.endsWith("*"))
		{
			parameter.addPointerLevel();
			theparam = theparam.substring(0, theparam.length() - 1);
		}
		// Fix array
		while (theparam.endsWith("[]"))
		{
			parameter.addArrayLevel();
			theparam = theparam.substring(0, theparam.length() - 2);
		}
		// Now get the type!
		StringTokenizer st = new StringTokenizer(theparam, " ", false);
		int[] type = new int[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens())
		{
			int ttype = GeneralUtils.getTypeForString(st.nextToken());
			// System.out.println("Param type: "+GnuCParser._tokenNames[ttype]);
			type[i] = ttype;
		}
		parameter.setType(type);
		return parameter;
	}

	public boolean hasParameterWithType(String type)
	{
		if (this.inParameters == null)
		{
			return false;
		}
        for (Object inParameter : this.inParameters) {
            Parameter param = (Parameter) inParameter;
            if (this.matchType(param, type)) {
                return true;
            }
        }
        return false;
	}

	public boolean hasParameterWithName(String name)
	{
        for (Object inParameter : this.inParameters) {
            Parameter param = (Parameter) inParameter;
            if (param.getValueID().equals(name)) {
                return true;
            }
        }
        return false;
	}

	public String toString()
	{
		return this.filename + ":" + this.name;
	}
}
