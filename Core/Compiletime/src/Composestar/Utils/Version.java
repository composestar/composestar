package Composestar.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class contains version and authorship information.
 * It contains only static data elements and basically just a central
 * place to put this kind of information so it can be updated easily
 * for each release.
 * <p/>
 * Version numbers used here are broken into 4 parts: major, minor, build, and
 * revision, and are written as v<major>.<minor>.<build>.<revision> (e.g. v0.1.4.a).
 * Major numbers will change at the time of major reworking of some
 * part of the system.  Minor numbers for each public release or
 * change big enough to cause incompatibilities.  Build numbers for each different build sequence and 
 * finally revision letter will be incremented for small bug fixes and changes that
 * probably wouldn't be noticed by a user.
 */

public class Version {
	/** 
	 * The private singleton instance
	 */
	private static final Version instance = new Version();
    
	/**
     * The major version number.
     */
    private String versionMajor = "CVSSource";  //Mondriaan for build

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

    /**
     * The minor version number.
     */
    private String versionMinor = "0";

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

    /**
     * The build version number.
     */
    private String versionBuild = "5";

    /**
     * The update letter.
     */
    private String versionRevision = "A";

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

    /**
     * String for the current version.
     */
    public static String getVersionString(){
    	return "version " + instance.versionMajor + '.' + instance.versionMinor + '.' + instance.versionBuild + '.' + instance.versionRevision;
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

    /**
     * Full title of the system
     */
    public static String getTitleString(){ 
		return "Composestar compile-time " + getVersionString();
	}

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

    /**
     * Name of the author
     */
    public static String getAuthorString() { 
		return "Developed by Compose* team University of Twente";
	}

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

    /**
     * The command name normally used to invoke this program
     */
    public final static String getProgramName(){ 
		return "Composestar.Core.Master.Master";
	}
    
    private Version(){
    	try {
    		String archiveFile = "ComposestarCORE.jar";
    		String versionFileName = "Composestar/.version";
    		
    		File versionFile;
    		BufferedReader br = null;
    		
    		File f = new File(archiveFile);
    		if (f.exists()) {
    			JarFile jf = new JarFile(archiveFile);
    			JarEntry je = jf.getJarEntry(versionFileName);
    			if (je != null) {
    				java.io.InputStream is = jf.getInputStream(je);
    				br = new BufferedReader(new InputStreamReader(is));
    			}
    		}
    		else {
    			versionFile = new File(versionFileName);
    			br = new BufferedReader(new FileReader(versionFile));
    		}
    			 
    		try {
				if (br != null) {
					String line;
			    	while ( (line = br.readLine()) != null) {
			    		if ( !line.startsWith("#") && !line.equalsIgnoreCase("") ) {
							if (line.indexOf('=') > 0) {
								String key = line.substring(0, line.indexOf('='));
								String value = "";
								if (line.indexOf('=')+1 != line.length()) {
									value = line.substring(line.indexOf('=') + 1, line.length());
								}
								if (key.equalsIgnoreCase("version.major"))
								{
									versionMajor = value;
								}
								else if (key.equalsIgnoreCase("version.minor"))
								{
									versionMinor = value;
								}
								else if (key.equalsIgnoreCase("version.build"))
								{
									versionBuild = value;
								}
								else if (key.equalsIgnoreCase("version.revision"))
								{
									versionRevision = value;
								}
							}
			    		}
			    	}
				}
    		}
			finally {
	    		if (br != null) br.close();
	    	}
    	}
    	catch (IOException e) {
    		//System.out.print(e.getMessage() );
    	}    	
    }

}
