/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETWeaveFileGenerator.java,v 1.1 2006/02/16 23:10:57 pascal_durr Exp $
 */
package Composestar.C.CONE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import Composestar.Core.CONE.WeaveFileGenerator;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;

import Composestar.Core.FILTH.FILTHService;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
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
    private String repository = "repository.xml";
    private int debugLevel = 0;
    private String application = "";
    
    /**
     * @roseuid 40EBC2AE0112
     */
    public CWeaveFileGenerator() 
    {
     
    }
       
    /**
     * @roseuid 40EBC2AE0121
     */
    private void writeMethodDefinitions() 
    {
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Writing method definitions block...");
        out.println("<methods>");
        
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Inserting method definition for 'handleApplicationStart'.");     
        out.println("<method id=\"application_start\" assembly=\"ComposeStarDotNETRuntimeInterpreter\" class=\"Composestar.RuntimeDotNET.FLIRT.DotNETMessageHandlingFacility\" name=\"handleDotNETApplicationStart\">");
    	out.println("<argument value=\"" + repository + "\" type=\"string\"/>");
    	out.println("<argument value=\"" + debugLevel + "\" type=\"int\"/>");
    	out.println("</method>");
    	
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Inserting method definition for 'handleInstanceCreation'.");     
    	out.println("<method id=\"after_instantiation\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleInstanceCreation\">");
    	out.println("<argument value=\"%senderobject\"/>");
    	out.println("<argument value=\"%createdobject\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
    	out.println("</method>");

      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Inserting method definition for 'handleVoidMethodCall'.");     
    	out.println("<method id=\"invocation_void\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleVoidMethodCall\">");
    	out.println("<argument value=\"%senderobject\"/>");
    	out.println("<argument value=\"%targetobject\"/>");
    	out.println("<argument value=\"%targetmethod\"/>");
    	out.println("<argument value=\"%originalparameters\"/>");
    	out.println("</method>");
    	
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Inserting method definition for 'handleReturnMethodCall'.");     
    	out.println("<method id=\"invocation_with_return\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleReturnMethodCall\" returnType=\"object\">");
    	out.println("<argument value=\"%senderobject\"/>");
    	out.println("<argument value=\"%targetobject\"/>");
    	out.println("<argument value=\"%targetmethod\"/>");
    	out.println("<argument value=\"%originalparameters\"/>");
    	out.println("</method>");
    	
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Inserting method definition for 'handleCast'.");     
        out.println("<method id=\"cast\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.CastingFacility\" name=\"handleCast\" returnType=\"object\">");
    	out.println("<argument value=\"%targetobject\"/>");
    	out.println("<argument value=\"%casttarget\"/>");
    	out.println("</method>");
 
    	out.println("</methods>");
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Method definitions block has been written.");     
    }
    
    public ArrayList getAfterInstantiationClasses() 
    {
    	ArrayList result = new ArrayList();
    	Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Searching for instantiation interceptions...");
		
    	DataStore ds = DataStore.instance();
		Iterator it = ds.getAllInstancesOf(CompiledImplementation.class);
		while(it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation)it.next();
			String className = ci.getClassName();
			if(className != null)
			{
				result.add(className);
			   	Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Adding interception for instantiation of class '" + className + "' to our internal list.");
			}
		}
		
		it = ds.getAllInstancesOf(CpsConcern.class);
		while (it.hasNext()) {
			CpsConcern c = (CpsConcern)it.next();
			
			Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Found CpsConcern with name["+c.getName()+"]: " + c.getQualifiedName());
			
			Object o = c.getDynObject("IMPLEMENTATION");
			if ( o != null) {
				
				PrimitiveConcern pc = (PrimitiveConcern)o;
				
				Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Found primitive concern with name: " + pc.getQualifiedName());
				result.add(pc.getQualifiedName());
				Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Adding interception for instantiation of class '" + pc.getQualifiedName() + "' to our internal list.");
			}
		}
		it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext()) {
			Concern c = (Concern)it.next();
			//Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Found Concern with name["+c.getName()+"]: " + c.getQualifiedName());
			if(c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
			{
				result.add(c.getQualifiedName());
				Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Adding interception for instantiation of class '" + c.getQualifiedName() + "' to our internal list.");
			}
		}
	   	
	   	return result;
    }
    
    private void writeMethodInvocations(CommonResources resources)
    {
        // Get a FILTHService instance
        FILTHService filthservice = FILTHService.getInstance(resources);
 
        out.println("<methodInvocations>");
      
		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while( iterConcerns.hasNext() )
		{
			Concern c = (Concern)iterConcerns.next();
				
			try {
				List list = filthservice.getOrder(c);
				if (!list.isEmpty()) {
					writeMethodInvocationRecord(c.getQualifiedName());
				}
			}
			catch (Exception e) {
				Debug.out(Debug.MODE_WARNING, "CONE-IS", "Unable to get ordering for concern '" + c.getName() + "' from FILTH.");
			}
		}
      
		out.println("</methodInvocations>");
    }
    
    private void writeCastingInterceptions()
    {
    	Set s = new HashSet();
     	
    	out.println("<casts>");
    	
  	  	Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
  	  	while ( iterConcerns.hasNext() )
	 	{
  	  		Concern c = (Concern)iterConcerns.next();
  	  		boolean castConcern = false;
				
  	  		if ( c.getDynObject("SingleOrder") != null) {
  	  			FilterModuleOrder fmo = (FilterModuleOrder)c.getDynObject("SingleOrder");

  	  			Iterator iterFilterModules = fmo.orderAsList().iterator();
  	  			while ( iterFilterModules.hasNext() )
  	  			{
  	  				FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)iterFilterModules.next());
  	  			
  	  				Iterator iterInternals = fm.getInternalIterator() ;
  	  				while ( iterInternals.hasNext() )
  	  				{
  	  					Internal internal = (Internal)iterInternals.next();
  	  					if ( !s.contains(internal) )
  	  					{
  	  						castConcern = true;
  	  						s.add( internal );
  	  						writeCastingInterceptionRecord( internal.type.getQualifiedName() );
  	  					}
  	  				}
  	  			}
  	  		}
  	  		
  	  		if ( castConcern ) {
  	  			writeCastingInterceptionRecord( c.getQualifiedName() );
  	  		}
	 	}
  	  	
    	out.println("</casts>");
    }
    
    /**
     * @param store
     * @param resources
     * @roseuid 40EBC2AE0316
     */
    private void writeClassDefinitions(CommonResources resources) {
      // Write definitions for inputfilters and dummy unlinking
  	  Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Writing definition for class '*'...");

	  out.println("<class name=\"*\">");
	  
	  writeMethodInvocations(resources);
	  writeCastingInterceptions();
	  
      out.println("</class>");
      
      Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Class definition for class '*' has been written.");
      
      // Write definitions for outputfilters and instantiations
      ArrayList instantiationClasses = getAfterInstantiationClasses();
      
	  Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
	  while ( iterConcerns.hasNext() )
	  {
		Concern c = (Concern)iterConcerns.next();
				
		if ( c.getDynObject("SingleOrder") != null) {
			FilterModuleOrder fmo = (FilterModuleOrder)c.getDynObject("SingleOrder");

			Iterator iterFilterModules = fmo.orderAsList().iterator();
			while ( iterFilterModules.hasNext() )
			{
				FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID((String)iterFilterModules.next());

				if ( !fm.outputFilters.isEmpty() )
				{
					// Outputfilters defined for this concern
				  	Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Writing definition for class '" + c.getQualifiedName() + "'...");

					out.println("<class name=\"" + c.getQualifiedName() + "\">");
				
					out.println("<methodInvocations>");
					writeMethodInvocationRecord();
					out.println("</methodInvocations>");
					
					if ( instantiationClasses.contains(c.getQualifiedName()) )
					{
						writeAfterInstantiationRecord();
						instantiationClasses.remove(c.getQualifiedName());
					}
				    
					out.println("</class>");
				      
			        Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Class definition for class '" + c.getQualifiedName() + "' has been written.");
				
					break;
				}
			}
		}
	  }
      
      // Write remaining class instantiations
      Iterator iterClasses = instantiationClasses.iterator();
      while ( iterClasses.hasNext() )
      {
      	String className = (String)iterClasses.next();
      	Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Writing definition for class '" + className + "'...");
      	writeAfterInstantiationRecord(className);
      	Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Class definition for class '" + className + "' has been written.");
      }
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
      
	  application = Configuration.instance().getProjects().getProperty("Executable");
      
	  try
	  {
	  	//this.repository = ((Properties)ds.getObjectByID("config")).getProperty("RepositoryFilename");
	  	debugLevel = Integer.parseInt(Configuration.instance().getProjects().getProperty("runDebugLevel"));
	  }
	  catch (NumberFormatException e)
	  {
	  	Debug.out(Debug.MODE_WARNING, "CONE-IS", "Unable to set debug level, using default debug level of 0.");
	  }
	  
      try
      {
        out = new PrintWriter(new BufferedWriter(new FileWriter(destination)));

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<weaveSpecification version=\"1.0\">");

        //writeAssemblyReferenceDefinitions( resources );
        writeMethodDefinitions();
        writeApplicationInfo();
        writeClassDefinitions(resources);

        out.println("</weaveSpecification>");

        out.flush();
        out.close();
      }
      catch (IOException e) {
        throw new ModuleException("Unable to create weave specification file '" + destination + "'.","CONE_IS");
      }
      catch (Exception e) {
        throw new ModuleException("Unhandled exception: " + e.getClass().toString() + ";"+ e.getMessage(),"CONE_IS");
      }     
    }
    
    /**
     * @roseuid 40F3CE3B00DD
     */
    private void writeApplicationInfo() 
    {
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Writing application information...");
      	out.println("<application name=\"" + application + "\">");
      	out.println("<notifyStart id=\"application_start\"/>");     	
      	out.println("</application>");
      	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Application information has been written.");     
    }
   
    private void writeMethodInvocationRecord() 
    {
		Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Adding interception for all outgoing calls.");
    	out.println("<callToMethod class=\"*\" name=\"*\">");
		out.println("<voidRedirectTo id=\"invocation_void\"/>");
		out.println("<returnvalueRedirectTo id=\"invocation_with_return\"/>");
		out.println("</callToMethod>");     
    }
    
    /**
     * @param target
     * @roseuid 40F3CE3B010F
     */
    private void writeMethodInvocationRecord(String target) 
    {
		Debug.out(Debug.MODE_DEBUG, "CONE-IS", "Adding interception for all methods of class '" + target + "'.");
    	out.println("<callToMethod class=\"" + target + "\" name=\"*\">");
		out.println("<voidRedirectTo id=\"invocation_void\"/>");
		out.println("<returnvalueRedirectTo id=\"invocation_with_return\"/>");
		out.println("</callToMethod>");     
    }
    
    private void writeClassReplacementRecord(String oldAssembly, String oldClass, String newAssembly, String newClass)
    {
     	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Replacing class '[" + oldAssembly + "]" + oldClass + "' with '[" + newAssembly + "]" + newClass + "'.");
    	out.println("<classReplacement assembly=\"" + oldAssembly + "\" class=\"" + oldClass + "\">");
    	out.println("<replaceWith assembly=\"" + newAssembly + "\" class=\"" + newClass + "\"/>");
    	out.println("</classReplacement>");
    }
    
    private void writeAfterInstantiationRecord()
    {
     	Debug.out(Debug.MODE_DEBUG, "CONE_IS", "Adding notification after instantiation.");
    	out.println("<afterClassInstantiation>");
		out.println("<executeMethod id=\"after_instantiation\"/>");
		out.println("</afterClassInstantiation>");  	
    }
    
    private void writeAfterInstantiationRecord(String className)
    {
		out.println("<class name=\""+className+"\">");
		writeAfterInstantiationRecord();
		out.println("</class>");	
    }
    
    private void writeCastingInterceptionRecord(String className)
    {
		out.println("<castTo assembly=\"\" class=\""+className+"\">");
		out.println("<executeMethodBefore id=\"cast\"/>");
		out.println("</castTo>");   	
    
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
    
    private class ClassInformation {
        private ArrayList mInvocations;
        private String mClassName = "";

        /**
         * @return java.lang.String
         * @roseuid 40EBC2C80155
         */
        public String getClassName() {
        return mClassName;
        }
        
        /**
         * @param invocation
         * @roseuid 40EBC2C80173
         */
        public void AddInvocation(CWeaveFileGenerator.MethodInformation invocation) {
        mInvocations.add(invocation);
        }
        
        /**
         * @return java.util.Iterator
         * @roseuid 40EBC2C801A2
         */
        public Iterator getInvocationsIterator() {
        return mInvocations.iterator();
        }
    }
}
