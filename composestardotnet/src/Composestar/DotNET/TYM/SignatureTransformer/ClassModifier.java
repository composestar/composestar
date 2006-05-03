//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\ClassModifier.java

package Composestar.DotNET.TYM.SignatureTransformer;

import Composestar.Core.LAMA.*;
import Composestar.Utils.Debug;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.DotNET.LAMA.*;

import java.util.Iterator;
import java.util.List;


/**
 * This class does the transformation of an actual class, physically present in a 
 * IL asm file within .class definitions.
 */
public class ClassModifier extends TransformerBase {
    private Concern Concrn;
    
    /**
     * @param con
     * @param parent
     * @roseuid 406AB00102EE
     */
    public ClassModifier(TransformerBase parent, Concern con) {
        super( parent );
        Concrn = con;     
    }
    
    /**
     * @param line
     * @param out
     * @throws ModifierException
     * @throws java.io.IOException
     * @roseuid 406AB0010316
     */
    public void run() throws ModifierException {
		String line = getLine();
        
        do {
            if( line.trim().startsWith( ".method" ) ) {
            	// split into string until cil "managed"
            	String plines = fetchMethodInfo( line );
            	String name = fetchMethodName( plines ); 
            	if( handleMethod(name) )
            	{
            		write( plines );
            		transformSection( false /* no eat */ );
            	}
            	else
            	{
            		transformSection( true /* eat */ );
            	}
            }
            else if( line.matches( "\\s*\\}.*" ) ) { // end of class
                // foreach methods marked with new --> print
				
         		if( Concrn.getSignature() != null )
         		{
	         		Iterator it = (Concrn.getSignature().getMethods( MethodWrapper.ADDED )).iterator();
					while( it.hasNext() ) {
						/* 
					 	Method methods = (Method) it.next();
						SignatureImplementation si = (SignatureImplementation) methods.getSignatureImplementation();
						printMethod (si.implementation());
						*/
						MethodInfo m = (MethodInfo) it.next();
						printMethod( m );
					}
         		}
                write( line ); // output final bracket
                return;
            }
            else
            {
            	write( line );
            }
        } while( (line = getLine()) != null );     
    }
    
    /**
     * @param name
     * @param returnType
     * @param params
     * @return true keep, false remove
     * Checks if a method should be kept or if it should be removed.@throws 
     * Composestar.CTAdaption.TYM.AssemblyTransformer.ModifierException
     * @roseuid 406AB001037A
     */
    private boolean handleMethod(String name) throws ModifierException {
		if( Concrn.getSignature().hasMethod(name))
		{
			int status = Concrn.getSignature().getMethodStatus(name);
			return status != MethodWrapper.REMOVED;
		}
		else
		{
			return true;
		}
    }
    
    /**
     * Parses input lines and transforms this into concrete method information.
     * @param line
     * @param name
     * @param returnType
     * @param params
     * @return java.lang.String
     * @throws ModifierException
     * @roseuid 406AB002001E
     */
    private String fetchMethodInfo(String line) throws ModifierException {
		String plines = line.trim();
		while( !line.endsWith( "cil managed" ) ) {
			line = getLine().trim();
			plines += " ";
			plines += line;
		}
		return plines;
    }			

	private String fetchMethodName(String plines) throws ModifierException {
		String name;
		
		plines = plines.replaceAll("  ", " ");

		String[] elems = plines.split( " " );
		int pos = 0;		
		while( pos < elems.length && !"instance".equals(elems[pos]) ) ++pos;
		
		if( pos >= elems.length-1 )
			return "main";
		
		if( "class".equals(elems[pos + 1]) ) ++pos; // ignore
		// next is returntype
		++pos;
		// next is name
		++pos;
		if( elems[pos].endsWith( "()" ) ) { // void
			name = elems[pos].replaceFirst( "\\(\\)", "" );
		} else { // params
			name = elems[pos].replaceFirst( "\\(", "" );
		}
		return name;     
    }
    
