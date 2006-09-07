//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\ModifierException.java

//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\ModifierException.java

package Composestar.DotNET.TYM.SignatureTransformer;


/**
 * Modifier exceptions are thrown if there are serious errors during the 
 * modification of an assembly.
 */
public class ModifierException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8558389968530240489L;

	/**
     * @roseuid 406AB00402AC
     */
    public ModifierException() {
     
    }
    
    /**
     * @param message
     * @roseuid 406AB0040298
     */
    public ModifierException(String message) {
     super( message );     
    }
}
