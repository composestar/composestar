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
 * $Id: WeaverEngine.java,v 1.3 2006/10/15 08:54:33 johantewinkel Exp $
 */
package Composestar.C.CONE;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.regex.Pattern;

import Composestar.C.MASTER.*;
import Composestar.C.specification.*;
import Composestar.C.wrapper.*;
import Composestar.C.wrapper.parsing.WrappedAST;
import Composestar.C.wrapper.utils.GeneralUtils;
import Composestar.C.wrapper.utils.StaticVariableReplacer;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.C.ASPECTS.TRACING.TracingAspect;
import Composestar.Utils.Debug;

public class WeaverEngine
{
	private ArrayList cfiles = new ArrayList();
	private String filename = null;
	public static int NUMWS = 0;
	   
	
	public Hashtable populateFunctionsWithRealInfo(HashMap aspectMap, CommonResources resources)
	{
		Debug.out(Debug.MODE_INFORMATION,"CONE","Analyzing files...");
		FileMap fm= FileMap.instance();
		Hashtable fileASTMap = fm.getFileASTs();
		Hashtable functionsToRealWeavebaleObjectsMap = new Hashtable();
		
		Iterator aspectit = aspectMap.values().iterator();
		while(aspectit.hasNext())
		{
			Aspect aspect = (Aspect)aspectit.next();
			
			for(int i=0; i<aspect.getNumberOfPointcuts(); i++)
			{
				Pointcut pc = aspect.getPointcut(i);
				for(int j=0; j<pc.getNumberOfFunctions(); j++)
				{
					Functions funcs = pc.getFunctions(j);
					
					if(funcs.getFile().equalsIgnoreCase("*.c")) 
					{
						Iterator keyit = fileASTMap.values().iterator();
						ArrayList list = new ArrayList();
						while(keyit.hasNext())
						{
							retrieveAST wrapper = (retrieveAST)keyit.next();
							if(funcs.getType() == GeneralUtils.FUNCTION)
							{
								Iterator funcit = wrapper.getFunctions().iterator();
								while(funcit.hasNext())
								{
									Function function = (Function)funcit.next();
									String mod = StaticVariableReplacer.replaceStaticVariables(funcs.getData(),function);
									if(Pattern.matches(mod,function.getName()))
									{
										list.add(function);
									}
								}
							}
							else if(funcs.getType() == GeneralUtils.STRUCT)
							{
								String mod = StaticVariableReplacer.replaceStaticVariables(funcs.getData(),wrapper.getFilename());
								if(wrapper.structASTMap.containsKey(mod))
								{
									Struct struct = (Struct)wrapper.structASTMap.get(mod);
									list.add(struct);
								}
							}
							else if(funcs.getType() == GeneralUtils.GLOBAL)
							{
								list.add(wrapper.introductionPoint);
							}
							else if(funcs.getType() == GeneralUtils.HEADER)
							{
								list.add(wrapper.headerintroductionPoint);
							}
						}
						list = this.restrictFunctions(funcs,list);
						functionsToRealWeavebaleObjectsMap.put(funcs,list);
					}
					else // bla.c methodebla
					{
						Iterator keyit = fileASTMap.values().iterator();
						ArrayList list = new ArrayList();
						while(keyit.hasNext())
						{
							retrieveAST wrapper = (retrieveAST)keyit.next();
							if(funcs.getType() == GeneralUtils.FUNCTION)
							{
								if(wrapper.getFilename().indexOf(funcs.getFile()) > 0)
								{
									Iterator funcit = wrapper.getFunctions().iterator();
									while(funcit.hasNext())
									{
										Function function = (Function)funcit.next();
										String mod = StaticVariableReplacer.replaceStaticVariables(funcs.getData(),function);
										if(Pattern.matches(mod,function.getName()))
										{
											list.add(function);
										}
									}
								}
							}
							else if(funcs.getType() == GeneralUtils.STRUCT)
							{
								String mod = StaticVariableReplacer.replaceStaticVariables(funcs.getData(),wrapper.getFilename());
								if(wrapper.structASTMap.containsKey(mod))
								{
									Struct struct = (Struct)wrapper.structASTMap.get(mod);
									list.add(struct);
								}
							}
							else if(funcs.getType() == GeneralUtils.GLOBAL)
							{
								list.add(wrapper.introductionPoint);
							}
							else if(funcs.getType() == GeneralUtils.HEADER)
							{
								list.add(wrapper.headerintroductionPoint);
							}
						}
						list = this.restrictFunctions(funcs,list);
						functionsToRealWeavebaleObjectsMap.put(funcs,list);
					}
				}
			}
		}
		return functionsToRealWeavebaleObjectsMap;
	}
	
