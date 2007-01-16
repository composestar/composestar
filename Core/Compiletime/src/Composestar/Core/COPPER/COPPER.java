/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.COPPER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Debug;
import antlr.CommonAST;
import antlr.RecognitionException;

/**
 * Main class used to run the parser
 */
public class COPPER implements CTCommonModule
{
	public static final String MODULE_NAME = "COPPER";

	private static final int ALL_PHASES = 0;

	private static final int PARSE_PHASES = 1;

	private static String cpscontents; // contents of the cps file we're

	// parsing

	private static String embeddedSource; // string used to hold the source

	// (if embedded)

	private static CpsParser parser;

	private static CommonAST parseTree;

	private static boolean showtree; // show the parse tree?

	public void parseCpsFile(String filename, int phase) throws ModuleException
	{
		// 1. parsing
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Parsing phase");
		CPSFileParser fileparser = new CPSFileParser();
		fileparser.parseCpsFileWithName(filename);

		if (phase == ALL_PHASES)
		{
			// 2.source extraction
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Source extraction phase");
			SourceExtractor se = new SourceExtractor();
			se.extractSource();

			// 3. create first version of objects
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Parse building phase");
			walkTree(filename);

			// somewhere:
			SyntacticSugarExpander sse = new SyntacticSugarExpander();
			sse.expand();
		}
	}

	public void walkTree(String filename) throws ModuleException
	{
		try
		{
			CpsTreeWalker walker = new CpsTreeWalker();
			walker.getRepositoryBuilder().setFilename(filename);
			walker.setASTNodeClass("Composestar.Core.COPPER.CpsAST");
			walker.concern(COPPER.getParseTree());
		}
		catch (RecognitionException r)
		{
			throw new ModuleException("AST Error: " + r.getMessage(), MODULE_NAME, r.getFilename(), r.getLine());
		}
	}

	public void copyOperation(String filename) throws ModuleException
	{
		INCRE inc = INCRE.instance();

		// collect and iterate over all objects from previous compilation runs
		Iterator it = inc.history.getIterator();
		while (it.hasNext())
		{
			Object obj = it.next();

			if (obj instanceof RepositoryEntity)
			{
				// COPPER only adds RepositoryEntities
				RepositoryEntity entity = (RepositoryEntity) obj;

				if (!entity.dynamicmap.isEmpty())
				{
					// remove dynamic object REFERENCED
					if (entity.getDynObject("REFERENCED") != null)
					{
						entity.dynamicmap.remove("REFERENCED");
					}
				}

				if (obj instanceof Reference)
				{
					// references need to be resolved again by REXREF
					Reference ref = (Reference) obj;
					ref.setResolved(false);
				}

				if (obj instanceof DeclaredObjectReference)
				{
					DeclaredObjectReference decl = (DeclaredObjectReference) obj;
					if (decl.getDescriptionFileName() == null)
					{
						if (!decl.getName().equals("inner"))
						{
							DataStore.instance().addObject(obj);
							entity.repositoryKey = entity.getUniqueID();
						}
					}
				}

				if (obj instanceof FilterType)
				{
					DataStore.instance().addObject(obj);
					// fixme: somehow filename is not persistent for FilterType
				}
				else if (entity.getDescriptionFileName() != null && entity.getDescriptionFileName().equals(filename))
				{
					if (obj instanceof CpsConcern)
					{
						CpsConcern cps = (CpsConcern) obj;
						try
						{
							CpsConcern cpsclone = (CpsConcern) cps.clone();
							DataStore.instance().addObject(cpsclone.getQualifiedName(), cpsclone);
						}
						catch (CloneNotSupportedException e)
						{
							e.printStackTrace();
						}
					}
					else if (obj != null)
					{
						DataStore.instance().addObject(obj);
						entity.repositoryKey = entity.getUniqueID();
						// don't forget to update repositoryKey due to different
						// hashcodes
					}
				}
			}
		}
	}

	public void run(CommonResources resources) throws ModuleException
	{
		COPPER copper = new COPPER();
		INCRE incre = INCRE.instance();
		Iterator cpsIterator = Configuration.instance().getProjects().getConcernSources().iterator();

		for (Object o : Configuration.instance().getProjects().getConcernSources())
		{
			ConcernSource concern = (ConcernSource) o;

			if (incre.isProcessedByModule(concern, MODULE_NAME))
			{
				INCRETimer coppercopy = incre.getReporter().openProcess(MODULE_NAME, concern.getFileName(),
						INCRETimer.TYPE_INCREMENTAL);
				copper.copyOperation(concern.getFileName());
				coppercopy.stop();
			}
			else
			{
				INCRETimer copperrun = incre.getReporter().openProcess(MODULE_NAME, concern.getFileName(),
						INCRETimer.TYPE_NORMAL);
				copper.parseCpsFile(concern.getFileName(), ALL_PHASES);
				copperrun.stop();
			}
		}
	}

