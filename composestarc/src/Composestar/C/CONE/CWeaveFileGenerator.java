/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CWeaveFileGenerator.java,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */
package Composestar.C.CONE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

import org.w3c.dom.Element;

import Composestar.Core.CONE.WeaveFileGenerator;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;

import Composestar.Core.FILTH.FILTHService;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.C.LAMA.CFile;
import Composestar.C.LAMA.CMethodInfo;
import Composestar.C.LAMA.CParameterInfo;
import Composestar.C.specification.Advice;
import Composestar.C.specification.AdviceApplication;
import  Composestar.C.specification.Aspect;
import Composestar.C.specification.Functions;
import Composestar.C.specification.Pointcut;
import Composestar.C.wrapper.utils.GeneralUtils;
import Composestar.C.wrapper.WeaveblePoint;
import Composestar.Utils.Debug;


/**
 * This class generates the interception specification file for ILICIT based on 
 * information in the repository.
 * 
 * @author Sverre Boschman
 */
public class CWeaveFileGenerator implements WeaveFileGenerator
{
    private PrintWriter out = null;
    public static HashMap filterModuleReferenceMap = new HashMap();
	private HashMap aspects= new HashMap();
	private DataStore ds  = DataStore.instance();
    
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
				//System.out.println("FilterModule: "+ fullname + " has filter: " + fm.getName() );
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
			//Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Found Concern with name["+c.getName()+"]: " + c.getQualifiedName());
			if(c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
			{
				/** Dit levert de namen van de filtermodules die op dit concern werken op**/
				if(c.getDynObject("SingleOrder") != null && c.getDynObject("SingleOrder") instanceof FilterModuleOrder)
				{
					FilterModuleOrder fmo = (FilterModuleOrder)c.getDynObject("SingleOrder");
					Vector order = fmo._order;
					for(int i=0; i<order.size(); i++)
					{
						String filtermodulename = (String)order.elementAt(i);
						FilterModule fm = (FilterModule)filterModuleReferenceMap.get(filtermodulename);
						
						Debug.out(Debug.MODE_INFORMATION,"cone","FQN: " + fm.getQualifiedName()+  " Concern : " + c.getName() + "has Filtermodule with name: "+fm.getName() + " Number of [c,m,i,filtertype,internals] : [" + fm.conditions.size() + "," + ((MatchingPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName()+ "," + fm.inputFilters.size()+ "," + ((Filter)fm.inputFilters.elementAt(0)).getFilterType().type + "," + fm.internals.size() + "]" );
						
						if(!fm.getQualifiedName().equals("CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule")){
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
									//System.out.println("Other superimposed functions are:" + ((MatchingPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(x).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());
									/*** concern has to contain the function**/
									CFile file= (CFile)c.getPlatformRepresentation();
									CMethodInfo method =(CMethodInfo)file.getMethod(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName());
									/****/
									if(method != null){
										AdviceApplication aa = new AdviceApplication();
										//aa.setId(((Filter)fm.inputFilters.elementAt(0)).getFilterType().getType());
										aa.setId(filter.getName()+x+((MatchingPart)(filter.getFilterElement(x).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName());//.getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());//.getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());
				            	
										aa.setType(GeneralUtils.getTypeOfAdvice("before"));
				            
										Pointcut pointcut = createPointcut(filter,c,x,pointcutNumber);
										pointcut.addAdviceApplication(aa);
										pointcut.setParent(asp);
										asp.addPointcut(pointcut);
										int priority =order.size()-i;
										Advice advice =createAdvice(filter,priority,x, "input",c);
										asp.addAdvice(advice);
										System.out.println("Pointcuts: "+pointcut.getId()+" "+ aa.getId()+ " "+ advice.getId()+  " "+ advice.getCode());
										
										aspectInConcern=true;
										//addSubstitutionTargetHeader(filter, x);
									}
								}
							
							}
							Iterator outputfilters = fm.getOutputFilterIterator();
							while(outputfilters.hasNext()){
								filter= (Filter)outputfilters.next();
								//aa.setId(((Filter)fm.inputFilters.elementAt(0)).getFilterType().getType());
								
								int numberOfFilterElements =filter.filterElements.size();//.elementAt(0))..getFilterElement(0) //.getMatchingPattern(0).getMatchingParts().size();
								for(int x=0; x<numberOfFilterElements; x++){
									//System.out.println("Other superimposed functions are:" + ((MatchingPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(x).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());
									/*****/
									CFile file= (CFile)c.getPlatformRepresentation();
									CMethodInfo method =(CMethodInfo)file.getMethod(((MatchingPart)(filter.getFilterElement(x).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName());
									/****/
									if(method != null){
									
										AdviceApplication aa = new AdviceApplication();
									
										aa.setId(filter.getName()+x+((MatchingPart)(filter.getFilterElement(x).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName());//.getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());
										aa.setType(GeneralUtils.getTypeOfAdvice("before"));
				            
										Pointcut pointcut = createPointcut(filter,c,x,pointcutNumber);
										pointcut.addAdviceApplication(aa);
										pointcut.setParent(asp);
										asp.addPointcut(pointcut);
										int priority =order.size()-i;
										Advice advice =createAdvice(filter,priority,x, "output",c);
										asp.addAdvice(advice);
										aspectInConcern=true;
									}
								}
							}
							
				            //if(!asp.aspectIsSane())
			        		//{
			        		//	System.out.println("Advice reference in aspect: "+asp.getId()+" can not be resolved...");
			            	//	System.exit(-1);
			        		//}
							
							out.println("<aspect id=\""+ fm.getName() +"\">");
							//out.println("<pointcut id=\""+ ((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).matchingPart.selector.getName()+"\">");
							//out.println("<elements files=\""+c.getName()+".c\" identifier=\"function\" data=\""+((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).matchingPart.selector.getName() +"\"/>");
							out.println("<advices><adviceapplication id=\""+((Filter)fm.inputFilters.elementAt(0)).getFilterType().type+"\" type=\"before\"/></advices>");
							out.println("</pointcut>");
							//out.println("<advice id=\""+ ((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).matchingPart.selector.getName()+"\" type=\"execution\" priority=\""+(order.size()-i)+"\">");
							out.println("<code><![CDATA[");
							//((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).getSubstitutionPart();
							if(!((Filter)fm.inputFilters.elementAt(0)).getFilterType().type.equals("Custom") && !((Filter)fm.inputFilters.elementAt(0)).getFilterType().type.equals("Error")){
								out.println("if("+1+"){"+((SubstitutionPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).getSubstitutionParts().elementAt(0)).getSelector().getName()+"();}");
							}
							out.println("]]></code>");
							out.println("</advice></aspect>");
													
							
						}
					}
				}
	
			}
			/** include header file **/
			if(aspectInConcern==true)includeMessageHeader(c);
		}	
    }
    
    private void includeMessageHeader(Concern c){
    	CFile file=(CFile)c.getPlatformRepresentation();
    	
    	Aspect aspect = new Aspect();
		aspect.setId(file.getUnitName());
		AdviceApplication aa = new AdviceApplication();
		
		aa.setId("header"+file.getUnitName());//.getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());
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
    	
    	Pointcut pointcut = new Pointcut(((MatchingPart)(filter.getFilterElement(filterelem).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName()+pointcutNumber);
        Functions functions = new Functions();
        functions.setFile(c.getName());
        functions.setData(((MatchingPart)(filter.getFilterElement(filterelem).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName());
		//System.out.println("Function to be superimposed:" + ((MatchingPart)(((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName() + " & " + fm.getQualifiedName() );
		//System.out.println("FQN: " + fm.getQualifiedName()+  " Concern : " + c.getName() + "has Filtermodule with name: "+fm.getName() + " Number of [c,m,i,filtertype,internals] : [" + fm.conditions.size() + "," + ((MatchingPart)((Filter)fm.inputFilters.elementAt(0)).getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName()+ "," + fm.inputFilters.size()+ "," + ((Filter)fm.inputFilters.elementAt(0)).getFilterType().type + "," + fm.internals.size() + "]" );
        int type = GeneralUtils.getTypeForProgramElement("function");
     	if(type ==0)
     	{
     		Debug.out(Debug.MODE_ERROR,"CONE","Unknown program element type: function");
     		System.exit(-1);
     	}
     	functions.setType(type);
     	functions.setParent(pointcut);
     	
     	/**TODO: if we want to select functions based 
     	 * on there parameters or returntype the following
     	 * functions can be used:
     	functions.setReturnType();
     	functions.addParameter()**/
     	
     	pointcut.addFunctions(functions);
     	return pointcut;
    }
    
    public Advice createAdvice(Filter filter, int priority, int filterelem, String filtertype, Concern c){
    	Advice advice = new Advice();
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
    	advice.setId(filter.getName()+filterelem+((MatchingPart)(filter.getFilterElement(filterelem).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName());
    	advice.setType(ttype);
    	advice.setPriority(priority);
    	String code="";
    	if(filter.getFilterType().getType().equals("Dispatch")){
    		Semantic filterSemantic=new DispatchSemantic(filter,filterelem,c);
    		code=filterSemantic.retrieveSemantics();
			addSubstitutionTargetHeader(filter, filterelem);
    	}
    	else code="int"+ filter.getFilterType().getType()+ ";";
    	advice.setCode(code);
    	Debug.out(Debug.MODE_INFORMATION,"CONE","Filter: "+filter.getName());
		 
        return advice;
    }
    
    public void addSubstitutionTargetHeader(Filter filter, int filterelem){
    	Aspect aspect=new Aspect();
    	SubstitutionPart sub=(SubstitutionPart)(filter.getFilterElement(filterelem).getMatchingPattern(0).getSubstitutionParts().elementAt(0));
    	String substitutionFunction=sub.getSelector().name;
    	String matchingFunction=((MatchingPart)(filter.getFilterElement(filterelem).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName();
    	String matchingFile=((MatchingPart)(filter.getFilterElement(filterelem).getMatchingPattern(0).getMatchingParts().elementAt(0))).getTarget().getName();
    	
    	CFile file=null;
    	CFile substitutionFile=null;
    	CMethodInfo method=null;
    	Concern c=null;
    	Iterator it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext()) {
			c = (Concern)it.next();
			if(c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
			{
				file=(CFile)c.getPlatformRepresentation();
				method =(CMethodInfo)file.getMethod(substitutionFunction);
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
		System.out.println("Aspect:"+ matchingFunction+substitutionFile.getUnitName()+"STH superimposes:"+ substitutionFile.getUnitName());			
    	aspect.setId(matchingFunction+substitutionFile.getUnitName()+"STH");
		AdviceApplication aa = new AdviceApplication();
		
		aa.setId(matchingFunction+substitutionFile.getUnitName()+"STH");//.getFilterElement(0).getMatchingPattern(0).getMatchingParts().elementAt(0)).getSelector().getName());
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
    
    /**
     * @param destination
     * @param store
     * @param resources
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 40EBC2AE0344
     */
    public void run(CommonResources resources) throws ModuleException 
	{
    	File destination = new File(Configuration.instance().getPathSettings().getPath("Base") + "weavespec.xml");
    	
      Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Writing weave specifications to file '" + destination.getName() + "'...");
      
      try
      {
        out = new PrintWriter(new BufferedWriter(new FileWriter(destination)));

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
 
        createAspects();

        out.flush();
        out.close();
        
        
      }
      catch (IOException e) {
        throw new ModuleException("Unable to create weave specification file '" + destination + "'.","CONE_IS");
      }
      catch (Exception e) {
        throw new ModuleException("Unhandled exception: " + e.getClass().toString() + ";"+ e.getMessage(),"CONE_IS");
      } 
      
//    Last piece of WeaveC weaver.java
      WeaverEngine we = new WeaverEngine();
      Hashtable functionsToRealWeavebaleObjectsMap = we.populateFunctionsWithRealInfo(aspects, resources);
      HashSet weavebleobjects = we.attachAdvicesToFunctions(functionsToRealWeavebaleObjectsMap);
      //we.processAllInternalAdvices(weavebleobjects);
      Iterator weaveit = weavebleobjects.iterator();
  	  while(weaveit.hasNext())
  	  {
  			we.weaveInstructionsForPoint((WeaveblePoint)weaveit.next());
  	  }
  	  we.emitFiles();
    }
    
   
    
    class MethodInformation {
        private String mClassName;
        private String mMethodName;
        
        /**
         * @param className
         * @param methodName
         * @roseuid 40EBC2C9001B
         */
        public MethodInformation(String className, String methodName) {
        mClassName = className;
        mMethodName = methodName;
        }
        
        /**
         * @return java.lang.String
         * @roseuid 40EBC2C9003B
         */
        public String getClassName() {
        return mClassName;
        }
        
        /**
         * @return java.lang.String
         * @roseuid 40EBC2C9005A
         */
        public String getMethodName() {
        return mMethodName;
        }
    }
    
   
}
