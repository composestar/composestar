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
 * $Id: Functions.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.specification;

import java.util.ArrayList;
import java.util.Hashtable;

import Composestar.C.wrapper.Function;
import Composestar.C.wrapper.utils.GeneralUtils;

public class Functions
{
	private boolean hasReturnSpec = false;

	private boolean hasParamSpec = false;

	private String file = "";

	private String data = "";

	private int type = 0;

	private String returnType = "";

	private boolean invertReturnType = false;

	private Pointcut parent = null;

	private ArrayList parameters = new ArrayList();

	public Hashtable realFunctionTable = new Hashtable();

	public XMLParameter getParameter(int i)
	{
		return (XMLParameter) this.parameters.get(i);
	}

	public void addParameter(XMLParameter param)
	{
		this.setHasParamSpec(true);
		this.parameters.add(param);
	}

	public int getNumberOfParameters()
	{
		return this.parameters.size();
	}

	public void setReturnType(String returnType)
	{
		this.setHasReturnSpec(true);
		this.returnType = returnType;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setData(String function)
	{
		this.data = function;
	}

	public String getData()
	{
		return data;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public String getFile()
	{
		return file;
	}

	public void setInvertReturnType(boolean invertReturnType)
	{
		this.invertReturnType = invertReturnType;
	}

	public boolean isInvertReturnType()
	{
		return invertReturnType;
	}

	public void addFunction(Function func)
	{
		this.realFunctionTable.put(func.getName(), func);
	}

	public void removeFunction(String funcname)
	{
		this.realFunctionTable.remove(funcname);
	}

	public String toString()
	{
		String tmp = "";
		tmp += "\t\tFunctions: file=" + file + " data=" + this.data + "("
				+ GeneralUtils.getTypeForProgramElement(this.type) + ")\n";
		if (this.invertReturnType)
		{
			tmp += "\t\t\t!!! Return: type=" + this.returnType + "\n";
		}
		else
		{
			tmp += "\t\t\t    Return: type=" + this.returnType + "\n";
		}
		for (int i = 0; i < this.getNumberOfParameters(); i++)
		{
			tmp += this.getParameter(i).toString();
		}
		return tmp;
	}

	public void setParent(Pointcut parent)
	{
		this.parent = parent;
	}

	public Pointcut getParent()
	{
		return parent;
	}

	public void setHasReturnSpec(boolean hasReturnSpec)
	{
		this.hasReturnSpec = hasReturnSpec;
	}

	public boolean hasReturnSpec()
	{
		return hasReturnSpec;
	}

	public void setHasParamSpec(boolean hasParamSpec)
	{
		this.hasParamSpec = hasParamSpec;
	}

	public boolean hasParamSpec()
	{
		return hasParamSpec;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
}
