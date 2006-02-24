/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright   2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ILICIT.java,v 1.1 2006/02/16 23:10:57 pascal_durr Exp $
 */


package Composestar.DotNET.ILICIT;
/*
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ILICIT.java,v 1.1 2006/02/16 23:10:57 pascal_durr Exp $
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import Composestar.Core.WEAVER.*;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

import java.util.ArrayList;

public class ILICIT implements WEAVER {

	public static final String version = "$Revision: 1.1 $";
	
    public void run(CommonResources resources) throws ModuleException {
      PrintWriter out = null;
	  ArrayList builtAssemblies = (ArrayList) resources.getResource("BuiltAssemblies");
	  
	  if(!builtAssemblies.isEmpty())
	  {
	  	String[] assemblyPaths = (String[]) builtAssemblies.toArray(new String[builtAssemblies.size()]);
	  	String binPath = resources.ProjectConfiguration.getProperty("ComposestarPath");
	  	String tempPath = resources.ProjectConfiguration.getProperty("TempFolder");
	  	String buildPath = resources.ProjectConfiguration.getProperty("BuildPath");
	  	String peweaver = binPath + "binaries\\peweaver.exe";
	  	String weavefile = "\"" + tempPath + "weavespec.xml" + '\"';
	  	String targets = "";
	  	if ( assemblyPaths.length > 20 ) {
	  		try {
	  			out = new PrintWriter(new BufferedWriter(new FileWriter(tempPath + "filelist.peweaver")));
	  		
	  			for( int i = 0; i < assemblyPaths.length; i++ )
	  			{
	  				out.println(assemblyPaths[i].replaceAll("\"", ""));
	  			}
			
	  			out.flush();
	  			out.close();
	  		}
	  		catch (IOException e) {
	  			throw new ModuleException("Unable to create the build file for the weaver.","CONE_IS");
	  		}
	  	}
	  	else {
	  		targets = StringConverter.stringListToString(assemblyPaths, " ");
	  	}
      
	  	boolean verify = false;
      
	  	if (resources.ProjectConfiguration.getProperty("VerifyAssemblies") != null) {
	  		if ("yes".equalsIgnoreCase(resources.ProjectConfiguration.getProperty("VerifyAssemblies"))) {
	  			verify = true;
	  		}
	  	}
      
	  	CommandLineExecutor cle = new CommandLineExecutor();

	  	File f = new File(peweaver);
	  	if (!f.exists()) {
	  		throw new ModuleException("Unable to locate the executable '" + peweaver + "'!");
	  	}
	  	else {
	  		String args = " /nologo";
        
	  		if (verify) {
	  			args = args.concat(" /verify");
	  		}
        
	  		// If debugging supply the /debug switch
	  		if ( Debug.getMode() == Debug.MODE_DEBUG ) args = args.concat(" /debug");
        
	  		args = args.concat(" /ws=" + weavefile);

	  		if ( targets.equals("") ) {
	  			args = args.concat(" /filelist=\"" + tempPath + "filelist.peweaver\"");
	  		}
	  		else {
	  			args = args.concat(" " + targets);
	  		}
        
	  		String cmd = "call \"" + peweaver + "\" " + args;
		
	  		// If debugging write output from PeWeaver to log file
	  		//if ( Debug.getMode() == Debug.MODE_DEBUG ) 
	  			cmd += " > \"" + buildPath + "peweaver.log\"";
		
	  			Debug.out(Debug.MODE_DEBUG, "ILICIT", "Starting execution of the 'PE Weaver' tool with arguments '" + args + "'...");
	  			int exitcode = cle.exec(cmd);

	  			if (exitcode != 0) {
	  				Debug.out(Debug.MODE_DEBUG, "ILICIT", cle.outputNormal());
	  			}
        
	  			String msg = "";
        
	  			switch(exitcode)
				{
				case 1: msg = "General PEWeaver failure"; break;
				case 2: msg = "Unable to find weave specification file"; break;
				case 3: msg = "An error occured in the weaving process"; break;
				case 4: msg = "Missing ildasm executable (ildasm.exe)"; break;
				case 5: msg = "Missing ilasm executable (SDK tool ilasm.exe)"; break;
				case 6: msg = "Missing PeVerify executable (SDK tool peverify.exe)"; break;
				case 9: msg = "Input file not found"; break;
				case 10: msg = "IL disassembler not found (ildasm.exe)"; break;
				case 11: msg = "IL disassembler execution failure"; break;
				case 13: msg = "IL file not found"; break;
				case 14: msg = "Unsupported file format"; break;
				case 15: msg = "IL assemmbler not found (ilasm.exe)"; break;
				case 16: msg = "IL assembler execution failure"; break;
				case 17: msg = "Output file not found"; break;
				case 18: msg = "PEVerify tool not found (SDK tool peverify.exe)"; break;
				case 19: msg = "PEVerify execution failure"; break;
				case 25: msg = "ILWeaver not found (ilweaver.exe)"; break;
				case 26: msg = "ILWeaver execution failure"; break;
				default: msg = "PeWeaver execution failure (exitcode " + exitcode + ')'; break;
				}

	  			if (exitcode == 0)
	  				Debug.out(Debug.MODE_DEBUG, "ILICIT", "Successfully executed the 'PE Weaver' tool.");
	  			else
	  				throw new ModuleException(msg, "ILICIT");        
	  	}
	  }	
    }

    public void main(String[] args) {
      CommonResources resources = new CommonResources();
      DataStore ds = DataStore.instance();
      resources.addResource("TheRepository", ds);

      Properties props = new Properties();
      props.setProperty("ILICIT_PEWEAVER", "C:\\Documents and Settings\\%username%\\My Documents\\Visual Studio Projects\\Composestar\\PeWeaver\\bin\\Debug\\peweaver.exe");
      props.setProperty("ILICIT_WEAVEFILE", "ws.xml");
      props.setProperty("ILICIT_VERIFY", "yes");
      props.setProperty("ILICIT_TARGETS", "\"C:\\Documents and Settings\\%username%\\My Documents\\Visual Studio Projects\\Composestar\\PeWeaver\\bin\\Debug\\bak\\TestProfilee.exe\"");

      ds.addObject("config", props);

      try {
        ILICIT ilicit = new ILICIT();
        ilicit.run(resources);
      }
      catch (ModuleException me) {
        System.out.println(me.getMessage());
      }
    }
}