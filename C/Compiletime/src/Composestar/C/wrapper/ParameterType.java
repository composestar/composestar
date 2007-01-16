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
 * $Id: ParameterType.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.wrapper;

import Composestar.C.wrapper.parsing.GnuCTokenTypes;
import Composestar.C.wrapper.utils.GeneralUtils;

/**
 * Created by IntelliJ IDEA. User: ByelasH Date: 23-dec-2004 Time: 10:09:11 To
 * change this template use File | Settings | File Templates.
 */
public class ParameterType
{
	protected int type[];

	protected String additionaltypeValue = null;

	protected boolean defined = false;

	protected int pointer = 0;

	protected int array = 0;

	public boolean isArray()
	{
		return array > 0;
	}

	public void addArrayLevel()
	{
		this.array++;
	}

	public int getArrayLevel()
	{
		return this.array;
	}

	public boolean isPointer()
	{
		return pointer > 0;
	}

	public void addPointerLevel()
	{
		this.pointer++;
	}

	public int getPointerLevel()
	{
		return this.pointer;
	}

	public ParameterType(int[] type, String additionalTypeValue)
	{
		this.type = type;
		this.additionaltypeValue = additionalTypeValue;
		this.defined = true;
	}

	public ParameterType(int[] type)
	{
		this.type = type;
	}

	public int getTypesLength()
	{
		return type.length;
	}

	public int getType(int i)
	{
		return type[i];
	}

	public void setType(int[] type)
	{
		this.type = type;
	}

	public String getAdditionalTypeValue()
	{
		return additionaltypeValue;
	}

	public void setAdditionalTypeValue(String addtype)
	{
		this.additionaltypeValue = addtype;
	}

	public boolean isDefined()
	{
		return defined;
	}

	public void setDefined()
	{
		this.defined = true;
	}

	public void testParameterType()
	{
		for (int i1 : type)
		{
			System.out.print(" " + GeneralUtils.getTypeForID(i1));
		}
		if (defined)
		{
			System.out.print(" " + additionaltypeValue);
		}

		if (this.isArray())
		{
			for (int j = 0; j < this.getArrayLevel(); j++)
			{
				System.out.print("[]");
			}
		}

		if (this.isPointer())
		{
			for (int j = 0; j < this.getPointerLevel(); j++)
			{
				System.out.print("*");
			}
		}

		System.out.println("");
	}

	public String getTypeName()
	{
		String tmp = "";
		for (int aType : type)
		{
			if (aType != GnuCTokenTypes.NTypedefName)
			{
				tmp += GeneralUtils.getTypeForID(aType);
			}
		}

		if (defined)
		{
			tmp += " " + additionaltypeValue;
		}

		if (this.isArray())
		{
			for (int j = 0; j < this.getArrayLevel(); j++)
			{
				tmp += "[]";
			}
		}

		if (this.isPointer())
		{
			for (int j = 0; j < this.getPointerLevel(); j++)
			{
				tmp += "*";
			}
		}
		return tmp;
	}
}
