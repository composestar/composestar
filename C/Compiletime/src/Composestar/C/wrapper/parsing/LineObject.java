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
package Composestar.C.wrapper.parsing;

class LineObject
{
	LineObject parent = null;

	String source = "";

	int line = 1;

	boolean enteringFile = false;

	boolean returningToFile = false;

	boolean systemHeader = false;

	boolean treatAsC = false;

	public LineObject()
	{
		super();
	}

	public LineObject(LineObject lobj)
	{
		parent = lobj.getParent();
		source = lobj.getSource();
		line = lobj.getLine();
		enteringFile = lobj.getEnteringFile();
		returningToFile = lobj.getReturningToFile();
		systemHeader = lobj.getSystemHeader();
		treatAsC = lobj.getTreatAsC();
	}

	public LineObject(String src)
	{
		source = src;
	}

	public void setSource(String src)
	{
		source = src;
	}

	public String getSource()
	{
		return source;
	}

	public void setParent(LineObject par)
	{
		parent = par;
	}

	public LineObject getParent()
	{
		return parent;
	}

	public void setLine(int l)
	{
		line = l;
	}

	public int getLine()
	{
		return line;
	}

	public void newline()
	{
		line++;
	}

	public void setEnteringFile(boolean v)
	{
		enteringFile = v;
	}

	public boolean getEnteringFile()
	{
		return enteringFile;
	}

	public void setReturningToFile(boolean v)
	{
		returningToFile = v;
	}

	public boolean getReturningToFile()
	{
		return returningToFile;
	}

	public void setSystemHeader(boolean v)
	{
		systemHeader = v;
	}

	public boolean getSystemHeader()
	{
		return systemHeader;
	}

	public void setTreatAsC(boolean v)
	{
		treatAsC = v;
	}

	public boolean getTreatAsC()
	{
		return treatAsC;
	}

	public String toString()
	{
		StringBuffer ret;
		ret = new StringBuffer("# " + line + " \"" + source + "\"");
		if (enteringFile)
		{
			ret.append(" 1");
		}
		if (returningToFile)
		{
			ret.append(" 2");
		}
		if (systemHeader)
		{
			ret.append(" 3");
		}
		if (treatAsC)
		{
			ret.append(" 4");
		}
		return ret.toString();
	}
}
