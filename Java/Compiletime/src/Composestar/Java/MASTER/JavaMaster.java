package Composestar.Java.MASTER;

import Composestar.Core.CpsProgramRepository.Legacy.LegacyFilterTypes;
import Composestar.Core.Master.Master;

/**
 * Main entry point for the CompileTime. The Master class holds Modules and
 * executes them in the order they are added.
 */
public class JavaMaster extends Master
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.Master#initEvironment()
	 */
	@Override
	protected void initEvironment()
	{
		super.initEvironment();
		LegacyFilterTypes.useLegacyFilterTypes = true;
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
