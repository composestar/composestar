package Composestar.RuntimeJava.FLIRT;

import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;

public class JavaMessageHandlingFacility extends MessageHandlingFacility
{

	public JavaMessageHandlingFacility()
	{

	}

	/**
	 * When the application starts this method is called to deserialize and link
	 * the compile time structure to the runtime
	 * 
	 * @param filename String The location of the xml file
	 * @param debug int The debug level
	 */
	public static synchronized void handleJavaApplicationStart(String filename, int debug)
	{
		handleApplicationStart(filename, debug, new JavaPlatformProvider());
	}
}