	public ArrayList restrictFunctions(Functions funcs, ArrayList funclist)
	{
		//this.printList(funclist);
		ArrayList restrictedList = new ArrayList(funclist);
		if(funcs.getType() != GeneralUtils.FUNCTION)
			return restrictedList;
		Iterator funcit = funclist.iterator();
		if(funcs.hasReturnSpec())
		{
			while(funcit.hasNext())
			{
				Object obj = funcit.next();
				if(obj instanceof Function)
				{
					Function func = (Function)obj;
					if(func.hasReturnType(funcs.getReturnType()))
					{
						if(funcs.isInvertReturnType())
						{
							//Return type matches and we have a not, thus remove
							restrictedList.remove(func);
						}
					}
					else
					{
						if(!funcs.isInvertReturnType())
						{
							restrictedList.remove(func);
						}
					}
				}
			}
		}
		if(!restrictedList.isEmpty() && funcs.hasParamSpec())
		{
			ArrayList toBeRemoved = new ArrayList();
			for(int i=0; i<funcs.getNumberOfParameters(); i++)
			{
				XMLParameter param = funcs.getParameter(i);
				funcit = restrictedList.iterator();
				while(funcit.hasNext())
				{
					Object obj = funcit.next();
					if(obj instanceof Function)
					{
						Function func = (Function)obj;
						if(param.getName().equals("*")) 
						{ // for all param names, thus ok
							if(func.hasParameterWithType(param.getType()))
							{
								if(param.isInvert())
									toBeRemoved.add(func);
							}
							else
							{
								if(!param.isInvert())
									toBeRemoved.add(func);
							}
						}
						else
						{ // check if function name matches
							if(func.hasParameterWithName(param.getName()))
							{
								if(func.hasParameterWithType(param.getType())) // 1 1 = 1
								{
									if(param.isInvert())
										toBeRemoved.add(func);
								}
								else // 1 0 = 0
								{
									if(!param.isInvert())
										toBeRemoved.add(func);
								}
							}
							else
							{
								if(!param.isInvert())
									toBeRemoved.add(func);
							}
						}
					}
				}
			}
			funcit = toBeRemoved.iterator();
			while(funcit.hasNext())
			{
				restrictedList.remove(funcit.next());
			}
			
		}
		Debug.out(Debug.MODE_INFORMATION,"CONE","Restricted functions for["+funcs.getParent().getId()+"]: "+funcs.getFile()+":"+funcs.getData()+" == "+restrictedList.size());		
		return restrictedList;
	}
	
