package Composestar.Java.MASTER;

import Composestar.Core.COPPER3.FilterTypeFactory;
import Composestar.Core.Master.Master;
import Composestar.Java.COMP.JavaSpecificationVersion;

/**
 * Main entry point for the CompileTime. The Master class holds Modules and
 * executes them in the order they are added.
 */
public class JavaMaster extends Master
{
	static final JavaSpecificationVersion MIN_JAVA_VERSION = JavaSpecificationVersion.get("1.5");

	@Override
	protected boolean loadConfiguration() throws Exception
	{
		if (!super.loadConfiguration())
		{
			return false;
		}
		FilterTypeFactory filterTypeFactory = new FilterTypeFactory(resources.repository());
		filterTypeFactory.createDefaultFilterTypes(FilterTypeFactory.DEFAULT_FILTER_TYPES);
		filterTypeFactory.setAllowCustomFilterTypeCreation(true);
		resources.put(FilterTypeFactory.RESOURCE_KEY, filterTypeFactory);
		return true;
	}

	@Override
	protected void initEvironment()
	{
		super.initEvironment();
		if (MIN_JAVA_VERSION != null && MIN_JAVA_VERSION.compareTo(JavaSpecificationVersion.get()) > 0)
		{
			logger.error(String.format("The JavaVM does not meet the minimum version requirement. "
					+ "Please update the JavaVM to at least version %s", MIN_JAVA_VERSION.toString()));
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
		main(JavaMaster.class, "ComposestarJava.jar", args);
	}

}
