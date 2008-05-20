package Composestar.DotNET.DUMMER;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.StreamGobbler;
import Composestar.Utils.Logging.CPSLogger;

public class CSharpDummyEmitter implements DummyEmitter
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DUMMER);

	protected Map<String, Source> srcmapping;

	protected CommonResources resources;

	public void createDummies(Project project, Set<Source> sources) throws ModuleException
	{
		srcmapping = new HashMap<String, Source>();
		try
		{
			File attributesFile = new File(project.getIntermediate(), "attributes.xml");
			ExternalCSharpDummyGenerator dg = new ExternalCSharpDummyGenerator(resources, attributesFile);

			for (Source src : sources)
			{
				dg.addDummy(src.getFile(), src.getStub());
				srcmapping.put(src.getFile().toString(), src);
			}

			ProcessResult pr = dg.go();
			if (pr.code != 0)
			{
				logger.debug("CSharpDummyGenerator failed. Output follows:");
				Iterator<String> it = pr.stdout.iterator();
				while (it.hasNext())
				{
					logger.debug("  " + it.next());
				}

				throw new ModuleException("Error creating dummies: CSharpDummyGenerator failed.",
						ModuleNames.DUMMER);
			}

			createTypeLocationMapping(project, sources, pr.stdout);
		}
		catch (ProcessExecutionException e)
		{
			throw new ModuleException("Error creating dummies: " + e.getMessage(), "DUMMER");
		}
	}

	public void setCommonResources(CommonResources resc)
	{
		resources = resc;
	}

	/**
	 * Parse output from CSharpDummyGen, if a line starts with the word
	 * 'TypeLocation' the next 2 lines will contain the filename (full path) and
	 * fully qualified name of a class that is defined in that file.
	 * 
	 * @param lines
	 * @param project
	 */
	private void createTypeLocationMapping(Project project, Set<Source> sources, List<String> lines)
	{
		Iterator<String> it = lines.iterator();
		while (it.hasNext())
		{
			String line = it.next();
			if (line.startsWith("TypeLocation"))
			{
				String filename = (String) it.next();
				String classname = (String) it.next();
				logger.debug("Defined mapping: " + filename + "=> " + classname);
				Source src = srcmapping.get(filename);
				if (src != null)
				{
					project.getTypeMapping().addType(classname, src);
				}
				else
				{
					logger.error(String.format("Type %s refers to unknown source: %s", classname, filename));
				}
			}
		}
	}
}

class ExternalCSharpDummyGenerator
{
	private PrintStream stdout;

	private StreamGobbler stdin, stderr;

	private Process process;

	public ExternalCSharpDummyGenerator(CommonResources resources, File attributesFile)
			throws ProcessExecutionException
	{
		String[] command = new String[2];
		command[0] = getExecutable(resources);
		command[1] = attributesFile.toString();

		try
		{
			process = Runtime.getRuntime().exec(command);
			stdout = new PrintStream(process.getOutputStream());
			stderr = new StreamGobbler(process.getErrorStream());
			stdin = new StreamGobbler(process.getInputStream());
			stderr.start();
			stdin.start();
		}
		catch (IOException e)
		{
			throw new ProcessExecutionException("IOException: " + e.getMessage(), e);
		}
	}

	private String getExecutable(CommonResources resources) throws ProcessExecutionException
	{
		File exe = resources.getPathResolver().getResource("bin/CSharpDummyGenerator.exe");
		if (exe == null)
		{
			throw new ProcessExecutionException("bin/CSharpDummyGenerator.exe does not exist");
		}
		return exe.getAbsolutePath();
	}

	public void addDummy(File sourceFilename, File targetFilename)
	{
		stdout.println(sourceFilename.toString());
		stdout.println(targetFilename.toString());
	}

	public ProcessResult go() throws ProcessExecutionException
	{
		try
		{
			stdout.close();
			stdin.waitForResult();
			stderr.waitForResult();
			int code = process.waitFor();
			return new ProcessResult(code, stdin.getResultLines(), stderr.getResultLines());
		}
		catch (InterruptedException e)
		{
			throw new ProcessExecutionException("InterruptedException: " + e.getMessage(), e);
		}
	}
}

class ProcessResult
{
	public final int code;

	public final List<String> stdout;

	public final List<String> stderr;

	public ProcessResult(int code, List<String> stdout, List<String> stderr)
	{
		this.code = code;
		this.stdout = stdout;
		this.stderr = stderr;
	}
}

class ProcessExecutionException extends Exception
{
	private static final long serialVersionUID = 3576560381216905720L;

	public ProcessExecutionException(String message)
	{
		super(message);
	}

	public ProcessExecutionException(String message, Exception cause)
	{
		super(message, cause);
	}
}
