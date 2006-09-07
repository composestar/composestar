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
 * $Id: CWrapper.java,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */
package Composestar.C.wrapper;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.CommonAST;
import antlr.debug.misc.ASTFrame;

import java.io.*;
import java.util.*;

import Composestar.C.wrapper.parsing.*;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.C.LAMA.*;
import Composestar.C.MASTER.FileMap;
import Composestar.Core.LAMA.*;

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 20-dec-2004
 * Time: 11:17:45
 * To change this template use File | Settings | File Templates.
 */
public class CWrapper implements CTCommonModule
{
	private Vector allNodes = null;
	private TNode node = null;
    public GlobalIntroductionPoint introductionPoint = null;
    public Hashtable fileASTMap = new Hashtable();
    private Vector functions = null;
    private String filename = null;
    private String tempFolder = null;
    public String objectname = null;
    public HashMap structASTMap = new HashMap();
    private ArrayList cfiles = new ArrayList();
    private WrappedAST wrappedAST = null;
    private PrintWriter out=null;
    private String namespace=null;
    private boolean firstNameSpace = false;
	private File directory=null;
	private static HashMap usedTypes= null;
  
	   
    public void run(CommonResources resources) throws ModuleException
    {
    	filename= Configuration.instance().getPathSettings().getPath("Base"); 
    	tempFolder= Configuration.instance().getPathSettings().getPath("Base");
    	/** here a new file(typ): Semantics of the annotations is instantiaded 
    	 * just as the xml file where the annotion information will be saved **/
    	File destination = new File(tempFolder+"attributes.xml");
    	CFile semantics = new CFile();
    	usedTypes=new HashMap();
    	DataStore dataStore = DataStore.instance();
    	PrimitiveConcern pcFile = new PrimitiveConcern();
    	semantics.setName("Semantics");
    	semantics.setFullName("Composestar.Semantics");
    	semantics.setAnnotation(true);
    	pcFile.setName( semantics.name() );
		pcFile.setPlatformRepresentation(semantics);
		semantics.setParentConcern(pcFile);
		
		dataStore.addObject( semantics.name(), pcFile );
		//FileMap fm = FileMap.instance();
		
	 	try{
    		out = new PrintWriter(new BufferedWriter(new FileWriter(destination)));
    		out.println("<?xml version=\"1.0\"?>");
    	    out.println("<Attributes>");
    	}
    	catch (IOException e) {
            e.printStackTrace();
        }
    	if( filename.equals( "ERROR" ) ) 
		{
			throw new ModuleException( "Error in configuration file: No such property TempFolder" );
		}
    	/**@Todo: now the subdirs are searched for .c files and not the sources of build.ini is used**/
    	
    	createNameSpace(new File(filename),true,new InputFileNameFilter(), resources);
    	cfiles = getAllFilesFromDirectory(new File(filename),true,new InputFileNameFilter());
  	
    	for(int i=0; i<cfiles.size(); i++)
		{
				this.fileASTMap.put((String)cfiles.get(i),new Object());
		}
        
    	Iterator it = this.fileASTMap.keySet().iterator();
		while(it.hasNext())
		{
			filename = (String)it.next();//(String)cfiles.get(i); //
			try
	    	{
				/******************************8
				 * object name is the filename without the extension and 
				 * without the tempfolder-path, subdirs from the tempfolder path
				 * are changed into subdir.object*
				 ***********************************/
				retrieveAST wrapper = new retrieveAST();
		    	
				setObjectName(filename,resources);
				setNameSpace(filename, resources);
				wrapper.createWrappedAST(filename, objectname, namespace, out, usedTypes, this);
				this.fileASTMap.put(filename,wrapper);
				FileMap.instance().addFileAST(filename,wrapper);//createWrappedAST(filename, objectname, namespace, out, usedTypes, this));
			}
    		catch(Exception e)
    		{
	    		e.printStackTrace();
	    	}
		}
		out.println("</Attributes>");
		out.close();
		
		//Iterator i = usedTypes.values().iterator();
        //while(i.hasNext()){
        	//System.out.println("HashMap usedTypes entry:" +(String)i.next());
        //}
    }
	
   
    private ArrayList getAllFilesFromDirectory(File dir, boolean recurse, FilenameFilter fnf) 
    {
    	ArrayList list = new ArrayList();
    	if (dir.isDirectory())
        {
            String[] children = dir.list(fnf);
            for (int i=0; i<children.length; i++)
            {
            	File f = new File(dir, children[i]);
                if(!recurse && f.isFile())
                	list.add(f.getAbsolutePath());
                else if(recurse)
                {
                	list.addAll(getAllFilesFromDirectory(f,recurse,fnf));
                }
            }
        }
        else
        {
        	list.add(dir.getAbsolutePath());
        }
        return list;
    }
    
    /**TODO: @johan this create namespace function makes a namespace from a directory
     * this does not work well cause is only looks for the path and not for the parent namespaces 
     * **/ 
    
    private void createNameSpace(File dir, boolean recurse, FilenameFilter fnf, CommonResources resources){
    	{
        	if (dir.isDirectory())
            {
        		namespace=dir.getName();
              	firstNameSpace = true;
            	String[] children = dir.list(fnf);
                for (int i=0; i<children.length; i++)
                {
                	File f = new File(dir, children[i]);
                    if(recurse)
                    {
                    	createNameSpace(f,recurse,fnf, resources);
                    }
                }
            }
            else //is in directory:namespace and this directory contains a .c file
            {
            	if(firstNameSpace){
            		LangNamespace ns = new LangNamespace(namespace);
            		//dict.addLanguageUnit(ns);
            		firstNameSpace=false;
            	}
            }
        }
    
    }
 
  
    public void setFunctions(Vector functions) {
		this.functions = functions;
	}

	public Vector getFunctions() {
		return functions;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void getObjectname(String objectname){
		this.objectname =objectname;
	}
	
	public void getNameSpace(String namespace){
		this.namespace=namespace;
	}
	
	public void getUsedType(HashMap usedTypes){
		this.usedTypes= usedTypes;
	}
	
	public void setObjectName(String filename, CommonResources resources){
		this.objectname = Configuration.instance().getPathSettings().getPath("Base");
		this.objectname = objectname.replace('/','\\');
		this.objectname = filename.substring(objectname.length());
		this.objectname = objectname.substring(0,objectname.lastIndexOf(".c"));
		this.objectname = objectname.replace('\\','.');
	}
	
	public void setNameSpace(String filename, CommonResources resources){
		namespace= Configuration.instance().getPathSettings().getPath("Base");
		namespace = namespace.replace('/','\\');
		namespace = namespace.substring(0,namespace.lastIndexOf("\\"));
		namespace = namespace.substring(0,namespace.lastIndexOf("\\"));
		namespace = filename.substring(namespace.length());
		namespace = namespace.substring(0,namespace.lastIndexOf("\\"));
		namespace = namespace.substring(namespace.indexOf("\\")+1, namespace.length());
		namespace = namespace.replace('\\','.');
	}
	    
	public String getFilename() {
		return filename;
	}
	
	public void pWriter(PrintWriter pw){
		this.out=pw;
	}

	public void setWrappedAST(WrappedAST wrappedAST) {
		this.wrappedAST = wrappedAST;
	}

	public WrappedAST getWrappedAST() {
		return wrappedAST;
	}
	
}
