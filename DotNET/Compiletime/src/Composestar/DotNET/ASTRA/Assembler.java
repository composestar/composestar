package Composestar.DotNET.ASTRA;

/**
 * Interface defining an assembler/dissassembler.
 */
interface Assembler
{
	/**
	 * @param inputFile File to assemble.
	 * @param outputFile Filename  of the assembled  file.
	 * @throws AssemblerException if there is an  error during assembling.
	 */
	public void assemble(String inputFile, String outputFile) throws AssemblerException;

	/**
	 * @param inputFile Input for dissassemblation
	 * @param outputFile Output of the dissassembly run
	 * @throws AssemblerException if there is an error during disassembling.
	 */
	public void disassemble(String inputFile, String outputFile) throws AssemblerException;
}
