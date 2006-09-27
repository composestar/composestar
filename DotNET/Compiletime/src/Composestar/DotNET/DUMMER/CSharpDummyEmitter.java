package Composestar.DotNET.DUMMER;

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
	//	StringBuffer processOutput = new StringBuffer();
		CSharpDummyProcess dummyGen = new CSharpDummyProcess();
		try
		{
			dummyGen.openProcess(project.getProperty("basePath") + "attributes.xml");
            
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
            int result = dummyGen.getProcess().waitFor();
    		if (result != 0)
    		{
    			Debug.out(Debug.MODE_DEBUG, "DUMMER", "CSharpDummyGenerator failed; output from dummy generation process:\n" + dummyGen.getStdin().result());
    			throw new ModuleException("Error creating dummies: CSharpDummyGenerator failed", "DUMMER");
    		}
            createTypeLocationMapping(project, dummyGen.getStdin());
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating dummies: " + e.getMessage(), "DUMMER");
		}
	}
	
	private void createTypeLocationMapping(Project project, StreamGobbler input)
	{
		// Parse output from CSharpDummyGen, if a line starts with the word 'TypeLocation' the next 2 lines
		// will contain the filename (full path) and name of a class that is defined in that file (FQN)
		ArrayList in  = input.getResultLines();
		Iterator i = in.iterator();
		while (i.hasNext())
		{
			String s = (String)i.next();
			if (s.startsWith("TypeLocation"))
			{
				String filename = (String)i.next();
				String classname = (String)i.next();
				Debug.out(Debug.MODE_DEBUG, "DUMMER", "Defined mapping: " + filename + "=> " + classname);
				TypeSource srcLocation = new TypeSource();
				srcLocation.setFileName(filename);
				srcLocation.setName(classname);				
				project.addTypeSource(srcLocation);
			}
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
	
	public void openProcess(String attributesFile) throws Exception
	{
		Configuration config = Configuration.instance();
		String csDummyEmitter = config.getPathSettings().getPath("Composestar") + "binaries/CSharpDummyGenerator.exe";

        Runtime rt = Runtime.getRuntime();

        String[] command = new String[2];
        command[0] = csDummyEmitter;
        command[1] = attributesFile;

        this.process = rt.exec(command);
        
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
