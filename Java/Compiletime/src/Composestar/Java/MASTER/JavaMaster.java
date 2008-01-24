package Composestar.Java.MASTER;

import Composestar.Core.CpsProgramRepository.Legacy.DefaultFilterFactory;
import Composestar.Core.Master.Master;

/**
 * Main entry point for the CompileTime. The Master class holds Modules and
 * executes them in the order they are added.
 */
public class JavaMaster extends Master
{
	@Override
	protected void loadConfiguration() throws Exception
	{
		super.loadConfiguration();
		DefaultFilterFactory filterFactory = new DefaultFilterFactory(resources.repository());
		filterFactory.addLegacyFilterTypes();
		resources.put(DefaultFilterFactory.RESOURCE_KEY, filterFactory);
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
