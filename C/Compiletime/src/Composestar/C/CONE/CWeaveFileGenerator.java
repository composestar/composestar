/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CWeaveFileGenerator.java,v 1.4 2006/10/23 11:30:45 johantewinkel Exp $
 */
package Composestar.C.CONE;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;
import java.lang.Class;

import Composestar.Core.CONE.WeaveFileGenerator;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

import Composestar.C.LAMA.CFile;
import Composestar.C.LAMA.CMethodInfo;
import Composestar.C.specification.Advice;
import Composestar.C.specification.AdviceApplication;
import  Composestar.C.specification.Aspect;
import Composestar.C.specification.Functions;
import Composestar.C.specification.Pointcut;
import Composestar.C.wrapper.utils.GeneralUtils;
import Composestar.C.wrapper.WeaveblePoint;

import Composestar.Utils.Debug;


/**
 * This class generates the aspects for weavec,
 * An aspect can add header files or add filter advice to super imposed functions
 * 
 * @author Johan te Winkel
 */
public class CWeaveFileGenerator implements WeaveFileGenerator
{
    public static HashMap filterModuleReferenceMap = new HashMap();
	private HashMap aspects= new HashMap();
	private DataStore ds  = DataStore.instance();
	private CFile file;
	private CMethodInfo method;
	private int methodOffset=-1;
    
    /**
     * @roseuid 40EBC2AE0112
     */
    public CWeaveFileGenerator() 
    {
     
    }
    
    public void retrieveFilterModuleReferences(){
    	Iterator concernIter = ds.getAllInstancesOf(CpsConcern.class);
		while(concernIter.hasNext())
		{
			CpsConcern cc = (CpsConcern)concernIter.next();
			Iterator filterModuleIter = cc.getFilterModuleIterator();
			while(filterModuleIter.hasNext())
			{
				String fullname = cc.getName();
				FilterModule fm = new FilterModule();
				fm = (FilterModule)filterModuleIter.next();
				fullname = fullname + "." +fm.getName();
				filterModuleReferenceMap.put(fullname,fm);
			}
		}
    }
 
