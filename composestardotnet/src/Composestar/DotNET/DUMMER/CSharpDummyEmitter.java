package Composestar.DotNET.DUMMER;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;

import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Source;

public class CSharpDummyEmitter implements DummyEmitter {
		
	public void createDummy(Source source, String outputFilename) throws ModuleException
	{
		try
		{
			CSharpDummyProcess dummyGen = new CSharpDummyProcess();
			dummyGen.openProcess();
			dummyGen.getStdout().println(source.getFileName());
			dummyGen.getStdout().println(outputFilename);
			dummyGen.getStdout().close();
			dummyGen.getProcess().waitFor();
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating dummy: " + e.getCause().getMessage(), "DUMMER");
		}
	}

	public void createDummies(Collection sources, Collection outputFilenames) throws ModuleException
	{
		try
		{
			CSharpDummyProcess dummyGen = new CSharpDummyProcess();
			dummyGen.openProcess();
            
            Iterator srcIter = sources.iterator();
            Iterator outputIter = outputFilenames.iterator();
            while (srcIter.hasNext())
            {
            	dummyGen.getStdout().println(((Source)srcIter.next()).getFileName());
            	dummyGen.getStdout().println((String)outputIter.next());
            }
            dummyGen.getStdout().close();
            dummyGen.getProcess().waitFor();
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating dummies: " + e.getCause().getMessage(), "DUMMER");
		}
	}
}

class CSharpDummyProcess
{
	private PrintStream stdout;
	private InputStream stdin;
	private Process process;
	
	public CSharpDummyProcess()
	{
	}
	
	public void openProcess() throws Exception
	{
		Configuration config = Configuration.instance();
		String csDummyEmitter = config.getPathSettings().getPath("Composestar") + "binaries/CSharpDummygen.exe";

        Runtime rt = Runtime.getRuntime();

        this.process = rt.exec(csDummyEmitter);
        this.stdin = process.getInputStream();
        this.stdout = new PrintStream(process.getOutputStream());		
	}

	public PrintStream getStdout() {
		return stdout;
	}

	public InputStream getStdin() {
		return stdin;
	}

	public Process getProcess() {
		return process;
	}
}
