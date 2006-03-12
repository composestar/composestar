//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\MSAssembler.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\MSAssembler.java

package Composestar.DotNET.TYM.SignatureTransformer;

import Composestar.Utils.CommandLineExecutor;

/**
 * Implementation of the Assembler interface for Microsft ilasm and ildasm.
 */
public class MSAssembler implements Assembler {
    
    /**
     * @param inputFile
     * @param outputFile
     * @throws AssemblerException
     * @roseuid 406AB0050005
     */
    public void assemble(String inputFile, String outputFile) throws AssemblerException {
        // ilasm /debug /dll (or exe) /quiet /output outputFile source.il
        String execString = "ilasm /debug" +
            (outputFile.matches( ".*\\.dll" ) ? " /dll" : " /exe") +
            " /quiet /output=\"" + outputFile +
            "\" \"" + inputFile + '\"';
		//System.err.println(execString);
        CommandLineExecutor cmdExec = new CommandLineExecutor();
        if( cmdExec.exec( execString ) != 0 ) {
            throw new AssemblerException( "There was a fatal assembler error!" + cmdExec.outputNormal() + "\n\n\n" + cmdExec.outputError());
        }     
    }
    
    /**
     * @param inputFile
     * @param outputFile
     * @throws AssemblerException
     * @roseuid 406AB0050087
     */
    public void disassemble(String inputFile, String outputFile) throws AssemblerException {
        // ildasm /out=filename /nobar /source inputFile
        String execString = "ildasm /out=" +
                '\"' + outputFile + "\" /nobar \"" + inputFile + '\"';
        //System.err.println(execString);
        CommandLineExecutor cmdExec = new CommandLineExecutor();
        if( cmdExec.exec( execString ) != 0 ) {
            throw new AssemblerException( "There was a fatal disassembler error!" + cmdExec.outputNormal());
        }     
    }
}
