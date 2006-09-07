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
 * $Id: WeavingInstruction.java,v 1.5 2005/11/24 00:46:45 pascal_durr Exp $
 */
package Composestar.C.CONE;

import Composestar.C.specification.*;
import Composestar.C.wrapper.Function;
import Composestar.C.wrapper.utils.GeneralUtils;

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 22-jan-2005
 * Time: 12:37:59
 * To change this template use File | Settings | File Templates.
 */
public class WeavingInstruction
{
    //weaving concrete places
    public final static int FUNCTION_EXECUTION_BEFORE = 0;
    public final static int FUNCTION_EXECUTION_AFTER = 1;
    
    public final static int FUNCTION_CALL_BEFORE = 2;
    public final static int FUNCTION_CALL_AFTER = 3;
    
    public final static int GLOBAL_INTRODUCTION_BEFORE = 4;
    public final static int GLOBAL_INTRODUCTION_AFTER = 5;
    
    public final static int FUNCTION_INTRODUCTION_BEFORE = 6;
    public final static int FUNCTION_INTRODUCTION_AFTER = 7;
    
    public final static int STRUCTURE_INTRODUCTION_BEFORE = 8;
    public final static int STRUCTURE_INTRODUCTION_AFTER = 9;
    
    public final static int HEADER_INTRODUCTION_BEFORE = 10;
    public final static int HEADER_INTRODUCTION_AFTER = 11;
    
    
    private Function function = null;
    private int instructiontype = -1;
    private String code = null;
    private String alternate = null;
    private Advice advice = null;
    private AdviceApplication aa = null;

    public WeavingInstruction(Function function, Advice advice, AdviceApplication aa)
    {
    	//System.out.println("Weaving instruction for: "+advicetype+" == "+applicationtype);
    	this.setAdviceApplication(aa);
    	this.setAdvice(advice);
        this.function = function;
        this.instructiontype = getInstructionType(advice.getType(), aa.getType()); 
        this.code = advice.getCode();
    }
    
    public WeavingInstruction(String alternate, Advice advice, AdviceApplication aa)
    {
    	//System.out.println("Weaving instruction for: "+advicetype+" == "+applicationtype);
    	this.setAdviceApplication(aa);
    	this.setAdvice(advice);
        this.setAlternate(alternate);
        this.instructiontype = getInstructionType(advice.getType(), aa.getType()); 
        this.code = advice.getCode();
    }

    public Function getFunction()
    {
        return this.function;
    }

    public void setFunction(Function function)
    {
        this.function = function;
    }
    
    public int getType()
    {
        return instructiontype;
    }

    public void setType(int type)
    {
        this.instructiontype = type;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String toString()
    {
    	if(alternate == null)
    		return "WI["+this.function.getFileName()+":"+this.function.getName()+"] @ "+this.getInstructionString(instructiontype)+" CODE: "+this.getCode();
    	else
    		return "WI["+this.advice.getId()+"] @ "+this.getInstructionString(instructiontype)+" CODE: "+this.getCode();
    }
    
    public static int getInstructionType(int advicetype, int applicationtype)
    {
    	int type = -1;
    	if(applicationtype == GeneralUtils.BEFORE)
    	{
    		switch(advicetype)
    		{
    			case GeneralUtils.FUNCTION_BODY:
    			{
    				type = WeavingInstruction.FUNCTION_EXECUTION_BEFORE;
    				break;
    			}
    			case GeneralUtils.FUNCTION_CALL:
    			{
    				type = WeavingInstruction.FUNCTION_CALL_BEFORE;
    				break;
    			}
    			case GeneralUtils.FUNCTION_INTRODUCTION:
    			{
    				type = WeavingInstruction.FUNCTION_INTRODUCTION_BEFORE;
    				break;
    			}
    			case GeneralUtils.GLOBAL_INTRODUCTION:
    			{
    				type = WeavingInstruction.GLOBAL_INTRODUCTION_BEFORE;
    				break;
    			}case GeneralUtils.HEADER_INTRODUCTION:
    			{
    				type = WeavingInstruction.HEADER_INTRODUCTION_BEFORE;
    				break;
    			}
    			case GeneralUtils.STRUCTURE_INTRODUCTION:
    			{
    				type = WeavingInstruction.STRUCTURE_INTRODUCTION_BEFORE;
    				break;
    			}
    		}
    	}
    	else if(applicationtype == GeneralUtils.AFTER)
    	{
    		switch(advicetype)
    		{
    			case GeneralUtils.FUNCTION_BODY:
    			{
    				type = WeavingInstruction.FUNCTION_EXECUTION_AFTER;
    				break;
    			}
    			case GeneralUtils.FUNCTION_CALL:
    			{
    				type = WeavingInstruction.FUNCTION_CALL_AFTER;
    				break;
    			}
    			case GeneralUtils.FUNCTION_INTRODUCTION:
    			{
    				type = WeavingInstruction.FUNCTION_INTRODUCTION_AFTER;
    				break;
    			}
    			case GeneralUtils.GLOBAL_INTRODUCTION:
    			{
    				type = WeavingInstruction.GLOBAL_INTRODUCTION_AFTER;
    				break;
    			}
    			case GeneralUtils.STRUCTURE_INTRODUCTION:
    			{
    				type = WeavingInstruction.STRUCTURE_INTRODUCTION_AFTER;
    				break;
    			}
    		}
    	}
    	
    	return type;
    }
    
    public String getInstructionString(int type)
    {
    	if(type == -1) return "COULD NOT RESOLVE TYPE!";
    	String[] sts = new String[]{
    			"FUNCTION_EXECUTION_BEFORE",
    			"FUNCTION_EXECUTION_AFTER",
    			"FUNCTION_CALL_BEFORE",
    			"FUNCTION_CALL_AFTER",
    			"GLOBAL_INTRODUCTION_BEFORE",
    			"GLOBAL_INTRODUCTION_AFTER",
    			"FUNCTION_INTRODUCTION_BEFORE",
    			"FUNCTION_INTRODUCTION_AFTER",
    			"STRUCTURE_INTRODUCTION_BEFORE",
    			"STRUCTURE_INTRODUCTION_AFTER"
    	};
    	return sts[type];
    }
    
    public boolean isSingularInstruction()
    {
    	switch(instructiontype)
    	{
	    	case STRUCTURE_INTRODUCTION_AFTER:
	    	case STRUCTURE_INTRODUCTION_BEFORE:
	    	case GLOBAL_INTRODUCTION_AFTER:
	    	case GLOBAL_INTRODUCTION_BEFORE:
	    	{
	    		return true;
	    	}
	    	default:
	    		return false;
    	}
    }

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	public Advice getAdvice() {
		return advice;
	}

	public void setAdviceApplication(AdviceApplication aa) {
		this.aa = aa;
	}

	public AdviceApplication getAdviceApplication() {
		return aa;
	}
}