    /**
     * Matches method info agains the
     * @param meth
     * @param name
     * @param returnType
     * @param params
     * @return boolean
     * @roseuid 406AB00200B4
     */
    boolean matchMethod(MethodWrapper methodWrapper, String name, String returnType, String[] params) {
		// fetch .NET version
		MethodInfo minfo = methodWrapper.getMethodInfo();
		if( !minfo.name().equals( name ) ) return false;
		if( !minfo.returnType().name().equals( returnType ) ) return false;
		// check params
		List paramList = minfo.getParameters();
		if( paramList.size() != params.length ) return false;
		for( int i = 0; i<params.length; ++i ) {
			if( !params[i].equals( ((ParameterInfo)paramList.get(i)).parameterType().name() ) )
				return false;
		}
		return true; // amazing.. we got through     
    }
    
    /**
     * method pointer
     * @throws ModifierException
     * @roseuid 406AB002010E
     */
    private void printMethod( MethodInfo meth ) throws ModifierException {
    	
    	DotNETMethodInfo dnmi = (DotNETMethodInfo)meth;
    	write ( ".method public hidebysig strict virtual");
        writenn ("instance " );
 
        writenn(((DotNETType)dnmi.returnType()).ilType()); 
        
        writenn(" ");
        
        writenn( meth.name() + "(");
        
        for( Iterator it = meth.getParameters().iterator(); it.hasNext(); )
        {
			ParameterInfo param = (ParameterInfo) it.next();
			Type paramType = param.parameterType();
			if( paramType != null )
			{
				String iltype =((DotNETType)paramType).ilType();
				
				writenn( iltype + " " + param.name());

				if( it.hasNext() )
					writenn(", ");
			} 
			else
			{
				Debug.out(Debug.MODE_WARNING,"ASTRA","Unresolvable parameter type: " + param.ParameterTypeString);

			}
        }
        
        
        write(") cil managed\n" ); // todo params
        write( "{" );
        
        /*
        if( meth.returnType().name().equals( "Void" ) )
        {
			write( ".maxstack 0" );
			write( "nop" );
			write( "ret" );
        }
        else if( meth.returnType().name().equals( "Int8" ) )
        {
        	write( ".maxstack 1" );
			write( ".locals init ([0] int8 CS$00000003$00000000)" );
			write( "ldc.i4.0" );
			write( "stloc.0" );
			write( "br.s" );
			write( "ldloc.0" );
			write( "ret" );

        }
        else if( meth.returnType().name().equals( "Int16" ) )
        {
        	write( ".maxstack 1" );
			write( ".locals init ([0] int16 CS$00000003$00000000)" );
			write( "ldc.i4.0" );
			write( "stloc.0" );
			write( "br.s" );
			write( "ldloc.0" );
			write( "ret" );
        }
        else if( meth.returnType().name().equals( "Int32" ) )
       	{
       		write( ".maxstack 1" );
			write( ".locals init ([0] int32 CS$00000003$00000000)" );
			write( "ldc.i4.0" );
			write( "stloc.0" );
			write( "br.s" );
			
			write( "ldloc.0" );
			write( "ret" );
       	}
       	else if( meth.returnType().name().equals( "Int64" ) )
       	{
        	write( ".maxstack 1" );
			write( ".locals init ([0] int64 CS$00000003$00000000)" );
			write( "ldc.i4.0" );
			write( "conv.i8" );
			write( "stloc.0" );
			write( "br.s" );
			write( "ret" );
       	}
       	else if( meth.returnType().name().equals( "Char" ) )
       	{
       		write( ".maxstack 1" );
			write( ".locals init ([0] char CS$00000003$00000000)" );
			write( "ldc.i4.0" );
			write( "stloc.0" );
			write( "br.s" );
			write( "ldloc.0" );
			write( "ret" );
       	}
		else if( meth.returnType().name().equals( "Boolean" ) )
		{
			write( ".maxstack 1" );
			write( ".locals init ([0] bool CS$00000003$00000000)" );
			write( "ldc.i4.0" );
			write( "stloc.0" );
			write( "br.s" );
			write( "ldloc.0" );
			write( "ret" );
		}
       	else // string or object
       	{
       		write( ".maxstack 1" );
			write( ".locals init ([0] " + meth.returnType().fullName() + " CS$00000003$00000000)" );
			write( "ldnull" );
			write( "stloc.0" );
			write( "br.s" );
			write( "ldloc.0" );
			write( "ret" );
       	}
       	*/
        write( "}" );
    }
}
