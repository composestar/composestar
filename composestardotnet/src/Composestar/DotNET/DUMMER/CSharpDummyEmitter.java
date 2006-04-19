package Composestar.DotNET.DUMMER;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Source;
import Composestar.Utils.Debug;
import Composestar.Utils.StreamGobbler;

public class CSharpDummyEmitter implements DummyEmitter {
		
	public void createDummy(Source source, String outputFilename) throws ModuleException
	{
		Vector sources = new Vector(); 
		sources.add(source);
		
		Vector outputFilenames = new Vector(); 
		outputFilenames.add(outputFilename);
		
		createDummies(sources, outputFilenames);
	}

	public void createDummies(Collection sources, Collection outputFilenames) throws ModuleException
	{
		int result = 0;
		StringBuffer processOutput = new StringBuffer();
		CSharpDummyProcess dummyGen = new CSharpDummyProcess();
		try
		{
			dummyGen.openProcess();
            
            Iterator srcIter = sources.iterator();
            Iterator outputIter = outputFilenames.iterator();
            while (srcIter.hasNext())
            {
            	dummyGen.getStdout().println(((Source)srcIter.next()).getFileName());
            	dummyGen.getStdout().println((String)outputIter.next());
            }
            dummyGen.getStdout().close();
            dummyGen.getStdin().waitForResult();
            dummyGen.getStderr().waitForResult();
            result = dummyGen.getProcess().waitFor();
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating dummies: " + e.getCause(), "DUMMER");
		}
		if (result != 0)
		{
			Debug.out(Debug.MODE_DEBUG, "DUMMER", "CSharpDummyGenerator failed; output from dummy generation process:\n" + dummyGen.getStdin().result());
			throw new ModuleException("Error creating dummies: CSharpDummyGenerator failed", "DUMMER");
		}
	}
}

class CSharpDummyProcess
{
	private PrintStream stdout;
	private StreamGobbler stdin, stderr;
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
        stderr = new StreamGobbler( process.getErrorStream() );
        stdin = new StreamGobbler( process.getInputStream() );
        stderr.start();
        stdin.start();
        this.stdout = new PrintStream(process.getOutputStream());
        //this.stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));
	}

	public PrintStream getStdout() {
		return stdout;
	}

	public Process getProcess() {
		return process;
	}

	public StreamGobbler getStdin() {
		return stdin;
	}
	
	public StreamGobbler getStderr() {
		return stderr;
	}
}
