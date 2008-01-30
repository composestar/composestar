package Composestar.Java.MASTER;

import java.util.Arrays;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.Master.Master;

/**
 * Main entry point for the CompileTime. The Master class holds Modules and
 * executes them in the order they are added.
 */
public class JavaMaster extends Master
{
	protected static final int[] MIN_JAVA_VERSION = { 1, 6 };

	@Override
	protected void loadConfiguration() throws Exception
	{
		super.loadConfiguration();
		DefaultFilterFactory filterFactory = new DefaultFilterFactory(resources.repository());
		filterFactory.addLegacyFilterTypes();
		resources.put(DefaultFilterFactory.RESOURCE_KEY, filterFactory);
	}

	protected static boolean hasMinJavaVersion()
	{
		String[] javaVersion = System.getProperty("java.specification.version").split("\\.");
		for (int i = 0; i < MIN_JAVA_VERSION.length; i++)
		{
			if (i > javaVersion.length)
			{
				return false;
			}
			try
			{
				int jvp = Integer.parseInt(javaVersion[i]);
				if (jvp < MIN_JAVA_VERSION[i])
				{
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				logger.error(nfe, nfe);
				return false;
			}
		}
		return true;
	}

	@Override
	protected void initEvironment()
	{
		super.initEvironment();
		if (!hasMinJavaVersion())
		{
			logger
					.error(String
							.format(
									"The JavaVM does not meet the minimum version requirement. Please update the JavaVM to at least version %s",
									Arrays.toString(MIN_JAVA_VERSION)));
			throw new UnsupportedOperationException("Outdated JavaVM");
		}
	}

	/**
	 * Compose* main function. Creates the Master object and invokes the run
	 * method.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarJava.jar [options] <config file>");
			return;
		}
		main(JavaMaster.class, args);
	}

}
