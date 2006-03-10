package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Main.java,v 1.1 2006/02/16 23:03:56 pascal_durr Exp $
 * 
**/


import java.io.*;

import java.util.LinkedList;
import Composestar.Core.COPPER.COPPER;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.Exception.*;
import Composestar.Core.FIRE.jargs.*;
import Composestar.Core.Master.*;
import Composestar.Core.RepositoryImplementation.*;

public class Main 
{
	private static LinkedList readConcern (String filename)
	{
		DataStore ds = DataStore.instance();
		ds.clean();

		Composestar.Core.REXREF.Main rexref = new Composestar.Core.REXREF.Main(); 
		CommonResources cr = new CommonResources(); 
		
		cr.addResource("CpsFileName", filename);

		//Composestar.Core.Supre.Main fillDatastore = new Composestar.Core.Supre.Main();
		COPPER copper = new COPPER();
		try 
		{
			copper.run(cr);
			rexref.run(cr);
		}
		catch (ModuleException e)
		{
			System.out.println ("Exception in FilterReasoningEngineTest.readConcern("+filename+")");
		}

		return getList(ds.getAllObjects());
	}

	private static LinkedList getList (Object [] data) 
	{
		// This list contains FilterModule references.
		LinkedList ll = new LinkedList();
		
		for (int i = 0; i < data.length; i++)
		{
			if (data[i] instanceof FilterModule) 
			{
				FilterModuleReference fmr = new FilterModuleReference();
				fmr.setRef((FilterModule) data[i]);
				ll.add(fmr);	
			}
		}

		return ll;
	}


    private static void printUsage() 
    {
        System.err.println("usage: Composestar.Core.FIRE.Main [{-h, --help}] [{-v,--verbose}] [{-t,--testfile}] filename");
    }

    private static void printHelp() 
    {
        System.out.println("help: ");
	System.out.println("This program reads an input filter and prints the possible actions");
	System.out.println();
	System.out.println("testfile: A simple inputfilter file. (Parsed by ANTLR)");
	System.out.println();
    }


	public static void main( String[] args ) 
	{
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option help = parser.addBooleanOption('h', "help");
		CmdLineParser.Option verbose = parser.addBooleanOption('v', "verbose");
		CmdLineParser.Option testfile = parser.addBooleanOption('t', "testfile");

		try 
		{
			parser.parse(args);
		}
		catch ( CmdLineParser.OptionException e ) 
		{
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}

        	String[] otherArgs = parser.getRemainingArgs();

		// Invalid parameters
		if (args.length == 0 || otherArgs.length != 1) 
		{
			printUsage(); 
			System.exit(1);
		}

		if (parser.getOptionValue(help) != null && ((Boolean)parser.getOptionValue(help)).booleanValue())
		{
			printHelp();
			System.exit(0);
		}



        // Extract the values entered for the various options -- if the
        // options were not specified, the corresponding values will be
        // null.
        Boolean isVerbose = (Boolean)parser.getOptionValue(verbose);
        Boolean isTestFile = (Boolean)parser.getOptionValue(testfile);
	String fileName = (String)otherArgs[0];


	// set verbose
	if (isVerbose != null && isVerbose.booleanValue()) Debug.setMode(3);
	else Debug.setMode(0);
		
	FilterReasoningEngine fire = null;

	if (isTestFile != null && isTestFile.booleanValue()) 
	{
	//	fire = new FilterReasoningEngine(fileName);
		//InputStream in = new FileInputStream (new FileInputStream (fileName));
		try
		{
			fire = new FilterReasoningEngine (new FileInputStream(fileName)); 
		} catch (FileNotFoundException e)
		{
			System.out.println ("Cannot find the specified file");
			System.exit(3);
		}
	}
	else
	{
		LinkedList list = readConcern(fileName);
		fire = new FilterReasoningEngine(list);
	}

	fire.run();

	System.out.println (fire.getTree().toTreeString());
	

        System.exit(0);
    }
}

