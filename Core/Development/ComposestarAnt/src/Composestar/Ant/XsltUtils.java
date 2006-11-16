package Composestar.Ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * An Xslt extention. Used to resolve assemblies
 * 
 * @author Michiel Hendriks
 */
public class XsltUtils
{
	protected static Map resolvedAsms = new HashMap();

	protected static String AntHelperEXE;

	protected static String currentDirectory;
	
	private XsltUtils() {}

	/**
	 * Sets the location of AntHelper.
	 */
	public static boolean setAntHelperPath(String path)
	{
		File an = new File(path, "AntHelper.exe");
		if (an.exists())
		{
			AntHelperEXE = an.toString();
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setCurrentDirectory(String path)
	{
		currentDirectory = path;
	}

	public static void registerAssembly(String assembly, String path)
	{
		resolvedAsms.put(assembly, path);
	}

	public static void clearAssemblyCache()
	{
		resolvedAsms.clear();
	}

	public static String resolveAssembly(String assembly, String hint) throws Exception
	{
		if (currentDirectory == null)
		{
			throw new Exception("currentDirectory not assigned.");
		}

		// try absolute hint path
		File hintFile = new File(hint);
		if (hintFile.exists()) return hintFile.getCanonicalPath();
		
		// try hint path relative to current directory
		hintFile = new File(currentDirectory, hint);
		if (hintFile.exists()) return hintFile.getCanonicalPath();
		
		// try via helper
		return lookupAssembly(assembly);
	}

	/**
	 * Calls AntHelper to resolve the assembly.
	 */
	protected static String lookupAssembly(String assembly) throws Exception
	{
		if (resolvedAsms.containsKey(assembly))
		{
			return (String) resolvedAsms.get(assembly);
		}
		
		if (AntHelperEXE == null)
		{
			throw new Exception("AntHelperEXE not assigned.");
		}
		if (currentDirectory == null)
		{
			throw new Exception("currentDirectory not assigned.");
		}
		
		String[] cmd = new String[3];
		cmd[0] = AntHelperEXE;
		cmd[1] = "lookupAssembly";
		cmd[2] = assembly;
		Process proc = Runtime.getRuntime().exec(cmd, null, new File(currentDirectory));

		BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		proc.waitFor();
		String resolvedAsm = stdout.readLine();
		proc.destroy();
		stdout.close();

		int err = proc.exitValue();
		if (err != 0)
		{
			throw new Exception("AntHelperEXE exit code is not zero; Unable to find: " + assembly);
		}
		if ((resolvedAsm == null) || (resolvedAsm.length() == 0))
		{
			throw new Exception("AntHelperEXE returned nothing; Unable to find: " + assembly);
		}

		resolvedAsms.put(assembly, resolvedAsm);
		return resolvedAsm;
	}
}
