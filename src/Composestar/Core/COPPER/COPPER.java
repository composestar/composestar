/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: COPPER.java,v 1.2 2006/02/13 11:53:07 pascal Exp $
 */
package Composestar.Core.COPPER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.*;
import Composestar.Core.RepositoryImplementation.*;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.Exception.*;
import antlr.*;


/**
 * Main class used to run the parser
 */
public class COPPER implements CTCommonModule
{
  //global variables
  private static String cpscontents = null; //contents of the cps file we're parsing
  private static CpsParser parser = null;
  private static CommonAST parseTree = null;
  private static String embeddedSource = null; //string used to hold the source (if embedded)
  private static boolean showtree = false; //show the parse tree?
  private static final int ALL_PHASES = 0;
  private static final int PARSE_PHASES = 1;
  
  public void parseCpsFile(String filename, int phase) throws ModuleException
  {
  	try
	{
  		//1. parsing
  		Debug.out(Debug.MODE_DEBUG,"COPPER","Parsing phase");
		CpsASTFactory factory = new CpsASTFactory();
  		CPSFileParser fileparser = new CPSFileParser();
		fileparser.setCpsASTFactory(factory);
        fileparser.parseCpsFileWithName(filename);

  		if(phase == ALL_PHASES)
  		{
	  		//2.source extraction
	  		Debug.out(Debug.MODE_DEBUG,"COPPER","Source extraction phase");
	  		SourceExtractor se = new SourceExtractor();
	  		se.extractSource();
	
	  		//3. create first version of objects
	  		Debug.out(Debug.MODE_DEBUG,"COPPER","Parse building phase");
	  		CpsParseTreeWalker db = new CpsParseTreeWalker();
			db.setCpsASTFactory(factory);
			Composestar.Core.COPPER.CpsRepositoryBuilder.filename = filename;
	  		db.walkTree();
	  		
	  		//somewhere:
	  		SyntacticSugarExpander sse = new SyntacticSugarExpander();
	  		sse.expand();
  		}
    }
  	catch (ModuleException e)
	{
  	  //e.printStackTrace();
      //System.err.println(e.getMessage());
      //System.exit(-1);
  		throw e;
    }
  }

  
  public void copyOperation(String filename)
  {
	  INCRE inc = INCRE.instance();
	  int addedObjects = 0; 
	  
	  /* collect and iterate over all objects from previous compilation runs */
	  Object[] objects = inc.history.getAllObjects();
	  for(int i=0;i<objects.length;i++)
	  {
		  Object obj = (Object)objects[i];
		  if(obj instanceof RepositoryEntity)
		  {
			  // COPPER only adds RepositoryEntities
			  RepositoryEntity entity = (RepositoryEntity)obj;
			  
			  if(!entity.dynamicmap.isEmpty())
			  {
					// remove dynamic object REFERENCED
					if(entity.getDynObject("REFERENCED")!=null)
						entity.dynamicmap.remove("REFERENCED");	
			  }

			  if(obj instanceof Reference)
			  {
			  	  // references need to be resolved again by REXREF
				  Reference ref = (Reference)obj;
				  ref.setResolved(false);
			  }
			  
			  if(obj instanceof DeclaredObjectReference)
			  {	
				  DeclaredObjectReference decl = (DeclaredObjectReference)obj;
			  	  if(decl.getDescriptionFileName()== null)
				  {
					  if(decl.getName().equals("inner")){}
					  else 
					  {
							DataStore.instance().addObject(obj);
							entity.repositoryKey = entity.getUniqueID();
					  }
				  }
			  } 
			  
			  if(obj instanceof FilterType)
			  {
				  //FilterType ft = (FilterType)obj;
				  DataStore.instance().addObject(obj);		
				  // fixme: somehow filename is not persistent for FilterType	
				 
			  }	
			  else if(entity.getDescriptionFileName()!=null && entity.getDescriptionFileName().equals(filename))
			  {
				  if(obj instanceof CpsConcern)
				  {
					  CpsConcern cps = (CpsConcern)obj;
					  CpsConcern cpsclone = (CpsConcern)cps.clone();
					  DataStore.instance().addObject(cpsclone.getQualifiedName(),cpsclone);
					  addedObjects++;
				  }
				  else if(obj!=null)
				  {
					  DataStore.instance().addObject(obj);
					  entity.repositoryKey = entity.getUniqueID();
					  // don't forget to update repositoryKey due to different hashcodes
					  addedObjects++;
				  }
			  }
		  }
	  }
	  
	  /**
	   * Verify if we have copied something
	   * If not then parse file
	   */
	  if(addedObjects==0)
	  {
		  try 
		  {
			  this.parseCpsFile(filename,ALL_PHASES);	
		  }
		  catch (ModuleException e){ System.exit(1); }
	  }
  }

  public void run(CommonResources resources) throws ModuleException 
	{
	    COPPER copper = new COPPER();
		INCRE incre = INCRE.instance();
		Iterator cpsIterator = (Iterator)resources.getResource("CpsIterator");
		
		while(cpsIterator.hasNext()) 
		{
			String concern = (String)cpsIterator.next();
	   
			if(incre.isProcessedByModule(concern,"COPPER"))
			{
				INCRETimer coppercopy = incre.getReporter().openProcess("COPPER",concern,INCRETimer.TYPE_INCREMENTAL);
				copper.copyOperation(concern);
				coppercopy.stop();
			}
			else 
			{
				try 
				{
					INCRETimer copperrun = incre.getReporter().openProcess("COPPER",concern,INCRETimer.TYPE_NORMAL);
					copper.parseCpsFile(concern,ALL_PHASES);
					copperrun.stop();
				}
				catch (ModuleException e)
				{
					System.exit(1);
				}
			}
		}
  }