    public void createAspects() 
    {
    	Aspect asp = null;
    	boolean aspectInConcern;
    	Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Searching for instantiation interceptions...");
				
		Iterator it = ds.getAllInstancesOf(CpsConcern.class);
		retrieveFilterModuleReferences();
		int pointcutNumber=0;		
		it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext()) {
			pointcutNumber++;
			aspectInConcern=false;
			Concern c = (Concern)it.next();
			if(c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
			{
				/** Dit levert de namen van de filtermodules die op dit concern werken op**/
				if(c.getDynObject("SingleOrder") != null && c.getDynObject("SingleOrder") instanceof FilterModuleOrder)
				{
					FilterModuleOrder fmo = (FilterModuleOrder)c.getDynObject("SingleOrder");
					Vector order = fmo.order;
					for(int i=0; i<order.size(); i++)
					{
						String filtermodulename = (String)order.elementAt(i);
						FilterModule fm = (FilterModule)filterModuleReferenceMap.get(filtermodulename);
						
						Debug.out(Debug.MODE_INFORMATION,"cone","FQN: " + fm.getQualifiedName()+  " Concern : " + c.getName() + "has Filtermodule with name: "+fm.getName() + " Number of [m,i,filtertype,internals] : ["  + ((MatchingPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName()+ "," + fm.inputFilters.size()+ "," + ((Filter)fm.inputFilters.elementAt(0)).getFilterType().type + "," + fm.internals.size() + "]" );
						
						if(!filtermodulename.startsWith("CpsDefaultInnerDispatchConcern") ){
							if(aspects.containsKey(fm.getQualifiedName())){ 
								asp = (Aspect)aspects.get(fm.getQualifiedName());
								//aspects.remove(fm.getQualifiedName());
							}
							else{
								asp = new Aspect();
								asp.setId(fm.getQualifiedName());
								aspects.put(fm.getQualifiedName(),asp);
							}
							Filter filter=null;
							Iterator inputfilters = fm.getInputFilterIterator();
							while(inputfilters.hasNext()){
								filter= (Filter)inputfilters.next();
								int numberOfFilterElements =filter.filterElements.size();//.elementAt(0))..getFilterElement(0) //.getMatchingPattern(0).getMatchingParts().size();
								for(int x=0; x<numberOfFilterElements; x++){
									Debug.out(Debug.MODE_INFORMATION,"cone","Other superimposed functions are:" + ((MatchingPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
									HashMap methods= new HashMap(); 
									
									if(c.getPlatformRepresentation() instanceof CFile){
										file= (CFile)c.getPlatformRepresentation();
										if(!((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName().equals("*"))
										{
											if(((CMethodInfo)file.getMethodInfo(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName()))!=null)
											{
												methods.put(((CMethodInfo)file.getMethodInfo(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName())), file);
											}
										}
										else
										{
											Iterator filemethodIt =file.getMethods().iterator();
											while(filemethodIt.hasNext()){
												methods.put((CMethodInfo)filemethodIt.next(),file);		
											}
										}
										/** we cannot overwrite the original parent of the methods, for the 
										 * functions of a defined concern in CConcern.xml
										 * we look if there is a file without any functions 
										 * if this is the case and the signature of the concern does has a
										 * function with the name of the method in the filter
										 * then this is the method where is superimposed on.
										 */
										
										if(methods.size()==0 && file.getMethods().size()==0)
										{
											Iterator methodIterator=c.getSignature().methodByName.m_keys.iterator();
											while(methodIterator.hasNext())
											{
												String methodName=(String)methodIterator.next();
												if(methodName.equals(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName()))
												{
													CMethodInfo mi=(CMethodInfo)((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo();
													//file=(CFile)((CFunction)method.Parent).Parent;
													CFile fl=(CFile)mi.Parent;
													methods.put(mi,fl);
													Debug.out(Debug.MODE_INFORMATION,"cone","Need to weave on function: "+((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo().Name + " in file " + file.FullName +"/"+ file.Name);
												}
												else if("*".equals(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName()))
												{
													CMethodInfo mi=(CMethodInfo)((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo();//(CMethodInfo)((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo();
													//file=(CFile)((CFunction)method.Parent).Parent;
													CFile fl=(CFile)mi.Parent;
													methods.put(mi,fl);
												}
											}
										}
									}	
									/** when there are conditions used, include the file where it is declared**/
									if(filter.getFilterElement(x).getConditionPart()instanceof ConditionExpression)
									{	
										addConditionHeader(((ConditionExpression)filter.getFilterElement(x).getConditionPart()).getDescriptionFileName(), method, file);
									}
									Iterator methodsIter =methods.keySet().iterator();
									methodOffset=-1;
									while(methodsIter.hasNext()){
										methodOffset+=1;
										method =(CMethodInfo)methodsIter.next();
										file=(CFile)methods.get(method);
										Debug.out(Debug.MODE_INFORMATION,"CCONE","Superimposed on file " + file.FullName + " and  function " + method.Name + " size of methods " + methods.size());
										int priority =order.size()-i;
										Pointcut pointcut = createPointcut(filter,c,x,pointcutNumber);
										Advice beforeAdvice =createAdvice(filter,priority,x, "input",c,"before");
										if(beforeAdvice != null){
											AdviceApplication beforeAA = new AdviceApplication();
											beforeAA.setId(filter.getName()+x+method.Name+methodOffset+"before");//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
											asp.addAdvice(beforeAdvice);
											beforeAA.setType(GeneralUtils.getTypeOfAdvice("before"));
											pointcut.addAdviceApplication(beforeAA);	
										}
										
										Advice afterAdvice =createAdvice(filter,priority,x, "input",c,"after");
										if(afterAdvice!=null){
											AdviceApplication afterAA = new AdviceApplication();
											afterAA.setId(filter.getName()+x+method.Name+methodOffset+"after");//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
											asp.addAdvice(afterAdvice);
											afterAA.setType(GeneralUtils.getTypeOfAdvice("after"));
											pointcut.addAdviceApplication(afterAA);	
										}
										
										//pointcut.addAdviceApplication(afterAdvice);
										
										pointcut.setParent(asp);
										asp.addPointcut(pointcut);
										aspectInConcern=true;
										//addSubstitutionTargetHeader(filter, x);
									}
								}
							
							}
							Iterator outputfilters = fm.getOutputFilterIterator();
							while(outputfilters.hasNext()){
								filter= (Filter)outputfilters.next();
								int numberOfFilterElements =filter.filterElements.size();//.elementAt(0))..getFilterElement(0) //.getMatchingPattern().getMatchingParts().size();
								for(int x=0; x<numberOfFilterElements; x++){
									HashMap methods= new HashMap(); 
									
									if(c.getPlatformRepresentation() instanceof CFile){
										file= (CFile)c.getPlatformRepresentation();
										if(!((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName().equals("*"))
										{
											if(((CMethodInfo)file.getMethodInfo(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName()))!=null)
											{
												methods.put(((CMethodInfo)file.getMethodInfo(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName())), file);
											}
										}
										else
										{
											Iterator filemethodIt =file.getMethods().iterator();
											while(filemethodIt.hasNext()){
												methods.put((CMethodInfo)filemethodIt.next(),file);		
											}
										}
										/** we cannot overwrite the original parent of the methods, for the 
										 * functions of a defined concern in CConcern.xml
										 * we look if there is a file without any functions 
										 * if this is the case and the signature of the concern does has a
										 * function with the name of the method in the filter
										 * then this is the method where is superimposed on.
										 */
										
										if(methods.size()==0 && file.getMethods().size()==0)
										{
											Iterator methodIterator=c.getSignature().methodByName.m_keys.iterator();
											while(methodIterator.hasNext())
											{
												String methodName=(String)methodIterator.next();
												if(methodName.equals(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName()))
												{
													CMethodInfo mi=(CMethodInfo)((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo();
													//file=(CFile)((CFunction)method.Parent).Parent;
													CFile fl=(CFile)mi.Parent;
													methods.put(mi,fl);
													Debug.out(Debug.MODE_INFORMATION,"cone","Need to weave on function: "+((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo().Name + " in file " + file.FullName +"/"+ file.Name);
												}
												else if("*".equals(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern().getMatchingParts().elementAt(0))).getSelector().getName()))
												{
													CMethodInfo mi=(CMethodInfo)((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo();//(CMethodInfo)((MethodWrapper)c.getSignature().methodByName.get(methodName)).getMethodInfo();
													//file=(CFile)((CFunction)method.Parent).Parent;
													CFile fl=(CFile)mi.Parent;
													methods.put(mi,fl);
												}
											}
										}
									}	
									/** when there are conditions used, include the file where it is declared**/
									if(filter.getFilterElement(x).getConditionPart()instanceof ConditionExpression)
									{	
										addConditionHeader(((ConditionExpression)filter.getFilterElement(x).getConditionPart()).descriptionFileName, method, file);//updated 31-10 ConditionLiteral 
									}
									Iterator methodsIter =methods.keySet().iterator();
									methodOffset=-1;
									while(methodsIter.hasNext()){
										methodOffset+=1;
										method =(CMethodInfo)methodsIter.next();
										file=(CFile)methods.get(method);
										Debug.out(Debug.MODE_INFORMATION,"CCONE","Superimposed on file " + file.FullName + " and  function " + method.Name + " size of methods " + methods.size());
										int priority =order.size()-i;
										Pointcut pointcut = createPointcut(filter,c,x,pointcutNumber);
										Advice beforeAdvice =createAdvice(filter,priority,x, "output",c,"before");
										if(beforeAdvice != null){
											AdviceApplication beforeAA = new AdviceApplication();
											beforeAA.setId(filter.getName()+x+method.Name+methodOffset+"before");//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
											asp.addAdvice(beforeAdvice);
											beforeAA.setType(GeneralUtils.getTypeOfAdvice("before"));
											pointcut.addAdviceApplication(beforeAA);	
										}
										
										Advice afterAdvice =createAdvice(filter,priority,x, "output",c,"after");
										if(afterAdvice!=null){
											AdviceApplication afterAA = new AdviceApplication();
											afterAA.setId(filter.getName()+x+method.Name+methodOffset+"after");//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
											asp.addAdvice(afterAdvice);
											afterAA.setType(GeneralUtils.getTypeOfAdvice("after"));
											pointcut.addAdviceApplication(afterAA);	
										}			
										pointcut.setParent(asp);
										asp.addPointcut(pointcut);
										aspectInConcern=true;
									}
								}
							}
						}
					}
				}
	
			}
			/** include header file **/
			if(aspectInConcern==true)includeMessageHeader();
		}	
    }
    
    private void includeMessageHeader(){
    	Aspect aspect = new Aspect();
		aspect.setId(file.getUnitName());
		AdviceApplication aa = new AdviceApplication();
		
		aa.setId("header"+file.getUnitName());//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
		aa.setType(GeneralUtils.BEFORE);
		Pointcut pointcut = new Pointcut(file.getUnitName()+".c");
        int type = GeneralUtils.HEADER;
        Functions functions = new Functions();
        functions.setFile(file.getUnitName()+".c");
        functions.setData(file.getUnitName()+".c");
		 
     	functions.setType(type);
     	functions.setParent(pointcut);
     	
		Advice advice = new Advice();
    	int ttype=GeneralUtils.HEADER_INTRODUCTION;
    	advice.setId("header"+file.getUnitName());
    	advice.setType(ttype);
    	advice.setPriority(1);
    	advice.setCode("#include \"message.h\"");
		aspect.addAdvice(advice);
		pointcut.addAdviceApplication(aa);
		pointcut.setParent(aspect);
		pointcut.addFunctions(functions);
		aspect.addPointcut(pointcut);
		aspects.put(file.getUnitName()+".c",aspect);
	}
    
    public Pointcut createPointcut(Filter filter, Concern c, int filterelem, int pointcutNumber){
    	
    	Pointcut pointcut = new Pointcut(method.name()+pointcutNumber);
        Functions functions = new Functions();
        functions.setFile(file.getFullName());
        functions.setData(method.name());
		Debug.out(Debug.MODE_INFORMATION,"CCONE","Function to be superimposed:" + method.name() + " & " + filter.getQualifiedName() );
		int type = GeneralUtils.getTypeForProgramElement("function");
     	if(type ==0)
     	{
     		Debug.out(Debug.MODE_ERROR,"CONE","Unknown program element type: function");
     		System.exit(-1);
     	}
     	functions.setType(type);
     	functions.setParent(pointcut);
     	pointcut.addFunctions(functions);
     	return pointcut;
    }
    
    public Advice createAdvice(Filter filter, int priority, int filterelem, String filtertype, Concern c, String adviceType){
    	String code="";
    	if(filter.getFilterType().getType().equals("Dispatch")){
    		Semantic filterSemantic=new DispatchSemantic();
    		instantiateFilterSemantic(filter, filterelem, c, filterSemantic);
    		if(adviceType.equals("before")&&filterSemantic.beforeAdvice())
    		{	
    			code=filterSemantic.getBeforeAdvice();
				addSubstitutionTargetHeader(filter, filterelem);
    		}
    		else if(adviceType.equals("after")&&filterSemantic.afterAdvice()){
    			code=filterSemantic.getAfterAdvice();
				addSubstitutionTargetHeader(filter, filterelem);
    		}
    		else return null;
    	}
    	else if(filter.getFilterType().getType().equals("Error")){
    		Semantic filterSemantic=new ErrorSemantic();
    		instantiateFilterSemantic(filter, filterelem, c, filterSemantic);
    		if(adviceType.equals("before")&&filterSemantic.beforeAdvice())
    		{	
    				code=filterSemantic.getBeforeAdvice();
    				addAssertHeader();
    		}
    		else if(adviceType.equals("after")&&filterSemantic.afterAdvice()){
    			code=filterSemantic.getAfterAdvice();
    			addAssertHeader();
    		}
    		else return null;
    	}
     	
    	else if(filter.getFilterType().getType().equals("Custom")){
    		if(filter.getFilterType().name.equals("Test")){
        		Semantic filterSemantic=new TestSemantic();
        		instantiateFilterSemantic(filter, filterelem, c, filterSemantic);
        		if(adviceType.equals("before")&&filterSemantic.beforeAdvice())
        		{	
        				code=filterSemantic.getBeforeAdvice();
        				addSubstitutionTargetHeader(filter, filterelem);
        		}
        		else if(adviceType.equals("after")&&filterSemantic.afterAdvice()){
        			code=filterSemantic.getAfterAdvice();
        			addSubstitutionTargetHeader(filter, filterelem);
        		}
        		else return null;
        	}
    		else
    		{
    		try{
					Class cl = Class.forName(filter.getFilterType().getName());//CF.getFilter());//Fully qualified name of class
					Semantic filterSemantic = (Semantic) cl.newInstance();
					instantiateFilterSemantic(filter, filterelem, c, filterSemantic);
					if(!filter.getFilterType().getName().equalsIgnoreCase(filterSemantic.getType()))
    					Debug.out(Debug.MODE_ERROR,"CONE","No Custom filter found with type:"+ filter.getFilterType().getName());
					if(adviceType.equals("before") && filterSemantic.beforeAdvice())
		    		{	
						code=filterSemantic.getBeforeAdvice();
		    			if(filterSemantic.redirectMessage())addSubstitutionTargetHeader(filter, filterelem);
		    			if(filterSemantic.needsHeaderFiles())addHeader(filter,filterelem,filterSemantic.headerFile());
		    		}
		    		else if(adviceType.equals("after")&& filterSemantic.afterAdvice()){
		    			code=filterSemantic.getAfterAdvice();
						if(filterSemantic.redirectMessage())addSubstitutionTargetHeader(filter, filterelem);
						if(filterSemantic.needsHeaderFiles())addHeader(filter,filterelem,filterSemantic.headerFile());
		    		}
		    		else return null;
				}
				catch(ClassNotFoundException e){
					Debug.out(Debug.MODE_ERROR, "CONE", "Class "+filter.getFilterType().getName()+" not found");
				}
				catch(IllegalAccessException e)
				{
					Debug.out(Debug.MODE_ERROR, "CONE", "Class "+filter.getFilterType().getName()+" not found");
				}
	            catch(InstantiationException e)
	            {
	            	Debug.out(Debug.MODE_ERROR, "CONE", "Class "+filter.getFilterType().getName()+" not found");
	            } 
    		}
    	}
    	else if(filter.getFilterType().getType().equals("Substitute")){
    		Semantic filterSemantic=new SubstituteSemantic();
    		instantiateFilterSemantic(filter, filterelem, c, filterSemantic);
    		if(adviceType.equals("before")&&filterSemantic.beforeAdvice())
    		{	
    			code=filterSemantic.getBeforeAdvice();
				addSubstitutionTargetHeader(filter, filterelem);
    		}
    		else if(adviceType.equals("after")&&filterSemantic.afterAdvice()){
    			code=filterSemantic.getAfterAdvice();
				addSubstitutionTargetHeader(filter, filterelem);
    		}
    		else return null;
    	}
    	else if(filter.getFilterType().getType().equals("Meta")){
    		MetaSemantic filterSemantic=new MetaSemantic();
    		instantiateFilterSemantic(filter, filterelem, c, filterSemantic);
    		if(adviceType.equals("before")&&filterSemantic.beforeAdvice())
    		{	
    			code=filterSemantic.getBeforeAdvice();
				addSubstitutionTargetHeader(filter, filterelem);
    		}
    		else if(adviceType.equals("after")&&filterSemantic.afterAdvice()){
    			code=filterSemantic.getAfterAdvice();
				addSubstitutionTargetHeader(filter, filterelem);
    		}
    		else return null;
    	}
    	else Debug.out(Debug.MODE_ERROR,"CONE","Filter type not found: "+filter.getFilterType().getType());
    	Advice advice = new Advice();
    	advice.setCode(code);
    	int ttype=0;
    	if(filtertype.equals("input"))
    		ttype= GeneralUtils.getTypeOfProgramPoint("execution");
    	else if(filtertype.equals("output"))
    		ttype= GeneralUtils.getTypeOfProgramPoint("call");
    	if(ttype == 0)
    	{
    		Debug.out(Debug.MODE_ERROR, "CONE","Undefined join point type: execution");
    		System.exit(-1);
    	}
    	advice.setId(filter.getName()+filterelem+method.name()+methodOffset+adviceType);
    	advice.setType(ttype);
    	advice.setPriority(priority);
    	
    	Debug.out(Debug.MODE_INFORMATION,"CONE","Filter: "+filter.getName() + " sets code: " +code);	 
        return advice;
    }
    
    public void addAssertHeader(){
    	if(!aspects.containsKey(file+"Assert")){
			Aspect aspect=new Aspect();
        	
			aspect.setId(file+"Assert");
			AdviceApplication aa = new AdviceApplication();
		
			aa.setId(file+"Assert");
			aa.setType(GeneralUtils.BEFORE);
			Pointcut pointcut = new Pointcut(file+"Assert");
			int type = GeneralUtils.HEADER;
			Functions functions = new Functions();
			functions.setFile(file.fullname()+".c");
			functions.setData(file.fullname()+".c");
		 
			functions.setType(type);
			functions.setParent(pointcut);
			Advice advice = new Advice();
	    	int ttype=GeneralUtils.HEADER_INTRODUCTION;
	    	advice.setId(file+"Assert");
	    	advice.setType(ttype);
	    	advice.setPriority(1);
	    	advice.setCode("#include <assert.h>");
			aspect.addAdvice(advice);
			pointcut.addAdviceApplication(aa);
			pointcut.setParent(aspect);
			pointcut.addFunctions(functions);
			aspect.addPointcut(pointcut);
			aspects.put(file+"Assert",aspect);
		}
    }
    
    public void addConditionHeader(String condition, CMethodInfo methodInfo, CFile cfile)
    {
    	CFile conditionFile = null;
    	Iterator it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext()) {
			Concern c = (Concern)it.next();
			if(!(c instanceof CpsConcern) && c.getPlatformRepresentation() instanceof CFile)
			{
				conditionFile=(CFile)c.getPlatformRepresentation();
				if((CMethodInfo)conditionFile.getMethodInfo(condition)!=null)
					break;
				else conditionFile =null;
			}
		}
		if(conditionFile==null){
			Debug.out(Debug.MODE_WARNING, "Cone", "No file found where condition is declared: "+condition);
			return;
		}
    	
    	Aspect aspect=new Aspect();
        	
    	aspect.setId(conditionFile+cfile.FullName+"CH");
		AdviceApplication aa = new AdviceApplication();
		
		aa.setId(conditionFile+cfile.FullName+"CH");
		aa.setType(GeneralUtils.BEFORE);
		Pointcut pointcut = new Pointcut(conditionFile+cfile.FullName+"CH");
        int type = GeneralUtils.HEADER;
        Functions functions = new Functions();
        functions.setFile(cfile.fullname()+".c");
        functions.setData(cfile.fullname()+".c");
		 
     	functions.setType(type);
     	functions.setParent(pointcut);
     	
		Advice advice = new Advice();
    	int ttype=GeneralUtils.HEADER_INTRODUCTION;
    	advice.setId(conditionFile+cfile.FullName+"CH");
    	advice.setType(ttype);
    	advice.setPriority(1);
    	advice.setCode("#include \""+ conditionFile.fullname()+".h\"");
		aspect.addAdvice(advice);
		pointcut.addAdviceApplication(aa);
		pointcut.setParent(aspect);
		pointcut.addFunctions(functions);
		aspect.addPointcut(pointcut);
		aspects.put(conditionFile+cfile.FullName+"CH",aspect);
    	
    }
    
    public void addHeader(Filter filter, int filterelem, String filename){
    	Aspect aspect=new Aspect();
    	
    	String matchingFunction=method.name();
    	String matchingFile=file.FullName;
    	Debug.out(Debug.MODE_INFORMATION,"CCONE", "Header added to file:"+ matchingFile + "For function" + matchingFunction );    	
    	aspect.setId(matchingFunction+filename+"STH");
		AdviceApplication aa = new AdviceApplication();
		
		aa.setId(matchingFunction+filename+"STH");//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
		aa.setType(GeneralUtils.BEFORE);
		Pointcut pointcut = new Pointcut(matchingFunction+filename+"STH");
        int type = GeneralUtils.HEADER;
        Functions functions = new Functions();
        functions.setFile(matchingFile+".c");
        functions.setData(matchingFile+".c");
		 
     	functions.setType(type);
     	functions.setParent(pointcut);
     	
		Advice advice = new Advice();
    	int ttype=GeneralUtils.HEADER_INTRODUCTION;
    	advice.setId(matchingFunction+filename+"STH");
    	advice.setType(ttype);
    	advice.setPriority(1);
    	advice.setCode("#include \""+ filename+"\"");
		aspect.addAdvice(advice);
		pointcut.addAdviceApplication(aa);
		pointcut.setParent(aspect);
		pointcut.addFunctions(functions);
		aspect.addPointcut(pointcut);
		aspects.put(matchingFunction+filename+"STH",aspect);
    }
    
    public void addSubstitutionTargetHeader(Filter filter, int filterelem){
    	Aspect aspect=new Aspect();
    	SubstitutionPart sub=(SubstitutionPart)(filter.getFilterElement(filterelem).getMatchingPattern().getSubstitutionParts().elementAt(0));
    	String substitutionFunction=sub.getSelector().name;
    	String matchingFunction=method.name();
    	String matchingFile=file.FullName;
    	
    	CFile file=null;
    	CFile substitutionFile=null;
    	CMethodInfo method=null;
    	Concern c=null;
    	Iterator it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext()) {
			c = (Concern)it.next();
			if(c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern) && c.getPlatformRepresentation() instanceof CFile)
			{
				file=(CFile)c.getPlatformRepresentation();
				method =(CMethodInfo)file.getMethodInfo(substitutionFunction);
				if(method!=null) {
					substitutionFile=file;
					break;
				}
			}
		}
		if(substitutionFile==null){
			Debug.out(Debug.MODE_WARNING, "Cone", "No substitution target found for filter:"+filter.getName());
			return;
		}
		Debug.out(Debug.MODE_INFORMATION,"cone","Aspect:"+ matchingFunction+substitutionFile.getUnitName()+"STH superimposes:"+ substitutionFile.getUnitName());			
    	aspect.setId(matchingFunction+substitutionFile.getUnitName()+"STH");
		AdviceApplication aa = new AdviceApplication();
		
		aa.setId(matchingFunction+substitutionFile.getUnitName()+"STH");//.getFilterElement(0).getMatchingPattern().getMatchingParts().elementAt(0)).getSelector().getName());
		aa.setType(GeneralUtils.BEFORE);
		Pointcut pointcut = new Pointcut(matchingFunction+substitutionFile.getUnitName()+"STH");
        int type = GeneralUtils.HEADER;
        Functions functions = new Functions();
        functions.setFile(matchingFile+".c");
        functions.setData(matchingFile+".c");
		 
     	functions.setType(type);
     	functions.setParent(pointcut);
     	
		Advice advice = new Advice();
    	int ttype=GeneralUtils.HEADER_INTRODUCTION;
    	advice.setId(matchingFunction+substitutionFile.getUnitName()+"STH");
    	advice.setType(ttype);
    	advice.setPriority(1);
    	advice.setCode("#include \""+ substitutionFile.getUnitName()+ ".h\"");
		aspect.addAdvice(advice);
		pointcut.addAdviceApplication(aa);
		pointcut.setParent(aspect);
		pointcut.addFunctions(functions);
		aspect.addPointcut(pointcut);
		aspects.put(matchingFunction+substitutionFile.getUnitName()+"STH",aspect);
    }
    
    public void instantiateFilterSemantic(Filter filter,int filterelem,Concern c, Semantic filterSemantic){
    	filterSemantic.setFilter(filter);
		filterSemantic.setElementNumber(filterelem);
		filterSemantic.setConcern(c);
		filterSemantic.setMethod(method);
		filterSemantic.setFile(file);
    }
    
    public void run(CommonResources resources) throws ModuleException 
	{
      createAspects();      
      WeaverEngine we = new WeaverEngine();
      Hashtable functionsToRealWeavebaleObjectsMap = we.populateFunctionsWithRealInfo(aspects, resources);
      HashSet weavebleobjects = we.attachAdvicesToFunctions(functionsToRealWeavebaleObjectsMap);
      /**Tracing is switched off**/
      //we.processAllInternalAdvices(weavebleobjects);
      Iterator weaveit = weavebleobjects.iterator();
  	  while(weaveit.hasNext())
  	  {
  			we.weaveInstructionsForPoint((WeaveblePoint)weaveit.next());
  	  }
  	  we.emitFiles();
    }
}
