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
 * $Id: TracingAspect.java,v 1.1 2006/09/01 15:31:20 johantewinkel Exp $
 */
package Composestar.C.ASPECTS.TRACING;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.C.CONE.WeaverEngine;
import Composestar.C.CONE.WeavingInstruction;
import Composestar.C.specification.Advice;
import Composestar.C.specification.AdviceApplication;
import Composestar.C.wrapper.Function;
import Composestar.C.wrapper.Parameter;
import Composestar.C.wrapper.Struct;
import Composestar.C.wrapper.retrieveAST;
import Composestar.C.wrapper.utils.GeneralUtils;
import Composestar.C.wrapper.utils.Profiler;
import Composestar.C.wrapper.utils.StaticVariableReplacer;

public class TracingAspect extends WeaveCAspect
{
	private retrieveAST wrapper = null;

	private HashSet weavebleobjs = new HashSet();

	private boolean hasInTrace = false;

	private boolean hasOutTrace = false;

	private boolean firstInTrace = true;

	private boolean firstOutTrace = false;

	private String inBuffer = "";

	private String outBuffer = "";

	private ArrayList inParams = new ArrayList();

	private ArrayList outParams = new ArrayList();

	public static final boolean PROFILE = true;

	public TracingAspect()
	{
		// if(props.getProperty("types") == null)
		if (true)
		{
			System.out.println("No trace types.xml specified!");
		}
		else
		{
			if (PROFILE)
			{
				Profiler.printStartStatus("Parsing trace types files");
			}
			TraceTypesReader ttr = new TraceTypesReader();
			// ttr.readTraceTypes(props.getProperty("types"));
			TraceTypesDB ttdb = TraceTypesDB.instance();
			if (PROFILE)
			{
				Profiler.printEndStatus("found " + ttdb.getNumberOfTraceTypes() + " types");
			}
		}
	}

	public HashSet weave(retrieveAST wrapper)
	{
		this.wrapper = wrapper;
		Iterator it = wrapper.getFunctions().iterator();
		while (it.hasNext())
		{
			this.hasInTrace = false;
			this.hasOutTrace = false;
			// this.inBuffer = "printf(\"Input parameter(s):";
			// this.outBuffer = "printf(\"Output parameter(s):";
			this.inBuffer = "ZPSPTR_trace_in_var(%MODULE_NAME%_mod_data.tr_handle,funcname,\"Input parameter(s): ";
			this.outBuffer = "ZPSPTR_trace_out_var(%MODULE_NAME%_mod_data.tr_handle,funcname,\"Output parameter(s): ";
			this.inParams = new ArrayList();
			this.outParams = new ArrayList();
			this.traceFunction((Function) it.next());
		}
		// System.out.println("Check: "+wrapper.getFunctions().size()+" ==
		// "+this.weavebleobjs.size());
		return weavebleobjs;
	}

	private void traceFunction(Function function)
	{
		// System.out.println("Processing function: "+function.getName());
		if (!function.hasNoParameters())
		{
			for (int i = 0; i < function.getNumberOfInputs(); i++)
			{
				Parameter param = function.getInputParameter(i);
				// System.out.println("Checking param: "+param.getTypeName());
				if (param.getAdditionalTypeValue() != null) // STRUCT!
				{
					this.traceStruct(param, function);
					this.weavebleobjs.add(function);
				}
				else
				{
					this.traceParameter(param, function);
					this.weavebleobjs.add(function);
				}
			}
		}
		else
		{
			this.weaveBasicIn(function);
			this.weaveBasicOut(function);
		}

		if (this.hasInTrace)
		{
			for (int i = 0; i < this.inParams.size(); i++)
			{
				if (i == 0)
				{
					this.inBuffer += "\"," + this.inParams.get(i);
				}
				else
				{
					this.inBuffer += "," + this.inParams.get(i);
				}
			}
			this.inBuffer += ");";
			if (PROFILE)
			{
				System.out.println("Input: " + this.inBuffer);
			}
			createWeaveInstruction(function, null, this.inBuffer, GeneralUtils.BEFORE);

		}
		if (this.hasOutTrace)
		{
			for (int i = 0; i < this.outParams.size(); i++)
			{
				if (i == 0)
				{
					this.outBuffer += "\"," + this.outParams.get(i);
				}
				else
				{
					this.outBuffer += "," + this.outParams.get(i);
				}
			}
			this.outBuffer += ");";
			if (PROFILE)
			{
				System.out.println("Output: " + this.outBuffer);
			}
			createWeaveInstruction(function, null, this.outBuffer, GeneralUtils.AFTER);
		}
	}

	private void weaveBasicIn(Function function)
	{
		String in = "ZDSPTR_trace_in(%MODULE_NAME%_mod_data.tr_handle, func_name);";
		this.createWeaveInstruction(function, null, in, GeneralUtils.BEFORE);
		this.weavebleobjs.add(function);
	}

