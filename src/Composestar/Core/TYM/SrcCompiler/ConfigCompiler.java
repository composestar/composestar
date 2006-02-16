//Source file: H:\\cvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\SrcCompiler\\ConfigCompiler.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ConfigCompiler.java,v 1.2 2006/02/13 11:53:08 pascal Exp $
 */
package Composestar.Core.TYM.SrcCompiler;
import Composestar.Utils.*;
import java.io.IOException;
import Composestar.Utils.*;
import Composestar.Utils.*;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * ConfigCompiler is an implementation of the Compiler interface which provides 
 * generic compiler support based on a configuration file.
 * 
 * @todo Sample configuration file syntax
 * 
 * @see Compiler
 */
public class ConfigCompiler implements Compiler {
    private String CompilerOutput;
    private Properties CompilerConfig;
    
    /**
     * Construct a compiler based on a configuration file.
     * 
     * @param fileName Full path name of the configuration file.
     * @throws CompilerException
     * @roseuid 401B83EC00FC
     */
    public ConfigCompiler(String fileName) throws CompilerException {
        CompilerConfig = new Properties();
        try {
            CompilerConfig.load( new FileInputStream( fileName ) );
        } catch( IOException e ) {
            throw new CompilerException( "Failed to load compiler configuration " + fileName );
        }     
    }
    
    /**
     * Disable empty public constructor.
     * @roseuid 401B98960102
     */
    private ConfigCompiler() {
     
    }
    
    /**
     * ReImpl.
     * 
     * @see Compiler
     * @param sources
     * @param target
     * @param targetLibrary
     * @param deps
     * @param options
     * @param compilerPath
     * @throws CompilerException
     * @roseuid 401B98050302
     */
    public void compile(String[] sources, String target, boolean targetLibrary, String[] deps, String options, String compilerPath) throws CompilerException {
    	String command = ""; 
		CompilerOutput = "";
        String libString = "";
        // work out the libraries string
        for( int i = 0; i < deps.length; i++ ) {
            String clstring = CompilerConfig.getProperty( "libraryParam", "ERROR" );
            clstring = clstring.replaceAll( "\\{LIB\\}", deps[i] );
            libString = libString + clstring + ' ';
        }

        // now generate command line command
        command = compilerPath + CompilerConfig.getProperty( "exec", "ERROR" ) +
            CompilerConfig.getProperty( targetLibrary ? "targetLib" : "targetExec" );

        command = command.replaceAll( "\\{OUT\\}", target );
        command = command.replaceAll( "\\{LIBS\\}", libString );
        command = command.replaceAll( "\\{OPTIONS\\}", options );

        // add sources
        command = command.replaceAll( "\\{SOURCES\\}", StringConverter.stringListToString( sources ) );
        
       // System.out.println("COMPILER: "+command);

        // ready to execute command
        //System.out.println( command );
        CommandLineExecutor cmdExec = new CommandLineExecutor();
        int result = cmdExec.exec(  "call " + command);
        //System.out.println("COMPILER: "+result);
        CompilerOutput = cmdExec.outputNormal();
       
        if( result != 0 ) { // there was an error
        	if (CompilerOutput.length() == 0){
           		CompilerOutput = "Could not execute compiler. Make sure the .net framework 1.1 folder is set in the path and restart Visual Studio.";
        	}
           	try
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer(
						CompilerOutput, "\n");
				//System.out.println("Tokens: "+st.countTokens());
				String lastToken = null;
				while (st.hasMoreTokens()) {
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "RECOMA", "RECOMACOMERROR:"
							+ lastToken);

				}

				throw new CompilerException("RECOMA reported errors during compilation.");
			}
        	catch (Exception ex)
			{
        		 throw new CompilerException( ex.getMessage() );
			}
        }     
    }
    
    /**
     * ReImpl.
     * @see Compiler
     * @return java.lang.String
     * @throws CompilerException
     * @roseuid 401B98090055
     */
    public String getName() throws CompilerException {
        String name = CompilerConfig.getProperty( "name", "ERROR" );
        if( "ERROR".equals(name) ) {
            throw new CompilerException( "Failed to read name property from compiler config" );
        }
        return name;     
    }
    
    /**
     * ReImpl.
     * @see Compiler
     * @return java.lang.String
     * @roseuid 401B980D0214
     */
    public String getOutput() {
        return CompilerOutput;     
    }
}
/**
 * void ConfigCompiler.main(java.lang.String[]){
 * try {
 * Compiler comp = new ConfigCompiler( "compilers/mscsharp.ini" );
 * String files[] = new String[1];
 * files[0] = "test.cs";
 * comp.compile( files, "test.dll", true, new String[0], "", "" );
 * } catch( CompilerException e ) {
 * System.out.println( "Got CompilerException " + e.getMessage() );
 * }
 * }
 */
