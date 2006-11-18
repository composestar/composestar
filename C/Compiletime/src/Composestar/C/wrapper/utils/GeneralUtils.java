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
 * $Id: GeneralUtils.java,v 1.1 2006/09/04 08:12:15 johantewinkel Exp $
 */
package Composestar.C.wrapper.utils;

import Composestar.C.wrapper.parsing.GnuCParser;
import Composestar.C.wrapper.parsing.GnuCTokenTypes;

public class GeneralUtils
{
	// ADVICE TYPES:
	public final static int FUNCTION_BODY = 1;
    public final static int FUNCTION_CALL = 2;
    public final static int FUNCTION_INTRODUCTION = 3;
    public final static int GLOBAL_INTRODUCTION = 4;
    public final static int STRUCTURE_INTRODUCTION = 5;
    public final static int HEADER_INTRODUCTION=6;
    
    //  ADVICE APPLICATION TYPES:
    public final static int BEFORE = 1;
    public final static int AFTER = 2;
    
    //  IDENTIFIER TYPES::
    public final static int FUNCTION = 1;
    public final static int STRUCT = 2;
    public final static int GLOBAL = 3;
    public final static int HEADER = 4;
    
    public static String getTypeForID(int id)
	{
		String tmp = GnuCParser._tokenNames[id];
		if(tmp.startsWith("\""))
			tmp = tmp.substring(1);
		if(tmp.endsWith("\""))
			tmp = tmp.substring(0,tmp.length()-1);
		return tmp;
	}
	
	public static int getTypeOfProgramPoint(String type)
	{
		int ttype = 0;
		if(type.equalsIgnoreCase("execution"))
			ttype = FUNCTION_BODY;
		else if(type.equalsIgnoreCase("call"))
			ttype = FUNCTION_CALL;
		else if(type.equalsIgnoreCase("global_introduction"))
			ttype = GLOBAL_INTRODUCTION;
		else if(type.equalsIgnoreCase("function_introduction"))
			ttype = FUNCTION_INTRODUCTION;
		else if(type.equalsIgnoreCase("function_variable_introduction"))
			ttype = FUNCTION_INTRODUCTION;
		else if(type.equalsIgnoreCase("header_introduction"))
			ttype = HEADER_INTRODUCTION;
		else if(type.equalsIgnoreCase("structure_introduction"))
			ttype = STRUCTURE_INTRODUCTION;
		else 
			ttype = 0;
		return ttype;
	}
	
	public static int getTypeOfAdvice(String type)
	{
		int ttype = 0;
		if(type.equalsIgnoreCase("before"))
			ttype = BEFORE;
		else if(type.equalsIgnoreCase("after"))
			ttype = AFTER;
		else 
			ttype = 0;
		return ttype;
	}
	
	public static int getTypeForString(String tmp)
	{
		int type = 0;
		if(tmp.equals("int"))
			type = GnuCTokenTypes.LITERAL_int;
		else if(tmp.equals("char"))
			type = GnuCTokenTypes.LITERAL_char;
		else if(tmp.equals("short"))
			type = GnuCTokenTypes.LITERAL_short;
		else if(tmp.equals("void"))
			type = GnuCTokenTypes.LITERAL_void;
		else if(tmp.equals("long"))
			type = GnuCTokenTypes.LITERAL_long;
		else if(tmp.equals("float"))
			type = GnuCTokenTypes.LITERAL_float;
		else if(tmp.equals("double"))
			type = GnuCTokenTypes.LITERAL_double;
		else if(tmp.equals("unsigned"))
			type = GnuCTokenTypes.LITERAL_unsigned;
		else if(tmp.equals("signed"))
			type = GnuCTokenTypes.LITERAL_signed;
		else
			type = GnuCTokenTypes.ID;
		return type;
	}
	
	public static boolean isType(int type)
	{
		if(type == GnuCTokenTypes.LITERAL_int ||
			type == GnuCTokenTypes.LITERAL_char ||
			type == GnuCTokenTypes.LITERAL_short||
			type == GnuCTokenTypes.LITERAL_void ||
			type == GnuCTokenTypes.LITERAL_long ||
			type == GnuCTokenTypes.LITERAL_float ||
			type == GnuCTokenTypes.LITERAL_double ||
			type == GnuCTokenTypes.LITERAL_unsigned ||
			type == GnuCTokenTypes.LITERAL_signed)
			return true;
		else
			return false;
	}
	
	public static int getTypeForProgramElement(String tmp)
	{
		int type = 0;
		if(tmp.equals("function"))
			type = FUNCTION;
		else if(tmp.equals("struct"))
			type = STRUCT;
		else if(tmp.equals("global"))
			type = STRUCT;
		return type; 
	}
	
	public static String getTypeForProgramElement(int type)
	{
		if(type == FUNCTION)
			return "function";
		else if(type == STRUCT)
			return "struct";
		else if(type == GLOBAL)
			return "global";
		return "";
	}
}
