package Composestar.Java.MASTER;

import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;

/**
 * Main entry point for the CompileTime. The Master class holds Modules and
 * executes them in the order they are added.
 */
public class JavaMaster extends Master
{
	public JavaMaster(String[] args)
	{
		super(args);
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public void run()
	{
		try
		{
			long beginTime = System.currentTimeMillis();

			// Apache XML driver is moved to a different package in Java 5
			if (System.getProperty("java.version").substring(0, 3).equals("1.5"))
			{
				System.setProperty("org.xml.sax.driver", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Selecting SAXParser XML SAX Driver");
			}
			else
			{
				System.setProperty("org.xml.sax.driver", "org.apache.crimson.parser.XMLReaderImpl");
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Selecting XMLReaderImpl XML SAX Driver");
			}

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Creating datastore...");
			DataStore.instance();

			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.run(resources);

			Iterator modulesIter = incre.getModules();
			while (modulesIter.hasNext())
			{
				// execute enabled modules one by one
				Module m = (Module) modulesIter.next();
				m.execute(resources);
			}

			incre.getReporter().close();
			if (Debug.willLog(Debug.MODE_WARNING))
			{
				Debug.outWarnings();
			}
			if (Debug.willLog(Debug.MODE_DEBUG))
			{
				System.out.println("Total time: "+(System.currentTimeMillis()-beginTime)+"ms");
			}

		}
		catch (ModuleException e)
		{ 
			String error = e.getMessage();
			String filename = e.getErrorLocationFilename();
			int line = e.getErrorLocationLineNumber();
			
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			if (filename == null || "".equals(filename))
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			}
			else
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, filename, line);
			}

			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + Debug.stackTrace(e));
			System.exit(ECOMPILE);
		}
		catch (Exception e)
		{
			String error = e.getMessage();
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Internal compiler error: " + error);
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
			System.exit(EFAIL);
		}
	}

	/**
	 * Compose* main function. Creates the Master object and invokes the run method.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{

		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarJava.jar <config file>");
			return;
		}
		
		if (args[0].equals("-v") || args[0].equals("--version"))
		{
			Version.reportVersion(System.out);
			return;
		}

		Master master = new JavaMaster(args);
		try
		{
			master.loadConfiguration();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(ECONFIG);
		}
		
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, Version.getTitle() + " " + Version.getVersionString());
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Compiled on "+Version.getCompileDate().toString());
		master.run();
	}

}