	public static void setParseTree(CommonAST theParseTree)
	{
		parseTree = theParseTree;
	}

	public static CommonAST getParseTree()
	{
		return parseTree;
	}

	public static void setCpscontents(String theCpscontents)
	{
		cpscontents = theCpscontents;
	}

	public static String getCpscontents()
	{
		return cpscontents;
	}

	public static void setParser(CpsParser theParser)
	{
		parser = theParser;
	}

	public static CpsParser getParser()
	{
		return parser;
	}

	public static void setEmbeddedSource(String theEmbeddedSource)
	{
		embeddedSource = theEmbeddedSource;
	}

	public static String getEmbeddedSource()
	{
		return embeddedSource;
	}

	public static void setShowtree(boolean theShowtree)
	{
		showtree = theShowtree;
	}

	public static boolean isShowtree()
	{
		return showtree;
	}

	public static void main(String[] args)
	{
		Debug.setMode(Debug.MODE_DEBUG);
		System.out.println("Welcome to COPPER...");

		COPPER copper = new COPPER();
		ArrayList cpsfiles = new ArrayList();
		boolean test = false;
		boolean verify = false;
		int result = 0;
		for (String arg : args)
		{
			if ("--test".equals(arg) || "-t".equals(arg))
			{
				test = true;
				verify = false;
			}
			else if (arg.equals("--verify") || "-v".equals(arg))
			{
				test = false;
				verify = true;
			}
			else if (arg.equals("--debug") || arg.equals("-d"))
			{
				Debug.setMode(Debug.MODE_INFORMATION);
			}
			else if (arg.startsWith("@"))
			{
				// Parse the response file (should contain a semicolon seperated
				// list of .cps files)

				File f = new File(arg.substring(1));
				if (f.exists())
				{
					try
					{
						BufferedReader br = new BufferedReader(new FileReader(f.getName()));
						String line = br.readLine();
						if (line != null)
						{
							String[] files = line.split(";");
							for (int k = 0; k < files.length; k++)
							{
								cpsfiles.add(files[k]);
							}
						}
						br.close();
					}
					catch (IOException e)
					{
						Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Error reading response file.");
					}
				}
				else
				{
					Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Response file '" + f.getName() + "' not found!");
				}

			}
			else
			{
				cpsfiles.add(arg);
			}
		}
		if (args.length == 0 || (!test && !verify))
		{
			System.out.println("Usage: java COPPER [options] *.cps");
			System.out.println("\t Options:\t --test   | -t\tParse all the files and write the XML file");
			System.out.println("\t\t\t --verify | -v\tOnly parse the concerns to see if there are syntax errors");
			System.out.println("\t\t\t --debug | -d\tPrint debug messages");
			System.out
					.println("\t\t\t @<filename>\tResponse file, contains a semicolon separated list of all cps files");
			System.exit(-1);
		}
		if (test)
		{
			int errors = 0;

			for (Object cpsfile : cpsfiles)
			{
				try
				{
					Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Parsing concernfile '" + cpsfile + "'...");
					copper.parseCpsFile((String) cpsfile, ALL_PHASES);

					Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Serializing repository...");
					// RepositorySerializer rs = new
					// DotNETRepositorySerializer(new
					// java.io.File((String)cpsfiles.get(i) + ".xml"),
					// DataStore.instance());
					// RepositorySerializer rs =
					// (RepositorySerializer)DataStore.instance().getObjectByID("RepositorySerializer");
					// rs.run(resources);
				}
				catch (ModuleException e)
				{
					errors++;
				}
			}

			if (errors > 0)
			{
				Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Grammar verification and/or serializing failed for "
						+ errors + " cps files!");
				result = 1;
			}
			else
			{
				Debug.out(Debug.MODE_INFORMATION, MODULE_NAME,
						"Grammar verification and serializing succeeded for all cps files.");
			}
		}
		else if (verify)
		{
			int errors = 0;

			for (Object cpsfile : cpsfiles)
			{
				Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Parsing concernfile '" + cpsfile + "'...");
				try
				{
					copper.parseCpsFile((String) cpsfile, PARSE_PHASES);
				}
				catch (ModuleException e)
				{
					errors++;
				}
			}

			if (errors > 0)
			{
				Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Grammar verification failed for " + errors + " cps files!");
				result = 1;
			}
			else
			{
				Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Grammar verification succeeded for all cps files.");
			}
		}

		System.exit(result);
	}
}
