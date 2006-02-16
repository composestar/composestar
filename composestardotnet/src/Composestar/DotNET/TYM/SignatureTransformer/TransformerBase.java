//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\TransformerBase.java

package Composestar.DotNET.TYM.SignatureTransformer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Base class for assembly modifiers. It provides  the means necessary to read 
 * from and write to the asm fille.
 */
abstract class TransformerBase {
    private BufferedWriter Out;
    private BufferedReader In;
    private TransformerBase Parent;
    
    /**
     * @param parent
     * @roseuid 406AB0050380
     */
    protected TransformerBase(TransformerBase parent) {
        Parent = parent;
        Out = parent.Out;
        In = parent.In;     
    }
    
    /**
     * @param in
     * @param out
     * @roseuid 406AB005034E
     */
    protected TransformerBase(BufferedReader in, BufferedWriter out) {
        Parent = null;
        Out = out;
        In = in;     
    }
    
    /**
     * Set the buffer to use as input for transformation.
     * @param in
     * @roseuid 406AB005039E
     */
    protected void setIn(BufferedReader in) {
        In = in;     
    }
    
    /**
     * Open an output file to write to.
     * @param fileName
     * @throws ModifierException
     * @roseuid 406AB00503BC
     */
    protected void openOut(String fileName) throws ModifierException {
        try {
            Out = new BufferedWriter( new FileWriter( fileName ) );
        }catch( IOException e ) {
			e.printStackTrace();
            throw new ModifierException( "IO error while modifying assembly. Make sure you have enough disk space\n" );
        }     
    }
    
    /**
     * close the output file.
     * @throws ModifierException
     * @roseuid 406AB0060042
     */
    protected void closeOut() throws ModifierException {
        try {
            Out.close();
            Out = null;
        }catch( IOException e ) {
            throw new ModifierException( "TransformerBase::closeOut() Can't close file.\n" );
        }     
    }
    
    /**
     * Gets one line from the input buffer. Throws an exception if there are no more 
     * lines.
     * @return java.lang.String
     * @throws ModifierException
     * @roseuid 406AB006009C
     */
    public String getLine() throws ModifierException {
        
        if( Parent != null )
        	return Parent.getLine();
        
        
        String line = null;
        try{
            do{
                if( (line = In.readLine()) == null )
                    //throw new ModifierException( "TransformerBase::readLine() unexpected end of file." );
                    return null;
                if( "".equals(line) || line.matches( "^\\s*//" )) {
                    //                    Out.write( line );
                    line = null;
                }
            }  while( line == null );
        }catch( IOException e ) {
            throw new ModifierException( "TransformerBase::readLine() IO error" + e.getMessage() );
        }

        return line;     
    }
    
    /**
     * Discards one line of input from the input buffer.
     * @throws ModifierException
     * @roseuid 406AB0060100
     */
    public void eatLine() throws ModifierException {
        getLine();     
    }
    
    /**
     * outputs the next line from the intput buffer to the output file.
     * @throws ModifierException
     * @roseuid 406AB0060150
     */
    public void printLine() throws ModifierException {
        write( getLine() );     
    }
    
    /**
     * Outputs or discards a whole section from { to } including inner levels.
     * @param eat
     * @throws ModifierException
     * @roseuid 406AB00601AB
     */
    public void transformSection(boolean eat) throws ModifierException {
        int level = 0;
        String line; // = getLine();
        
        /*
        if( line.matches("^\\s*\\{") == false )
            throw new ModifierException( "TransformerBase::readLine() Section must start with {" );
        ++level;
        if( !eat )
        	write( line );
        */
        do{
            line = getLine();
            if( line.matches( "^\\s*\\{" ) ) {
            	level++;
            } 
            if( line.matches( "^\\s*}.*" ) )
            {
            	 level--;
           	} 
            if( !eat )
            {
            	write( line );
            }
        } while( level > 0 );     
    }
    
    /**
     * write parameter to output stream.
     * @param out
     * @throws ModifierException
     * @roseuid 406AB0060205
     */
    public void write(String out) throws ModifierException {
        try {
            Out.write( out );
            Out.newLine();
        }catch( IOException e ) {
            throw new ModifierException( "TransfomerBase::write Could not write to file" );
        }     
    }
    
    public void writenn(String out) throws ModifierException {
		try {
			Out.write( out );
		}catch( IOException e ) {
			throw new ModifierException( "TransfomerBase::write Could not write to file" );
		}     
    }
    
    /**
     * Entry hook. Must be reimplemented by children.
     * @throws ModifierException
     * @roseuid 406AB0060269
     */
    abstract void run() throws ModifierException;
}
