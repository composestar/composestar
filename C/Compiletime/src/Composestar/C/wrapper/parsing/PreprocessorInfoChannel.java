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
 * $Id: PreprocessorInfoChannel.java,v 1.1 2006/09/04 08:12:15 johantewinkel Exp $
 */
package Composestar.C.wrapper.parsing;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class PreprocessorInfoChannel
{
	Hashtable lineLists = new Hashtable(); // indexed by Token number

	int firstValidTokenNumber = 0;

	int maxTokenNumber = 0;

	public void addLineForTokenNumber(Object line, Integer toknum)
	{
		if (line instanceof String)
		{
			if (((String) line).indexOf("# ") > 0)
			{
				System.out.println("Found line directive " + line);
			}
		}
		if (lineLists.containsKey(toknum))
		{
			Vector lines = (Vector) lineLists.get(toknum);
			lines.addElement(line);
		}
		else
		{
			Vector lines = new Vector();
			lines.addElement(line);
			lineLists.put(toknum, lines);
			if (maxTokenNumber < toknum.intValue())
			{
				maxTokenNumber = toknum.intValue();
			}
		}
	}

	public int getMaxTokenNumber()
	{
		return maxTokenNumber;
	}

	public void setMaxTokenNumber(int maxTokenNumber)
	{
		this.maxTokenNumber = maxTokenNumber;
	}

	public Vector extractLinesPrecedingTokenNumber(Integer toknum)
	{
		Vector lines = new Vector();
		if (toknum == null)
		{
			return lines;
		}
		for (int i = firstValidTokenNumber; i < toknum.intValue(); i++)
		{
			Integer inti = new Integer(i);
			if (lineLists.containsKey(inti))
			{
				Vector tokenLineVector = (Vector) lineLists.get(inti);
				if (tokenLineVector != null)
				{
					Enumeration tokenLines = tokenLineVector.elements();
					while (tokenLines.hasMoreElements())
					{
						lines.addElement(tokenLines.nextElement());
					}
					lineLists.remove(inti);
				}
			}
		}
		firstValidTokenNumber = toknum.intValue();
		return lines;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer("Composestar.C.parsing.PreprocessorInfoChannel:\n");
		for (int i = 0; i <= maxTokenNumber + 1; i++)
		{
			Integer inti = new Integer(i);
			if (lineLists.containsKey(inti))
			{
				Vector tokenLineVector = (Vector) lineLists.get(inti);
				if (tokenLineVector != null)
				{
					Enumeration tokenLines = tokenLineVector.elements();
					while (tokenLines.hasMoreElements())
					{
						sb.append(inti + ":" + tokenLines.nextElement() + '\n');
					}
				}
			}
		}
		return sb.toString();
	}

	public Hashtable getHashtableLine()
	{
		return lineLists;
	}

	public void setHashtable(Hashtable hashtable)
	{
		this.lineLists = hashtable;
	}
}