	public static void setParseTree(CommonAST theParseTree) {
		parseTree = theParseTree;
	}
	
	public static CommonAST getParseTree() {
		return parseTree;
	}

	public static void setCpscontents(String theCpscontents) {
		cpscontents = theCpscontents;
	}

	public static String getCpscontents() {
		return cpscontents;
	}

	public static void setParser(CpsParser theParser) {
		parser =theParser;
	}

	public static CpsParser getParser() {
		return parser;
	}

	public static void setEmbeddedSource(String theEmbeddedSource) {
		embeddedSource = theEmbeddedSource;
	}

	public static String getEmbeddedSource() {
		return embeddedSource;
	}

	public static void setShowtree(boolean theShowtree) {
		showtree = theShowtree;
	}

	public static boolean isShowtree() {
		return showtree;
	}
	
	public static void main(String args[])
	{
	  	Debug.setMode(2);
		System.out.println("Welcome to COPPER...");
	    COPPER copper = new COPPER();
	    ArrayList cpsfiles = new ArrayList();
	    boolean test = false;
	    boolean verify = false;
	    int result = 0;
	    for(int i=0; i<args.length; i++)
	    {
	    	if ("--test".equals(args[i]) || "-t".equals(args[i]))
	    	{
	    		test = true;
	    		verify = false;
	    	}
	    	else if (args[i].equals("--verify") || "-v".equals(args[i]))
	    	{
	    		test = false;
	    		verify = true;
	    	}
	    	else if (args[i].equals("--debug") || args[i].equals("-d"))
	    	{
	    		Debug.setMode(3);
	    	}
	    	else if (args[i].startsWith("@"))
	    	{
	    		// Parse the response file (should contain a semicolon seperated list of .cps files)
	    		try {
		    		File f = new File(args[i].substring(1));
		    		if ( f.exists() ) {
		    			BufferedReader br = new BufferedReader( new FileReader(f.getName()) );
		    			String line = br.readLine();
		    			String[] files = line.split(";");
		    			for (int k=0; k<files.length; k++) {
		    				cpsfiles.add(files[k]);
		    			}
		    			br.close();
		    		}
		    		else {
		    			Debug.out(Debug.MODE_WARNING,"COPPER","Response file '" + f.getName() + "' not found!");
		    		}
	    		}
	    		catch (Exception e) {
	    			Debug.out(Debug.MODE_DEBUG,"COPPER","Error reading response file.");
	    		}
	    	}
	    	else
	    	{
	    		cpsfiles.add(args[i]);
	    	}
	    }
	    if(args.length == 0 || (!test && !verify) )
	    {
	    	System.out.println("Usage: java COPPER [options] *.cps");
	    	System.out.println("\t Options:\t --test   | -t\tParse all the files and write the XML file");
	    	System.out.println("\t\t\t --verify | -v\tOnly parse the concerns to see if there are syntax errors");
	    	System.out.println("\t\t\t --debug | -d\tPrint debug messages");
	    	System.out.println("\t\t\t @<filename>\tResponse file, contains a semicolon separated list of all cps files");
	    	System.exit(-1);
	    }
	    if(test)
	    {
	    	int errors = 0;
	    	
	    	for(int i=0; i<cpsfiles.size(); i++)
	    	{
	    		try {
		    		Debug.out(Debug.MODE_INFORMATION, "COPPER", "Parsing concernfile '" + (String)cpsfiles.get(i) + "'...");
		    		copper.parseCpsFile((String)cpsfiles.get(i),ALL_PHASES);

		    		Debug.out(Debug.MODE_INFORMATION, "COPPER", "Serializing repository...");
		    		//RepositorySerializer rs = new DotNETRepositorySerializer(new java.io.File((String)cpsfiles.get(i) + ".xml"), DataStore.instance());
		    		//RepositorySerializer rs = (RepositorySerializer)DataStore.instance().getObjectByID("RepositorySerializer");
		    		//rs.run(resources);
	    		}
	    		catch (ModuleException e) {
	    			errors++;
	    		}
	    	}

      		if (errors > 0) {
	    		Debug.out(Debug.MODE_WARNING, "COPPER", "Grammar verification and/or serializing failed for " + errors + " cps files!");
	    		result = 1;
      		}
	    	else {
	    		Debug.out(Debug.MODE_INFORMATION, "COPPER", "Grammar verification and serializing succeeded for all cps files.");
	    	}
	    }
	    else if(verify)
	    {
	    	int errors = 0;
	    	
	    	for(int i=0; i<cpsfiles.size(); i++)
	    	{
	    		Debug.out(Debug.MODE_INFORMATION, "COPPER", "Parsing concernfile '" + (String)cpsfiles.get(i) + "'...");
	    		try {
	    			copper.parseCpsFile((String)cpsfiles.get(i),PARSE_PHASES);
	    		}
	    		catch (ModuleException e) {
	    			errors++;
	    		}
	    	}

      		if (errors > 0) {
	    		Debug.out(Debug.MODE_WARNING, "COPPER", "Grammar verification failed for " + errors + " cps files!");
	    		result = 1;
      		}
	    	else {
	    		Debug.out(Debug.MODE_INFORMATION, "COPPER", "Grammar verification succeeded for all cps files.");
	    	}
	    }
	    
	    System.exit(result);
	  }
}
