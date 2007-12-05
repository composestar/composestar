package Composestar.Core.Config;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A compiler action argument. This represents a single commandline argument.
 * Spaces should be safe to be used. The value can contain variables that will
 * be resolved when the commandline is requested through the addArgs method.
 * There are three types of variables:
 * <dl>
 * <dt>${foo}</dt>
 * <dd>A module provided variable</dd>
 * <dt>\@{foo}</dt>
 * <dd>A Java System property</dd>
 * <dt>%{foo}</dt>
 * <dd>A environment variable</dd>
 * </dl>
 * 
 * @author Michiel Hendriks
 */
public class CmdLineArgument implements Serializable
{
	private static final long serialVersionUID = 5033444261781683182L;

	/**
	 * The regular expression that captures all three forms variables.
	 */
	protected static final Pattern PATTERN = Pattern.compile("([$@%])\\{([^:}]+(:([^}]+))?)\\}");

	protected boolean useUnixSlashes;

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

	/**
	 * Set the current value of the commandline argument
	 * 
	 * @param inValue
	 */
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
	 * argument. %{x} resolves to an environment variable. \@{x} resolves to a
	 * system property.
	 * 
	 * @param str
	 * @param prop
	 * @return
	 */
	public String resolve(String str, Properties prop)
	{
		StringBuffer res = new StringBuffer(str.length());
		Matcher m = PATTERN.matcher(str);
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

		if (isUseUnixSlashes())
		{
			return res.toString().replace('\\', '/');
		}
		else
		{
			return res.toString();
		}
	}

	@Override
	public String toString()
	{
		return value;
	}

	public boolean isUseUnixSlashes()
	{
		return useUnixSlashes;
	}

	public void setUseUnixSlashes(boolean useUnixSlashes)
	{
		this.useUnixSlashes = useUnixSlashes;
	}
}
