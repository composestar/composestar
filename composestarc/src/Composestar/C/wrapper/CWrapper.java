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
 * $Id: CWrapper.java,v 1.7 2005/11/30 21:43:32 pascal_durr Exp $
 */
package Composestar.C.wrapper;

import antlr.RecognitionException;
import antlr.TokenStreamException;

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

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 20-dec-2004
 * Time: 11:17:45
 * To change this template use File | Settings | File Templates.
 */
public class CWrapper implements CTCommonModule
{

    private GnuCLexer lexer = null;
    private GnuCParser parser = null;
    private TNode node = null;
    public GlobalIntroductionPoint introductionPoint = null;
    private PreprocessorInfoChannel infoChannel = null;
    Hashtable fileASTMap = new Hashtable();
    private Vector functions = null;
    private String filename = null;
    private String tempFolder = null;
    private String objectname = null;
    public HashMap structASTMap = new HashMap();
    private ArrayList cfiles = new ArrayList();
    private WrappedAST wrappedAST = null;
    private PrintWriter out=null;
    
    public void createWrappedAST(String filename, String objectname, PrintWriter pw) throws FileNotFoundException
    {
        this.setFilename(filename);
        this.getObjectname(objectname);
        this.pWriter(pw);

        try
        {
        	DataInputStream input = new DataInputStream(new FileInputStream(filename));
        	initialization(input);
        }
        catch(FileNotFoundException fnfe)
        {
        	System.out.println("File "+filename+" not found!");
        	System.exit(-1);
        }
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
    
    public void run(CommonResources resources) throws ModuleException
    {
    	filename= Configuration.instance().getPathSettings().getPath("Base"); 
    	tempFolder= Configuration.instance().getPathSettings().getPath("Base");
    	
    	/** here a new file(typ): Semantics of the annotations is instantiaded 
    	 * just as the xml file where the annotion information will be saved **/
    	File destination = new File(tempFolder+"attributes.xml");
    	CFile semantics = new CFile();
    	DataStore dataStore = DataStore.instance();
    	PrimitiveConcern pcFile = new PrimitiveConcern();
    	semantics.setName("Semantics");
    	semantics.setFullName("Composestar.Semantics");
    	semantics.setAnnotation(true);
    	pcFile.setName( semantics.name() );
		pcFile.setPlatformRepresentation(semantics);
		semantics.setParentConcern(pcFile);
		dataStore.addObject( semantics.name(), pcFile );
		
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
    	
    	cfiles = getAllFilesFromDirectory(new File(filename),true,new InputFileNameFilter());
  	
    	for(int i=0; i<cfiles.size(); i++)
		{
				this.fileASTMap.put((String)cfiles.get(i),new Object());
		}
        
    	Iterator it = this.fileASTMap.keySet().iterator();
		while(it.hasNext())
		{
			filename = (String)it.next();
			try
	    	{
				CWrapper wrapper = new CWrapper();
				/******************************8
				 * object name is the filename without the extension and 
				 * without the tempfolder-path, subdirs from the tempfolder path
				 * are changed into subdir.object*
				 ***********************************/
				setObjectName(filename,resources);
				wrapper.createWrappedAST(filename, objectname, out);
				fileASTMap.put(filename,wrapper);
    		}
    		catch(Exception e)
    		{
	    		e.printStackTrace();
	    	}
		}
		out.println("</Attributes>");
		out.close();
    }
   
    private void initialization(DataInputStream input)
    {
    	lexer = new GnuCLexer(input);
        lexer.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        lexer.initialize();
        infoChannel = lexer.getPreprocessorInfoChannel();

        parser = new GnuCParser(lexer);

        parser.setASTNodeClass(TNode.class.getName());
        TNode.setTokenVocabulary("Composestar.C.wrapper.parsing.GnuCTokenTypes");
        try
        {
        	parser.setFilename(this.filename);
        	parser.translationUnit();
            
            GnuCTreeParser treeparser = new GnuCTreeParser();
            
            treeparser.initiateXMLFile(out);
            treeparser.setFilename(this.objectname);
            treeparser.translationUnit(parser.getAST());
            
            this.introductionPoint = treeparser.getIntroductionPoint();
            this.functions = treeparser.getFunctions();
            this.structASTMap = treeparser.getStructASTMap();
        }
        catch (RecognitionException e)
        {
            e.printStackTrace();
        }
        catch (TokenStreamException e)
        {
            e.printStackTrace();
        }
                
        node = (TNode) parser.getAST();
    }
    /**
    public void printMetaModel()
    {
    	System.out.println("Meta model for file: "+this.getFilename());
    	if(this.introductionPoint != null)
    		System.out.println("Introduction JP: "+this.introductionPoint.getNode().getLineNum());
    	for(int i=0; i<getFunctions().size(); i++)
    	{
    		Function function = (Function)this.getFunctions().get(i);
    		if(function != null)
    		{
	    		System.out.println("Meta model for function: "+function.getName());
	    		System.out.println("\tIn file: "+function.getFileName());
	    		System.out.print("\tReturn type: ");
    			if(function.getReturnParameter() != null)
	    		{
	    			function.getReturnParameter().testParameterType();
	    		}
	    		else 
	    			System.out.println("void");
    			
	    		if(!function.hasNoParameters())
	    		{
		    		System.out.println("\tNumber of parameters: "+function.getNumberOfInputs());
		    		for(int j=0; j<function.getNumberOfInputs(); j++)
		    		{
		    			Parameter param = function.getInputParameter(j);
		    			System.out.print("\t\tParameter["+j+"]: ");
		    			param.testParameter();
		    		}
	    		}
    		}
    	}
    }**/

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
	
	public void setObjectName(String filename, CommonResources resources){
		this.objectname = Configuration.instance().getPathSettings().getPath("Base");
		this.objectname = objectname.replace('/','\\');
		this.objectname = filename.substring(objectname.length());
		this.objectname = objectname.substring(0,objectname.lastIndexOf(".c"));
		this.objectname = objectname.replace('\\','.');
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
