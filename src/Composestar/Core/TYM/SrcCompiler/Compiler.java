//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\SrcCompiler\\Compiler.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Compiler.java,v 1.1 2006/02/13 11:16:57 pascal Exp $
 */
package Composestar.Core.TYM.SrcCompiler;


/**
 * Interface defining a compiler and how to use it.
 * General usage:
 * try {
 * Compiler comp = new ConfigCompiler( "compilers/mscsharp.ini" );
 * String files[] = new String[1];
 * files[0] = "test.cs";
 * comp.compile( files, "test.dll", true, new String[0], "" );
 * } catch( CompilerException e ) {}
 */
public interface Compiler {
    
    /**
     * Compile sources into exe or dll.
     * 
     * @param sources Array containing all the sources for the current compilation.
     * @param target Full path name for the output file.
     * @param targetLibrary True for dll output, false for exe output.
     * @param deps String array containing the names of all libraries to link with.
     * @param options Compiler switches.
     * @param compilerPath Path to compiler executable.
     * @throws CompilerException Trhown if there
     * was an error during compilation.
     * @roseuid 401B83EC0278
     */
    public void compile(String[] sources, String target, boolean targetLibrary, String[] deps, String options, String compilerPath) throws CompilerException;
    
    /**
     * Identifier for this compiler. Must be unique.
     * 
     * @return Name of the compiler. Filename is the same with .ini appended.
     * @throws CompilerException Thrown if the
     * name is not set.
     * @roseuid 401B83EC02AA
     */
    public String getName() throws CompilerException;
    
    /**
     * The output of the latest compiler run.
     * 
     * @return The ouput of the last invocation.
     * @roseuid 401B83EC02AB
     */
    public String getOutput();
}
