package Composestar.DotNET.ASTRA;

import java.io.File;

/**
 * Interface defining an assembler/dissassembler.
 */
public interface Assembler
{
	/**
	 * @param inputFile File to assemble.
	 * @param outputFile Filename of the assembled file.
	 * @throws AssemblerException if there is an error during assembling.
	 */
	void assemble(File inputFile, File outputFile) throws AssemblerException;

	/**
	 * @param inputFile Input for dissassemblation
	 * @param outputFile Output of the dissassembly run
	 * @throws AssemblerException if there is an error during disassembling.
	 */
	void disassemble(File inputFile, File outputFile) throws AssemblerException;
}