	public HashSet attachAdvicesToFunctions(Hashtable functionsToRealWeavebaleObjectsMap)
    {
    	// funcs --> function/struct 
    	HashSet weavebleobjects = new HashSet();
    	Iterator funcit = functionsToRealWeavebaleObjectsMap.keySet().iterator();
    	while(funcit.hasNext())
    	{
    		Functions funcs = (Functions)funcit.next();
    		Debug.out(Debug.MODE_INFORMATION,"CONE","Evaluating advice: "+funcs.getParent().getParent().getId() );
    		if(funcs.getType() == GeneralUtils.FUNCTION)
    		{
    			ArrayList list = (ArrayList)functionsToRealWeavebaleObjectsMap.get(funcs);
	    		for(int i=0; i<list.size(); i++)
	    		{
	    			Function func = (Function)list.get(i);
	    				for(int j=0; j< funcs.getParent().getNumberOfAdviceApplications(); j++)
	    			{
	    				AdviceApplication aa = (AdviceApplication)funcs.getParent().getAdviceApplication(j);
	    				Hashtable defadvices = funcs.getParent().getParent().getAdvices();
	    				Advice adv = (Advice)defadvices.get(aa.getId());
	    				
	    				if(adv.getType() == GeneralUtils.FUNCTION_INTRODUCTION
	    						|| adv.getType() == GeneralUtils.FUNCTION_CALL
	    						|| adv.getType() == GeneralUtils.FUNCTION_BODY )
	    				{
	    					func.addWeavingInstruction(new WeavingInstruction(func,adv,aa));
	    					NUMWS++;
	    					weavebleobjects.add(func);
	    					}
	    			}
	    		}
    		}
    		else if(funcs.getType() == GeneralUtils.STRUCT)
    		{
    			ArrayList list = (ArrayList)functionsToRealWeavebaleObjectsMap.get(funcs);
	    		for(int i=0; i<list.size(); i++)
	    		{
	    			Struct struct = (Struct)list.get(i);
	    			for(int j=0; j< funcs.getParent().getNumberOfAdviceApplications(); j++)
	    			{
	    				AdviceApplication aa = (AdviceApplication)funcs.getParent().getAdviceApplication(j);
	    				Hashtable defadvices = funcs.getParent().getParent().getAdvices();
	    				Advice adv = (Advice)defadvices.get(aa.getId());
	    				if(adv.getType() == GeneralUtils.STRUCTURE_INTRODUCTION)
	    				{
	    					struct.addWeavingInstruction(new WeavingInstruction("",adv,aa));
	    					NUMWS++;
	    					weavebleobjects.add(struct);
	    				}
	    			}
	    		}
    		}
    		else if(funcs.getType() == GeneralUtils.GLOBAL)
    		{
    			ArrayList list = (ArrayList)functionsToRealWeavebaleObjectsMap.get(funcs);
	    		for(int i=0; i<list.size(); i++)
	    		{
	    			GlobalIntroductionPoint gip = (GlobalIntroductionPoint)list.get(i);
	    			for(int j=0; j< funcs.getParent().getNumberOfAdviceApplications(); j++)
	    			{
	    				AdviceApplication aa = (AdviceApplication)funcs.getParent().getAdviceApplication(j);
	    				Hashtable defadvices = funcs.getParent().getParent().getAdvices();
	    				Advice adv = (Advice)defadvices.get(aa.getId());
	    				if(adv.getType() == GeneralUtils.GLOBAL_INTRODUCTION)
	    				{
	    					gip.addWeavingInstruction(new WeavingInstruction("",adv,aa));
	    					NUMWS++;
	    					weavebleobjects.add(gip);
	    				}
	    			}
	    		}
    		}
    		else if(funcs.getType() == GeneralUtils.HEADER)
    		{
    			ArrayList list = (ArrayList)functionsToRealWeavebaleObjectsMap.get(funcs);
	    		for(int i=0; i<list.size(); i++)
	    		{
	    			HeaderIntroductionPoint hip = (HeaderIntroductionPoint)list.get(i);
	    			for(int j=0; j< funcs.getParent().getNumberOfAdviceApplications(); j++)
	    			{
	    				AdviceApplication aa = (AdviceApplication)funcs.getParent().getAdviceApplication(j);
	    				Hashtable defadvices = funcs.getParent().getParent().getAdvices();
	    				Advice adv = (Advice)defadvices.get(aa.getId());
	    				if(adv.getType() == GeneralUtils.HEADER_INTRODUCTION)
	    				{
	    					hip.addWeavingInstruction(new WeavingInstruction("",adv,aa));
	    					NUMWS++;
	    					weavebleobjects.add(hip);
	    				}
	    			}
	    		}
    		}
    	}
    	Debug.out(Debug.MODE_INFORMATION,"CCONE","weaveinstructions: "+weavebleobjects);
    	return weavebleobjects;
    }
	
