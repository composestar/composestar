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
 * $Id$
 */
package Composestar.C.specification;

import java.util.ArrayList;

public class Pointcut
{
	private String id = null;

	private ArrayList functions = new ArrayList();

	private ArrayList advices = new ArrayList();

	private Aspect parent = null;

	public Pointcut(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void addFunctions(Functions funcs)
	{
		functions.add(funcs);
	}

	public int getNumberOfFunctions()
	{
		return functions.size();
	}

	public Functions getFunctions(int i)
	{
		return (Functions) functions.get(i);
	}

	public void addAdviceApplication(AdviceApplication advapp)
	{
		advices.add(advapp);
	}

	public int getNumberOfAdviceApplications()
	{
		return advices.size();
	}

	public AdviceApplication getAdviceApplication(int i)
	{
		return (AdviceApplication) advices.get(i);
	}

	public String toString()
	{
		String tmp = "";
		tmp += "\tPointcut: " + id + "\n";
		for (int i = 0; i < getNumberOfFunctions(); i++)
		{
			tmp += getFunctions(i).toString();
		}
		for (int i = 0; i < getNumberOfAdviceApplications(); i++)
		{
			tmp += getAdviceApplication(i).toString();
		}
		return tmp;
	}

	public void setParent(Aspect parent)
	{
		this.parent = parent;
	}

	public Aspect getParent()
	{
		return parent;
	}
}
