package Composestar.DotNET.DUMMER;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Utils.Debug;
import Composestar.Utils.StreamGobbler;

public class CSharpDummyEmitter implements DummyEmitter
{
	public void createDummy(Project project, Source source, String outputFilename) throws ModuleException
	{
		List sources = new ArrayList();
		sources.add(source);

		List outputFilenames = new ArrayList();
		outputFilenames.add(outputFilename);

		createDummies(project, sources, outputFilenames);
	}

	public void createDummies(Project project, List sources, List outputFilenames) throws ModuleException
	{
		try
		{
			String attributesFile = project.getBasePath() + "attributes.xml";
			ExternalCSharpDummyGenerator dg = new ExternalCSharpDummyGenerator(attributesFile);

			Iterator srcIter = sources.iterator();
			Iterator outputIter = outputFilenames.iterator();
			while (srcIter.hasNext())
			{
				Source source = (Source) srcIter.next();
				String sourceFilename = source.getFileName();
				String targetFilename = (String) outputIter.next();

				dg.addDummy(sourceFilename, targetFilename);
			}

			ProcessResult pr = dg.go();
			if (pr.code != 0)
			{
				Debug.out(Debug.MODE_DEBUG, "DUMMER", "CSharpDummyGenerator failed. Output follows:");
				Iterator it = pr.stdout.iterator();
				while (it.hasNext())
				{
					Debug.out(Debug.MODE_DEBUG, "DUMMER", "  " + it.next());
				}

				throw new ModuleException("Error creating dummies: CSharpDummyGenerator failed.", "DUMMER");
			}

			createTypeLocationMapping(project, pr.stdout);
		}
		catch (ProcessExecutionException e)
		{
			throw new ModuleException("Error creating dummies: " + e.getMessage(), "DUMMER");
		}
	}

	/**
	 * Parse output from CSharpDummyGen, if a line starts with the word
	 * 'TypeLocation' the next 2 lines will contain the filename (full path) and
	 * fully qualified name of a class that is defined in that file.
	 * 
	 * @param lines
	 * @param project
	 */
	private void createTypeLocationMapping(Project project, List lines)
	{
		Iterator it = lines.iterator();
		while (it.hasNext())
		{
			String line = (String) it.next();
			if (line.startsWith("TypeLocation"))
			{
				String filename = (String) it.next();
				String classname = (String) it.next();
				Debug.out(Debug.MODE_DEBUG, "DUMMER", "Defined mapping: " + filename + "=> " + classname);

				TypeSource srcLocation = new TypeSource();
				srcLocation.setFileName(filename);
				srcLocation.setName(classname);

				project.addTypeSource(srcLocation);
			}
		}
	}
}

class ExternalCSharpDummyGenerator
{
	private PrintStream stdout;

	private StreamGobbler stdin, stderr;

	private Process process;

	public ExternalCSharpDummyGenerator(String attributesFile) throws ProcessExecutionException
	{
		String[] command = new String[2];
		command[0] = getExecutable();
		command[1] = attributesFile;

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

	private String getExecutable() throws ProcessExecutionException
	{
		Configuration config = Configuration.instance();
		String cps = config.getPathSettings().getPath("Composestar");
		File exe = new File(cps, "binaries/CSharpDummyGenerator.exe");
		if (!exe.exists())
		{
			throw new ProcessExecutionException("Executable does not exist: " + exe.getAbsolutePath());
		}

		return exe.getAbsolutePath();
	}

	public void addDummy(String sourceFilename, String targetFilename)
	{
		stdout.println(sourceFilename);
		stdout.println(targetFilename);
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

	public final List stdout;

	public final List stderr;

	public ProcessResult(int code, List stdout, List stderr)
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