	public void processAllInternalAdvices(HashSet weavebleobjects)
    {
		FileMap fm= FileMap.instance();
		Hashtable fileASTMap = fm.getFileASTs();
		Iterator wrapit = fileASTMap.values().iterator();
    	TracingAspect ta = new TracingAspect();
    	while(wrapit.hasNext())
    	{
    		retrieveAST wrapper = (retrieveAST)wrapit.next();
    		HashSet set = ta.weave(wrapper);
    		if(set != null)
    			weavebleobjects.addAll(set);
    	}
    }
	public void weaveInstructionsForPoint(WeaveblePoint wp)
    {
    	FileMap fm= FileMap.instance();
		Hashtable fileASTMap = fm.getFileASTs();
		Hashtable test= fm.getFileASTs();
		Enumeration ittest= test.keys();
		ArrayList wis = wp.prioritizeWeavingInstructions();
		Iterator wisit = wis.iterator();
		while(wisit.hasNext())
		{
			WeavingInstruction wi = (WeavingInstruction)wisit.next();
			
			if(wi.getCode() == null)
	        {
	        	Debug.out(Debug.MODE_WARNING,"CONE","WARNING: no code to weave, skipping...");
	        	return;
	        }
	    	
	    	switch(wi.getType())
	        {
	            case WeavingInstruction.FUNCTION_EXECUTION_BEFORE:
	            {
	                Function func = wi.getFunction();
	                 String filename = objectToFileName(func.getFileName(),fm);
	                
	                retrieveAST cwrapper = (retrieveAST)fileASTMap.get(filename);
	                WrappedAST wast = cwrapper.getWrappedAST();
		            wast.weaveEntryFunction(func, wi.getCode());
		             break;
	            }
	            case WeavingInstruction.FUNCTION_EXECUTION_AFTER:
	            {
	            	Function func = wi.getFunction();
	                String filename = objectToFileName(func.getFileName(),fm);
	                System.out.println("Weaving on function execution after: "+func.getFileName()+":"+func.getName()+ ": " +wi.getAdvice().getId()+ "= this different then:" + filename);
	                
	                retrieveAST cwrapper = (retrieveAST)fileASTMap.get(filename);
	                WrappedAST wast = cwrapper.getWrappedAST();
	                
	                wast.weaveExitFunction(func, wi.getCode());
		            break;
	            }
		        case WeavingInstruction.FUNCTION_CALL_BEFORE:
	            {
		        	Function func = wi.getFunction();
	                Iterator fileit = fileASTMap.values().iterator();
	                while(fileit.hasNext())
	                {
		            	retrieveAST cwrapper = (retrieveAST)fileit.next();
	                	cwrapper.getWrappedAST().weaveGlobalFunctionCallBefore(wi.getCode(),func.getName());
	                }
		            break;
	            }
	            case WeavingInstruction.FUNCTION_CALL_AFTER:
	            {
	            	Function func = wi.getFunction();
	                Iterator fileit = fileASTMap.values().iterator();
	                while(fileit.hasNext())
	                {
		            	retrieveAST cwrapper = (retrieveAST)fileit.next();
	                	cwrapper.getWrappedAST().weaveGlobalFunctionCallAfter(wi.getCode(),func.getName());
	                }
		            break;
	            }
	            case WeavingInstruction.GLOBAL_INTRODUCTION_BEFORE:
	            case WeavingInstruction.GLOBAL_INTRODUCTION_AFTER:
	            {
	            	
	            	GlobalIntroductionPoint gip = (GlobalIntroductionPoint)wp;
	            	String completeName = fm.getFileASTwithName(gip.getFileName());
		            retrieveAST cwrapper = (retrieveAST)fileASTMap.get(completeName);
	                WrappedAST wast = cwrapper.getWrappedAST();
	                wast.weaveGlobalIntroduction(wi.getCode(),gip);
		            break;
	            }
	            case WeavingInstruction.HEADER_INTRODUCTION_BEFORE:
	            case WeavingInstruction.HEADER_INTRODUCTION_AFTER:
	            {
	            	 
	            	HeaderIntroductionPoint hip = (HeaderIntroductionPoint)wp;
	            	
	            	String completeName = fm.getFileASTwithName(hip.getFileName());
	                 
	            	retrieveAST cwrapper = (retrieveAST)fileASTMap.get(completeName);
	                WrappedAST wast = cwrapper.getWrappedAST();
	                
	                wast.weaveHeaderIntroduction(wi.getCode(),hip);
		            break;
	            }
	            case WeavingInstruction.STRUCTURE_INTRODUCTION_BEFORE:
	            case WeavingInstruction.STRUCTURE_INTRODUCTION_AFTER:
	            {
	            	Struct struct = (Struct)wp; 
	            	retrieveAST cwrapper = (retrieveAST)fileASTMap.get(struct.getFileName());
	                WrappedAST wast = cwrapper.getWrappedAST();
	                wast.weaveStructureIntroduction(wi.getCode(), (Struct)wp);
		            break;
	            }
	            case WeavingInstruction.FUNCTION_INTRODUCTION_AFTER:
	            case WeavingInstruction.FUNCTION_INTRODUCTION_BEFORE:
	            {
	            	Function func = wi.getFunction();
	             	retrieveAST cwrapper = (retrieveAST)fileASTMap.get(func.getFileName());
	                WrappedAST wast = cwrapper.getWrappedAST();
	                wast.weaveFunctionIntroduction(func, wi.getCode());
	                break;
	            }
	            default:
	            {
	                Debug.out(Debug.MODE_ERROR,"CONE","The specified weaving operation is not supported, exciting...");
	            	System.exit(-1);
	            }
	        }
		}
    }
	
	 public void emitFiles()
	    {
		 	String tempFolder= Configuration.instance().getPathSettings().getPath("Output");
	    	FileMap fm= FileMap.instance();
			Hashtable fileASTMap = fm.getFileASTs();
				
			Iterator fileit = fileASTMap.keySet().iterator();
	    	while(fileit.hasNext())
	    	{
	    		String file = (String)fileit.next();
	    		WrappedAST wast = ((retrieveAST)fileASTMap.get(file)).getWrappedAST();
	    		if(file.lastIndexOf(File.separator) > 0)
	    		{
	    			file = file.substring(file.lastIndexOf(File.separator)+1)+"out";
	    		}
	    		Debug.out(Debug.MODE_INFORMATION,"CONE","Writing file: "+tempFolder+file+"...");
	    		try
	    		{
	    			wast.emiteToFile(tempFolder+file);
	    		}
	    		catch(Exception e)
	    		{
	    			Debug.out(Debug.MODE_ERROR,"CONE","An error occured while writing the output file...");
	    			e.printStackTrace();
	    		}
	    	}
	    }
	
	private String objectToFileName(String object, FileMap fm){
		Hashtable test= fm.getFileASTs();
		Iterator ittest= test.values().iterator();
		while(ittest.hasNext()){
			retrieveAST cw=(retrieveAST)ittest.next();
			if(object.equals(cw.objectname))
				return (cw.getFilename()).replace('\\','\\');
		}
		return null;
	}
}
	