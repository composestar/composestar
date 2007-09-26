package Composestar.DotNET.ASTRA;

import java.io.File;
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
	private final static String s_basepath = ""; // "C:\\WINDOWS\\Microsoft.NET\\Framework\\v1.1.4322\\";

	public void assemble(File inputFile, File outputFile) throws AssemblerException
	{
		List<String> cmdList = new ArrayList<String>();
		cmdList.add(s_basepath + "ilasm");
		cmdList.add("/debug");
		cmdList.add("/quiet");
		cmdList.add(outputFile.toString().endsWith(".dll") ? "/dll" : "/exe");
		cmdList.add("/output=" + outputFile.toString());
		cmdList.add(inputFile.toString());

		debug("Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			debug("Output from ilasm:\n" + cle.outputNormal());
			throw new AssemblerException("There was a fatal assembler error");
		}
	}

	public void disassemble(File inputFile, File outputFile) throws AssemblerException
	{
		List<String> cmdList = new ArrayList<String>();
		cmdList.add(s_basepath + "ildasm");
		cmdList.add("/linenum");
		cmdList.add("/nobar");
		// cmdList.add("/raweh"); // for .NET 2
		cmdList.add("/out=" + outputFile.toString());
		cmdList.add(inputFile.toString());

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
		Debug.out(Debug.MODE_DEBUG, ASTRA.MODULE_NAME, msg);
	}
}
