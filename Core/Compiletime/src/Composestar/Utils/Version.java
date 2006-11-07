package Composestar.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * This class contains version and authorship information. It contains only
 * static data elements and basically just a central place to put this kind of
 * information so it can be updated easily for each release. <p/> Version
 * numbers used here are broken into 4 parts: major, minor, build, and revision,
 * and are written as v<major>.<minor>.<build>.<revision> (e.g. v0.1.4.a).
 * Major numbers will change at the time of major reworking of some part of the
 * system. Minor numbers for each public release or change big enough to cause
 * incompatibilities. Build numbers for each different build sequence and
 * finally revision letter will be incremented for small bug fixes and changes
 * that probably wouldn't be noticed by a user.
 */

public class Version
{
	/**
	 * The private singleton instance
	 */
	private static final Version instance = new Version();

	private Properties props = new Properties();

	/**
	 * String for the current version.
	 */
	public static String getVersionString()
	{
		return "version " + getVersion();
	}
	
	/**
	 * Return just the version quad
	 * @return
	 */
	public static String getVersion()
	{
		return instance.props.getProperty("version", "0.0.0.0");
	}
	
	/**
	 * Return just the build number. This is only non-zero for builds
	 * produced by the continuous integration server.
	 * 
	 * @return
	 */
	public static int getBuild()
	{
		return Integer.parseInt(instance.props.getProperty("build", "0"));
	}

	/**
	 * Get the compilation date
	 */
	public static Date getCompileDate()
	{
		try
		{
			// note: this must match the format used by the ant script
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			return sdf.parse(instance.props.getProperty("compiledate", ""));
		}
		catch (ParseException e)
		{
			return new Date(0);
		}
	}

	/**
	 * Full title of the system
	 */
	public static String getTitleString()
	{
		return instance.props.getProperty("title", "Composestar Compile-Time");
	}

	/**
	 * Name of the author
	 */
	public static String getAuthorString()
	{
		return instance.props.getProperty("author", "Developed by the Compose* team; University of Twente");
	}

	/**
	 * The command name normally used to invoke this program
	 */
	public final static String getProgramName()
	{
		return "Composestar.Core.Master.Master";
	}

	private Version()
	{
		InputStream is = this.getClass().getResourceAsStream("/Composestar/version.properties");
		if (is != null)
		{
			try
			{
				props.load(is);
			}
			catch (IOException e)
			{
				props.clear();
			}
		}
	}
}
