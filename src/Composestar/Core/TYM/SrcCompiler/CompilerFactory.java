//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\SrcCompiler\\CompilerFactory.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CompilerFactory.java,v 1.1 2006/02/13 11:16:57 pascal Exp $
 */
package Composestar.Core.TYM.SrcCompiler;

import java.util.HashMap;

/**
 * Singleton class.
 * Compiler factory. Holds the different compilers and is responsible for 
 * returning the correct one if available.
 */
public class CompilerFactory {
    
    /**
     * Singleton instance.
     */
    private static CompilerFactory Instance = new CompilerFactory();
    private HashMap Compilers;
    
    /**
     * Default ctor.
     * @roseuid 401B83EC01F6
     */
    private CompilerFactory() {
        Compilers = new HashMap();     
    }
    
    /**
     * Get the required compiler instance. Returns null if the compiler was not found.
     * 
     * @param compiler The name of the compiler to get. The configuration file of this 
     * compiler must have the filename "compiler".ini
     * @param compilerName
     * @return Composestar.CTAdaption.TYM.SrcCompiler.Compiler
     * @roseuid 401B83EC020A
     */
    public Compiler createCompiler(String compilerName) {
        if( !Compilers.containsKey( compilerName ) ) { // if not in map
            try {
                Compiler comp = new ConfigCompiler( compilerName + ".ini" );
                Compilers.put( compilerName, comp );
            } catch( CompilerException e ) { return null; }
        }

        return (Compiler)Compilers.get( compilerName );     
    }
    
    /**
     * Get the CompilerFactory instance (Singleton).
     * @return Composestar.CTAdaption.TYM.SrcCompiler.CompilerFactory
     * @roseuid 401B83EC023C
     */
    public static CompilerFactory instance() {
        return Instance;     
    }
}
