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
 * $Id: Parameter.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.wrapper;

import Composestar.C.wrapper.parsing.GnuCTokenTypes;
import Composestar.C.wrapper.utils.GeneralUtils;

/**
 * Created by IntelliJ IDEA. User: ByelasH Date: 23-dec-2004 Time: 10:09:11 To
 * change this template use File | Settings | File Templates.
 */
public class Parameter extends ParameterType
{
	public static final int NONE = 1;

	public static final int IN = 2;

	public static final int OUT = 3;

	public static final int INOUT = 4;

	private int usageType = NONE;

	private String valueID;

	public Parameter(int[] type, String additionalTypeValue)
	{
		super(type, additionalTypeValue);
	}

	public Parameter(int[] type)
	{
		super(type);
	}

	public Parameter(int[] type, String additionalTypeValue, String valueID)
	{
		super(type, additionalTypeValue);
		this.valueID = valueID;
	}

	public String getValueID()
	{
		return valueID;
	}

	public void setValueID(String valueID)
	{
		this.valueID = valueID;
	}

	public void testParameter()
	{

        for (int aType : type) {
            if (!(aType == GnuCTokenTypes.NTypedefName)) {
                System.out.print(GeneralUtils.getTypeForID(aType) + " ");
            }
        }
        if (defined)
		{
			System.out.print(additionaltypeValue);
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
		System.out.print("  " + valueID);
		if (this.usageType == IN)
		{
			System.out.println("(IN)");
		}
		else if (this.usageType == OUT)
		{
			System.out.println("(OUT)");
		}
		else if (this.usageType == INOUT)
		{
			System.out.println("(INOUT)");
		}
		else
		{
			System.out.println("");
		}
	}

	public String getParameterTypeName()
	{
		String tmp = super.getTypeName();
		tmp += valueID;
		return tmp;
	}

	public void setUsageType(int usageType)
	{
		this.usageType = usageType;
	}

	public int getUsageType()
	{
		return usageType;
	}
}
