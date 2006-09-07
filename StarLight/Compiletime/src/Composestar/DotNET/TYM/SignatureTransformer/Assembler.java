//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\Assembler.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\Assembler.java

package Composestar.DotNET.TYM.SignatureTransformer;


/**
 * Interface defining an assembler dissassembler
 */
interface Assembler {
    
    /**
     * @param inputFile File to assemble
     * @param outputFile Filename  of the assembled  file
     * @throws AssemblerException
     * Thrown if there is an  error during assembling
     * @roseuid 406AAFFF02D7
     */
    public void assemble(String inputFile, String outputFile) throws AssemblerException;
    
    /**
     * @param inputFile Input for dissassemblation
     * @param outputFile Output of the dissassembly run
     * @throws AssemblerException
     * Thrown if there is an error during the dissassembly
     * @roseuid 406AAFFF0359
     */
    public void disassemble(String inputFile, String outputFile) throws AssemblerException;
}
