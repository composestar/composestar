package ComposestarEclipsePlugin.Core.Utils;

public class FileUtils
{
	/**
	 * This method converts filenames so they contain only 'slashes' instead of
	 * backslashes. This works on all platforms (Java will automatically convert
	 * to backslashes on the Windows platform, which is about the only platform
	 * using backslashes as a directory separator). Because backslashes are
	 * often interpreted as escape characters (e.g. by the prolog engine!) we
	 * can't have them in filenames - besides, it would only work in Windows.
	 * 
	 * @param name A filename, possibly containing backslashes
	 * @return The filename, with backslashes converted to slashes.
	 */
	public static String fixFilename(String name)
	{
		return name.replace('\\', '/');
	}

	public static String getDirectoryPart(String pathToFile)
	{
		int pathEnd = pathToFile.lastIndexOf('/');
		if (pathEnd > 0)
		{
			return pathToFile.substring(0, pathEnd);
		}
		else
		{
			return pathToFile;
		}
	}
}
