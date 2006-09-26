package Composestar.DotNET.TYM.SignatureTransformer;

import java.util.ArrayList;
import java.util.List;

import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
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
		cmdList.add(outputFile.matches(".*\\.dll") ? "/dll" : "/exe");
		cmdList.add("/output=" + FileUtils.quote(outputFile));
		cmdList.add(FileUtils.quote(inputFile));

		Debug.out(Debug.MODE_DEBUG, "TYM_ASM", "Command: " + StringUtils.join(cmdList));
	
		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			String stdout = cle.outputNormal();
			throw new AssemblerException("There was a fatal assembler error! Output from ilasm:\n" + stdout);
		}
	}

	public void disassemble(String inputFile, String outputFile) throws AssemblerException
	{
		List cmdList = new ArrayList();
		cmdList.add(s_basepath + "ildasm");
		cmdList.add("/linenum");
		cmdList.add("/nobar");
		cmdList.add("/out=" + FileUtils.quote(outputFile));
		cmdList.add(FileUtils.quote(inputFile));

		Debug.out(Debug.MODE_DEBUG, "TYM_ASM", "Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		if (cle.exec(cmdList) != 0)
		{
			String stdout = cle.outputNormal();
			throw new AssemblerException("There was a fatal disassembler error! Output from ildasm:\n" + stdout);
		}
	}
}