	private void weaveBasicOut(Function function)
	{
		String out = "ZDSPTR_trace_out(%MODULE_NAME%_mod_data.tr_handle, func_name, result);";
		this.createWeaveInstruction(function, null, out, GeneralUtils.AFTER);
		this.weavebleobjs.add(function);
	}

	private void traceParameter(Parameter param, Function function)
	{
		TraceTypesDB ttdb = TraceTypesDB.instance();
		TraceableType tt = ttdb.getTraceType(param.getTypeName());
		if (tt == null && param.getPointerLevel() == 0)
		{
			// System.out.println("No tracetype found for type:
			// "+function.getName()+"."+param.getValueID());
		}
		else if (tt == null && param.getPointerLevel() > 0)
		{
			if (param.getUsageType() == Parameter.IN)
			{
				this.hasInTrace = true;
				if (!this.firstInTrace)
				{
					this.inBuffer += ",";
					this.firstInTrace = false;
				}
				this.inBuffer += StaticVariableReplacer.replaceParameter("%PARAM_NAME% = %p", param);
				if (this.inParams.size() > 0)
				{
					this.inParams.add(" " + param.getValueID());
				}
				else
				{
					this.inParams.add(param.getValueID());
				}
			}
			else if (param.getUsageType() == Parameter.OUT)
			{
				this.hasOutTrace = true;
				if (this.firstOutTrace)
				{
					this.outBuffer += ",";
					this.firstOutTrace = false;
				}
				this.outBuffer += StaticVariableReplacer.replaceParameter("%PARAM_NAME% = %p", param);
				if (this.outParams.size() > 0)
				{
					this.outParams.add(" " + param.getValueID());
				}
				else
				{
					this.outParams.add(param.getValueID());
				}
			}
			else if (param.getUsageType() == Parameter.INOUT)
			{
				this.hasInTrace = true;
				if (this.firstInTrace)
				{
					this.inBuffer += ",";
					this.firstInTrace = false;
				}
				this.inBuffer += StaticVariableReplacer.replaceParameter("%PARAM_NAME% = %p", param);
				if (this.inParams.size() > 0)
				{
					this.inParams.add(" " + param.getValueID());
				}
				else
				{
					this.inParams.add(param.getValueID());
				}

				this.hasOutTrace = true;
				if (this.firstOutTrace)
				{
					this.outBuffer += ",";
					this.firstOutTrace = false;
				}
				this.outBuffer += StaticVariableReplacer.replaceParameter("%PARAM_NAME% = %p", param);
				if (this.outParams.size() > 0)
				{
					this.outParams.add(" " + param.getValueID());
				}
				else
				{
					this.outParams.add(param.getValueID());
				}
			}
		}
		else
		{
			if (param.getUsageType() == Parameter.IN)
			{
				this.hasInTrace = true;
				if (this.firstInTrace)
				{
					this.inBuffer += ",";
					this.firstInTrace = false;
				}
				this.inBuffer += StaticVariableReplacer.replaceParameter(tt.getInTraceString(), param);
				if (this.inParams.size() > 0)
				{
					this.inParams.add(" " + param.getValueID());
				}
				else
				{
					this.inParams.add(param.getValueID());
				}
			}
			else if (param.getUsageType() == Parameter.OUT)
			{
				this.hasOutTrace = true;
				if (this.firstOutTrace)
				{
					this.outBuffer += ",";
					this.firstOutTrace = false;
				}
				this.outBuffer += StaticVariableReplacer.replaceParameter(tt.getOutTraceString(), param);
				if (this.outParams.size() > 0)
				{
					this.outParams.add(" " + param.getValueID());
				}
				else
				{
					this.outParams.add(param.getValueID());
				}
			}
			else if (param.getUsageType() == Parameter.INOUT)
			{
				this.hasInTrace = true;
				if (this.firstInTrace)
				{
					this.inBuffer += ",";
					this.firstInTrace = false;
				}
				this.inBuffer += StaticVariableReplacer.replaceParameter(tt.getInTraceString(), param);
				if (this.inParams.size() > 0)
				{
					this.inParams.add(" " + param.getValueID());
				}
				else
				{
					this.inParams.add(param.getValueID());
				}

				this.hasOutTrace = true;
				if (this.firstOutTrace)
				{
					this.outBuffer += ",";
					this.firstOutTrace = false;
				}
				this.outBuffer += StaticVariableReplacer.replaceParameter(tt.getOutTraceString(), param);
				if (this.outParams.size() > 0)
				{
					this.outParams.add(" " + param.getValueID());
				}
				else
				{
					this.outParams.add(param.getValueID());
				}
			}
		}
	}

