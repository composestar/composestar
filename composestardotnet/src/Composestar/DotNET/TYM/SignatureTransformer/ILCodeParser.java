//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\ILCodeParser.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\ILCodeParser.java

package Composestar.DotNET.TYM.SignatureTransformer;



import java.io.*;
import java.util.Iterator;
import Composestar.DotNET.LAMA.*;
import java.util.HashMap;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;

/**
 * Responsible for dissassembling an assembly,  parsing the contents of its
 * dissassembled file  and delegating the modification to it and finally
 * assemblying the output.
 */
public class ILCodeParser extends TransformerBase {
    private HashMap LinkToAssemblies;
    private HashMap Concerns;
    protected String AssemblyName;
    
    /**
     * @roseuid 406AB0030387
     */
    public ILCodeParser() {
        super( null, null );
        Concerns = new HashMap();
        LinkToAssemblies = new HashMap(); 
    }
    
    /**
     * @param concern The concern to add.
     * 
     * Add a concern to transform for this ILCodeParser
     * @roseuid 406AB0030391
     */
    public void addConcern(Concern concern) {

        if( concern.getPlatformRepresentation() != null )
        {
			Concerns.put( ((DotNETType)concern.getPlatformRepresentation()).fullName(), new ConcernHolder( concern ) );
        }
        // check assembly dependencies.
		// we only need to check added methods and what assembly they are from
        Signature signature = concern.getSignature();
        if( signature == null )
        {
        	return;
        }
        
        List methods = signature.getMethods(MethodWrapper.ADDED);
        Iterator it = methods.iterator();
        //Iterator it = (concern.getSignature().getMethods( MethodWrapper.ADDED )).iterator();
        
        while( it.hasNext() ) 
        {
			DotNETMethodInfo methodInfo = (DotNETMethodInfo) it.next();
			// fetch assembly
			
			//String assemblyName = ((SignatureImplementation)meth.getSignatureImplementation()).implementation().parent().assemblyQualifiedName();
			//String assemblyName = meth.getMethodInfo().parent().assemblyQualifiedName();
			
			
			DotNETType dnt = (DotNETType)methodInfo.parent();
			String assemblyName = dnt.assemblyName();

			LinkToAssemblies.put( assemblyName, null );
		}     
    }
    
    /**
     * @throws ModifierException
     * @roseuid 406AB00303AF
     */
    public void run() throws ModifierException {
        if("".equals(getAssemblyName())) return; // nothing to do
        
        
        String ilName = getAssemblyName().replaceAll( "\\.\\w+", ".il" );

        // disassemble
        Assembler asm = new MSAssembler();
        try{
            asm.disassemble( getAssemblyName(), ilName );
        } catch( AssemblerException e ){
            throw new ModifierException( e.getMessage() + " (Assembly: '" + getAssemblyName() + "', IL: '" + ilName + "')");
        }

        // open dissassembled file
        BufferedReader in = null;
        try {
            in = new BufferedReader( new InputStreamReader( new FileInputStream( ilName ) ) );
        }catch( FileNotFoundException e ) {
            throw new ModifierException( "Can't open newly created il file " + ilName + " in AssemblyModifier::dissect" );
        }

        // run main assembly transformation
        setIn( in );
        openOut( ilName + ".il" );
        dissect( ilName );
        closeOut();
        try{
        	in.close();
        }catch( IOException e ) {
   			// silent eat: what do we care if we couldn't close the input file.
        }

        // assemble
        try {
            asm.assemble( ilName + ".il", getAssemblyName() );
        } catch( AssemblerException e ){
            throw new ModifierException( e.getMessage() );
        }     
    }
    
    /**
     * @param ilName Name of assembly to transform.
     * @throws ModifierException
     * 
     * Opens an assembly and starts transforming it.
     * @throws ModifierException
     * @roseuid 406AB0040017
     */
    public void dissect(String ilName) throws ModifierException {
        String namespace = "" ;
        String line;
        while( (line = getLine()) != null) {
            if( line.trim().startsWith( ".assembly extern" ) ) {
                // fetch last part (name).
                String[] elems = line.split( " " );
                // remove from assembly list
                LinkToAssemblies.remove( elems[elems.length-1] );
                write(line);
            }
            else if( line.trim().startsWith(".namespace"))
            {
            	String[] elems = line.trim().split(" ");
	            namespace = elems[elems.length-1];
	            write(line);	
            }
            else if( line.trim().startsWith( ".assembly" ) ) {
                // output missing assembly references
                Iterator it = LinkToAssemblies.keySet().iterator();
                while( it.hasNext() ) {
                    write( " .assembly extern " + it.next() + '\n' );
                    write( "{\n  .ver 0:0:0:0\n}\n" );
                }
                write( line );
            }
            else if( line.trim().startsWith( ".class" ) ) {
                // fetch last part (name).
                String[] elems = line.split( " " );
                String name = elems[elems.length-1];
                ConcernHolder conc = (ConcernHolder)Concerns.get( name );
                if( conc == null )
                	conc = (ConcernHolder) Concerns.get( namespace + '.' + name);
                if( conc != null ) {
                    // if not declared mark as declared else mark as current
                    if( !conc.isDeclared() ) {
                        conc.setDeclared(true) ;
                        // output to }
                        write( line );
                        printLine();
                        transformSection( false /* no eat */ );
                    }
                    else {
                        write( line );  // print complete declaration
                        printLine();
                        // new handler
                        ClassModifier mod = new ClassModifier( this, conc.getConcrn() );
						mod.run();
						conc.setDeclared(false) ;
                        //transformSection( false);
                    }
                }
                else {
                    // if not present output complete class.
					write( line );
                    printLine();
                    transformSection( false ); // no eat
                }
            }
            else { // unkown, better echo
                write( line );
            }
        }     
    }

    public String getAssemblyName() {
        return AssemblyName;
    }

    public void setAssemblyName(String assemblyName) {
        AssemblyName = assemblyName;
    }

    class ConcernHolder {
        protected boolean Declared = false;
        protected Concern Concrn;
        
        /**
         * @param conc
         * @roseuid 406AB00400F3
         */
        ConcernHolder(Concern conc) {
        	setConcrn(conc);
        }

        public boolean isDeclared() {
            return Declared;
        }

        public void setDeclared(boolean declared) {
            Declared = declared;
        }

        public Concern getConcrn() {
            return Concrn;
        }

        public void setConcrn(Concern concrn) {
            Concrn = concrn;
        }
    }
}
