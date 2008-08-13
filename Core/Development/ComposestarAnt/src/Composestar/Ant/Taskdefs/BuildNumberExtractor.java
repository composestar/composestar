/**
 * 
 */
package Composestar.Ant.Taskdefs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.Task;

/**
 * @author Michiel Hendriks
 */
public class BuildNumberExtractor extends Task
{
	protected String label = null;

	protected String property = null;

	/**
	 * 
	 */
	public BuildNumberExtractor()
	{

	}

	/**
	 * Sets the string attribute of the BuildNumberExtractor object
	 * 
	 * @param string The new string value
	 */
	public void setLabel(String name)
	{
		label = name;
	}

	/**
	 * Sets the property attribute of the BuildNumberExtractor object
	 * 
	 * @param name The new property value
	 */
	public void setProperty(String name)
	{
		property = name;
	}

	/** Description of the Method */
	@Override
	public void execute()
	{
		getProject().setProperty(property, getBuildNumber(label));
	}

	protected String getBuildNumber(String input)
	{
		if (input == null || input.length() == 0)
		{
			return "0";
		}
		Matcher m;

		final Pattern buildpat = Pattern.compile("build\\.([0-9]+)", Pattern.CASE_INSENSITIVE);
		m = buildpat.matcher(input);
		if (m.matches())
		{
			return m.group(1);
		}

		final Pattern svnpat = Pattern.compile("svn\\.([0-9]+)(:([0-9]+))?([MSP])?", Pattern.CASE_INSENSITIVE);
		m = svnpat.matcher(input);
		if (m.matches())
		{
			if (m.group(3) != null)
			{
				return m.group(3);
			}
			return m.group(1);
		}

		return "0";
	}
}
