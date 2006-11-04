package Composestar.DotNET.ASTRA;

import java.util.ArrayList;
import java.util.List;

import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.StringUtils;

/**
 * Implementation of the Assembler interface for Microsft ilasm and ildasm.
 */
public class MSAssembler implements Assembler
{    
	// FIXME: location should be configurable like with csc and vjc
	private final static String s_basepath = ""; //"C:\\WINDOWS\\Microsoft.NET\\Framework\\v1.1.4322\\";

	public void assemble(String inputFile, String outputFile) throws AssemblerException
	{
		List cmdList = new ArrayList();
		cmdList.add(s_basepath + "ilasm");
		cmdList.add("/debug");
		cmdList.add("/quiet");
		cmdList.add(outputFile.endsWith(".dll") ? "/dll" : "/exe");
		cmdList.add("/output=" + outputFile);
		cmdList.add(inputFile);

		debug("Command: " + StringUtils.join(cmdList));
	
		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			debug("Output from ilasm:\n" + cle.outputNormal());
			throw new AssemblerException("There was a fatal assembler error");
		}
	}

	public void disassemble(String inputFile, String outputFile) throws AssemblerException
	{
		List cmdList = new ArrayList();
		cmdList.add(s_basepath + "ildasm");
		cmdList.add("/linenum");
		cmdList.add("/nobar");
	//	cmdList.add("/raweh");	// for .NET 2
		cmdList.add("/out=" + outputFile);
		cmdList.add(inputFile);

		debug("Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			debug("Output from ildasm:\n" + cle.outputNormal());
			throw new AssemblerException("There was a fatal disassembler error");
		}
	}
	
	private void debug(String msg)
	{
		Debug.out(Debug.MODE_DEBUG, "ASTRA", msg);
	}
}
