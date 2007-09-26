package Composestar.Core.Config;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A compiler action argument
 * 
 * @author Michiel Hendriks
 */
public class CmdLineArgument implements Serializable
{
	private static final long serialVersionUID = 5033444261781683182L;

	protected static final Pattern pattern = Pattern.compile("([$@%])\\{([^:}]+(:([^}]+))?)\\}");

	/**
	 * The argument template. Used for simple arguments.
	 */
	protected String value;

	public CmdLineArgument()
	{}

	public CmdLineArgument(String inValue)
	{
		super();
		setValue(inValue);
	}

	public void setValue(String inValue)
	{
		if (inValue == null || inValue.length() == 0)
		{
			value = null;
		}
		else
		{
			value = inValue;
		}
	}

	/**
	 * Add the resolved arguments to the given list. This is used to construct
	 * the commandline.
	 * 
	 * @param args
	 * @param p
	 */
	public void addArgs(List<String> tolist, Project proj, Set<File> sources, Properties prop)
	{
		if (value == null)
		{
			return;
		}
		tolist.add(resolve(value, prop));
	}

	/**
	 * Resolve the variables in the string. ${x} resolves to an item in the prop
	 * argument. %{x} resolves to an environment variable. @ {x} resolves to a
	 * system property.
	 * @param str
	 * @param prop
	 * @return
	 */
	public String resolve(String str, Properties prop)
	{
		StringBuffer res = new StringBuffer(str.length());
		Matcher m = pattern.matcher(str);
		while (m.find())
		{
			String type = m.group(1);
			String var = m.group(2);
			String def = m.group(4);
			String replacement = null;
			if ("$".equals(type))
			{
				replacement = prop.getProperty(var, def);
			}
			else if ("@".equals(type))
			{
				// system property
				replacement = System.getProperty(var, def);
			}
			else if ("%".equals(type))
			{
				// environment variable
				replacement = System.getenv(var);
				if (replacement == null)
				{
					replacement = def;
				}
			}
			if (replacement == null)
			{
				replacement = "";
			}
			m.appendReplacement(res, Matcher.quoteReplacement(replacement));
		}
		m.appendTail(res);
		return res.toString();
	}

	@Override
	public String toString()
	{
		return value;
	}
	
	
}
