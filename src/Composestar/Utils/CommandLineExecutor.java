/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CommandLineExecutor.java,v 1.1 2006/02/13 11:19:59 pascal Exp $
 */

package Composestar.Utils;


/**
 * CmdExec is a tool for running command line programs. Its intention is to handle 
 * command line
 * calls for all operating systems.
 * Things that do not work:
 * - programs that require user input.
 * - redirection in windows.
 * Usage:
 * CmdExec e = new CmdExec();
 * e.exec( "dir" );
 * System.out.println( e.outputNormal() );
 * e.exec( ...... )
 */
public class CommandLineExecutor {
    private StreamGobbler ErrorGobbler;
    private StreamGobbler OutputGobbler;
    
    /**
     * Get the program output to STDOUT.
     * @return java.lang.String
     * @roseuid 404DCCF500D4
     */
    public String outputNormal() { return OutputGobbler.result();     
    }
    
    /**
     * Get the program output to STDERR.
     * @return java.lang.String
     * @roseuid 404DCCF500F3
     */
    public String outputError() { return ErrorGobbler.result();     
    }
    
    /**
     * Execute command.
     * exec executes the command and waits for it to return. WARNING: If the program 
     * hangs this
     * function will never return.
     * Please note that return values indicating error differ between programs and 
     * operating
     * systems.
     * @param execString The command to execute.
     * @return The exit code of the program to run.
     * @roseuid 404DCCF50112
     */
    public int exec(String execString) {
        try {
            String osName = System.getProperty( "os.name" );
            // "some" OSs need special treatment to be able to use built in functions
            if( osName.equals( "Windows NT" )
                || osName.equals( "Windows 2000")
                || osName.equals( "Windows CE")
                || osName.equals( "Windows XP" ) ) {
                	execString = "cmd.exe /C " + execString;
            }
            else if( osName.equals( "Windows 95" )
                     || osName.equals( "Windows 98" )
                     || osName.equals( "Windows ME" ) ) {
                execString = "command.exe /C " + execString;
            }
            // else real operating systems handle this flawlessly

            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec( execString );

            // connect error and output filters
            // these are threads because the buffers used to hold the output data
            // could otherwise overrun which blocks the program.
            ErrorGobbler = new StreamGobbler( proc.getErrorStream() );
            OutputGobbler = new StreamGobbler( proc.getInputStream() );
            ErrorGobbler.start();
            OutputGobbler.start();

            // wait for program return.
            int exitVal = proc.waitFor();

            // wait for the output threads
            OutputGobbler.waitForResult();
            ErrorGobbler.waitForResult();
            return exitVal;
        } catch( Throwable t ) {
            // TODO: New throw specific to project
            t.printStackTrace();
        }
        return -1;     
    }
    
    /**
     * just for testing purposes
     * @param args[]
     * @roseuid 404DCCF50170
     */
    public static void main(String args[]) {
        CommandLineExecutor e = new CommandLineExecutor();
        e.exec( args[0] );
        //System.out.println( e.outputNormal() );     
    }
}
