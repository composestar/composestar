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
package Composestar.C.wrapper.utils;

import Composestar.C.wrapper.*;

import java.io.File;
import java.util.*;

public class StaticVariableReplacer
{
	public static String replaceStaticVariables(String input, Function function)
	{
		String output = input.replaceAll("%FUNC_NAME%",function.getName());
		output = output.replaceAll("%FUNC_NAME_UPPER%",function.getName().toUpperCase());
		output = output.replaceAll("%FILE_NAME%",function.getFileName());
		output = output.replaceAll("%FILE_NAME_UPPER%",function.getFileName().toUpperCase());
		output = output.replaceAll("%MODULE_NAME_UPPER%",getFileNameFromString(function.getFileName().toUpperCase()));
		output = output.replaceAll("%MODULE_NAME%",getFileNameFromString(function.getFileName().toLowerCase()));
		return output;
	}
	
	public static String replaceParameter(String input, Parameter param)
	{
		return input.replaceAll("%PARAM_NAME%",param.getValueID());
	}
	
	public static String replaceStaticVariables(String input, String file)
	{
		String output = input.replaceAll("%FILE_NAME%",file);
		output = output.replaceAll("%FILE_NAME_UPPER%",file.toUpperCase());
		output = output.replaceAll("%MODULE_NAME_UPPER%",getFileNameFromString(file.toUpperCase()));
		output = output.replaceAll("%MODULE_NAME%",getFileNameFromString(file.toLowerCase()));
		
		return output;
	}
	
	private static String getFileNameFromString(String str)
	{
		String out = "";
		if(str.indexOf(File.separator) > 0)
		{
			out = str.substring(str.lastIndexOf(File.separator)+1);
		}
		if(out.endsWith(".ccc") || out.endsWith(".CCC"))
			out = out.substring(0,out.length()-2);
		return out;
	}
}
