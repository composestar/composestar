package Composestar.DotNET.ASTRA;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Implementation of the Assembler interface for Microsft ilasm and ildasm.
 */
public class MSAssembler implements Assembler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ASTRA.MODULE_NAME);

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

		logger.debug("Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			logger.debug("Output from ilasm:\n" + cle.outputNormal());
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

		logger.debug("Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			logger.debug("Output from ildasm:\n" + cle.outputNormal());
			throw new AssemblerException("There was a fatal disassembler error");
		}
	}
}
