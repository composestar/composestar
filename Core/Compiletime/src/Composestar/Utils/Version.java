/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Version.java 2735 2006-11-14 19:39:01Z elmuerte $
 */
package Composestar.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * This class contains version information that can be used for whatever
 * purpose. The data is read from the version.properties file included with the
 * JAR. This version.properties file is written/updated by the ANT build system.
 */
public final class Version
{
	/**
	 * The private singleton instance
	 */
	private static final Version instance = new Version();

	/**
	 * Cache of the compile date value
	 */
	private Date compileDate;

	/**
	 * Properties instance
	 */
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
	 * 
	 * @return
	 */
	public static String getVersion()
	{
		return instance.props.getProperty("version", "0.0.0.0");
	}

	/**
	 * Return just the build number. This is only non-zero for builds produced
	 * by the continuous integration server.
	 * 
	 * @return
	 */
	public static int getBuild()
	{
		return Integer.parseInt(instance.props.getProperty("version.build", "0"));
	}

	/**
	 * Get the compilation date
	 */
	public static Date getCompileDate()
	{
		if (instance.compileDate != null)
		{
			return instance.compileDate;
		}
		try
		{
			// note: this must match the format used by the ant script
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			instance.compileDate = sdf.parse(instance.props.getProperty("version.compiledate", "0"));
		}
		catch (ParseException e)
		{
			instance.compileDate = new Date(0);
		}
		return instance.compileDate;
	}

	/**
	 * Full title of the system
	 */
	public static String getTitle()
	{
		return instance.props.getProperty("application.title", "Compose* Compiletime");
	}

	/**
	 * Name of the author
	 */
	public static String getAuthor()
	{
		return instance.props.getProperty("application.author", "the Compose* team; University of Twente");
	}

	/**
	 * Website URL
	 */
	public static String getWebsite()
	{
		return instance.props.getProperty("application.website", "http://composestar.sourceforge.net");
	}

	/**
	 * Return the license stub
	 * 
	 * @return
	 */
	public static String getLicenseStub()
	{
		Calendar c = Calendar.getInstance();
		c.setTime(getCompileDate());
		return "Copyright (C) 2003-" + c.get(Calendar.YEAR) + " " + getAuthor() + ".\n"
				+ "This program is distributed in the hope that it will be useful,\n"
				+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU\n"
				+ "Lesser General Public License for more details.";
	}

	/**
	 * Prints a version report to the provided print stream. This can be used
	 * when the program is executed with the --version argument
	 */
	public static void reportVersion(PrintStream os)
	{
		os.println(Version.getTitle() + " " + Version.getVersion());
		os.println("Developed by " + Version.getAuthor());
		os.println(Version.getWebsite());
		os.println("Compiled on " + Version.getCompileDate().toString());
		os.println("");
		os.println(Version.getLicenseStub());
	}

	private Version()
	{
		InputStream is = this.getClass().getResourceAsStream("/version.properties");
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