	private void createWeaveInstruction(Function function, Parameter param, String txt, int type)
	{
		if (param != null)
		{
			String out = StaticVariableReplacer.replaceParameter(txt, param);

			Advice advice = new Advice();
			advice.setId("TraceParam[" + param.getValueID() + "]");
			advice.setPriority(10);
			advice.setCode(out);
			advice.setType(GeneralUtils.FUNCTION_BODY);

			AdviceApplication aa = new AdviceApplication();
			aa.setId("TraceParam[" + param.getValueID() + "]");
			aa.setType(type);

			function.addWeavingInstruction(new WeavingInstruction(function, advice, aa));
			WeaverEngine.NUMWS++;
		}
		else
		{
			Advice advice = new Advice();
			advice.setId("Trace");
			advice.setPriority(10);
			advice.setCode(txt);
			advice.setType(GeneralUtils.FUNCTION_BODY);

			AdviceApplication aa = new AdviceApplication();
			aa.setId("Trace");
			aa.setType(type);

			function.addWeavingInstruction(new WeavingInstruction(function, advice, aa));
			WeaverEngine.NUMWS++;
		}
	}

	private void traceStruct(Parameter param, Function function)
	{
		if (param.getPointerLevel() > 1)
		{
			return; // Don't know how to trace
														// this
		}

		Struct struct = (Struct) this.wrapper.structASTMap.get(param.getAdditionalTypeValue());
		if (struct != null)
		{
			String intrace = param.getValueID() + " = { ";
			String outtrace = param.getValueID() + " = { ";
			ArrayList tracenames = new ArrayList();
			for (int j = 0; j < struct.getNumberOfElements(); j++)
			{
				Parameter structparam = struct.getElement(j);
				intrace += getStructParameterTraceString(structparam) + " ";
				outtrace += getStructParameterTraceString(structparam) + " ";
				if (param.getPointerLevel() == 0)
				{
					tracenames.add(param.getValueID() + "." + structparam.getValueID());
				}
				else if (param.getPointerLevel() == 1)
				{
					tracenames.add(param.getValueID() + "->"
							+ structparam.getValueID());
				}
			}

			intrace += "}";
			outtrace += "}";

			if (param.getUsageType() == Parameter.IN)
			{
				this.hasInTrace = true;
				if (this.firstInTrace)
				{
					this.inBuffer += ",";
					this.firstInTrace = false;
				}
				this.inBuffer += intrace;
				for (int j = 0; j < tracenames.size(); j++)
				{
					if (this.inParams.size() > 0)
					{
						this.inParams.add(" " + tracenames.get(j));
					}
					else
					{
						this.inParams.add(tracenames.get(j));
					}
				}
			}
			else if (param.getUsageType() == Parameter.OUT)
			{
				this.hasOutTrace = true;
				if (this.firstOutTrace)
				{
					this.outBuffer += ",";
					this.firstOutTrace = false;
				}
				this.outBuffer += outtrace;
				for (int j = 0; j < tracenames.size(); j++)
				{
					if (this.outParams.size() > 0)
					{
						this.outParams.add(" " + tracenames.get(j));
					}
					else
					{
						this.outParams.add(tracenames.get(j));
					}
				}
			}
			else if (param.getUsageType() == Parameter.INOUT)
			{
				this.hasInTrace = true;
				if (this.firstInTrace)
				{
					this.inBuffer += ",";
					this.firstInTrace = false;
				}
				this.inBuffer += intrace;
				for (int j = 0; j < tracenames.size(); j++)
				{
					if (this.inParams.size() > 0)
					{
						this.inParams.add(" " + tracenames.get(j));
					}
					else
					{
						this.inParams.add(tracenames.get(j));
					}
				}

				this.hasOutTrace = true;
				if (this.firstOutTrace)
				{
					this.outBuffer += ",";
					this.firstOutTrace = false;
				}
				this.outBuffer += outtrace;
				for (int j = 0; j < tracenames.size(); j++)
				{
					if (this.outParams.size() > 0)
					{
						this.outParams.add(" " + tracenames.get(j));
					}
					else
					{
						this.outParams.add(tracenames.get(j));
					}
				}
			}
		}
	}

	private String getStructParameterTraceString(Parameter param)
	{
		String retvalue = "";
		if (param.getTypeName().equalsIgnoreCase("int"))
		{
			retvalue = param.getValueID() + "=%d";
		}
		else if (param.getTypeName().equalsIgnoreCase("char"))
		{
			retvalue = param.getValueID() + "=%c";
		}
		else if (param.getTypeName().equalsIgnoreCase("char*"))
		{
			retvalue = param.getValueID() + "=%s";
		}
		else if (param.getTypeName().equalsIgnoreCase("double"))
		{
			retvalue = param.getValueID() + "=%d";
		}
		else if (param.getTypeName().equalsIgnoreCase("float"))
		{
			retvalue = param.getValueID() + "=%f";
		}
		else if (param.getTypeName().equalsIgnoreCase("unsigned int"))
		{
			retvalue = param.getValueID() + "=%d";
		}
		else if (param.getTypeName().equalsIgnoreCase("signed int"))
		{
			retvalue = param.getValueID() + "=%d";
		}
		else if (param.getTypeName().equalsIgnoreCase("short"))
		{
			retvalue = param.getValueID() + "=%d";
		}
		return retvalue;
	}
}
